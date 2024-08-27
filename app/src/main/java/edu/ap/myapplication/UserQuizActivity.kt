package edu.ap.myapplication

import ExamQuestion
import MultipleChoiceQuestion
import OpenQuestion
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.myapplication.databinding.ActivityUserQuizBinding
import java.util.Date

class UserQuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserQuizBinding
    private lateinit var viewPager: ViewPager2
    private var examId: String? = null
    private var resultId: String? = null
    private var userId: String? = null
    lateinit var questionsList: List<ExamQuestion>
    private lateinit var quizPagerAdapter: QuizPagerAdapter
    private val userAnswers = mutableMapOf<Int, Any?>()  // Use a map to store answers by questionIndex

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        examId = intent.getStringExtra("EXAM_DOCUMENT_ID")
        resultId = intent.getStringExtra("RESULT_DOCUMENT_ID")
        userId = intent.getStringExtra("USER_DOCUMENT_ID")

        viewPager = binding.viewPager

        loadQuestions()
    }

    private fun loadQuestions() {
        examId?.let { id ->
            FirebaseFirestore.getInstance()
                .collection("exams")
                .document(id)
                .collection("questions")
                .orderBy("questionIndex")  // Order questions by questionIndex
                .get()
                .addOnSuccessListener { result ->
                    questionsList = result.documents.mapNotNull { document ->
                        val data = document.data ?: return@mapNotNull null

                        val questionType = data["questionType"] as? String ?: return@mapNotNull null
                        val questionText = data["question"] as? String ?: return@mapNotNull null
                        val questionIndex = (data["questionIndex"] as? Long)?.toInt() ?: return@mapNotNull null

                        when (questionType) {
                            "multiple_choice" -> {
                                val options = data["choices"] as? List<String> ?: return@mapNotNull null
                                val correctAnswerIndex = (data["correctAnswerIndex"] as? Long)?.toInt() ?: return@mapNotNull null
                                MultipleChoiceQuestion(
                                    text = questionText,
                                    options = options,
                                    correctAnswerIndex = correctAnswerIndex,
                                    questionIndex = questionIndex
                                )
                            }
                            "open" -> {
                                val correctAnswer = data["answer"] as? String ?: return@mapNotNull null
                                OpenQuestion(
                                    text = questionText,
                                    correctAnswer = correctAnswer,
                                    questionIndex = questionIndex
                                )
                            }
                            else -> null
                        }
                    }
                    if (questionsList.isNotEmpty()) {
                        setupViewPager(questionsList)
                    } else {
                        Toast.makeText(this, "No questions found for this exam.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to load questions: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupViewPager(questions: List<ExamQuestion>) {
        quizPagerAdapter = QuizPagerAdapter(this, questions, examId!!, resultId!!)
        viewPager.adapter = quizPagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateQuestionNumber(position)
            }
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    storeAnswerForCurrentQuestion()
                }
            }
        })

        updateQuestionNumber(0)
    }

    private fun storeAnswerForCurrentQuestion() {
        val currentFragment = supportFragmentManager.fragments.find { it is UserQuizFragment && it.isVisible } as? UserQuizFragment

        currentFragment?.let {
            val answer = it.getSelectedAnswer()
            val questionIndex = it.question.questionIndex
            storeAnswer(answer, questionIndex)
        }
    }

    private fun updateQuestionNumber(position: Int) {
        val questionNumberText = "Question ${position + 1} of ${questionsList.size}"
        binding.tvQuestionNumber.text = questionNumberText
    }

    fun onNextQuestion() {
        val nextItem = viewPager.currentItem + 1
        if (nextItem < quizPagerAdapter.itemCount) {
            viewPager.currentItem = nextItem
        } else {
            Toast.makeText(this, "Quiz Completed!", Toast.LENGTH_SHORT).show()
            calculateResult()
            submitAnswers()
        }
    }

    fun storeAnswer(answer: Any?, questionIndex: Int) {
        userAnswers[questionIndex] = answer
        Log.d("UserQuizActivity", "Stored answers: $userAnswers")
    }

    private fun calculateResult(): Int {
        var correctCount = 0

        for (question in questionsList) {
            val userAnswer = userAnswers[question.questionIndex]
            if (question.isCorrectAnswer(userAnswer ?: "")) {
                correctCount++
            }
        }

        val resultPercentage = (correctCount.toDouble() / questionsList.size.toDouble()) * 100
        Log.d("UserQuizActivity", "Correct answers: $correctCount, Result: $resultPercentage%")
        return resultPercentage.toInt()
    }

    private fun submitAnswers() {
        val userAnswersStringKeys = userAnswers.mapKeys { (key, value) -> key.toString() }

        val resultScore = calculateResult()
        val resultData = hashMapOf(
            "userRef" to FirebaseFirestore.getInstance().collection("users").document(userId!!),
            "examRef" to FirebaseFirestore.getInstance().collection("exams").document(examId!!),
            "answers" to userAnswersStringKeys,
            "score" to resultScore,
            "endTime" to Timestamp(Date())
        )

        FirebaseFirestore.getInstance().collection("results").document(resultId!!)
            .update(resultData)
            .addOnSuccessListener {
                navigateToRootActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to submit answers, try again: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun navigateToRootActivity() {
        val intent = Intent(this, RootActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

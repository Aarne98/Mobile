package edu.ap.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.myapplication.databinding.FragmentAddExamBinding

class AddExamFragment : Fragment() {

    private var _binding: FragmentAddExamBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }
    private val questionsList = mutableListOf<HashMap<String,Any >>()
    private var questionIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddExamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartCreatingQuestions.setOnClickListener {
            val examTitle = binding.etExamTitle.text.toString().trim()

            if (examTitle.isNotEmpty()) {
                addExamTitleToFirestore(examTitle)
            } else {
                showToast("Please enter an exam title")
            }
        }


        binding.btnChooseMcQuestion.setOnClickListener {
            binding.questionTypeContainer.visibility = View.GONE
            binding.mcQuestionContainer.visibility = View.VISIBLE
        }

        binding.btnChooseOpenQuestion.setOnClickListener {
            binding.questionTypeContainer.visibility = View.GONE
            binding.openQuestionContainer.visibility = View.VISIBLE
        }

        binding.btnAddMcQuestion.setOnClickListener {
            handleAddMcQuestion()
        }

        binding.btnAddOpenQuestion.setOnClickListener {
            handleAddOpenQuestion()
        }
        binding.btnSubmitExam.setOnClickListener {
            handleSubmitExam()
        }
    }

    private fun addExamTitleToFirestore(examTitle: String) {
        binding.tvQuestionCounter.text = "Questions Created: 0"
        binding.btnStartCreatingQuestions.visibility = View.GONE
        binding.etExamTitle.isEnabled = false
        binding.questionTypeContainer.visibility = View.VISIBLE
        binding.btnSubmitExam.visibility = View.VISIBLE
    }

    private fun handleAddMcQuestion() {
        val mcQuestion = binding.etMcQuestion.text.toString().trim()
        val mcChoices = listOf(
            binding.etMcChoice1.text.toString().trim(),
            binding.etMcChoice2.text.toString().trim(),
            binding.etMcChoice3.text.toString().trim(),
            binding.etMcChoice4.text.toString().trim()
        ).filter { it.isNotEmpty() }
        val mcCorrectAnswerIndex = binding.etMcCorrectAnswerIndex.text.toString().trim().toIntOrNull()

        if (mcQuestion.isNotEmpty() && mcChoices.size == 4 && mcCorrectAnswerIndex != null && mcCorrectAnswerIndex in 1..4) {
            val mcQuestionMap = hashMapOf(
                "questionType" to "multiple_choice",
                "question" to mcQuestion,
                "choices" to mcChoices,
                "correctAnswerIndex" to mcCorrectAnswerIndex - 1,
                "questionIndex" to questionIndex
            )
            questionsList.add(mcQuestionMap)
            questionIndex++
            binding.tvQuestionCounter.text = "Questions Created: $questionIndex"
            clearMcQuestionFields()
            showToast("Multiple-choice question added successfully")
            binding.mcQuestionContainer.visibility = View.GONE
            binding.questionTypeContainer.visibility = View.VISIBLE
        } else {
            showToast("Please fill in all fields for the multiple-choice question and ensure correct answer index is between 1 and 4")
        }
    }

    private fun handleAddOpenQuestion() {
        val openQuestion = binding.etOpenQuestion.text.toString().trim()
        val openAnswer = binding.etOpenAnswer.text.toString().trim()

        if (openQuestion.isNotEmpty() && openAnswer.isNotEmpty()) {
            val openQuestionMap: HashMap<String, Any> = hashMapOf(
                "questionType" to "open",
                "question" to openQuestion,
                "answer" to openAnswer,
                "questionIndex" to questionIndex
            )
            questionsList.add(openQuestionMap)
            questionIndex++
            binding.tvQuestionCounter.text = "Questions Created: $questionIndex"
            clearOpenQuestionFields()
            showToast("Open question added successfully")
            binding.openQuestionContainer.visibility = View.GONE
            binding.questionTypeContainer.visibility = View.VISIBLE
        } else {
            showToast("Please fill in both the open question and the answer")
        }
    }

    private fun clearMcQuestionFields() {
        binding.etMcQuestion.text.clear()
        binding.etMcChoice1.text.clear()
        binding.etMcChoice2.text.clear()
        binding.etMcChoice3.text.clear()
        binding.etMcChoice4.text.clear()
        binding.etMcCorrectAnswerIndex.text.clear()
    }

    private fun clearOpenQuestionFields() {
        binding.etOpenQuestion.text.clear()
        binding.etOpenAnswer.text.clear()
    }

    private fun handleSubmitExam() {
        if (questionsList.isEmpty()) {
            showToast("Please add at least one question before submitting")
            return
        }

        val examRef = db.collection("exams").document()
        examRef.set(hashMapOf("title" to binding.etExamTitle.text.toString().trim()))
            .addOnSuccessListener {
                val questionsCollection = examRef.collection("questions")
                for (question in questionsList) {
                    questionsCollection.add(question)
                }
                showToast("Exam submitted successfully")
                resetFragmentState()
            }
            .addOnFailureListener { e ->
                showToast("Error submitting exam: ${e.localizedMessage}")
                Log.w("AddExamFragment", "Error writing document", e)
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun resetFragmentState() {
        questionsList.clear()

        questionIndex = 0
        binding.tvQuestionCounter.text = "Questions Created: 0"


        binding.etExamTitle.isEnabled = true
        binding.etExamTitle.text.clear()


        binding.btnStartCreatingQuestions.visibility = View.VISIBLE
        binding.questionTypeContainer.visibility = View.GONE
        binding.mcQuestionContainer.visibility = View.GONE
        binding.openQuestionContainer.visibility = View.GONE
        binding.btnSubmitExam.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

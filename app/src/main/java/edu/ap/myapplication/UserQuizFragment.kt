package edu.ap.myapplication

import ExamQuestion
import MultipleChoiceQuestion
import OpenQuestion
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import edu.ap.myapplication.databinding.FragmentUserQuizBinding

class UserQuizFragment : Fragment() {

    private var _binding: FragmentUserQuizBinding? = null
    private val binding get() = _binding!!

    lateinit var question: ExamQuestion
    private lateinit var examId: String
    private lateinit var resultId: String
    private var isLastQuestion: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        examId = requireArguments().getString("EXAM_ID")!!
        resultId = requireArguments().getString("RESULT_DOCUMENT_ID")!!
        question = requireArguments().getSerializable("QUESTION") as ExamQuestion
        val currentQuestionIndex = requireArguments().getInt("INDEX")
        val totalQuestions = (activity as? UserQuizActivity)?.questionsList?.size ?: 0

        isLastQuestion = currentQuestionIndex == totalQuestions - 1

        displayQuestion()

        if (isLastQuestion) {
            binding.btnSubmitAnswer.text = "Submit Exam"
        }

    }

    private fun displayQuestion() {
        binding.tvQuestion.text = question.text

        when (question) {
            is MultipleChoiceQuestion -> {
                displayMultipleChoiceQuestion(question as MultipleChoiceQuestion)
            }
            is OpenQuestion -> {
                displayOpenQuestion(question as OpenQuestion)
            }
        }

        binding.btnSubmitAnswer.setOnClickListener {
            val answer = getSelectedAnswer()

            val currentQuestionIndex = requireArguments().getInt("INDEX")

            (activity as? UserQuizActivity)?.storeAnswer(answer, currentQuestionIndex)

            (activity as? UserQuizActivity)?.onNextQuestion()
        }
    }

    private fun displayMultipleChoiceQuestion(question: MultipleChoiceQuestion) {
        binding.mcQuestionContainer.visibility = View.VISIBLE
        binding.openQuestionContainer.visibility = View.GONE

        question.options.forEachIndexed { index, option ->
            val radioButton = RadioButton(context).apply {
                text = option
                id = index
            }
            binding.radioGroup.addView(radioButton)
        }
    }

    private fun displayOpenQuestion(question: OpenQuestion) {
        binding.mcQuestionContainer.visibility = View.GONE
        binding.openQuestionContainer.visibility = View.VISIBLE
    }

    fun getSelectedAnswer(): Any? {
        return when (question) {
            is MultipleChoiceQuestion -> {
                val selectedOptionId = binding.radioGroup.checkedRadioButtonId
                if (selectedOptionId != -1) selectedOptionId else null
            }
            is OpenQuestion -> {
                binding.etOpenAnswer.text.toString().takeIf { it.isNotBlank() }
            }
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(index: Int, question: ExamQuestion, examId: String, resultId: String): UserQuizFragment {
            val fragment = UserQuizFragment()
            val args = Bundle().apply {
                putInt("INDEX", index)
                putSerializable("QUESTION", question)
                putString("EXAM_ID", examId)
                putString("RESULT_DOCUMENT_ID", resultId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}

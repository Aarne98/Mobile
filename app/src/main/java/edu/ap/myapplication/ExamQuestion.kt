import java.io.Serializable

interface ExamQuestion : Serializable {
    val text: String
    val questionIndex: Int
    fun isCorrectAnswer(answer: Any): Boolean
}

class MultipleChoiceQuestion(
    override var text: String = "",
    var options: List<String> = emptyList(),
    var correctAnswerIndex: Int = 0,
    override val questionIndex: Int = 0
) : ExamQuestion {
    override fun isCorrectAnswer(answer: Any): Boolean {
        return answer is Int && answer == correctAnswerIndex
    }
}
class OpenQuestion(
    override var text: String = "",
    var correctAnswer: String = "",
    override val questionIndex: Int = 0
) : ExamQuestion {
    override fun isCorrectAnswer(answer: Any): Boolean {
        return answer is String && answer.equals(correctAnswer, ignoreCase = true)
    }
}

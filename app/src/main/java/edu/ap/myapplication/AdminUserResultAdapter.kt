import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.ap.myapplication.R

class AdminUserResultAdapter(private val examList: MutableList<ExamResult>) : RecyclerView.Adapter<AdminUserResultAdapter.ExamViewHolder>() {

    class ExamViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val examTitleTextView: TextView = view.findViewById(R.id.textViewExamTitle)
        val examScoreTextView: TextView = view.findViewById(R.id.textViewExamScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exam_result, parent, false)
        return ExamViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val examResult = examList[position]
        holder.examTitleTextView.text = examResult.examTitle
        holder.examScoreTextView.text = "Score: ${examResult.score}"
    }

    override fun getItemCount(): Int {
        return examList.size
    }

    fun updateResults(newResults: List<ExamResult>) {
        examList.clear()
        examList.addAll(newResults)
        notifyDataSetChanged()
    }
}

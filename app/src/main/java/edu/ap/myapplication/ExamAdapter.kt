package edu.ap.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import edu.ap.myapplication.databinding.ItemExamBinding

class ExamAdapter(
    private val examList: List<DocumentSnapshot>,
    private val onDeleteClick: (DocumentSnapshot) -> Unit,
    private val onAddUsersClick: (DocumentSnapshot) -> Unit,
    private val onViewResultsClick: (DocumentSnapshot) -> Unit
) : RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

    inner class ExamViewHolder(private val binding: ItemExamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exam: DocumentSnapshot) {
            val title = exam.getString("title") ?: "No Title"
            binding.tvExamTitle.text = title

            binding.btnDeleteExam.setOnClickListener {
                onDeleteClick(exam)
            }

            binding.btnAddUsers.setOnClickListener {
                onAddUsersClick(exam)
            }

            binding.btnViewResults.setOnClickListener {
                onViewResultsClick(exam)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(examList[position])
    }

    override fun getItemCount(): Int = examList.size
}

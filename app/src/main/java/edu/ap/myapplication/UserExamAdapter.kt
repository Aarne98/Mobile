package edu.ap.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.ap.myapplication.databinding.ItemUserExamBinding

class UserExamAdapter(
    private var exams: List<Pair<String, String>>,
    private val onExamClicked: (String) -> Unit
) : RecyclerView.Adapter<UserExamAdapter.UserExamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserExamViewHolder {
        val binding = ItemUserExamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserExamViewHolder, position: Int) {
        val (examId, examTitle) = exams[position]
        holder.bind(examId, examTitle)
    }

    override fun getItemCount(): Int = exams.size

    fun submitList(newExams: List<Pair<String, String>>) {
        exams = newExams
        notifyDataSetChanged()
    }

    inner class UserExamViewHolder(private val binding: ItemUserExamBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(examId: String, examTitle: String) {
            binding.tvExamTitle.text = examTitle
            binding.root.setOnClickListener {
                onExamClicked(examId)
            }
        }
    }
}

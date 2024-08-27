package edu.ap.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.ap.myapplication.databinding.ItemUserUserBinding

class UserUserAdapter(
    private var users: List<UserUser>,
    private val examId: String,
    private val context: Context
) : RecyclerView.Adapter<UserUserAdapter.UserUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserUserViewHolder {
        val binding = ItemUserUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserUserViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = users.size

    fun submitList(newUsers: List<UserUser>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class UserUserViewHolder(private val binding: ItemUserUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserUser) {
            binding.tvUserUserName.text = "${user.firstName} ${user.lastName}"
            binding.root.setOnClickListener {
                showConfirmationDialog(user)
            }
        }

        private fun showConfirmationDialog(user: UserUser) {
            AlertDialog.Builder(context)
                .setTitle("Start Exam")
                .setMessage("Do you want to start the exam for ${user.firstName} ${user.lastName}?")
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    startExam(user)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        private fun startExam(user: UserUser) {
            val intent = Intent(context, UserTakeExamActivity::class.java)
            Log.d("info",user.id)
            intent.putExtra("USER_DOCUMENT_ID", user.id)
            intent.putExtra("EXAM_DOCUMENT_ID", examId)
            context.startActivity(intent)
        }
    }
}

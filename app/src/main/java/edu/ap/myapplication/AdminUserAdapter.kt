package edu.ap.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminUserAdapter(
    private val users: List<Map<String, Any>>,
    private val addedUserIds: Set<String>,
    private val onAddUserClick: (String) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    private val selectedUserIds = mutableSetOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val userId = user["id"] as String
        val firstName = user["firstName"] as String
        val lastName = user["lastName"] as String

        holder.userName.text = "$firstName $lastName"

        if (addedUserIds.contains(userId)) {
            holder.addUserButton.setBackgroundResource(R.drawable.btn_green)
            holder.addUserButton.text = "✔"
            selectedUserIds.add(userId)
        } else {
            holder.addUserButton.setBackgroundResource(R.drawable.btn_blue)
            holder.addUserButton.text = "+"
        }

        holder.addUserButton.setOnClickListener {
            if (selectedUserIds.contains(userId)) {
                selectedUserIds.remove(userId)
                holder.addUserButton.setBackgroundResource(R.drawable.btn_blue)
                holder.addUserButton.text = "+"
            } else {
                selectedUserIds.add(userId)
                holder.addUserButton.setBackgroundResource(R.drawable.btn_green)
                holder.addUserButton.text = "✔"
            }
            onAddUserClick(userId)
        }
    }

    override fun getItemCount() = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val addUserButton: Button = itemView.findViewById(R.id.btnAddUser)
    }
}
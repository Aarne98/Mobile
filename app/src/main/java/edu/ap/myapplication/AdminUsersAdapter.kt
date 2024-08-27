
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import edu.ap.myapplication.R
import edu.ap.myapplication.UserUser

class AdminUsersAdapter(private val userList: List<UserUser>,private val context: Context, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<AdminUsersAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstNameTextView: TextView = view.findViewById(R.id.textViewFirstName)
        val lastNameTextView: TextView = view.findViewById(R.id.textViewLastName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_users, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.firstNameTextView.text = user.firstName
        holder.lastNameTextView.text = user.lastName

        holder.firstNameTextView.setOnClickListener {
            navigateToAdminUserResultFragment(user)
        }

        holder.lastNameTextView.setOnClickListener {
            navigateToAdminUserResultFragment(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
    private fun navigateToAdminUserResultFragment(user: UserUser) {
        val adminUserResultFragment = AdminUserResultFragment()

        val bundle = Bundle()
        bundle.putString("userId", user.id)
        bundle.putString("firstName", user.firstName)
        bundle.putString("lastName", user.lastName)

        adminUserResultFragment.arguments = bundle

        fragmentManager.beginTransaction()
            .replace(R.id.frameLayout2, adminUserResultFragment)
            .addToBackStack(null)
            .commit()
    }
}
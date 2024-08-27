import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import edu.ap.myapplication.R
import edu.ap.myapplication.UserUser

class AdminUsersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: AdminUsersAdapter
    private val userList = mutableListOf<UserUser>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_users, container, false)


        recyclerView = view.findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())




        userAdapter = AdminUsersAdapter(userList,requireContext(), parentFragmentManager)


        recyclerView.adapter = userAdapter

        fetchUsersFromFirestore()

        return view
    }
    private fun fetchUsersFromFirestore() {
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .whereEqualTo("role", "user")
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                userList.clear()

                for (document in querySnapshot.documents) {
                    val user = document.toObject(UserUser::class.java)
                    user?.let {
                        it.id = document.id
                        userList.add(it)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("AdminUsersFragment", "Error fetching users: ", exception)
            }
    }
}

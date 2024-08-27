package edu.ap.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.myapplication.databinding.FragmentSelectUsersBinding

class SelectUsersFragment : Fragment() {

    private var _binding: FragmentSelectUsersBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val examId = arguments?.getString("examId") ?: return
        val examTitle = arguments?.getString("examTitle") ?: "Unknown Exam"
        binding.textViewExamTitle.text = examTitle

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())

        val examRef = db.collection("exams").document(examId)

        examRef.collection("users").get().addOnSuccessListener { result ->
            val addedUserIds = result.documents.map { it.id }.toMutableSet()
            Log.d("info",addedUserIds.toString())

            db.collection("users")
                .whereEqualTo("role", "user")
                .get()
                .addOnSuccessListener { result ->
                    val userList = result.documents.map { it.data!! + mapOf("id" to it.id) }
                    binding.recyclerViewUsers.adapter = AdminUserAdapter(
                        userList,
                        addedUserIds,
                        onAddUserClick = { userId ->
                            val userRef = examRef.collection("users").document(userId)
                            if (addedUserIds.contains(userId)) {
                            } else {
                                userRef.set(mapOf("userId" to userId))
                                    .addOnSuccessListener {
                                        Log.d("info", userList.toString())
                                        val userMap = userList.find { it["id"] == userId }
                                        Toast.makeText(requireContext(), "User ${userMap?.get("firstName")} added to" +
                                                " $examTitle", Toast.LENGTH_SHORT).show()
                                        addedUserIds.add(userId)
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("info", "Error adding user $userId: ${exception.localizedMessage}")
                                    }
                            }
                        }
                    )
                }
                .addOnFailureListener { exception ->
                    Log.e("info", "Error fetching users: ${exception.localizedMessage}")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
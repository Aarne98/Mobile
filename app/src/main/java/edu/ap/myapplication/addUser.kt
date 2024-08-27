package edu.ap.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.ap.myapplication.databinding.FragmentAddUserBinding

class AddUserFragment : Fragment() {

    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { Firebase.firestore }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateUser.setOnClickListener {
            handleSingleUser()
        }
        binding.btnCreateUsers.setOnClickListener {
            handleMultipleUsers()
        }
    }

    private fun handleSingleUser() {
        val firstName = binding.etVoornaam.text.toString().trim()
        val lastName = binding.etAchternaam.text.toString().trim()

        if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            val user = hashMapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "role" to "user"
            )
            saveUserToDatabase(user)
        }
    }
    private fun handleMultipleUsers(){
        val userPattern = "\\(([^,]+),\\s*([^\\)]+)\\)".toRegex()
        val matches = userPattern.findAll(binding.etUserDetails.text.toString().trim())

        for (match in matches) {
            val voornaam = match.groupValues[1].trim()
            val achternaam = match.groupValues[2].trim()

            if (voornaam.isNotEmpty() && achternaam.isNotEmpty()) {
                val user = hashMapOf(
                    "firstName" to voornaam,
                    "lastName" to achternaam,
                    "role" to "user"
                )
                saveUserToDatabase(user)
            }
        }
    }
    private fun saveUserToDatabase(users: HashMap<String, String>) {
        if (users.isNotEmpty()) {
            db.collection("users")
                .add(users)
                .addOnSuccessListener { documentReference ->
                    showToast("User added successfully")
                }
                .addOnFailureListener { e ->
                    showToast("Error adding user: ${e.localizedMessage}")
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

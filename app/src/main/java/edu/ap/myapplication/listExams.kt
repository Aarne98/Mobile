package edu.ap.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.myapplication.databinding.FragmentViewExamsBinding

class ExamListFragment : Fragment() {

    private var _binding: FragmentViewExamsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewExamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewExams.layoutManager = LinearLayoutManager(requireContext())

        db.collection("exams").get()
            .addOnSuccessListener { result ->
                val examList = result.documents
                binding.recyclerViewExams.adapter = ExamAdapter(
                    examList,
                    onDeleteClick = { exam ->
                        deleteExam(exam.id)
                    },
                    onAddUsersClick = { exam ->
                        val fragment = SelectUsersFragment().apply {
                            arguments = Bundle().apply {
                                putString("examId", exam.id)
                                putString("examTitle", exam.getString("title"))
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout2, fragment)
                            .addToBackStack(null)
                            .commit()
                    },
                    onViewResultsClick = { exam ->
                        val fragment = AdminResultsFragment().apply {
                            arguments = Bundle().apply {
                                putString("examId", exam.id)
                                putString("examTitle", exam.getString("title"))
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout2, fragment) // Use your container ID
                            .addToBackStack(null)
                            .commit()
                    }
                )
            }
            .addOnFailureListener { exception ->
            }
    }

    private fun deleteExam(examId: String) {
        db.collection("exams").document(examId).delete()
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

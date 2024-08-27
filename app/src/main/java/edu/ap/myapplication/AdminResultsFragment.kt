package edu.ap.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import edu.ap.myapplication.databinding.FragmentAdminResultsBinding

class AdminResultsFragment : Fragment() {

    private var _binding: FragmentAdminResultsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private var examId: String? = null
    private var examTitle: String? = null

    private val resultsAdapter = ResultsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        examId = arguments?.getString("examId")
        examTitle = arguments?.getString("examTitle")

        binding.tvExamTitle.text = examTitle

        binding.recyclerViewResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = resultsAdapter
        }

        loadResults()
    }
    override fun onResume() {
        super.onResume()
        loadResults()
    }
    private fun loadResults() {
        examId?.let { id ->
            db.collection("results")
                .whereEqualTo("examRef", db.collection("exams").document(id))
                .get()
                .addOnSuccessListener { result ->
                    processResults(result)
                }
                .addOnFailureListener { exception ->

                }
        }
    }

    private fun processResults(result: QuerySnapshot) {
        val resultsList = mutableListOf<ResultItem>()

        for (document in result.documents) {
            Log.d("tag",document.toString())
            val userRef = document.getDocumentReference("userRef")
            val score = document.getLong("score")?.toInt() ?: 0
            val address = document.getString("address") ?: "Unknown Address"
            val startTime = document.getTimestamp("startTime")
            val endTime = document.getTimestamp("endTime")
            Log.d("info","$score , $startTime , $endTime , ")
            val timeTaken = if (startTime != null && endTime != null) {
                endTime.toDate().time - startTime.toDate().time
            } else {
                0L
            }

            userRef?.get()
                ?.addOnSuccessListener { userSnapshot ->
                    val firstName = userSnapshot.getString("firstName") ?: "Unknown User"
                    resultsList.add(
                        ResultItem(
                            userName = firstName,
                            score = score,
                            address = address,
                            timeTaken = timeTaken
                        )
                    )

                    resultsAdapter.submitList(resultsList)
                }
                ?.addOnFailureListener { exception ->
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

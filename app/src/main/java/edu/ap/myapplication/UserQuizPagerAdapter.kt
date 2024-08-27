package edu.ap.myapplication

import ExamQuestion
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class QuizPagerAdapter(
    activity: FragmentActivity,
    private val questions: List<ExamQuestion>,
    private val examId: String,
    private val resultId: String
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = questions.size

    override fun createFragment(position: Int) = UserQuizFragment.newInstance(
        index = position,
        question = questions[position],
        examId = examId,
        resultId = resultId
    )
}

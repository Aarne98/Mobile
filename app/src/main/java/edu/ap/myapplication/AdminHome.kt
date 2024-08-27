package edu.ap.myapplication

import AdminUsersFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import edu.ap.myapplication.databinding.ActivityAdminHomeBinding

class AdminHome : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addUser -> {
                    replaceFragment(AddUserFragment())
                    true
                }
                R.id.addExam ->{
                    replaceFragment(AddExamFragment())
                    true
                }
                R.id.viewExams ->{
                    replaceFragment(ExamListFragment())
                    true
                }
                R.id.viewUsers->{
                    replaceFragment(AdminUsersFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(AddUserFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout2, fragment)
        fragmentTransaction.commit()
    }
}
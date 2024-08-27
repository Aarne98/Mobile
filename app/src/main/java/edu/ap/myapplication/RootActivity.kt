package edu.ap.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import edu.ap.myapplication.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (isLocationPermissionGranted()) {

            setupClickListeners()
        } else {
            requestLocationPermission()
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted && coarseLocationGranted
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (granted) {

            setupClickListeners()
        } else {
            Toast.makeText(
                this,
                "Location permissions are required to use this app.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }

    private fun setupClickListeners() {

        binding.btnGoAdmin.setOnClickListener {
            val intent = Intent(this, AdminLogin::class.java)
            startActivity(intent)
        }

        binding.btnGoStudent.setOnClickListener {
            val intent = Intent(this, UserExamListActivity::class.java)
            startActivity(intent)
        }
    }
}

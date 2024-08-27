package edu.ap.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.myapplication.databinding.ActivityAdminLoginBinding
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


class AdminLogin : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bALlogin.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (firstName.isNotEmpty() && password.isNotEmpty()) {
                val encryptedPassword = EncryptionUtils.encrypt(password).trim()
                authenticateAdmin(firstName, encryptedPassword)
            } else {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticateAdmin(firstName: String, encryptedPassword: String) {
        Log.d("info",encryptedPassword)
        //addUserToDatabase(firstName,encryptedPassword)
        db.collection("users")
            .whereEqualTo("role", "admin")
            .whereEqualTo("firstName", firstName)
            .whereEqualTo("password", encryptedPassword)
            .get()
            .addOnSuccessListener { result ->
                Log.d("info", "Number of documents found: ${result.size()}")
                if (result.isEmpty) {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, AdminHome::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
    fun addUserToDatabase(firstName: String, encryptedPassword: String) {
        val user = hashMapOf(
            "firstName" to firstName,
            "password" to encryptedPassword,
            "role" to "admin"
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                // Log success
                Log.d("info", "User added successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("info", "Error adding user", e)
            }
    }
}

object EncryptionUtils {

    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES"

    private val BASE64_ENCODED_KEY = "7bV/kdRk88BsUNHWegWf+U5sK+V1KPlrEr1qABqm7tQ="
    private val secretKey: SecretKey by lazy {
        val decodedKey = Base64.decode(BASE64_ENCODED_KEY, Base64.DEFAULT)
        SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
    }
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decryptedBytes = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT))
        return String(decryptedBytes)
    }
}



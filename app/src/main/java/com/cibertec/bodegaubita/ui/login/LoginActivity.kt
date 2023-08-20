package com.cibertec.bodegaubita.ui.login

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cibertec.bodegaubita.MainActivity
import com.cibertec.bodegaubita.databinding.ActivityLoginBinding
import com.cibertec.bodegaubita.ui.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email.isEmpty()) {
                binding.edtEmail.error = "Ingrese su correo"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edtPassword.error = "Ingrese su contraseña"
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser

                        user?.let {
                            if (user.uid == "ea8uchUy27hsqmdk6QSrCUlsbYH2"){
                                startActivity(Intent(this, AdminActivity::class.java))
                            } else {
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                        }
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                    binding.progressBar.visibility = View.GONE
                }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.uid == "ea8uchUy27hsqmdk6QSrCUlsbYH2"){
                startActivity(Intent(this, AdminActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }

}
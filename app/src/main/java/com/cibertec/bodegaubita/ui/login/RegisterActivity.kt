package com.cibertec.bodegaubita.ui.login

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.cibertec.bodegaubita.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.cibertec.bodegaubita.model.Account
import com.google.firebase.firestore.ktx.firestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val address = binding.edtAddress.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (name.isEmpty()) {
                binding.edtName.error = "Ingrese su nombre"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                binding.edtEmail.error = "Ingrese su correo"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.edtPassword.error = "Ingrese su contraseña"
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateProfile(user, name)

                    val account = Account(uid = user?.uid.toString(), address = address)

                    val collectionUser = Firebase.firestore.collection("User")
                    collectionUser
                        .document(user?.uid.toString())
                        .set(account)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Error creating account.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateProfile(user: FirebaseUser?, displayName: String) {
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(displayName).build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User profile updated.")
            }
        }
    }

}
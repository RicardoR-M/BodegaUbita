package com.cibertec.bodegaubita.ui.account

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cibertec.bodegaubita.databinding.FragmentAccountBinding
import com.cibertec.bodegaubita.ui.account.about.AboutActivity
import com.cibertec.bodegaubita.ui.account.orders.OrdersActivity
import com.cibertec.bodegaubita.ui.login.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)

        init()
        events()

        return binding.root
    }

    private fun init() {
        val user = Firebase.auth.currentUser

        if (user?.displayName.isNullOrEmpty()) {
            binding.tvUserName.text = "Usuario"
        } else {
            binding.tvUserName.text = user?.displayName
        }

        binding.tvEmail.text = user?.email
    }

    private fun events() {
        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()

            startActivity(Intent(context, LoginActivity::class.java))
            activity?.finish()
        }

        binding.clOrders.setOnClickListener {
            val intent = Intent(context, OrdersActivity::class.java)
            startActivity(intent)
        }

        binding.clAbout.setOnClickListener {
            val intent = Intent(context, AboutActivity::class.java)
            startActivity(intent)
        }
    }
}
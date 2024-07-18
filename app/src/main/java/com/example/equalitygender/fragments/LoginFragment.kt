package com.example.equalitygender.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.equalitygender.R
import com.example.equalitygender.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.example.equalitygender.Admin
import com.example.equalitygender.Masyarakat

class LoginFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    private val adminUids = listOf(
        "107eT8rDoic1d6Lzw5hUq3EZ87m2",
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        binding.textViewSignUp.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_daftarFragment)
        }

        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty())
                loginUser(email, pass)
            else
                Toast.makeText(context, "Silahkan Isi Semua Input!", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loginUser(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = mAuth.currentUser
                val userId = user?.uid

                if (userId != null && adminUids.contains(userId)) {
                    // Navigate to Admin Activity if the user is an admin
                    val intent = Intent(activity, Admin::class.java)
                    startActivity(intent)
                } else {
                    // Navigate to Masyarakat Activity if the user is not an admin
                    val intent = Intent(activity, Masyarakat::class.java)
                    startActivity(intent)
                }
                activity?.finish()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
    }

}
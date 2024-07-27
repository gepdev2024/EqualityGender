package com.example.equalitygender.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.equalitygender.R
import com.example.equalitygender.databinding.FragmentDaftarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DaftarFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: FragmentDaftarBinding

    private val defaultProfilePictureUrl = "https://firebasestorage.googleapis.com/v0/b/equalitygender-468e1.appspot.com/o/default_profil.png?alt=media&token=a8e6f554-deea-474e-987b-35aae2a32de6"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDaftarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        binding.textViewSignIn.setOnClickListener {
            navController.navigate(R.id.action_daftarFragment_to_loginFragment)
        }

        binding.nextBtn.setOnClickListener {
            val username = binding.usernameEt.text.toString()
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()
            val verifyPass = binding.verifyPassEt.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                if (pass == verifyPass) {
                    registerUser(username, email, pass)
                } else {
                    Toast.makeText(context, "Password Tidak Sama!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Silahkan Isi Semua Input!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(username: String, email: String, pass: String) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                saveUserToFirestore(username)
                Toast.makeText(context, "Akun Berhasil Didaftar!", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_daftarFragment_to_loginFragment)
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToFirestore(username: String) {
        val userId = mAuth.currentUser?.uid
        val user = hashMapOf(
            "username" to username,
            "email" to mAuth.currentUser?.email,
            "profilePictureUrl" to defaultProfilePictureUrl
        )
        userId?.let {
            firestore.collection("users").document(it).set(user)
                .addOnSuccessListener {
                    // User saved successfully
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }
}

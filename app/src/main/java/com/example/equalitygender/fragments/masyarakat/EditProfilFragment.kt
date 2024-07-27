package com.example.equalitygender.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.equalitygender.R
import com.example.equalitygender.databinding.FragmentEditProfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfilFragment : Fragment() {

    private var _binding: FragmentEditProfilBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        binding.buttonSave.setOnClickListener {
            val username = binding.editTextUsername.text.toString()

            if (username.isNotEmpty()) {
                val user = auth.currentUser

                user?.let {
                    val userId = user.uid
                    val userDocRef = firestore.collection("users").document(userId)
                    userDocRef.update("username", username)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to update username", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        user?.let {
            val userId = user.uid
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        binding.editTextUsername.setText(username)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

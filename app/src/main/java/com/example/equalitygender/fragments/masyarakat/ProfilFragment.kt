package com.example.equalitygender.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.equalitygender.Awal
import com.example.equalitygender.MainActivity
import com.example.equalitygender.R
import com.example.equalitygender.databinding.FragmentProfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        binding.textViewEditProfile.setOnClickListener {
            loadFragment(EditProfilFragment())
        }

        binding.textViewLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(activity, Awal::class.java))
            activity?.finish()
        }

        binding.imageViewProfile.setOnClickListener {
            openFileChooser()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        user?.let {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val username = document.getString("username")
                        val profileImageUrl = document.getString("profileImageUrl")

                        binding.textViewUsername.text = username
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this).load(profileImageUrl).into(binding.imageViewProfile)
                        } else {
                            binding.imageViewProfile.setImageResource(R.drawable.ic_profile)
                        }
                    }
                }
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            uploadProfileImage()
        }
    }

    private fun uploadProfileImage() {
        imageUri?.let { uri ->
            val user = auth.currentUser
            user?.let {
                val storageRef = storage.reference.child("profile_images/${user.uid}")
                val uploadTask = storageRef.putFile(uri)

                uploadTask.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val profileImageUrl = downloadUri.toString()
                        firestore.collection("users").document(user.uid)
                            .update("profileImageUrl", profileImageUrl)
                            .addOnSuccessListener {
                                Glide.with(this).load(profileImageUrl).into(binding.imageViewProfile)
                            }
                    }
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wraper, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.equalitygender.fragments.masyarakat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.equalitygender.R
import com.example.equalitygender.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CreatePostFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private lateinit var imageViewPost: ImageView
    private lateinit var imageViewUserProfile: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var buttonSubmit: Button
    private var postId: String? = null
    private var existingPost: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        imageViewPost = view.findViewById(R.id.imageViewPost)
        imageViewUserProfile = view.findViewById(R.id.imageViewUserProfile)
        progressBar = view.findViewById(R.id.progressBar)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)

        postId = arguments?.getString("postId")

        if (postId != null) {
            loadPostDetails(postId!!)
        }

        view.findViewById<Button>(R.id.buttonUploadImage).setOnClickListener {
            openFileChooser()
        }

        buttonSubmit.setOnClickListener {
            if (auth.currentUser != null) {
                savePost()
            } else {
                // Handle not logged in state
            }
        }

        // Fetch and display user's profile image
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(profileImageUrl).into(imageViewUserProfile)
                    } else {
                        imageViewUserProfile.setImageResource(R.drawable.ic_profile)
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
            imageViewPost.visibility = View.VISIBLE
            Glide.with(this).load(imageUri).into(imageViewPost)
        }
    }

    private fun loadPostDetails(postId: String) {
        firestore.collection("posts").document(postId).get()
            .addOnSuccessListener { document ->
                existingPost = document.toObject(Post::class.java)
                existingPost?.let {
                    view?.findViewById<EditText>(R.id.editTextIsi)?.setText(it.isi)
                    if (it.imageUrl.isNotEmpty()) {
                        imageViewPost.visibility = View.VISIBLE
                        Glide.with(this).load(it.imageUrl).into(imageViewPost)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun savePost() {
        val isi = view?.findViewById<EditText>(R.id.editTextIsi)?.text.toString()

        if (isi.isEmpty() || (imageUri == null && existingPost?.imageUrl.isNullOrEmpty())) {
            // Handle validation
            return
        }

        buttonSubmit.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        if (imageUri != null) {
            val storageRef = storage.reference.child("posts/${UUID.randomUUID()}")
            val uploadTask = imageUri?.let { storageRef.putFile(it) }

            uploadTask?.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    savePostToFirestore(isi, uri.toString())
                }
            }?.addOnFailureListener {
                // Handle upload failure
                progressBar.visibility = View.GONE
                buttonSubmit.visibility = View.VISIBLE
            }
        } else {
            savePostToFirestore(isi, existingPost?.imageUrl ?: "")
        }
    }

    private fun savePostToFirestore(isi: String, imageUrl: String) {
        val post = hashMapOf(
            "isi" to isi,
            "imageUrl" to imageUrl,
            "upvotes" to (existingPost?.upvotes ?: 0),
            "downvotes" to (existingPost?.downvotes ?: 0),
            "timestamp" to (existingPost?.timestamp ?: System.currentTimeMillis()),
            "userId" to auth.currentUser!!.uid,
            "votes" to (existingPost?.votes ?: hashMapOf<String, Int>())
        )

        val postDocument = if (postId != null) {
            firestore.collection("posts").document(postId!!)
        } else {
            firestore.collection("posts").document()
        }

        postDocument.set(post)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Post berhasil disimpan", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                // Handle failure
            }
            .addOnCompleteListener {
                progressBar.visibility = View.GONE
                buttonSubmit.visibility = View.VISIBLE
            }
    }

    companion object {
        fun newInstance(postId: String): CreatePostFragment {
            val fragment = CreatePostFragment()
            val args = Bundle()
            args.putString("postId", postId)
            fragment.arguments = args
            return fragment
        }
    }
}

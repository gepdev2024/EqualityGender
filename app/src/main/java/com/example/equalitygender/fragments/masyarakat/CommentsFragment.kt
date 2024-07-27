package com.example.equalitygender.fragments.masyarakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.adapters.masyarakat.CommentsAdapter
import com.example.equalitygender.models.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommentsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentsAdapter: CommentsAdapter
    private val commentsList = mutableListOf<Comment>()
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            postId = it.getString(ARG_POST_ID) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        recyclerView = view.findViewById(R.id.recyclerViewComments)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        commentsAdapter = CommentsAdapter(commentsList)
        recyclerView.adapter = commentsAdapter

        val buttonAddComment: Button = view.findViewById(R.id.buttonAddComment)
        val editTextComment: EditText = view.findViewById(R.id.editTextComment)

        buttonAddComment.setOnClickListener {
            val commentText = editTextComment.text.toString()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
                editTextComment.text.clear()
            }
        }

        fetchComments()
    }

    private fun fetchComments() {
        firestore.collection("comments")
            .whereEqualTo("postId", postId)
            .get()
            .addOnSuccessListener { documents ->
                commentsList.clear()
                for (document in documents) {
                    val comment = document.toObject(Comment::class.java).apply { id = document.id }
                    commentsList.add(comment)
                }
                commentsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun addComment(text: String) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    val username = document.getString("username") ?: "Anonymous"
                    val comment = Comment(
                        postId = postId,
                        userId = user.uid,
                        userName = username,  // Store the username
                        text = text,
                        timestamp = System.currentTimeMillis()
                    )

                    firestore.collection("comments")
                        .add(comment)
                        .addOnSuccessListener { documentReference ->
                            comment.id = documentReference.id
                            commentsList.add(comment)
                            commentsAdapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle error
                }
        }
    }

    companion object {
        private const val ARG_POST_ID = "postId"

        @JvmStatic
        fun newInstance(postId: String) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POST_ID, postId)
                }
            }
    }
}

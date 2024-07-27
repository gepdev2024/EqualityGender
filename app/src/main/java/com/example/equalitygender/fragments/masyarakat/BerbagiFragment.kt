package com.example.equalitygender.fragments.masyarakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.adapters.masyarakat.MasyarakatPostAdapter
import com.example.equalitygender.models.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BerbagiFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: MasyarakatPostAdapter
    private val postList = mutableListOf<Post>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_berbagi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        postAdapter = MasyarakatPostAdapter(postList, this::editPost, this::showDeleteConfirmationDialog)
        recyclerView.adapter = postAdapter

        val spinnerFilter: Spinner = view.findViewById(R.id.spinnerFilter)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filter_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> fetchPosts(Query.Direction.DESCENDING, "timestamp")
                    1 -> fetchPosts(Query.Direction.DESCENDING, "upvotes")
                    2 -> fetchPostsByUser()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        fetchPosts(Query.Direction.DESCENDING, "timestamp")

        val fabCreatePost: FloatingActionButton = view.findViewById(R.id.fabCreatePost)
        fabCreatePost.setOnClickListener {
            navigateToCreatePost()
        }
    }

    private fun fetchPosts(direction: Query.Direction, field: String) {
        firestore.collection("posts")
            .orderBy(field, direction)
            .get()
            .addOnSuccessListener { documents ->
                postList.clear()
                for (document in documents) {
                    val post = document.toObject(Post::class.java).apply {
                        id = document.id
                        // Ensure default values for fields that might be null
                        upvotes = document.getLong("upvotes")?.toInt() ?: 0
                        downvotes = document.getLong("downvotes")?.toInt() ?: 0
                    }
                    postList.add(post)
                }
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun fetchPostsByUser() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                postList.clear()
                for (document in documents) {
                    val post = document.toObject(Post::class.java).apply {
                        id = document.id
                        // Ensure default values for fields that might be null
                        upvotes = document.getLong("upvotes")?.toInt() ?: 0
                        downvotes = document.getLong("downvotes")?.toInt() ?: 0
                    }
                    postList.add(post)
                }
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun navigateToCreatePost() {
        val fragment = CreatePostFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wraper, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun editPost(post: Post) {
        val fragment = CreatePostFragment.newInstance(post.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wraper, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showDeleteConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Postingan?")
            .setMessage("Apakah anda ingin menghapusnya?")
            .setPositiveButton("Ya") { dialog, which ->
                deletePost(post)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun deletePost(post: Post) {
        firestore.collection("posts").document(post.id).delete()
            .addOnSuccessListener {
                postList.remove(post)
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }
}

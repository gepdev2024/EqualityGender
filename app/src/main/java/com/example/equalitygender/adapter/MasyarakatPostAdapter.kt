package com.example.equalitygender.adapters.masyarakat

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.equalitygender.R
import com.example.equalitygender.models.Post
import com.example.equalitygender.fragments.masyarakat.CommentsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MasyarakatPostAdapter(
    private val postList: List<Post>,
    private val onEditPost: (Post) -> Unit,
    private val onDeletePost: (Post) -> Unit
) : RecyclerView.Adapter<MasyarakatPostAdapter.PostViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.bind(post)
    }

    override fun getItemCount() = postList.size

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewIsi: TextView = itemView.findViewById(R.id.textViewIsi)
        private val imageViewPost: ImageView = itemView.findViewById(R.id.imageViewPost)
        private val textViewVotes: TextView = itemView.findViewById(R.id.textViewVotes)
        private val buttonUpvote: ImageButton = itemView.findViewById(R.id.buttonUpvote)
        private val buttonDownvote: ImageButton = itemView.findViewById(R.id.buttonDownvote)
        private val buttonViewComments: ImageButton = itemView.findViewById(R.id.buttonViewComments)
        private val imageButtonMenu: ImageButton = itemView.findViewById(R.id.imageButtonMenu)
        private val imageViewUserProfile: ImageView = itemView.findViewById(R.id.imageViewUserProfile)
        private val textViewUserName: TextView = itemView.findViewById(R.id.textViewUserName)
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
        private val userId = auth.currentUser?.uid

        fun bind(post: Post) {
            textViewIsi.text = post.isi
            textViewVotes.text = (post.upvotes - post.downvotes).toString()

            // Fetch user profile image and username
            firestore.collection("users").document(post.userId).get()
                .addOnSuccessListener { document ->
                    val profileImageUrl = document.getString("profileImageUrl")
                    val username = document.getString("username")
                    textViewUserName.text = username
                    if (!profileImageUrl.isNullOrEmpty()) {
                        Glide.with(itemView.context).load(profileImageUrl).into(imageViewUserProfile)
                    } else {
                        imageViewUserProfile.setImageResource(R.drawable.ic_profile)
                    }
                }

            if (post.imageUrl.isNotEmpty()) {
                Glide.with(itemView.context).load(post.imageUrl).into(imageViewPost)
                imageViewPost.visibility = View.VISIBLE
            } else {
                imageViewPost.visibility = View.GONE
            }

            buttonUpvote.setOnClickListener {
                userId?.let { id ->
                    when (post.votes[id]) {
                        null -> {
                            post.upvotes++
                            post.votes[id] = 1
                        }
                        -1 -> {
                            post.downvotes--
                            post.upvotes++
                            post.votes[id] = 1
                        }
                        1 -> {
                            post.upvotes--
                            post.votes.remove(id)
                        }
                    }
                    updatePostVotes(post)
                }
            }

            buttonDownvote.setOnClickListener {
                userId?.let { id ->
                    when (post.votes[id]) {
                        null -> {
                            post.downvotes++
                            post.votes[id] = -1
                        }
                        1 -> {
                            post.upvotes--
                            post.downvotes++
                            post.votes[id] = -1
                        }
                        -1 -> {
                            post.downvotes--
                            post.votes.remove(id)
                        }
                    }
                    updatePostVotes(post)
                }
            }

            buttonViewComments.setOnClickListener {
                val fragment = CommentsFragment.newInstance(post.id)
                val transaction = (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fl_wraper, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            // Show menu only for the user's own posts
            if (post.userId == userId) {
                imageButtonMenu.visibility = View.VISIBLE
                imageButtonMenu.setOnClickListener {
                    showPopupMenu(it, post)
                }
            } else {
                imageButtonMenu.visibility = View.GONE
            }
        }

        private fun showPopupMenu(view: View, post: Post) {
            val popup = PopupMenu(view.context, view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.menu_post_options, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit -> {
                        onEditPost(post)
                        true
                    }
                    R.id.action_delete -> {
                        onDeletePost(post)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        private fun updatePostVotes(post: Post) {
            firestore.collection("posts").document(post.id).update(
                mapOf(
                    "upvotes" to post.upvotes,
                    "downvotes" to post.downvotes,
                    "votes" to post.votes
                )
            )
            textViewVotes.text = (post.upvotes - post.downvotes).toString()
        }
    }
}

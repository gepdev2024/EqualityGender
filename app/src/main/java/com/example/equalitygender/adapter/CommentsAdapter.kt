package com.example.equalitygender.adapters.masyarakat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.models.Comment

class CommentsAdapter(private val commentsList: List<Comment>) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentsList[position]
        holder.bind(comment)
    }

    override fun getItemCount() = commentsList.size

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewUser: TextView = itemView.findViewById(R.id.textViewCommentUser)
        private val textViewText: TextView = itemView.findViewById(R.id.textViewCommentText)

        fun bind(comment: Comment) {
            textViewUser.text = comment.userName
            textViewText.text = comment.text
        }
    }
}

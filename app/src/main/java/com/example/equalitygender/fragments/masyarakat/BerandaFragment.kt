package com.example.equalitygender.fragments.masyarakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.equalitygender.R
import com.example.equalitygender.models.Informasi
import com.example.equalitygender.models.Post
import com.example.equalitygender.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BerandaFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var textViewWelcome: TextView
    private lateinit var textViewInformasiContent: TextView
    private lateinit var textViewBerbagiUser: TextView
    private lateinit var textViewBerbagiContent: TextView
    private lateinit var imageViewInformasi: ImageView
    private lateinit var imageViewUserProfile: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        textViewWelcome = view.findViewById(R.id.textViewWelcome)
        textViewInformasiContent = view.findViewById(R.id.textViewInformasiContent)
        textViewBerbagiUser = view.findViewById(R.id.textViewBerbagiUser)
        textViewBerbagiContent = view.findViewById(R.id.textViewBerbagiContent)
        imageViewInformasi = view.findViewById(R.id.imageViewInformasi)
        imageViewUserProfile = view.findViewById(R.id.imageViewUserProfile)

        fetchUser()
        fetchInformasiHighlight()
        fetchBerbagiHighlight()

        view.findViewById<TextView>(R.id.textViewLihatSelengkapnyaInformasi).setOnClickListener {
            navigateToFragment(InformasiFragment())
        }

        view.findViewById<TextView>(R.id.textViewLihatSelengkapnyaBerbagi).setOnClickListener {
            navigateToFragment(BerbagiFragment())
        }
    }

    private fun fetchUser() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.let {
                    textViewWelcome.text = "Hai ${it.username}!"
                    if (it.profileImageUrl.isNotEmpty()) {
                        Glide.with(this).load(it.profileImageUrl).into(imageViewUserProfile)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun fetchInformasiHighlight() {
        firestore.collection("informasi")
            .orderBy("tanggal", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val informasi = document.toObject(Informasi::class.java)
                    textViewInformasiContent.text = informasi.judul
                    Glide.with(this).load(informasi.gambar).into(imageViewInformasi)
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun fetchBerbagiHighlight() {
        firestore.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val post = document.toObject(Post::class.java)
                    fetchPostUserDetails(post)
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun fetchPostUserDetails(post: Post) {
        firestore.collection("users").document(post.userId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.let {
                    textViewBerbagiUser.text = it.username
                    textViewBerbagiContent.text = getTruncatedContent(post.isi)
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    private fun getTruncatedContent(content: String): String {
        val lines = content.split("\n")
        return if (lines.size <= 1) {
            lines.joinToString("\n")
        } else {
            lines.take(1).joinToString("\n") + "..."
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wraper, fragment)
            .addToBackStack(null)
            .commit()
    }
}

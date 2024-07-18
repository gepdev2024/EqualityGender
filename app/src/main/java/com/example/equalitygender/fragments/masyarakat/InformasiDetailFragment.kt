package com.example.equalitygender.fragments.masyarakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.equalitygender.R

class InformasiDetailFragment : Fragment() {

    private var title: String? = null
    private var date: String? = null
    private var imageUrl: String? = null
    private var description: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title")
            date = it.getString("date")
            imageUrl = it.getString("imageUrl")
            description = it.getString("description")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_informasi_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageButtonBack = view.findViewById<ImageButton>(R.id.imageButtonBack)
        view.findViewById<TextView>(R.id.textViewTitle).text = title
        view.findViewById<TextView>(R.id.textViewDate).text = date
        view.findViewById<TextView>(R.id.textViewDescription).text = description
        val imageView = view.findViewById<ImageView>(R.id.imageViewInformasi)
        Glide.with(this).load(imageUrl).into(imageView)

        imageButtonBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) =
            InformasiDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("id", id)
                }
            }
    }
}

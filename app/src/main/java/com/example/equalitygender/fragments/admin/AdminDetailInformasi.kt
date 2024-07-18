package com.example.equalitygender.fragments.admin

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

class AdminDetailInformasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_detail_informasi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageButtonBack = view.findViewById<ImageButton>(R.id.imageButtonBack)
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
        val textViewDate = view.findViewById<TextView>(R.id.textViewDate)
        val imageViewInformasi = view.findViewById<ImageView>(R.id.imageViewInformasi)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewDescription)

        val title = arguments?.getString("title")
        val date = arguments?.getString("date")
        val imageUrl = arguments?.getString("imageUrl")
        val description = arguments?.getString("description")

        textViewTitle.text = title
        textViewDate.text = date
        textViewDescription.text = description
        Glide.with(requireContext()).load(imageUrl).into(imageViewInformasi)

        imageButtonBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}

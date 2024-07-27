package com.example.equalitygender.fragments.masyarakat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.equalitygender.R
import com.google.firebase.firestore.FirebaseFirestore

class ReportDetailFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private var reportId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            reportId = it.getString(ARG_REPORT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        val imageButtonBack: ImageButton = view.findViewById(R.id.imageButtonBack)
        imageButtonBack.setOnClickListener {
            activity?.onBackPressed()
        }

        reportId?.let { id ->
            firestore.collection("reports").document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val kategori = document.getString("kategori")
                        val kodeLaporan = document.getString("kodeLaporan")
                        val status = document.getString("status")
                        val tanggalKejadian = document.getString("tanggalKejadian")
                        val lokasiKejadian = document.getString("lokasiKejadian")
                        val deskripsiKejadian = document.getString("deskripsiKejadian")
                        val mediaUrl = document.getString("mediaUrl")

                        view.findViewById<TextView>(R.id.textViewKategori).text = kategori
                        view.findViewById<TextView>(R.id.textViewKodeLaporan).text = kodeLaporan
                        view.findViewById<TextView>(R.id.textViewStatusLaporan).text = status
                        view.findViewById<TextView>(R.id.textViewTanggalKejadian).text = tanggalKejadian
                        view.findViewById<TextView>(R.id.textViewLokasiKejadian).text = lokasiKejadian
                        view.findViewById<TextView>(R.id.textViewDeskripsiKejadian).text = deskripsiKejadian

                        val imageViewMedia = view.findViewById<ImageView>(R.id.imageViewMedia)
                        if (!mediaUrl.isNullOrEmpty()) {
                            Glide.with(view.context).load(mediaUrl).into(imageViewMedia)
                            imageViewMedia.visibility = View.VISIBLE
                        } else {
                            imageViewMedia.visibility = View.GONE
                        }
                    } else {
                        Log.e("ReportDetailFragment", "Document is null")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ReportDetailFragment", "Error fetching document", exception)
                }
        }
    }

    companion object {
        private const val ARG_REPORT_ID = "reportId"

        @JvmStatic
        fun newInstance(reportId: String) =
            ReportDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_REPORT_ID, reportId)
                }
            }
    }
}

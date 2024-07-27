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
import com.example.equalitygender.models.Report
import com.google.firebase.firestore.FirebaseFirestore

class AdminReportDetailFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var reportId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            reportId = it.getString("reportId") ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_report_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        val textViewKategori: TextView = view.findViewById(R.id.textViewKategori)
        val textViewKodeLaporan: TextView = view.findViewById(R.id.textViewKodeLaporan)
        val textViewStatusLaporan: TextView = view.findViewById(R.id.textViewStatusLaporan)
        val textViewTanggalKejadian: TextView = view.findViewById(R.id.textViewTanggalKejadian)
        val textViewLokasiKejadian: TextView = view.findViewById(R.id.textViewLokasiKejadian)
        val textViewDeskripsiKejadian: TextView = view.findViewById(R.id.textViewDeskripsiKejadian)
        val imageViewMedia: ImageView = view.findViewById(R.id.imageViewMedia)
        val imageButtonBack: ImageButton = view.findViewById(R.id.imageButtonBack)

        imageButtonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        firestore.collection("reports").document(reportId).get()
            .addOnSuccessListener { document ->
                val report = document.toObject(Report::class.java)
                report?.let {
                    textViewKategori.text = it.kategori
                    textViewKodeLaporan.text = "Kode Laporan: ${it.kodeLaporan}"
                    textViewStatusLaporan.text = it.status
                    textViewTanggalKejadian.text = it.tanggalKejadian
                    textViewLokasiKejadian.text = it.lokasiKejadian
                    textViewDeskripsiKejadian.text = it.deskripsiKejadian

                    if (it.mediaUrl.isNotEmpty()) {
                        Glide.with(this).load(it.mediaUrl).into(imageViewMedia)
                        imageViewMedia.visibility = View.VISIBLE
                    } else {
                        imageViewMedia.visibility = View.GONE
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(reportId: String) =
            AdminReportDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("reportId", reportId)
                }
            }
    }
}

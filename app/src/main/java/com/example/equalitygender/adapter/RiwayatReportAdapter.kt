package com.example.equalitygender.adapters.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.equalitygender.R
import com.example.equalitygender.models.Report
import com.example.equalitygender.fragments.admin.AdminReportDetailFragment

class RiwayatReportAdapter(
    private val reportList: List<Report>
) : RecyclerView.Adapter<RiwayatReportAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_riwayat, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.bind(report)
    }

    override fun getItemCount() = reportList.size

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewKategori: TextView = itemView.findViewById(R.id.textViewKategori)
        private val textViewKodeLaporan: TextView = itemView.findViewById(R.id.textViewKodeLaporan)
        private val textViewTanggalKejadian: TextView = itemView.findViewById(R.id.textViewTanggalKejadian)
        private val textViewStatusLaporan: TextView = itemView.findViewById(R.id.textViewStatusLaporan)
        private val imageViewLaporan: ImageView = itemView.findViewById(R.id.imageViewLaporan)
        private val textViewDeskripsiKejadian: TextView = itemView.findViewById(R.id.textViewDeskripsiKejadian)

        fun bind(report: Report) {
            textViewKategori.text = report.kategori
            textViewKodeLaporan.text = "Kode Laporan: ${report.kodeLaporan}"
            textViewTanggalKejadian.text = report.tanggalKejadian
            textViewStatusLaporan.text = report.status
            textViewDeskripsiKejadian.text = report.deskripsiKejadian

            if (!report.mediaUrl.isNullOrEmpty()) {
                Glide.with(itemView.context).load(report.mediaUrl).into(imageViewLaporan)
                imageViewLaporan.visibility = View.VISIBLE
            } else {
                imageViewLaporan.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val fragment = AdminReportDetailFragment.newInstance(report.id)
                val transaction = (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fl_wraper, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }
}

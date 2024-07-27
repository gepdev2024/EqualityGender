package com.example.equalitygender.adapters.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.fragments.admin.AdminReportDetailFragment
import com.example.equalitygender.models.Report

class AdminReportAdapter(
    private val reportList: List<Report>,
    private val onAcceptClicked: (Report) -> Unit,
    private val onRejectClicked: (Report) -> Unit
) : RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_admin, parent, false)
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
        private val buttonAccept: Button = itemView.findViewById(R.id.buttonAccept)
        private val buttonReject: Button = itemView.findViewById(R.id.buttonReject)

        fun bind(report: Report) {
            textViewKategori.text = report.kategori
            textViewKodeLaporan.text = "Kode Laporan: ${report.kodeLaporan}"

            buttonAccept.setOnClickListener { onAcceptClicked(report) }
            buttonReject.setOnClickListener { onRejectClicked(report) }

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

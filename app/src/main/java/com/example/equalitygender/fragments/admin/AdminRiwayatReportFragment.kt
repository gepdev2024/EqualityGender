package com.example.equalitygender.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.adapters.admin.RiwayatReportAdapter
import com.example.equalitygender.models.Report
import com.google.firebase.firestore.FirebaseFirestore

class AdminRiwayatReportFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportAdapter: RiwayatReportAdapter
    private val reportList = mutableListOf<Report>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_riwayat_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewRiwayatReports)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        reportAdapter = RiwayatReportAdapter(reportList)
        recyclerView.adapter = reportAdapter

        fetchReports()
    }

    private fun fetchReports() {
        firestore.collection("reports")
            .whereIn("status", listOf("Laporan Disetujui", "Laporan Ditolak"))
            .get()
            .addOnSuccessListener { documents ->
                reportList.clear()
                for (document in documents) {
                    val report = document.toObject(Report::class.java).apply { id = document.id }
                    reportList.add(report)
                }
                reportAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }
}

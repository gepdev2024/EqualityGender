package com.example.equalitygender.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.adapters.admin.AdminReportAdapter
import com.example.equalitygender.models.Report
import com.google.firebase.firestore.FirebaseFirestore

class AdminLaporanFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportAdapter: AdminReportAdapter
    private val reportList = mutableListOf<Report>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_laporan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewReports)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        reportAdapter = AdminReportAdapter(reportList, ::onAcceptClicked, ::onRejectClicked)
        recyclerView.adapter = reportAdapter

        fetchReports()

        view.findViewById<Button>(R.id.buttonRiwayat).setOnClickListener {
            navigateToRiwayat()
        }
    }

    private fun fetchReports() {
        firestore.collection("reports")
            .whereEqualTo("status", "Laporan Diproses")
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

    private fun onAcceptClicked(report: Report) {
        showConfirmationDialog(report, "Apakah Anda yakin ingin menyetujui laporan ini?", "Laporan Disetujui")
    }

    private fun onRejectClicked(report: Report) {
        showConfirmationDialog(report, "Apakah Anda yakin ingin menolak laporan ini?", "Laporan Ditolak")
    }

    private fun showConfirmationDialog(report: Report, message: String, status: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("Ya") { _, _ ->
                updateReportStatus(report, status)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun updateReportStatus(report: Report, status: String) {
        firestore.collection("reports").document(report.id).update("status", status)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Laporan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                fetchReports() // Refresh the list
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }

    private fun navigateToRiwayat() {
        val fragment = AdminRiwayatReportFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fl_wraper, fragment)
            .addToBackStack(null)
            .commit()
    }
}

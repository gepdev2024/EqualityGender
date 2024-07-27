package com.example.equalitygender.fragments.masyarakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.equalitygender.R
import com.example.equalitygender.adapters.masyarakat.MasyarakatReportAdapter
import com.example.equalitygender.models.Report
import com.google.firebase.firestore.FirebaseFirestore

class LaporFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportAdapter: MasyarakatReportAdapter
    private val reportList = mutableListOf<Report>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lapor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerViewReports)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        reportAdapter = MasyarakatReportAdapter(reportList)
        recyclerView.adapter = reportAdapter

        view.findViewById<Button>(R.id.buttonCreateReport).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_wraper, CreateReportFragment())
                .addToBackStack(null)
                .commit()
        }

        fetchReports()
    }

    private fun fetchReports() {
        firestore.collection("reports").get()
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

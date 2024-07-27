package com.example.equalitygender.models

data class Report(
    var id: String = "",
    var kategori: String = "",
    var tanggalKejadian: String = "",
    var mediaUrl: String = "",
    var lokasiKejadian: String = "",
    var deskripsiKejadian: String = "",
    var status: String = "Laporan Diproses",
    var kodeLaporan: String = ""
)

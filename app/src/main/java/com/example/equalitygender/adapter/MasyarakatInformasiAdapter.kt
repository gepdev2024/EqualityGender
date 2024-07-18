package com.example.equalitygender.adapters.masyarakat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.equalitygender.R
import com.example.equalitygender.models.Informasi
import com.example.equalitygender.fragments.masyarakat.InformasiDetailFragment

class MasyarakatInformasiAdapter(
    private val informasiList: List<Informasi>
) : RecyclerView.Adapter<MasyarakatInformasiAdapter.InformasiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformasiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.masyarakat_item_informasi, parent, false)
        return InformasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: InformasiViewHolder, position: Int) {
        val informasi = informasiList[position]
        holder.bind(informasi)
    }

    override fun getItemCount() = informasiList.size

    inner class InformasiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewInformasi)
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewDescription)

        fun bind(informasi: Informasi) {
            textViewTitle.text = informasi.judul

            // Truncate description to 20-30 words
            val descriptionWords = informasi.deskripsi.split(" ")
            textViewDescription.text = if (descriptionWords.size > 30) {
                descriptionWords.take(30).joinToString(" ") + "..."
            } else {
                informasi.deskripsi
            }

            Glide.with(itemView.context).load(informasi.gambar).into(imageView)

            itemView.setOnClickListener {
                val fragment = InformasiDetailFragment()
                val bundle = Bundle()
                bundle.putString("title", informasi.judul)
                bundle.putString("date", informasi.tanggal)
                bundle.putString("imageUrl", informasi.gambar)
                bundle.putString("description", informasi.deskripsi)
                fragment.arguments = bundle

                val transaction = (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fl_wraper, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }
}

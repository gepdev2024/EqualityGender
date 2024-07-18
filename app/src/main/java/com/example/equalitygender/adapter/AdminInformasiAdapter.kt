package com.example.equalitygender.adapters

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.equalitygender.R
import com.example.equalitygender.models.Informasi
import com.example.equalitygender.fragments.admin.AdminDetailInformasiFragment
import com.example.equalitygender.fragments.admin.AdminInformasiEditFragment

class AdminInformasiAdapter(
    private val informasiList: List<Informasi>,
    private val onEditClick: (Informasi) -> Unit,
    private val onDeleteClick: (Informasi) -> Unit
) : RecyclerView.Adapter<AdminInformasiAdapter.InformasiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformasiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_item_informasi, parent, false)
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
        private val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
        private val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)

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
                val fragment = AdminDetailInformasiFragment()
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

            buttonEdit.setOnClickListener {
                val fragment = AdminInformasiEditFragment()
                val bundle = Bundle()
                bundle.putString("id", informasi.id)
                bundle.putString("judul", informasi.judul)
                bundle.putString("tanggal", informasi.tanggal)
                bundle.putString("gambar", informasi.gambar)
                bundle.putString("kategori", informasi.kategori)
                bundle.putString("deskripsi", informasi.deskripsi)
                fragment.arguments = bundle

                val transaction = (itemView.context as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fl_wraper, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            buttonDelete.setOnClickListener {
                showDeleteConfirmationDialog(informasi)
            }
        }

        private fun showDeleteConfirmationDialog(informasi: Informasi) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("Konfirmasi Penghapusan")
            builder.setMessage("Apakah Anda yakin ingin menghapus informasi ini?")
            builder.setPositiveButton("Ya") { dialog, _ ->
                onDeleteClick(informasi)
                dialog.dismiss()
            }
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }
}

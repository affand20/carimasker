package id.trydev.carimasker.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import id.trydev.carimasker.GlideApp
import id.trydev.carimasker.R

class UploadCatalogAdapter(val context: Context): RecyclerView.Adapter<UploadCatalogAdapter.ViewHolder>() {

    private val listImage = mutableListOf<String>()

    fun setData(listImage: List<String>) {
        this.listImage.clear()
        this.listImage.addAll(listImage)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_foto_barang, parent, false))
    }

    override fun getItemCount(): Int = listImage.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listImage[position])
        holder.itemPic.clipToOutline = true
    }

    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view) {

        private val btnRemove = view.findViewById<ImageButton>(R.id.btn_remove_item)
        val itemPic: ImageView = view.findViewById(R.id.item_pic)

        fun bindItem(url:String) {
            if (url.contains("firebase")) {
                GlideApp.with(context)
                    .asBitmap()
                    .load(url)
                    .centerCrop()
                    .into(itemPic)
            } else {
                itemPic.setImageURI(url.toUri())
            }

            btnRemove.setOnClickListener {
                listImage.remove(url)
                notifyDataSetChanged()
            }
        }
    }
}
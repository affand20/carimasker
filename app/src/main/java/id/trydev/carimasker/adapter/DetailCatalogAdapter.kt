package id.trydev.carimasker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.siyamed.shapeimageview.RoundedImageView
import id.trydev.carimasker.GlideApp
import id.trydev.carimasker.R
import id.trydev.carimasker.model.Katalog

class DetailCatalogAdapter(val context:Context): RecyclerView.Adapter<DetailCatalogAdapter.ViewHolder>() {

    private val listImage = mutableListOf<String>()

    fun setListImage(listImage: List<String>) {
        this.listImage.clear()
        this.listImage.addAll(listImage)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_katalog_preview, parent, false))
    }

    override fun getItemCount(): Int = listImage.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(listImage[position])
    }

    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view) {

        private val imgPreview: RoundedImageView = view.findViewById(R.id.img_preview)

        fun bindItem(item: String) {
            GlideApp.with(context)
                .asBitmap()
                .centerCrop()
                .load(item)
                .into(imgPreview)
        }
    }
}
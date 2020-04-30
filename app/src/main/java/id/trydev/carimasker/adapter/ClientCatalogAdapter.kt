package id.trydev.carimasker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import id.trydev.carimasker.GlideApp
import id.trydev.carimasker.R
import id.trydev.carimasker.model.Katalog
import id.trydev.carimasker.model.User
import id.trydev.carimasker.ui.clientkatalog.ClientKatalogFragmentDirections
import id.trydev.carimasker.ui.katalog.KatalogFragmentDirections

class ClientCatalogAdapter(private val context: Context): RecyclerView.Adapter<ClientCatalogAdapter.ViewHolder>() {

    private val listKatalog = mutableListOf<Katalog>()
    private lateinit var user: User

    fun setData(listKatalog: List<Katalog>) {
        this.listKatalog.clear()
        this.listKatalog.addAll(listKatalog)
        notifyDataSetChanged()
    }

    fun setUser(user: User) {
        this.user = user
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_katalog, parent, false))
    }

    override fun getItemCount(): Int = listKatalog.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgKatalog.clipToOutline = true
        holder.bindItem(listKatalog[position])
    }

    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view) {
        val imgKatalog: ImageView = view.findViewById(R.id.img_katalog)
        private val itemBody: MaterialCardView = view.findViewById(R.id.item_body)
        private val titleKatalog: TextView = view.findViewById(R.id.tv_title)
        private val priceKatalog: TextView = view.findViewById(R.id.tv_price)

        fun bindItem(item:Katalog) {
            if (item.photo!=null) {
                GlideApp.with(context)
                    .asBitmap()
                    .thumbnail(0.2f)
                    .centerCrop()
                    .load(item.photo?.get(0))
                    .into(imgKatalog)
            }

            titleKatalog.text = item.title
            if (item.price == 0 && item.isDonation == true) {
                priceKatalog.text = "Gratis"
            } else {
                priceKatalog.text = item.price.toString()
            }

            itemBody.setOnClickListener {view ->
                view.findNavController().navigate(
                    ClientKatalogFragmentDirections.Action_clientKatalogFragment_to_detailFragment(item.id, item.category).setUser(Gson().toJson(user)))
            }
        }
    }
}
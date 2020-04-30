package id.trydev.carimasker.model

import java.io.Serializable

data class Katalog (
    var id:String? = null,
    var title:String? = null,
    var description:String? = null,
    var price:Int? = null,
    var isDonation:Boolean? = null,
    var category: String? = null,
    var stock:Int? = null,
    var photo:List<String>? = null,
    var ownerId: String? = null
): Serializable
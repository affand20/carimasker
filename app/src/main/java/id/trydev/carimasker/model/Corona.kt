package id.trydev.carimasker.model

data class Corona (
    var provinsi:String? = null,
    var sembuh:Int? = null,
    var meninggal:Int? = null,
    var positif:Int? = null
)

data class CoronaResponse (
    val listData: List<Corona>
)
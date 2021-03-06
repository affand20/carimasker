package id.trydev.carimasker.model

data class User(
    var uid:String? = null,
    var email:String? = null,
    var fullname:String? = null,
    var phone:String? = null,
    var address:String? = null,
    var profileUrl:String? = null,
    var latitude:Double? = null,
    var longitude:Double? = null,
    var masker: HashMap<String, Any>? = null,
    var handsanitizer: HashMap<String, Any>? = null,
    var apd: HashMap<String, Any>? = null
)
package sk.stuba.bp

import java.io.Serializable
import java.util.*

 class Container(
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    var isActive: Boolean? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val custom: Boolean?=null
 ) : Serializable{

 }
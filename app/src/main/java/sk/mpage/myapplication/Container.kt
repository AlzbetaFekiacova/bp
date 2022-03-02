package sk.mpage.myapplication

import java.io.Serializable

data class Container(
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val isActive: Boolean? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
 )
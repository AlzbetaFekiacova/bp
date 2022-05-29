package sk.stuba.bp.databaseClasses

import java.io.Serializable

 class Container(
    val custom: Boolean?=null,
    @field:JvmField
    var isActive: Boolean? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
 ) : Serializable
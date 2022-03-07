package sk.stuba.bp

import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import java.io.Serializable
import java.util.*

data class Container(
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val isActive: Boolean? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timeStamp: Date? = null,
    val annotation: PointAnnotation? = null,
 ) : Serializable{

 }
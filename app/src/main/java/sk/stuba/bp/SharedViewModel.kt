package sk.stuba.bp

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager

class SharedViewModel : ViewModel() {

    var filters = mutableMapOf(
        MyConstants.BACK_UP to true,
        MyConstants.CONTAINER_GLASS to true,
        MyConstants.CONTAINER_PLASTIC to true,
        MyConstants.CONTAINER_PAPER to true,
        MyConstants.CONTAINER_COMMUNAL to true,
        MyConstants.CONTAINER_METAL to true,
        MyConstants.CONTAINER_BIO to true,
        MyConstants.BIN_COMMUNAL to true,
        MyConstants.BIN_PLASTIC to true,
        MyConstants.BIN_PAPER to true,
        MyConstants.CLOTHES_COLLECTING to true
    )

    var result = false
//    var backUpMachines = arrayListOf<Container>()
//    var containersPlastic = arrayListOf<Container>()
//    var containersPaper = arrayListOf<Container>()
//    var containersCommunal = arrayListOf<Container>()
//    var containersBio = arrayListOf<Container>()
//    var containersMetal = arrayListOf<Container>()
//    var containersGlass = arrayListOf<Container>()
//    var binsPlastic = arrayListOf<Container>()
//    var binsPaper = arrayListOf<Container>()
//    var binsCommunal = arrayListOf<Container>()
//    var clothesCollecting = arrayListOf<Container>()
//    var oloGlassContainers = arrayListOf<Container>()

    lateinit var databaseName: String
    lateinit var db: FirebaseFirestore
    var container = Container()
    var deleted = false
    lateinit var annotationManager: PointAnnotationManager
    lateinit var annotation: PointAnnotation

    var customAnnotations = mutableMapOf<PointAnnotation, Container>()

    fun saveContainer(item: Container, databaseName: String, db: FirebaseFirestore) {
        this.db = db
        this.db.collection(databaseName).add(item)
            .addOnSuccessListener {

                Log.d("ADD_ITEM", "$item added successfully")
            }
            .addOnFailureListener {
                Log.d("ADD_ITEM", "EXCEPTION: $it")
            }
    }


    fun click(pointAnnotation: PointAnnotation, pointAnnotationManager: PointAnnotationManager) {
        this.annotation = pointAnnotation
        this.annotationManager = pointAnnotationManager

    }

    fun addCustom(annotation: PointAnnotation, container: Container) {
        customAnnotations[annotation] = container
    }

    fun clickYes() {
        var id = ""
        db.collection(databaseName)
            .whereEqualTo("latitude", customAnnotations[annotation]?.latitude)
            .whereEqualTo("longitude", customAnnotations[annotation]?.longitude)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //Log.d("KOS", "${document.id} => ${document.data}")
                    id = document.id
                    container = Container(
                        document.getBoolean("custom"),
                        document.getBoolean("isActive"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                    )

                }

                if (id != "") {
                    val docRef = db.collection(databaseName).document(id)

                    if (!container.isActive!!) {
                        docRef.update("isActive", true)
                            .addOnSuccessListener {
                                Log.d("Suc", "super")
                                result = true
                            }
                            .addOnFailureListener { exception ->
                                Log.d("EX", "exc : $exception")
                                result = false
                            }
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.w("KOS", "Error getting documents: ", exception)
            }
    }


    fun clickNo() {
        //Log.d("MENO", databaseName)
        //Log.d("ANN", customAnnotations[annotation].toString())
        var id = ""
        deleted = false
        db.collection(databaseName)
            .whereEqualTo("latitude", customAnnotations[annotation]?.latitude)
            .whereEqualTo("longitude", customAnnotations[annotation]?.longitude)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //Log.d("KOS", "${document.id} => ${document.data}")
                    id = document.id
                    container = Container(
                        document.getBoolean("custom"),
                        document.getBoolean("isActive"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        //document.getDate("timeStamp"),

                    )

                }

                if (id != "") {
                    val docRef = db.collection(databaseName).document(id)

                    if (container.isActive!!) {
                        docRef.update("isActive", false)
                            .addOnSuccessListener {
                                Log.d("Suc", "super")
                                result = true
                                deleted = false
                            }
                            .addOnFailureListener { exception ->
                                Log.d("EX", "exc : $exception")
                                result = false
                            }
                    } else {
                        docRef.delete().addOnSuccessListener {
                            Log.d("DELETE", "deletion successful")
                            deleted = true
                        }
                            .addOnFailureListener { exception ->
                                Log.w("EX", "exception: $exception")
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("KOS", "Error getting documents: ", exception)
            }
    }


    fun change(value: String) {
        val bool = filters[value]
        filters[value] = !bool!!
    }

}
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
        MyConstants.CONTAINER_ELECTRO to true,
        MyConstants.CONTAINER_BIO to true,
        MyConstants.BIN_COMMUNAL to true,
        MyConstants.BIN_PLASTIC to true,
        MyConstants.BIN_PAPER to true,
        MyConstants.CLOTHES_COLLECTING to true
    )

    var result = false
    var backUpMachines = arrayListOf<Container>()
    var containersPlastic = arrayListOf<Container>()
    var containersPaper = arrayListOf<Container>()
    var containersCommunal = arrayListOf<Container>()
    var containersBio = arrayListOf<Container>()
    var containersElectro = arrayListOf<Container>()
    var containersGlass = arrayListOf<Container>()
    var binsPlastic = arrayListOf<Container>()
    var binsPaper = arrayListOf<Container>()
    var binsCommunal = arrayListOf<Container>()
    var clothesCollecting = arrayListOf<Container>()

    //private var _container = MutableLiveData<Container>()
    //var container: LiveData<Container> = _container
    lateinit var databaseName: String
    lateinit var db: FirebaseFirestore
    var container = Container()
    var deleted = false
    lateinit var annotationManager: PointAnnotationManager
    lateinit var annotation: PointAnnotation

    var customAnnotations = mutableMapOf<PointAnnotation, Container>()

    fun saveDb(dbb: FirebaseFirestore) {
        this.db = dbb
    }

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


    fun fillCollection(databaseName: String, container: Container) {
        when (databaseName) {
            MyConstants.BIN_PLASTIC -> {
                binsPlastic.add(container)
            }
            MyConstants.BIN_PAPER -> {
                binsPaper.add(container)
            }
            MyConstants.BIN_COMMUNAL -> {
                binsCommunal.add(container)
            }
            MyConstants.CLOTHES_COLLECTING -> {
                clothesCollecting.add(container)
            }
            MyConstants.BACK_UP -> {
                backUpMachines.add(container)
            }
            MyConstants.CONTAINER_COMMUNAL -> {
                containersCommunal.add(container)
            }
            MyConstants.CONTAINER_GLASS -> {
                containersGlass.add(container)
            }
            MyConstants.CONTAINER_ELECTRO -> {
                containersElectro.add(container)
            }
            MyConstants.CONTAINER_PLASTIC -> {
                containersPlastic.add(container)
            }
            MyConstants.CONTAINER_PAPER -> {
                containersPaper.add(container)
            }
            MyConstants.CONTAINER_BIO -> {
                containersBio.add(container)
            }
        }
    }

    fun click(pointAnnotation: PointAnnotation, pointAnnotationManager: PointAnnotationManager) {
        this.annotation = pointAnnotation
        this.annotationManager = pointAnnotationManager
        for (i in customAnnotations) {
            Log.d("CONT", i.value.toString())
        }

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
                        document.getBoolean("isActive"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        document.getDate("timeStamp"),
                        document.getBoolean("custom")
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
                        document.getBoolean("isActive"),
                        document.getDouble("latitude"),
                        document.getDouble("longitude"),
                        document.getDate("timeStamp"),
                        document.getBoolean("custom")
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
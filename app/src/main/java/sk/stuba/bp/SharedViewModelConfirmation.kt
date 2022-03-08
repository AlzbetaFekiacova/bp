package sk.stuba.bp

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class SharedViewModelConfirmation : ViewModel() {
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

    private var _container = MutableLiveData<Container>()
    var container: LiveData<Container> = _container
    //lateinit var db: FirebaseFirestore

    fun saveContainer(item: Container, databaseName:String, db: FirebaseFirestore) {
        db.collection(databaseName).add(container)
            .addOnSuccessListener {

                Log.d("ADD_ITEM", "$container added successfully")
            }
            .addOnFailureListener {
                Log.d("ADD_ITEM", "EXCEPTION: $it")
            }
    }



    fun fillCollection(databaseName: String) {
        when (databaseName) {
            MyConstants.BIN_PLASTIC -> {
                container.value?.let { binsPlastic.add(it) }
            }
            MyConstants.BIN_PAPER -> {
                container.value?.let { binsPaper.add(it) }
            }
            MyConstants.BIN_COMMUNAL -> {
                container.value?.let { binsCommunal.add(it) }
            }
            MyConstants.CLOTHES_COLLECTING -> {
                container.value?.let { clothesCollecting.add(it) }
            }
            MyConstants.BACK_UP -> {
                container.value?.let { backUpMachines.add(it) }

            }
            MyConstants.CONTAINER_COMMUNAL -> {
                container.value?.let { containersCommunal.add(it) }

            }
            MyConstants.CONTAINER_GLASS -> {
                container.value?.let { containersGlass.add(it) }

            }
            MyConstants.CONTAINER_ELECTRO -> {
                container.value?.let { containersElectro.add(it) }

            }
            MyConstants.CONTAINER_PLASTIC -> {
                container.value?.let { containersPlastic.add(it) }

            }
            MyConstants.CONTAINER_PAPER -> {
                container.value?.let { containersPaper.add(it) }

            }
            MyConstants.CONTAINER_BIO -> {
                container.value?.let { containersBio.add(it) }
            }
        }
    }
}
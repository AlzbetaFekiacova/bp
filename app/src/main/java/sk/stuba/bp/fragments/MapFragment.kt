package sk.stuba.bp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import sk.stuba.bp.*
import sk.stuba.bp.databinding.FragmentMapBinding
import kotlin.collections.ArrayList
import kotlin.system.measureTimeMillis


const val REQUEST_CODE = 101

class MapFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var onMapReady: (MapboxMap) -> Unit
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var pointAnnotationManager: PointAnnotationManager? = null
    private var currentPosition = Point.fromLngLat(19.13491, 48.6385)
    private val constLocation = Point.fromLngLat(19.13491, 48.6385)
    private var addingContent = false
    private lateinit var content: Number
    private lateinit var databaseName: String
    private val sharedViewModel: SharedViewModel by activityViewModels()
    var backUpMachines = arrayListOf<Container>()
    var containersPlastic = arrayListOf<Container>()
    var containersPaper = arrayListOf<Container>()
    var containersCommunal = arrayListOf<Container>()
    var containersBio = arrayListOf<Container>()
    var containersMetal = arrayListOf<Container>()
    var containersGlass = arrayListOf<Container>()
    var binsPlastic = arrayListOf<Container>()
    var binsPaper = arrayListOf<Container>()
    var binsCommunal = arrayListOf<Container>()
    var clothesCollecting = arrayListOf<Container>()
    var oloGlassContainers = arrayListOf<Container>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        //super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        //auth = Firebase.auth
        /*lifecycleScope.launch {
            val time = measureTimeMillis {
                anonymousUser()
            }
            Log.d("TIME", "it took $time")
        }*/
        if (!checkIfLoggedIn()) {
            lifecycleScope.launch { anonymousUser() }
        }

        readDatabase()
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView

        Log.d("MAP", sharedViewModel.filters.toString())

        binding.btnCancel.setOnClickListener {

            pointAnnotationManager?.delete(pointAnnotationManager!!.annotations[pointAnnotationManager!!.annotations.lastIndex])
            binding.btnCancel.visibility = View.INVISIBLE
            binding.btnDone.visibility = View.INVISIBLE
            addingContent = false
        }


        binding.btnDone.setOnClickListener {

            pointAnnotationManager!!.annotations[pointAnnotationManager!!.annotations.size - 1].isDraggable =
                false
            val container = Container(
                custom = true,
                isActive = true,
                latitude = pointAnnotationManager!!.annotations[pointAnnotationManager!!.annotations.size - 1].point.latitude(),
                longitude = pointAnnotationManager!!.annotations[pointAnnotationManager!!.annotations.size - 1].point.longitude()

            )

            val db = FirebaseFirestore.getInstance()
            sharedViewModel.saveContainer(container, databaseName, db)
            binding.btnDone.visibility = View.INVISIBLE
            binding.btnCancel.visibility = View.INVISIBLE

            addingContent = false

        }
        binding.btnPosition.setOnClickListener(this)
        binding.btnFilter.setOnClickListener {
            val dialog = FilterFragment()
            parentFragmentManager.let { dialog.show(it, "customDialog") }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapboxMap = mapView.getMapboxMap()
        if (::onMapReady.isInitialized) {
            onMapReady.invoke(mapboxMap)
        }
    }

    override fun onClick(postition: View?) {
        fetchLocation()
    }

    private fun fetchLocation() {

        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                Toast.makeText(context, "Lokalizovné", Toast.LENGTH_SHORT).show()
                currentPosition = Point.fromLngLat(it.longitude, it.latitude)
                val initialCameraOptions = CameraOptions.Builder()
                    .center(currentPosition)
                    .zoom(15.5)
                    .build()

                mapView.getMapboxMap().flyTo(
                    initialCameraOptions,
                    mapAnimationOptions {
                        duration(5000)
                    }
                )
                addAnnotationToMap(
                    currentPosition,
                    R.drawable.marker_current_position,
                    false,
                    false
                )

            } else {
                Toast.makeText(context, "Nepodarilo sa lokalizovať", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAnnotationToMap(point: Point, marker: Int, draggable: Boolean, custom: Boolean) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            requireContext(),
            //R.drawable.red_marker
            marker
        )?.let {
            val annotationApi = mapView.annotations
            //val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
            pointAnnotationManager = annotationApi.createPointAnnotationManager()
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(point)
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
                .withDraggable(draggable)


            pointAnnotationManager!!.create(pointAnnotationOptions)
            if (custom && checkIfLoggedIn()) {
                pointAnnotationManager!!.addClickListener(OnPointAnnotationClickListener { it ->
                    if (currentPosition.latitude() != 48.6385 && currentPosition.longitude() != 19.13491) {
                        if (getDistance(
                                currentPosition.latitude(),
                                currentPosition.longitude(),
                                it.point.latitude(),
                                it.point.longitude()
                            ) < 200
                        ) {
                            when (marker) {
                                R.drawable.marker_container_plastic -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.CONTAINER_PLASTIC
                                }
                                R.drawable.marker_container_paper -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.CONTAINER_PAPER
                                }
                                R.drawable.marker_container_glass -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.CONTAINER_GLASS
                                }
                                R.drawable.marker_container_electro -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.CONTAINER_METAL
                                }
                                R.drawable.marker_container_bio -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.CONTAINER_BIO
                                }
                                R.drawable.marker_container_comunal -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.CONTAINER_COMMUNAL
                                }
                                R.drawable.marker_collecting_clothes -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.CLOTHES_COLLECTING
                                }
                                R.drawable.marker_back_up -> {
                                    sharedViewModel.databaseName = MyConstants.BACK_UP
                                }
                                R.drawable.marker_bin_paper -> {
                                    sharedViewModel.databaseName = MyConstants.BIN_PAPER
                                }
                                R.drawable.marker_bin_comunal -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.BIN_COMMUNAL
                                }
                                R.drawable.marker_bin_plastic -> {
                                    sharedViewModel.databaseName =
                                        MyConstants.BIN_PLASTIC
                                }
                            }
                            sharedViewModel.db = FirebaseFirestore.getInstance()
                            sharedViewModel.click(it, pointAnnotationManager!!)
                            val confirmationFragment = PositionConfirmationFragment()
                            confirmationFragment.show(parentFragmentManager, "customDialog")
                        }

                    }
                    true
                })
            }
        }

    }

    private fun getDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val loc1 = Location("")
        loc1.latitude = lat1
        loc1.longitude = lng1
        val loc2 = Location("")
        loc2.latitude = lat2
        loc2.longitude = lng2
        return loc1.distanceTo(loc2)

    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val registerItem = menu.findItem(R.id.itemRegister)
        val logInItem = menu.findItem(R.id.itemLogIn)
        registerItem.isVisible = !checkIfLoggedIn()
        logInItem.isVisible = !checkIfLoggedIn()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val logOrRegDialogFragment = LogOrRegDialogFragment()
        //val addingDialogFragment = AddingDialogFragment(2)
        when (item.itemId) {

            R.id.itemProfile -> {
                if (checkIfLoggedIn()) {
                    findNavController().navigate(R.id.profileFragment)
                } else {
                    parentFragmentManager.let {
                        logOrRegDialogFragment.show(
                            it,
                            "customDialog"
                        )
                    }
                }
            }

            R.id.itemClothes -> {
                if (!checkIfLoggedIn())
                    parentFragmentManager.let {
                        logOrRegDialogFragment.show(
                            it,
                            "customDialog"
                        )
                    }
                else if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    parentFragmentManager.let {
                        addingContent = true
                        //content = 0
                        //databaseName = "clothesCollecting"
                        //databaseName = MyConstants.CLOTHES_COLLECTING
                        //addItem(3, 0)
                        databaseName = MyConstants.CLOTHES_COLLECTING
                        addItem(databaseName)

                    }
                    //addingDialogFragment.show(it, "customDialog") }
                }
            }
            R.id.itemBackingUp -> {
                if (!checkIfLoggedIn())
                    parentFragmentManager.let {
                        logOrRegDialogFragment.show(
                            it,
                            "customDialog"
                        )
                    }
                else if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    parentFragmentManager.let {
                        addingContent = true
                        content = 0
                        //databaseName = "backUp"
                        databaseName = MyConstants.BACK_UP
                        addItem(databaseName)
                        //addItem(4, 0)
                        //AddingDialogFragment(4).show(it, "customDialog")
                    }
                }
            }

            R.id.itemLogIn -> {
                findNavController().navigate(R.id.loginFragment)
            }
            R.id.itemRegister -> {
                findNavController().navigate(R.id.registrationFragment)
            }
//            R.id.itemAppInfo -> {
//                Toast.makeText(context, "Clicked on app info", Toast.LENGTH_LONG).show()
//            }
            R.id.itemRecycleInfo -> {
                //Toast.makeText(context, "Clicked on recycle info", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.separationInfoFragment)
            }
            R.id.itemBin -> {
                if (!checkIfLoggedIn())
                    parentFragmentManager.let {
                        logOrRegDialogFragment.show(
                            it,
                            "customDialog"
                        )
                    }
                else if (!addingContent) {
                    parentFragmentManager.let {
                        //addingDialogFragment.show(it, "customDialog")
                        //AddingDialogFragment(2).show(it, "customDialog")
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            R.id.subItemMixBin -> {
                if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    addingContent = true
                    databaseName = MyConstants.BIN_COMMUNAL
                    addItem(databaseName)
                    //content = 1
                    //databaseName = "trashBins"
                    //addItem(2, 1)
                }
            }
            R.id.subItemPaperBin -> {
                if (addingContent)
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    addingContent = true
                    databaseName = MyConstants.BIN_PAPER
                    addItem(databaseName)
                    //content = 2
                    //databaseName = "trashBins"
                    //addItem(2, 2)
                }
            }
            R.id.subItemPlasticBin -> {
                if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    addingContent = true
                    databaseName = MyConstants.BIN_PLASTIC
                    addItem(databaseName)
                    //databaseName = "trashBins"
                    //content = 3
                    //addItem(2, 3)
                }
            }

            R.id.itemContainer -> {
                if (!checkIfLoggedIn())
                    parentFragmentManager.let {
                        logOrRegDialogFragment.show(
                            it,
                            "customDialog"
                        )
                    }
                else if (!addingContent) {
                    parentFragmentManager.let {
                        //addingDialogFragment.show(it, "customDialog")
                        //AddingDialogFragment(2).show(it, "customDialog")
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            R.id.subItemMixContainer -> {

                if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    addingContent = true
                    databaseName = MyConstants.CONTAINER_COMMUNAL
                    addItem(databaseName)
                    //databaseName = "trashContainers"
                    //content = 1
                    //addItem(5, 1)
                }
            }
            R.id.subItemPaperContainer -> {

                if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    addingContent = true
                    databaseName = MyConstants.CONTAINER_PAPER
                    addItem(databaseName)
                    //databaseName = "trashContainers"
                    //content = 2
                    //addItem(5, 2)
                }
            }
            R.id.subItemPlasticContainer -> {

                if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    addingContent = true
                    //databaseName = "trashContainers"
                    //content = 3
                    //addItem(5, 3)
                    databaseName = MyConstants.CONTAINER_PLASTIC
                    addItem(databaseName)
                }
            }
            R.id.subItemElectronicsContainer -> {

                if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    addingContent = true
                    //databaseName = "trashContainers"
                    //content = 4
                    //addItem(5, 4)
                    databaseName = MyConstants.CONTAINER_METAL
                    addItem(databaseName)
                }
            }
            R.id.subItemBioContainer -> {

                if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    addingContent = true
                    //databaseName = "trashContainers"
                    //content = 5
                    //addItem(5, 5)
                    databaseName = MyConstants.CONTAINER_BIO
                    addItem(databaseName)
                }
            }
            R.id.subItemGlassContainer -> {

                if (addingContent) {
                    Toast.makeText(
                        context,
                        "Pre pridanie musis najprv potvrdit pridanie posledneho kosa",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    addingContent = true
                    databaseName = MyConstants.CONTAINER_GLASS
                    addItem(databaseName)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    //private fun addItem(container: Int, content: Int) {
    private fun addItem(item: String) {
        if(currentPosition.equals(constLocation)){
            Toast.makeText(context, "Pre pridanie sa musíš najprv lokalizovať", Toast.LENGTH_SHORT).show()
            addingContent = false
        }
        else {


            val position = Point.fromLngLat(
                currentPosition.longitude() - 0.001,
                currentPosition.latitude() + 0.001
            )
            when (item) {
                MyConstants.CONTAINER_GLASS -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_container_glass,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.CONTAINER_METAL -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_container_electro,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.CONTAINER_PLASTIC -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_container_plastic,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.CONTAINER_PAPER -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_container_paper,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.CONTAINER_COMMUNAL -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_container_comunal,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.CONTAINER_BIO -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_container_bio,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.BIN_PLASTIC -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_bin_plastic,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.BIN_PAPER -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_bin_paper,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.BIN_COMMUNAL -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_bin_comunal,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.CLOTHES_COLLECTING -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_collecting_clothes,
                        draggable = true,
                        custom = true
                    )
                }
                MyConstants.BACK_UP -> {
                    addAnnotationToMap(
                        position, R.drawable.marker_back_up,
                        draggable = true,
                        custom = true
                    )
                }

            }

            val cameraOptions = CameraOptions.Builder()
                .center(position)
                .zoom(15.5)
                .build()

            mapView.getMapboxMap().flyTo(
                cameraOptions,
                mapAnimationOptions {
                    duration(3000)
                }
            )



            binding.btnDone.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.VISIBLE

        }
    }

    private fun checkIfLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("MAP_FRAGMENT LOG", currentUser?.email.toString());
        /*if (currentUser != null) {
            return currentUser.email != null
        }*/
        if (currentUser != null) {
            Log.d("MAP_FRAGMENT", currentUser.isAnonymous.toString())
            return !currentUser.isAnonymous;
        }
        //return FirebaseAuth.getInstance().currentUser?.isAnonymous ?:
        return false
    }

    private suspend fun anonymousUser() = withContext(Dispatchers.IO) {
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnSuccessListener {
                val currentUser = FirebaseAuth.getInstance().currentUser
                Log.d("signInAnonymously", "Authentication successful")
            }
            .addOnFailureListener { task ->
                Log.w("signInAnonymously", "Authentication failed", task.cause)
            }


    }


//    suspend fun fetchDecksByLanguage(name: String,
//                                     db: FirebaseFirestore,
//                                     arrayToFill: ArrayList<Container>): List<Container> {
//
//        val result = suspendCancellableCoroutine<List<Container>> { continuation ->
//
//            db.collection("Decks")
//                .whereArrayContains("languages", activeLanguage)
//                .get()
//                .addOnSuccessListener { documents ->
//
//                    val items = ArrayList<Decks>()
//
//                    if (documents != null) {
//                        for (document in documents) {
//                            val id: String = document.id
//                            val name: String = document.data["name"] as String
//                            val languages: List<String> =
//                                document.data["languages"] as List<String>
//                            items.add(Decks(id, name, languages))
//                        }
//
//                    }
//                    continuation.resume(items)
//                }
//                .addOnFailureListener { err ->
//                    continuation.resumeWithException(err)
//                }
//        }
//        return result
//    }

    private fun readCollection(
        name: String,
        db: FirebaseFirestore,
        arrayToFill: ArrayList<Container>
    ) {
        db.collection(name)
            .get()
            .addOnSuccessListener { result ->
                arrayToFill.addAll(result.toObjects(Container::class.java))
                addMarkersFromArray(arrayToFill, name)
                databaseName = name
            }
            .addOnFailureListener { exception ->
                Log.d("DATA", "Error getting documents: $name", exception)
            }


    }

    private fun addMarkersFromArray(markers: ArrayList<Container>, name: String) {
        for (container in markers) {
            when (name) {
                MyConstants.CONTAINER_GLASS -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_container_glass,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.CONTAINER_PLASTIC -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_container_plastic,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.CONTAINER_PAPER -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_container_paper,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.CONTAINER_BIO -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_container_bio,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.CONTAINER_COMMUNAL -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_container_comunal,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.CONTAINER_METAL -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_container_electro,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.BIN_PLASTIC -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_bin_plastic,
                        draggable = false,
                        custom = true
                    )
                }
                MyConstants.BIN_PAPER -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_bin_paper,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.BIN_COMMUNAL -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_bin_comunal,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.CLOTHES_COLLECTING -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_collecting_clothes,
                        draggable = false,
                        container.custom!!
                    )
                }
                MyConstants.BACK_UP -> {
                    addAnnotationToMap(
                        Point.fromLngLat(container.longitude!!, container.latitude!!),
                        R.drawable.marker_back_up,
                        draggable = false,
                        container.custom!!
                    )
                }
            }
            if (container.custom == true && pointAnnotationManager != null) {

                sharedViewModel.addCustom(
                    pointAnnotationManager!!.annotations[pointAnnotationManager!!.annotations.size - 1],
                    container
                )
            }

        }
    }

    private fun readDatabase() {
        val db = FirebaseFirestore.getInstance()
        if (sharedViewModel.filters[MyConstants.CLOTHES_COLLECTING] == true) {
            readCollection(
                MyConstants.CLOTHES_COLLECTING,
                db,
                // sharedViewModel.clothesCollecting
                clothesCollecting
            )
        }
        if (sharedViewModel.filters[MyConstants.BACK_UP] == true) {
            readCollection(
                MyConstants.BACK_UP, db,
                //sharedViewModel.backUpMachines)
                backUpMachines
            )
        }
        if (sharedViewModel.filters[MyConstants.CONTAINER_BIO] == true) {
            readCollection(
                MyConstants.CONTAINER_BIO, db,
                //sharedViewModel.containersBio
                containersBio
            )
        }
        if (sharedViewModel.filters[MyConstants.CONTAINER_COMMUNAL] == true) {
            readCollection(
                MyConstants.CONTAINER_COMMUNAL,
                db,
                //sharedViewModel.containersCommunal
                containersCommunal
            )
        }
        if (sharedViewModel.filters[MyConstants.CONTAINER_PAPER] == true) {
            readCollection(
                MyConstants.CONTAINER_PAPER,
                db,
                //sharedViewModel.
                containersPaper
            )
        }
        if (sharedViewModel.filters[MyConstants.CONTAINER_PLASTIC] == true) {
            readCollection(
                MyConstants.CONTAINER_PLASTIC,
                db,
                //sharedViewModel.
                containersPlastic
            )
        }
        if (sharedViewModel.filters[MyConstants.CONTAINER_METAL] == true) {
            readCollection(
                MyConstants.CONTAINER_METAL,
                db,
                //sharedViewModel.
                containersMetal
            )
        }
        if (sharedViewModel.filters[MyConstants.CONTAINER_GLASS] == true) {
            readCollection(
                MyConstants.CONTAINER_GLASS,
                db,
                //sharedViewModel.
                containersGlass
            )

            readCollection(
                MyConstants.OLO_DATA_GLASS, db,
                //sharedViewModel.
                containersGlass
            )

        }
        if (sharedViewModel.filters[MyConstants.BIN_COMMUNAL] == true) {
            readCollection(
                MyConstants.BIN_COMMUNAL, db,
                //sharedViewModel.
                binsCommunal
            )
        }
        if (sharedViewModel.filters[MyConstants.BIN_PAPER] == true) {
            readCollection(
                MyConstants.BIN_PAPER, db, //sharedViewModel.
                binsPaper
            )
        }
        if (sharedViewModel.filters[MyConstants.BIN_PLASTIC] == true) {
            readCollection(
                MyConstants.BIN_PLASTIC, db, //sharedViewModel.
                binsPlastic
            )
        }
    }


}
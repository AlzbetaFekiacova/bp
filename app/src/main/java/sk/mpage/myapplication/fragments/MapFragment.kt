package sk.mpage.myapplication.fragments

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
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.addOnMapLongClickListener
import sk.mpage.myapplication.Place
import sk.mpage.myapplication.R
import sk.mpage.myapplication.databinding.FragmentMapBinding

const val REQUEST_CODE = 101

class MapFragment : Fragment(), View.OnClickListener, OnMapLongClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var onMapReady: (MapboxMap) -> Unit
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth
    private var backUpPlaces = arrayListOf<Place>()
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private var currentPosition = Point.fromLngLat(19.13491, 48.6385)
    private var addingContent = false
    private lateinit var content: Number
    private lateinit var databaseName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        //super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
        //readDatabase()


        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView

        binding.btnDone.setOnClickListener {

            pointAnnotationManager.annotations[pointAnnotationManager.annotations.size - 1].isDraggable =
                false
            val bin = hashMapOf(
                "latitude" to pointAnnotationManager.annotations[pointAnnotationManager.annotations.size - 1].point.latitude(),
                "longitude" to pointAnnotationManager.annotations[pointAnnotationManager.annotations.size - 1].point.longitude(),
                "content" to content,
                "isActive" to true
            )

            val db = FirebaseFirestore.getInstance()

            db.collection(databaseName).add(bin)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Uspesne pridane",
                        Toast.LENGTH_SHORT
                    ).show()

                }


                .addOnFailureListener {
                    Toast.makeText(context, "Neprebehlo pridanie", Toast.LENGTH_SHORT).show()
                }

            binding.btnDone.visibility = View.INVISIBLE
            addingContent = false

        }

        binding.btnPosition.setOnClickListener(this)
        binding.btnFilter.setOnClickListener {
            Toast.makeText(context, "clicked on filter", Toast.LENGTH_SHORT).show()
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
        mapboxMap.addOnMapLongClickListener(this)


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
                addAnnotationToMap(currentPosition, R.drawable.marker_current_position, false)

            } else {
                Toast.makeText(context, "Nepodarilo sa lokalizovať", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAnnotationToMap(point: Point, marker: Int, draggable: Boolean) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            requireContext(),
            //R.drawable.red_marker
            marker
        )?.let {
            val annotationApi = mapView.annotations
            //val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
            pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(point)
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
                .withDraggable(draggable)


            pointAnnotationManager.create(pointAnnotationOptions)
            if (marker != R.drawable.marker_current_position) {
                pointAnnotationManager.addClickListener(OnPointAnnotationClickListener {
                    if (currentPosition.latitude() != 48.6385 && currentPosition.longitude() != 19.13491) {

                        if (getDistance(
                                currentPosition.latitude(),
                                currentPosition.longitude(),
                                pointAnnotationManager.annotations[pointAnnotationManager.annotations.size-1].point.latitude(),
                                pointAnnotationManager.annotations[pointAnnotationManager.annotations.size-1].point.longitude()
                            ) < 200
                        ) {

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
        Log.d("VZD", "" + loc1.distanceTo(loc2))
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
                    parentFragmentManager.let { logOrRegDialogFragment.show(it, "customDialog") }
                }
            }

            R.id.itemClothes -> {
                if (!checkIfLoggedIn())
                    parentFragmentManager.let { logOrRegDialogFragment.show(it, "customDialog") }
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
                        databaseName = "clothesCollecting"
                        addItem(3, 0)
                    }
                    //addingDialogFragment.show(it, "customDialog") }
                }
            }
            R.id.itemBackingUp -> {
                if (!checkIfLoggedIn())
                    parentFragmentManager.let { logOrRegDialogFragment.show(it, "customDialog") }
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
                        databaseName = "backUp"
                        addItem(4, 0)
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
            R.id.itemAppInfo -> {
                Toast.makeText(context, "Clicked on app info", Toast.LENGTH_LONG).show()
            }
            R.id.itemRecycleInfo -> {
                Toast.makeText(context, "Clicked on recycle info", Toast.LENGTH_LONG).show()
            }
            R.id.itemBin -> {
                if (!checkIfLoggedIn())
                    parentFragmentManager.let { logOrRegDialogFragment.show(it, "customDialog") }
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
                    content = 1
                    databaseName = "trashBins"
                    addItem(2, 1)
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
                    content = 2
                    databaseName = "trashBins"
                    addItem(2, 2)
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
                    databaseName = "trashBins"
                    content = 3
                    addItem(2, 3)
                }
            }

            R.id.itemContainer -> {
                if (!checkIfLoggedIn())
                    parentFragmentManager.let { logOrRegDialogFragment.show(it, "customDialog") }
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
                    databaseName = "trashContainers"
                    content = 1
                    addItem(5, 1)
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
                    databaseName = "trashContainers"
                    content = 2
                    addItem(5, 2)
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
                    databaseName = "trashContainers"
                    content = 3
                    addItem(5, 3)
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
                    databaseName = "trashContainers"
                    content = 4
                    addItem(5, 4)
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
                    databaseName = "trashContainers"
                    content = 5
                    addItem(5, 5)
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
                    databaseName = "trashContainers"
                    content = 6
                    addItem(5, 6)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    private fun addItem(container: Int, content: Int) {
        val position = Point.fromLngLat(
            currentPosition.longitude() - 0.001,
            currentPosition.latitude() + 0.001
        )
        when (container) {
            2 -> {
                when (content) {
                    1 -> {
                        addAnnotationToMap(position, R.drawable.marker_bin_comunal, true)
                    }
                    2 -> {
                        addAnnotationToMap(position, R.drawable.marker_bin_paper, true)
                    }
                    3 -> {
                        addAnnotationToMap(position, R.drawable.marker_bin_plastic, true)
                    }
                }
            }
            3 -> {
                addAnnotationToMap(position, R.drawable.marker_collecting_clothes, true)
            }
            4 -> {
                addAnnotationToMap(position, R.drawable.marker_back_up, true)
            }
            else -> {
                when (content) {
                    1 -> {
                        addAnnotationToMap(position, R.drawable.marker_container_comunal, true)
                    }
                    2 -> {
                        addAnnotationToMap(position, R.drawable.marker_container_paper, true)
                    }
                    3 -> {
                        addAnnotationToMap(position, R.drawable.marker_container_plastic, true)
                    }
                    4 -> {
                        addAnnotationToMap(position, R.drawable.marker_container_electro, true)
                    }
                    5 -> {
                        addAnnotationToMap(position, R.drawable.marker_container_bio, true)
                    }
                    6 -> {
                        addAnnotationToMap(position, R.drawable.marker_container_glass, true)
                    }
                }
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


    }

    private fun checkIfLoggedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }

    private fun readDatabase() {
        val db = FirebaseFirestore.getInstance()
        //db.collection("sample_collection")
        //db.collection("trash_containers")
        //db.collection("backUp")
        db.collection("olo_data")
            .get()
            .addOnSuccessListener { result ->
                result.forEach { document ->
                    Log.d("DATA", "${document.id} => ${document.data}")
                    val p = Place(
                        document.getDouble("latitude")!!,
                        document.getDouble("longitude")!!
                    )
                    backUpPlaces.add(p)

                    addAnnotationToMap(
                        Point.fromLngLat(p.longitude, p.latitude),
                        R.drawable.marker_container_glass,
                        false
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.d("DATA", "Error getting documents: ", exception)
            }
    }

    override fun onMapLongClick(point: Point): Boolean {
        Log.d("KLICK", "kliknutie na mapu")
        return false
    }

}


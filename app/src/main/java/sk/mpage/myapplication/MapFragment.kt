package sk.mpage.myapplication

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import sk.mpage.myapplication.databinding.FragmentMapBinding
import android.annotation.SuppressLint

import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.animation.flyTo

const val REQUEST_CODE = 101

class MapFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var onMapReady: (MapboxMap) -> Unit
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapView = binding.mapView

        binding.btnPosition.setOnClickListener(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
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
                val point = Point.fromLngLat(it.longitude, it.latitude)
                val initialCameraOptions = CameraOptions.Builder()
                    .center(point)
                    .zoom(15.5)
                    .build()

                mapView.getMapboxMap().flyTo(
                    initialCameraOptions,
                    mapAnimationOptions {
                        duration(5000)
                    }
                )
                addAnnotationToMap(point)

            } else {
                Toast.makeText(context, "Nepodarilo sa lokalizovať", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addAnnotationToMap(point: Point) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(point)
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
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
        val logOutItem = menu.findItem(R.id.logOut)
        val registerItem = menu.findItem(R.id.itemRegister)
        val logInItem = menu.findItem(R.id.itemLogIn)
        registerItem.isVisible = !checkIfLoggedIn()
        logInItem.isVisible = !checkIfLoggedIn()
        logOutItem.isVisible = checkIfLoggedIn()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val logOrRegDialogFragment = LogOrRegDialogFragment()
        val addingDialogFragment = AddingDialogFragment()
        when (item.itemId) {

            R.id.logOut -> {
                if (checkIfLoggedIn()) {
                    Firebase.auth.signOut()
                    Toast.makeText(context, "Bol si úspešne odhlásený", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.itemClothes -> {
                Toast.makeText(context, "Clicked on clothes", Toast.LENGTH_LONG).show()
                if (!checkIfLoggedIn())
                    parentFragmentManager.let { logOrRegDialogFragment.show(it, "customDialog") }
                else
                    parentFragmentManager.let { addingDialogFragment.show(it, "customDialog") }
            }
            R.id.itemBackingUp -> {
                Toast.makeText(context, "Clicked on back up", Toast.LENGTH_LONG).show()
                if (!checkIfLoggedIn())
                    parentFragmentManager.let { logOrRegDialogFragment.show(it, "customDialog") }
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
            else -> {
                Toast.makeText(context, "Clicked on antoher item", Toast.LENGTH_LONG).show()

            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkIfLoggedIn(): Boolean {
        val currentUser = auth.currentUser
        return currentUser != null
    }
}


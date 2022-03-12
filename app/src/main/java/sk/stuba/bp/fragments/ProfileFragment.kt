package sk.stuba.bp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import sk.stuba.bp.R
import sk.stuba.bp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        auth = Firebase.auth
        val email = auth.currentUser?.email
        binding.textViewIdentity.text = "EMAIL: $email"

        binding.buttonLogOut.setOnClickListener {

            val alertDialog: AlertDialog? = activity.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(
                        "Áno, chcem sa odhlásiť"
                    ) { _, _ ->
                        auth.signOut()
                        Toast.makeText(context, "Boli ste úspešne odhlásený", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.mapFragment)
                    }
                    setNegativeButton(
                        "Nie, nechcem sa odhlásiť"
                    ) { _, _ ->
                        findNavController().navigate(R.id.mapFragment)
                    }
                }
                builder.setTitle("ODHLÁSENIE Z ÚČTU")
                builder.setMessage("Po odhlásení nebude možné využívať plnú funkcionalitu aplikácie.")
                // Create the AlertDialog
                builder.create()
            }
            alertDialog?.show()
        }

        binding.buttonDeleteAccount.setOnClickListener {

            val alertDialog: AlertDialog? = activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(
                        "Áno, chcem zmazať"
                    ) { _, _ ->
                        val user = Firebase.auth.currentUser!!

                        user.delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Váš účet bol úspešne odstánený",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigate(R.id.mapFragment)
                                }
                            }
                    }
                    setNegativeButton(
                        "Nie, nechcem zmazať"
                    ) { _, _ ->
                        findNavController().navigate(R.id.mapFragment)
                    }
                }
                // Set other dialog properties
                builder.setTitle("ZMAZANIE ÚČTU")
                builder.setMessage("Po zamazní účtu stratíte prístup k vášmu účtu.")
                // Create the AlertDialog
                builder.create()
            }
            alertDialog?.show()
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(p0: View?) {

    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        inflater.inflate(R.menu.back_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("MENU", "click")
        findNavController().navigate(R.id.mapFragment)
        return super.onOptionsItemSelected(item)
    }

}
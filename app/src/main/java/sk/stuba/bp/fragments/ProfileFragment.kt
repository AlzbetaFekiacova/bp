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
                        getString(R.string.IwantToLogInTxt)
                    ) { _, _ ->
                        auth.signOut()
                        Toast.makeText(context, getString(R.string.succesffulyLoggedOut), Toast.LENGTH_SHORT)
                            .show()
                        findNavController().navigate(R.id.mapFragment)
                    }
                    setNegativeButton(
                        getString(R.string.IdoNotWantToLogOut)
                    ) { _, _ ->
                        findNavController().navigate(R.id.mapFragment)
                    }
                }
                builder.setTitle(getString(R.string.toLogOut))
                builder.setMessage(getString(R.string.NotFullFuncionallityTxt))
                builder.create()
            }
            alertDialog?.show()
        }

        binding.buttonDeleteAccount.setOnClickListener {

            val alertDialog: AlertDialog? = activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.apply {
                    setPositiveButton(
                        getString(R.string.yesToDelete)
                    ) { _, _ ->
                        val user = Firebase.auth.currentUser!!

                        user.delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        getString(R.string.successfullyDeleted),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    findNavController().navigate(R.id.mapFragment)
                                }
                            }
                    }
                    setNegativeButton(
                        getString(R.string.noToDelete)
                    ) { _, _ ->
                        findNavController().navigate(R.id.mapFragment)
                    }
                }

                builder.setTitle(getString(R.string.ToDelete))
                builder.setMessage(getString(R.string.afterDeleteNoFullFuncionallity))
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
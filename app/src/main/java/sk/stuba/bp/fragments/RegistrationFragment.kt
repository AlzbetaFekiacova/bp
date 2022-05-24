package sk.stuba.bp.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import sk.stuba.bp.R
import sk.stuba.bp.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        binding.btnRegister.setOnClickListener(this)
        auth = Firebase.auth
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(p0: View?) {
        when {
            TextUtils.isEmpty(binding.editTxtEmailRegister.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(context, getString(R.string.emailRequierdText), Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(
                binding.editTxtPasswordRegister.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(context, getString(R.string.enterPassworText), Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(
                binding.editTxtReenterPasswordRegister.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(context, "Musíš znovu zadať heslo", Toast.LENGTH_LONG).show()
            }
            !checkEqualityOfPassword() -> {
                Toast.makeText(context, "Heslá sa nezhodujú", Toast.LENGTH_LONG).show()
            }
            else -> {
                val email = binding.editTxtEmailRegister.text.toString().trim { it <= ' ' }
                val password = binding.editTxtPasswordRegister.text.toString()


                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val user = auth.currentUser
                            logInUser(email, password)
                            Toast.makeText(context, getString(R.string.successfulRegistration), Toast.LENGTH_SHORT)
                                .show()
                            findNavController().navigate(R.id.mapFragment)
                        } else {
                            Toast.makeText(
                                context,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }
    }

    private fun logInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                } else {
                    Toast.makeText(
                        context, getString(R.string.noSuccessfulLogIn),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun checkEqualityOfPassword(): Boolean {
        return binding.editTxtPasswordRegister.text.toString() == binding.editTxtReenterPasswordRegister.text.toString()
    }
}
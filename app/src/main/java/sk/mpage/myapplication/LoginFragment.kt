package sk.mpage.myapplication

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
import sk.mpage.myapplication.databinding.FragmentLoginBinding

class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.btnLogIn.setOnClickListener(this)
        auth = Firebase.auth
        return binding.root
    }

    override fun onClick(p0: View?) {
        when {
            TextUtils.isEmpty(binding.editTxtEmailLogIn.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(context, "Musíš zadať emailovú adresu", Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(
                binding.editTxtPasswordLogIn.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(context, "Musíš zadať heslo", Toast.LENGTH_LONG).show()
            }

            else -> {
                val email = binding.editTxtEmailLogIn.text.toString().trim { it <= ' ' }
                val password = binding.editTxtPasswordLogIn.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, "Prihlásenie bolo úspešné", Toast.LENGTH_LONG)
                                .show()
                            val user = auth.currentUser
                            findNavController().navigate(R.id.mapFragment)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, task.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }

            }
        }

    }
}
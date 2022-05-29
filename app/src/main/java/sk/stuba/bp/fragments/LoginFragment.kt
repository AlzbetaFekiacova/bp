package sk.stuba.bp.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import sk.stuba.bp.R
import sk.stuba.bp.databinding.FragmentLoginBinding

class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var numberOfLogs = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.btnLogIn.setOnClickListener(this)
        auth = Firebase.auth

        binding.btnForgottenPassword.setOnClickListener{
            val emailAddress = binding.editTxtEmailLogIn.text.toString().trim { it <= ' ' }

            Firebase.auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener {
                    Toast.makeText(context, "Zaslany reset hesla na Váš email.", Toast.LENGTH_SHORT).show()
                    binding.btnForgottenPassword.visibility = View.INVISIBLE
                    binding.btnLogIn.visibility = View.VISIBLE
                }
        }

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
                val credential = EmailAuthProvider.getCredential(email, password)

                //https://firebase.google.com/docs/auth/android/start
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, "Prihlásenie bolo úspešné", Toast.LENGTH_LONG)
                                .show()
                            val user = auth.currentUser
                            auth.currentUser!!.linkWithCredential(credential)
                                .addOnCompleteListener() { task ->
                                    if (task.isSuccessful) {
                                        Log.d("LOGINFRAGMENT", "linkWithCredential:success")
                                        val user = task.result?.user
                                    } else {
                                        Log.w("LOGINFRAGMENT", "linkWithCredential:failure", task.exception)
                                    }
                                }
                            findNavController().navigate(R.id.mapFragment)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(context, task.exception.toString(), Toast.LENGTH_LONG)
                                .show()
                            numberOfLogs++
                            if (numberOfLogs >= 3) {
                                numberOfLogs = 0
                                binding.btnLogIn.visibility = View.INVISIBLE
                                binding.btnForgottenPassword.visibility = View.VISIBLE
                            }
                        }
                    }
            }
        }

    }
}
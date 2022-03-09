package sk.stuba.bp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
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
import sk.stuba.bp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        val email = auth.currentUser?.email
        binding.textViewIdentity.text = "EMAIL: $email"

        binding.buttonLogOut.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Boli ste úspešne odhlásený", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.mapFragment)
        }

        binding.buttonDeleteAccount.setOnClickListener {
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

        /*binding.btnName.setOnClickListener {
            val user = Firebase.auth.currentUser

            if (TextUtils.isEmpty(binding.edtTxtName.text.toString().trim { it <= ' ' })) {
                Toast.makeText(context, "Musíš zadať meno", Toast.LENGTH_LONG).show()
            } else {
                val name = binding.edtTxtName.text.toString()
                val profileUpdates = userProfileChangeRequest {
                    displayName = name

                }
                user!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("TAG", "User profile updated.")
                        }
                    }

                binding.edtTxtName.visibility = View.GONE
                binding.btnName.visibility = View.GONE
                binding.txtName.setText("MENO: $name")
                binding.txtName.visibility = View.VISIBLE
            }
        }*/

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(p0: View?) {

    }
}
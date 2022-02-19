package sk.mpage.myapplication.fragments

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
import sk.mpage.myapplication.R
import sk.mpage.myapplication.databinding.FragmentProfileBinding

class ProfileFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        binding.buttonLogOut.setOnClickListener{
            auth.signOut()
            Toast.makeText(context, "Boli ste úspešne odhlásený", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.mapFragment)
        }

        binding.buttonDeleteAccount.setOnClickListener{
            val user = Firebase.auth.currentUser!!

            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Váš účet bol úspešne odstánený", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.mapFragment)
                    }
                }
        }

        binding.buttonChangePasswd.setOnClickListener{
            //TODO
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onClick(p0: View?) {

    }
}
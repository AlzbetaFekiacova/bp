package sk.mpage.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import sk.mpage.myapplication.databinding.FragmentDialogBinding

class CustomDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        binding.dialogButtonLogIn.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.loginFragment)
        }

        binding.dialogButtonRegister.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.registrationFragment)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
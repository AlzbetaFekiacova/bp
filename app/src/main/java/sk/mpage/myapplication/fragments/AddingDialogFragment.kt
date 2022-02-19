package sk.mpage.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import sk.mpage.myapplication.R
import sk.mpage.myapplication.databinding.FragmentAddBinding

class AddingDialogFragment:DialogFragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        binding.dialogButtonLogIn.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.mapFragment)
        }

        binding.dialogButtonRegister.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.mapFragment)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

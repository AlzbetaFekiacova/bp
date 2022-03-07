package sk.stuba.bp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import sk.stuba.bp.R
import sk.stuba.bp.databinding.FragmentLogOrRegBinding

class LogOrRegDialogFragment : DialogFragment() {

    private var _binding: FragmentLogOrRegBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogOrRegBinding.inflate(inflater, container, false)
        binding.buttonLogIn.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.loginFragment)
        }

        binding.buttonRegister.setOnClickListener {
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
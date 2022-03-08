package sk.stuba.bp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stuba.bp.R
import sk.stuba.bp.SharedViewModel
import sk.stuba.bp.databinding.FragmentPositionConfirmationBinding

class PositionConfirmationFragment : DialogFragment() {

    private lateinit var sharedViewModel: SharedViewModel
    private var _binding: FragmentPositionConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPositionConfirmationBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        binding.buttonYes.setOnClickListener {
            sharedViewModel.clickYes()
            dismiss()
            findNavController().navigate(R.id.mapFragment)
        }

        binding.buttonNo.setOnClickListener {
            sharedViewModel.clickNo()
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

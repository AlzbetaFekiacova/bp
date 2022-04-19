package sk.stuba.bp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
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
            lifecycleScope.launch {
                sharedViewModel.clickYes()
                dismiss()
                findNavController().navigate(R.id.mapFragment)
            }
        }

        binding.buttonNo.setOnClickListener {
            lifecycleScope.launch {
                sharedViewModel.clickNo()
                dismiss()
                findNavController().navigate(R.id.mapFragment)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

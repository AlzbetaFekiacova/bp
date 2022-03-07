package sk.stuba.bp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import sk.stuba.bp.R
import sk.stuba.bp.databinding.FragmentPositionConfirmationBinding

class PositionConfirmationFragment : DialogFragment() {
    private var _binding: FragmentPositionConfirmationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPositionConfirmationBinding.inflate(inflater, container, false)
        binding.buttonYes.setOnClickListener {
            Toast.makeText(context, "Pozicia potvrdena", Toast.LENGTH_SHORT).show()
            dismiss()
            findNavController().navigate(R.id.mapFragment)
        }

        binding.buttonNo.setOnClickListener {
            Toast.makeText(context, "Pozicia zamietnuta", Toast.LENGTH_SHORT).show()
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

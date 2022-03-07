package sk.stuba.bp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import sk.stuba.bp.R
import sk.stuba.bp.databinding.FragmentAddBinding

class AddingDialogFragment(var parameter: Number):DialogFragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        binding.buttonYes.setOnClickListener {
            Toast.makeText(context, "toto je parameter " + parameter, Toast.LENGTH_SHORT).show()
            dismiss()
            findNavController().navigate(R.id.mapFragment)
        }

        binding.buttonNo.setOnClickListener {
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

package sk.stuba.bp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import sk.stuba.bp.MyConstants
import sk.stuba.bp.R
import sk.stuba.bp.SharedViewModel
import sk.stuba.bp.adapters.ItemAdapter
import sk.stuba.bp.databinding.FragmentFilterBinding

//https://guides.codepath.com/android/using-the-recyclerview
class FilterFragment : DialogFragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private var values = arrayListOf(
        "Zberné miesta",
        "Kontajner - Sklo",
        "Kontajner - Plast",
        "Kontajner - Papier",
        "Kontajner - Zmesový odpad",
        "Kontajner - Kovy",
        "Kontajner - Bio odpad",
        "Kôš - Zmesový odpad",
        "Kôš - Plast",
        "Kôš - Papier",
        "Zber - Šatstvo",
        "Uložiť filter"
    )

    private var listOfKeys = arrayListOf(
        MyConstants.BACK_UP,
        MyConstants.CONTAINER_GLASS,
        MyConstants.CONTAINER_PLASTIC,
        MyConstants.CONTAINER_PAPER,
        MyConstants.CONTAINER_COMMUNAL,
        MyConstants.CONTAINER_METAL,
        MyConstants.CONTAINER_BIO,
        MyConstants.BIN_COMMUNAL,
        MyConstants.BIN_PLASTIC,
        MyConstants.BIN_PAPER,
        MyConstants.CLOTHES_COLLECTING
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        binding.recyclerViewItems.layoutManager = LinearLayoutManager(context)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val itemAdapter = ItemAdapter(requireContext(), getItemList(), sharedViewModel.filters)
        binding.recyclerViewItems.adapter = itemAdapter
        itemAdapter.setOnItemClickListener(
            object : ItemAdapter.OnMyItemClickListener {
                override fun onItemClick(position: Int) {
                    if (position == 11) {
                        dismiss()
                        findNavController().navigate(R.id.mapFragment)
                    } else {
                        sharedViewModel.change(listOfKeys[position])
                    }
                }

            }
        )

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getItemList(): ArrayList<String> {
        val list = ArrayList<String>()

        for (i in 0..11) {
            list.add(values[i])
        }
        return list
    }
}
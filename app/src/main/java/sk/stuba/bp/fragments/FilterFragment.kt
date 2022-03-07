package sk.stuba.bp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import sk.stuba.bp.MyConstants
import sk.stuba.bp.adapters.ItemAdapter
import sk.stuba.bp.databinding.FragmentFilterBinding


class FilterFragment(var map: MutableMap<String, Boolean>) : DialogFragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private var values = arrayListOf(
        "Zberné miesta",
        "Kontajner - Sklo",
        "Kontajner - Plast",
        "Kontajner - Papier",
        "Kontajner - Zmesový odpad",
        "Kontajner - Elktro odpad",
        "Kontajner - Bio odpad",
        "Kôš - Zmesový odpad",
        "Kôš - Plast",
        "Kôš - Papier",
        "Zber - Šatstvo"
    )

    private var listOfKeys = arrayListOf(
        MyConstants.BACK_UP,
        MyConstants.CONTAINER_GLASS,
        MyConstants.CONTAINER_PLASTIC,
        MyConstants.CONTAINER_PAPER,
        MyConstants.CONTAINER_COMMUNAL,
        MyConstants.CONTAINER_ELECTRO,
        MyConstants.CONTAINER_BIO,
        MyConstants.BIN_COMMUNAL,
        MyConstants.BIN_PLASTIC,
        MyConstants.BIN_PAPER,
        MyConstants.BIN_COMMUNAL
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        binding.recyclerViewItems.layoutManager = LinearLayoutManager(context)

        val itemAdapter = ItemAdapter(requireContext(), getItemList())
        binding.recyclerViewItems.adapter = itemAdapter
        itemAdapter.setOnItemClickListener(
            object : ItemAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    map[listOfKeys[position]] = false
                    Toast.makeText(
                        context,
                        "Cliked on $position",
                        Toast.LENGTH_SHORT
                    )
                        .show()

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

        for (i in 0..10) {
            list.add(values[i])
        }
        return list
    }
}
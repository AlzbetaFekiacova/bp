package sk.mpage.myapplication.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import sk.mpage.myapplication.adapters.ItemAdapter
import sk.mpage.myapplication.databinding.FragmentFilterBinding

class FilterFragment : DialogFragment() {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    var values = arrayListOf(
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        binding.recyclerViewItems.layoutManager= LinearLayoutManager(context)

        val itemAdapter = ItemAdapter(requireContext(), getItemList())
        binding.recyclerViewItems.adapter = itemAdapter
        itemAdapter.setOnItemClickListener(
            object : ItemAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    Toast.makeText(context, "Cliked on ${position.toString()}", Toast.LENGTH_SHORT).show()

                }

            }
        )

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getItemList():ArrayList<String> {
        val list = ArrayList<String>()

        for (i in 0 .. 10){
            list.add("${values.get(i)}")
        }
        return list
    }
}
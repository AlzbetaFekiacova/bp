package sk.stuba.bp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import sk.stuba.bp.R
import sk.stuba.bp.adapters.ItemAdapter
import sk.stuba.bp.adapters.MyAdapter
import sk.stuba.bp.databinding.FragmentSeparationInfoBinding

class SeparationInfoFragment : Fragment() {
    private var _binding: FragmentSeparationInfoBinding? = null
    private val binding get() = _binding!!

    private var separationTitles = arrayListOf<String>()
    private var separationDescription = arrayListOf<String>()
    private var images = arrayListOf(
        R.drawable.ic_trash_container_blue,
        R.drawable.ic_trash_container_yellow,
        R.drawable.ic_trash_container_green,
        R.drawable.ic_trash_container_brown,
        R.drawable.ic_trash_container_black,
        R.drawable.ic_baseline_checkroom_24,
        R.drawable.ic_cans_or_bottles,
        R.drawable.ic_trash_container_red
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentSeparationInfoBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        separationTitles.addAll(resources.getStringArray(R.array.separation_info))
        separationDescription.addAll(resources.getStringArray(R.array.separation_description))
        val myAdapter = MyAdapter(requireContext(), separationTitles, separationDescription, images)
        binding.recyclerView.adapter = myAdapter
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        inflater.inflate(R.menu.back_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("MENU", "click")
        findNavController().navigate(R.id.mapFragment)
        return super.onOptionsItemSelected(item)
    }
}
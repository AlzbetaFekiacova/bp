package sk.stuba.bp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.bp.R

//https://guides.codepath.com/android/using-the-recyclerview
class MyAdapter(
    val context: Context,
    private val separationTitles: ArrayList<String>,
    private val separationDescription: ArrayList<String>,
    private val images: ArrayList<Int>
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    private lateinit var holder: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        holder = ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_separarion_row, parent, false)
        )
        return holder

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
        holder.titleText.text = separationTitles[position]
        holder.contentText.text = separationDescription[position]
    }

    override fun getItemCount(): Int {
        return separationTitles.size
    }


    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imageView)
        var titleText: TextView = view.findViewById(R.id.txtViewTitle)
        var contentText: TextView = view.findViewById(R.id.txtViewContent)
    }

}
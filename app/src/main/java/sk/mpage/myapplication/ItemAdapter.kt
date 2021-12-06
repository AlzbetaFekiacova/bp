package sk.mpage.myapplication

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(val context: Context, val items: ArrayList<String>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private lateinit var clickListener: onItemClickListener
    private lateinit var holder: ViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        holder = ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_custom_row, parent, false),
            clickListener
        )
        return holder

    }


    fun setOnItemClickListener(listener: onItemClickListener) {
        clickListener = listener

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)

        holder.tvViewItem.text = item

        when (position) {
            0 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_cans_or_bottles)
            }
            1 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_green)
            }
            2 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_yellow)
            }
            3 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_blue)
            }
            4 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_black)
            }
            5 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_red)
            }
            6 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_brown)
            }
            7 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_can_black)
            }
            8 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_bin_yellow)
            }
            9 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_bin_blue)
            }
            10 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_baseline_checkroom_24)
            }

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(view: View, listener: onItemClickListener) : RecyclerView.ViewHolder(view) {
        val tvViewItem: TextView = view.findViewById(R.id.txtViewFilter)
        val imageIcon: ImageView = view.findViewById(R.id.image_icon)
        val cardViewItem: CardView = view.findViewById(R.id.card_view_item)

        init {
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

    }

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }
}
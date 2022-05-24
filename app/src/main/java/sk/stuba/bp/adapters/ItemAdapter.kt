package sk.stuba.bp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import sk.stuba.bp.R

//https://guides.codepath.com/android/using-the-recyclerview
class ItemAdapter(
    val context: Context,
    private val items: ArrayList<String>,
    all: MutableMap<String, Boolean>
) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private lateinit var clickListener: OnMyItemClickListener
    private lateinit var holder: ViewHolder
    private var boolValues = all.values.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        holder = ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_custom_row, parent, false),
            clickListener
        )
        return holder

    }


    fun setOnItemClickListener(listener: OnMyItemClickListener) {
        clickListener = listener

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvViewItem.text = item

        when (position) {
            0 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_cans_or_bottles)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            1 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_green)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            2 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_yellow)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            3 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_blue)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            4 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_black)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            5 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_red)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            6 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_brown)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            7 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_can_black)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            8 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_bin_yellow)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            9 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_bin_blue)
                if (boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                } else {
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }
            10 -> {
                holder.imageIcon.setBackgroundResource(R.drawable.ic_baseline_checkroom_24)
                if(boolValues[position]) {
                    holder.cardViewItem.setCardBackgroundColor(Color.GRAY)
                }
                else{
                    holder.cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
            }

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(view: View, listener: OnMyItemClickListener) : RecyclerView.ViewHolder(view) {
        val tvViewItem: TextView = view.findViewById(R.id.txtViewFilter)
        val imageIcon: ImageView = view.findViewById(R.id.image_icon)
        val cardViewItem: CardView = view.findViewById(R.id.card_view_item)

        init {
            view.setOnClickListener {
                if(cardViewItem.cardBackgroundColor.defaultColor == Color.GRAY){
                    cardViewItem.setCardBackgroundColor(Color.WHITE)
                }
                else{
                    cardViewItem.setCardBackgroundColor(Color.GRAY)
                }

                listener.onItemClick(adapterPosition)
            }
        }

    }

    interface OnMyItemClickListener {
        fun onItemClick(position: Int)
    }
}
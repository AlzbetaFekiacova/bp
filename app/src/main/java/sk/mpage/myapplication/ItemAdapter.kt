package sk.mpage.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(val context: Context, val items: ArrayList<String>):
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_custom_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)

        holder.tvViewItem.text = item

        if(position % 2 == 0){
            holder.cardViewItem.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.light_green
                )
            )
        }
        when(position){
            0->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_cans_or_bottles)
            }
            1->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_green)
            }
            2->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_yellow)
            }
            3->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_blue)
            }
            4->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_black)
            }
            5->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_red)
            }
            6->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_container_brown)
            }
            7->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_can_black)
            }
            8->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_bin_yellow)
            }
            9->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_trash_bin_blue)
            }
            10->{
                holder.imageIcon.setBackgroundResource(R.drawable.ic_baseline_checkroom_24)
            }

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(view: View) :RecyclerView.ViewHolder(view){
        val tvViewItem : TextView = view.findViewById(R.id.txtViewFilter)
        val imageIcon : ImageView = view.findViewById(R.id.image_icon)
        val cardViewItem : CardView = view.findViewById(R.id.card_view_item)
    }
}
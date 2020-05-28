package com.example.bricklist


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.bricklist.R.id
import com.example.bricklist.R.layout


class InventoriesAdapter(var mInventories: ArrayList<Inventory>, val clickListener: (Inventory) -> Unit, val context: Context, archived:Boolean) : RecyclerView.Adapter<InventoriesAdapter.ViewHolder>() {

    val mArchived = archived
    inner class ViewHolder(listItemView: View, context: Context, adapter:InventoriesAdapter) : RecyclerView.ViewHolder(listItemView) {
        public val nameTextView = itemView.findViewById<TextView>(id.inventory_name)
        public val archiveButton = itemView.findViewById<ToggleButton>(id.archiveButton)
        val mAdapter = adapter

        fun bind(inventory: Inventory,clickListener: (Inventory) -> Unit)
        {

            nameTextView.setText(inventory.name)
            if(inventory.active == 1) {
                archiveButton.setChecked(true)
            }else{
                archiveButton.setChecked(false)
            }
            itemView.setOnClickListener {
                clickListener(inventory)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val inventoryView: View = inflater.inflate(layout.inventory_row, parent, false)
        return ViewHolder(inventoryView, parent.context,this)
    }

    override fun getItemCount(): Int {
        return mInventories.size
    }


    override fun onBindViewHolder(holder: InventoriesAdapter.ViewHolder, position: Int) {
        val inventory: Inventory = mInventories.get(position)

        holder.archiveButton.setOnClickListener{
            inventory.active = - inventory.active
            val helper = MyDBHandler(context,null,null, 1)
            helper.updateInventory(inventory)
            holder.bind(mInventories.get(position), clickListener)

            if(mArchived){
                mInventories = helper.getAllInventories()
            }else{
                mInventories = helper.getNotArchivedInventories()
            }
            notifyDataSetChanged()
        }
        holder.bind(mInventories.get(position), clickListener)
    }
}
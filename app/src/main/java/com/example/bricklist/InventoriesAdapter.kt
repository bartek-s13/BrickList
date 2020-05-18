package com.example.bricklist


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bricklist.R.id
import com.example.bricklist.R.layout


class InventoriesAdapter(private val mInventories: List<Inventory>) : RecyclerView.Adapter<InventoriesAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        public val nameTextView = itemView.findViewById<TextView>(id.inventory_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)
        val inventoryView: View = inflater.inflate(layout.inventory_row, parent, false)

        return ViewHolder(inventoryView)
    }

    override fun getItemCount(): Int {
        return mInventories.size
    }

    override fun onBindViewHolder(holder: InventoriesAdapter.ViewHolder, position: Int) {
        val inventory: Inventory = mInventories.get(position)
        val textView: TextView = holder.nameTextView
        textView.setText(inventory.name)

    }
}
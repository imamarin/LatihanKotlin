package com.example.latihankotlin


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Locale


class ItemAdapter(private val context: Context, private val dataSource: MutableList<Item>) : BaseAdapter(), Filterable{

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val loadImage = MainScope();
    private var filterData: MutableList<Item> = dataSource


    override fun getCount(): Int {
        return filterData.size
    }

    override fun getItem(position: Int): Any {
        return filterData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.item_card_view, parent, false)

        val textView = rowView.findViewById<TextView>(R.id.textViewItem)
        val imageView = rowView.findViewById<ImageView>(R.id.imageView)

        val item = getItem(position) as Item
        textView.text = item.name

        loadImage.launch{
            val bitmap = LoadImageFromUrl(item.imageUrl)
            imageView.setImageBitmap(bitmap)
        }

        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filtering = mutableListOf<Item>()
                if (constraint.isNullOrBlank()) {
                    filtering.addAll(dataSource)
                } else {
                    val filterPattern = constraint.toString().toLowerCase().trim()
                    for (item in dataSource) {
                        if (item.name.toLowerCase(Locale.getDefault()).contains(filterPattern)) {
                            filtering.add(Item(item.name, item.imageUrl))
                        }
                    }
                }

                val results = FilterResults()
                results.values = filtering
                return results
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filterData = p1?.values as ArrayList<Item>
                notifyDataSetChanged()
            }

        }
    }
}
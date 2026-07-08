package com.example.luciano.chanchuno

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes

class PartidaBaseAdapter internal constructor(
    private val item: Array<Array<String?>>,
    private val context: Context
) : BaseAdapter() {
    init {
        view = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return item.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val vista: View
        val holder: ViewHolder

        if (convertView == null) {
            vista = view!!.inflate(R.layout.cv_partida, parent, false)
            holder = ViewHolder(
                imageView = vista.findViewById(R.id.fotocarnet),
                nombrejugador = vista.findViewById(R.id.tvNombreJugadorPartida),
                chancho = vista.findViewById(R.id.tvchancho)
            )
            vista.tag = holder
        } else {
            vista = convertView
            holder = vista.tag as ViewHolder
        }

        holder.nombrejugador.text = item[position][0]
        holder.chancho.text = item[position][1]
        holder.imageView.setImageResource(drawableFor(item[position][2]))

        return vista
    }

    @DrawableRes
    private fun drawableFor(nombre: String?): Int {
        return when (nombre) {
            "pig1" -> R.drawable.pig1
            "pig3" -> R.drawable.pig3
            "pig5" -> R.drawable.pig5
            "pig6" -> R.drawable.pig6
            "pig18" -> R.drawable.pig18
            else -> R.drawable.pig1
        }
    }

    private class ViewHolder(
        val imageView: ImageView,
        val nombrejugador: TextView,
        val chancho: TextView
    )

    companion object {
        private var view: LayoutInflater? = null
    }
}

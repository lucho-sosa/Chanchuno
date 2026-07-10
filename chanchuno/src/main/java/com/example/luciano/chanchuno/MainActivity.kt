package com.example.luciano.chanchuno

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.widget.FrameLayout
import androidx.core.net.toUri
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : AppCompatActivity() {
    private var etNombre: EditText? = null
    private var btnagregar: Button? = null
    private var btncomenzar: FloatingActionButton? = null
    private var adapter: jugadorAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var lista: RecyclerView? = null
    private var toolbar: Toolbar? = null
    private var adView: AdView? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var jugadors = ArrayList<String>()

    companion object {
        private const val PREFS_NAME = "chanchuno_prefs"
        private const val KEY_ULTIMOS_JUGADORES = "ultimos_jugadores"
        private const val SEPARADOR_JUGADORES = "\n"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_general, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.menuItem02 -> {
                firebaseAnalytics?.logEvent("reglas_consultadas", null)
                val intent = Intent(this, comoJugar::class.java)
                startActivity(intent)
                true
            }

            R.id.menuItem03 -> {
                val builder = AlertDialog.Builder(this)
                val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val v = layoutInflater.inflate(R.layout.quienessomos, null)
                builder.setView(v)
                val bt = v.findViewById<View>(R.id.twitterLogo) as ImageView
                val bf = v.findViewById<View>(R.id.facebookLogo) as ImageView
                val a = builder.create()
                a.setCancelable(true)
                a.show()
                bt.setOnClickListener {
                    val uri = "https://twitter.com/Think_In_Code".toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                bf.setOnClickListener {
                    val uri = "https://www.facebook.com/thinkincode".toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                super.onOptionsItemSelected(item)
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        jugadors = cargarUltimosJugadores()
        lista = findViewById<View>(R.id.contenedor) as RecyclerView
        //TODO: revisar mejor como funciona el setHasFixedSize
        lista!!.setHasFixedSize(false)
        layoutManager = LinearLayoutManager(this)
        lista!!.layoutManager = layoutManager
        adapter = jugadorAdapter(jugadors, this)
        lista!!.adapter = adapter
        etNombre = findViewById<View>(R.id.etNombreJugador) as EditText
        btnagregar = findViewById<View>(R.id.btnAgregar) as Button
        btncomenzar = findViewById<View>(R.id.floatingActionButton2) as FloatingActionButton
        val adContainer = findViewById<FrameLayout>(R.id.adContainerMain)
        adView = AdView(this).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = BuildConfig.BANNER_AD_UNIT_ID
        }
        adContainer.addView(adView)
        adView?.loadAd(AdRequest.Builder().build())
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    override fun onResume() {
        super.onResume()
        adView?.resume()
    }

    private fun cargarUltimosJugadores(): ArrayList<String> {
        val guardados = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString(KEY_ULTIMOS_JUGADORES, null)
        if (guardados.isNullOrEmpty()) {
            return ArrayList()
        }
        return ArrayList(guardados.split(SEPARADOR_JUGADORES))
    }

    private fun guardarUltimosJugadores() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_ULTIMOS_JUGADORES, jugadors.joinToString(SEPARADOR_JUGADORES))
            .apply()
    }

    override fun onPause() {
        adView?.pause()
        super.onPause()
    }

    override fun onDestroy() {
        adView?.destroy()
        super.onDestroy()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun agregar(view: View?) {
        val playerNameTrimmed = etNombre!!.text.toString().trim()
        if (playerNameTrimmed.isEmpty()) {
            Toast.makeText(this, "Ingres algun nombre de jugador", Toast.LENGTH_SHORT).show()
        } else {
            var playerName = playerNameTrimmed
            playerName = playerName[0].uppercaseChar().toString() + playerName.substring(1, playerName.length)
            if (jugadors.size == 12) {
                firebaseAnalytics?.logEvent("limite_maximo_alcanzado", null)
                Toast.makeText(this, "Maximo 12 jugadores", Toast.LENGTH_SHORT).show()
            } else {
                if (!jugadors.contains(playerName)) {
                    jugadors.add(playerName)
                    etNombre!!.setText("")
                    adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this,
                        etNombre!!.text.toString() + " ya existe en la partida",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun iniciarPartida(view: View?) {
        if (jugadors.size < 2) {
            val dialog = AlertDialog.Builder(this)
            if (jugadors.size == 0) {
                dialog.setTitle("Vos tenes problemitas")
                dialog.setMessage("Ingresa al menos dos jugadores")
            }
            if (jugadors.size == 1) {
                dialog.setTitle("Ah pero sos loco")
                dialog.setMessage("¿Cómo vas a jugar solo?")
            }
            dialog.setCancelable(true)
            dialog.show()
        } else {
            firebaseAnalytics?.logEvent("partida_iniciada", Bundle().apply {
                putLong("cantidad_jugadores", jugadors.size.toLong())
            })
            guardarUltimosJugadores()
            val intent = Intent(this, partida::class.java)
            intent.putExtra("jugadores", jugadors)
            startActivity(intent)
        }
    }
}

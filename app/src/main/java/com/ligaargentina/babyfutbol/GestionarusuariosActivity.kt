package com.ligaargentina.babyfutbol

import UsuarioAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cardozo.babyfutbol.AdminActivity
import org.json.JSONArray

class GestionarusuariosActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var usuarioList: MutableList<Usuario>
    private lateinit var adapter: UsuarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionarusuarios)

        listView = findViewById(R.id.listViewUsuarios)
        usuarioList = mutableListOf()
        adapter = UsuarioAdapter(this, usuarioList)
        listView.adapter = adapter

        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        val url = "http://10.0.2.2/api/usuarios.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val usuarioJson = jsonArray.getJSONObject(i)
                        val id = usuarioJson.getInt("id")
                        val nombre = usuarioJson.getString("nombre")
                        val apellido = usuarioJson.getString("apellido")
                        val email = usuarioJson.getString("email")
                        val edad = usuarioJson.getInt("edad")


                        val usuario = Usuario(id, nombre, apellido, email, edad)
                        usuarioList.add(usuario)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(stringRequest)
    }

    fun gestVolver(view: View) {
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
    }

}

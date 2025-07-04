package com.ligaargentina.babyfutbol

import ArbitroAdapter
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

class GestionararbitrosActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var arbitroList: MutableList<Arbitro>
    private lateinit var adapter: ArbitroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestionararbitros)

        listView = findViewById(R.id.listViewArbitros)
        arbitroList = mutableListOf()
        adapter = ArbitroAdapter(this, arbitroList)
        listView.adapter = adapter

        cargarArbitros()
    }

    private fun cargarArbitros() {
        val url = "http://10.0.2.2/api/arbitros.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                if (response.trim().startsWith("[")) {
                    val jsonArray = JSONArray(response)
                    for (i in 0 until jsonArray.length()) {
                        val arbitroJson = jsonArray.getJSONObject(i)
                        val id = arbitroJson.getInt("id")
                        val nombre = arbitroJson.getString("nombre")
                        val apellido = arbitroJson.getString("apellido")
                        val email = arbitroJson.getString("email")
                        val edad = arbitroJson.getInt("edad")
                        val experiencia = arbitroJson.getInt("experiencia")

                        val arbitro = Arbitro(id, nombre, apellido, email,
                            edad, experiencia, 5)
                        arbitroList.add(arbitro)
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    val jsonObject = org.json.JSONObject(response)
                    val message = jsonObject.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
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

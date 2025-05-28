package com.ligaargentina.babyfutbol

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class EditarUsuarioActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextEdad: EditText
    private lateinit var spinnerRoles: Spinner
    private lateinit var buttonGuardar: Button

    private var usuarioId: Int = 0
    private var rolActualId: Int = 0
    private var rolSeleccionadoId: Int = 0

    private val rolesList = mutableListOf<Rol>()

    data class Rol(val id: Int, val nombre: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_editar_usuario)

        // Referencias
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextApellido = findViewById(R.id.editTextApellido)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextEdad = findViewById(R.id.editTextEdad)
        spinnerRoles = findViewById(R.id.spinnerRoles)

        // Obtener datos del intent
        usuarioId = intent.getIntExtra("id", 0)
        rolActualId = intent.getIntExtra("rolId", 0)
        editTextNombre.setText(intent.getStringExtra("nombre"))
        editTextApellido.setText(intent.getStringExtra("apellido"))
        editTextEmail.setText(intent.getStringExtra("email"))
        editTextEdad.setText(intent.getIntExtra("edad", 0).toString())

        cargarRoles()

        buttonGuardar.setOnClickListener {
            actualizarUsuario()
        }
    }

    private fun cargarRoles() {
        val url = "http://10.0.2.2/api/roles.php"
        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                val jsonArray = JSONArray(response)
                rolesList.clear()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.getInt("roles_id")
                    val nombre = obj.getString("nombre")
                    rolesList.add(Rol(id, nombre))
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rolesList.map { it.nombre })
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerRoles.adapter = adapter

                // Seleccionar el rol actual
                val posicion = rolesList.indexOfFirst { it.id == rolActualId }
                if (posicion >= 0) {
                    spinnerRoles.setSelection(posicion)
                }

                spinnerRoles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        rolSeleccionadoId = rolesList[position].id
                        Log.d("ROL_SELECT", "ID seleccionado: $rolSeleccionadoId")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            })

        queue.add(request)
    }



    private fun actualizarUsuario() {
        val url = "http://10.0.2.2/api/actualizar_usuario.php"
        val queue = Volley.newRequestQueue(this)

        val nombre = editTextNombre.text.toString()
        val apellido = editTextApellido.text.toString()
        val email = editTextEmail.text.toString()
        val edad = editTextEdad.text.toString().toIntOrNull() ?: 0

        val request = object : StringRequest(Method.POST, url,
            { response ->
                Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                finish()
            },
            { error ->
                Toast.makeText(this, "Error al actualizar: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "id" to usuarioId.toString(),
                    "nombre" to nombre,
                    "apellido" to apellido,
                    "email" to email,
                    "edad" to edad.toString(),
                    "rol_id" to rolSeleccionadoId.toString()
                )
            }
        }

        queue.add(request)
    }
}

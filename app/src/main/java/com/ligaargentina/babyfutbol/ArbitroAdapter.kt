import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ligaargentina.babyfutbol.Arbitro
import com.ligaargentina.babyfutbol.R
import com.ligaargentina.babyfutbol.Rol
import org.json.JSONObject

class ArbitroAdapter(
    private val context: Context,
    private val arbitro: MutableList<Arbitro>
) : ArrayAdapter<Arbitro>(context, 0, arbitro) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
        val arbitro = arbitro[position]

        val textViewNombre = view.findViewById<TextView>(R.id.textViewNombre)
        val textViewEmail = view.findViewById<TextView>(R.id.textViewEmail)
        val btnEditar = view.findViewById<Button>(R.id.btnEditar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        textViewNombre.text = arbitro.nombre
        textViewEmail.text = arbitro.email

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("¿Eliminar árbitro?")
                .setMessage("¿Estás seguro de eliminar a ${arbitro.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    eliminarArbitro(arbitro.id, position)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnEditar.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_arbitro, null)
            val editTextNombre = dialogView.findViewById<EditText>(R.id.editTextNombre)
            val editTextApellido = dialogView.findViewById<EditText>(R.id.editTextApellido)
            val editTextEmail = dialogView.findViewById<EditText>(R.id.editTextEmail)
            val editTextEdad = dialogView.findViewById<EditText>(R.id.editTextEdad)
            val spinnerRoles = dialogView.findViewById<Spinner>(R.id.spinnerRoles)

            // Prellenar campos
            editTextNombre.setText(arbitro.nombre)
            editTextApellido.setText(arbitro.apellido)
            editTextEmail.setText(arbitro.email)
            editTextEdad.setText(arbitro.edad.toString())

            val rolesList = mutableListOf<Rol>()
            val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mutableListOf())
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRoles.adapter = adapter

            val url = "http://10.0.2.2/api/roles.php"
            val queue = Volley.newRequestQueue(context)
            val request = StringRequest(
                com.android.volley.Request.Method.GET, url,
                { response ->
                    try {
                        val jsonArray = org.json.JSONArray(response)
                        val roleNames = mutableListOf<String>()
                        for (i in 0 until jsonArray.length()) {
                            val rolJson = jsonArray.getJSONObject(i)
                            val rol = Rol(
                                roles_id = rolJson.getInt("roles_id"),
                                nombre = rolJson.getString("nombre")
                            )
                            rolesList.add(rol)
                            roleNames.add(rol.nombre)
                        }
                        adapter.clear()
                        adapter.addAll(roleNames)
                        adapter.notifyDataSetChanged()

                        val rolActualIndex = rolesList.indexOfFirst { it.roles_id == arbitro.roles_id }
                        if (rolActualIndex >= 0) spinnerRoles.setSelection(rolActualIndex)

                        AlertDialog.Builder(context)
                            .setTitle("Editar árbitro")
                            .setView(dialogView)
                            .setPositiveButton("Guardar") { _, _ ->
                                val nuevoNombre = editTextNombre.text.toString()
                                val nuevoApellido = editTextApellido.text.toString()
                                val nuevoEmail = editTextEmail.text.toString()
                                val nuevaEdad = editTextEdad.text.toString()
                                val rolSeleccionado = rolesList[spinnerRoles.selectedItemPosition].roles_id

                                editarArbitro(
                                    arbitro.id,
                                    nuevoNombre,
                                    nuevoApellido,
                                    nuevoEmail,
                                    nuevaEdad.toInt(),
                                    rolSeleccionado,
                                    position
                                )
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al parsear roles", Toast.LENGTH_SHORT).show()
                    }
                },
                {
                    Toast.makeText(context, "Error al cargar roles", Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(request)
        }

        return view
    }

    private fun editarArbitro(id: Int, nuevoNombre: String, nuevoApellido: String, nuevoEmail: String, nuevaEdad: Int, nuevoRolId: Int, position: Int) {
        val url = "http://10.0.2.2/api/arbitros.php"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                Toast.makeText(context, "Árbitro actualizado", Toast.LENGTH_SHORT).show()
                arbitro[position].nombre = nuevoNombre
                arbitro[position].apellido = nuevoApellido
                arbitro[position].email = nuevoEmail
                arbitro[position].edad = nuevaEdad
                arbitro[position].roles_id = nuevoRolId
                notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(context, "Error al actualizar: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = id.toString()
                params["nombre"] = nuevoNombre
                params["apellido"] = nuevoApellido
                params["email"] = nuevoEmail
                params["edad"] = nuevaEdad.toString()
                params["rol_id"] = nuevoRolId.toString()
                params["_method"] = "PUT"
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun eliminarArbitro(id: Int, position: Int) {
        val url = "http://10.0.2.2/api/usuarios.php"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Method.POST, url,
            { response ->
                Toast.makeText(context, "Árbitro eliminado", Toast.LENGTH_SHORT).show()
                arbitro.removeAt(position)
                notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(context, "Error al eliminar: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = id.toString()
                params["_method"] = "DELETE"
                return params
            }
        }

        requestQueue.add(stringRequest)
    }




}

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ligaargentina.babyfutbol.R
import com.ligaargentina.babyfutbol.Rol

class UsuarioAdapter(
    private val context: Context,
    private val usuarios: MutableList<Usuario>,
    private val onUsuarioActualizado: ((Usuario) -> Unit)? = null,
    private val onUsuarioEliminado: ((Int) -> Unit)? = null
) : ArrayAdapter<Usuario>(context, 0, usuarios) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_usuario, parent, false)
        val usuario = usuarios[position]

        val textViewNombre = view.findViewById<TextView>(R.id.textViewNombre)
        val textViewEmail = view.findViewById<TextView>(R.id.textViewEmail)
        val btnEditar = view.findViewById<Button>(R.id.btnEditar)
        val btnEliminar = view.findViewById<Button>(R.id.btnEliminar)

        textViewNombre.text = usuario.nombre
        textViewEmail.text = usuario.email

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("¿Eliminar usuario?")
                .setMessage("¿Estás seguro de eliminar a ${usuario.nombre}?")
                .setPositiveButton("Sí") { _, _ -> eliminarUsuario(usuario.id, position) }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnEditar.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_usuario, null)
            val editTextNombre = dialogView.findViewById<EditText>(R.id.editTextNombre)
            val editTextApellido = dialogView.findViewById<EditText>(R.id.editTextApellido)
            val editTextEmail = dialogView.findViewById<EditText>(R.id.editTextEmail)
            val editTextEdad = dialogView.findViewById<EditText>(R.id.editTextEdad)
            val spinnerRoles = dialogView.findViewById<Spinner>(R.id.spinnerRoles)
            val buttonGuardar = dialogView.findViewById<Button>(R.id.buttonGuardar)
            val buttonCancelar = dialogView.findViewById<Button>(R.id.buttonCancelar)

            editTextNombre.setText(usuario.nombre)
            editTextApellido.setText(usuario.apellido)
            editTextEmail.setText(usuario.email)
            editTextEdad.setText(usuario.edad.toString())

            val rolesList = mutableListOf<Rol>()
            val adapterSpinner = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, mutableListOf())
            adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerRoles.adapter = adapterSpinner

            val urlRoles = "http://10.0.2.2/api/roles.php"
            val queue = Volley.newRequestQueue(context)
            val request = StringRequest(
                com.android.volley.Request.Method.GET, urlRoles,
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
                        adapterSpinner.clear()
                        adapterSpinner.addAll(roleNames)
                        adapterSpinner.notifyDataSetChanged()

                        val rolActualIndex = rolesList.indexOfFirst { it.roles_id == usuario.roles_id }
                        if (rolActualIndex >= 0) spinnerRoles.setSelection(rolActualIndex)

                        val alertDialog = AlertDialog.Builder(context)
                            .setTitle("Editar usuario")
                            .setView(dialogView)
                            .create()

                        buttonGuardar.setOnClickListener {
                            val nuevoNombre = editTextNombre.text.toString()
                            val nuevoApellido = editTextApellido.text.toString()
                            val nuevoEmail = editTextEmail.text.toString()
                            val nuevaEdadStr = editTextEdad.text.toString()
                            val rolSeleccionado = rolesList[spinnerRoles.selectedItemPosition].roles_id

                            val nuevaEdad = nuevaEdadStr.toIntOrNull() ?: usuario.edad

                            editarUsuario(
                                usuario.id,
                                nuevoNombre,
                                nuevoApellido,
                                nuevoEmail,
                                nuevaEdad,
                                rolSeleccionado,
                                position
                            )
                            alertDialog.dismiss()
                        }

                        buttonCancelar.setOnClickListener {
                            alertDialog.dismiss()
                        }

                        alertDialog.show()

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

    private fun editarUsuario(
        id: Int,
        nuevoNombre: String,
        nuevoApellido: String,
        nuevoEmail: String,
        nuevaEdad: Int,
        nuevoRolId: Int,
        position: Int
    ) {
        val url = "http://10.0.2.2/api/usuarios.php"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                Toast.makeText(context, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                usuarios[position].nombre = nuevoNombre
                usuarios[position].apellido = nuevoApellido
                usuarios[position].email = nuevoEmail
                usuarios[position].edad = nuevaEdad
                usuarios[position].roles_id = nuevoRolId
                notifyDataSetChanged()
                onUsuarioActualizado?.invoke(usuarios[position])
            },
            { error ->
                Toast.makeText(context, "Error al actualizar: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = hashMapOf(
                    "id" to id.toString(),
                    "nombre" to nuevoNombre,
                    "apellido" to nuevoApellido,
                    "email" to nuevoEmail,
                    "edad" to nuevaEdad.toString(),
                    "roles_id" to nuevoRolId.toString(),
                    "_method" to "PUT"
                )
                if (nuevoRolId == 5) {
                    params["hacer_arbitro"] = "1"
                }
                Log.d("Params PUT", params.toString())
                return params
            }
        }

        requestQueue.add(stringRequest)
    }



    private fun eliminarUsuario(id: Int, position: Int) {
        val url = "http://10.0.2.2/api/usuarios.php"
        val requestQueue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(Method.POST, url,
            {
                Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                usuarios.removeAt(position)
                notifyDataSetChanged()
                onUsuarioEliminado?.invoke(id)
            },
            { error ->
                Toast.makeText(context, "Error al eliminar: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return hashMapOf(
                    "id" to id.toString(),
                    "_method" to "DELETE"
                )
            }
        }

        requestQueue.add(stringRequest)
    }
}

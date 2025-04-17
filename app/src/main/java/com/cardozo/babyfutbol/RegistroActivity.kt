package com.ligaargentina.babyfutbol

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class RegistroActivity : AppCompatActivity() {

    private lateinit var passwordEditText: EditText
    private lateinit var togglePassword: ImageView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { _, insets -> insets }

        var passwordVisible = false

        val passwordEditText = findViewById<EditText>(R.id.passwordText)
        val togglePassword = findViewById<ImageView>(R.id.togglePassword)

        togglePassword.setOnClickListener {
            passwordVisible = !passwordVisible

            if (passwordVisible) {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.visibleon) // ← Cambia al ojo abierto
            } else {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.visibleoff) // ← Cambia al ojo cerrado
            }

            passwordEditText.setSelection(passwordEditText.text.length)
        }

    }

    fun registerVolver(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun registrarUsuario(nombre: String, apellido: String, email: String, password: String, edad: Int) {
        val url = "http://10.0.2.2/api/usuarios.php"

        val params = HashMap<String, String>()
        params["nombre"] = nombre
        params["apellido"] = apellido
        params["email"] = email
        params["password"] = password
        params["edad"] = edad.toString()

        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
            },
            Response.ErrorListener { error ->
                val networkResponse: NetworkResponse? = error.networkResponse
                if (networkResponse != null) {
                    if (networkResponse.statusCode == 409) {
                        // Error de email ya registrado
                        val errorData = String(networkResponse.data)
                        try {
                            val jsonObject = JSONObject(errorData)
                            val errorMsg = jsonObject.getString("message")
                            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Toast.makeText(this, "Email ya registrado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Otro tipo de error
                        val errorData = String(networkResponse.data)
                        try {
                            val jsonObject = JSONObject(errorData)
                            val errorMsg = jsonObject.getString("message")
                            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                        } catch (e: JSONException) {
                            Toast.makeText(this, "Error desconocido", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Si no hay conexion muestra un error
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }) {
            override fun getParams(): MutableMap<String, String> = params
        }

        Volley.newRequestQueue(this).add(request)
    }

    fun registerAc(view: View) {
        val nombre = findViewById<EditText>(R.id.nombreText).text.toString().trim()
        val apellido = findViewById<EditText>(R.id.apellidoText).text.toString().trim()
        val email = findViewById<EditText>(R.id.emailText).text.toString().trim()
        val password = findViewById<EditText>(R.id.passwordText).text.toString().trim()
        val edad = findViewById<EditText>(R.id.edadText).text.toString().trim()

        // Validaciones
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() || edad.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        if (!password.matches(Regex(".*[a-z].*")) || !password.matches(Regex(".*[A-Z].*"))) {
            Toast.makeText(this, "La contraseña debe tener al menos una mayúscula y una minúscula", Toast.LENGTH_SHORT).show()
            return
        }

        if (!password.matches(Regex(".*[^a-zA-Z0-9].*"))) {
            Toast.makeText(this, "La contraseña debe tener al menos un carácter especial", Toast.LENGTH_SHORT).show()
            return
        }

        val edadInt = edad.toIntOrNull()
        if (edadInt == null || edadInt !in 5..100) {
            Toast.makeText(this, "Edad no válida (entre 5 y 100)", Toast.LENGTH_SHORT).show()
            return
        }
        registrarUsuario(nombre, apellido, email, password, edadInt!!)
    }
}
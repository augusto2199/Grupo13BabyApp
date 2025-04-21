package com.ligaargentina.babyfutbol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.cardozo.babyfutbol.AdminActivity
import com.cardozo.babyfutbol.ArbitroActivity
import com.cardozo.babyfutbol.EntrenadorActivity
import com.cardozo.babyfutbol.JugadorActivity
import com.cardozo.babyfutbol.UsuarioActivity

import com.ligaargentina.babyfutbol.R
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun registerAc(view: View) {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    fun RecuperoContrasenia(view: View) {
        val intent = Intent(this, RecuperoActivity::class.java)
        startActivity(intent)
    }

    fun login(view: View) {
        val userEdit = findViewById<EditText>(R.id.userEdit)
        val passEdit = findViewById<EditText>(R.id.passEdit)

        val email = userEdit.text.toString().trim()
        val password = passEdit.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Log para ver el contenido antes de enviar
        Log.d("LOGIN_REQUEST", "Email: $email, Password: $password")

        val url = "http://10.0.2.2/api/login.php"  // Cambiar IP si es necesario

        val requestQueue = Volley.newRequestQueue(this)

        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("password", password)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                // Imprimir la respuesta para ver qué estás recibiendo
                Log.d("LOGIN_RESPONSE", response.toString())

                val status = response.optString("status")
                if (status == "success") {
                    val usuario = response.getJSONObject("usuario")
                    val rol = usuario.optString("rol")

                    Toast.makeText(this, "Bienvenido, $rol", Toast.LENGTH_SHORT).show()

                    when (rol.lowercase()) {
                        "admin" -> startActivity(Intent(this, AdminActivity::class.java))
                        "árbitro" -> startActivity(Intent(this, ArbitroActivity::class.java))
                        "entrenador" -> startActivity(Intent(this, EntrenadorActivity::class.java))
                        "jugador" -> startActivity(Intent(this, JugadorActivity::class.java))
                        "usuario" -> startActivity(Intent(this, UsuarioActivity::class.java))
                        else -> Toast.makeText(this, "Rol desconocido: $rol", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Mostrar el error de Volley en caso de que haya un problema de red
                Log.e("LOGIN_ERROR", "Error: ${error.message}")
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )


        requestQueue.add(request)
    }




}

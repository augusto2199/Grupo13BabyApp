package com.cardozo.babyfutbol

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ligaargentina.babyfutbol.GestionarusuariosActivity
import com.ligaargentina.babyfutbol.MainActivity
import com.ligaargentina.babyfutbol.R

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val nombreUsuarioNavbar = findViewById<TextView>(R.id.tituloNavbar)
        val iconCerrarSesion = findViewById<ImageView>(R.id.iconCerrarSesion)

        // Obtener el nombre del usuario desde SharedPreferences
        val sharedPref = getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPref.getString("nombre", null)

        // Log para verificar si se obtiene el nombre correctamente
        Log.d("AdminActivity", "Nombre obtenido desde SharedPreferences: $nombreUsuario")

        // Mostrar nombre en barra superior o Admin por defecto
        if (nombreUsuario != null && nombreUsuario.isNotBlank()) {
            nombreUsuarioNavbar.text = "Bienvenido, $nombreUsuario"
        } else {
            nombreUsuarioNavbar.text = "Bienvenido, Admin"
        }

        // Acción para cerrar sesión
        iconCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    fun Gestion(view: View) {
        val intent = Intent(this, GestionarusuariosActivity::class.java)
        startActivity(intent)
    }

    private fun cerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que querés cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                val sharedPref = getSharedPreferences("usuario", Context.MODE_PRIVATE)
                sharedPref.edit().clear().apply()

                Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

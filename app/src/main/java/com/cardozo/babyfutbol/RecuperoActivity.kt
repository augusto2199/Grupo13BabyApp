package com.ligaargentina.babyfutbol

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.ligaargentina.babyfutbol.R

class RecuperoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recupero)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.volverRecupero)) { _, insets -> insets }

    }
    fun recuperoVolver(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}






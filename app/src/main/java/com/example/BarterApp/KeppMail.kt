package com.example.BarterApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class keppMail : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var EMAIL = intent.getStringExtra("Email")
    }


}



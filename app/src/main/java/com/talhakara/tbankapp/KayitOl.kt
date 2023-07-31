package com.talhakara.tbankapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.talhakara.tbankapp.databinding.ActivityKayitOlBinding



class KayitOl : AppCompatActivity() {

    lateinit var binding: ActivityKayitOlBinding
    private lateinit var auth: FirebaseAuth
    // Veritabanı referansı
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()

        binding = ActivityKayitOlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sisteme kayıt işlemi
        binding.KayitKayitOlButton.setOnClickListener {
            val kullaniciAdi = binding.kayitKullaniciAdi.text.toString()
            val kullaniciSifre = binding.kayitSifreEditText.text.toString()
            var dolar: Int
            var euro: Int
            var tl: Int

            try {
                dolar = binding.dolarYatirEditText.text.toString().toInt()
            } catch (e: NumberFormatException) {
                dolar = 0
            }
            try {
                euro = binding.euroYatirEditText.text.toString().toInt()
            } catch (e: NumberFormatException) {
                euro = 0
            }
            try {
                tl = binding.tlYatirEditText.text.toString().toInt()
            } catch (e: NumberFormatException) {
                tl = 0
            }


            auth.createUserWithEmailAndPassword(kullaniciAdi, kullaniciSifre).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    user?.let {
                        // Yeni bir kullanıcı girişi veritabanında oluşturuluyor
                        val userId = it.uid
                        val userRef = database.reference.child("users").child(userId)
                        userRef.child("dolar").setValue(dolar)
                        userRef.child("euro").setValue(euro)
                        userRef.child("tl").setValue(tl)

                        Toast.makeText(applicationContext, "BAŞARIYLA KULLANICI OLUŞTURULDU", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            // KAYIT OLDUKTAN SONRA MAİN ACTİVİTYE GİTME
            intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}
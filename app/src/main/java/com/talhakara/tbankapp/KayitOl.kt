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

            if (kullaniciAdi.isEmpty() || kullaniciSifre.isEmpty()) {
                Toast.makeText(this, "kullanıcı adı ve şifre eksik", Toast.LENGTH_SHORT).show()
            } else {
                // Kullanıcı için benzersiz bir IBAN oluştur
                val kullaniciIBAN = benzersizIBANOlustur()

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
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            // Yeni bir kullanıcı girişi veritabanında oluşturuluyor
                            val userId = it.uid
                            val userRef = database.reference.child("users").child(userId)
                            userRef.child("iban").setValue(kullaniciIBAN) // Oluşturulan IBAN'ı kaydet
                            userRef.child("dolar").setValue(dolar)
                            userRef.child("euro").setValue(euro)
                            userRef.child("tl").setValue(tl)

                            Toast.makeText(applicationContext, "BAŞARIYLA KULLANICI OLUŞTURULDU", Toast.LENGTH_SHORT).show()
                            // KAYIT OLDUKTAN SONRA MAİN ACTİVİTYE GİTME
                            intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun benzersizIBANOlustur(): String {
        val ulkeKodu = "TR" // Ülke kodunu ihtiyacınıza göre değiştirebilirsiniz
        val hesapNumarasi = (1000000000000000..9999999999999999).random().toString()
        val kontrolHanesi = ibanKontrolHanesiHesapla(ulkeKodu + hesapNumarasi)
        return ulkeKodu + kontrolHanesi + hesapNumarasi
    }

    private fun ibanKontrolHanesiHesapla(iban: String): String {
        val numerikIBAN = iban.substring(4) + iban.substring(0, 2) + "00"
        var kalan = 0L

        for (i in numerikIBAN.indices) {
            kalan = (kalan * 10 + (numerikIBAN[i] - '0')) % 97
        }

        val kontrolHanesi = (98 - kalan).toString()
        return if (kontrolHanesi.length == 1) "0$kontrolHanesi" else kontrolHanesi
    }

}
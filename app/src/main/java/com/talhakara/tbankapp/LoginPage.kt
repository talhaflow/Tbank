package com.talhakara.tbankapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.talhakara.tbankapp.databinding.ActivityLoginPageBinding

class LoginPage : AppCompatActivity() {
    lateinit var binding: ActivityLoginPageBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding= ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //kullanıcı varsa çıkış yapma
        auth.signOut()


        binding.loginKayitOlButton.setOnClickListener {
            intent= Intent(applicationContext,KayitOl::class.java)
            startActivity(intent)
            finish()
        }

        //sisteme girme işlemi
        binding.LoginGirisYapbutton.setOnClickListener {
            val kullaniciAdi = binding.girisKullaniciAdiEditText.text.toString()
            val kullaniciSifre = binding.girisSifreEditText.text.toString()

            if(kullaniciAdi.isEmpty()||kullaniciSifre.isEmpty()){
                Toast.makeText(this,"kullanıcı adı ve şifre eksik",Toast.LENGTH_LONG).show()
            }else{

                auth.signInWithEmailAndPassword(kullaniciAdi, kullaniciSifre).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Giriş başarılı, MainActivity'e devam et
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Giriş başarısız, hata ile ilgilen
                        Toast.makeText(applicationContext, "Giriş başarısız: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    // Diğer hataları ele al, varsa
                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }





        }




    }

}
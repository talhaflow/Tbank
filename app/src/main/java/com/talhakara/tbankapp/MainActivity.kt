package com.talhakara.tbankapp


import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.talhakara.tbankapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates



class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityMainBinding



    lateinit var dolarTV:TextView
    lateinit var euroTV:TextView
    lateinit var tlTV:TextView

    lateinit var kurDolarTV:TextView
    lateinit var kurEuroTV:TextView

    lateinit var ibanTV: TextView
    var dolar: Int by Delegates.notNull()
    var euro: Int by Delegates.notNull()
    var tl: Int by Delegates.notNull()
    lateinit var iban: String

    var dolarKurSayisal by Delegates.notNull<Double>()
    var euroKurSayisal by Delegates.notNull<Double>()

    private lateinit var retrofit:Retrofit
    private lateinit var dovizAPI: DovizAPI
    private var BASE_URL="https://api.freecurrencyapi.com/v1/latest/"

    private lateinit var dovizTlCall: Call<DovizTl>
    private lateinit var dovizTl: DovizTl







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        ibanTV = binding.ibanTextView
        tlTV=binding.AnaSayfaTlGoster
        euroTV=binding.AnaSayfaEuroGoster
        dolarTV=binding.AnaSayfaDolarGosterTV

        kurDolarTV=binding.AnaSayfaDolarKurGosterTV
        kurEuroTV=binding.AnaSayfaEuroKurGosterTV




        binding.alGitBtn.setOnClickListener {
            intent= Intent(applicationContext,AlActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.satGitBtn.setOnClickListener {
            intent= Intent(applicationContext,SatActivity::class.java)
            startActivity(intent)
            finish()
        }

        val guncelKullanici = auth.currentUser

        if (guncelKullanici != null) {
            val userId = guncelKullanici.uid
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Verileri çekme işlemleri burada yapılacak
                    if (snapshot.exists()) {
                        dolar = (snapshot.child("dolar").value as Long).toInt() // Dolar değeri
                        euro = (snapshot.child("euro").value as Long).toInt() // Euro değeri
                        tl = (snapshot.child("tl").value as Long).toInt() // TL değeri
                        iban = snapshot.child("iban").value.toString() // IBAN değeri

                        // Verileri kullanmak veya göstermek için burada işlemler yapabilirsiniz

                        ibanTV.text = iban
                        dolarTV.text="${dolar}"
                        euroTV.text="${euro}"
                        tlTV.text="${tl}"
                        setRetrofitSettings()
                    } else {
                        // Eğer veriler yoksa veya hata varsa burada işlemler yapılabilir.
                        ibanTV.text = "Veriler bulunamadı."
                        dolarTV.text="Veriler bulunamadı."
                        euroTV.text="Veriler bulunamadı."
                        tlTV.text="Veriler bulunamadı."

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Veri çekme işlemi iptal edilirse burada işlemler yapılabilir.
                    ibanTV.text = "Veri çekme işlemi iptal edildi."
                }
            })
        }
    }

    fun setRetrofitSettings(){
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

         dovizAPI = retrofit.create(DovizAPI::class.java)

        dovizTlCall=dovizAPI.getDoviz()

        dovizTlCall.enqueue(object : Callback<DovizTl> {
            override fun onResponse(call: Call<DovizTl>, response: Response<DovizTl>) {
                // onResponse işlemleri burada yapılır
                if(response.isSuccessful&&response.body()!=null){

                   dovizTl= response.body()!!
                  kurEuroTV.text= "${1/dovizTl.tlBoluEuro}"
                  kurDolarTV.text="${1/dovizTl.tlBoluDolar}"
                }

            }

            override fun onFailure(call: Call<DovizTl>, t: Throwable) {
                // onFailure işlemleri burada yapılır
                print(t.toString())
            }
        })




    }
}


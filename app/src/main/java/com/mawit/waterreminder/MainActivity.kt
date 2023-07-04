package com.mawit.waterreminder

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.text.method.CharacterPickerDialog
import android.widget.TimePicker
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mawit.waterreminder.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    lateinit var timePickerDialog: TimePickerDialog
    lateinit var calendario : Calendar
    private var mInterstitialAd: InterstitialAd? = null
    lateinit var mAdView: AdView
    var horaAtual = 0
    var minutoAtual = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeAds()

        var btnAdd250ml = binding.btnAdd250ml
        var btnAdd500ml = binding.btnAdd500ml
        var btnAdd1l = binding.btnAdd1l

        var txtTotalLitros = binding.txtTotalLitros
        var btnDefinirAlarme = binding.btnDefinirAlarme
        var btnResetarContador = binding.btnResetarContador

        var txtTotalLitrosDouble = 0.0

        var horaAlarme : Int? = null
        var minutoAlarme: Int? = null

        btnAdd250ml.setOnClickListener {
            txtTotalLitrosDouble += 0.250
            txtTotalLitros.text = "$txtTotalLitrosDouble litros"
        }

        btnAdd500ml.setOnClickListener {
            txtTotalLitrosDouble += 0.500
            txtTotalLitros.text = "$txtTotalLitrosDouble litros"
        }

        btnAdd1l.setOnClickListener {
            txtTotalLitrosDouble += 1
            txtTotalLitros.text = "$txtTotalLitrosDouble litros"
        }

        btnResetarContador.setOnClickListener {
            txtTotalLitros.text = "0,0 litros"
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            }
        }

        btnDefinirAlarme.setOnClickListener {

            calendario = Calendar.getInstance()
            horaAtual = calendario.get(Calendar.HOUR_OF_DAY)
            minutoAtual = calendario.get(Calendar.MINUTE)

            timePickerDialog = TimePickerDialog(this, {timePicker: TimePicker, hourOfDay: Int, minutes: Int ->
                horaAlarme = hourOfDay
                minutoAlarme = minutes
                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                intent.putExtra(AlarmClock.EXTRA_HOUR, horaAlarme)
                intent.putExtra(AlarmClock.EXTRA_MINUTES, minutoAlarme)
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.alarme_mensagem))
                startActivity(intent)
            }, horaAtual, minutoAtual, true)
            timePickerDialog.show()

        }

    }

    fun initializeAds(){
        MobileAds.initialize(this)
        val banner = AdView(this)
        val adRequest = AdRequest.Builder().build()
        banner.setAdSize(AdSize.BANNER)
        banner.adUnitId = "ca-app-pub-5618593123155937/2135748447"
        mAdView = binding.adView
        mAdView.loadAd(adRequest)



        InterstitialAd.load(
            this,"ca-app-pub-5618593123155937/2694468876",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd

                    mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                        override fun onAdClicked() {}

                        override fun onAdDismissedFullScreenContent() {
                            mInterstitialAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            mInterstitialAd = null
                        }

                        override fun onAdImpression() {}

                        override fun onAdShowedFullScreenContent() {}
                    }
                }
            })

    }
}
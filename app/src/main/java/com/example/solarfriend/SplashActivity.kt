package com.example.solarfriend
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val animationView = findViewById<LottieAnimationView>(R.id.lottieAnimationView)
        animationView.setAnimation(R.raw.solarlottie)
        animationView.playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }, 3000)
    }
}




package com.cibertec.bodegaubita.ui.cart

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import com.cibertec.bodegaubita.databinding.ActivitySuccessBinding
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class SuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // konfetti animation
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )
        binding.konfettiView.start(party)

        val rotation = ObjectAnimator.ofFloat(binding.ivSuccess, "rotation", 0f, 360f)
        rotation.duration = 1000
        rotation.interpolator = DecelerateInterpolator()
        rotation.start()

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

    }
}
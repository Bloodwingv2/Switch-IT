package com.example.switch_it;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Initialize the Lottie animation view
        LottieAnimationView animationView = findViewById(R.id.splashAnimation);
        animationView.setAnimation("rocketman.json"); // Ensure this matches your Lottie animation file
        animationView.setRepeatCount(LottieDrawable.INFINITE); // Loop the animation indefinitely
        animationView.playAnimation();

        // Duration for the splash screen
        int splashDuration = 6000; // 3 seconds

        // Using a Handler to delay the transition to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // Apply smooth transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Close the SplashActivity
        }, splashDuration);
    }
}

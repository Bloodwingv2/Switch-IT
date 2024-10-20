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

    private static final int SPLASH_DURATION = 3600; // Total duration of the splash screen in milliseconds
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // Initialize the Lottie animation view
        animationView = findViewById(R.id.splashAnimation);
        animationView.setAnimation("rocketman.json"); // Ensure this matches your Lottie animation file
        animationView.setRepeatCount(LottieDrawable.INFINITE); // Loop the animation indefinitely
        animationView.setSpeed(1); // Set the speed of the animation
        animationView.playAnimation();

        // Using a Handler to delay the transition to MainActivity
        new Handler().postDelayed(() -> {
            // Stop the animation and transition to the MainActivity
            animationView.cancelAnimation(); // Stop the animation
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // Apply smooth transition animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Close the SplashActivity
        }, SPLASH_DURATION);
    }
}

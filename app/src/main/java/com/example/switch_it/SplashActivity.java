package com.example.switch_it;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3600; // Total duration of the splash screen in milliseconds

    private LottieAnimationView animationView;
    private TextView appNameTextView, mottoTextView;

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

        // Initialize the TextView for the app name and motto
        appNameTextView = findViewById(R.id.appNameTextView);
        mottoTextView = findViewById(R.id.mottoTextView);

        setColoredAppName(); // Set the colored app name
        setMottoText();      // Set the motto text

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

    // Function to set colored app name
    private void setColoredAppName() {
        String fullText = "Switch-IT";
        SpannableString spannableString = new SpannableString(fullText);

        // Change color of specific letters in "Switch-IT"
        int redColor = ContextCompat.getColor(this, R.color.red);
        int yellowColor = ContextCompat.getColor(this, R.color.yellow);
        spannableString.setSpan(new ForegroundColorSpan(redColor), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // S
        spannableString.setSpan(new ForegroundColorSpan(redColor), 8, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // T
        spannableString.setSpan(new ForegroundColorSpan(yellowColor), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // w

        appNameTextView.setText(spannableString);
    }

    // Function to set colored motto text
    private void setMottoText() {
        String motto = "Simple But Useful";
        SpannableString spannableMotto = new SpannableString(motto);

        // Apply color for the entire motto
        int redColor = ContextCompat.getColor(this, R.color.red);
        int yellowColor = ContextCompat.getColor(this, R.color.yellow);
        spannableMotto.setSpan(new ForegroundColorSpan(redColor), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // S
        spannableMotto.setSpan(new ForegroundColorSpan(yellowColor), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableMotto.setSpan(new ForegroundColorSpan(redColor), 7, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // S
        spannableMotto.setSpan(new ForegroundColorSpan(yellowColor), 8, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableMotto.setSpan(new ForegroundColorSpan(redColor), 11, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // S
        spannableMotto.setSpan(new ForegroundColorSpan(yellowColor), 12, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mottoTextView.setText(spannableMotto);
    }
}

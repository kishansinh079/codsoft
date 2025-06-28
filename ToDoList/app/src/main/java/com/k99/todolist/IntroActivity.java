package com.k99.todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    Button getStartedBtn;
    private LinearLayout howItWorksBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if intro has been shown already
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("isFirstLaunch", true);

        if (!isFirstTime) {
            // Skip intro and open MainActivity
            startActivity(new Intent(IntroActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_intro);

        getStartedBtn = findViewById(R.id.btn_get_started);
        howItWorksBtn = findViewById(R.id.btn_how_it_works);

        getStartedBtn.setOnClickListener(view -> {
            // Mark intro as shown
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();

            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        howItWorksBtn.setOnClickListener(view -> {
            // Replace the URL with your actual YouTube link
            String youtubeUrl = "https://youtube.com/shorts/uIFnq_He09k?feature=share";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
            startActivity(intent);
        });
    }
}

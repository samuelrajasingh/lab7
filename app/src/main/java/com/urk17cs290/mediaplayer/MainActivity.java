package com.urk17cs290.mediaplayer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.urk17cs290.mediaplayer.music.activities.SplashScreen;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button music = findViewById(R.id.music);
        Button video = findViewById(R.id.video);

        music.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SplashScreen.class);
            startActivity(i);
        });
        video.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this,VideoActivity.class);
            startActivity(i);
        });

    }
}
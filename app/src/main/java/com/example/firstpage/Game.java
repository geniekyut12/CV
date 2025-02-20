package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Find the Play button in the layout
        Button playButton = findViewById(R.id.playButton);

        // Set an OnClickListener for the Play button
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to GameView activity
                Intent intent = new Intent(Game.this, Game1Tut.class);
                startActivity(intent);
            }
        });
    }
}

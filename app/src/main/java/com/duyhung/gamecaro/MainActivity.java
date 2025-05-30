package com.duyhung.gamecaro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnTwoPlayers, btnPlayWithAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTwoPlayers = findViewById(R.id.btnTwoPlayers);
        btnPlayWithAI = findViewById(R.id.btnPlayWithAI);

        btnTwoPlayers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra(GameActivity.EXTRA_MODE, GameActivity.MODE_TWO_PLAYERS);
            startActivity(intent);
        });

        btnPlayWithAI.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra(GameActivity.EXTRA_MODE, GameActivity.MODE_PLAY_WITH_AI);
            startActivity(intent);
        });
    }
}


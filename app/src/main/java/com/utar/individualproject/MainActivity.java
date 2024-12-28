package com.utar.individualproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button compareNumbersButton = findViewById(R.id.comparingNumbersButton);
        Button composingNumbersButton = findViewById(R.id.composingNumbersButton);

        compareNumbersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompareNumbersActivity.class);
            startActivity(intent);
        });

        composingNumbersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ComposingNumbersActivity.class);
            startActivity(intent);
        });
    }
}


package com.example.myapplication;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
public class MainActivity extends AppCompatActivity {
    Intent intent;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        ImageButton buttonImage = findViewById(R.id.buttonImage);
        ImageButton buttonHistory = findViewById(R.id.buttonHistory);
        DBHandler dbHandler = new DBHandler(MainActivity.this);
        dbHandler.loadHandler();
        buttonImage.setOnClickListener(v -> {
            intent = new Intent(MainActivity.this, Image.class);
            startActivity(intent);
        });
        buttonHistory.setOnClickListener(v -> {
            intent = new Intent(MainActivity.this, DetailsActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
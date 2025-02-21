package com.example.firstpage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class RewMasug5per extends AppCompatActivity {

    private ImageView ivQRCode;
    private final String qrData = "https://www.youtube.com/watch?v=xvFZjo5PgG0"; // Set your dynamic QR data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rew_masug5per); // Ensure this XML file exists

        // Initialize UI Elements
        Button backBtn = findViewById(R.id.btn_masug5per);
        ivQRCode = findViewById(R.id.ivQRCode); // Make sure you add this ImageView in your XML

        // Generate QR Code automatically
        generateQRCode(qrData);

        // Set Click Listener for the Back Button
        backBtn.setOnClickListener(v -> navigateToHome());
    }

    // Function to generate and display the QR code
    private void generateQRCode(String text) {
        if (text.isEmpty()) {
            Toast.makeText(this, "No data for QR Code", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 400, 400);
            ivQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to navigate back to the main/home screen
    private void navigateToHome() {
        Intent intent = new Intent(RewMasug5per.this, FragmentRewardM.class); // Change to the correct destination activity
        startActivity(intent);
        finish(); // Close the current activity
    }
}

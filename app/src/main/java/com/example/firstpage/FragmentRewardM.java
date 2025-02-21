package com.example.firstpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class FragmentRewardM extends AppCompatActivity {

    private final int userPoints = 350;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_reward_m);

        // Initialize buttons
        Button redeemButton1 = findViewById(R.id.claim_masug5per);
        Button redeemButton2 = findViewById(R.id.claim_masug10per);
        Button redeemButton3 = findViewById(R.id.claim_wani5per);
        Button redeemButton4 = findViewById(R.id.claim_playm5per);

        // Uncomment if you have this method implemented
        // updatePointsDisplay();

        // Show RedeemDialog when redeemButton1 is clicked
        redeemButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRedeemDialog();
            }
        });

        // Open RewMasug5per Activity when redeemButton2 is clicked
        redeemButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentRewardM.this, RewMasug5per.class);
                startActivity(intent);
            }
        });

        // Open RewWani5per Activity when redeemButton3 is clicked
        redeemButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentRewardM.this, RewWani5per.class);
                startActivity(intent);
            }
        });

        // Open RewPlaym5per Activity when redeemButton4 is clicked
        redeemButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FragmentRewardM.this, RewPlaym5per.class);
                startActivity(intent);
            }
        });
    }

    // Method to show the RedeemDialog fragment
    private void showRedeemDialog() {
        FragmentManager fm = getSupportFragmentManager();
        RedeemDialog redeemDialog = new RedeemDialog();
        redeemDialog.show(fm, "redeem_dialog");
    }
}

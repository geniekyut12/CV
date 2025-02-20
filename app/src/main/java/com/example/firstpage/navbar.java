package com.example.firstpage;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Stack;

public class navbar extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final Stack<Integer> fragmentStack = new Stack<>();
    private static final String PREFS_NAME = "loginPrefs";
    private static final String PREF_HAS_SEEN_POPUP = "hasSeenPopup"; // Track if popup was shown

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Load the default fragment (HomeFragment)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), R.id.nav_home);
        }

        // Handle bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Avoid reloading the current fragment
            if (!fragmentStack.isEmpty() && fragmentStack.peek() == itemId) {
                return true;
            }

            Fragment selectedFragment = null;
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_footprint) {
                selectedFragment = new FootPrintFragment();
            } else if (itemId == R.id.nav_games) {
                selectedFragment = new GameFragment();
            } else if (itemId == R.id.nav_challenge) {
                selectedFragment = new CommunityFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment, itemId);
            }

            return true;
        });

        // Check for discount popup
        checkForDiscountPopup();
    }

    private void loadFragment(Fragment fragment, int itemId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        // Add the selected fragment to the stack
        if (!fragmentStack.isEmpty() && fragmentStack.peek() == itemId) {
            return; // Prevent duplicate entries
        }
        fragmentStack.push(itemId);
    }

    @Override
    public void onBackPressed() {
        if (fragmentStack.size() > 1) {
            fragmentStack.pop(); // Remove the current fragment
            int previousItemId = fragmentStack.peek();

            Fragment previousFragment = null;
            if (previousItemId == R.id.nav_home) {
                previousFragment = new HomeFragment();
            } else if (previousItemId == R.id.nav_footprint) {
                previousFragment = new FootPrintFragment();
            } else if (previousItemId == R.id.nav_games) {
                previousFragment = new GameFragment();
            } else if (previousItemId == R.id.nav_challenge) {
                previousFragment = new CommunityFragment();
            } else if (previousItemId == R.id.nav_profile) {
                previousFragment = new ProfileFragment();
            }

            if (previousFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, previousFragment)
                        .commit();
                bottomNavigationView.setSelectedItemId(previousItemId);
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Fetch total registered users and determine if the current user qualifies for the 10% discount.
     */
    private void checkForDiscountPopup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("users");

        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int totalUsers = task.getResult().size();

                if (totalUsers <= 10) { // Check if user is among the first 10
                    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    boolean hasSeenPopup = sharedPreferences.getBoolean(PREF_HAS_SEEN_POPUP, false);

                    if (!hasSeenPopup) {
                        showDiscountPopup();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(PREF_HAS_SEEN_POPUP, true); // Mark that popup was shown
                        editor.apply();
                    }
                }
            }
        });
    }

    /**
     * Displays the 10% discount popup.
     */
    private void showDiscountPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Congratulations!")
                .setMessage("You are among the first 10 users! Enjoy a 10% discount on your first purchase.")
                .setPositiveButton("Claim Now", (dialog, which) -> {
                    // You can add logic to redirect the user to a discount page
                    dialog.dismiss();
                })
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

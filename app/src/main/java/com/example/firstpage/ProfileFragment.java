package com.example.firstpage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "loginPrefs";
    private static final String PREF_IS_LOGGED_IN = "isLoggedIn";
    private static final String CHANNEL_ID = "user_notifications";
    private static final long NOTIFICATION_INTERVAL = 5000; // 5 seconds

    private TextView titleName;
    private LinearLayout logoutButton, editButton;
    private ImageView profileImg, notificationIcon;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String username;
    private Handler handler;
    private Runnable notificationRunnable;

    public ProfileFragment() {
        // Default constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) return; // Prevent crashes if activity is null

        initializeViews(view);
        createNotificationChannel();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fetchUserData();
        setupListeners();
        startNotificationService(); // Start the background notification service
    }

    private void initializeViews(View view) {
        titleName = view.findViewById(R.id.titleName);
        editButton = view.findViewById(R.id.editProfile);
        logoutButton = view.findViewById(R.id.btnlogout);
        profileImg = view.findViewById(R.id.profileImg);
        notificationIcon = view.findViewById(R.id.notificationIcon); // Initialize this view
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> handleLogout());
        editButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), EditProfile.class)));
        notificationIcon.setOnClickListener(v -> showStoredNotifications());
    }

    private void fetchUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userEmail = currentUser.getEmail();
        if (userEmail == null) return;

        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        username = document.getString("username");
                        titleName.setText(username);
                        listenForNotifications();
                        startRealtimeNotifications();
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Failed to get username: " + e.getMessage()));
    }

    private void listenForNotifications() {
        if (username == null || db == null) return;
        db.collection("notifications")
                .whereEqualTo("username", username)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            String message = dc.getDocument().getString("message");
                            if (message != null) {
                                sendNotification(message);
                                saveNotificationLocally(message); // Save the notification locally
                            }
                        }
                    }
                });
    }

    private void startRealtimeNotifications() {
        handler = new Handler(Looper.getMainLooper());
        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                sendNotification("🔔 This is a real-time notification!");
                handler.postDelayed(this, NOTIFICATION_INTERVAL); // Repeat every 5 seconds
            }
        };
        handler.postDelayed(notificationRunnable, NOTIFICATION_INTERVAL);
    }

    private void sendNotification(String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(requireContext());
        try {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            Log.e("NotificationError", "Notification permission denied: " + e.getMessage());
        }
    }

    private void saveNotificationLocally(String message) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastNotification", message);
        editor.apply();
    }

    private void showStoredNotifications() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("notifications", Context.MODE_PRIVATE);
        String lastNotification = prefs.getString("lastNotification", "No notifications");
        Toast.makeText(getContext(), lastNotification, Toast.LENGTH_LONG).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "User Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = requireActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void startNotificationService() {
        Intent serviceIntent = new Intent(requireActivity(), NotificationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent);
        } else {
            requireActivity().startService(serviceIntent);
        }
    }

    private void handleLogout() {
        FirebaseAuth.getInstance().signOut();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_IS_LOGGED_IN, false);
        editor.apply();

        Intent intent = new Intent(requireActivity(), Signin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        requireActivity().finish();
    }
}
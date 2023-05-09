package com.example.testingalternateroutes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    ImageView loadingGif;
    Button startButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TextView loadingText = findViewById(R.id.loading_text);
        loadingText.setVisibility(View.GONE);

        loadingGif = findViewById(R.id.loading_gif);

        startButton = findViewById(R.id.start_button);
        exitButton = findViewById(R.id.exit_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setTitle("Enable Location")
                        .setMessage("Please enable location permission to use all features of this app. \n" +
                                "Disregard if location is already permitted.")
                        .setPositiveButton("Enable Location", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Open app settings to allow user to enable location permission
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog, do nothing
                            }
                        })
                        .setNeutralButton("Start anyway", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // show the loading spinner and text
                                loadingText.setVisibility(View.VISIBLE);
                                loadingGif.setVisibility(View.VISIBLE);
                                startButton.setEnabled(false);
                                exitButton.setEnabled(false);

                                Animation alphaAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_anim);
                                alphaAnimation.setRepeatCount(Animation.INFINITE);

                                // apply the animation to the text view
                                loadingText.startAnimation(alphaAnimation);

                                // start the main activity after a delay
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish(); // finish the StartActivity so the user can't go back to it
                                    }
                                }, 3000);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
                builder.setTitle("Confirm exit");
                builder.setMessage("Are you sure you want to exit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish(); // exit the app
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

}
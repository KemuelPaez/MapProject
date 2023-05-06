package com.example.testingalternateroutes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private ProgressBar mLoadingSpinner;
    Button startButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TextView loadingText = findViewById(R.id.loading_text);
        loadingText.setVisibility(View.GONE);

        mLoadingSpinner = findViewById(R.id.loading_spinner);
        mLoadingSpinner.setVisibility(View.GONE);

        startButton = findViewById(R.id.start_button);
        exitButton = findViewById(R.id.exit_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show the loading spinner and text
                mLoadingSpinner.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);

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
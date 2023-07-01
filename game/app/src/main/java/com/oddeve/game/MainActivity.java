package com.oddeve.game;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


public class MainActivity extends AppCompatActivity {
    Button playBtn, secretBtn, multiplayerBtn;
    private InterstitialAd mInterstitialAd;
    boolean userVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        playBtn = findViewById(R.id.playBtn);
        secretBtn = findViewById(R.id.about);
        multiplayerBtn = findViewById(R.id.multiplayer);

        // Check network connectivity
        if (!isConnectedToInternet()) {
            // Display toast message
            Toast.makeText(this, "please connect to internet to play", Toast.LENGTH_LONG).show();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1375172801279152/9328850948");

        userVerified = (boolean)getIntent().getBooleanExtra("userVerified", false );

        if (!userVerified) {
            secretBtn.setEnabled(false);
            playBtn.setEnabled(false);
            multiplayerBtn.setEnabled(false);

            Toast.makeText(MainActivity.this, "Verifying user please wait", Toast.LENGTH_LONG).show();
        }
        
        loadInterstitialAd();
        showInterstitial();

        playBtn.setOnClickListener(View -> {
            showInterstitial();
            Intent i = new Intent(getApplicationContext(), tossActivity.class);
            startActivity(i);
        });

        secretBtn.setOnClickListener(View -> {
            showInterstitial();
            Intent i = new Intent(getApplicationContext(), secretActivity.class);
            startActivity(i);
        });

        multiplayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
                Intent i = new Intent(getApplicationContext(), multiplayerActivity.class);
                startActivity(i);
            }
        });
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void showInterstitial() {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
    }

    public void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.game_over_interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        if (!userVerified) {
                            Toast.makeText(MainActivity.this, "User Verified", Toast.LENGTH_SHORT).show();
                            playBtn.setEnabled(true);
                            secretBtn.setEnabled(true);
                            multiplayerBtn.setEnabled(true);
                        }
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        mInterstitialAd = null;
                                        Log.d(TAG, "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        mInterstitialAd = null;
                                        Log.d(TAG, "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d(TAG, "The ad was shown.");
                                    }
                                }
                        );
                    }
                });
    }

}
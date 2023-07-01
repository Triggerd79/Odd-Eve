package com.oddeve.game;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Random;

public class tossActivity extends AppCompatActivity {
    Random random = new Random();
    private InterstitialAd mInterstitialAd;
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Button btn7;
    Button btn8;
    Button btn9;
    Button btn10;
    Button oddBtn;
    Button evenBtn;
    ImageView bat;
    ImageView ball;
    Button nextBtn;
    Button[] buttons;
    TextView eventView;
    TextView tossTitle;
    LinearLayout tossGameContainer;
    String tossWinner;
    int choice;
    int player;
    int comp;
    int sum;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_toss);

        Toast.makeText(tossActivity.this, "Initialising toss please wait", Toast.LENGTH_LONG).show();

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

        // initialize all buttons
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btn10 = findViewById(R.id.btn10);
        oddBtn = findViewById(R.id.oddBtn);
        evenBtn = findViewById(R.id.evenBtn);
        nextBtn = findViewById(R.id.nextBtn);
        bat = findViewById(R.id.battingBtn);
        ball = findViewById(R.id.ballingBtn);
        eventView = findViewById(R.id.tossEventView);
        tossTitle = findViewById(R.id.Toss);
        tossGameContainer = findViewById(R.id.tossGameContainer);
        buttons = new Button[]{btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10};

        // set click event listener to all buttons
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleBtnClick(btn1);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn2);
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn3);
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn4);
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn5);
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn6);
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn7);
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn8);
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn9);
            }
        });
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBtnClick(btn10);
            }
        });
        oddBtn.setOnClickListener(View -> {choice =  1; chooseNumber();});
        evenBtn.setOnClickListener(View -> {choice =  2; chooseNumber();});
        ball.setOnClickListener(View -> gotoPlayScreen("balling"));
        bat.setOnClickListener(View -> gotoPlayScreen("batting"));
        nextBtn.setOnClickListener(View -> gotoPlayScreen("batting"));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1375172801279152/5295947029");

        // hide game container
        tossGameContainer.setVisibility(View.INVISIBLE);
        findViewById(R.id.tossEventView);

        // load interstitial ad
        loadInterstitialAd();

        // hide all buttons
        for(Button button : buttons) button.setVisibility(View.INVISIBLE);
        oddBtn.setVisibility(View.INVISIBLE);
        evenBtn.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        bat.setVisibility(View.INVISIBLE);
        ball.setVisibility(View.INVISIBLE);

        // start the game
        chooseOddEven();
    }

    private void gotoPlayScreen(String choice ) {

        showInterstitial();
        // Wait for 3 seconds and then show the Ad
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), playActivity.class);
                i.putExtra("playerAction", choice);
                startActivity(i);
            }
        }, 3000);
    }

    private void chooseOddEven() {
        // change event screen text
        String str = "Choose ODD or EVEN";
        eventView.setText(str);

        // display odd and even btn
        oddBtn.setVisibility(View.VISIBLE);
        evenBtn.setVisibility(View.VISIBLE);
    }

    private void chooseNumber() {
        // hide odd and even btn
        oddBtn.setVisibility(View.INVISIBLE);
        evenBtn.setVisibility(View.INVISIBLE);

        // display number buttons
        for(Button button : buttons) button.setVisibility(View.VISIBLE);

        // change event screen text
        String string = "choose your action";
        eventView.setText(string);
    }

    public void chooseBatBall(){
        String str;


            // display bat and ball btn
            bat.setVisibility(View.VISIBLE);
            ball.setVisibility(View.VISIBLE);

            // get last text of event screen
            String oldText = eventView.getText().toString();
            str = "\nChoose batting or balling";
            str = oldText + str;// append new text in event screen
            eventView.setText(str);

    }

    private void getTossWinner() {
        String str;

        if (sum % 2 == 0){
            // for even sum
            if (choice == 2) tossWinner = "player";
            else tossWinner = "comp";

            str = "\n sum is even";
        }else {
            // for odd sum
            if (!(choice == 2)) tossWinner = "player";
            else tossWinner = "comp";

            str = "\n sum is odd";
        }

        str += "\n" + tossWinner + " wins the toss ";

        String oldText = eventView.getText().toString();
        str = oldText + str;
        eventView.setText(str);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // if player wins the toss
        if (tossWinner.equalsIgnoreCase("player")){
            chooseBatBall();
        }else {
            // set event screen text
            str += "\n\n\ncomputer wins the toss and \nchoose balling \nclick next to continue";
            eventView.setText(str);

            // display next btn
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    private void handleBtnClick(Button btn)  {
        player = Integer.parseInt(btn.getText().toString());
        comp = random.nextInt(10) + 1;
        sum = comp + player;

        String str = "Player : " + player +
                    "\nComp : " + comp +
                    "\nSum : " + sum;

        // change text of event screen
        eventView.setText(str);

        // hide all number buttons
        for(Button button : buttons) button.setVisibility(View.INVISIBLE);

        showInterstitial();

        Toast.makeText(tossActivity.this, "Getting toss winner....", Toast.LENGTH_LONG).show();

        // get the toss winner
        getTossWinner();
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
        InterstitialAd.load(this, getString(R.string.toss_screen_interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        // show game container
                        tossGameContainer.setVisibility(View.VISIBLE);
                        tossTitle.setVisibility(View.VISIBLE);

                        Toast.makeText(tossActivity.this, "Toss Initiated", Toast.LENGTH_SHORT).show();
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
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;

                    }
        });
    }

}
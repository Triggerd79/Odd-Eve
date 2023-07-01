package com.oddeve.game;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Random;

public class playActivity extends AppCompatActivity {
    Random random = new Random();
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    Button btn5;
    Button btn6;
    Button btn10;
    Button restart;
    Button[] buttons;
    TextView eventView;
    TextView playerScoreView;
    TextView compScoreView;
    TextView playerBallsPlayedView;
    TextView compBallsPlayedView;
    LinearLayout gameContainer;
    int player;
    int comp;
    int playerScore = 0;
    int compScore = 0;
    int playerBallsPlayed = 0;
    int compBallsPlayed = 0;
    String winner = "";
    String firstAction = "";
    String secondBatsman = "";
    boolean scoreChased = false;
    boolean playerOut = false;
    boolean compOut = false;
    boolean isFirstInningOver = false;
    private InterstitialAd mInterstitialAd;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);


        gameContainer = findViewById(R.id.gameContainer);
        compScoreView = findViewById(R.id.compScore);
        playerBallsPlayedView = findViewById(R.id.playerBallsPlayed);
        compBallsPlayedView = findViewById(R.id.compballsPlayed);
        playerScoreView = findViewById(R.id.playerScore);
        eventView = findViewById(R.id.eventTextView);
        restart = findViewById(R.id.restart);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn10 = findViewById(R.id.btn10);
        buttons = new Button[]{btn1, btn2, btn3, btn4, btn5, btn6, btn10};

        // Check network connectivity
        if (!isConnectedToInternet()) {
            // Display toast message
            Toast.makeText(this, "please connect to internet to play", Toast.LENGTH_LONG).show();
        }

        // load the banner ad
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1375172801279152/5784738472");

        // hide restart btn
        restart.setVisibility(View.INVISIBLE);

        // hide the game container
        gameContainer.setVisibility(View.INVISIBLE);

        Toast.makeText(playActivity.this, "Initialising Game please wait", Toast.LENGTH_SHORT).show();

        // load the Interstitial ad
        loadInterstitialAd();

        // add click event listener to all buttons
        for(Button btn : buttons){
            btn.setOnClickListener(View -> handleBtnClick(btn));
        }

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("userVerified", true);
                startActivity(intent);
                finish();
            }
        });

        // Retrieve the player action from SharedPreferences
        firstAction = getIntent().getStringExtra("playerAction");

        // change event text as per player action
        String startingEventText = "Lets Play \n its your turn for " + firstAction;
        eventView.setText(startingEventText);
    }

    private void playerBatting(){
        // player batting function
        eventView.setTextColor(getColor(R.color.black));// set event view text color black
        playerBallsPlayed++;// increase no of balls played by player

        String eventViewString = "\nComp : " + comp + "\nplayer : " + player;
        eventView.setText(eventViewString);// change event screen text

        //check for player out
        if (player == comp){
            playerOut = true;
        }

        if (!playerOut){
            playerScore = playerScore + player;// increase player score if not out

            if (playerScore >= 100 && playerScore < 110){
                eventViewString += "\nYou achieved " + playerScore + "\uD83E\uDD2F";
                eventView.setText(eventViewString);
            }
        }else {
            eventView.setTextColor(getColor(R.color.red));// set event view text color red if player is out

            if (!isFirstInningOver) {
                eventViewString += "\nYou are out!\nNow you are bowling";
                eventView.setText(eventViewString);
            }
            else {
                // if player is out and score is less than computer
                if (compScore > playerScore) {
                    // winner is computer
                    winner = "computer";

                    // call game over function
                    gameOver();
                }else if (compScore == playerScore) {
                    // call tie function
                    tie();
                }
            }
        }

        updateScoreAndBallsPlayed("player");// update score view for player

        if (isFirstInningOver){
            // if first inning is over and player score exceeds computer
            if (compScore < playerScore) {
                // winner is player
                winner = "player";
                scoreChased = true;// score chased
                // call game over function
                gameOver();
            }
        }

        if (playerOut) {
            if (!isFirstInningOver) {
                isFirstInningOver = true;
            }
        }
    }

    private void computerBatting() {
        // player batting function

        eventView.setTextColor(getColor(R.color.black));// set event view text color black
        compBallsPlayed++;// increase no of balls played by computer

        String eventViewString = "\nComp : " + comp + "\nplayer : " + player;
        eventView.setText(eventViewString);

        // check if computer out
        if (player == comp){
            compOut = true;
        }


        if (!compOut) {
            compScore = compScore + comp;// increase computer score if not out
        }
        else {
            eventView.setTextColor(getColor(R.color.red));// set event view text color red

            if (!isFirstInningOver) {
                eventViewString += "\nComp out! \nNow you are batting";
                eventView.setText(eventViewString);
            }
            else {
                // if computer is out and score is less than player
                if (playerScore > compScore) {
                    // winner is player
                    winner = "player";

                    // call game over function
                    gameOver();
                } else if (compScore == playerScore) {
                    // call tie function
                    tie();
                }
            }
        }

        updateScoreAndBallsPlayed("comp");// update score view for computer

        if (isFirstInningOver) {
            // if first inning is over and computer score exceeds player
            if (compScore > playerScore) {
                // winner is computer
                winner = "computer";
                scoreChased = true;// score chased
                // call game over function
                gameOver();
            }
        }

        if (compOut) {
            if (!isFirstInningOver) {
                isFirstInningOver = true;
            }
        }
    }

    private void tie() {
        String string;
        string = "its a tie!";
        eventView.setText(string);
        eventView.setTypeface(Typeface.DEFAULT_BOLD);
        eventView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        hideButtons();

        // Wait for 3 seconds and then show the Ad
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showInterstitial();
                // show restart button
                restart.setVisibility(View.VISIBLE);
            }
        }, 3000);
    }

    private void gameOver() {
        String string = "";

        if (!scoreChased){
            string = secondBatsman + " is out!";
        }

        string += "Game over!" +
                "\n Winner is " + winner;

        if (winner.contains("comp")){
            string += " \uD83D\uDE2D";
        }else {
            string += "\uD83D\uDE0D";
        }

        eventView.setText(string);// change event screen text
        eventView.setTypeface(Typeface.DEFAULT_BOLD);
        eventView.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

        // hide all buttons
        hideButtons();

        // Wait for 3 seconds and then show the Ad
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showInterstitial();
                // show restart button
                restart.setVisibility(View.VISIBLE);
            }
        }, 3000);

    }

    private void updateScoreAndBallsPlayed(String currentBatting) {
        String score, ballsPlayed;

        if (currentBatting.equalsIgnoreCase("comp")){
            ballsPlayed = "Balls played : " + compBallsPlayed;
            score = "Comp Score  : " + compScore;

            //change score and balls played view text
            compScoreView.setText(score);
            compBallsPlayedView.setText(ballsPlayed);
        }else {
            ballsPlayed = "Balls played : " + playerBallsPlayed;
            score = "Player Score  : " + playerScore;

            //change score and balls played view text
            playerScoreView.setText(score);
            playerBallsPlayedView.setText(ballsPlayed);
        }
    }

    private void handleBtnClick(Button btn){
        player = Integer.parseInt(btn.getText().toString());// get player choice
        comp = chooseCompNumber();// get computer choice

        if (firstAction.contains("batting")){
            secondBatsman = "comp";
            if (!isFirstInningOver){
                playerBatting();
            }else {
                computerBatting();
            }
        }
        else if (firstAction.contains("balling")){
            secondBatsman = "player";
            if (!isFirstInningOver){
                computerBatting();
            }else {
                playerBatting();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "An Error occurred", Toast.LENGTH_LONG).show();
            System.exit(0);
        }

    }

    private int chooseCompNumber() {
        int no;
        int[] numberList = {1, 2, 3, 4, 5, 6, 10};
        int numberListLength = numberList.length;
        int bound = random.nextInt(numberListLength) ;
        no = numberList[bound];
        return no;
    }

    private void hideButtons(){
        for(Button button : buttons){
            button.setVisibility(View.GONE);
        }
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
        InterstitialAd.load(this, getString(R.string.home_page_interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        Toast.makeText(playActivity.this, "Game Initialised", Toast.LENGTH_SHORT).show();
                        gameContainer.setVisibility(View.VISIBLE);

                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        //mInterstitialAd = null;
                                        Log.d(TAG, "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        //mInterstitialAd = null;
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

package com.example.reenamaryputhota.microgolf;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity {


    List<Hole> holeItems;
    TextView player1TextView, player2TextView, player1ShotTextView, player2ShotTextView;
    Hole hole_item;
    ListView listView;
    Button startButton, restartButton;
    int holeImageID, jackpotImageID, ballImageID;
    Dialog dialog;
    HoleAdapter holeAdapter;
    private static final int SET_JACKPOT_HOLE = 0;
    int randomHole;
    private static final int minRange = 0, maxRange = 50;
    private int newRandom;
    boolean stopTheGame = false;
    int min = 0, max = 50;
    String text1 = "", text2 = "";

    Handler player1Handler, player2Handler;
    private static int jackpotHole, jackpotGroup;
    Set<Integer> used1 = new HashSet<Integer>();
    Set<Integer> used2 = new HashSet<Integer>();


    //Dialog Box
    TextView outcomeText1, outcomeText2;
    Button okButton, restart_button;

    //RESPONSE
    private static final int
            JACKPOT = 1,
            NEAR_MISS = 2,
            NEAR_GROUP = 3,
            BIG_MISS = 4,
            CATASTROPHE = 5;

    private static final int
            RANDOM = 11;

    //ACTIONS
    private static final int
            KNOW_SHOT = 6;


    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        int holeNo, playerNo, imageID;
        Message message;
        String outputAppend;

        public synchronized void handleMessage(Message msg) {
            int what = msg.what;
            playerNo = msg.arg1;
            holeNo = msg.arg2;
            imageID = getDrawableResource(playerNo);
            if (playerNo == 1) {
                message = player2Handler.obtainMessage(KNOW_SHOT);
                if (used2.contains(holeNo)) {
                    what = CATASTROPHE;
                }
            } else if (playerNo == 2) {
                message = player1Handler.obtainMessage(KNOW_SHOT);
                if (used1.contains(holeNo)) {
                    what = CATASTROPHE;
                }
            }

            if (stopTheGame) {
                return;
            } else {
                holeItems.get(holeNo).setImageId(imageID);
                holeAdapter.notifyDataSetChanged();
            }
            String text = "";
            switch (what) {
                case RANDOM:
                    break;

                case NEAR_MISS:
                    text = "NEAR MISS!";
                    message.obj = NEAR_MISS;
                    break;

                case NEAR_GROUP:
                    text= "NEAR GROUP!";
                    message.obj = NEAR_GROUP;
                    break;

                case BIG_MISS:
                    text = "BIG MISS!";
                    message.obj = BIG_MISS;
                    break;

                case JACKPOT:
                    text = "JACKPOT! Player "+msg.arg1+" won!";
                    if(msg.arg1 == 1) {
                        outputAppend = "Player 2 lost!";
                    }
                    else if(msg.arg1 == 2){
                        outputAppend = "Player 1 lost!";
                    }
                    stopTheGame = true;
                    int jackpotImageID = getJackpotDrawableResource(msg.arg1);
                    holeItems.get(holeNo).setImageId(jackpotImageID);
                    holeAdapter.notifyDataSetChanged();
                    break;

                case CATASTROPHE:
                    text = "CATASTROPHE! Player "+msg.arg1+" lost!";
                    if(msg.arg1 == 1) {
                        outputAppend = "Player 2 won!";
                    }
                    else if(msg.arg1 == 2){
                        outputAppend =  "Player 1 won!";
                    }
                    stopTheGame = true;
                    break;

            }

            if(msg.arg1 == 1){
                player1TextView.setText(player1TextView.getText()+"\n"+text + " Hole - "+holeNo);
            }
            else if(msg.arg1 == 2){
                player2TextView.setText(player2TextView.getText()+"\n"+text + " Hole - "+holeNo);
            }

            if(what == JACKPOT || what == CATASTROPHE){
                outcomeText1.setText(text);
                outcomeText2.setText(outputAppend);
                dialog.show();
            }
            Log.e("Player " + msg.arg1, " shot: " + msg.arg2 + " | " + what + " | " + text);



            if (!stopTheGame) {
                message.arg2 = holeNo;
                if (msg.arg1 == 1) {
                    message.arg1 = 1;
                    //

                    player1Handler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    //
                    player1Handler.sendMessage(message);
                } else if (msg.arg1 == 2) {
                    message.arg1 = 2;
                    player2Handler.sendMessage(message);
                }
            }


        }
    };


    public int getDrawableResource(int whichPlayer) {
        int imageID = 0;
        if (whichPlayer == 1) {
            imageID = R.drawable.player1;
        } else if (whichPlayer == 2) {
            imageID = R.drawable.player2;
        }
        return imageID;
    }

    public int getJackpotDrawableResource(int whichPlayer) {
        int imageID = 0;
        if (whichPlayer == 1) {
            // player1TextView.setText(holeNo);
            imageID = R.drawable.player1_jackpot;
        } else if (whichPlayer == 2) {
            // player2TextView.setText(holeNo);
            imageID = R.drawable.player2_jackpot;
        }
        return imageID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        startButton = findViewById(R.id.startButton);
        restartButton = findViewById(R.id.restartButton);
        player1TextView = findViewById(R.id.player1);
        player2TextView = findViewById(R.id.player2);

        player1ShotTextView = findViewById(R.id.player1Shot);
        player2ShotTextView = findViewById(R.id.player2Shot);

        holeItems = new ArrayList<>();

        holeImageID = R.drawable.hole;
        jackpotImageID = R.drawable.jackpot;
        ballImageID = R.drawable.ball;

        for (int i = 0; i < 50; i++) {
            hole_item = new Hole(holeImageID,i);
            holeItems.add(hole_item);
        }
        holeAdapter = new HoleAdapter(getApplicationContext(), R.layout.list_item, holeItems);
        listView.setAdapter(holeAdapter);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.GONE);

                //startButton.setText("Now Playing");

                Random jackpotSelector = new Random();
                jackpotHole = jackpotSelector.nextInt(50);
                // Note that nextInt(int max) returns an int between 0 inclusive and 50 exclusive.
                holeItems.get(jackpotHole).setImageId(jackpotImageID);
                holeAdapter.notifyDataSetChanged();
                Log.e("jackpotHole", jackpotHole + "");
                jackpotGroup = jackpotHole / 10;

                Log.e("jackpotGroup", jackpotGroup + "");

                Thread t1 = new Thread(new Player1());
                t1.start();

                Thread t2 = new Thread(new Player2());
                t2.start();
            }

        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating intent to restart activity
                Intent i = new Intent(MainActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });



        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.outcome_dialog);
        // Get the layout inflater
        outcomeText1 = dialog.findViewById(R.id.outcomeTextView1);
        outcomeText2 = dialog.findViewById(R.id.outcomeTextView2);
        okButton = dialog.findViewById(R.id.okButton);
        restart_button = dialog.findViewById(R.id.restart_button);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                restartButton.setVisibility(View.VISIBLE);
            }
        });

        restart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating intent to restart activity
                Intent i = new Intent(MainActivity.this,MainActivity.class);
                startActivity(i);
                finish();

            }
        });


    }

    private void restartActivity() {
            //creating intent to restart activity
            Intent i = new Intent(MainActivity.this,MainActivity.class);
            startActivity(i);
            finish();

    }


    class Player1 implements Runnable {

        Random random1 = new Random();

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();
            player1Handler = new Handler() {

                public void handleMessage(Message msg) {
                    if (stopTheGame) {
                        return;
                    } else {
                        int what = msg.what;
                        //Log.e("obj 1", msg.obj + "");
                        switch (what) {
                            case KNOW_SHOT:
                                //ex: jackpot hole = 26
                                // previous shot = 21
                                // if hole was found to be in Jackpot group.. improve shot to (24, 27]
                                if (msg.obj == (Integer) NEAR_MISS) {
                                    min = jackpotHole - 2;
                                    max = jackpotHole + 3;
                                }

                                //previous shot = 18
                                //if hole was in NEAR GROUP.. improve shot to (20,30] Jackpot group
                                else if (msg.obj == (Integer) NEAR_GROUP) {
                                    //improve to SAME_GROUP
                                    min = jackpotGroup * 10;
                                    max = (jackpotGroup + 1) * 10;
                                }

                                //previous shot = 45
                                //if hole was in BIG MISS groups.. improve shot to (10,40] NEAR GROUP
                                else if (msg.obj == (Integer) BIG_MISS) {
                                    min = (jackpotGroup - 1) * 10;
                                    max = (jackpotGroup + 2) * 10;
                                }
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                makeShotAndTellUI(1,min, max, random1, used1);

                                break;

                        }
                    }
                }

            };
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //INITIAL EXECUTION OF THREAD 2
            //HAPPENS ONLY ONCE!
            makeShotAndTellUI(1,min, max, random1, used1);

            Looper.loop();
        }


    }


    class Player2 implements Runnable {
        Random random2 = new Random();

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            Looper.prepare();
            player2Handler = new Handler() {

                public void handleMessage(Message msg) {
                    // Log.e("obj 2", msg.obj + "");
                    if (stopTheGame) {
                        return;
                    } else {
                        int what = msg.what;

                        switch (what) {

                            case KNOW_SHOT:
                                Object obj = new Object();
                                // if hole was found to be in Jackpot group.. improve shot to (24, 27]
                                if (msg.obj == (Integer) NEAR_MISS) {
                                    min = jackpotHole - 5;
                                    max = jackpotHole + 6;
                                }
                                else if (msg.obj == (Integer) NEAR_GROUP) {
                                    //previous shot = 18
                                    //if hole was in NEAR GROUP.. improve shot to (20,30] Jackpot group
                                    //Inverted with below (Different from Strategy 1)
                                    min = (jackpotGroup - 1) * 10;
                                    max = (jackpotGroup + 2) * 10;
                                } else if (msg.obj == (Integer) BIG_MISS) {

                                    //previous shot = 45
                                    //if hole was in BIG MISS groups.. improve shot to (10,40] NEAR GROUP
                                    //Inverted with above (Different from strategy 1)
                                    min = jackpotGroup * 10;
                                    max = (jackpotGroup + 1) * 10;
                                } else {
                                    //TARGET HOLE
                                    min = jackpotHole+1;
                                    max = jackpotHole-1;
                                }
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                               // makeShotAndTellUI_2();
                                makeShotAndTellUI(2,min, max, random2, used2);

                                break;
                        }
                    }
                }
            };

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //INITIAL EXECUTION OF THREAD 2
            //HAPPENS ONLY ONCE!
           // makeShotAndTellUI_2();
            makeShotAndTellUI(2, min, max, random2, used2);

            Looper.loop();
        }

        //min,max,random2,used2

    }

    public void makeShotAndTellUI(int whichPlayer, int min, int max, Random random, Set<Integer> used){
        Message msgToUI;
        int num = getUniqueRandom(min, max, random, used);
        //Log.e("Player 2", num + "");

        if (num == jackpotHole) {
            msgToUI = uiHandler.obtainMessage(JACKPOT);
        } else if ((num / 10) == jackpotGroup) {
            msgToUI = uiHandler.obtainMessage(NEAR_MISS);
        } else if ((num / 10) + 1 == jackpotGroup || (num / 10) - 1 == jackpotGroup) {
            msgToUI = uiHandler.obtainMessage(NEAR_GROUP);
        } else if ((num / 10) + 1 < jackpotGroup || (num / 10) - 1 > jackpotGroup) {
            msgToUI = uiHandler.obtainMessage(BIG_MISS);
        } else {
            msgToUI = uiHandler.obtainMessage(RANDOM);
        }

        msgToUI.arg1 = whichPlayer;
        msgToUI.arg2 = num;

        uiHandler.sendMessage(msgToUI);
    }


    public int getUniqueRandom(int min, int max, Random random, Set<Integer> used) {
        //Log.e("getUniqueRandom", "MIN: " + min + "MAX: " + max + "");
        if (min < 0) {
            min = minRange;
        }
        if (max > 50) {
            max = maxRange;
        }
        if (used.size() == 50) {
            return -1;
            /*** "cannot get more unique numbers than the size of the range"
             ***/
        } else {
            do {
                newRandom = random.nextInt(Math.abs(max - min)) + min;
            } while (used.contains(newRandom));

            used.add(newRandom);
            return newRandom;
        }
    }

}

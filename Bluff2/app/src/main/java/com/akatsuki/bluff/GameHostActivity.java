package com.akatsuki.bluff;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akatsuki.bluff.adapter.MyAdapter;
import com.akatsuki.bluff.network.server.ServerThread;
import com.akatsuki.bluff.util.Card;
import com.akatsuki.bluff.util.Player;

import java.util.ArrayList;
import java.util.List;

public class GameHostActivity extends Activity {
    final String TAG = "BhaiKo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        playerTextView[0] = (TextView) findViewById(R.id.textView3);
        playerTextView[1] = (TextView) findViewById(R.id.textView2);
        playerTextView[2] = (TextView) findViewById(R.id.textView);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(52);

        imageView = (ImageView) findViewById(R.id.imageView14);
        passNplay = (Button) findViewById(R.id.pass_play);
        checkButton = (Button) findViewById(R.id.check_button);

        hostName = getIntent().getStringExtra("inGameName");
        self = new Player(hostName, 0);
        game = getIntent().getStringExtra("gameName");
        playersNumber = getIntent().getIntExtra("playersNumber", 4);

        sp = (Spinner) findViewById(R.id.spinner);
        sp.setPromptId(R.string.kya_bolta_h);
        sp.setAlpha(.25f);
        sp.setEnabled(false);
        sp.setSelection(0);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyAdapter(getApplicationContext(), sp, passNplay);
        mRecyclerView.setAdapter(mAdapter);


        passNplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + chance + " " + self.getPosition());
                if (self.getPosition() == chance) {
                    selectedCards = mAdapter.getSelectedCards();
                    for (Card card : selectedCards) {
                        removeCard(card);
                    }
                    setImage(mAdapter.selectedLadke);

                    if (firstChance == self.getPosition()) {
                        setKyaBola(sp.getSelectedItem().toString());
                        serverThread.hostPlayedCard(selectedCards, sp.getSelectedItemPosition());
                    }

                    serverThread.hostPlayedCard(selectedCards);
                    mAdapter.setSelectedCards(new ArrayList<Card>());
                    mAdapter.setSelectedLadke(0);

                } else {
                    Toast.makeText(getApplicationContext(), "Not your turn", Toast.LENGTH_SHORT).show();
                }
            }

        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Bundle cardBundle = msg.getData();
                        Player obtainPlayer = (Player) cardBundle.get("card");
                        playerTextView[obtainPlayer.getPosition() - 1].setText(obtainPlayer.getPlayerName());
                        break;
                    case 2:
                        final Card card = msg.getData().getParcelable("card");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mAdapter.getItemCount() > 1) {
                                    mAdapter.add(1, card);
                                } else {
                                    mAdapter.add(0, card);
                                }
                                //addCard(card);
                            }
                        });
                        break;
                    case 3:
                        chance = msg.arg1;
                        firstChance = msg.arg2;
                        Log.d(TAG, "handleMessage: " + chance +" " + firstChance);
                        break;
                    case 4:
                        final Message msg1 = msg;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setImage(msg1.arg1);
                                setKyaBola(Card.rankToString(msg1.arg2));
                            }
                        });
                        break;
                }
            }
        };

        serverThread = new ServerThread(getApplicationContext(), handler);
        serverThread.players((playersNumber));
        serverThread.initializePlayer(self);
        server = new Thread(serverThread);
        server.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Bhia", "onDestroy() called");
        serverThread.socketClose();
    }

    private void setKyaBola(String s) {
        TextView txt = (TextView) findViewById(R.id.kya_bola);
        txt.setText(s);
    }

    private void setImage(int selectedLadke) {

        switch (selectedLadke) {
            case 1:
                imageView.setImageResource(R.drawable.one);
                break;
            case 2:
                imageView.setImageResource(R.drawable.two);
                break;
            case 3:
                imageView.setImageResource(R.drawable.three);
                break;
            case 4:
                imageView.setImageResource(R.drawable.four);
                break;
        }
    }

    public void check(View view) {
        serverThread.checkBluff(0);
    }

    //public void addCard(Card card) {
     //   cardSet.add(card);
     //   mAdapter.setCards(cardSet);
     //   mAdapter.notifyDataSetChanged();
    //}

    public void removeCard(Card card) {
        mAdapter.remove(card);
        passNplay.setText("Pass");
        sp.setEnabled(false);
    }

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ImageView imageView;
    Spinner sp;
    Button passNplay;
    Button checkButton;
    TextView[] playerTextView = new TextView[3];

    ServerThread serverThread;
    Thread server;
    static Handler handler;
    //List<Card> cardSet;
    String game;
    String hostName;
    List<Card> selectedCards = new ArrayList<>(4);
    int playersNumber;
    Player self;
    private int chance;
    private int firstChance;
}

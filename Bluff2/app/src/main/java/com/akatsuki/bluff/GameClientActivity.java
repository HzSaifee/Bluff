package com.akatsuki.bluff;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akatsuki.bluff.adapter.MyAdapter;
import com.akatsuki.bluff.network.client.ClientThread;
import com.akatsuki.bluff.util.Card;
import com.akatsuki.bluff.util.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameClientActivity extends Activity {
    final String TAG = "BhaiKo";

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    JSONObject jo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        playerTextView[0] = (TextView) findViewById(R.id.textView);
        playerTextView[1] = (TextView) findViewById(R.id.textView2);
        playerTextView[2] = (TextView) findViewById(R.id.textView3);

        self = new Player(getIntent().getStringExtra("name"), 0);

        cardSet = new ArrayList<>();
        cardSet.addAll(Arrays.asList(myDataset));

        passNplay = (Button) findViewById(R.id.pass_play);
        sp = (Spinner) findViewById(R.id.spinner);
        sp.setPromptId(R.string.kya_bolta_h);
        sp.setAlpha(.25f);
        sp.setEnabled(false);
        sp.setSelection(0);
        checkButton = (Button) findViewById(R.id.check_button);
        imageView = (ImageView) findViewById(R.id.imageView14);
        textView = (TextView) findViewById(R.id.kya_bola);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(52);

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
                    if (selectedCards.isEmpty()) {
                        try {
                            jo = new JSONObject();
                            jo.put("Action", "Pass");
                        } catch (JSONException ex) {
                            Log.d("bhai", "fat gayi json ki");
                        }

                        clientThread.trial(jo.toString());
                    } else {
                        jo = new JSONObject();
                        JSONArray jsonArray = new JSONArray();

                        try {
                            jo = new JSONObject();
                            jo.put("Action", "Play");
                            jo.put("Spinner", -1);
                            if (firstChance == self.getPosition())
                                jo.put("Spinner", sp.getSelectedItemPosition());
                        } catch (JSONException ex) {
                            Log.d("bhai", "fat gayi json ki");
                        }
                        int i = 0;
                        selectedCards = mAdapter.getSelectedCards();

                        for (Card card : selectedCards) {
                            try {
                                jsonArray.put(i, card);
                                i++;
                            } catch (JSONException ex) {
                                Log.d("bhai", "fat gayi json ki");
                            }
                            removeCard(card);
                        }

                        try {
                            jo.put("Cards", jsonArray);
                        } catch (JSONException jsonException) {
                            Log.d("bhai", "fat gayi json ki");
                        }

                        mAdapter.setSelectedCards(new ArrayList<Card>());
                        mAdapter.setSelectedLadke(0);

                        clientThread.trial(jo.toString());
                    }
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
                        final Bundle playerData = msg.getData();
                        if (isMe) {
                            self = new Player(playerData.getString("name"), playerData.getInt("position"));
                            pos = self.getPosition();
                            isMe = false;
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    nextPlayerPos--;
                                    playerTextView[nextPlayerPos].setText(playerData.getString("name"));
                                }
                            });
                            Toast.makeText(getApplicationContext(), "Player added", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        final Card card = msg.getData().getParcelable("card");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addCard(card);
                            }
                        });
                        break;
                    case 3:
                        final Bundle bundle = msg.getData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setImage(bundle.getInt("number"));
                                textView.setText(Card.rankToString(bundle.getInt("supposed")));
                            }
                        });
                        break;
                    case 4:
                        chance = msg.arg1;
                        firstChance = chance;
                        Log.d(TAG, "handleMessage: " + chance + " " + firstChance);
                        break;
                    case 5:
                        chance = msg.arg1;
                        firstChance = -1;
                        break;
                    case 6:
                        final Bundle data = msg.getData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pos--;
                                Log.d("Bhai", pos+" par Host" );
                                playerTextView[pos].setText(data.getString("name"));
                            }
                        });
                        break;
                    /*case 4:
                        Bundle cardBundle = msg.getData();
                        Player obtainPlayer = (Player) cardBundle.get("card");
                        playerTextView[obtainPlayer.getPosition() - 1].setText(obtainPlayer.getPlayerName());
                        Toast.makeText(getApplicationContext(), "Player added" + obtainPlayer, Toast.LENGTH_SHORT).show();
                        break;*/
                }
            }
        };
        clientThread = new ClientThread(getIntent().getStringExtra("IP"), self, getApplicationContext(), handler);
        client = new Thread(clientThread);
        client.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Bhia", "onDestroy() called");
        clientThread.socketRelease();
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
        if (self.getPosition() == chance) {
            try {
                jo = new JSONObject();
                jo.put("Action", "Check");
            } catch (JSONException ex) {
                Log.d("bhai", "fat gayi json ki");
            }
            clientThread.trial(jo.toString());
        }
        else{
            Toast.makeText(getApplicationContext(),"Not your turn",Toast.LENGTH_SHORT).show();
        }
    }

    public void addCard(Card card) {
        cardSet.add(card);
        mAdapter.setCards(cardSet);
        mAdapter.notifyDataSetChanged();
    }

    public void removeCard(Card card) {
        cardSet.remove(card);
        mAdapter.setCards(cardSet);
        mAdapter.notifyDataSetChanged();
        passNplay.setText("Pass");
        sp.setEnabled(false);
    }

    Card[] myDataset = {};
    Player self;
    int chance;
    int firstChance;
    int pos;
    int nextPlayerPos = 3;
    boolean isMe = true;
    ClientThread clientThread;
    Handler handler;
    List<Card> cardSet;
    List<Card> selectedCards = new ArrayList<>(4);
    Thread client;
    TextView[] playerTextView = new TextView[3];

    Spinner sp;
    Button passNplay;
    Button checkButton;
    ImageView imageView;
    TextView textView;
}
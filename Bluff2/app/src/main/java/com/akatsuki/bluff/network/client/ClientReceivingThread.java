package com.akatsuki.bluff.network.client;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.akatsuki.bluff.util.Card;
import com.akatsuki.bluff.util.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientReceivingThread implements Runnable {

    final String TAG = "BhaiKo";

    @Override
    public void run() {
        Looper.prepare();
        while (!player.getSocket().isClosed()) {
            try {
                final String str = fromServer.readUTF();
                kyaHua(new JSONObject(str));
            } catch (IOException exception) {
                // Log.d("Bhai", "padhne me dikkat");
            } catch (JSONException ex) {

            }
        }

    }

    private void kyaHua(final JSONObject jsonObject) {
        try {
            Log.d(TAG, "kyaHua: " + jsonObject.toString());
            String str = jsonObject.getString("Action");

            if (str.equals("PreviousPlayerNames")) {
                String playerName = (String) jsonObject.get("PlayerName");
                int playerPosition = jsonObject.getInt("PlayerPosition");
                clientThread.addPlayer(playerName, playerPosition);
            }

            if (str.equals("Player Connected")) {
                String playerName = (String) jsonObject.get("Player");
                int position = jsonObject.getInt("Position");
                clientThread.initializePlayer(playerName, position);
            }

            if (str.equals("AddCards")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Cards");
                int len = jsonArray.length();
                Log.d(TAG, len + "");
                int i = 0;
                while (i < len) {
                    final Card card = Card.parseCard(jsonArray.get(i).toString());
                    clientThread.add(card);
                    i++;
                }
            }

            if (str.equals("Played")) {
                clientThread.setChanges(jsonObject.getInt("NumberOfCards"), jsonObject.getInt("Supposed"));
            }

            if (str.equals("Set Chance")) {
                clientThread.setChance(jsonObject.getInt("Chance"), jsonObject.getBoolean("isFirstChance"));
                final int chance = jsonObject.getInt("Chance");
                final String name = jsonObject.getString("Kiski");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, chance + " " + name, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (JSONException ex) {
            Log.d("bhai", "chaddi fat gayi");
            Log.e("bhai", "kyaHua: ", ex);
        }
    }

    public ClientReceivingThread(Player player, Context context, ClientThread clientThread) {
        this.player = player;
        this.context = context;
        this.clientThread = clientThread;
        try {
            fromServer = new DataInputStream(player.getSocket().getInputStream());
            dataOutputStream = new DataOutputStream(player.getSocket().getOutputStream());
        } catch (IOException exception) {
            Log.d("Bhai", "Thread ni ban pa raha hai");
        }
    }

    Context context;
    Player player;
    DataInputStream fromServer;
    DataOutputStream dataOutputStream;
    ClientThread clientThread;
}
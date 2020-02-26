package com.akatsuki.bluff.network.server;

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

public class ServerReceivingThread implements Runnable {
    final String TAG = "BhaiKo";

    public void run() {
        Looper.prepare();
        while (!player.getSocket().isClosed()) {
            try {
                final String str = fromServer.readUTF();
                JSONObject jo = new JSONObject(str);
                Log.d(TAG, "run: "+jo);
                kyaHua(jo);
            } catch (IOException exception) {
                Log.d("Bhai", "padhne me dikkat");
            } catch (JSONException exception) {
                Log.d("Bhai", "kuch gadbad");
            }
        }
        Log.d(TAG, "run: kuch hua");
    }

    private void kyaHua(JSONObject jsonObject) {
        try {
            String str = jsonObject.getString("Action");
            if (str.equals("Pass")) {

                serverThread.clientPassed();

            } else if (str.equals("Check")) {
                serverThread.checkBluff(player.getPosition());
            } else if (str.equals("Play")) {
                List<Card> lastCards = new ArrayList<>(4);
                int sp = jsonObject.getInt("Spinner");
                if (sp != -1)
                    serverThread.serverData.setLastRank(sp);
                JSONArray jsonArray = jsonObject.getJSONArray("Cards");
                int len = jsonArray.length();
                int i = 0;
                while (i < len) {
                    final Card card = Card.parseCard((String) jsonArray.get(i));
                    lastCards.add(card);
                    i++;
                }
                serverThread.setPlayedCard(lastCards , serverThread.serverData.getLastRank() , player.getPosition());
            }
        } catch (JSONException ex) {

        }

    }

    public ServerReceivingThread(Player player, Context context, ServerThread serverThread) {
        this.player = player;
        this.context = context;
        this.serverThread = serverThread;
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
    ServerThread serverThread;
}

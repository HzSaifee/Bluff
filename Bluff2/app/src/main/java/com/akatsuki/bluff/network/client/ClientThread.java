package com.akatsuki.bluff.network.client;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.akatsuki.bluff.util.Card;
import com.akatsuki.bluff.util.Player;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread implements Runnable {

    public ClientThread(String ipAddress, Player player, Context context, Handler handler) {
        this.ipAddress = ipAddress;
        this.player = player;
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "connection started", Toast.LENGTH_SHORT).show();
            }
        });
        try {
            socket = new Socket(ipAddress, port);

            outToServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new DataInputStream(socket.getInputStream());

            outToServer.writeUTF(player.getPlayerName());
            player.setSocket(socket);

        } catch (IOException exception) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Not connected", Toast.LENGTH_SHORT).show();
                }
            });
        }
        Thread hii = new Thread(new ClientReceivingThread(player, context, this));
        hii.start();
    }

    public void socketRelease(){
        try {
            socket.close();
            Log.d("Bhia","SocketReleased");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trial(String str) {
        try {
            outToServer.writeUTF(str);
            outToServer.flush();
        } catch (IOException ex) {
            Log.d("bhai", "Gadbad in Trial");
        }

    }

    public void add(Card card) {
        Message msg = Message.obtain(handler, 2);
        Bundle bundle = new Bundle();
        bundle.putParcelable("card", card);
        msg.setData(bundle);
        handler.dispatchMessage(msg);
    }

    public void addPlayer(String playerName, int position) {
        Message msg = Message.obtain(handler, 6);

        Bundle data = new Bundle();
        data.putString("name", playerName);
        data.putInt("Position", position);
        msg.setData(data);

        handler.dispatchMessage(msg);
    }

    public void setChanges(int numberOfCard, int supposed) {
        Message message = handler.obtainMessage(3);

        Bundle bundle = new Bundle();
        bundle.putInt("number" , numberOfCard);
        bundle.putInt("supposed" , supposed);

        message.setData(bundle);
        handler.dispatchMessage(message);
    }

    Player player;
    String ipAddress;
    int port = 27000;
    Socket socket;
    Context context;
    DataOutputStream outToServer;
    DataInputStream fromServer;
    Handler handler;

    public void initializePlayer(String playerName, int position) {
        Message msg = handler.obtainMessage(1);

        Bundle data = new Bundle();
        data.putString("name" , playerName);
        data.putInt("position" , position);
        msg.setData(data);

        handler.dispatchMessage(msg);
    }

    public void setChance(int chance , boolean firstChance){
        if (firstChance){
            handler.dispatchMessage(handler.obtainMessage(4 , chance , 0));
        }
        else{
            handler.dispatchMessage(handler.obtainMessage(5 , chance , 0));
        }

    }
}

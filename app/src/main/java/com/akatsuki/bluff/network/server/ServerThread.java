package com.akatsuki.bluff.network.server;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.akatsuki.bluff.R;
import com.akatsuki.bluff.util.Card;
import com.akatsuki.bluff.util.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ServerThread implements Runnable {

    final String TAG = "Server Thread Bhai";
    JSONObject job[] = new JSONObject[4];

    public ServerThread(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        serverData = new ServerData();
        players.add(0, new Player("Host", serverData.HOST));
        Looper.prepare();
        try {
            serverSocket = new ServerSocket(27000);
            while (i < playersNumber - 1) {
                Socket socket = serverSocket.accept();
                socketList.add(socket);

                final String name = new DataInputStream(socket.getInputStream()).readUTF();

                Player player = new Player(name, i + 1);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                player.setSocket(socket);

                players.add(player);

                for (int j = 0; j < i+1; ++j)
                    dos.writeUTF(job[j].toString());

                job[i + 1] = new JSONObject();
                job[i + 1].put("Action", "PreviousPlayerNames");
                job[i + 1].put("PlayerName", player.getPlayerName());
                job[i + 1].put("PlayerPosition", player.getPosition());

                dos.writeUTF("Connected");

                Message msg = handler.obtainMessage(1);
                Bundle bundle = new Bundle();
                bundle.putParcelable("card", player);
                msg.setData(bundle);

                handler.sendMessage(msg);

                JSONObject jo = new JSONObject();
                try {
                    jo.put("Action", "Player Connected");
                    jo.put("Player", player.getPlayerName());
                    jo.put("Position", i + 1);
                } catch (JSONException e) {

                }
                broadCast(jo);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
                    }
                });

                Thread hello = new Thread(new ServerReceivingThread(player, context, this));
                hello.start();
                i++;
            }
        } catch (IOException exception) {
            Log.d("bhai", "Gadbad in server thread");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (i < playersNumber)
            deal(playersNumber);
    }

    public void socketClose() {
        try {
            serverSocket.close();
            Log.d("Bhia", "Socket is Closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void players(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    public void initializePlayer(Player player){
        job[0] = new JSONObject();
        try {
            job[0].put("Action", "PreviousPlayerNames");
            job[0].put("PlayerName", player.getPlayerName());
            job[0].put("PlayerPosition", player.getPosition());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendTo(int bluffedBy, int chance) {
        serverData.setChance(chance);
        if (bluffedBy == serverData.HOST) {
            List<Card> cards = serverData.getCardsOnTable();
            for (Card card : cards) {
                add(card);
            }
            serverData.removeFromTable();
            JSONObject jsonObject;
            jsonObject = new JSONObject();
            try {
                jsonObject.put("Action", "Set Chance");
                jsonObject.put("Chance", chance);
                jsonObject.put("isFirstChance", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.dispatchMessage(handler.obtainMessage(3, chance, -1));
            broadCast(jsonObject);
        } else {
            Player player = players.get(bluffedBy);
            try {
                DataOutputStream dos = new DataOutputStream(player.getSocket().getOutputStream());
                List<Card> cards = serverData.getCardsOnTable();
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < cards.size(); i++) {
                    jsonArray.put(i, cards.get(i).toString());
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Action", "AddCards");
                jsonObject.put("Cards", jsonArray);
                dos.writeUTF(jsonObject.toString());
                serverData.removeFromTable();

                jsonObject = new JSONObject();
                jsonObject.put("Action", "Set Chance");
                jsonObject.put("Chance", chance);
                jsonObject.put("isFirstChance", true);
                broadCast(jsonObject);
                handler.dispatchMessage(handler.obtainMessage(3, chance, 0));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

        }
    }

    public void broadCast(JSONObject obj) {
        DataOutputStream dos;
        int i = 0;
        for (Player player : players) {
            if (i == 0) {
                i++;
                continue;
            }
            try {
                dos = new DataOutputStream(player.getSocket().getOutputStream());
                dos.writeUTF(obj.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deal(int playersNumber) {
        int j;
        if (playersNumber == 2)
            j = 26;
        else if (playersNumber == 3) {
            j = 17;
        } else {
            j = 13;
        }
        for (int i = 0; i < j; ++i) {
            add(serverData.deck.get(i));
            handler.dispatchMessage(handler.obtainMessage(3, serverData.PLAYER_1, -1));
        }
        JSONObject jo = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 1; i < playersNumber; ++i) {
            for (int t = 0; t < j; ++t) {
                try {
                    Log.d("Bhaiko", " " + (t + (j * (i))));
                    jsonArray.put(t, serverData.deck.get(t + (j * (i))));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Bhaiko", "GadBad");
                }
            }
            try {
                jo.put("Cards", jsonArray);
                jo.put("Action", "AddCards");

                DataOutputStream dos = new DataOutputStream(players.get(i).getSocket().getOutputStream());
                dos.writeUTF(jo.toString());

                jo = new JSONObject();
                jo.put("Action", "Set Chance");
                jo.put("Chance", serverData.PLAYER_1);
                jo.put("isFirstChance", true);
                dos.writeUTF(jo.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkBluff(int checkedBy) {
        List<Card> c = serverData.getLastCardsPlayed();
        boolean bluff = false;
        for (int i = 0; i < c.size(); i++) {
            if (c.get(i).getRank() != serverData.getLastRank()) {
                bluff = true;
                break;
            }
        }
        if (bluff) {
            sendTo(serverData.getLastPlayedBy(), checkedBy);
        } else {
            sendTo(checkedBy, serverData.getLastPlayedBy());
        }
    }

    public void add(Card card) {
        Message msg = Message.obtain(handler, 2);
        Bundle bundle = new Bundle();
        bundle.putParcelable("card", card);
        msg.setData(bundle);
        handler.dispatchMessage(msg);
    }

    public void hostPlayedCard(List<Card> cards, int rank) {

        serverData.setLastCardsPlayed(cards);
        serverData.setLastRank(rank);
        serverData.setLastPlayedBy(serverData.HOST);
        serverData.setNextChance();

        JSONObject jo = new JSONObject();
        try {
            jo.put("Action", "Played");
            jo.put("NumberOfCards", cards.size());
            jo.put("Supposed", serverData.getLastRank());

            broadCast(jo);
            serverData.setNextChance();
            Log.d(TAG, "hostPlayedCard: " + serverData.getChance());

            jo = new JSONObject();
            jo.put("Action", "Set Chance");
            jo.put("Chance", serverData.getChance());
            jo.put("isFirstChance", false);
            broadCast(jo);
            handler.dispatchMessage(handler.obtainMessage(3, serverData.getChance(), -1));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void hostPlayedCard(List<Card> cards) {

        serverData.setLastCardsPlayed(cards);
        serverData.setLastPlayedBy(serverData.HOST);

        JSONObject jo = new JSONObject();
        try {
            jo.put("Action", "Played");
            jo.put("NumberOfCards", cards.size());
            jo.put("Supposed", serverData.getLastRank());

            broadCast(jo);
            serverData.setNextChance();
            Log.d(TAG, "hostPlayedCard: " + serverData.getChance());

            jo = new JSONObject();
            jo.put("Action", "Set Chance");
            jo.put("Chance", serverData.getChance());
            jo.put("isFirstChance", false);
            broadCast(jo);
            handler.dispatchMessage(handler.obtainMessage(3, serverData.getChance(), -1));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPlayedCard(List<Card> cards, int rank, int playerPosition) {
        handler.dispatchMessage(handler.obtainMessage(4, cards.size(), rank));

        serverData.setLastCardsPlayed(cards);
        serverData.setLastRank(rank);
        serverData.setLastPlayedBy(playerPosition);

        JSONObject jo = new JSONObject();
        try {
            jo.put("Action", "Played");
            jo.put("NumberOfCards", cards.size());
            jo.put("Supposed", serverData.getLastRank());

            broadCast(jo);
            serverData.setNextChance();
            Log.d(TAG, "setPlayedCard: " + serverData.getChance());

            jo = new JSONObject();
            jo.put("Action", "Set Chance");
            jo.put("Chance", serverData.getChance());
            jo.put("isFirstChance", false);
            broadCast(jo);
            handler.dispatchMessage(handler.obtainMessage(3, serverData.getChance(), -1));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clientPassed() {
        serverData.noOfPasses++;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Action", "Set Chance");
            jsonObject.put("Chance", serverData.setNextChance());
            jsonObject.put("Kiski", players.get(serverData.getChance()));
            jsonObject.put("isFirstChance", false);
        } catch (JSONException e) {
            Log.e(TAG, "clientPassed: ", e);
        }
        broadCast(jsonObject);
        if (serverData.getChance() == serverData.HOST) {
            handler.dispatchMessage(handler.obtainMessage(3, 0, -1));
        }
    }

    public ServerData serverData;
    ServerSocket serverSocket;

    int i = 0;
    int playersNumber = 1;

    List<Player> players = new ArrayList<>(4);
    List<Socket> socketList = new ArrayList<>(3);

    Context context;
    Handler handler;


    class ServerData {
        final int HOST = 0;
        final int PLAYER_1 = 1;
        final int PLAYER_2 = 2;
        final int PLAYER_3 = 3;

        LinkedList<Integer> l = new LinkedList<>();

        Card[] cards = {
                new Card(Card.ACE, Card.SPADES, R.drawable.ace_of_spades),
                new Card(Card.ACE, Card.CLUBS, R.drawable.ace_of_clubs),
                new Card(Card.ACE, Card.DIAMONDS, R.drawable.ace_of_diamonds),
                new Card(Card.ACE, Card.HEARTS, R.drawable.ace_of_hearts),
                new Card(Card.DEUCE, Card.SPADES, R.drawable.deuce_of_spades),
                new Card(Card.DEUCE, Card.CLUBS, R.drawable.deuce_of_clubs),
                new Card(Card.DEUCE, Card.DIAMONDS, R.drawable.deuce_of_diamonds),
                new Card(Card.DEUCE, Card.HEARTS, R.drawable.deuce_of_hearts),
                new Card(Card.THREE, Card.SPADES, R.drawable.three_of_spades),
                new Card(Card.THREE, Card.CLUBS, R.drawable.three_of_clubs),
                new Card(Card.THREE, Card.DIAMONDS, R.drawable.three_of_diamonds),
                new Card(Card.THREE, Card.HEARTS, R.drawable.three_of_hearts),
                new Card(Card.FOUR, Card.SPADES, R.drawable.four_of_spades),
                new Card(Card.FOUR, Card.CLUBS, R.drawable.four_of_clubs),
                new Card(Card.FOUR, Card.DIAMONDS, R.drawable.four_of_diamonds),
                new Card(Card.FOUR, Card.HEARTS, R.drawable.four_of_hearts),
                new Card(Card.FIVE, Card.SPADES, R.drawable.five_of_spades),
                new Card(Card.FIVE, Card.CLUBS, R.drawable.five_of_clubs),
                new Card(Card.FIVE, Card.DIAMONDS, R.drawable.five_of_diamonds),
                new Card(Card.FIVE, Card.HEARTS, R.drawable.five_of_hearts),
                new Card(Card.SIX, Card.SPADES, R.drawable.six_of_spades),
                new Card(Card.SIX, Card.CLUBS, R.drawable.six_of_clubs),
                new Card(Card.SIX, Card.DIAMONDS, R.drawable.six_of_diamonds),
                new Card(Card.SIX, Card.HEARTS, R.drawable.six_of_hearts),
                new Card(Card.SEVEN, Card.SPADES, R.drawable.seven_of_spades),
                new Card(Card.SEVEN, Card.CLUBS, R.drawable.seven_of_clubs),
                new Card(Card.SEVEN, Card.DIAMONDS, R.drawable.seven_of_diamonds),
                new Card(Card.SEVEN, Card.HEARTS, R.drawable.seven_of_hearts),
                new Card(Card.EIGHT, Card.SPADES, R.drawable.eight_of_spades),
                new Card(Card.EIGHT, Card.CLUBS, R.drawable.eight_of_clubs),
                new Card(Card.EIGHT, Card.DIAMONDS, R.drawable.eight_of_diamonds),
                new Card(Card.EIGHT, Card.HEARTS, R.drawable.eight_of_hearts),
                new Card(Card.NINE, Card.SPADES, R.drawable.nine_of_spades),
                new Card(Card.NINE, Card.CLUBS, R.drawable.nine_of_clubs),
                new Card(Card.NINE, Card.DIAMONDS, R.drawable.nine_of_diamonds),
                new Card(Card.NINE, Card.HEARTS, R.drawable.nine_of_hearts),
                new Card(Card.TEN, Card.SPADES, R.drawable.ten_of_spades),
                new Card(Card.TEN, Card.CLUBS, R.drawable.ten_of_clubs),
                new Card(Card.TEN, Card.DIAMONDS, R.drawable.ten_of_diamonds),
                new Card(Card.TEN, Card.HEARTS, R.drawable.ten_of_hearts),
                new Card(Card.JACK, Card.SPADES, R.drawable.jack_of_spades),
                new Card(Card.JACK, Card.CLUBS, R.drawable.jack_of_clubs),
                new Card(Card.JACK, Card.DIAMONDS, R.drawable.jack_of_diamonds),
                new Card(Card.JACK, Card.HEARTS, R.drawable.jack_of_hearts),
                new Card(Card.QUEEN, Card.SPADES, R.drawable.queen_of_spades),
                new Card(Card.QUEEN, Card.CLUBS, R.drawable.queen_of_clubs),
                new Card(Card.QUEEN, Card.DIAMONDS, R.drawable.queen_of_diamonds),
                new Card(Card.QUEEN, Card.HEARTS, R.drawable.queen_of_hearts),
                new Card(Card.KING, Card.SPADES, R.drawable.king_of_spades),
                new Card(Card.KING, Card.CLUBS, R.drawable.king_of_clubs),
                new Card(Card.KING, Card.DIAMONDS, R.drawable.king_of_diamonds),
                new Card(Card.KING, Card.HEARTS, R.drawable.king_of_hearts)
        };
        List<Card> deck = new ArrayList<>();

        public int getLastRank() {
            return lastRank;
        }

        public void setLastRank(int lastRank) {
            this.lastRank = lastRank;
        }

        public int getChance() {
            return chance;
        }

        public int setNextChance() {
            if (playersNumber == 2) {
                switch (chance) {
                    case HOST:
                        setChance(PLAYER_1);
                        break;
                    case PLAYER_1:
                        setChance(HOST);
                        break;
                }
            } else if (playersNumber == 3) {
                switch (chance) {
                    case HOST:
                        setChance(PLAYER_1);
                        break;
                    case PLAYER_1:
                        setChance(PLAYER_2);
                        break;
                    case PLAYER_2:
                        setChance(HOST);
                        break;
                }
            } else if (playersNumber == 4) {
                switch (chance) {
                    case HOST:
                        setChance(PLAYER_1);
                        break;
                    case PLAYER_1:
                        setChance(PLAYER_2);
                        break;
                    case PLAYER_2:
                        setChance(PLAYER_3);
                        break;
                    case PLAYER_3:
                        setChance(HOST);
                        break;
                }
            }

            return chance;
        }

        public void setChance(int chance) {
            this.chance = chance;
        }

        public void removeFromTable() {
            cardsOnTable.clear();
        }

        ServerData() {
            deck.addAll(Arrays.asList(cards));
            Collections.shuffle(deck);
            chance = 1;
            lastRank = -1;
            noOfPasses = 0;

        }

        public List<Card> getCardsOnTable() {
            return cardsOnTable;
        }

        public void setCardsOnTable(List<Card> cardsOnTable) {
            this.cardsOnTable = cardsOnTable;
        }

        public List<Card> getLastCardsPlayed() {
            return lastCardsPlayed;
        }

        public void setLastCardsPlayed(List<Card> lastCardsPlayed) {
            this.lastCardsPlayed = lastCardsPlayed;
            List<Card> list = getCardsOnTable();
            list.addAll(lastCardsPlayed);
            setCardsOnTable(list);
        }

        public int getLastPlayedBy() {
            return lastPlayedBy;
        }

        public void setLastPlayedBy(int lastPlayedBy) {
            this.lastPlayedBy = lastPlayedBy;
        }

        private List<Player> players = new ArrayList<>(3);
        private List<Card> lastCardsPlayed = new ArrayList<>(4);
        private List<Card> cardsOnTable = new ArrayList<>(52);

        private int chance;
        private int lastRank;
        private int lastPlayedBy;
        private int noOfPasses;
    }
}
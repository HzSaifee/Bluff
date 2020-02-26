package com.akatsuki.bluff.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.Socket;

public class Player implements Parcelable {

    final static int FIRST_PLAYER = 1;
    final static int SECOND_PLAYER = 2;
    final static int THIRD_PLAYER = 3;

    public Player(String playerName, int position) {
        this.playerName = playerName;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getPlayerName() {
        return playerName;
    }

    protected Player(Parcel in) {
        playerName = in.readString();
        position = in.readInt();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public String toString() {
        return playerName + "at position " + position;
    }

    String playerName;
    int position;
    Socket socket;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(FIRST_PLAYER);
        dest.writeInt(SECOND_PLAYER);
        dest.writeInt(THIRD_PLAYER);
        dest.writeString(playerName);
        dest.writeInt(position);
    }
}

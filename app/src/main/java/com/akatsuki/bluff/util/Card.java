package com.akatsuki.bluff.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.akatsuki.bluff.R;

public class Card implements Parcelable {
    private final int rank;
    private final int suit;
    private final int cardImage;

    // Kinds of suits
    public final static int DIAMONDS = 1;
    public final static int CLUBS = 2;
    public final static int HEARTS = 3;
    public final static int SPADES = 4;

    // Kinds of ranks
    public final static int ACE = 0;
    public final static int DEUCE = 1;
    public final static int THREE = 2;
    public final static int FOUR = 3;
    public final static int FIVE = 4;
    public final static int SIX = 5;
    public final static int SEVEN = 6;
    public final static int EIGHT = 7;
    public final static int NINE = 8;
    public final static int TEN = 9;
    public final static int JACK = 10;
    public final static int QUEEN = 11;
    public final static int KING = 12;

    public Card(int rank, int suit, int cardImage) {
        this.rank = rank;
        this.suit = suit;
        this.cardImage = cardImage;
    }

    protected Card(Parcel in) {
        rank = in.readInt();
        suit = in.readInt();
        cardImage = in.readInt();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public int getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    public static boolean isValidRank(int rank) {
        return ACE <= rank && rank <= KING;
    }

    public static boolean isValidSuit(int suit) {
        return DIAMONDS <= suit && suit <= SPADES;
    }

    public int getCardImage() {
        return cardImage;
    }

    public static String rankToString(int rank) {
        switch (rank) {
            case ACE:
                return "ace";
            case DEUCE:
                return "deuce";
            case THREE:
                return "three";
            case FOUR:
                return "four";
            case FIVE:
                return "five";
            case SIX:
                return "six";
            case SEVEN:
                return "seven";
            case EIGHT:
                return "eight";
            case NINE:
                return "nine";
            case TEN:
                return "ten";
            case JACK:
                return "jack";
            case QUEEN:
                return "queen";
            case KING:
                return "king";
            default:
                //Handle an illegal argument.  There are generally two
                //ways to handle invalid arguments, throwing an exception
                //(see the section on Handling Exceptions) or return null
                return null;
        }
    }

    public static String suitToString(int suit) {
        switch (suit) {
            case DIAMONDS:
                return "diamonds";
            case CLUBS:
                return "clubs";
            case HEARTS:
                return "hearts";
            case SPADES:
                return "spades";
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return rankToString(rank) + "_of_" + suitToString(suit);
    }

    public static Card parseCard(String str) {
        switch (str) {
            case "ace_of_spades":
                return new Card(Card.ACE, Card.SPADES, R.drawable.ace_of_spades);
            case "ace_of_hearts":
                return new Card(Card.ACE, Card.HEARTS, R.drawable.ace_of_hearts);
            case "ace_of_clubs":
                return new Card(Card.ACE, Card.CLUBS, R.drawable.ace_of_clubs);
            case "ace_of_diamonds":
                return new Card(Card.ACE, Card.DIAMONDS, R.drawable.ace_of_diamonds);
            case "deuce_of_spades":
                return new Card(Card.DEUCE, Card.SPADES, R.drawable.deuce_of_spades);
            case "deuce_of_hearts":
                return new Card(Card.DEUCE, Card.HEARTS, R.drawable.deuce_of_hearts);
            case "deuce_of_clubs":
                return new Card(Card.DEUCE, Card.CLUBS, R.drawable.deuce_of_clubs);
            case "deuce_of_diamonds":
                return new Card(Card.DEUCE, Card.DIAMONDS, R.drawable.deuce_of_diamonds);
            case "three_of_spades":
                return new Card(Card.THREE, Card.SPADES, R.drawable.three_of_spades);
            case "three_of_hearts":
                return new Card(Card.THREE, Card.HEARTS, R.drawable.three_of_hearts);
            case "three_of_clubs":
                return new Card(Card.THREE, Card.CLUBS, R.drawable.three_of_clubs);
            case "three_of_diamonds":
                return new Card(Card.THREE, Card.DIAMONDS, R.drawable.three_of_diamonds);
            case "four_of_spades":
                return new Card(Card.FOUR, Card.SPADES, R.drawable.four_of_spades);
            case "four_of_hearts":
                return new Card(Card.FOUR, Card.HEARTS, R.drawable.four_of_hearts);
            case "four_of_clubs":
                return new Card(Card.FOUR, Card.CLUBS, R.drawable.four_of_clubs);
            case "four_of_diamonds":
                return new Card(Card.FOUR, Card.DIAMONDS, R.drawable.four_of_diamonds);
            case "five_of_spades":
                return new Card(Card.FIVE, Card.SPADES, R.drawable.five_of_spades);
            case "five_of_hearts":
                return new Card(Card.FIVE, Card.HEARTS, R.drawable.five_of_hearts);
            case "five_of_clubs":
                return new Card(Card.FIVE, Card.CLUBS, R.drawable.five_of_clubs);
            case "five_of_diamonds":
                return new Card(Card.FIVE, Card.DIAMONDS, R.drawable.five_of_diamonds);
            case "six_of_spades":
                return new Card(Card.SIX, Card.SPADES, R.drawable.six_of_spades);
            case "six_of_hearts":
                return new Card(Card.SIX, Card.HEARTS, R.drawable.six_of_hearts);
            case "six_of_clubs":
                return new Card(Card.SIX, Card.CLUBS, R.drawable.six_of_clubs);
            case "six_of_diamonds":
                return new Card(Card.SIX, Card.DIAMONDS, R.drawable.six_of_diamonds);
            case "seven_of_spades":
                return new Card(Card.SEVEN, Card.SPADES, R.drawable.seven_of_spades);
            case "seven_of_hearts":
                return new Card(Card.SEVEN, Card.HEARTS, R.drawable.seven_of_hearts);
            case "seven_of_clubs":
                return new Card(Card.SEVEN, Card.CLUBS, R.drawable.seven_of_clubs);
            case "seven_of_diamonds":
                return new Card(Card.SEVEN, Card.DIAMONDS, R.drawable.seven_of_diamonds);
            case "eight_of_spades":
                return new Card(Card.EIGHT, Card.SPADES, R.drawable.eight_of_spades);
            case "eight_of_hearts":
                return new Card(Card.EIGHT, Card.HEARTS, R.drawable.eight_of_hearts);
            case "eight_of_clubs":
                return new Card(Card.EIGHT, Card.CLUBS, R.drawable.eight_of_clubs);
            case "eight_of_diamonds":
                return new Card(Card.EIGHT, Card.DIAMONDS, R.drawable.eight_of_diamonds);
            case "nine_of_spades":
                return new Card(Card.NINE, Card.SPADES, R.drawable.nine_of_spades);
            case "nine_of_hearts":
                return new Card(Card.NINE, Card.HEARTS, R.drawable.nine_of_hearts);
            case "nine_of_clubs":
                return new Card(Card.NINE, Card.CLUBS, R.drawable.nine_of_clubs);
            case "nine_of_diamonds":
                return new Card(Card.NINE, Card.DIAMONDS, R.drawable.nine_of_diamonds);
            case "ten_of_spades":
                return new Card(Card.TEN, Card.SPADES, R.drawable.ten_of_spades);
            case "ten_of_hearts":
                return new Card(Card.TEN, Card.HEARTS, R.drawable.ten_of_hearts);
            case "ten_of_clubs":
                return new Card(Card.TEN, Card.CLUBS, R.drawable.ten_of_clubs);
            case "ten_of_diamonds":
                return new Card(Card.TEN, Card.DIAMONDS, R.drawable.ten_of_diamonds);
            case "jack_of_spades":
                return new Card(Card.JACK, Card.SPADES, R.drawable.jack_of_spades);
            case "jack_of_hearts":
                return new Card(Card.JACK, Card.HEARTS, R.drawable.jack_of_hearts);
            case "jack_of_clubs":
                return new Card(Card.JACK, Card.CLUBS, R.drawable.jack_of_clubs);
            case "jack_of_diamonds":
                return new Card(Card.JACK, Card.DIAMONDS, R.drawable.jack_of_diamonds);
            case "queen_of_spades":
                return new Card(Card.QUEEN, Card.SPADES, R.drawable.queen_of_spades);
            case "queen_of_hearts":
                return new Card(Card.QUEEN, Card.HEARTS, R.drawable.queen_of_hearts);
            case "queen_of_clubs":
                return new Card(Card.QUEEN, Card.CLUBS, R.drawable.queen_of_clubs);
            case "queen_of_diamonds":
                return new Card(Card.QUEEN, Card.DIAMONDS, R.drawable.queen_of_diamonds);
            case "king_of_spades":
                return new Card(Card.KING, Card.SPADES, R.drawable.king_of_spades);
            case "king_of_hearts":
                return new Card(Card.KING, Card.HEARTS, R.drawable.king_of_hearts);
            case "king_of_clubs":
                return new Card(Card.KING, Card.CLUBS, R.drawable.king_of_clubs);
            case "king_of_diamonds":
                return new Card(Card.KING, Card.DIAMONDS, R.drawable.king_of_diamonds);
        }
        return new Card(Card.ACE, Card.DIAMONDS, R.drawable.ace_of_diamonds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rank);
        dest.writeInt(suit);
        dest.writeInt(cardImage);
    }
}

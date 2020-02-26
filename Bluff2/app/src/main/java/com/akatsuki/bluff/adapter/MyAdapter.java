package com.akatsuki.bluff.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.akatsuki.bluff.R;
import com.akatsuki.bluff.util.Card;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Card> cards;
    public int selectedLadke;
    List<Card> selectedCards;
    Context context;
    Spinner sp;
    Button b;

    class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public MyViewHolder(ImageView v) {
            super(v);
            mImageView = v;
        }
    }

    public MyAdapter(Context context, Spinner sp, Button b) {
        cards = new ArrayList<>(52);
        this.b = b;
        this.context = context;
        this.sp = sp;
        selectedCards = new ArrayList<>(4);
        selectedLadke = 0;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_trial, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.mImageView.setImageResource(cards.get(position).getCardImage());
        Card card = cards.get(position);

        if (getSelectedCards().contains(card)) {
            holder.mImageView.setAlpha(.5f);
        }
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getAlpha() == 1 && selectedLadke != 4) {
                    v.setAlpha(.5f);
                    selectedCards.add(cards.get(position));
                    selectedLadke++;
                } else if (v.getAlpha() == .5) {
                    v.setAlpha(1f);
                    selectedCards.remove(cards.get(position));
                    if (selectedLadke > 0)
                        selectedLadke--;
                }
                if (selectedLadke > 0) {
                    b.setText("Play");
                    sp.setAlpha(1f);
                    sp.setEnabled(true);
                } else {
                    sp.setAlpha(.25f);
                    sp.setEnabled(false);
                }
                if (selectedLadke == 0)
                    b.setText("Pass");
            }
        });
        holder.setIsRecyclable(false);
    }

    public void setSelectedLadke(int selectedLadke) {
        this.selectedLadke = selectedLadke;
    }


    public List<Card> getSelectedCards() {
        return selectedCards;
    }

    public void setSelectedCards(List<Card> selectedCards) {
        this.selectedCards = selectedCards;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void add(int position, Card card) {
        cards.add(position, card);
        notifyItemInserted(position);
    }

    public void remove(Card card) {
        cards.remove(card);
        notifyDataSetChanged();
    }
}
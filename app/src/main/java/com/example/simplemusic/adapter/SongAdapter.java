package com.example.simplemusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.simplemusic.R;
import com.example.simplemusic.model.ModelSong;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private List<ModelSong> items = new ArrayList<>();

private Context ctx;
private SongAdapter.OnItemClickListener mOnItemClickListener;

public interface OnItemClickListener {
    void onItemClick(View view, SongAdapter obj, int position);
}

    public void setOnItemClickListener(final SongAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public SongAdapter(Context context, List<ModelSong> items) {
        this.items = items;
        ctx = context;
    }

public class OriginalViewHolder extends RecyclerView.ViewHolder {
    public TextView artistname;
    public TextView songtitle;
    public OriginalViewHolder(View v) {
        super(v);
        songtitle=v.findViewById(R.id.title);
        artistname=v.findViewById(R.id.artist);

    }
}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        vh = new SongAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ModelSong obj = items.get(position);
        if (holder instanceof SongAdapter.OriginalViewHolder) {
            SongAdapter.OriginalViewHolder view = (SongAdapter.OriginalViewHolder) holder;
            view.artistname.setText(obj.getArtist());
            view.songtitle.setText(" "+obj.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}


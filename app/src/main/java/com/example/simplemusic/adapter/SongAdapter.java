package com.example.simplemusic.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.simplemusic.Ads;
import com.example.simplemusic.MainActivity;
import com.example.simplemusic.R;
import com.example.simplemusic.SplashActivity;
import com.example.simplemusic.Tools;
import com.example.simplemusic.model.ModelSong;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private List<ModelSong> items = new ArrayList<>();

private  int itemly;

private Context ctx;
private SongAdapter.OnItemClickListener mOnItemClickListener;

public interface OnItemClickListener {
    void onItemClick(ModelSong obj, int position);
}

    public void setOnItemClickListener(final SongAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public SongAdapter(Context context, List<ModelSong> items,int itemly) {
        this.items = items;
        ctx = context;
        this.itemly=itemly;
    }

public class OriginalViewHolder extends RecyclerView.ViewHolder {
    public TextView artistname;
    public TextView songtitle;
    public LinearLayout mainlylist;
    public ImageView imageView;

    public OriginalViewHolder(View v) {
        super(v);
        songtitle=v.findViewById(R.id.title);
        artistname=v.findViewById(R.id.artist);
        mainlylist=v.findViewById(R.id.mainly);
        imageView=v.findViewById(R.id.songimage);

    }
}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(itemly, parent, false);
        vh = new SongAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ModelSong obj = items.get(position);
        if (holder instanceof SongAdapter.OriginalViewHolder) {
            final SongAdapter.OriginalViewHolder view = (SongAdapter.OriginalViewHolder) holder;
            view.artistname.setText(obj.getArtist());
            view.songtitle.setText(obj.getTitle());
            Tools.displayImageOriginal(ctx, view.imageView, obj.getImageurl());

            view.mainlylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnItemClickListener != null) {
                        Ads ads = new Ads(ctx,true);
                        ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
                            @Override
                            public void onAdsfinish() {
                                mOnItemClickListener.onItemClick(obj,position);

                            }

                        });
                    }
                }
            });



        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}


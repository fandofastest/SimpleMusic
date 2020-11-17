package com.mysimplemusic.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mysimplemusic.player.Ads;
import com.mysimplemusic.player.R;
import com.mysimplemusic.player.model.ModelOffline;

import java.util.ArrayList;
import java.util.List;

public class OffAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ModelOffline> items = new ArrayList<>();

    private  int itemly;

    private Context ctx;
    private OffAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ModelOffline obj, int position);
    }

    public void setOnItemClickListener(final OffAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public OffAdapter(Context context, List<ModelOffline> items, int itemly) {
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
        vh = new OffAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ModelOffline obj = items.get(position);
        if (holder instanceof OffAdapter.OriginalViewHolder) {
            final OffAdapter.OriginalViewHolder view = (OffAdapter.OriginalViewHolder) holder;
            view.songtitle.setText(obj.getTitle());
            view.imageView.setImageResource(R.drawable.ic_default);

            view.mainlylist.setOnClickListener(v -> {

                if (mOnItemClickListener != null) {
                    Ads ads = new Ads(ctx,true);
                    ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
                        @Override
                        public void onAdsfinish() {
                            mOnItemClickListener.onItemClick(obj,position);

                        }

                    });
                }
            });



        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}


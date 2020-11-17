package com.mysimplemusic.player.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mysimplemusic.player.MusicActivity;
import com.mysimplemusic.player.R;
import com.mysimplemusic.player.adapter.OffAdapter;
import com.mysimplemusic.player.adapter.SongAdapter;
import com.mysimplemusic.player.model.ModelSong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mysimplemusic.player.Config.APIKEY;
import static com.mysimplemusic.player.MusicService.currentlist;
import static com.mysimplemusic.player.MusicService.listfavorite;
import static com.mysimplemusic.player.MusicService.listoff;
import static com.mysimplemusic.player.MusicService.listpopuler;
import static com.mysimplemusic.player.MusicService.listrecent;
import static com.mysimplemusic.player.MusicService.listtrending;
import static io.realm.Realm.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String listtype;
    private String mParam2;
    RecyclerView rvlistl;
    List<ModelSong> listsearch = new ArrayList<>();
    Context context;
    SongAdapter songAdapter;
    OffAdapter offAdapter;


    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String param1, String param2) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listtype = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context=getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvlistl=view.findViewById(R.id.rvlistsong);




        if (listtype.equals("Populer")){
            songAdapter = new SongAdapter(context, listpopuler,R.layout.item_song_list);
            currentlist=listpopuler;
        }
        else if (listtype.equals("Trending")) {
            songAdapter = new SongAdapter(context, listtrending,R.layout.item_song_list);
            currentlist=listtrending;
        }
        else if (listtype.equals("Favorite")) {
            songAdapter = new SongAdapter(context, listfavorite,R.layout.item_song_list);
            currentlist=listfavorite;
        }
        else if (listtype.equals("Recent")) {
            songAdapter = new SongAdapter(context, listrecent,R.layout.item_song_list);
            currentlist=listrecent;
        }
        else  if (listtype.equals("Search")){
            songAdapter = new SongAdapter(context, listsearch,R.layout.item_song_list);
           getsongs(mParam2);
        }

        else  if (listtype.equals("Local")){
            offAdapter = new OffAdapter(context, listoff,R.layout.item_song_list);
        }

        rvlistl.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false));
        rvlistl.setHasFixedSize(true);
        //set data and list adapter


        if (listtype.equals("Local")){
            rvlistl.setAdapter(offAdapter);
            offAdapter.setOnItemClickListener((obj, position) -> {
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("pos",position);
                intent.putExtra("local",true);
                startActivity(intent);
            });

        }
        else {
            rvlistl.setAdapter(songAdapter);
            songAdapter.setOnItemClickListener((obj, position) -> {
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("pos",position);
                startActivity(intent);
            });

        }

    }
    public void getsongs(String q){
        listsearch.clear();
        rvlistl.removeAllViews();
        String url;
        url="https://api-v2.soundcloud.com/search/tracks?q="+q+"&client_id="+APIKEY+"&limit=100";
        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray1=response.getJSONArray("collection");

                for (int i = 0;i<jsonArray1.length();i++){
                    JSONObject jsonObject=jsonArray1.getJSONObject(i);
                    ModelSong listModalClass = new ModelSong();
                    listModalClass.setId(jsonObject.getInt("id"));
                    listModalClass.setTitle(jsonObject.getString("title"));
                    listModalClass.setImageurl(jsonObject.getString("artwork_url"));
                    listModalClass.setDuration(jsonObject.getString("full_duration"));
                    listModalClass.setType("online");


                    try {
                        JSONObject jsonArray3=jsonObject.getJSONObject("publisher_metadata");
                        listModalClass.setArtist(jsonArray3.getString("artist"));

                    }
                    catch (JSONException e){
                        listModalClass.setArtist("Artist");

                    }
                    listsearch.add(listModalClass);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            songAdapter.notifyDataSetChanged();
            currentlist=listsearch;




        }, error -> {

        });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


    }


}
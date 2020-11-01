package com.example.simplemusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.simplemusic.MainActivity;
import com.example.simplemusic.MusicActivity;
import com.example.simplemusic.MusicService;
import com.example.simplemusic.RealmHelper;
import com.example.simplemusic.adapter.OffAdapter;
import com.example.simplemusic.model.ModelOffline;
import com.example.simplemusic.model.ModelSong;
import com.example.simplemusic.R;
import com.example.simplemusic.adapter.SongAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.example.simplemusic.Config.APIKEY;
import static com.example.simplemusic.MusicService.currentlist;
import static com.example.simplemusic.MusicService.listfavorite;
import static com.example.simplemusic.MusicService.listoff;
import static com.example.simplemusic.MusicService.listpopuler;
import static com.example.simplemusic.MusicService.listrecent;
import static com.example.simplemusic.MusicService.listtrending;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Realm realm;
    SearchView searchview;


    Button buttonmorefav,buttonmoretrending,buttonmorepopular,buttonmorerecent,buttonmorelocal;
    Context context;
    SongAdapter populerAdapter,trendingAdapter,favAdapter,recentAdapter;
    OffAdapter offAdapter;

    List<ModelSong> listsong = new ArrayList<>();

    RecyclerView recyclerViewfav,rvpopuler,rvtrending,rvrecent,rvlocal;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchview=view.findViewById(R.id.searchview);
        searchview.setQueryHint("Find Your Songs");
        EditText editText = searchview.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(Color.GRAY);
        editText.setHintTextColor(Color.GRAY);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((MainActivity) getActivity()).gotoSonglist("Search",query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        buttonmorefav=view.findViewById(R.id.buttonfavmore);
        buttonmorepopular=view.findViewById(R.id.buttonpopularmore);
        buttonmoretrending=view.findViewById(R.id.buttontrendingmore);
        buttonmorerecent=view.findViewById(R.id.buttonrecentmore);
        buttonmorelocal=view.findViewById(R.id.buttonlocalmore);

        recyclerViewfav=view.findViewById(R.id.rvfavoritesong);
        recyclerViewfav.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
        recyclerViewfav.setHasFixedSize(true);
        //set data and list adapter
        favAdapter = new SongAdapter(context, listfavorite,R.layout.item_song);
        favAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelSong obj, int position) {
                currentlist=listfavorite;
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });
        recyclerViewfav.setAdapter(favAdapter);

        rvpopuler=view.findViewById(R.id.rvpopularsong);
        rvpopuler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
        rvpopuler.setHasFixedSize(true);
        //set data and list adapter
        populerAdapter = new SongAdapter(context, listpopuler,R.layout.item_song);
        populerAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelSong obj, int position) {
                currentlist=listpopuler;
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });
        rvpopuler.setAdapter(populerAdapter);

        rvtrending=view.findViewById(R.id.rvtrendingsong);
        rvtrending.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
        rvtrending.setHasFixedSize(true);
        //set data and list adapter
        trendingAdapter = new SongAdapter(context, listtrending,R.layout.item_song);
        trendingAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelSong obj, int position) {
                currentlist=listtrending;
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });
        rvtrending.setAdapter(trendingAdapter);


        rvrecent=view.findViewById(R.id.rvrecent);
        rvrecent.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
        rvrecent.setHasFixedSize(true);
        //set data and list adapter
        recentAdapter = new SongAdapter(context, listrecent,R.layout.item_song);
        recentAdapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelSong obj, int position) {
                currentlist=listrecent;
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("pos",position);
                startActivity(intent);
            }
        });
        rvrecent.setAdapter(recentAdapter);


        rvlocal=view.findViewById(R.id.rvlocal);
        rvlocal.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false));
        rvlocal.setHasFixedSize(true);
        //set data and list adapter
        offAdapter = new OffAdapter(context, listoff,R.layout.item_song);
        offAdapter.setOnItemClickListener(new OffAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ModelOffline obj, int position) {
                Intent intent = new Intent(context, MusicActivity.class);
                intent.putExtra("pos",position);
                intent.putExtra("local",true);
                startActivity(intent);
            }

        });
        rvlocal.setAdapter(offAdapter);


        buttonmorefav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).gotoSonglist("Favorite","");

            }
        });
        buttonmoretrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).gotoSonglist("Trending","");

            }
        });
        buttonmorepopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).gotoSonglist("Populer","");

            }
        });
        buttonmorerecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).gotoSonglist("Recent","");

            }
        });
        buttonmorelocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).gotoSonglist("Local","");

            }
        });
    initrealm();
    getTrending();
    getPopular();
    getRecent();
    getFav();
    getLocalSong();



    }

    public void getFav(){
        recyclerViewfav.removeAllViews();
        RealmHelper realmHelper = new RealmHelper(realm,getContext());
        listfavorite= realmHelper.getAllSongsFAv();
        Log.e("myfav", "getFav: "+listfavorite.size() );
        favAdapter.notifyDataSetChanged();

    }

    public void getRecent(){
        rvrecent.removeAllViews();
        RealmHelper realmHelper = new RealmHelper(realm,getContext());
        listrecent= realmHelper.getAllSongsrecent();
        recentAdapter.notifyDataSetChanged();
    }
    public void  initrealm(){
        Realm.init(Objects.requireNonNull(getContext()));
        RealmConfiguration configuration = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        realm = Realm.getInstance(configuration);
    }

    public void getTrending(){
        listtrending.clear();
        rvtrending.removeAllViews();
        String url="https://api-v2.soundcloud.com/charts?kind=trending&genre=soundcloud:genres:all-music&high_tier_only=false&limit=100&client_id="+APIKEY;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray1=response.getJSONArray("collection");
                    for (int i = 0;i<jsonArray1.length();i++){
                        JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                        JSONObject jsonObject=jsonObject1.getJSONObject("track");
                        ModelSong modelSong = new ModelSong();
                        modelSong.setId(jsonObject.getInt("id"));
                        modelSong.setTitle(jsonObject.getString("title"));
                        modelSong.setImageurl(jsonObject.getString("artwork_url"));
                        modelSong.setDuration(jsonObject.getString("full_duration"));
                        modelSong.setType("online");
                        try {
                            JSONObject jsonArray3=jsonObject.getJSONObject("publisher_metadata");
                            modelSong.setArtist(jsonArray3.getString("artist"));
                        }
                        catch (JSONException e){
                            modelSong.setArtist("Artist");
                        }
                        listtrending.add(modelSong);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                trendingAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }
    public void getPopular(){
        listpopuler.clear();
        rvpopuler.removeAllViews();
        String url="https://api-v2.soundcloud.com/charts?kind=top&genre=soundcloud:genres:all-music&high_tier_only=false&limit=100&client_id="+APIKEY;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray1=response.getJSONArray("collection");
                    for (int i = 0;i<jsonArray1.length();i++){
                        JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                        JSONObject jsonObject=jsonObject1.getJSONObject("track");
                        ModelSong modelSong = new ModelSong();
                        modelSong.setId(jsonObject.getInt("id"));
                        modelSong.setTitle(jsonObject.getString("title"));
                        modelSong.setImageurl(jsonObject.getString("artwork_url"));
                        modelSong.setDuration(jsonObject.getString("full_duration"));
                        modelSong.setType("online");
                        try {
                            JSONObject jsonArray3=jsonObject.getJSONObject("publisher_metadata");
                            modelSong.setArtist(jsonArray3.getString("artist"));
                        }
                        catch (JSONException e){
                            modelSong.setArtist("Artist");
                        }
                        listpopuler.add(modelSong);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                populerAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }
    public  void getLocalSong(){
        listoff.clear();
        rvlocal.removeAllViews();


        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor =  getContext().getContentResolver().query(allSongsUri, null, null, null, selection);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ModelOffline modalClass = new ModelOffline();
                    modalClass.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    modalClass.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));

                    listoff.add(modalClass);




                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        offAdapter.notifyDataSetChanged();
    }

}
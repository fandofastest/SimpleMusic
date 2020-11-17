package com.mysimplemusic.player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mysimplemusic.player.model.ModelOffline;
import com.mysimplemusic.player.model.ModelSong;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;


import cn.pedant.SweetAlert.SweetAlertDialog;
import hiennguyen.me.circleseekbar.CircleSeekBar;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ir.siaray.downloadmanagerplus.classes.Downloader;
import ir.siaray.downloadmanagerplus.enums.Storage;

import static com.mysimplemusic.player.Config.INTENTFILTER;
import static com.mysimplemusic.player.Config.PLAYING;
import static com.mysimplemusic.player.Config.STATUSINTENT;
import static com.mysimplemusic.player.MusicService.currentartist;
import static com.mysimplemusic.player.MusicService.currentimageurl;
import static com.mysimplemusic.player.MusicService.currentlist;
import static com.mysimplemusic.player.MusicService.currentoffpos;
import static com.mysimplemusic.player.MusicService.currentonline;
import static com.mysimplemusic.player.MusicService.currentpos;
import static com.mysimplemusic.player.MusicService.currenttitle;
import static com.mysimplemusic.player.MusicService.listoff;
import static com.mysimplemusic.player.MusicService.totalduration;

public class MusicActivity extends AppCompatActivity {
    ImageButton back,fav,download,shuffle,repeat,prev,next,play,voldown,volup;
    TextView title,artist,currentdura,totaldura;
    ImageView songimage,iconprev,iconnext;
    int nextpos,prevpos;
    ProgressBar progressplay;
    CircleSeekBar seekBar;
    MusicUtils musicUtils= new MusicUtils();
    private Handler mHandler = new Handler();
    Realm realm;
    ModelSong  modelSong;
    ModelOffline modelOffline;
    SweetAlertDialog pDialog;
    AudioManager audioManager;
    IndicatorSeekBar seekbarvolume;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        initComponent();
        initrealm();

        if (getIntent().hasExtra("main")){
            if (MusicService.PLAYERSTATUS.equals(PLAYING)){
            title.setText(currenttitle);
            artist.setText(currentartist);
            Tools.displayImageOriginal(MusicActivity.this,songimage,currentimageurl);
            play.setImageResource(R.drawable.ic_pause_100);
            play.setVisibility(View.VISIBLE);
            progressplay.setVisibility(View.GONE);
                mHandler.post(mUpdateTimeTask);
            }
            if (currentonline){
                modelSong=currentlist.get(currentpos);

            }
            else {
                modelOffline=listoff.get(currentoffpos);
            }


        }

        else {
            if (getIntent().hasExtra("local")){
                currentonline=false;
                currentoffpos=getIntent().getIntExtra("pos",0);
                loadDataDetailOffline();
                startService(currentoffpos,false);
            }

            else {
                currentpos=getIntent().getIntExtra("pos",0);
                currentonline=true;
                loadDataDetail();
                startService(currentpos,true);
                RealmHelper realmHelper = new RealmHelper(realm,getApplication());
                if (realmHelper.getStatusFav(modelSong.getId())){
                    fav.setImageResource(R.drawable.ic_fav_full);
                }


            }


        }


        getlocalbroadcaster();
        musicControll();


//
        seekBar.setSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangedListener() {
            @Override
            public void onPointsChanged(CircleSeekBar circleSeekBar, int points, boolean fromUser) {

                if(fromUser){

                    seekBar.setProgressDisplayAndInvalidate(points);
                    double currentseek = ((double) points/(double)MusicUtils.MAX_PROGRESS);

                    int totaldura= (int) totalduration;
                    int seek= (int) (totaldura*currentseek);

                    Intent intent = new Intent(INTENTFILTER);
                    intent.putExtra(STATUSINTENT, "seek");
                    intent.putExtra("seektime",seek);
                    LocalBroadcastManager.getInstance(MusicActivity.this).sendBroadcast(intent);

                }
            }

            @Override
            public void onStartTrackingTouch(CircleSeekBar circleSeekBar) {
                //needed by listener

            }

            @Override
            public void onStopTrackingTouch(CircleSeekBar circleSeekBar) {
                //needed by listener
            }
        });






    }

    public void  initrealm(){
        Realm.init(MusicActivity.this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        realm = Realm.getInstance(configuration);
    }
    public void musicControll(){
        play.setOnClickListener(arg0 -> {
            // check for already playing
            if (MusicService.PLAYERSTATUS.equals("PLAYING")) {
                pause();
            } else {
                // Resume song
                resume();

            }

        });
        next.setOnClickListener(v -> next());
        prev.setOnClickListener(v -> prev());
        fav.setOnClickListener(v -> {

            RealmHelper realmHelper = new RealmHelper(realm,getApplication());
            if (realmHelper.getStatusFav(modelSong.getId())) {
                realmHelper.updateFav(modelSong.getId(),"0");
                fav.setImageResource(R.drawable.ic_like);

                pDialog = new SweetAlertDialog(MusicActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                pDialog.getProgressHelper().setBarColor(R.color.bludemain);
                pDialog.setTitleText("Remove from favorite");

            }
            else {
                realmHelper.saveFav(modelSong);
                fav.setImageResource(R.drawable.ic_fav_full);
                pDialog = new SweetAlertDialog(MusicActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                pDialog.getProgressHelper().setBarColor(R.color.bludemain);
                pDialog.setTitleText("Added to favorite");

            }
            pDialog.setCancelable(false);
            pDialog.show();


        });

        shuffle.setOnClickListener(v -> {
          if (MusicService.SHUFFLE.equals("OFF")){
              MusicService.SHUFFLE="ON";
              shuffle.setImageResource(R.drawable.ic_shuffle_tint);
          }
          else {
              MusicService.SHUFFLE="OFF";
              shuffle.setImageResource(R.drawable.ic_shuffle);
          }
        });

        repeat.setOnClickListener(v -> {
            if (MusicService.REPEAT.equals("OFF")){
                MusicService.REPEAT="ON";
                repeat.setImageResource(R.drawable.ic_repeat_tint);
            }
            else {
                MusicService.REPEAT="OFF";
                repeat.setImageResource(R.drawable.ic_repeat);
            }
        });

        volup.setOnClickListener(v -> {
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
            int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            seekbarvolume.setProgress(volumeLevel);
        });
        voldown.setOnClickListener(v -> {
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            int volumeLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            seekbarvolume.setProgress(volumeLevel);
    });


        seekbarvolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        seekbarvolume.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

                if (seekParams.fromUser){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekParams.progress, 0);

                }

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                //needed by listener

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                //needed by listener

            }
        });

        download.setOnClickListener(v -> {
            Downloader downloader = Downloader.getInstance(MusicActivity.this)
                    .setUrl(Config.SERVERMUSIC+modelSong.getId())
                    .setToken("download")
                    .setAllowedOverRoaming(true)
                    .setAllowedOverMetered(true) //Api 16 and higher
                    .setVisibleInDownloadsUi(true)
                    .setDestinationDir(Storage.DIRECTORY_DOWNLOADS, modelSong.getTitle())
                    .setNotificationTitle(modelSong.getTitle());

            downloader.start();
        });

    }
    public void pause (){
        play.setImageResource(R.drawable.ic_playbig);
        Intent intent = new Intent(INTENTFILTER);
        intent.putExtra(STATUSINTENT, "pause");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    public void resume (){
        play.setImageResource(R.drawable.ic_pause_100);
        Intent intent = new Intent(INTENTFILTER);
        intent.putExtra(STATUSINTENT, "resume");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);

    }



    public void next (){
        currentdura.setText("");
        totaldura.setText("");
        title.setText("Please Wait");
        artist.setText("");
        songimage.setImageResource(R.color.colorPrimary);
        play.setVisibility(View.GONE);
        progressplay.setVisibility(View.VISIBLE);

        Intent intent = new Intent(INTENTFILTER);
        intent.putExtra(STATUSINTENT, "next");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);


    }
    public void prev (){
        currentdura.setText("");
        totaldura.setText("");
        title.setText("Please Wait");
        artist.setText("");
        songimage.setImageResource(R.color.colorPrimary);
        play.setVisibility(View.GONE);
        progressplay.setVisibility(View.VISIBLE);

        Intent intent = new Intent(INTENTFILTER);
        intent.putExtra(STATUSINTENT, "prev");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }
    @SuppressLint("WrongViewCast")
    public void initComponent(){
        back=findViewById(R.id.backarrow);
        fav=findViewById(R.id.fav);
        download=findViewById(R.id.download);
        shuffle=findViewById(R.id.shuffle);
        repeat=findViewById(R.id.repeat);
        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        play=findViewById(R.id.play);
        voldown=findViewById(R.id.volumedown);
        volup=findViewById(R.id.volumeup);
        title=findViewById(R.id.title);
        artist=findViewById(R.id.artist);
        currentdura=findViewById(R.id.currentdura);
        totaldura=findViewById(R.id.totaldura);
        songimage=findViewById(R.id.songimage);
        iconprev=findViewById(R.id.iconprev);
        iconnext=findViewById(R.id.iconnext);
        progressplay=findViewById(R.id.progressplay);
        seekBar=findViewById(R.id.seekbarcircular);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        seekbarvolume=findViewById(R.id.seekbarvolume);





    }

    public void loadDataDetail(){
        Log.e("loadDataDetail", "loadDataDetail: "+currentlist );
         modelSong = currentlist.get(currentpos);
        title.setText(modelSong.getTitle());
        artist.setText(modelSong.getArtist());
        Tools.displayImageOriginal(MusicActivity.this,songimage,modelSong.getImageurl());

        if (currentpos==currentlist.size()-1){
            nextpos=0;
            prevpos=currentpos-1;
        }
        else if (currentpos==0){
            prevpos=currentlist.size()-1;
            nextpos=currentpos+1;
        }
        else {
            nextpos=currentpos+1;
            prevpos=currentpos-1;
        }

        ModelSong prevmodel = currentlist.get(prevpos);
        Tools.displayImageOriginal(MusicActivity.this,iconprev,prevmodel.getImageurl());

        ModelSong nextmodel = currentlist.get(nextpos);
        Tools.displayImageOriginal(MusicActivity.this,iconnext,nextmodel.getImageurl());
    }
    public void loadDataDetailOffline(){
        modelOffline = listoff.get(currentoffpos);
        title.setText(modelOffline.getTitle());
        artist.setText("Local Song");
        songimage.setImageResource(R.drawable.ic_default);
        iconprev.setImageResource(R.drawable.ic_default);
        iconnext.setImageResource(R.drawable.ic_default);
        download.setVisibility(View.GONE);

        if (currentoffpos==listoff.size()-1){
            nextpos=0;
            prevpos=currentoffpos-1;
        }
        else if (currentoffpos==0){
            prevpos=listoff.size()-1;
            nextpos=currentoffpos+1;
        }
        else {
            nextpos=currentoffpos+1;
            prevpos=currentoffpos-1;
        }

    }

    public void startService(int pos,boolean online){
        Log.e("gotoSonglist", String.valueOf(online));

        Intent playerservice= new Intent(MusicActivity.this, MusicService.class);
        playerservice.putExtra("pos",pos);
        playerservice.putExtra("online",online);
        startService(playerservice);

    }

    public void getlocalbroadcaster(){
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");
                if (status.equals("playing")){
                    play.setVisibility(View.VISIBLE);
                    progressplay.setVisibility(View.GONE);
                    mHandler.post(mUpdateTimeTask);

                }
                else if (status.equals("prepare")){
                    if (currentonline){
                        loadDataDetail();
                    }
                    else {
                        loadDataDetailOffline();
                    }

                    play.setVisibility(View.GONE);
                    progressplay.setVisibility(View.VISIBLE);
                    artist.setText(MusicService.currentartist);
                    title.setText(MusicService.currenttitle);
                    Tools.displayImageOriginal(MusicActivity.this,songimage,MusicService.currentimageurl);


                }

            }
        }, new IntentFilter("musicplayer"));

    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (MusicService.PLAYERSTATUS.equals("PLAYING")) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "getduration");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        currentdura.setText(musicUtils.milliSecondsToTimer(MusicService.currentduraiton));
        totaldura.setText(musicUtils.milliSecondsToTimer(totalduration));
        // Updating progress bar
        int progress = (int) (musicUtils.getProgressSeekBar(MusicService.currentduraiton, totalduration));
        seekBar.setProgressDisplayAndInvalidate(progress);


    }


}
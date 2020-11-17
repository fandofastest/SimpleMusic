package com.mysimplemusic.player;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mysimplemusic.player.model.ModelOffline;
import com.mysimplemusic.player.model.ModelSong;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.mysimplemusic.player.Config.INTENTFILTER;
import static com.mysimplemusic.player.Config.PLAYING;
import static com.mysimplemusic.player.Config.STATUSINTENT;

public class MusicService extends Service {


    public  static Equalizer equalizer;

    public static  List<ModelSong> listpopuler = new ArrayList<>();
    public static  List<ModelSong> listtrending = new ArrayList<>();
    public static  List<ModelSong> listfavorite = new ArrayList<>();
    public static  List<ModelSong> currentlist = new ArrayList<>();
    public static  List<ModelSong> listrecent = new ArrayList<>();
    public static  List<ModelOffline> listoff = new ArrayList<>();

    public  static String PLAYERSTATUS="STOP",REPEAT="OFF",SHUFFLE="OFF",CURRENTTYPE="OFF";
    public static int totalduration,currentduraiton,currentpos,currentoffpos;
    String from;
    public static String currenttitle,currentartist,currentimageurl;
    public static boolean currentonline=true;
    Realm realm;
    public  static int sessionId;

    public static  Equalizer mEqualizer;
    public static BassBoost bassBoost;
    public static PresetReverb presetReverb;

    private MediaPlayer mp = new MediaPlayer();


    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra(STATUSINTENT);
                if (status.equals("pause")){
                    mp.pause();
                    PLAYERSTATUS="PAUSE";
                }
                else if (status.equals("resume")){
                    PLAYERSTATUS=PLAYING;
                    mp.start();
                }
                else  if (status.equals("seek")){
                    int seek = intent.getIntExtra("seektime",0);
                    mp.pause();
                    mp.seekTo(seek);
                    mp.start();
                }
                else if (status.equals("stopmusic")){
                    PLAYERSTATUS="STOPING";
                    mp.release();
                }
                else if (status.equals("getduration")){
                    totalduration=mp.getDuration();
                    currentduraiton=mp.getCurrentPosition();
                }
                else if (status.equals("next")){
                    playsong(currentpos+1);
                }

                else if (status.equals("prev")){
                    playsong(currentpos-1);
                }
                else if (status.equals("settimer")){
                    Long end= intent.getLongExtra("end",0);
                    new CountDownTimer(end, 1000) {
                        public void onTick(long millisUntilFinished) {
                            //needed by library
                        }
                        public void onFinish() {
                            PLAYERSTATUS="STOPING";
                            mp.release();
                        }
                    }.start();
                }
            }
        }, new IntentFilter(INTENTFILTER));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initrealm();

        if (intent.getBooleanExtra("online",true)){

            playsong(intent.getIntExtra("pos",0));

        }else {
            playsongoff(intent.getIntExtra("pos",0));

        }

        return START_STICKY;
    }
    public void playsong(int pos){

        if (pos==currentlist.size()-1){
            pos=0;
        }
        else if (pos==-1){
            pos=currentlist.size()-1;
        }

        currentpos=pos;

        try {
            final ModelSong modelSong =currentlist.get(pos);
            currentartist=modelSong.getArtist();
            currenttitle=modelSong.getTitle();
            currentimageurl=modelSong.getImageurl();

            Intent intent = new Intent(INTENTFILTER);
            intent.putExtra(STATUSINTENT, "prepare");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


            mp.stop();
            mp.reset();
            mp.release();
            Uri myUri = Uri.parse(Config.SERVERMUSIC+modelSong.getId());
            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnErrorListener((mp, what, extra) -> true);
            mp.setOnCompletionListener(mp1 -> {
                if (REPEAT.equals("ON")){
                    playsong(currentpos);
                }
                else if (SHUFFLE.equals("ON")){
                    int pos1 = (int) (Math.random() * (currentlist.size()));
                    playsong(pos1);
                }
                else {

                    playsong(currentpos+1);
                }
            });
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onPrepared(MediaPlayer mplayer) {
                    RealmHelper realmHelper = new RealmHelper(realm,getApplication());
                    realmHelper.saverecent(modelSong);
                    sessionId=mp.getAudioSessionId();
                    if (mplayer.isPlaying()) {
                        mp.pause();
                    } else {
                        mp.start();
                        PLAYERSTATUS="PLAYING";
                        Intent intent = new Intent(INTENTFILTER);
                        intent.putExtra(STATUSINTENT, "playing");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                }
            });
            mp.prepareAsync();
        }
        catch (Exception e){
            Log.d("TAG", "playsong: "+e);
        }
    }

    public void playsongoff(int pos){
        if (pos==listoff.size()-1){
            pos=0;
        }
        else if (pos==-1){
            pos=listoff.size()-1;
        }

        currentoffpos=pos;

        try {
            final ModelOffline modelSong =listoff.get(pos);
            currentartist="Local Song";
            currenttitle=modelSong.getTitle();
            currentimageurl="offline";

            Intent intent = new Intent("musicplayer");
            intent.putExtra("status", "prepare");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);


            mp.stop();
            mp.reset();
            mp.release();
            Uri myUri = Uri.parse(modelSong.getPath());
            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setOnErrorListener((mp, what, extra) -> true);
            mp.setOnCompletionListener(mp1 -> {
                if (REPEAT.equals("ON")){
                    playsongoff(currentoffpos);
                }
                else if (SHUFFLE.equals("ON")){
                    int pos1 = (int) (Math.random() * (currentlist.size()));
                    playsongoff(pos1);
                }
                else {

                    playsongoff(currentoffpos+1);
                }
            });
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onPrepared(MediaPlayer mplayer) {

                    sessionId=mp.getAudioSessionId();
                    if (mplayer.isPlaying()) {
                        mp.pause();
                    } else {
                        mp.start();
                        PLAYERSTATUS="PLAYING";
                        Intent intent = new Intent("musicplayer");
                        intent.putExtra("status", "playing");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    }
                }
            });
            mp.prepareAsync();
        }
        catch (Exception e){
            Log.d("TAG", "playsongoff: "+e);
        }
    }
    public void  initrealm(){
        Realm.init(MusicService.this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        realm = Realm.getInstance(configuration);
    }

}

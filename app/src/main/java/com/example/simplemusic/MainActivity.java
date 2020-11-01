package com.example.simplemusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.example.simplemusic.fragment.HomeFragment;
import com.example.simplemusic.fragment.ListFragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.warkiz.widget.IndicatorSeekBar;

import guy4444.smartrate.SmartRate;
import id.fando.GDPRChecker;

import static com.example.simplemusic.MusicService.currentartist;
import static com.example.simplemusic.MusicService.currenttitle;
import static com.example.simplemusic.MusicService.totalduration;


public class MainActivity extends AppCompatActivity {
    Fragment homefragment;
    ImageView bgminiplayer;
    MusicUtils musicUtils= new MusicUtils();
    private Handler mHandler = new Handler();
    private TextView titleToolbar;
    ProgressBar progressplay;
    IndicatorSeekBar seekBar;
    ImageButton prev,next,play;
    ImageView songimage;
    TextView title,artist;
    String fposition="0";
    LinearLayout mainly;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fposition="0";
        new GDPRChecker()
                .withContext(getApplicationContext())
                .withActivity(MainActivity.this)
                .withAppId(Ads.appid)
                .withDebug()
                .check();
        initToolbar();
        initComponent();
        loadFragment(homefragment);
        getlocalbroadcaster();
        musicControll();
        seekBar.setProgress(0);
        seekBar.setMax(MusicUtils.MAX_PROGRESS);
        play.setImageResource(R.drawable.ic_playbig);

        if (MusicService.PLAYERSTATUS.equals("PLAYING")){
            title.setText(currenttitle);
            artist.setText(currentartist);
            play.setImageResource(R.drawable.ic_pause_100);
        }

        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();


        LinearLayout  banner=findViewById(R.id.banner_container);
        Ads ads= new Ads(MainActivity.this,false);
        Display display =getWindowManager().getDefaultDisplay();
        ads.showBannerAds(banner,display);

    }

   private void initComponent (){
        artist=findViewById(R.id.artist);
        title=findViewById(R.id.title);
        homefragment=new HomeFragment();
        bgminiplayer=findViewById(R.id.bgminiplayer);
        play=findViewById(R.id.play);
        prev=findViewById(R.id.prev);
        next=findViewById(R.id.next);
        seekBar=findViewById(R.id.seekbar);
       progressplay=findViewById(R.id.progressplay);
       songimage=findViewById(R.id.songimage);
       mainly=findViewById(R.id.mainly);

   }
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public void gotoSonglist(String list,String query) {
        fposition="1";
        toolbar.setVisibility(View.VISIBLE);
        titleToolbar.setText(list);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.frame, ListFragment.newInstance(list,query))
                .addToBackStack("opsi")
                .commit();

    }
    @Override
    public void onBackPressed() {
        if (!fposition.equals("1")){
            exitdialog();
        }
        else {

            super.onBackPressed();
            titleToolbar.setText("Music App");
            fposition="0";

        }




    }
    public void  exitdialog(){
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Quit App")
                .setMessage("Are you sure?")

                .addButton("Sure", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        finishAffinity();
                        finish();

                    }
                })
                .addButton("Rate App", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.END, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showRate();

                    }
                });

// Show the alert
        builder.show();
    }
    public void showRate(){

        SmartRate.Rate(MainActivity.this
                , "Rate Us"
                , "Tell others what you think about this app"
                , "Continue"
                , "Please take a moment and rate us on Google Play"
                , "click here"
                , "Cancel"
                , "Thanks for the feedback"
                , Color.parseColor("#FF0BDFEC")
                , 4
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ResourceAsColor")
    public void initToolbar(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.color.colorWhite);
        titleToolbar = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        titleToolbar.setText("Music App");

    }
    public void getlocalbroadcaster(){
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");
                if (status.equals("playing")){
                    play.setVisibility(View.VISIBLE);
                    play.setImageResource(R.drawable.ic_pause_100);
                    progressplay.setVisibility(View.GONE);
                    mHandler.post(mUpdateTimeTask);

                }
                else if (status.equals("prepare")){

                    play.setVisibility(View.GONE);
                    progressplay.setVisibility(View.VISIBLE);
                    artist.setText(MusicService.currentartist);
                    title.setText(MusicService.currenttitle);
                    Tools.displayImageOriginal(MainActivity.this,songimage,MusicService.currentimageurl);


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

        // Updating progress bar
        int progress = (int) (musicUtils.getProgressSeekBar(MusicService.currentduraiton, totalduration));
        seekBar.setProgress(progress);


    }
    public void next (){

        title.setText("Please Wait");
        artist.setText("");
        songimage.setImageResource(R.color.colorPrimary);
        play.setVisibility(View.GONE);
        progressplay.setVisibility(View.VISIBLE);

        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "next");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);


    }
    public void prev (){

        title.setText("Please Wait");
        artist.setText("");
        songimage.setImageResource(R.color.colorPrimary);
        play.setVisibility(View.GONE);
        progressplay.setVisibility(View.VISIBLE);

        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "prev");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//        mHandler.post(mUpdateTimeTask);

    }
    public void pause (){
        play.setImageResource(R.drawable.ic_playbig);
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "pause");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    public void resume (){
        play.setImageResource(R.drawable.ic_pause_100);
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "resume");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);

    }

    public void musicControll(){
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (MusicService.PLAYERSTATUS.equals("PLAYING")) {
                    pause();
                } else {
                    // Resume song
                    resume();

                }

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev();
            }
        });

        mainly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicService.PLAYERSTATUS.equals("PLAYING")){
                    Intent intent = new Intent(MainActivity.this,MusicActivity.class);
                    intent.putExtra("main","main");
                    startActivity(intent);

                }

            }
        });

    }


}
package com.example.simplemusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


import android.os.Bundle;
import android.widget.ImageView;

import com.example.simplemusic.fragment.HomeFragment;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Fragment homefragment;
    ImageView bgminiplayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initComponent();


        loadFragment(homefragment);


    }

   private void initComponent (){
        homefragment=new HomeFragment();
        bgminiplayer=findViewById(R.id.bgminiplayer);


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



    private void initToolbar() {
        setSupportActionBar(toolbar);
        Tools.setSystemBarColor(this, R.color.colorWhite);
    }

}
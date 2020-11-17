package com.mysimplemusic.player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;


import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity {
    Button startbutton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startbutton=findViewById(R.id.startapp);
        progressBar=findViewById(R.id.progress);
        startbutton.setVisibility(View.GONE);
        getKey();
        getStatusApp(Ads.urlconfig);

    }

    public void getKey(){
        String url="https://fando.id/soundcloud/getapi.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Config.APIKEY=response.replaceAll("^\"|\"$", "");
                }, error -> {
        });
        Volley.newRequestQueue(SplashActivity.this).add(stringRequest);
    }
    private void getStatusApp(String url){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                Ads.primaryads=response.getString("primaryads");
                Ads.modealternatif=response.getString("modealternatif");
                Ads.appid=response.getString("appid");
                Ads.fanbanner=response.getString("fanbanner");
                Ads.faninter=response.getString("faninter");
                Ads.admobinter=response.getString("admobinter");
                Ads.admobbanner=response.getString("admobbanner");
                Config.statusapp=response.getString("statusapp");
                Config.appupdate=response.getString("appupdate");
                Config.primaryads=response.getString("primaryads");

                if (Config.statusapp.equals("suspend")){
                    showDialog(Config.appupdate);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    startbutton.setVisibility(View.VISIBLE);
                    startbutton.setOnClickListener(v -> {
                        Ads ads = new Ads(SplashActivity.this,true);
                        ads.setCustomObjectListener(new Ads.MyCustomObjectListener() {
                            @Override
                            public void onAdsfinish() {
                                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        });
                    });

                }
            } catch (JSONException e) {
                Log.e("errorparsing",e.getMessage());
            }
        }, error -> {
        });
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    private void  showDialog(String appupdate){
        new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("App Was Discontinue")
                .setContentText("Please Install Our New Music App")
                .setConfirmText("Install")

                .setConfirmClickListener(sDialog -> {
                    sDialog
                            .setTitleText("Install From Playstore")
                            .setContentText("Please Wait, Open Playstore")
                            .setConfirmText("Go")


                            .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(
                                "https://play.google.com/store/apps/details?id="+appupdate));
                        intent.setPackage("com.android.vending");
                        startActivity(intent);
//                                Do something after 100ms
                    }, 3000);



                })
                .show();
    }



}
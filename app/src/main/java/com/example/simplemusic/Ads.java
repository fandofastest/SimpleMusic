package com.example.simplemusic;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;


import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Ads {
        public static String admobbanner,admobinter,fanbanner,faninter,primaryads,modealternatif,appid;
        public  static String urlconfig="https://fando.id/sample/musicapp.php";
        InterstitialAd interstitialAdfan;
        com.facebook.ads.AdView adViewfb;
        AdView mAdViewadmob;

        Context context;
        com.google.android.gms.ads.InterstitialAd mInterstitialAdadmob;
        KProgressHUD hud;
        public interface MyCustomObjectListener {
            void onAdsfinish();
        }

    private MyCustomObjectListener listener;

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }

    public Ads(Context context,Boolean loadinter) {
        this.context = context;

        if (loadinter){
            hud = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Loading")
                    .setCancellable(true)
                    .setDetailsLabel("Please Wait")
                    .setMaxProgress(100)
                    .show();
            if (primaryads.equals("admob")){
                showinteradmob(this.context,admobinter);
            }
            else if (primaryads.equals("fan")){
                showinterfb(this.context,faninter);
            }

        }




    }



    public void showinterfb(final Context context, String interfb){
        AdSettings.addTestDevice("fad050e3-e081-46fc-8739-1b23ab2e880b");

        interstitialAdfan = new com.facebook.ads.InterstitialAd(context, interfb);

        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
                if (hud.isShowing()){
                    hud.dismiss();
                }
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                listener.onAdsfinish();
                if (hud.isShowing()){
                    hud.dismiss();
                }

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                hud.dismiss();
                // Ad error callback
                if ((modealternatif.equals("on") && primaryads.equals("fan"))){
                    showinteradmob(context,admobinter);
                }
                else {
                    listener.onAdsfinish();
                }

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad

                if (hud.isShowing()){
                    hud.dismiss();
                }

                interstitialAdfan.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };
        interstitialAdfan.loadAd(
                interstitialAdfan.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());


    }


    public  void showinteradmob(final Context context, String inter) {

        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("FC366145111DC30BB6BCA2904BE0B77E")).build();
        MobileAds.setRequestConfiguration(configuration);
        Log.e("showinteradmob", "showinteradmob: "+inter );

        mInterstitialAdadmob = new com.google.android.gms.ads.InterstitialAd(context);
        mInterstitialAdadmob.setAdUnitId(inter);
        mInterstitialAdadmob.loadAd(new AdRequest.Builder().build());
        mInterstitialAdadmob.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAdadmob.show();
                if (hud.isShowing()){
                    hud.dismiss();
                }

                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                hud.dismiss();
                super.onAdFailedToLoad(loadAdError);
                Log.e("loadadmob", "onError:"+loadAdError );

                if ((modealternatif.equals("on") && primaryads.equals("admob")   )){
                    showinterfb(context,faninter);
                }
                else {
                    listener.onAdsfinish();
                }


            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                listener.onAdsfinish();
                if (hud.isShowing()){
                    hud.dismiss();
                }

                // Code to be executed when the interstitial ad is closed.
            }
        });


    }

    public  void showBannerAds(LinearLayout mAdViewLayout, Display display) {
        AdSettings.addTestDevice("fad050e3-e081-46fc-8739-1b23ab2e880b");



        adViewfb = new com.facebook.ads.AdView(context, fanbanner, com.facebook.ads.AdSize.BANNER_HEIGHT_50);


        adViewfb.loadAd();


        mAdViewadmob  = new com.google.android.gms.ads.AdView(context);
        AdSize adSize = getAdSize(context,display);

        mAdViewadmob.setAdSize(adSize);
        mAdViewadmob.setAdUnitId(admobbanner);

        AdRequest.Builder builder = new AdRequest.Builder();




        mAdViewadmob.loadAd(builder.build());
//        mAdViewLayout.addView(mAdView);
        System.out.println(fanbanner);
        if (Ads.primaryads.equals("admob")){
            mAdViewLayout.addView(mAdViewadmob);

        }
        else {
            mAdViewLayout.addView(adViewfb);
        }






    }


    private AdSize getAdSize(Context context, Display display) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }


}

package com.mysimplemusic.player;

import android.content.Context;
import android.util.Log;

import com.mysimplemusic.player.model.ModelSong;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class  RealmHelper {

    Realm realm;
    Context contex;

    public RealmHelper(Realm realm, Context contex) {
        this.realm = realm;
        this.contex = contex;
    }


    // untuk menyimpan data
    public void saverecent(final ModelSong songModel) {
        realm.executeTransaction(realm -> {
            if (realm != null) {
                RealmResults<ModelSong> result = realm.where(ModelSong.class)
                        .equalTo("id", songModel.getId())
                        .findAll();

                if (result.size() > 0) {
                    updatesongrecent(songModel.getId(),"1");
                    Log.d("TAG", "saverecent: ");
                } else {
                    try {
                        songModel.setRecent("1");
                        ModelSong model = realm.copyToRealm(songModel);
                        Log.d("TAG", "saverecent: "+model.getId());
                    } catch (Exception e) {
                        Log.d("TAG", "errr: "+e);

                    }
                }
            } else {
                Log.e("ppppp", "execute: Database not Exist");
            }
        });
    }

    public void saveFav(final ModelSong songModel) {
        realm.executeTransaction(realm -> {
            if (realm != null) {
                RealmResults<ModelSong> result = realm.where(ModelSong.class)
                        .equalTo("id", songModel.getId())
                        .findAll();

                if (result.size() > 0) {
                    updateFav(songModel.getId(),"1");
                    Log.d("TAG", "diupdate ke recent: ");

                } else {
                    try {
                        songModel.setFav("1");
                        ModelSong model = realm.copyToRealm(songModel);
                        Log.d("TAG", "ditambahkan ke recent");

                    } catch (Exception e) {
                        Log.d("TAG", "saveFav: "+e);


                    }
                }
            } else {
                Log.e("ppppp", "execute: Database not Exist");
            }
        });
    }




    public List<ModelSong> getAllSongsrecent() {
        RealmResults<ModelSong> results = realm.where(ModelSong.class)
                .equalTo("recent", "1")
                .findAll();
        return results;
    }
    public List<ModelSong> getAllSongsFAv() {
        RealmResults<ModelSong> results = realm.where(ModelSong.class)
                .equalTo("fav", "1")
                .findAll();
        return results;
    }

    public boolean getStatusFav(int idsong){
                ModelSong modelSong =realm.where(ModelSong.class)
                        .equalTo("fav","1")
                        .equalTo("id",idsong)
                        .findFirst();
               if (modelSong==null){
                   return false;
               }
               else {
                   return true;
               }

    }



    // untuk meng-update data
    public void updateFav(final Integer id,final String status) {
        realm.executeTransactionAsync(realm -> {
            ModelSong model = realm.where(ModelSong.class)
                    .equalTo("id", id)
                    .findFirst();
            model.setFav(status);
        }, () -> Log.e("pppp", "onSuccess: Update Successfully"), error -> error.printStackTrace());
    }

    // untuk meng-update data
    public void updatesongrecent(final Integer id, final String status) {
        realm.executeTransactionAsync(realm -> {
            ModelSong model = realm.where(ModelSong.class)
                    .equalTo("id", id)
                    .findFirst();
            model.setRecent(status);
        }, () -> Log.e("pppp", "onSuccess: Update Successfully"), error -> error.printStackTrace());
    }






}

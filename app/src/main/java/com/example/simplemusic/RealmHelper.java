package com.example.simplemusic;

import android.content.Context;
import android.util.Log;

import com.example.simplemusic.model.ModelSong;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class  RealmHelper {

    Realm realm;
    Context contex;

    public RealmHelper(Realm realm, Context contex) {
        this.realm = realm;
        this.contex = contex;
    }


    // untuk menyimpan data
    public void saverecent(final ModelSong songModel) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realm != null) {
                    RealmResults<ModelSong> result = realm.where(ModelSong.class)
                            .equalTo("id", songModel.getId())
                            .findAll();

                    if (result.size() > 0) {
                        updatesongrecent(songModel.getId(),"1");
                        System.out.println("diupdate ke recent");
                    } else {
                        try {
                            songModel.setRecent("1");
                            ModelSong model = realm.copyToRealm(songModel);
                            System.out.println("ditambahkan ke recent");
                        } catch (Exception e) {
//                        Toast.makeText(contex,"Song already exists",Toast.LENGTH_LONG).show();

                        }
                    }
                } else {
                    Log.e("ppppp", "execute: Database not Exist");
                }
            }
        });
    }

    public void saveFav(final ModelSong songModel) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realm != null) {
                    RealmResults<ModelSong> result = realm.where(ModelSong.class)
                            .equalTo("id", songModel.getId())
                            .findAll();

                    if (result.size() > 0) {
                        updateFav(songModel.getId(),"1");
                        System.out.println("diupdate ke recent");
                    } else {
                        try {
                            songModel.setFav("1");
                            ModelSong model = realm.copyToRealm(songModel);
                            System.out.println("ditambahkan ke recent");
                        } catch (Exception e) {
//                        Toast.makeText(contex,"Song already exists",Toast.LENGTH_LONG).show();

                        }
                    }
                } else {
                    Log.e("ppppp", "execute: Database not Exist");
                }
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
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ModelSong model = realm.where(ModelSong.class)
                        .equalTo("id", id)
                        .findFirst();
                model.setFav(status);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("pppp", "onSuccess: Update Successfully");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    // untuk meng-update data
    public void updatesongrecent(final Integer id, final String status) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ModelSong model = realm.where(ModelSong.class)
                        .equalTo("id", id)
                        .findFirst();
                model.setRecent(status);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("pppp", "onSuccess: Update Successfully");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }






}

package com.shifu.user.project1;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmController {

    private Realm realm;

    public RealmController(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    public void Clear() {
        realm.beginTransaction();
        realm.where(RealmModel.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }
    public Long addInfo(String title) {
        realm.beginTransaction();

        RealmModel realmObject = realm.createObject(RealmModel.class);
        Long id = getNextKey();
        realmObject.setID(id);
        realmObject.setName(title);

        realm.commitTransaction();

        return id;
    }

    public RealmResults<RealmModel> getInfo() {
        return realm.where(RealmModel.class).findAll();
    }

    public void updateInfo(Long id, String title) {
        realm.beginTransaction();

        RealmModel realmObject = realm.where(RealmModel.class).equalTo("ID", id).findFirst();
        realmObject.setName(title);

        realm.commitTransaction();
    }

    public void removeItemById(long id) {

        long size = getNextKey();
        realm.beginTransaction();
        RealmResults<RealmModel> results = realm.where(RealmModel.class).equalTo("ID", id).findAll();
        Log.d("To Delete:", results.toString());
        results.deleteAllFromRealm();
        realm.commitTransaction();
        Log.d("DB AfterDelete:", realm.where(RealmModel.class).findAll().toString());
        Log.d("DB Size:", Long.toString(realm.where(RealmModel.class).count()));
    }

    private Long getNextKey() {
        if (realm.where(RealmModel.class).count() > 1) {
            return realm.where(RealmModel.class).max("ID").longValue()+1;
        }
        else {
            return 0L;
        }
    }

}
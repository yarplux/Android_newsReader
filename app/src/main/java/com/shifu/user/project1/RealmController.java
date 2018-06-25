package com.shifu.user.project1;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmController {

    private Realm realm;

    public RealmController(Context context, RealmConfiguration config) {
        Realm.init(context);
        realm.setDefaultConfiguration(config);
        realm = Realm.getInstance(config);
    }

    public void Clear() {
        realm.beginTransaction();
        realm.where(RealmModel.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }
    public Long addInfo(String title, String content, String link) {

        if (title == null || title.equals("")) return -1L;

        realm.beginTransaction();
        RealmModel realmObject = realm.createObject(RealmModel.class);
        Long id = getNextKey();
        realmObject.setID(id);
        realmObject.setTitle(title);

        if (content != null) {
            realmObject.setContent(content);
        } else {
            realmObject.setContent("");
        }
        if (link != null) {
            realmObject.setLink(link);
        } else {
            realmObject.setContent("");
        }

        realm.commitTransaction();
        return id;
    }

    public RealmResults<RealmModel> getInfo() {
        return realm.where(RealmModel.class).findAll();
    }

    public boolean updateInfo(Long id, String title, String content, String link) {

        if (title == null || title.equals("")) return false;

        realm.beginTransaction();
        RealmModel realmObject = realm.where(RealmModel.class).equalTo("id", id).findFirst();
        realmObject.setTitle(title);

        if (content != null) {
            realmObject.setContent(content);
        } else {
            realmObject.setContent("");
        }
        if (link != null) {
            realmObject.setLink(link);
        } else {
            realmObject.setContent("");
        }

        realm.commitTransaction();

        return true;
    }

    public void removeItemById(long id) {

        long size = getNextKey();
        realm.beginTransaction();
        RealmResults<RealmModel> results = realm.where(RealmModel.class).equalTo("id", id).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
//        Log.d("To Delete:", results.toString());
//        Log.d("DB AfterDelete:", realm.where(RealmModel.class).findAll().toString());
//        Log.d("DB Size:", Long.toString(realm.where(RealmModel.class).count()));
    }

    private Long getNextKey() {
        if (realm.where(RealmModel.class).count() > 1) {
            return realm.where(RealmModel.class).max("id").longValue()+1;
        }
        else {
            return 0L;
        }
    }

}
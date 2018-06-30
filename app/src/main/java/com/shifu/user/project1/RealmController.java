package com.shifu.user.project1;

import android.content.Context;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmController {

    private Realm realm;
    private Context context;

    public RealmController(Context context, RealmConfiguration config) {
        Realm.init(context);
        this.context = context;
        realm.setDefaultConfiguration(config);
        realm = Realm.getInstance(config);
    }

    public void Clear() {
        realm.beginTransaction();
        realm.where(RealmModel.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void addInfo(final Countries data) {
        realm.beginTransaction();
        for (com.shifu.user.project1.CountriesResponse obj : data.getResponse()) {
            RealmModel item = RealmModel.create(realm);
                    String country_content = context.getResources().getString(R.string.country_entry,
                            obj.getRegion(),
                            obj.getSubRegion(),
                            obj.getNativeLanguage(),
                            obj.getCurrencyName());
                    item.setTitle(obj.getName());
                    item.setContent(country_content);
        }
        realm.commitTransaction();

// Test of writing in realms
//        for (Long i=0L; i<data.getResponse().size(); i++) {
//            RealmModel obj = realm.where(RealmModel.class).equalTo("id", i).findFirst();
//            String id = Long.toString(obj.getID());
//            String name = obj.getTitle();
//            Log.d("In realm:", id+' '+name);
//        }
//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                for (int i=0; i< data.getResponse().size(); i++) RealmModel.create(realm);
//                Long i=0L;
//                for (com.shifu.user.project1.CountriesResponse obj : data.getResponse()) {
//                    String country_content = context.getResources().getString(R.string.country_entry,
//                            obj.getRegion(),
//                            obj.getSubRegion(),
//                            obj.getNativeLanguage(),
//                            obj.getCurrencyName());
//                    updateInfo(i++, obj.getName(), country_content, null);
//                }
//            }
//        });
    }

    public void addInfo(final String title, final String content, final String link) {
        if (title == null || title.equals("")) return;
        realm.beginTransaction();
        RealmModel obj = RealmModel.create(realm);
        obj.setTitle(title);
        obj.setContent(content);
        obj.setLink(link);
        realm.commitTransaction();
    }

//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//        });
//    }

    public RealmResults<RealmModel> getInfo() {
        return realm.where(RealmModel.class).findAll();
    }

    public void updateInfo(final Long id, final String title, final String content, final String link) {
        if (title == null || title.equals("")) return;
        realm.beginTransaction();
        RealmModel obj = realm.where(RealmModel.class).equalTo("id", id).findFirst();
        obj.setTitle(title);
        obj.setContent(content);
        obj.setLink(link);
        realm.commitTransaction();
//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//            }
//        });
    }

    public void removeItemById(final long id) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmModel.delete(realm, id);
            }
        });
    }

}
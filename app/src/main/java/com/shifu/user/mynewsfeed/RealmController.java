package com.shifu.user.mynewsfeed;

import android.content.Context;

import com.shifu.user.mynewsfeed.realm.Article;
import com.shifu.user.mynewsfeed.realm.State;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmController {

    private Realm realm;

    private static RealmController instance = null;
    public static RealmController getInstance() {
        return instance;
    }


    RealmController(Context context) {
        if (instance == null) {
            Realm.init(context);
            realm = Realm.getDefaultInstance();
            instance = this;
        }
    }


    /*
     * Create data functions
     */

    public void stateInit(){
        if (realm.where(State.class).count() == 0) {
            realm.executeTransaction(trRealm -> {
                State state = new State();
                trRealm.copyToRealm(state);
            });
        }
    }

    /*
     * Read data functions
     */

    public Realm getRealmFromLooperThread(){
        realm.setAutoRefresh(true);
        return realm;
    }

    public RealmResults<Article> getArticles() {
        State state = realm.where(State.class).findFirst();
        if (state == null) {
            return realm.where(Article.class).sort("publishedAt", Sort.DESCENDING).findAll();
        } else {
            return realm.where(Article.class).equalTo("category", state.getCategory()).sort("publishedAt", Sort.DESCENDING).findAll();
        }
    }

    public String getCategory() {
        State out = realm.where(State.class).findFirst();
        return (out == null)?null:out.getCategory();
    }

    public Boolean getAutoupdate() {
        State out = realm.where(State.class).findFirst();
        if (out == null || out.getAutoupdate() == null) return false;
        return out.getAutoupdate();
    }

    /*
     * Update data functions
     */

    public void refresh(){
        realm.refresh();
    }

    public void setCategory(String category) {
        realm.executeTransaction(trRealm -> {
                State state = trRealm.where(State.class).findFirst();
                if (state != null) trRealm.where(State.class).findFirst().setCategory(category);
        });
    }

    public void setAutoupdate(Boolean autoupdate) {
        realm.executeTransaction(trRealm -> {
            State state = trRealm.where(State.class).findFirst();
            if (state != null) trRealm.where(State.class).findFirst().setAutoupdate(autoupdate);
        });
    }



    /*
     * Delete data functions
     */

    public void clear() {
        realm.executeTransaction(trRealm -> realm.where(Article.class).findAll().deleteAllFromRealm());
    }

    public void close() {
        if (realm != null) realm.close();
    }

}
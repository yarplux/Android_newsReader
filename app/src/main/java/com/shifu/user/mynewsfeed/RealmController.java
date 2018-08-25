package com.shifu.user.mynewsfeed;

import android.content.Context;

import com.shifu.user.mynewsfeed.json.JsonArticle;
import com.shifu.user.mynewsfeed.realm.Article;
import com.shifu.user.mynewsfeed.realm.RealmSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
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
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();

            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
            instance = this;
        }
    }

    /*
     * Create data funations
     */

    public void loadSources(final JSONArray sources) {
        realm.executeTransaction(trRealm -> {
            try {
                for (int i = 0; i < sources.length(); i++) {
                    JSONObject obj = sources.getJSONObject(i);
                    RealmSource objIn = trRealm.where(RealmSource.class).equalTo(RealmSource.getNetIdField(), obj.getString(RealmSource.getNetIdField())).findFirst();
                    if (objIn == null) {
                        trRealm.copyToRealm(new RealmSource(obj));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadArticles(final List<JsonArticle> articles) {
        realm.executeTransaction(trRealm -> {
            Article.setLastID(trRealm.where(Article.class).count());
            for (JsonArticle obj : articles) {
                Article objIn = trRealm.where(Article.class).equalTo(Article.getNetIdField(), obj.getUrl()).findFirst();
                if (objIn == null && obj.getPublishedAt() != null) {
                    trRealm.copyToRealm(new Article(obj));
                }
            }
        });
    }

    /*
     * Read data functions
     */

    public RealmResults<Article> getArticles() {
        return realm.where(Article.class).findAll().sort("publishedAt", Sort.DESCENDING);
    }


    /*
     * Delete data functions
     */

    public void clear() {
        realm.executeTransaction(trRealm -> realm.deleteAll());
    }

}
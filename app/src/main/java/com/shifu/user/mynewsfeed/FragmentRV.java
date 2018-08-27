package com.shifu.user.mynewsfeed;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shifu.user.mynewsfeed.json.JsonArticle;
import com.shifu.user.mynewsfeed.realm.Article;
import com.shifu.user.mynewsfeed.realm.State;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.shifu.user.mynewsfeed.CustomDialogCall.showRadioButtonDialog;

public class FragmentRV extends Fragment {

    private RecyclerView rv;
    private RealmRVAdapter ra = RealmRVAdapter.getInstance();
    private static RealmController rc = RealmController.getInstance();
    private static ApiInterface api = ApiClient.getInstance().getApi();

    Disposable disposable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_layout, container, false);

        rc = RealmController.getInstance();
        api = ApiClient.getInstance().getApi();

        Log.d("RV", "is rc null? "+Boolean.toString(rc==null));

        rv = rootView.findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setItemViewCacheSize(20);

        ImageButton filter = getActivity().findViewById(R.id.filter);
        filter.setOnClickListener(view -> {
            if (disposable != null && !disposable.isDisposed()) disposable.dispose();
            disposable = showRadioButtonDialog(getContext())
                    .observeOn(Schedulers.computation())
                    .map(index -> {
                        if (index == null) return -2;
                        String msg = null;
                        if (index != -1) {
                            String str = getResources().getStringArray(R.array.categories)[index];
                            msg = str.substring(0, str.indexOf('|'));
                        }
                        Log.d("Dialog", "Index: " + index + " Name: " + msg);

                        Realm realm = Realm.getDefaultInstance();
                        State state = realm.where(State.class).findFirst();
                        String currentCategory = (state == null) ? null : state.getCategory();
                        realm.beginTransaction();
                        if (index != -1 && !msg.equals(currentCategory)) {
                            if (state == null) realm.createObject(State.class);
                            realm.where(State.class).findFirst().setCategory(msg);
                            realm.commitTransaction();
                            return index;
                        } else if (index == -1) {
                            if (state == null) realm.createObject(State.class);
                            realm.where(State.class).findFirst().setCategory(null);
                            realm.commitTransaction();
                            return -1;
                        } else {
                            realm.cancelTransaction();
                            return -2;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(index -> {
                        if (index != -2) {
                            NavigationView nvDrawer = getActivity().findViewById(R.id.nvView);
                            View hView = nvDrawer.getHeaderView(0);
                            TextView category = hView.findViewById(R.id.category);

                            if (index == -1) {
                                category.setText(getResources().getString(R.string.category, "Новости без категории"));
                            } else {
                                String str = getResources().getStringArray(R.array.categories)[index];
                                str = str.substring(str.indexOf('|') + 1);
                                category.setText(getResources().getString(R.string.category, str));
                            }
                        }

                        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
                        disposable = getArticles(getContext(), false, null)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(i -> verifyStoragePermissionsAndRequest());

                    });
        });

        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
        disposable = getArticles(getContext(), false, null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> verifyStoragePermissionsAndRequest());

        return rootView;
    }

    public static Flowable<Integer> getArticles(Context context, Boolean fromBack, String key){
        Log.d("getArticles","Start loading. From back? "+fromBack);
        Map<String, String> options = new HashMap<>();
        options.put("country", "ru");

        return   Flowable
                .fromCallable(() -> {
                    if (fromBack) Realm.init(context);
                    Realm realm = Realm.getDefaultInstance();
                    realm.refresh();
                    State item = realm.where(State.class).findFirst();
                    if (item != null && item.getCategory() != null) {
                        options.put("category", item.getCategory());
                        return item.getCategory();
                    } else {
                        return "";
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .concatMap(i -> {
                    Log.d("REST", "Request category: "+i);
                    if (key != null) {
                        return api.loadNews(options, key);
                    } else {
                        return api.loadNews(options, context.getResources().getString(R.string.api_key));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(t-> {
                    Log.d("REST", "Failure: "+t.toString());
                    t.printStackTrace();
                    if (!fromBack) {
                        Toast.makeText(context, "Ошибка соединения с сервером", Toast.LENGTH_LONG).show();
                    }
                })
                .filter(response -> {
                    if (response.isSuccessful()
                            && response.body() != null
                            && response.body().getStatus() != null
                            && response.body().getStatus().equals("ok")
                            && response.body().getArticles() != null) {
                        return true;
                    } else {
                        if (response.errorBody() != null) {
                            Log.e("REST error: ", response.errorBody().toString());
                        } else {
                            Log.e("REST error: ", null);
                        }
                        if (!fromBack) {
                            Toast.makeText(context, "Ошибка получения данных", Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }
                })
                .observeOn(Schedulers.computation())
                .map(response -> {
                    Realm realm = Realm.getDefaultInstance();
                    realm.refresh();
                    Integer newArticles = 0;
                    realm.beginTransaction();
                    Article.setLastID(realm.where(Article.class).count());
                    int i=1;
                    State state = realm.where(State.class).findFirst();
                    String chosenCategory = null;
                    if (state != null) chosenCategory = state.getCategory();
                    Log.d("Articles", "Current: "+chosenCategory);
                    for (JsonArticle obj : response.body().getArticles()) {
                        RealmResults<Article> objsIn = realm.where(Article.class).equalTo("url", obj.getUrl()).findAll();
                        Log.d("Article "+(i++), /* obj.toString() + */" \nexist in base? " +Boolean.toString(objsIn!=null)+"\npublishedAt: "+obj.getPublishedAt());
                        if (objsIn.size() == 0 && obj.getPublishedAt() != null) {
                            newArticles++;
                            Article item = new Article(obj,chosenCategory);
                            realm.copyToRealm(item);
                            Log.d("New Article ("+item.getUid()+")", " \n"+item.getCategory()+" "+item.getTitle()+"\npublished At: "+item.getPublishedAt());
                        } else if (objsIn.size() > 0) {
                            Boolean flag = true;
                            for (Article objIn : objsIn) {
                                if (objIn.getCategory() != null && objIn.getCategory().equals(chosenCategory)) {
                                    flag = false;
                                    break;
                                } else if (objIn.getCategory() == null && chosenCategory == null) {
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                newArticles++;
                                Log.d("Article ("+(Article.getLastID())+")", "New category: "+chosenCategory+" Title: "+obj.getTitle());
                                realm.copyToRealm(new Article(obj, chosenCategory));
                            }
                        }
                    }
                    realm.commitTransaction();
                    return newArticles;
                });
    }

    public void verifyStoragePermissionsAndRequest() {
        String[] permissions = { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE };
        int permission = ActivityCompat.checkSelfPermission(getActivity(), permissions[0]);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 0);
        } else {
            ((ActivityMain) getActivity()).finishLoading();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        rv.setAdapter(ra);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
    }

}

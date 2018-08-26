
package com.shifu.user.mynewsfeed;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shifu.user.mynewsfeed.realm.Article;
import com.shifu.user.mynewsfeed.realm.State;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmRVAdapter extends RealmRecyclerViewAdapter<Article, RealmRVAdapter.ViewHolder> {


    private final static DateFormat DATE_FORMAT_IN = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    private static Resources res;
    private static RealmRVAdapter instance;
    public static RealmRVAdapter getInstance(){
        return instance;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView layout;
        private ImageView image;
        private TextView title, source, published;

        Article data;

        ViewHolder(View v) {
            super(v);

            image = v.findViewById(R.id.image);
            title = v.findViewById(R.id.title);
            source = v.findViewById(R.id.source);
            published = v.findViewById(R.id.published);

            layout = v.findViewById(R.id.item_layout);
            layout.setOnClickListener(view -> {

                Fragment frag = new FragmentNews();
                Bundle args = new Bundle();
                args.putString("url", data.getUrl());
                frag.setArguments(args);

                ((FragmentActivity) view.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, frag)
                        .addToBackStack(null)
                        .commit();
            });
        }

    }

    RealmRVAdapter(OrderedRealmCollection<Article> data, Resources resources) {
        super(data, true);
        setHasStableIds(true);

        if (instance == null) instance = this;
        res = resources;
    }

    @Override
    public @NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_news, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Article obj = getItem(position);
        Log.d("RA", "Bind "+position);
        if (obj != null) {
            viewHolder.data = obj;
            String text = obj.getTitle();
            viewHolder.title.setText((text == null)?"":text);

            text = obj.getUrlToImage();
            if (text != null && !text.equals("")) {
                Log.d("Image", "position: "+position+" title: "+obj.getTitle()+" url: "+text);
                Picasso.get()
                        .load(text)
                        .into(viewHolder.image);
            } else {
                viewHolder.image.setImageDrawable(null);
            }

            Date date = obj.getPublishedAt();
            if (date != null) {
                viewHolder.published.setText(DATE_FORMAT_IN.format(date));
            }

            text = obj.getAuthor();
            if (text == null) text = obj.getName();
            if (text == null) text = "-";
            viewHolder.source.setText(res.getString(R.string.source, text));
        }

    }

    public void updateData() {
        Realm realm = RealmController.getInstance().getRealmFromLooperThread();

        State state = realm.where(State.class).findFirst();
        RealmQuery<Article> query;
        if (state == null) {
            query = realm.where(Article.class).sort("publishedAt", Sort.DESCENDING);
        } else {
            query = realm.where(Article.class).equalTo("category", state.getCategory()).sort("publishedAt", Sort.DESCENDING);
        }
        RealmResults<Article> results = query.findAll();
        updateData(results);

        Log.d("RA", "Load size: "+results.size()+" Category: "+RealmController.getInstance().getCategory());

//        if (data.size() != 0) {
//            updateData(data);
//        }

        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int index) {
        return (getItem(index) == null)?-1:getItem(index).getUid();
    }

}

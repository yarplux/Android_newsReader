package com.shifu.user.mynewsfeed.realm;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicLong;

import io.realm.RealmObject;


public class RealmSource extends RealmObject{

    final private static String FIELD_PK = "suid";
    final private static String FIELD_NET_ID = "id";


    final private static AtomicLong lastID = new AtomicLong(0);

    private Long suid;
    private String id;

    private String name;
    private String description;
    private String url;
    private String category;
    private String language;
    private String country;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public RealmSource() {}

    public RealmSource(JSONObject source) throws JSONException {
        this.suid = increment();
        this.id = source.getString("id");
        this.name = source.getString("name");
        this.description = source.getString("description");
        this.url = source.getString("url");
        this.category = source.getString("category");
        this.language = source.getString("language");
        this.country = source.getString("country");
    }


    private static Long increment() {
        return lastID.getAndIncrement();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                .append("name", name)
                .append("description", description)
                .append("url", url)
                .append("category", category)
                .append("language", language)
                .append("country", country)
                .toString();
    }

    public static String getFieldPk() {
        return FIELD_PK;
    }

    public static String getNetIdField() {
        return FIELD_NET_ID;
    }

    public Long getSuid() {
        return suid;
    }
}
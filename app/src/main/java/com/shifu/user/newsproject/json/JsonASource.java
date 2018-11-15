package com.shifu.user.newsproject.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

import io.realm.RealmObject;

public class JsonASource {

    @SerializedName("id")
    @Expose
    private Object id;
    @SerializedName("name")
    @Expose
    private String name;

    public Object getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .toString();
    }
}

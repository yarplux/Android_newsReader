package com.shifu.user.newsproject.realm;

import org.apache.commons.lang3.builder.ToStringBuilder;

import io.realm.RealmObject;

public class State extends RealmObject {

    private String category;
    private Boolean autoupdate;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public State() {}

    @Override
    public String toString(){
        return new ToStringBuilder(this)
                .append("category", category)
                .append("autoupdate", autoupdate)
                .toString();
    }

    public Boolean getAutoupdate() {
        return autoupdate;
    }

    public void setAutoupdate(Boolean autoupdate) {
        this.autoupdate = autoupdate;
    }
}

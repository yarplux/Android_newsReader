package com.shifu.user.project1;

import io.realm.RealmObject;

public class RealmModel  extends RealmObject {

    private Long id;
    private String title;
    private String content;
    private String link;


    public Long getID() { return id; }
    public void setID(Long Number) { this.id = Number; }

    public String getTitle() { return title; }
    public void setTitle(String data) { this.title = data; }

    public String getContent() { return content; }
    public void setContent(String data) { this.content = data; }

    public String getLink() { return link; }
    public void setLink(String data) { this.link = data; }

}

package com.shifu.user.project1;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmModel  extends RealmObject {

    public static final String FIELD_ID = "id";
    private static AtomicInteger INTEGER_COUNTER = new AtomicInteger(0);

    @PrimaryKey
    private Long id;

    private String title;
    private String content;
    private String link;


    public Long getID() { return id; }
    public void setID(Long Number) { this.id = Number; }

    public String getTitle() { return title; }
    public void setTitle(String data) { this.title = data; }

    public String getContent() { return content; }
    public void setContent(String data) {
        this.content = data;
    }

    public String getLink() { return link; }
    public void setLink(String data) { this.link = data; }

    //  create() & delete() needs to be called inside a transaction.

    static RealmModel create(Realm realm) {
        RealmParent parent;

        if (realm.where(RealmParent.class).findFirst() == null) {
            parent = new RealmParent();
        } else {
            parent = realm.where(RealmParent.class).findFirst();
        }

        RealmList<RealmModel> items = parent.getItemList();
        RealmModel counter = realm.createObject(RealmModel.class, increment());
        items.add(counter);
        return counter;
    }

    static void delete(Realm realm, long id) {
        RealmModel item = realm.where(RealmModel.class).equalTo(FIELD_ID, id).findFirst();
        // Otherwise it has been deleted already.
        if (item != null) {
            item.deleteFromRealm();
        }
    }

    private static int increment() {
        return INTEGER_COUNTER.getAndIncrement();
    }
}

package com.shifu.user.project1;

import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmParent extends RealmObject {
    @SuppressWarnings("unused")
    private RealmList<RealmModel> itemList = new RealmList<>();

    public RealmList<RealmModel> getItemList() {
        return itemList;
    }
}
package com.shifu.user.project1;

import io.realm.RealmObject;

public class RealmModel  extends RealmObject {

    private Long ID;

    private String Name;

    public Long getID() { return ID; }

    public void setID(Long Number) { this.ID = Number; }

    public String getName() { return Name; }

    public void setName(String Name) { this.Name = Name; }

}

package com.shifu.user.project1;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

// To Migrate from Задание #3 to Задание #4

public class MyMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

        // Migrate to version 1: Add a new class.
        // Example:
        // public Person extends RealmObject {
        //     // getters and setters left out for brevity
        // }
        if (oldVersion == 0) {
            schema.create("RealmModel_1")
                    .addField("ID", Long.class)
                    .addField("Name", String.class);
            oldVersion++;
        }

        // Migrate to version 2: Add a primary key + object references
        // Example:
        // public Person extends RealmObject {
        //     // getters and setters left out for brevity
        // }
        if (oldVersion == 1) {
            schema.get("RealmMode_2")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("title", String.class)
                    .addField("content", String.class)
                    .addField("link", String.class);
            oldVersion++;
        }
    }
}

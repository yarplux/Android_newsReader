package com.shifu.user.project1;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

// To Migrate from Задание #3 to Задание #4
public class RealmMigrationBetweenTasks implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.create("RealmModel_1")
                    .addField("ID", Long.class)
                    .addField("Name", String.class);
            oldVersion++;
        }

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

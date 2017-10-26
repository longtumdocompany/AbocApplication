package com.longarndu.aboc.mainaboc;

import android.content.Context;
import io.realm.RealmResults;

/**
 * Created by TOPPEE on 9/11/2017.
 */

public class RealmBooksAdapter extends RealmModelAdapter<Book> {

    public RealmBooksAdapter(Context context, RealmResults<Book> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
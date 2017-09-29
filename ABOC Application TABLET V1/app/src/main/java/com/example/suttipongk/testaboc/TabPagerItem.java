package com.example.suttipongk.testaboc;

import android.support.v4.app.Fragment;

/**
 * Created by TOPPEE on 7/23/2017.
 */

public class TabPagerItem {

    private final CharSequence mTitle;
    private final Fragment mFragment;

    public TabPagerItem(CharSequence title, Fragment fragment) {
        this.mTitle = title;
        this.mFragment = fragment;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

}

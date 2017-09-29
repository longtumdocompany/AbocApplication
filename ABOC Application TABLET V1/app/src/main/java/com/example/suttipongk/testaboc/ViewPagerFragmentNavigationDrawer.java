package com.example.suttipongk.testaboc;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TOPPEE on 7/23/2017.
 */

public class ViewPagerFragmentNavigationDrawer extends Fragment {
	private List<TabPagerItem> mTabs = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTabPagerItem();
    }

    private void createTabPagerItem(){
        mTabs.add(new TabPagerItem(getString(R.string.speechrecognition), MainFragmentNavigationDrawer.newInstance(getString(R.string.speechrecognition))));
        mTabs.add(new TabPagerItem(getString(R.string.facedetection), MainFragmentNavigationDrawer.newInstance(getString(R.string.facedetection))));
        mTabs.add(new TabPagerItem(getString(R.string.documents), MainFragmentNavigationDrawer.newInstance(getString(R.string.documents))));
        mTabs.add(new TabPagerItem(getString(R.string.scanpaper), MainFragmentNavigationDrawer.newInstance(getString(R.string.scanpaper))));
        mTabs.add(new TabPagerItem(getString(R.string.iot), MainFragmentNavigationDrawer.newInstance(getString(R.string.iot))));
        mTabs.add(new TabPagerItem(getString(R.string.fall_detection), MainFragmentNavigationDrawer.newInstance(getString(R.string.fall_detection))));
        mTabs.add(new TabPagerItem(getString(R.string.chat_room), MainFragmentNavigationDrawer.newInstance(getString(R.string.chat_room))));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);

    	mViewPager.setOffscreenPageLimit(mTabs.size());
        mViewPager.setAdapter(new ViewPagerAdapterNavigation(getChildFragmentManager(), mTabs));
        TabLayout mSlidingTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSlidingTabLayout.setElevation(15);
        }
        mSlidingTabLayout.setupWithViewPager(mViewPager);
    }
}
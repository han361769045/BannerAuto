package com.leo.lu.bannerauto;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LeoLu on 2016/10/20.
 * A PagerAdapter that wraps around another PagerAdapter to handle paging wrap-around.
 * Thanks to: https://github.com/antonyt/InfiniteViewPager
 */

public class InfinitePagerAdapter extends PagerAdapter {
    private static final String TAG = "InfinitePagerAdapter";

    private BannerAdapter adapter;

    public InfinitePagerAdapter(BannerAdapter adapter) {
        this.adapter = adapter;
    }

    public BannerAdapter getRealAdapter() {
        return this.adapter;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public int getRealCount() {
        return adapter.getCount();
    }

    public int getCurrentPosition(int position) {
        if (getRealCount() == 0)
            return 0;
        if (position <= getRealCount())
            return position;
        return position % getRealCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (getRealCount() == 0) {
            return null;
        }
        return adapter.instantiateItem(container, position % getRealCount());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (getRealCount() == 0) {
            return;
        }
        adapter.destroyItem(container, position % getRealCount(), object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return adapter.isViewFromObject(view, object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        adapter.finishUpdate(container);
    }

    @Override
    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        adapter.restoreState(bundle, classLoader);
    }

    @Override
    public Parcelable saveState() {
        return adapter.saveState();
    }

    @Override
    public void startUpdate(ViewGroup container) {
        adapter.startUpdate(container);
    }

}

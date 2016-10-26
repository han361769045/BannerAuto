package com.leo.lu.bannerauto;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.leo.lu.bannerauto.bannertypes.BaseBannerView;

import java.util.ArrayList;

/**
 * Created by LeoLu on 2016/10/20.
 */

public class BannerAdapter extends PagerAdapter implements BaseBannerView.ImageLoadListener {

    private Context mContext;
    private ArrayList<BaseBannerView> mImageContents;
    private BannerLayout mBannerLayout;

    public BannerAdapter(Context context) {
        mContext = context;
        mImageContents = new ArrayList<>();
    }

    public BannerAdapter(Context context, BannerLayout bannerLayout) {
        mContext = context;
        this.mBannerLayout = bannerLayout;
        mImageContents = new ArrayList<>();
    }


    public <T extends BaseBannerView> void addBanner(T banner) {
        banner.setOnImageLoadListener(this);
        mImageContents.add(banner);
        notifyDataSetChanged();
    }

    public BaseBannerView getBannerView(int position) {
        if (position < 0 || position >= mImageContents.size()) {
            return null;
        } else {
            return mImageContents.get(position);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public <T extends BaseBannerView> void removeBanner(T banner) {
        if (mImageContents.contains(banner)) {
            mImageContents.remove(banner);
            notifyDataSetChanged();
        }
    }

    public void removeBannerAt(int position) {
        if (mImageContents.size() > position) {
            mImageContents.remove(position);
            notifyDataSetChanged();
        }
    }

    public void removeAllBanners() {
        mImageContents.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mImageContents.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        BaseBannerView b = mImageContents.get(position);
        View v = b.getView();
        container.addView(v);
        return v;
    }


    @Override
    public void onStart(BaseBannerView target) {

    }

    @Override
    public void onEnd(boolean result, BaseBannerView target) {
        if (target.isErrorDisappear() || result) {
            return;
        }
        for (BaseBannerView banner : mImageContents) {
            if (banner.equals(target)) {
                removeBanner(target);
                break;
            }
        }
    }

    /**
     * This method should be called by the application if the data backing this adapter has changed
     * and associated views should update.
     */
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (mBannerLayout != null) {
            if (getCount() == 0) {
                mBannerLayout.showDefault();
            } else {
                mBannerLayout.hideDefault();
            }
        }
    }

}

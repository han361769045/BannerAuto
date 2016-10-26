package com.leo.lu.bannerauto.bannertypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.leo.lu.bannerauto.R;

/**
 * Created by LeoLu on 2016/10/20.
 */
public class DefaultBannerView extends BaseBannerView {

    public DefaultBannerView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.banner_default, null);
        ImageView target = (ImageView) v.findViewById(R.id.banner_image);
        bindEventAndShow(v, target);
        return v;
    }
}

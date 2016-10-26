package com.leo.lu.bannerauto.bannertypes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.leo.lu.bannerauto.R;

/**
 * Created by LeoLu on 2016/10/20.
 */

public class TextBannerView extends BaseBannerView {

    public TextBannerView(Context context) {
        super(context);
    }

    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.banner_text, null);
        ImageView target = (ImageView) v.findViewById(R.id.banner_image);
        TextView description = (TextView) v.findViewById(R.id.description);
        description.setText(getDescription());
        bindEventAndShow(v, target);
        return v;
    }
}

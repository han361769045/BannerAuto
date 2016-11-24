package com.bannerauto.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.leo.lu.bannerauto.BannerLayout;
import com.leo.lu.bannerauto.bannertypes.DefaultBannerView;

/**
 * Created by LeoLu on 2016/10/26.
 */

public class MainActivity extends AppCompatActivity {

    BannerLayout bl_bl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bl_bl = (BannerLayout) findViewById(R.id.bl_bl);
        for (int i = 0; i < 10; i++) {
            DefaultBannerView defaultBannerView = new DefaultBannerView(this);
            defaultBannerView.image(R.mipmap.ic_launcher);
            bl_bl.addBanner(defaultBannerView);
        }
    }
}

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
            defaultBannerView.image("http://img.zcool.cn/community/0117e2571b8b246ac72538120dd8a4.jpg@1280w_1l_2o_100sh.jpg");
            bl_bl.addBanner(defaultBannerView);
        }
    }
}

package com.leo.lu.bannerauto.transforms;

import android.view.View;

/**
 * Created by LeoLu on 2016/11/24.
 */

public class RotateYTransformer extends ABaseTransformer {

    private static final float DEFAULT_MAX_ROTATE = 35f;
    private float mMaxRotate = DEFAULT_MAX_ROTATE;
    private static final float DEFAULT_CENTER = 0.5f;

    public static final float MAX_SCALE = 1.2f;
    public static final float MIN_SCALE = 0.6f;

    @Override
    protected void onTransform(View view, float position) {
//        view.setPivotY(view.getHeight() / 2);
//        if (position < -1) { // [-Infinity,-1)
//            // This page is way off-screen to the left.
//            view.setRotationY(-1 * mMaxRotate);
//            view.setPivotX(view.getWidth());
//        } else if (position <= 1) { // [-1,1]
//            // Modify the default slide transition to shrink the page as well
//            view.setRotationY(position * mMaxRotate);
//            if (position < 0)//[0,-1]
//            {
//                view.setPivotX(view.getWidth() * (DEFAULT_CENTER + DEFAULT_CENTER * (-position)));
//                view.setPivotX(view.getWidth());
//            } else//[1,0]
//            {
//                view.setPivotX(view.getWidth() * DEFAULT_CENTER * (1 - position));
//                view.setPivotX(0);
//            }
//            // Scale the page down (between MIN_SCALE and 1)
//        } else { // (1,+Infinity]
//            // This page is way off-screen to the right.
//            view.setRotationY(1 * mMaxRotate);
//            view.setPivotX(0);
//        }
        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }
        float tempScale = position < 0 ? 1 + position : 1 - position;
        float slope = (MAX_SCALE - MIN_SCALE) / 1;
        //一个公式
        float scaleValue = MIN_SCALE + tempScale * slope;
        view.setScaleX(scaleValue);
        view.setScaleY(scaleValue);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//            view.getParent().requestLayout();
//        }
    }

//    protected void onPostTransform(View view, float position) {
//    }
//
    protected void onPreTransform(View view, float position) {
    }

    protected boolean hideOffscreenPages() {
        return false;
    }
}

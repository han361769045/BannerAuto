package com.leo.lu.bannerauto.animation;

import android.view.View;

/**
 * This interface gives you chance to inject your own animation or do something when the
 * {@link android.support.v4.view.ViewPager} animation (PagerTransformer) starts or ends.
 * There are two items you have to know. The first item is the slider you are dragging. This item
 * I call it Current Item. The second is the slider that gonna to show. I call that Next Item.
 * <p>
 * When you start to drag the slider in front of you, onPrepareCurrentItemLeaveScreen() and
 * onPrepareNextItemShowInScreen will be called.
 * <p>
 * When you finish drag, the onCurrentItemDisappear and onNextItemAppear will be invoked.
 * <p>
 * You can see a demo class {@link com.leo.lu.bannerauto.animation.DescriptionAnimation},
 * this class gives the description text an animation.
 */
public interface BaseAnimationInterface {

    /**
     * When the current item prepare to start leaving the screen.
     *
     * @param current current
     */
    public void onPrepareCurrentItemLeaveScreen(View current);

    /**
     * The next item which will be shown in ViewPager/
     *
     * @param next next
     */
    public void onPrepareNextItemShowInScreen(View next);

    /**
     * Current item totally disappear from screen.
     *
     * @param view view
     */
    public void onCurrentItemDisappear(View view);

    /**
     * Next item totally show in screen.
     *
     * @param view view
     */
    public void onNextItemAppear(View view);
}
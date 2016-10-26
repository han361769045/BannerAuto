/*
 * Copyright 2014 Toxic Bakery
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leo.lu.bannerauto.transforms;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

import com.leo.lu.bannerauto.animation.BaseAnimationInterface;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ABaseTransformer implements PageTransformer {

    private BaseAnimationInterface mCustomAnimationInterface;

    private HashMap<View, ArrayList<Float>> h = new HashMap<>();

    /**
     * Called each {@link #transformPage(View, float)}.
     *
     * @param View     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center position of the pager. 0 is front and
     *                 center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    protected abstract void onTransform(View View, float position);

    /**
     * Apply a property transformation to the given page. For most use cases, this method should not be overridden.
     * Instead use {@link #transformPage(View, float)} to perform typical transformations.
     *
     * @param page     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center position of the pager. 0 is front and
     *                 center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    @Override
    public void transformPage(View page, float position) {
        onPreTransform(page, position);
        onTransform(page, position);
        onPostTransform(page, position);
    }

    /**
     * If the position offset of a fragment is less than negative one or greater than one, returning true will set the
     * fragment alpha to 0f. Otherwise fragment alpha is always defaulted to 1f.
     *
     * @return boolean
     */
    protected boolean hideOffscreenPages() {
        return true;
    }

    /**
     * Indicates if the default animations of the view pager should be used.
     *
     * @return boolean
     */
    protected boolean isPagingEnabled() {
        return false;
    }

    /**
     * Called each {@link #transformPage(View, float)} before {{@link #onTransform(View, float)}.
     * <p>
     * The default implementation attempts to reset all view properties. This is useful when toggling transforms that do
     * not modify the same page properties. For instance changing from a transformation that applies rotation to a
     * transformation that fades can inadvertently leave a fragment stuck with a rotation or with some degree of applied
     * alpha.
     *
     * @param view     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center position of the pager. 0 is front and
     *                 center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    @SuppressWarnings("ResourceType")
    protected void onPreTransform(View view, float position) {
        final float width = view.getWidth();

        view.setRotationX(0);
        view.setRotationY(0);
        view.setRotation(0);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setPivotX(0);
        view.setPivotY(0);
        view.setTranslationY(0);
        view.setTranslationX(isPagingEnabled() ? 0f : -width * position);

        if (hideOffscreenPages()) {
            view.setAlpha(position <= -1f || position >= 1f ? 0f : 1f);
            view.setEnabled(false);
        } else {
            view.setEnabled(true);
            view.setAlpha(1f);
        }
        if (mCustomAnimationInterface != null) {
            if (h.containsKey(view) || h.get(view).size() == 1) {
                if (position > -1 && position < 1) {
                    if (h.get(view) == null) {
                        h.put(view, new ArrayList<Float>());
                    }
                    h.get(view).add(position);
                    if (h.get(view).size() == 2) {
                        float zero = h.get(view).get(0);
                        float cha = h.get(view).get(1) - h.get(view).get(0);
                        if (zero > 0) {
                            if (cha > -1 && cha < 0) {
                                //in
                                mCustomAnimationInterface.onPrepareNextItemShowInScreen(view);
                            } else {
                                //out
                                mCustomAnimationInterface.onPrepareCurrentItemLeaveScreen(view);
                            }
                        } else {
                            if (cha > -1 && cha < 0) {
                                //out
                                mCustomAnimationInterface.onPrepareCurrentItemLeaveScreen(view);
                            } else {
                                //in
                                mCustomAnimationInterface.onPrepareNextItemShowInScreen(view);
                            }
                        }
                    }
                }
            }
        }

    }
    boolean isApp,isDis;
    /**
     * Called each {@link #transformPage(View, float)} after {@link #onTransform(View, float)}.
     *
     * @param view     Apply the transformation to this page
     * @param position Position of page relative to the current front-and-center position of the pager. 0 is front and
     *                 center. 1 is one full page position to the right, and -1 is one page position to the left.
     */
    protected void onPostTransform(View view, float position) {
        if(mCustomAnimationInterface != null){
            if(position == -1 || position == 1){
                mCustomAnimationInterface.onCurrentItemDisappear(view);
                isApp = true;
            }else if(position == 0){
                mCustomAnimationInterface.onNextItemAppear(view);
                isDis = true;
            }
            if(isApp && isDis){
                h.clear();
                isApp = false;
                isDis = false;
            }
        }
    }

    /**
     * Same as {@link Math#min(double, double)} without double casting, zero closest to infinity handling, or NaN support.
     *
     * @param val val
     * @param min min
     * @return boolean
     */
    protected static final float min(float val, float min) {
        return val < min ? min : val;
    }


    public void setCustomAnimationInterface(BaseAnimationInterface animationInterface) {
        mCustomAnimationInterface = animationInterface;
    }

}

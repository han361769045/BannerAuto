package com.leo.lu.bannerauto;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.leo.lu.bannerauto.animation.BaseAnimationInterface;
import com.leo.lu.bannerauto.bannertypes.BaseBannerView;
import com.leo.lu.bannerauto.indicators.PagerIndicator;
import com.leo.lu.bannerauto.transforms.ABaseTransformer;
import com.leo.lu.bannerauto.transforms.AccordionTransformer;
import com.leo.lu.bannerauto.transforms.BackgroundToForegroundTransformer;
import com.leo.lu.bannerauto.transforms.CubeInTransformer;
import com.leo.lu.bannerauto.transforms.CubeOutTransformer;
import com.leo.lu.bannerauto.transforms.DefaultTransformer;
import com.leo.lu.bannerauto.transforms.DepthPageTransformer;
import com.leo.lu.bannerauto.transforms.FadeTransformer;
import com.leo.lu.bannerauto.transforms.FlipHorizontalTransformer;
import com.leo.lu.bannerauto.transforms.FlipVerticalTransformer;
import com.leo.lu.bannerauto.transforms.ForegroundToBackgroundTransformer;
import com.leo.lu.bannerauto.transforms.RotateDownTransformer;
import com.leo.lu.bannerauto.transforms.RotateUpTransformer;
import com.leo.lu.bannerauto.transforms.ScaleInOutTransformer;
import com.leo.lu.bannerauto.transforms.StackTransformer;
import com.leo.lu.bannerauto.transforms.TabletTransformer;
import com.leo.lu.bannerauto.transforms.ZoomInTransformer;
import com.leo.lu.bannerauto.transforms.ZoomOutSlideTransformer;
import com.leo.lu.bannerauto.transforms.ZoomOutTranformer;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LeoLu on 2016/10/20.
 */

public class BannerLayout extends RelativeLayout {

    private Context mContext;
    /**
     * InfiniteViewPager is extended from ViewPagerEx. As the name says, it can scroll without bounder.
     */
    private InfiniteViewPager mViewPager;

    /**
     * InfiniteViewPager adapter.
     */
    private BannerAdapter mBannerAdapter;

    /**
     * {@link android.support.v4.view.ViewPager} indicator.
     */
    private PagerIndicator mIndicator;

    /**
     * A timer and a TimerTask using to cycle the {@link android.support.v4.view.ViewPager}.
     */
    private Timer mCycleTimer;
    private TimerTask mCycleTask;

    /**
     * For resuming the cycle, after user touch or click the {@link android.support.v4.view.ViewPager}.
     */
    private Timer mResumingTimer;
    private TimerTask mResumingTask;

    /**
     * If {@link android.support.v4.view.ViewPager} is Cycling
     */
    private boolean mCycling;

    /**
     * Determine if auto recover after user touch the {@link android.support.v4.view.ViewPager}
     */
    private boolean mAutoRecover = true;

    private int mTransformerId;

    /**
     * {@link android.support.v4.view.ViewPager} transformer time span.
     */
    private int mTransformerSpan;

    private boolean mAutoCycle;

    /**
     * the duration between animation.
     */
    private long mBannerDuration = 4000;

    /**
     * Visibility of {@link com.leo.lu.bannerauto.indicators.PagerIndicator}
     */
    private PagerIndicator.IndicatorVisibility mIndicatorVisibility = PagerIndicator.IndicatorVisibility.Visible;

    /**
     * {@link android.support.v4.view.ViewPager} 's transformer
     */
    private ABaseTransformer mViewPagerTransformer;

    /**
     * @see com.leo.lu.bannerauto.animation.BaseAnimationInterface
     */
    private BaseAnimationInterface mCustomAnimation;

    private int pagerIndicator;

    private int pagerIndicatorPlace;

    private ViewStub vs, vs_default;

    private Drawable pagerNoBanner;

    private ImageView imageView;

    public BannerLayout(Context context) {
        this(context, null);
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.banner_layout, this, true);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BannerLayout,
                defStyle, 0);

        mTransformerSpan = attributes.getInteger(R.styleable.BannerLayout_pager_animation_span, 1100);
        mTransformerId = attributes.getInt(R.styleable.BannerLayout_pager_animation, Transformer.Default.ordinal());
        mAutoCycle = attributes.getBoolean(R.styleable.BannerLayout_auto_cycle, true);
        int visibility = attributes.getInt(R.styleable.BannerLayout_indicator_visibility, 0);
        for (PagerIndicator.IndicatorVisibility v : PagerIndicator.IndicatorVisibility.values()) {
            if (v.ordinal() == visibility) {
                mIndicatorVisibility = v;
                break;
            }
        }
        mBannerAdapter = new BannerAdapter(mContext, this);
        PagerAdapter wrappedAdapter = new InfinitePagerAdapter(mBannerAdapter);

        mViewPager = (InfiniteViewPager) findViewById(R.id.banner_viewpager);
        mViewPager.setAdapter(wrappedAdapter);
        mViewPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        recoverCycle();
                        break;
                }
                return false;
            }
        });
        pagerNoBanner = attributes.getDrawable(R.styleable.BannerLayout_pager_no_banner);
        pagerIndicator = attributes.getResourceId(R.styleable.BannerLayout_pager_indicator, 0);
        pagerIndicatorPlace = attributes.getInt(R.styleable.BannerLayout_pager_indicator_place, 0);
        vs_default = (ViewStub) findViewById(R.id.vs_default);
        attributes.recycle();
        init();
    }

    public void showDefault() {
        if (pagerNoBanner != null && imageView == null) {
            imageView = (ImageView) vs_default.inflate();
            imageView.setImageDrawable(pagerNoBanner);
        } else if (imageView != null) {
            imageView.setVisibility(VISIBLE);
        }
    }

    public void hideDefault() {
        if (imageView != null && imageView.isShown())
            imageView.setVisibility(GONE);
    }


    public boolean isInEditMode() {
        return true;
    }

    private void init() {
        vs = (ViewStub) findViewById(R.id.vs_indicator);
        if (pagerIndicator == 0) {
            setIndicator();
        } else {
            vs.setLayoutResource(pagerIndicator);
            View v = vs.inflate();
            if (v instanceof PagerIndicator) {
                setCustomIndicator((PagerIndicator) v);
            } else {
                throw new RuntimeException(" Unsupported PagerIndicator, it must be  PagerIndicator");
            }
        }
        setPresetTransformer(mTransformerId);
        setBannerTransformDuration(mTransformerSpan, null);
        setIndicatorVisibility(mIndicatorVisibility);
        if (mAutoCycle) {
            startAutoCycle();
        }
    }


    private void setIndicator() {
        vs.setLayoutResource(R.layout.default_indicator);
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) vs.getLayoutParams();
        switch (pagerIndicatorPlace) {
            case 0:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 1:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                break;
            case 2:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                break;
            case 3:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 4:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
                break;
            case 5:
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                break;
        }
        setCustomIndicator((PagerIndicator) vs.inflate());
    }


    public void addOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        if (onPageChangeListener != null) {
            mViewPager.addOnPageChangeListener(onPageChangeListener);
        }
    }

    public void removeOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mViewPager.removeOnPageChangeListener(onPageChangeListener);
    }

    public void setCustomIndicator(PagerIndicator indicator) {
        if (mIndicator != null) {
            mIndicator.destroySelf();
        }
        mIndicator = indicator;
        mIndicator.setIndicatorVisibility(mIndicatorVisibility);
        mIndicator.setViewPager(mViewPager);
        mIndicator.redraw();
    }

    public <T extends BaseBannerView> void addBanner(T imageContent) {
        mBannerAdapter.addBanner(imageContent);
    }

    private android.os.Handler mh = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            moveNextPosition(true);
        }
    };

    public void startAutoCycle() {
        startAutoCycle(1000, mBannerDuration, mAutoRecover);
    }

    /**
     * start auto cycle.
     *
     * @param delay       delay time
     * @param duration    animation duration time.
     * @param autoRecover if recover after user touches the banner.
     */
    public void startAutoCycle(long delay, long duration, boolean autoRecover) {
        if (mCycleTimer != null) mCycleTimer.cancel();
        if (mCycleTask != null) mCycleTask.cancel();
        if (mResumingTask != null) mResumingTask.cancel();
        if (mResumingTimer != null) mResumingTimer.cancel();
        mBannerDuration = duration;
        mCycleTimer = new Timer();
        mAutoRecover = autoRecover;
        mCycleTask = new TimerTask() {
            @Override
            public void run() {
                mh.sendEmptyMessage(0);
            }
        };
        mCycleTimer.schedule(mCycleTask, delay, mBannerDuration);
        mCycling = true;
        mAutoCycle = true;
    }

    /**
     * pause auto cycle.
     */
    private void pauseAutoCycle() {
        if (mCycling) {
            mCycleTimer.cancel();
            mCycleTask.cancel();
            mCycling = false;
        } else {
            if (mResumingTimer != null && mResumingTask != null) {
                recoverCycle();
            }
        }
    }

    /**
     * set the duration between two  changes. the duration value must le 500
     *
     * @param duration duration
     */
    public void setDuration(long duration) {
        if (duration >= 500) {
            mBannerDuration = duration;
            if (mAutoCycle && mCycling) {
                startAutoCycle();
            }
        }
    }

    /**
     * stop the auto circle
     */
    public void stopAutoCycle() {
        if (mCycleTask != null) {
            mCycleTask.cancel();
        }
        if (mCycleTimer != null) {
            mCycleTimer.cancel();
        }
        if (mResumingTimer != null) {
            mResumingTimer.cancel();
        }
        if (mResumingTask != null) {
            mResumingTask.cancel();
        }
        mAutoCycle = false;
        mCycling = false;
    }

    /**
     * when paused cycle, this method can weak it up.
     */
    private void recoverCycle() {
        if (!mAutoRecover || !mAutoCycle) {
            return;
        }

        if (!mCycling) {
            if (mResumingTask != null && mResumingTimer != null) {
                mResumingTimer.cancel();
                mResumingTask.cancel();
            }
            mResumingTimer = new Timer();
            mResumingTask = new TimerTask() {
                @Override
                public void run() {
                    startAutoCycle();
                }
            };
            mResumingTimer.schedule(mResumingTask, 6000);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseAutoCycle();
                break;
        }
        return false;
    }

    /**
     * set ViewPager transformer.
     *
     * @param reverseDrawingOrder reverseDrawingOrder
     * @param transformer transformer
     */
    public void setPagerTransformer(boolean reverseDrawingOrder, ABaseTransformer transformer) {
        mViewPagerTransformer = transformer;
        mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        mViewPager.setPageTransformer(reverseDrawingOrder, mViewPagerTransformer);
    }


    /**
     * set the duration between two banner changes.
     *
     * @param period period
     * @param interpolator interpolator
     */
    public void setBannerTransformDuration(int period, Interpolator interpolator) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(), interpolator, period);
            mScroller.set(mViewPager, scroller);
        } catch (Exception e) {

        }
    }

    /**
     * preset transformers and their names
     */
    public enum Transformer {
        Default("Default"),
        Accordion("Accordion"),
        Background2Foreground("Background2Foreground"),
        CubeIn("CubeIn"),
        CubeOut("CubeOut"),
        DepthPage("DepthPage"),
        Fade("Fade"),
        FlipHorizontal("FlipHorizontal"),
        FlipVertical("FlipVertical"),
        Foreground2Background("Foreground2Background"),
        RotateDown("RotateDown"),
        RotateUp("RotateUp"),
        ScaleInOut("ScaleInOut"),
        Stack("Stack"),
        Tablet("Tablet"),
        ZoomIn("ZoomIn"),
        ZoomOutSlide("ZoomOutSlide"),
        ZoomOut("ZoomOut");

        private final String name;

        Transformer(String s) {
            name = s;
        }

        public String toString() {
            return name;
        }

        public boolean equals(String other) {
            return other != null && name.equals(other);
        }
    }

    ;

    /**
     * set a preset viewpager transformer by id.
     *
     * @param transformerId transformerId
     */
    public void setPresetTransformer(int transformerId) {
        for (Transformer t : Transformer.values()) {
            if (t.ordinal() == transformerId) {
                setPresetTransformer(t);
                break;
            }
        }
    }

    /**
     * set preset PagerTransformer via the name of transforemer.
     *
     * @param transformerName transformerName
     */
    public void setPresetTransformer(String transformerName) {
        for (Transformer t : Transformer.values()) {
            if (t.equals(transformerName)) {
                setPresetTransformer(t);
                return;
            }
        }
    }

    /**
     * Inject your custom animation into PageTransformer, you can know more details in
     * {@link com.leo.lu.bannerauto.animation.BaseAnimationInterface},
     * and you can see a example in {@link com.leo.lu.bannerauto.animation.DescriptionAnimation}
     *
     * @param animation animation
     */
    public void setCustomAnimation(BaseAnimationInterface animation) {
        mCustomAnimation = animation;
        if (mViewPagerTransformer != null) {
            mViewPagerTransformer.setCustomAnimationInterface(mCustomAnimation);
        }
    }

    /**
     * pretty much right? enjoy it. :-D
     *
     * @param ts ts
     */
    public void setPresetTransformer(Transformer ts) {
        //
        // special thanks to https://github.com/ToxicBakery/ViewPagerTransforms
        //
        ABaseTransformer t = null;
        switch (ts) {
            case Default:
                t = new DefaultTransformer();
                break;
            case Accordion:
                t = new AccordionTransformer();
                break;
            case Background2Foreground:
                t = new BackgroundToForegroundTransformer();
                break;
            case CubeIn:
                t = new CubeInTransformer();
                break;
            case CubeOut:
                t = new CubeOutTransformer();
                break;
            case DepthPage:
                t = new DepthPageTransformer();
                break;
            case Fade:
                t = new FadeTransformer();
                break;
            case FlipHorizontal:
                t = new FlipHorizontalTransformer();
                break;
            case FlipVertical:
                t = new FlipVerticalTransformer();
                break;
            case Foreground2Background:
                t = new ForegroundToBackgroundTransformer();
                break;
            case RotateDown:
                t = new RotateDownTransformer();
                break;
            case RotateUp:
                t = new RotateUpTransformer();
                break;
            case Stack:
                t = new StackTransformer();
                break;
            case ScaleInOut:
                t = new ScaleInOutTransformer();
                break;
            case Tablet:
                t = new TabletTransformer();
                break;
            case ZoomIn:
                t = new ZoomInTransformer();
                break;
            case ZoomOutSlide:
                t = new ZoomOutSlideTransformer();
                break;
            case ZoomOut:
                t = new ZoomOutTranformer();
                break;
        }
        setPagerTransformer(true, t);
    }


    /**
     * Set the visibility of the indicators.
     *
     * @param visibility visibility
     */
    public void setIndicatorVisibility(PagerIndicator.IndicatorVisibility visibility) {
        if (mIndicator == null) {
            return;
        }
        mIndicator.setIndicatorVisibility(visibility);
    }

    public PagerIndicator.IndicatorVisibility getIndicatorVisibility() {
        if (mIndicator != null) {
            return mIndicator.getIndicatorVisibility();
        }
        return PagerIndicator.IndicatorVisibility.Invisible;

    }

    /**
     * get the {@link com.leo.lu.bannerauto.indicators.PagerIndicator} instance.
     * You can manipulate the properties of the indicator.
     *
     * @return PagerIndicator
     */
    public PagerIndicator getPagerIndicator() {
        return mIndicator;
    }

    private InfinitePagerAdapter getWrapperAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            return (InfinitePagerAdapter) adapter;
        } else {
            return null;
        }
    }

    private BannerAdapter getRealAdapter() {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            return ((InfinitePagerAdapter) adapter).getRealAdapter();
        }
        return null;
    }

    /**
     * get the current item position
     *
     * @return int
     */
    public int getCurrentPosition() {

        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a banner adapter");

        return mViewPager.getCurrentItem() % getRealAdapter().getCount();

    }

    /**
     * get current banner.
     *
     * @return BaseBannerView
     */
    public BaseBannerView getCurrentBanner() {

        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a banner adapter");

        int count = getRealAdapter().getCount();
        int realCount = mViewPager.getCurrentItem() % count;
        return getRealAdapter().getBannerView(realCount);
    }

    /**
     * remove  the banner at the position. Notice: It's a not perfect method, a very small bug still exists.
     * @param  position position
     */
    public void removeBannerAt(int position) {
        if (getRealAdapter() != null) {
            getRealAdapter().removeBannerAt(position);
            mViewPager.setCurrentItem(mViewPager.getCurrentItem(), false);
        }
    }

    /**
     * remove all the banners. Notice: It's a not perfect method, a very small bug still exists.
     */
    public void removeAllBanners() {
        if (getRealAdapter() != null) {
            int count = getRealAdapter().getCount();
            getRealAdapter().removeAllBanners();
            //a small bug, but fixed by this trick.
            //bug: when remove adapter's all the s.some caching  still alive.
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + count, false);
        }
    }

    /**
     * set current
     *
     * @param position position
     * @param smooth smooth
     */
    public void setCurrentPosition(int position, boolean smooth) {
        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a  adapter");
        if (position >= getRealAdapter().getCount()) {
            throw new IllegalStateException("Item position is not exist");
        }
        int p = mViewPager.getCurrentItem() % getRealAdapter().getCount();
        int n = (position - p) + mViewPager.getCurrentItem();
        mViewPager.setCurrentItem(n, smooth);
    }

    public void setCurrentPosition(int position) {
        setCurrentPosition(position, true);
    }

    /**
     * move to prev slide.
     *@param  smooth smooth
     */
    public void movePrevPosition(boolean smooth) {

        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a banner adapter");

        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, smooth);
    }

    public void movePrevPosition() {
        movePrevPosition(true);
    }

    /**
     * move to next slide.
     * @param  smooth smooth
     */
    public void moveNextPosition(boolean smooth) {

        if (getRealAdapter() == null)
            throw new IllegalStateException("You did not set a banner adapter");

        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, smooth);
    }

    public void moveNextPosition() {
        moveNextPosition(true);
    }

}

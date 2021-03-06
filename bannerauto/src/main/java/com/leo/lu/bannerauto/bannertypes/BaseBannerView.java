package com.leo.lu.bannerauto.bannertypes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.leo.lu.bannerauto.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by LeoLu on 2016/10/20.
 */

public abstract class BaseBannerView {

    protected Context mContext;

    private Bundle mBundle;

    /**
     * Error place holder image.
     */
    private int mErrorPlaceHolderRes;

    /**
     * Empty imageView placeholder.
     */
    private int mEmptyPlaceHolderRes;

    private String mUrl;
    private File mFile;
    private int mRes;
    protected OnBannerClickListener mOnBannerClickListener;
    private boolean mErrorDisappear;
    private ImageLoadListener mLoadListener;
    private String mDescription;
    private Picasso mPicasso;
    private RequestManager mGlide;
    private boolean isGlide = true;
    /**
     * Scale type of the image.
     */
    private ScaleType mScaleType = ScaleType.Fit;


    public enum ScaleType {
        CenterCrop, CenterInside, Fit, FitCenterCrop
    }

    protected BaseBannerView(Context context) {
        mContext = context;
    }

    /**
     * When you want to implement your own banner view, please call this method in the end in `getView()` method
     *
     * @param v               the whole view
     * @param targetImageView where to place image
     */
    protected void bindEventAndShow(final View v, ImageView targetImageView) {
        final BaseBannerView me = this;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBannerClickListener != null) {
                    mOnBannerClickListener.onBannerClick(me);
                }
            }
        });
        if (targetImageView == null)
            return;
        if (mLoadListener != null) {
            mLoadListener.onStart(me);
        }
        if (isGlide) {
            useGlide(v, targetImageView, me);
        } else {
//            usePicasso(v, targetImageView, me);
        }
    }

//    private void usePicasso(final View v, ImageView targetImageView, final BaseBannerView me) {
//        Picasso p = (mPicasso != null) ? mPicasso : Picasso.with(mContext);
//        RequestCreator rq;
//        if (mUrl != null) {
//            rq = p.load(mUrl);
//        } else if (mFile != null) {
//            rq = p.load(mFile);
//        } else if (mRes != 0) {
//            rq = p.load(mRes);
//        } else {
//            return;
//        }
//
//        if (rq == null) {
//            return;
//        }
//
//        if (getEmpty() != 0) {
//            rq.placeholder(getEmpty());
//        }
//
//        if (getError() != 0) {
//            rq.error(getError());
//        }
//
//        switch (mScaleType) {
//            case Fit:
//                rq.fit();
//                break;
//            case CenterCrop:
//                rq.fit().centerCrop();
//                break;
//            case CenterInside:
//                rq.fit().centerInside();
//                break;
//        }
//
//        rq.into(targetImageView, new Callback() {
//            @Override
//            public void onSuccess() {
//                if (v.findViewById(R.id.loading_bar) != null) {
//                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onError() {
//                if (mLoadListener != null) {
//                    mLoadListener.onEnd(false, me);
//                }
//                if (v.findViewById(R.id.loading_bar) != null) {
//                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
//                }
//            }
//        });
//    }

    private void useGlide(final View v, ImageView targetImageView, final BaseBannerView me) {
        RequestOptions requestOptions = RequestOptions.sizeMultiplierOf(0.3f).centerCrop();
        RequestManager p = (mGlide != null) ? mGlide : Glide.with(mContext);
        if (getEmpty() != 0) {
            requestOptions = requestOptions.placeholder(getEmpty());
        }
        if (getError() != 0) {
            requestOptions = requestOptions.error(getError());
        }
        p.applyDefaultRequestOptions(requestOptions);
        RequestBuilder<Drawable> rq;
        if (mUrl != null) {
            rq = p.load(mUrl);
        } else if (mFile != null) {
            rq = p.load(mFile);
        } else if (mRes != 0) {
            rq = p.load(mRes);
        } else {
            return;
        }
        if (rq == null) {
            return;
        }
        rq.listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (v.findViewById(R.id.loading_bar) != null) {
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }
                if (mLoadListener != null) {
                    mLoadListener.onEnd(false, me);
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                //在这里添加一些图片加载完成的操作
                if (v.findViewById(R.id.loading_bar) != null) {
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }
                return false;
            }
        }).into(targetImageView);
    }


    /**
     * if you set errorDisappear false, this will set a error placeholder image.
     *
     * @param resId image resource id
     * @return BaseBannerView
     */
    public BaseBannerView error(int resId) {
        mErrorPlaceHolderRes = resId;
        return this;
    }

    /**
     * the description of a banner image.
     *
     * @param description description
     * @return BaseBannerView
     */
    public BaseBannerView description(String description) {
        mDescription = description;
        return this;
    }

    /**
     * set a url as a image that preparing to load
     *
     * @param url url
     * @return BaseBannerView
     */
    public BaseBannerView image(String url) {
        if (mFile != null || mRes != 0) {
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mUrl = url;
        return this;
    }

    /**
     * set a file as a image that will to load
     *
     * @param file file
     * @return BaseBannerView
     */
    public BaseBannerView image(File file) {
        if (mUrl != null || mRes != 0) {
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mFile = file;
        return this;
    }

    public BaseBannerView image(int res) {
        if (mUrl != null || mFile != null) {
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mRes = res;
        return this;
    }

    /**
     * lets users add a bundle of additional information
     *
     * @param bundle bundle
     * @return BaseBannerView
     */
    public BaseBannerView bundle(Bundle bundle) {
        mBundle = bundle;
        return this;
    }


    /**
     * the placeholder image when loading image from url or file.
     *
     * @param resId Image resource id
     * @return BaseBannerView
     */
    public BaseBannerView empty(int resId) {
        mEmptyPlaceHolderRes = resId;
        return this;
    }

    /**
     * determine whether remove the image which failed to download or load from file
     *
     * @param disappear disappear
     * @return BaseBannerView
     */
    public BaseBannerView errorDisappear(boolean disappear) {
        mErrorDisappear = disappear;
        return this;
    }

    public BaseBannerView setScaleType(ScaleType type) {
        mScaleType = type;
        return this;
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isErrorDisappear() {
        return mErrorDisappear;
    }

    public int getEmpty() {
        return mEmptyPlaceHolderRes;
    }

    public int getError() {
        return mErrorPlaceHolderRes;
    }

    public String getDescription() {
        return mDescription;
    }

    public Context getContext() {
        return mContext;
    }


    /**
     * set a banner image click listener
     *
     * @param l image click listener
     * @return BaseBannerView
     */
    public BaseBannerView setOnBannerClickListener(OnBannerClickListener l) {
        mOnBannerClickListener = l;
        return this;
    }

    /**
     * the extended class have to implement getView(), which is called by the adapter,
     * every extended class response to render their own view.
     *
     * @return View
     */
    public abstract View getView();

    /**
     * set a listener to get a message , if load error.
     *
     * @param l mLoadListener
     */
    public void setOnImageLoadListener(ImageLoadListener l) {
        mLoadListener = l;
    }


    /**
     * when you have some extra information, please put it in this bundle.
     *
     * @return mBundle
     */
    public Bundle getBundle() {
        return mBundle;
    }

    /**
     * Get the last instance set via setPicasso(), or null if no user provided instance was set
     *
     * @return The current user-provided Picasso instance, or null if none
     */
    public Picasso getPicasso() {
        return mPicasso;
    }

    /**
     * Provide a Picasso instance to use when loading pictures, this is useful if you have a
     * particular HTTP cache you would like to share.
     *
     * @param picasso The Picasso instance to use, may be null to let the system use the default
     *                instance
     */
    public void setPicasso(Picasso picasso) {
        mPicasso = picasso;
    }


    public void setmGlide(RequestManager mGlide) {
        this.mGlide = mGlide;
    }


    public interface OnBannerClickListener {
        void onBannerClick(BaseBannerView banner);
    }

    public interface ImageLoadListener {

        void onStart(BaseBannerView target);

        void onEnd(boolean result, BaseBannerView target);
    }

    public boolean isGlide() {
        return isGlide;
    }

    public BaseBannerView setUseGlide(boolean glide) {
        isGlide = glide;
        return this;
    }
}

package com.bignerdranch.android.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import static android.animation.ObjectAnimator.ofInt;

/**
 * Created by Chris on 4/24/2017.
 */

public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private boolean mAnimationForward;
    private Drawable mSunRings;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mAnimationForward = true;

        mSceneView = view;

        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);

        final Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        final LayerDrawable layers = (LayerDrawable) getActivity().getResources().getDrawable(R.drawable.sun).mutate();
        mSunRings = layers.findDrawableByLayerId(R.id.sun_rings).mutate();

        final ValueAnimator showRingsAnimation = ValueAnimator.ofInt(0, 180);
        showRingsAnimation.setDuration(1300)
                .setRepeatCount(ValueAnimator.INFINITE);

        showRingsAnimation.setRepeatMode(ValueAnimator.REVERSE);

        showRingsAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        showRingsAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int alphaValue = (int) animation.getAnimatedValue();
                mSunRings.setAlpha(alphaValue);
                mSunRings.invalidateSelf();
                mSunView.setBackground(layers);
            }
        });

        showRingsAnimation.start();


        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAnimationForward) {
                    startAnimation();
                    // Update direction for next click
                    mAnimationForward = false;
                } else {
                    reverseAnimation();
                    mAnimationForward = true;
                }
            }
        });

        return view;
    }

    private void startAnimation() {
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);

        heightAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator =
                ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                        .setDuration(3000);

        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightSkyAnimator =
                ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                        .setDuration(3000);

        nightSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);

        animatorSet.start();
    }

    private void reverseAnimation() {
        float sunYStart = mSkyView.getHeight();
        float sunYEnd = mSunView.getTop();

        ObjectAnimator heightAnimator = ObjectAnimator
                .ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(6000);
        heightAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator sunsetSkyAnimator =
                ObjectAnimator.ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                        .setDuration(1000);
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());

        ObjectAnimator blueSkyAnimator =
                ObjectAnimator.ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                        .setDuration(5000);

        blueSkyAnimator.setStartDelay(1000);
        blueSkyAnimator.setEvaluator(new ArgbEvaluator());

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.play(heightAnimator)
                .with(sunsetSkyAnimator)
                .with(blueSkyAnimator);
        animatorSet.start();
    }

}

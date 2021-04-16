package com.manthanvanani.fileorganizer.Constants;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.text.Html;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.manthanvanani.fileorganizer.R;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.io.File;

public class ViewUtils {

    public static void slideView(View view, int currentHeight, int newHeight) {
        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentHeight, newHeight)
                .setDuration(300);

        /* We use an update listener which listens to each tick
         * and manually updates the height of the view  */

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().height = value.intValue();
            view.requestLayout();
        });

        /*  We use an animationSet to play the animation  */

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }
    public static void showFilePopup(View view, File file){
        Balloon balloon = new Balloon.Builder(view.getContext())
                .setLayout(R.layout.item_file_popup)
                .setIsVisibleArrow(false)
                .setWidthRatio(0.85f)
                .setMarginRight(20)
                .setCornerRadius(0f)
                .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                .build();
        ImageView mImgClose = balloon.getContentView().findViewById(R.id.mImgClose);
        TextView mTxtName = balloon.getContentView().findViewById(R.id.mTxtName);
        TextView mTxtUsedStorage = balloon.getContentView().findViewById(R.id.mTxtUsedStorage);
        TextView mTxtModified = balloon.getContentView().findViewById(R.id.mTxtModified);
        TextView mTxtPath = balloon.getContentView().findViewById(R.id.mTxtPath);

        mTxtName.setText(file.getName());
//        mTxtUsedStorage.setText("Size:  "+Utills.convertBytes(file.length()));
        mTxtUsedStorage.setText(Html.fromHtml("<b>Size:   </b> " + Utills.convertBytes(file.length())));
        mTxtModified.setText(Html.fromHtml("<b>Modified:   </b> " + Utills.millisecondToDate(file.lastModified())));
        mTxtPath.setText(Html.fromHtml("<b>Path:   </b> " + file.getAbsolutePath()));
//        mTxtModified.setText("Modified:  "+Utills.millisecondToDate(file.lastModified()));
//        mTxtPath.setText("Path:  "+file.getAbsolutePath());

        mImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balloon.dismiss();
            }
        });
        balloon.show(view);
    }
}

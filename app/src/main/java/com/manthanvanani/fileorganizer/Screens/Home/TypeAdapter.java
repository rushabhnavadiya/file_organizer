package com.manthanvanani.fileorganizer.Screens.Home;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manthanvanani.fileorganizer.Constants.Constants;
import com.manthanvanani.fileorganizer.Constants.Utills;
import com.manthanvanani.fileorganizer.R;
import com.manthanvanani.fileorganizer.Screens.Search.SearchScreen;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.kaopiz.kprogresshud.KProgressHUD;

import co.ceryle.fitgridview.FitGridAdapter;

class TypeAdapter extends FitGridAdapter {

    private int[] drawables = {
            R.drawable.ic_picture, R.drawable.ic_video, R.drawable.ic_music, R.drawable.ic_document,
            R.drawable.ic_apk, R.drawable.ic_zip,R.drawable.ic_download, R.drawable.ic_cloud};
    private String[] typeList = {
            "Image","Video","Music","Document",
            "Apk","Zip File","Download","Cloud"};
    private Context context;
    KProgressHUD kProgressHUD;
    InterstitialAd interstitialAd;

    TypeAdapter(Context context) {
        super(context, R.layout.type_grid_row);
        this.context = context;
        interstitialAd = new InterstitialAd(context, Constants.FB_INTERSTITIAL);

    }

    @Override
    public void onBindView(final int position, View itemView) {
        ImageView mImgType = (ImageView) itemView.findViewById(R.id.mImgType);
        TextView mTxtType = itemView.findViewById(R.id.mTxtType);
        LinearLayout mLLRootType = itemView.findViewById(R.id.mLLRootType);
        mImgType.setImageResource(drawables[position]);
        mTxtType.setText(typeList[position]);
        itemView.setBackgroundResource(R.drawable.ripple_effect);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               kProgressHUD = Utills.showLoader(view.getContext());
                AbstractAdListener adListener = new AbstractAdListener() {
                    @Override
                    public void onError(Ad ad, AdError error) {
                        super.onError(ad, error);
                        kProgressHUD.dismiss();
                        clickEvent(position);

                    }
                    @Override
                    public void onAdLoaded(Ad ad) {
                        super.onAdLoaded(ad);
                        interstitialAd.show();
                    }
                    @Override
                    public void onAdClicked(Ad ad) {
                        super.onAdClicked(ad);
                    }
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        super.onInterstitialDisplayed(ad);
                    }
                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        super.onInterstitialDismissed(ad);
                        kProgressHUD.dismiss();
                        clickEvent(position);

                    }
                };
                InterstitialAd.InterstitialLoadAdConfig interstitialLoadAdConfig = interstitialAd.buildLoadAdConfig()
                        .withAdListener(adListener)
                        .build();
                interstitialAd.loadAd(interstitialLoadAdConfig);
            }
        });
    }

    void clickEvent(int position){
        if(position == 0){
            try {
                Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY);
                context.startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setType("image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
        else if(position == 1){
            try {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setType("video/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }catch (Exception e){

            }
        }
        else if(position == 2){
            try {
                Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC);
                context.startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                    context.startActivity(intent);
                }catch (Exception e1){
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setType("audio/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }
        else if(position == 3){
            try {
//                        Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_FILES);
//                        context.startActivity(intent);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setType("*/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }catch (Exception e){

            }
        }
        else if(position == 4){
            context.startActivity(new Intent(context, SearchScreen.class).putExtra("type",3));

        }
        else if(position == 5){
            context.startActivity(new Intent(context, SearchScreen.class).putExtra("type",2));

        }
        else if(position == 6){
            try {
                context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
            }catch (Exception e){

            }
        }
        else if(position == 7){
            try {
//                        Intent intent = new Intent();
//                        intent.setAction(android.content.Intent.ACTION_VIEW);
//                        intent.setType("video/*");
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
            }catch (Exception e){

            }
        }
    }
}

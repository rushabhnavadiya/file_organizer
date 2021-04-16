package com.manthanvanani.fileorganizer.Screens.Home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manthanvanani.fileorganizer.Constants.Constants;
import com.manthanvanani.fileorganizer.Constants.GlobalConstants;
import com.manthanvanani.fileorganizer.Constants.Utills;
import com.manthanvanani.fileorganizer.Constants.ViewUtils;
import com.manthanvanani.fileorganizer.R;
import com.manthanvanani.fileorganizer.Screens.Search.SearchScreen;
import com.manthanvanani.fileorganizer.Screens.ShowFile.FileScreen;
import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;
import com.yalantis.taurus.PullToRefreshView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import co.ceryle.fitgridview.FitGridView;

public class HomeScreen extends AppCompatActivity {
    private FitGridView gridView;
    TextView mTxtUsedStorage,mTxtTotalStorage;
    LinearLayout mLLInternalStorage;
    AnimatedPieView mAnimatedPieView;
    ImageButton mImgScrollButton;
    ImageView mImgSearch;
    NestedScrollView mNestedScroll;
    RelativeLayout mRLLoader,mRLRecentFiles,mRLTitleView;
    PullToRefreshView mPullToRefreshView;
    long totalSpace,freeSpace;
    int freePercentage;
    PermissionListener permissionlistener;
    private List<Object> recentFiles = new ArrayList<>();
    private RecyclerView mRVRecentFiles;
    RecentFileAdapter recentFileAdapter;
    LoadingView mPieLoader;
    boolean fullScreenBottomSheet = false;
    InterstitialAd interstitialAd;
    KProgressHUD kProgressHUD;
    int height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        init();
        setView();
        setPadding();
        setData();
        setClick();
        setAdapter();
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                ShowRecentFileBackgroundTask showRecentFileBackgroundTask = new ShowRecentFileBackgroundTask();
                showRecentFileBackgroundTask.execute(new String[] { "currentFile.getAbsolutePath()" });
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                mRLLoader.setVisibility(View.GONE);

            }
        };

        mRLLoader.setVisibility(View.VISIBLE);

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    void init(){
        totalSpace = Utills.totalSpace();
        freeSpace = Utills.freeSpace();
        freePercentage = (int) (((freeSpace*100)/totalSpace));
        recentFileAdapter = new RecentFileAdapter(this,recentFiles);
        height = Utills.getDeviceHeight(this);

        interstitialAd = new InterstitialAd(this, Constants.FB_INTERSTITIAL);
    }

    void setView(){
        mImgSearch = findViewById(R.id.mImgSearch);
        mAnimatedPieView = findViewById(R.id.pie);
        gridView = findViewById(R.id.gridView);
        mTxtUsedStorage = findViewById(R.id.mTxtUsedStorage);
        mTxtTotalStorage = findViewById(R.id.mTxtTotalStorage);
        mRVRecentFiles = findViewById(R.id.mRVRecentFiles);
        mRLRecentFiles = findViewById(R.id.mRLRecentFiles);
        mImgScrollButton = findViewById(R.id.mImgScrollButton);
        mNestedScroll = findViewById(R.id.mNestedScroll);
        mLLInternalStorage = findViewById(R.id.mLLInternalStorage);
        mRLLoader = findViewById(R.id.mRLLoader);
        mPieLoader = findViewById(R.id.mPieLoader);
        mRLTitleView = findViewById(R.id.mRLTitleView);
        mPullToRefreshView = findViewById(R.id.pull_to_refresh);

        mRVRecentFiles.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRVRecentFiles.setItemAnimator(new DefaultItemAnimator());
        loadAdFifty();
    }
    void setPadding(){
        mNestedScroll.setPadding(0,0,0,((height/4)-50));
        if(!fullScreenBottomSheet){
            mRLRecentFiles.getLayoutParams().height = (height/(4));
        }
    }

    void setData(){
        mPieLoader.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mAnimatedPieView.setVisibility(View.VISIBLE);
                mPieLoader.setVisibility(View.GONE);
                AnimatedPieViewConfig config = new AnimatedPieViewConfig();
                config.startAngle(-90)// Starting angle offset
                        .addData(new SimplePieInfo(freePercentage, getResources().getColor(R.color.green_color), (freePercentage)+"%"))//Data (bean that implements the IPieInfo interface)
                        .addData(new SimplePieInfo((100-freePercentage), getResources().getColor(R.color.orange_color), (100-freePercentage)+"%"))
                        .duration(1000)
                        .autoSize(true)
                        .guideLineMarginStart(0)
                        .drawText(true)
                        .textSize(40)
                        .strokeWidth(100);

                mAnimatedPieView.applyConfig(config);
                mAnimatedPieView.start();
            }
        }, 3500);

        mTxtUsedStorage.setText(Utills.convertBytes(freeSpace).toUpperCase());
        mTxtTotalStorage.setText("Total: "+Utills.convertBytes(totalSpace).toUpperCase());
    }

    void setClick(){
        mImgScrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullScreenBottomSheet){
                    ViewUtils.slideView(mRLRecentFiles,(int)(height/(1.05)),(height/(4)));
                    mImgScrollButton.setImageResource(R.drawable.ic_up_angle);
                    fullScreenBottomSheet = false;
                }else{
                    ViewUtils.slideView(mRLRecentFiles,(height/(4)),(int)(height/(1.05)));
                    mImgScrollButton.setImageResource(R.drawable.ic_down_angle);
                    fullScreenBottomSheet = true;
                }
            }
        });
        mLLInternalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kProgressHUD = Utills.showLoader(v.getContext());
                AbstractAdListener adListener = new AbstractAdListener() {
                    @Override
                    public void onError(Ad ad, AdError error) {
                        super.onError(ad, error);
                        kProgressHUD.dismiss();
                        startActivity(new Intent(HomeScreen.this, FileScreen.class));

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
                        startActivity(new Intent(HomeScreen.this, FileScreen.class));
                    }
                };
                InterstitialAd.InterstitialLoadAdConfig interstitialLoadAdConfig = interstitialAd.buildLoadAdConfig()
                        .withAdListener(adListener)
                        .build();
                interstitialAd.loadAd(interstitialLoadAdConfig);


            }
        });
        mImgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kProgressHUD = Utills.showLoader(v.getContext());

                AbstractAdListener adListener = new AbstractAdListener() {
                    @Override
                    public void onError(Ad ad, AdError error) {
                        super.onError(ad, error);
                        kProgressHUD.dismiss();
                        startActivity(new Intent(v.getContext(), SearchScreen.class).putExtra("type",1));
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
                        startActivity(new Intent(v.getContext(), SearchScreen.class).putExtra("type",1));
                    }
                };
                InterstitialAd.InterstitialLoadAdConfig interstitialLoadAdConfig = interstitialAd.buildLoadAdConfig()
                        .withAdListener(adListener)
                        .build();
                interstitialAd.loadAd(interstitialLoadAdConfig);

            }
        });

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                kProgressHUD = Utills.showLoader(HomeScreen.this);

                AbstractAdListener adListener = new AbstractAdListener() {
                    @Override
                    public void onError(Ad ad, AdError error) {
                        super.onError(ad, error);
                        kProgressHUD.dismiss();
                        TedPermission.with(HomeScreen.this)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .check();
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
                        TedPermission.with(HomeScreen.this)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .check();
                    }
                };
                InterstitialAd.InterstitialLoadAdConfig interstitialLoadAdConfig = interstitialAd.buildLoadAdConfig()
                        .withAdListener(adListener)
                        .build();
                interstitialAd.loadAd(interstitialLoadAdConfig);

            }
        });
    }

    void setAdapter(){
        gridView.setFitGridAdapter(new TypeAdapter(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gridView.setNestedScrollingEnabled(false);
        }
        mRVRecentFiles.setAdapter(recentFileAdapter);

    }

    private class ShowRecentFileBackgroundTask extends AsyncTask<String, Void, List<File>> {
        @Override
        protected List<File> doInBackground(String... urls) {
            try {
                List<File> fileList = GlobalConstants.getAllFiles("/");

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Collections.sort(fileList, Comparator.comparingLong(File::lastModified).reversed());
//
//            }else{
//
//            }
                Collections.sort(fileList,(o1,o2)-> {
                    return new Date(o2.lastModified()).compareTo(new Date(o1.lastModified()));
                });

                return fileList;

            }catch (Exception e){
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<File> result) {
            recentFiles.clear();
            recentFiles.addAll(result);
            mRLLoader.setVisibility(View.GONE);
            if(recentFiles.size() >3){
                recentFileAdapter.initNativeAds();
            }
            recentFileAdapter.notifyDataSetChanged();
            mPullToRefreshView.setRefreshing(false);


//                Log.d("LOG", "FileName:" + files.get(0));
        }
    }

    @Override
    public void onBackPressed() {
        if(fullScreenBottomSheet){
            ViewUtils.slideView(mRLRecentFiles,(int)(height/(1.05)),(height/(4)));
            fullScreenBottomSheet = false;
        }else{
            super.onBackPressed();
        }
    }

    private void loadAdFifty() {
        final RelativeLayout adContainer = findViewById(R.id.ad_banner_50);
        AdView adView = new AdView(this, Constants.FB_BANNER_50, AdSize.BANNER_HEIGHT_50);
        adContainer.addView(adView);
        AdListener adListener = new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
//                Toast.makeText(Ad_Banner.this, "Ad 50 Error: " + adError.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
//                Toast.makeText(Ad_Banner.this, "Ad Loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        };
        AdView.AdViewLoadConfig loadAdConfig = adView.buildLoadAdConfig()
                .withAdListener(adListener)
                .build();
        adView.loadAd(loadAdConfig);
    }

    private void showInterstitialAd(){
        AbstractAdListener adListener = new AbstractAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                super.onError(ad, error);
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
            }
        };
        InterstitialAd.InterstitialLoadAdConfig interstitialLoadAdConfig = interstitialAd.buildLoadAdConfig()
                .withAdListener(adListener)
                .build();
        interstitialAd.loadAd(interstitialLoadAdConfig);
    }


}
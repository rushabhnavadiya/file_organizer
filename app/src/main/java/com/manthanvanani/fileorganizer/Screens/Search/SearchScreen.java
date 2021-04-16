package com.manthanvanani.fileorganizer.Screens.Search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manthanvanani.fileorganizer.Constants.Constants;
import com.manthanvanani.fileorganizer.Constants.FileUtils;
import com.manthanvanani.fileorganizer.Constants.GlobalConstants;
import com.manthanvanani.fileorganizer.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.yalantis.taurus.PullToRefreshView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;

public class SearchScreen extends AppCompatActivity {
    SearchAdapter searchAdapter;
    RecyclerView mRVSearch;
    PullToRefreshView mPullToRefreshView;
    SearchView mSVSearch;
    ImageView mImgBack;
    TextView mTxtTitle;
    RelativeLayout mRLLoader;
    LoadingView loadingView;
    PermissionListener permissionlistener;

    int type = 1 ;
    private final List<Object> searchedFile = new ArrayList<>();
    private List<Object> allFile = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        loadAd();
        init();
        setView();
        setPadding();
        setData();
        setClick();
        setAdapter();
    }
    void init(){
        if(getIntent()!= null){
            type = getIntent().getIntExtra("type",1);
        }
        searchAdapter = new SearchAdapter(this,searchedFile);
        if(type == 1){
            searchedFile.addAll(GlobalConstants.getAllFiles());
            allFile.addAll(searchedFile);
        }else if(type == 2){
            searchedFile.addAll(GlobalConstants.getAllZipFiles());
            allFile.addAll(searchedFile);
        }else if(type == 3){
            searchedFile.addAll(GlobalConstants.getAllApkFiles());
            allFile.addAll(searchedFile);
        }

        searchAdapter.notifyDataSetChanged();
    }

    void setView(){
        mRVSearch = findViewById(R.id.mRVSearch);
        mPullToRefreshView = findViewById(R.id.pull_to_refresh);

        mSVSearch = findViewById(R.id.mSVSearch);
        mImgBack = findViewById(R.id.mImgBack);
        mTxtTitle = findViewById(R.id.mTxtTitle);
        mRLLoader = findViewById(R.id.mRLLoader);
        loadingView = findViewById(R.id.loadingView);


        mRVSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRVSearch.setItemAnimator(new DefaultItemAnimator());
//        LoadingRenderer loadingRenderer = new SwapLoadingRenderer.Builder(this)
//                .setWidth((int) DensityUtil.dip2px(this, 10))
//                .setHeight((int) DensityUtil.dip2px(this, 40))
//                .setBallRadius((int) DensityUtil.dip2px(this, 10))
//                .setBallInterval((int) DensityUtil.dip2px(this, 3))
//                .setStrokeWidth((int) DensityUtil.dip2px(this, 1))
//                .setColor(Color.BLUE)
//                .setDuration(4000)
//                .setBallCount(7)
//                .build();
//        LoadingRenderer loadingRenderer = new CollisionLoadingRenderer.Builder(this)
//                .setWidth((int) DensityUtil.dip2px(this, 150))
//                .setHeight((int) DensityUtil.dip2px(this, 40))
//                .setColors(new int[] {Color.GRAY, Color.CYAN})
//                .setBallRadius((int) DensityUtil.dip2px(this, 10))
//                .setBallMoveXOffsets((int) DensityUtil.dip2px(this, 20))
//                .setBallQuadCoefficient(4)
//                .setBallCount(5)
//                .setDuration(4000)
//                .build();
//        LoadingRenderer builder = new LoadingRenderer(this) {
//            @Override
//            protected void computeRender(float renderProgress) {
//
//            }
//
//            @Override
//            protected void setAlpha(int alpha) {
//
//            }
//
//            @Override
//            protected void setColorFilter(ColorFilter cf) {
//                PorterDuffColorFilter greyFilter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
//
//                setColorFilter(greyFilter);
//            }
//
//            @Override
//            protected void reset() {
//
//            }
//        };
//        loadingView.setLoadingRenderer(loadingRenderer);

    }
    void setPadding(){

    }

    void setData(){
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                AllFileBackgroundTask showRecentFileBackgroundTask = new AllFileBackgroundTask();
                showRecentFileBackgroundTask.execute();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                mRLLoader.setVisibility(View.GONE);

            }
        };

        if(allFile.size() == 0){
            mRLLoader.setVisibility(View.VISIBLE);

            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        }else{
            mSVSearch.setIconified(false);
        }

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                TedPermission.with(SearchScreen.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });


    }

    void setClick(){
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSVSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("LOG", "newText    " + newText);

                searchedFile.clear();
                if(!newText.isEmpty()){
                    for(int i=0;i<allFile.size();i++){
                        if(((File)allFile.get(i)).getName().toLowerCase().contains(newText.toLowerCase())){
                            searchedFile.add(allFile.get(i));
                        }
                    }
                }else{
                    searchedFile.addAll(allFile);
                }
                if(searchedFile.size() >3){
                    searchAdapter.initNativeAds();
                }
                searchAdapter.notifyDataSetChanged();
                return true;
            }
        });
//        mLLInternalStorage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeScreen.this, FileScreen.class));
//            }
//        });
    }

    void setAdapter(){
        mRVSearch.setAdapter(searchAdapter);
    }
    private class AllFileBackgroundTask extends AsyncTask<Void, Void, List<File>> {
        @Override
        protected List<File> doInBackground(Void... voids) {
            try {
                if(type == 2){
                    return FileUtils.getZipFiles("/");
                }else if(type == 3){
                    return FileUtils.getApkFiles("/");
                }else{
                    return FileUtils.getAllFiles("/");
                }
            }catch (Exception e){
                return new ArrayList<>();
            }
        }
        @Override
        protected void onPostExecute(List<File> result) {
            mRLLoader.setVisibility(View.GONE);
            mSVSearch.setIconified(false);
            allFile.clear();
            allFile.addAll(result);
            Log.d("LOG", "mSVSearch   " + mSVSearch.getQuery().toString());

            mSVSearch.setQuery(mSVSearch.getQuery().toString(), false);

            mPullToRefreshView.setRefreshing(false);

        }
    }

    private void loadAd() {
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

}
package com.manthanvanani.fileorganizer.Screens.ShowFile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manthanvanani.fileorganizer.Constants.Constants;
import com.manthanvanani.fileorganizer.Constants.FileUtils;
import com.manthanvanani.fileorganizer.Constants.Utills;
import com.manthanvanani.fileorganizer.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.dinus.com.loadingdrawable.LoadingView;
import app.dinus.com.loadingdrawable.render.LoadingRenderer;
import app.dinus.com.loadingdrawable.render.circle.jump.CollisionLoadingRenderer;


public class FileScreen extends AppCompatActivity {
    ImageView mImgBack,mImgSearch;
    TextView mTxtFolderName,mTxtFolderSize,mTxtFolderPath;
    FileAdapter fileAdapter;
    PermissionListener permissionlistener;
    RelativeLayout mRLLoader,mRLEmpty;
    LoadingView loadingView;
    private List<Object> fileList = new ArrayList<>();
    private RecyclerView mRVFiles;
    static File currentFile;
    public static final int ITEMS_PER_AD = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_screen);
        loadAdSomeTime();
        loadAd();
        init();
        setView();
        setData();
        setClick();
        setAdapter();
    }
    void init(){
        fileAdapter = new FileAdapter(this,fileList);

    }

    void setView(){
        mImgBack = findViewById(R.id.mImgBack);
        mImgSearch = findViewById(R.id.mImgSearch);
        mTxtFolderName = findViewById(R.id.mTxtFolderName);
        mTxtFolderSize = findViewById(R.id.mTxtFolderSize);
        mTxtFolderPath = findViewById(R.id.mTxtFolderPath);
        mRVFiles = findViewById(R.id.mRVFiles);
        mRLLoader = findViewById(R.id.mRLLoader);
        loadingView = findViewById(R.id.loadingView);
        mRLEmpty = findViewById(R.id.mRLEmpty);

        mRVFiles.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRVFiles.setItemAnimator(new DefaultItemAnimator());


    }

    void setData(){
        mRLLoader.setVisibility(View.VISIBLE);

        currentFile = Environment.getExternalStorageDirectory();
        mTxtFolderSize.setText(Utills.convertBytes(currentFile.getUsableSpace()));
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                YoYo.with(Techniques.FadeInRight)
                        .duration(500)
                        .playOn(mRVFiles);
                ShowFileBackgroundTask showFileBackgroundTask = new ShowFileBackgroundTask();
                showFileBackgroundTask.execute(new String[] { currentFile.getAbsolutePath() });
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
        /*LoadingRenderer loadingRenderer = new SwapLoadingRenderer.Builder(this)
                .setColor(getResources().getColor(R.color.light_blue))
                .setDuration(2000)
                .setBallCount(5)
                .build();*/
        LoadingRenderer loadingRenderer = new CollisionLoadingRenderer.Builder(this)
//                .setWidth((int) DensityUtil.dip2px(this, 150))
//                .setHeight((int) DensityUtil.dip2px(this, 40))
                .setColors(new int[] {getResources().getColor(R.color.light_blue), getResources().getColor(R.color.light_blue)})
//                .setBallRadius((int) DensityUtil.dip2px(this, 10))
//                .setBallMoveXOffsets((int) DensityUtil.dip2px(this, 20))
//                .setBallQuadCoefficient(0.04f)
                .setBallCount(5)
                .setDuration(2000)
                .build();


        loadingView.setLoadingRenderer(loadingRenderer);
    }

    void setClick(){
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFile.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())){
                    finish();
                }else{
                    String path = currentFile.getAbsolutePath().replace(currentFile.getAbsolutePath().substring(currentFile.getAbsolutePath().lastIndexOf("/")),"");
                    File directory = new File(path);
                    clickOnRecyclerView(directory);
                }
            }
        });
    }

    void setAdapter(){
        mRVFiles.setAdapter(fileAdapter);
    }


    public void clickOnRecyclerView(File file){
        currentFile = file;
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    public void openFile(){
        if(currentFile.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())){
            mTxtFolderName.setText("Internal Storage");
            mTxtFolderSize.setText(Utills.convertBytes(currentFile.getUsableSpace()));
        }else{
            mTxtFolderName.setText(currentFile.getName());
            mTxtFolderSize.setText(Utills.convertBytes(Utills.folderSize(currentFile)));
        }

        mTxtFolderPath.setText(FileUtils.showedFilePath(currentFile).replace("/"," / "));

        fileList.clear();
        fileList.addAll(FileUtils.getFilesWithFolder(currentFile.getAbsolutePath()));
        fileAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(currentFile.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())){
            super.onBackPressed();
        }else{
            String path = currentFile.getAbsolutePath().replace(currentFile.getAbsolutePath().substring(currentFile.getAbsolutePath().lastIndexOf("/")),"");
            File directory = new File(path);
            clickOnRecyclerView(directory);
        }
    }

    private class ShowFileBackgroundTask extends AsyncTask<String, Void, List<File>> {
        @Override
        protected List<File> doInBackground(String... urls) {
            try {
                return FileUtils.getFilesWithFolder(currentFile.getAbsolutePath());
            }catch (Exception e){
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<File> result) {
            mRLLoader.setVisibility(View.GONE);
            mRLEmpty.setVisibility(View.GONE);

            mRVFiles.scrollToPosition(0);
            if(currentFile.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath())){
                mTxtFolderName.setText("Internal Storage");
                mTxtFolderSize.setText(Utills.convertBytes(currentFile.getUsableSpace()));
            }else{
                mTxtFolderName.setText(currentFile.getName());
                mTxtFolderSize.setText(Utills.convertBytes(Utills.folderSize(currentFile)));
            }

            mTxtFolderPath.setText(FileUtils.showedFilePath(currentFile).replace("/"," / "));

            fileList.clear();
            fileList.addAll(result);
            if(result.size() == 0){
                mRLEmpty.setVisibility(View.VISIBLE);
            }
            if(fileList.size() >3){
                fileAdapter.initNativeAds();
            }

            fileAdapter.notifyDataSetChanged();
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

    private void loadAdSomeTime(){
        final Handler handler = new Handler();
        final int delay = 1000*60; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                loadAd();
                Log.e("MyReceiver"," here!"); // Do your work here

                handler.postDelayed(this, delay);
            }
        }, delay);

    }
}
package com.manthanvanani.fileorganizer.Screens.ShowFile;

import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manthanvanani.fileorganizer.Constants.Constants;
import com.manthanvanani.fileorganizer.Constants.FileUtils;
import com.manthanvanani.fileorganizer.Constants.Utills;
import com.manthanvanani.fileorganizer.Constants.ViewUtils;
import com.manthanvanani.fileorganizer.R;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> fileList;
    private Activity activity;
    private NativeAdsManager fbNativeManager;
    private ArrayList<NativeAd> nativeAd = new ArrayList<>();

    private final int ITEM_TYPE_DATA = 0;
    private final int ITEM_TYPE_AD = 1;

    private final int AD_POSITION = 3;
    private final int AD_POSITION_EVERY_COUNT = 5;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTxtFileName, mTxtFileSize;
        public ImageView mImgFile,mImgMenuFile;
        public LinearLayout mLLRootFile;

        public MyViewHolder(View view) {
            super(view);
            mImgFile = view.findViewById(R.id.mImgFile);
            mImgMenuFile = view.findViewById(R.id.mImgMenuFile);
            mTxtFileName = view.findViewById(R.id.mTxtFileName);
            mTxtFileSize = view.findViewById(R.id.mTxtFileSize);
            mLLRootFile = view.findViewById(R.id.mLLRootFile);
        }
    }
    public class NativeAdViewHolder extends RecyclerView.ViewHolder {
        LinearLayout nativeAdContainer;
        NativeAdViewHolder(@NonNull View itemView) {
            super(itemView);
            nativeAdContainer = itemView.findViewById(R.id.adContainer);
        }
    }
    public FileAdapter(Activity activity,List<Object> fileList) {
        this.activity = activity;
        this.fileList = fileList;
        fbNativeManager = new NativeAdsManager(activity, Constants.FB_NATIVE,3);

    }
    public void initNativeAds(){
        try {
            fbNativeManager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    Log.i("TAG", "onAdsLoaded!" + fbNativeManager.getUniqueNativeAdCount());

                    int count = fbNativeManager.getUniqueNativeAdCount();
                    for(int i=0; i< count; i ++) {
                        NativeAd ad = fbNativeManager.nextNativeAd();
                        addNativeAd(i, ad);
                    }
                }

                @Override
                public void onAdError(AdError adError) {

                }
            });
            fbNativeManager.loadAds();
        }catch (Exception e){

        }

    }
    public void addNativeAd(int i, NativeAd ad) {
        try {
            if (ad == null) {
                return;
            }
//        if (this.nativeAd.size() > i && this.nativeAd.get(i) != null) {
//            this.nativeAd.get(i).unregisterView();
//            this.fileList.remove(AD_POSITION + (i * AD_POSITION_EVERY_COUNT));
//            this.nativeAd = null;
//            this.notifyDataSetChanged();
//        }
            this.nativeAd.add(i, ad);

            if(fileList.size() > (AD_POSITION + (i * AD_POSITION_EVERY_COUNT))) {
                fileList.add(AD_POSITION + (i * AD_POSITION_EVERY_COUNT), ad);
                notifyItemInserted(AD_POSITION + (i * AD_POSITION_EVERY_COUNT));
            }
        }catch (Exception e){

        }

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == ITEM_TYPE_AD)
        {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.native_ads_row, viewGroup, false);
            return new NativeAdViewHolder( v );
        }else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_file, viewGroup, false);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            return new MyViewHolder(v);
        }
        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file, parent, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return new MyViewHolder(itemView);*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof MyViewHolder) {
            File file = (File) fileList.get(position);

            MyViewHolder holder = (MyViewHolder) viewHolder;
            holder.mTxtFileName.setText(file.getName());
            if(file.isDirectory() || !file.getName().contains(".")){
                holder.mImgFile.setImageResource(R.drawable.ic_folder);
                holder.mTxtFileSize.setText(Utills.convertBytes(Utills.folderSize(file)));
                holder.mImgMenuFile.setVisibility(View.INVISIBLE);
            }else{
                holder.mImgFile.setImageResource(Utills.getImageFromFile(file));
                holder.mTxtFileSize.setText(Utills.convertBytes(file.length()));
                holder.mImgMenuFile.setVisibility(View.VISIBLE);
            }
            holder.mLLRootFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("LOG", "");
                    if(((File) fileList.get(position)).isDirectory()){
                        ((FileScreen)activity).clickOnRecyclerView((File) fileList.get(position));
                    }else{
                        try {
                            activity.startActivity(FileUtils.openFile((File) fileList.get(position)));
                        }
                        catch (Exception e){
                            Toast.makeText(activity,activity.getResources().getText(R.string.no_file_available),Toast.LENGTH_SHORT).show();
                            Log.e("OPEN_ERROR",((File) fileList.get(position)).getName()+" --------- "+e.getMessage());
                        }
                    }
                }
            });
            holder.mImgMenuFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("LOG123", "");
                    ViewUtils.showFilePopup(v,file);
                }
            });
        }else{
            NativeAdViewHolder holder = (NativeAdViewHolder) viewHolder;
            NativeAd ad = (NativeAd) this.fileList.get(position);

            View mNativeView = NativeAdView.render(activity, ad, NativeAdView.Type.HEIGHT_300);
            holder.nativeAdContainer.removeAllViews();
            holder.nativeAdContainer.addView(mNativeView);
        }

    }

//    @Override
//    public int getItemCount() {
//        return fileList.size();
//    }
    @Override
    public int getItemCount() {
        if(fileList == null)
            return 0;
        return fileList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if( fileList.get(position) instanceof NativeAd)
            return ITEM_TYPE_AD;
        else
            return ITEM_TYPE_DATA;
    }
    /*public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<File> fileList;
        private Activity activity;
        private NativeAdsManager fbNativeManager;
        private ArrayList<NativeAd> nativeAd = new ArrayList<>();

        private final int ITEM_TYPE_DATA = 0;
        private final int ITEM_TYPE_AD = 1;

        private final int AD_POSITION = 1;
        private final int AD_DISPLAY_FREQUENCY  = 5;
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView mTxtFileName, mTxtFileSize;
            public ImageView mImgFile,mImgMenuFile;
            public LinearLayout mLLRootFile;

            public MyViewHolder(View view) {
                super(view);
                mImgFile = view.findViewById(R.id.mImgFile);
                mImgMenuFile = view.findViewById(R.id.mImgMenuFile);
                mTxtFileName = view.findViewById(R.id.mTxtFileName);
                mTxtFileSize = view.findViewById(R.id.mTxtFileSize);
                mLLRootFile = view.findViewById(R.id.mLLRootFile);
            }
        }
        public class NativeAdViewHolder extends RecyclerView.ViewHolder {
            LinearLayout nativeAdContainer;
            NativeAdViewHolder(@NonNull View itemView) {
                super(itemView);
                nativeAdContainer = itemView.findViewById(R.id.adContainer);
            }
        }

        public FileAdapter(Activity activity,List<File> fileList) {
            this.activity = activity;
            this.fileList = fileList;
            fbNativeManager = new NativeAdsManager(activity, Constants.FB_NATIVE,3);

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType) {
                case ITEM_TYPE_DATA:
                    View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_file, parent, false);
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    return new com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder(itemView);
                case ITEM_TYPE_AD:
                    View itemView1 = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.native_ads_row, parent, false);
                    return new NativeAdViewHolder(itemView1);

            }

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == ITEM_TYPE_DATA) {
                File file = fileList.get(position);

                ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mTxtFileName.setText(file.getName());
                if(file.isDirectory() || !file.getName().contains(".")){
                    ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mImgFile.setImageResource(R.drawable.ic_folder);
                    ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mTxtFileSize.setText(Utills.convertBytes(Utills.folderSize(file)));
                    ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mImgMenuFile.setVisibility(View.INVISIBLE);
                }else{
                    ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mImgFile.setImageResource(Utills.getImageFromFile(file));
                    ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mTxtFileSize.setText(Utills.convertBytes(file.length()));
                    ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mImgMenuFile.setVisibility(View.VISIBLE);
                }
                ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mLLRootFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("LOG", "");
                        if(fileList.get(position).isDirectory()){
                            ((FileScreen)activity).clickOnRecyclerView(fileList.get(position));
                        }else{
                            try {
                                activity.startActivity(FileUtils.openFile(fileList.get(position)));
                            }
                            catch (Exception e){
                                Toast.makeText(activity,activity.getResources().getText(R.string.no_file_available),Toast.LENGTH_SHORT).show();
                                Log.e("OPEN_ERROR",fileList.get(position).getName()+" --------- "+e.getMessage());
                            }
                        }
                    }
                });
                ((com.app.filemanager.Screens.ShowFile.FileAdapter.MyViewHolder)holder).mImgMenuFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("LOG123", "");
                        ViewUtils.showFilePopup(v,file);
                    }
                });
            }
            else {

            }
        }

//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//
//
//    }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position != 0 && position % AD_DISPLAY_FREQUENCY  == 0)
                return ITEM_TYPE_AD;
            else
                return ITEM_TYPE_DATA;
        }
        public void initNativeAds(){
            fbNativeManager.setListener(new NativeAdsManager.Listener() {
                @Override
                public void onAdsLoaded() {
                    Log.i("TAG", "onAdsLoaded!" + fbNativeManager.getUniqueNativeAdCount());

                    int count = fbNativeManager.getUniqueNativeAdCount();
                    for(int i=0; i< count; i ++) {
                        NativeAd ad = fbNativeManager.nextNativeAd();
                        addNativeAd(i, ad);
                    }
                }

                @Override
                public void onAdError(AdError adError) {

                }

            });
            fbNativeManager.loadAds();
        }
        public void addNativeAd(int i, NativeAd ad) {
//        if (ad == null) {
//            return;
//        }
//        if (this.nativeAd.size() > i && this.nativeAd.get(i) != null) {
//            this.nativeAd.get(i).unregisterView();
//            this.fileList.remove(AD_POSITION + (i * AD_POSITION_EVERY_COUNT));
//            this.nativeAd = null;
//            this.notifyDataSetChanged();
//        }
//        this.nativeAd.add(i, ad);
//
//        if(fileList.size() > (AD_POSITION + (i * AD_POSITION_EVERY_COUNT))) {
//            fileList.add(AD_POSITION + (i * AD_POSITION_EVERY_COUNT), ad);
//            notifyItemInserted(AD_POSITION + (i * AD_POSITION_EVERY_COUNT));
//        }
        }



    }*/
}
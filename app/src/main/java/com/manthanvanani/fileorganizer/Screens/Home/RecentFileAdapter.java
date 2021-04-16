package com.manthanvanani.fileorganizer.Screens.Home;


import android.content.Context;
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
import com.manthanvanani.fileorganizer.R;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecentFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> recentFiles;
    Context context;
    private NativeAdsManager fbNativeManager;
    private ArrayList<NativeAd> nativeAd = new ArrayList<>();

    private final int ITEM_TYPE_DATA = 0;
    private final int ITEM_TYPE_AD = 1;

    private final int AD_POSITION = 5;
    private final int AD_POSITION_EVERY_COUNT = 7;
    public class NativeAdViewHolder extends RecyclerView.ViewHolder {
        LinearLayout nativeAdContainer;
        NativeAdViewHolder(@NonNull View itemView) {
            super(itemView);
            nativeAdContainer = itemView.findViewById(R.id.adContainer);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTxtRecentFileName, mTxtRecentFileSizeDate;
        public ImageView mImgRecentFile;
        public LinearLayout mLLRootRecentFile;

        public MyViewHolder(View view) {
            super(view);
            mImgRecentFile = view.findViewById(R.id.mImgRecentFile);
            mTxtRecentFileName = view.findViewById(R.id.mTxtRecentFileName);
            mTxtRecentFileSizeDate = view.findViewById(R.id.mTxtRecentFileSizeDate);
            mLLRootRecentFile = view.findViewById(R.id.mLLRootRecentFile);

        }
    }


    public RecentFileAdapter(Context context,List<Object> recentFiles) {
        this.context = context;
        this.recentFiles = recentFiles;
        fbNativeManager = new NativeAdsManager(context, Constants.FB_NATIVE,20);

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

            if(recentFiles.size() > (AD_POSITION + (i * AD_POSITION_EVERY_COUNT))) {
                recentFiles.add(AD_POSITION + (i * AD_POSITION_EVERY_COUNT), ad);
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
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recent_file, viewGroup, false);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            return new MyViewHolder(v);
        }
        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_file, parent, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        return new MyViewHolder(itemView);*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) viewHolder;

            File file = (File) recentFiles.get(position);
            holder.mTxtRecentFileName.setText(file.getName());
            holder.mTxtRecentFileSizeDate.setText(Utills.convertBytes(file.length())+"  |  "+Utills.millisecondToDate(file.lastModified()));
            holder.mImgRecentFile.setImageResource(Utills.getImageFromFile(file));
            holder.mLLRootRecentFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("LOG", "");
                    try {
                        context.startActivity(FileUtils.openFile(file));
                    }
                    catch (Exception e){
                        Toast.makeText(context,context.getResources().getText(R.string.no_file_available),Toast.LENGTH_SHORT).show();
                        Log.e("OPEN_ERROR",file.getName()+" --------- "+e.getMessage());
                    }
                }
            });
        }else{
            NativeAdViewHolder holder = (NativeAdViewHolder) viewHolder;
            NativeAd ad = (NativeAd) this.recentFiles.get(position);

            View mNativeView = NativeAdView.render(context, ad, NativeAdView.Type.HEIGHT_300);
            holder.nativeAdContainer.removeAllViews();
            holder.nativeAdContainer.addView(mNativeView);
        }

    }
    @Override
    public int getItemCount() {
        if(recentFiles == null)
            return 0;
        return recentFiles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if( recentFiles.get(position) instanceof NativeAd)
            return ITEM_TYPE_AD;
        else
            return ITEM_TYPE_DATA;
    }
//    @Override
//    public int getItemCount() {
//        return recentFiles.size();
//    }
}
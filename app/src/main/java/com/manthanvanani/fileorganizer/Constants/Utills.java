package com.manthanvanani.fileorganizer.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.manthanvanani.fileorganizer.R;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;

public class Utills {

    public static KProgressHUD showLoader(Context context){
        return KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.3f)
                .show();
    }
    public static void hideLoader(KProgressHUD kProgressHUD){
        kProgressHUD.dismiss();
    }

    public static int getDeviceHeight(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getDeviceWidth(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public static long totalSpace(){
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSizeLong() * (long)stat.getBlockCountLong();
        long megAvailable = bytesAvailable;
        Log.e("Utills","Total MB : "+megAvailable);
        return (megAvailable);
    }

    public static long freeSpace(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }
        else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }
        long megAvailable = bytesAvailable;
        Log.e("Utills","Available MB : "+megAvailable);
        return (megAvailable);
    }

    public static String convertBytes (long size){
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;
        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " KB";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " MB";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " GB";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " TB";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " PB";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " EB";

        return "anything...";
    }

    public static String floatForm (double d){
//        return new DecimalFormat("#.##").format(d);
        return new DecimalFormat("#.#").format(d);
    }

    public static String millisecondToDate(long millisecond){
        return DateFormat.format("dd MMM yyyy KK:mm a", new Date(millisecond)).toString();
    }

    public static String getFileExtension(File file){
        return file.getName().substring(file.getName().lastIndexOf(".")).replace(".","");
    }
    public static String getFileExtensionWithDot(File file){
        return file.getName().substring(file.getName().lastIndexOf("."));
    }

    public static int getImageFromFile(File file){
        String type = getFileExtension(file);
        switch (type){
            case "3ds":  return R.drawable.a3ds;
            case "aac":  return R.drawable.aac;
            case "ai":   return R.drawable.ai;
            case "apk":  return R.drawable.apk;
            case "avi":  return R.drawable.avi;
            case "bmp":  return R.drawable.bmp;
            case "cad":  return R.drawable.cad;
            case "cdr":  return R.drawable.cdr;
            case "css":  return R.drawable.css;
            case "dat":  return R.drawable.dat;
            case "dll":  return R.drawable.dll;
            case "dmg":  return R.drawable.dmg;
            case "doc":  return R.drawable.doc;
            case "docx":  return R.drawable.docx;
            case "eps":  return R.drawable.eps;
            case "fla":  return R.drawable.fla;
            case "flv":  return R.drawable.flv;
            case "gif":  return R.drawable.gif;
            case "heic":  return R.drawable.heic;
            case "html":  return R.drawable.html;
            case "indd": return R.drawable.indd;
            case "iso":  return R.drawable.iso;
            case "jpg":  return R.drawable.jpg;
            case "js":   return R.drawable.js;
            case "mp3":  return R.drawable.mp3;
            case "mkv":  return R.drawable.mkv;
            case "mp4":  return R.drawable.mp4;
            case "midi": return R.drawable.midi;
            case "mov":  return R.drawable.mov;
            case "mpg":  return R.drawable.mpg;
            case "odt":  return R.drawable.odt;
            case "pptx":  return R.drawable.pptx;
            case "pdf":  return R.drawable.pdf;
            case "php":  return R.drawable.php;
            case "png":  return R.drawable.png;
            case "ppt":  return R.drawable.ppt;
            case "ps;":  return R.drawable.ps;
            case "psd":  return R.drawable.psd;
            case "raw":  return R.drawable.raw;
            case "sql":  return R.drawable.sql;
            case "srt":  return R.drawable.srt;
            case "svg":  return R.drawable.svg;
            case "tar":  return R.drawable.tar;
            case "tif":  return R.drawable.tif;
            case "txt":  return R.drawable.txt;
            case "wmv":  return R.drawable.wmv;
            case "webp":  return R.drawable.webp;
            case "xlsx":  return R.drawable.xlsx;
            case "xls":  return R.drawable.xls;
            case "xml":  return R.drawable.xml;
            case "zip":  return R.drawable.zip;
            default: return R.drawable.default1;
        }
    }

    public static long folderSize(File directory) {
        long length = 0;
        try {
            for (File file : directory.listFiles()) {
                if (file.isFile())
                    length += file.length();
                else
                    length += folderSize(file);
            }
            return length;
        }catch (Exception e){
            return length;
        }

    }

    public static void sendEmail(Context context, String email, String subject,
                          String text) {
        final Intent emailIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { email });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    /**
     * Dial.
     *
     * @param context the context
     * @param number the number
     */
    public static void dial(Context context, String number) {
        Uri dialUri = Uri.parse("tel:" + number);
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, dialUri);
        context.startActivity(dialIntent);
    }

    /**
     * Call.
     *
     * @param context the context
     * @param number the number
     */
    public static void call(Context context, String number) {
        Uri callUri = Uri.parse("tel:" + number);
        Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
        context.startActivity(callIntent);
    }
}

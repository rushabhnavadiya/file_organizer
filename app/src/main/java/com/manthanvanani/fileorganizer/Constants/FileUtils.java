package com.manthanvanani.fileorganizer.Constants;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    private static final String LOG = "FileUtils";

    public static String showedFilePath(File file){
        String home = Environment.getExternalStorageDirectory().getPath();
        String path = file.getAbsolutePath().replace(home,"Home");
        return path;
    }

    public static List<File> getAllFiles(String rootPath){
        List<File> allFiles = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString()+rootPath;
        Log.d(LOG, "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        try{
            for (int i = 0; i < files.length; i++) {
                if(files[i].isFile()){
                    if(files[i].getName().startsWith(".")
                            || files[i].getAbsolutePath().replace(Environment.getExternalStorageDirectory().toString(),"").startsWith("/Android/data/")
                            || !files[i].getName().contains(".")
                            || files[i].getParent().contains("/.")
                    ){
                    }else{
                        allFiles.add(files[i]);
                    /*if(Utills.getFileExtension(files[i]).equalsIgnoreCase("zip")){
                        allZipFiles.add(files[i]);
                    }
                    if(Utills.getFileExtension(files[i]).equalsIgnoreCase("apk")){
                        allApkFiles.add(files[i]);
                    }*/
                    }
                }else{

                    List<File> folderFiles = getFilesFromFolder(files[i].getAbsolutePath());
                    for(int j=0;j< folderFiles.size();j++){
                        allFiles.add(folderFiles.get(j));
                    }
                }
            }
            Log.d(LOG, "" + allFiles.size());
            return allFiles;
        }catch (Exception e){
            return allFiles;
        }
    }

    public static List<File> getZipFiles(String rootPath){
        List<File> allFiles = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString()+rootPath;
        File directory = new File(path);
        File[] files = directory.listFiles();
        try{
            for (int i = 0; i < files.length; i++) {
                if(files[i].isFile()){
                    if(files[i].getName().startsWith(".")
                            || files[i].getAbsolutePath().replace(Environment.getExternalStorageDirectory().toString(),"").startsWith("/Android/data/")
                            || !files[i].getName().contains(".")
                            || files[i].getParent().contains("/.")
                    ){
                    }else{
                        if(Utills.getFileExtensionWithDot(files[i]).equalsIgnoreCase(".zip")){
                            allFiles.add(files[i]);
                        }
                    }
                }else{

                    List<File> folderFiles = getFilesFromFolder(files[i].getAbsolutePath());
                    for(int j=0;j< folderFiles.size();j++){
                        if(Utills.getFileExtensionWithDot(folderFiles.get(j)).equalsIgnoreCase(".zip")){
                            allFiles.add(folderFiles.get(j));
                        }
                    }
                }
            }
            return allFiles;
        }catch (Exception e){
            return allFiles;
        }
    }
    public static List<File> getApkFiles(String rootPath){
        List<File> allFiles = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString()+rootPath;
        File directory = new File(path);
        File[] files = directory.listFiles();
        try{
            for (int i = 0; i < files.length; i++) {
                if(files[i].isFile()){
                    if(files[i].getName().startsWith(".")
                            || files[i].getAbsolutePath().replace(Environment.getExternalStorageDirectory().toString(),"").startsWith("/Android/data/")
                            || !files[i].getName().contains(".")
                            || files[i].getParent().contains("/.")
                    ){
                    }else{
                        if(Utills.getFileExtensionWithDot(files[i]).equalsIgnoreCase(".apk")){
                            allFiles.add(files[i]);
                        }
                    }
                }else{
                    List<File> folderFiles = getFilesFromFolder(files[i].getAbsolutePath());
                    for(int j=0;j< folderFiles.size();j++){
                        if(Utills.getFileExtensionWithDot(folderFiles.get(j)).equalsIgnoreCase(".apk")){
                            allFiles.add(folderFiles.get(j));
                        }
                    }
                }
            }
            return allFiles;
        }catch (Exception e){
            return allFiles;
        }
    }


    public static List<File> getFilesFromFolder(String absolutePath){
        List<File> folderFiles = new ArrayList<>();

        File directory = new File(absolutePath);
        File[] files = directory.listFiles();
        try{
            for (int i = 0; i < files.length; i++) {
                if(files[i].isFile()){
                    if(files[i].getName().startsWith(".")
                            || files[i].getAbsolutePath().replace(Environment.getExternalStorageDirectory().toString(),"").startsWith("/Android/data/")
                            || !files[i].getName().contains(".")
                            || files[i].getParent().contains("/.")
                    ){
                    }else{
                        folderFiles.add(files[i]);
                    }
                }else{
                    List<File> subFiles = getFilesFromFolder(files[i].getAbsolutePath());
                    folderFiles.addAll(subFiles);
                }
            }
            return folderFiles;
        }catch(Exception e){
            return folderFiles;
        }

    }

    public static List<File> getFilesWithFolder(String path){
        List<File> folderFiles = new ArrayList<>();

        File directory = new File(path);
        File[] files = directory.listFiles();
        try {
            for (int i = 0; i < files.length; i++) {
                if(!files[i].getName().startsWith(".")
                        && !(files[i].isFile() && !files[i].getName().contains("."))
                ){
                    folderFiles.add(files[i]);
                }
            }
            Collections.sort(folderFiles, (abc1, abc2) ->
                    Boolean.compare(abc2.isDirectory(), abc1.isDirectory()));
            return folderFiles;
        }catch (Exception e){
            return folderFiles;
        }
    }

    public static Intent openFile(File url){
        // Create URI
        File file= url;
        Uri uri = Uri.fromFile(file);
        String pathExtension = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
        Intent intent = new Intent(Intent.ACTION_VIEW);

        try{
// Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (pathExtension.equalsIgnoreCase(".doc") || pathExtension.equalsIgnoreCase(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if(pathExtension.equalsIgnoreCase(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if(pathExtension.equalsIgnoreCase(".ppt") || pathExtension.equalsIgnoreCase(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if(pathExtension.equalsIgnoreCase(".xls") || pathExtension.equalsIgnoreCase(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if(pathExtension.equalsIgnoreCase(".zip") || pathExtension.equalsIgnoreCase(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if(pathExtension.equalsIgnoreCase(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if(pathExtension.equalsIgnoreCase(".wav") || pathExtension.equalsIgnoreCase(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if(pathExtension.equalsIgnoreCase(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if(pathExtension.equalsIgnoreCase(".jpg") || pathExtension.equalsIgnoreCase(".jpeg") || pathExtension.equalsIgnoreCase(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if(url.toString().equalsIgnoreCase(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if(pathExtension.equalsIgnoreCase(".3gp") || pathExtension.equalsIgnoreCase(".mpg") || pathExtension.equalsIgnoreCase(".mpeg") || pathExtension.equalsIgnoreCase(".mpe") || pathExtension.equalsIgnoreCase(".mp4") || pathExtension.equalsIgnoreCase(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }
        }catch (Exception e){
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
//        activity.startActivity(intent);
    }

}

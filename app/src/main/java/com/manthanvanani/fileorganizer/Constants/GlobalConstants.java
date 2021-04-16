package com.manthanvanani.fileorganizer.Constants;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.manthanvanani.fileorganizer.Constants.FileUtils.getFilesFromFolder;

public class GlobalConstants extends Application {
    public static List<File> allZipFiles = new ArrayList<>();
    public static List<File> allApkFiles = new ArrayList<>();
    public static List<File> allFiles = new ArrayList<>();

    public static List<File> getAllFiles(String rootPath){
        allZipFiles.clear();
        allApkFiles.clear();
        allFiles.clear();
        String path = Environment.getExternalStorageDirectory().toString()+rootPath;
        File directory = new File(path);
        File[] files = directory.listFiles();

        try {
            for (int i = 0; i < files.length; i++) {
                if(files[i].isFile()){
                    if(files[i].getName().startsWith(".")
                            || files[i].getAbsolutePath().replace(Environment.getExternalStorageDirectory().toString(),"").startsWith("/Android/data/")
                            || !files[i].getName().contains(".")
                            || files[i].getParent().contains("/.")
                    ){
                    }else{
                        allFiles.add(files[i]);
                        if(Utills.getFileExtensionWithDot(files[i]).equalsIgnoreCase(".zip")){
                            allZipFiles.add(files[i]);
                        }
                        if(Utills.getFileExtensionWithDot(files[i]).equalsIgnoreCase(".apk")){
                            allApkFiles.add(files[i]);
                        }
                    }
                }else{

                    List<File> folderFiles = getFilesFromFolder(files[i].getAbsolutePath());
                    for(int j=0;j< folderFiles.size();j++){
                        allFiles.add(folderFiles.get(j));
                        if(Utills.getFileExtensionWithDot(folderFiles.get(j)).equalsIgnoreCase(".zip")){
                            allZipFiles.add(folderFiles.get(j));
                        }
                        if(Utills.getFileExtensionWithDot(folderFiles.get(j)).equalsIgnoreCase(".apk")){
                            allApkFiles.add(folderFiles.get(j));
                        }
                    }
                }
            }
            return getAllFiles();
        }catch (Exception e){
            return getAllFiles();
        }
    }

    public static List<File> getAllZipFiles() {
        return allZipFiles;
    }

    public static List<File> getAllApkFiles() {
        return allApkFiles;
    }

    public static List<File> getAllFiles() {
        return allFiles;
    }
}

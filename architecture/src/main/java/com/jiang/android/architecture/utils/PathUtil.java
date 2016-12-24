package com.jiang.android.architecture.utils;

/**
 * Created by guxin on 15/7/6.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;


public class PathUtil {
    private static String pathPrefix = "config/";
    private static final String imagePathName = "/image/";
    private static final String voicePathName = "/voice/";
    private static final String cachePathName = "/cache/";
    private static final String filePathName = "/file/";

    private static File storageDir = null;
    private static PathUtil instance = null;
    private File voicePath = null;
    private File imagePath = null;
    private File cachePath = null;
    private File filePath = null;

    private PathUtil(String name) {
        if (!TextUtils.isEmpty(name)) {
            pathPrefix = name + "/";
        }

    }

    public static PathUtil getInstance() {
        return getInstance(null);
    }

    public static PathUtil getInstance(String pathName) {
        if (instance == null) {
            instance = new PathUtil(pathName);
        }
        return instance;
    }


    public void initDirs(Context var3) {
        this.voicePath = generateVoicePath(var3);
        if (!this.voicePath.exists()) {
            this.voicePath.mkdirs();
        }
        this.imagePath = generateImagePath(var3);
        if (!this.imagePath.exists()) {
            this.imagePath.mkdirs();
        }
        this.cachePath = generateCachePath(var3);
        if (!this.cachePath.exists()) {
            this.cachePath.mkdirs();
        }
        this.filePath = generateFilePath(var3);
        if (!this.filePath.exists()) {
            this.filePath.mkdirs();
        }
    }

    public File getImagePath() {
        return this.imagePath;
    }

    public File getVoicePath() {
        return voicePath;
    }

    public File getFilePath() {
        return filePath;
    }

    private static File getStorageDir(Context var0) {
        if (storageDir == null) {
            File var1 = Environment.getExternalStorageDirectory();
            if (var1.exists()) {
                return var1;
            }

            storageDir = var0.getFilesDir();
        }

        return storageDir;
    }

    private static File generateImagePath(Context var2) {
        String var3 = pathPrefix + "/images/";
        return new File(getStorageDir(var2), var3);
    }

    private static File generateVoicePath(Context var2) {
        String var3 = pathPrefix + "/voice/";
        return new File(getStorageDir(var2), var3);
    }

    private static File generateCachePath(Context var2) {
        String var3 = pathPrefix + "/cache/";
        return new File(getStorageDir(var2), var3);
    }

    private static File generateFilePath(Context var2) {
        String var3 = pathPrefix + "/file/";
        return new File(getStorageDir(var2), var3);
    }
}


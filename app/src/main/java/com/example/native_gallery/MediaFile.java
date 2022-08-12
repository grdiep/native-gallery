package com.example.native_gallery;

import java.io.Serializable;
import java.net.URLConnection;
import java.util.ArrayList;

public class MediaFile implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String mPath;
    private ArrayList<String> mApiArray;

    MediaFile(String path) {
        mPath = path;
    }

    MediaFile(String path, ArrayList<String> apiArray) {
        mPath = path;
        mApiArray = apiArray; }

    public String getmPath() {
        return mPath;
    }

    public ArrayList<String> getmApiArray() { return mApiArray; }

    public boolean isImageFile() {
        return fileType(mPath, "image");
    }

    public boolean isVideoFile() {
        return fileType(mPath, "video");
    }

    private boolean fileType(String path, String type) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith(type);
    }
}

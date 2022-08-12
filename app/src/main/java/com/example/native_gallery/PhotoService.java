package com.example.native_gallery;

import static com.example.native_gallery.Constants.PATH_INFORMATION;
import static com.example.native_gallery.Constants.SCANNING_COMPLETE;
import static com.example.native_gallery.Constants.SCANNING_IS_COMPLETED;
import static com.example.native_gallery.Constants.SCANNING_STATUS;
import static com.example.native_gallery.Constants.SEND_INTENT;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class PhotoService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        retrievePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA});

        retrievePath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media.DATA});

        endScan();
        return START_STICKY;
    }

    private void retrievePath(Uri uri, String[] projection) {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri,
                    projection,
                    null,
                    null,
                    null);
            if (cursor == null || !cursor.moveToFirst()) {
                return;
            } else {
                do {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                    MediaFile mediaFile = new MediaFile(path);
                    sendMessage(mediaFile);
                }
                while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void endScan() {
        Intent intent = new Intent();
        intent.setAction(SCANNING_COMPLETE);
        intent.putExtra(SCANNING_STATUS, SCANNING_IS_COMPLETED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        stopSelf();
    }

    private void sendMessage(MediaFile path) {
        Intent intent = new Intent(SEND_INTENT);
        intent.putExtra(PATH_INFORMATION, path);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

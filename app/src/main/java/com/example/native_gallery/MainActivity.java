package com.example.native_gallery;

import static com.example.native_gallery.Constants.IMAGE_LOCATION;
import static com.example.native_gallery.Constants.NUM_OF_COLUMNS;
import static com.example.native_gallery.Constants.PATH_INFORMATION;
import static com.example.native_gallery.Constants.SCANNING_COMPLETE;
import static com.example.native_gallery.Constants.SEND_INTENT;
import static com.example.native_gallery.Constants.SEND_INTENT2;
import static com.example.native_gallery.Constants.STORAGE_PERMISSION_CODE;
import static com.example.native_gallery.Constants.VIDEO_LOCATION;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnClickThumbListener {

    private ArrayList<MediaFile> mListOfPaths;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Boolean flag = false;
    private MediaFile mediaFile;

    public void openDialog(MainActivity view) {
        CustomDialog custom_dialog = new CustomDialog();
        custom_dialog.show(getSupportFragmentManager(), "Test");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListOfPaths = new ArrayList<>();
        mLayoutManager = new GridLayoutManager(this, NUM_OF_COLUMNS);

        mRecyclerView = findViewById(R.id.gallery_recyclerView);
        mRecyclerView.setLayoutManager(mLayoutManager);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SEND_INTENT);
        intentFilter.addAction(SCANNING_COMPLETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
        //intent filters listens to specific action from the intent

//        new myTask().execute();

        openDialog(this);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SEND_INTENT)) {
                MediaFile message = (MediaFile) intent.getSerializableExtra(PATH_INFORMATION);
                mListOfPaths.add(message);
            } else if (intent.getAction().equals(SCANNING_COMPLETE)) {
                mAdapter = new RecyclerAdapter(mListOfPaths, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    };

    //allows us to do background operations and publish results on UI thread
    //AsyncTask<Params, Progress, Result>
    //Params: input sent to the task upon execution
    //Progress: progress units published during the background computation
    //Result: the type of result from the operation done in the background process
    //for any used types, mark as void

    private class myTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                sendGET();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void sendGET() throws IOException {
            URL obj = new URL(Constants.URL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Locale:", "en-US");
            con.setRequestProperty("CarrierId", "aff");
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("main_activity", response.toString());

                //TODO: figure out what type of model class object is needed to pass into a dialog as a param
                //convert into a model class before instantiating feedback dialog
                Gson gson = new Gson();
                mediaFile = gson.fromJson(String.valueOf(response), MediaFile.class);

                Intent newIntent = new Intent(SEND_INTENT2);
                newIntent.putExtra("apiResponseArray", mediaFile);
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(newIntent);
            } else {
                Log.d("main_Activity", "GET request not worked");
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    //check if user has already authorized permission
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            if (!flag) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.permission_already_granted),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, PhotoService.class);
                startService(intent);
                flag = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.storage_permission_granted),
                        Toast.LENGTH_SHORT).show();
                if (!flag) {
                    Intent intent = new Intent(this, PhotoService.class);
                    startService(intent);
                    flag = true;
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.storage_permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void OnClickImage(MediaFile filePath) {
        Intent fullScreenIntent = new Intent(this, FullScreenImageActivity.class);
        fullScreenIntent.putExtra(IMAGE_LOCATION, filePath.getmPath());
        startActivity(fullScreenIntent);
    }

    @Override
    public void OnClickVideo(MediaFile filePath) {
        Intent fullScreenIntent = new Intent(this, FullScreenVideoActivity.class);
        fullScreenIntent.putExtra(VIDEO_LOCATION, filePath.getmPath());
        startActivity(fullScreenIntent);
    }
}

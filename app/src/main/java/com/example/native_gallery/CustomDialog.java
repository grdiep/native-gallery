package com.example.native_gallery;

import static com.example.native_gallery.Constants.PATH_INFORMATION;
import static com.example.native_gallery.Constants.SCANNING_COMPLETE;
import static com.example.native_gallery.Constants.SEND_INTENT;
import static com.example.native_gallery.Constants.SEND_INTENT2;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class CustomDialog extends DialogFragment {

    //empty constructor
    public CustomDialog() {}

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(view);

        TextView textView = (TextView) view.findViewById(R.id.textBox1);

//        textView.setText(); //how do you access elements in the array

        builder.setView(view);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SEND_INTENT2);

        return builder.create();
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SEND_INTENT2)) {
                MediaFile message = (MediaFile) intent.getSerializableExtra("apiResponseArray");
            }
        }
    };
}

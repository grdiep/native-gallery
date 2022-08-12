package com.example.native_gallery;

import static com.example.native_gallery.Constants.NUM_OF_COLUMNS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolderOne> {

    private Context mContext;
    private int mImageSize;
    private OnClickThumbListener mOnClickThumbListener;
    private ArrayList<MediaFile> mListOfPaths;

    public interface OnClickThumbListener {
        void OnClickImage(MediaFile mediaFilePath);

        void OnClickVideo(MediaFile mediaFilePath);
    }

    public RecyclerAdapter(ArrayList<MediaFile> listOfPaths, Context context) {
        mContext = context;
        mImageSize = context.getResources().getDisplayMetrics().widthPixels / NUM_OF_COLUMNS;
        mOnClickThumbListener = (OnClickThumbListener) context;
        mListOfPaths = listOfPaths;
    }

    @NonNull
    @Override
    public ViewHolderOne onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_image,
                parent, false);

        return new ViewHolderOne(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderOne holder, int position) {
        holder.itemView.getLayoutParams().height = mImageSize;
        MediaFile mediaFile = mListOfPaths.get(position);

        Glide.with(mContext)
                .load(mediaFile.getmPath())
                .into(holder.imageView);

        if (mediaFile.isVideoFile()) {
            holder.playImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mListOfPaths.size();
    }

    //stores and recycles views as they are scrolled off screen
    public class ViewHolderOne extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ImageView playImageView;

        public ViewHolderOne(View view) {
            super(view);
            imageView = view.findViewById(R.id.image);
            playImageView = view.findViewById(R.id.videoPlayIcon);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            getOnClick(getAdapterPosition());
        }
    }

    private void getOnClick(int position) {
        MediaFile mediaFilePath = mListOfPaths.get(position);
        if (mediaFilePath.isImageFile()) {
            mOnClickThumbListener.OnClickImage(mediaFilePath);
        } else {
            mOnClickThumbListener.OnClickVideo(mediaFilePath);
        }
    }
}

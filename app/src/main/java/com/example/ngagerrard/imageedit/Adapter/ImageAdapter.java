package com.example.ngagerrard.imageedit.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ngagerrard.imageedit.R;

import java.util.ArrayList;

/**
 * Created by Nga Gerrard on 26/04/2017.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    public static final String BROADCAST = "com.example.ngagerrard.imageedit.ABCDE";
    public static final String POSITION = "position";

    private ArrayList<String> mDataColor;
    private ArrayList<Bitmap> mImage;
    Context context;
    public ImageAdapter(ArrayList<String> mDataColor, ArrayList<Bitmap> mImage, Context context) {
        this.mDataColor = mDataColor;
        this.mImage = mImage;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_color, parent, false);
        ViewHolder vh = new ViewHolder(v, context, mDataColor,mImage);
        return vh;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtEditColor.setText(mDataColor.get(position));
        //Bitmap mImge = BipmapEfficiently.getResizedBitmap(mImage.get(position), holder.imgEditColor.getWidth(), holder.imgEditColor.getHeight());
        holder.imgEditColor.setImageBitmap(mImage.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataColor.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView txtEditColor;
        private ImageView imgEditColor;
        ArrayList<String> mDatas = new ArrayList<String>();
        ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();
        Context context;
        public ViewHolder(View itemView, Context context, ArrayList<String> mData, ArrayList<Bitmap> mImage) {
            super(itemView);
            this.context = context;
            this.mDatas = mData;
            this.mImages = mImage;
            itemView.setOnClickListener(this);

            txtEditColor = (TextView)itemView.findViewById(R.id.txtEditColor);
            imgEditColor = (ImageView)itemView.findViewById(R.id.imageViewEditColor);
            imgEditColor.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String mData = this.mDatas.get(position);
            Bitmap mImage = this.mImages.get(position);

            int id = v.getId();
            switch (id){
                case R.id.imageViewEditColor:
                    Intent intent = new Intent();
                    intent.setAction(BROADCAST);
                    intent.putExtra(POSITION, position);
                    context.sendBroadcast(intent);
                break;
            }

        }
    }
}

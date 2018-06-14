package com.example.ngagerrard.imageedit.Activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.ngagerrard.imageedit.Adapter.ImageAdapter;
import com.example.ngagerrard.imageedit.ImageEdit.AddFrameImage;
import com.example.ngagerrard.imageedit.ImageEdit.ImageProcess;
import com.example.ngagerrard.imageedit.ImageEdit.BipmapEfficiently;
import com.example.ngagerrard.imageedit.ImageEdit.SaveImage;
import com.example.ngagerrard.imageedit.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView imgView;
    File file;
    Uri uri;
    FrameLayout frameImageEdit;

    //Color
    RecyclerView recylcerEditColor;
    RecyclerView.LayoutManager layoutRecyclerViewColor;
    RecyclerView.Adapter adaterColor;

    //Frame
    RecyclerView recylcerFrame;
    RecyclerView.LayoutManager layoutRecyclerViewFrame;
    RecyclerView.Adapter adapterFrame;

    //Other Edit
    RecyclerView recylerOtherEdit;
    RecyclerView.LayoutManager layoutRecyclerOtherEdit;
    RecyclerView.Adapter adapterOtherEdit;

    BroadcastReceiver broadcastReceiver;
    SeekBar seekBar;
    LinearLayout linear;
    Button btnCrop1, btnCrop2, btnCrop3, btnCrop4;


    ArrayList<String> mDataColor;

    ArrayList<Bitmap> mImageColor;
    ArrayList<String> mDataFrame;
    ArrayList<Bitmap> mImageFrame;
    ArrayList<String> mDataOther;
    ArrayList<Bitmap> mImageOther;
    ArrayList<Bitmap> mRecyclerImageColor;
    ArrayList<Bitmap> mRecyclerImageFrame;
    ArrayList<Bitmap> mRecyclerImageOther;

    BottomNavigationView bottomview;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_GALLERY = 2;
    final int RequestPermissionCode = 1;
    Bitmap bitmap;

    public static int tabMode = 0;
    DisplayMetrics displayMetrics;
    int wight, height;
    Intent cropItent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        addWidgets();
        int permisstionCheck = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
        if(permisstionCheck == PackageManager.PERMISSION_DENIED){
            RequestRuntimePermission();
        }
        addEvent();
    }

    private void addEvent() {
        bottomview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btn_addframe:
                        AddFrameRecycler();
                        tabMode = 1;
                        break;
                    case R.id.btn_colorchange:
                        changeColorRecylcer();
                        tabMode = 2;
                        break;
                    case R.id.btn_addfun:
                        changeImageOther();
                        tabMode = 3;
                        break;
                    case R.id.btn_crop:
                        Crop();
                        break;
                }
                return true;
            }
        });

    }

    private void Crop() {
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();
        if(drawable == null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Chú ý!");
            builder.setMessage("Bạn chưa lựa chọn ảnh");
            builder.show();
        }else {

            final Bitmap src = drawable.getBitmap();

            //seekBar.setVisibility(View.VISIBLE);
            linear.setVisibility(View.VISIBLE);
            recylcerEditColor.setVisibility(View.INVISIBLE);
            recylerOtherEdit.setVisibility(View.INVISIBLE);
            recylcerFrame.setVisibility(View.INVISIBLE);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressValue;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressValue = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    //Rotate image
                    Bitmap bmRotated = ImageProcess.rotate(src, (float) progressValue / seekBar.getMax() * 360);
                    imgView.setImageBitmap(bmRotated);

                }
            });
            //cropImage();
            btnCrop1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cropImage(1, 1);
                }
            });
            btnCrop2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cropImage(2, 3);
                }
            });
            btnCrop3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cropImage(4, 3);
                }
            });
            btnCrop4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cropImage(3, 4);
                }
            });
        }
    }


    //Crop
    private void cropImage(int x, int y) {
        try {

            displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            wight   =  displayMetrics.widthPixels;
            height    =  displayMetrics.heightPixels;
            cropItent = new Intent("com.android.camera.action.CROP");
            BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();

            if(drawable == null){
                //Toast.makeText(this, "A", Toast.LENGTH_LONG).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Chú ý!");
                builder.setMessage("Bạn chưa lựa chọn ảnh");
                builder.show();
            }else {
                Bitmap bmsrc = drawable.getBitmap();
                uri = getImageUri(this, bmsrc);
            }

            cropItent.setDataAndType(uri, "image/*");
//            BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();
//            Bitmap bmsrcs = drawable.getBitmap();
            //cropItent.putExtra("image", BipmapEfficiently.getResizedBitmap(bmsrcs, imgView.getWidth(), imgView.getHeight()));
            cropItent.putExtra("crop", "true");
            cropItent.putExtra("outputX", wight);
            cropItent.putExtra("outputY", height);
            cropItent.putExtra("aspectX", x);
            cropItent.putExtra("aspectY", y);
//            cropItent.putExtra("aspectX", wight);
//            cropItent.putExtra("aspectY", height);
            cropItent.putExtra("scaleUpIfNeeded", true);
            cropItent.putExtra("return-data", true);
            startActivityForResult(cropItent, 1);
        }catch (ActivityNotFoundException ex){

        }
    }


    private void changeImageOther() {
        mDataOther = new ArrayList<String>();
        mImageOther = new ArrayList<Bitmap>();
        mRecyclerImageOther = new ArrayList<Bitmap>();
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();

        if(drawable == null){
            //Toast.makeText(this, "A", Toast.LENGTH_LONG).show();
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Chú ý!");
            builder.setMessage("Bạn chưa lựa chọn ảnh");
            builder.show();
        }else {
            Bitmap bmsrc = drawable.getBitmap();
            Bitmap src = BipmapEfficiently.getResizedBitmap(drawable.getBitmap(), 80, 80);
            //mean removal effect
            mImageOther.add(src);
            mDataOther.add("None");
            Bitmap bmMeanRemoval = BipmapEfficiently.getResizedBitmap(ImageProcess.applyMeanRemoval(src), 80, 80);
            mImageOther.add(bmMeanRemoval);

            mDataOther.add("Mean Removal");
            //invert, xau lam
            //Bitmap imvertImage = ImageProcess.doInvert(bmRoot);

            //Gaussian Blur
            Bitmap bmGaussianBlur = BipmapEfficiently.getResizedBitmap(ImageProcess.applyGaussianBlur(src), 80, 80);
            mImageOther.add(bmGaussianBlur);

            mDataOther.add("Blur");

            //Sharpend Image
            Bitmap bmSharpend = BipmapEfficiently.getResizedBitmap(ImageProcess.sharpen(src, 10), 80, 80);
            mImageOther.add(bmSharpend);

            mDataOther.add("Sharpen");

            //Reflection
            Bitmap bmReflection = BipmapEfficiently.getResizedBitmap(ImageProcess.applyReflection(src), 80, 80);
            mImageOther.add(bmReflection);

            mDataOther.add("Reflection");

            //Hue Fiter
            Bitmap bmHueFilter = BipmapEfficiently.getResizedBitmap(ImageProcess.applyHueFilter(src, 5), 80, 80);
            mImageOther.add(bmHueFilter);
            mDataOther.add("Hue Filter");

            //Saturation filter
            Bitmap bmSaturation = BipmapEfficiently.getResizedBitmap(ImageProcess.applySaturationFilter(src,1), 80, 80);
            mImageOther.add(bmSaturation);
            mDataOther.add("Saturation Filter");

            //engrave effect
            Bitmap bmEngrave = BipmapEfficiently.getResizedBitmap(ImageProcess.engrave(src), 80, 80);
            mImageOther.add(bmEngrave);
            mDataOther.add("Engrave");


            //Emboss Effect
            Bitmap bmEmboss = BipmapEfficiently.getResizedBitmap(ImageProcess.emboss(src), 80, 80);
            mImageOther.add(bmEmboss);
            mDataOther.add("Emboss");


            recylerOtherEdit.setHasFixedSize(true);
            layoutRecyclerOtherEdit = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recylerOtherEdit.setLayoutManager(layoutRecyclerOtherEdit);
            adapterOtherEdit = new ImageAdapter(mDataOther, mImageOther, this);
            recylerOtherEdit.setAdapter(adapterOtherEdit);
            recylerOtherEdit.setVisibility(View.VISIBLE);
            recylcerFrame.setVisibility(View.INVISIBLE);
            recylcerEditColor.setVisibility(View.INVISIBLE);
            //seekBar.setVisibility(View.INVISIBLE);
            linear.setVisibility(View.INVISIBLE);
            mRecyclerImageOther.add(bmsrc);
//            mRecyclerImageOther.add(ImageProcess.applyMeanRemoval(bmsrc));
//            mRecyclerImageOther.add(ImageProcess.applyGaussianBlur(bmsrc));
//            mRecyclerImageOther.add(ImageProcess.sharpen(bmsrc, 10));
//            mRecyclerImageOther.add(ImageProcess.applyReflection(bmsrc));
//            mRecyclerImageOther.add(ImageProcess.applyHueFilter(bmsrc, 10));
//            mRecyclerImageOther.add(ImageProcess.applySaturationFilter(bmsrc, 1));
//            mRecyclerImageOther.add(ImageProcess.applyShadingFilter(bmsrc, Color.CYAN));
        }
    }

    private void changeColorRecylcer() {
        //recyclerview change color
        mDataColor = new ArrayList<String>();

        // Bitmap bmImage = ImageProcess.createSepiaToningEffect(bitmap, 150, 0.7, 0.3, 0.11);
        mImageColor = new ArrayList<Bitmap>();
        mRecyclerImageColor = new ArrayList<Bitmap>();

        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();

        if(drawable == null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Chú ý!");
            builder.setMessage("Bạn chưa lựa chọn ảnh");
            builder.show();
        }else {
            Bitmap bmsrc = BipmapEfficiently.getResizedBitmap(drawable.getBitmap(), imgView.getWidth(), imgView.getHeight());
            Bitmap src = BipmapEfficiently.getResizedBitmap(drawable.getBitmap(), 80, 80);
            //Bitmap bmRoot = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
            mImageColor.add(src);

            mDataColor.add("None");
            //Hieu ung sepia

            Bitmap bmImageSepia = BipmapEfficiently.getResizedBitmap(ImageProcess.createSepiaToningEffect(src, 150, 0.7, 0.3, 0.11), 80, 80);
            mImageColor.add(bmImageSepia);
            mDataColor.add("Sepia");

            //grayscale: 100,1,1,1 //
            Bitmap bmImageGray = BipmapEfficiently.getResizedBitmap(ImageProcess.createSepiaToningEffect(src, 50, 1, 1, 1), 80, 80);
            mImageColor.add(bmImageGray);
            mDataColor.add("Gray");


            Bitmap bmImageGreen = BipmapEfficiently.getResizedBitmap(ImageProcess.createSepiaToningEffect(src, 150, 0.2, 0.55, 0.3), 80, 80);
            mImageColor.add(bmImageGreen);
            mDataColor.add("Green");


            Bitmap bmImageBlue = BipmapEfficiently.getResizedBitmap(ImageProcess.createSepiaToningEffect(src, 150, 0.11, 0.5, 0.8), 80, 80);
            mImageColor.add(bmImageBlue);
            mDataColor.add("Blue");


            //Tint color
            Bitmap bmTint = BipmapEfficiently.getResizedBitmap(ImageProcess.tintImage(src, 50), 80, 80);
            mImageColor.add(bmTint);
            mDataColor.add("Tint Color");


            //do gamma image
            Bitmap bmGrammaDecrease = BipmapEfficiently.getResizedBitmap(ImageProcess.doGramma(src, 0.6, 0.6, 0.6), 80, 80);
            mImageColor.add(bmGrammaDecrease);
            mDataColor.add("Dark");


            Bitmap bmGammaIncrease = BipmapEfficiently.getResizedBitmap(ImageProcess.doGramma(src, 1.8, 1.8, 1.8), 80, 80);
            mImageColor.add(bmGammaIncrease);
            mDataColor.add("Light");



            //Snow Effect
            Bitmap bmSnow = BipmapEfficiently.getResizedBitmap(ImageProcess.applySnowEffect(src), 80, 80);
            mImageColor.add(bmSnow);
            mDataColor.add("Snow");


            //Black Effect
            Bitmap bmBlack = BipmapEfficiently.getResizedBitmap(ImageProcess.applyBlackFilter(src), 80, 80);
            mImageColor.add(bmBlack);
            mDataColor.add("Black");

            //Shading
            Bitmap bmShading = BipmapEfficiently.getResizedBitmap(ImageProcess.applyShadingFilter(src, Color.CYAN), 80, 80);
            mImageColor.add(bmShading);
            mDataColor.add("Shading");


            //imgView.setImageBitmap(bmEmboss);
            //recycler view color

//            for (int i = 0; i < mImageColor.size(); i++){
//                mRecyclerImageColor.add(BipmapEfficiently.getResizedBitmap(mImageColor.get(i), 80, 80));
//            }
            recylcerEditColor.setHasFixedSize(true);
            layoutRecyclerViewColor = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recylcerEditColor.setLayoutManager(layoutRecyclerViewColor);
            adaterColor = new ImageAdapter(mDataColor, mImageColor, this);
            recylcerEditColor.setAdapter(adaterColor);
            recylcerEditColor.setVisibility(View.VISIBLE);
            recylcerFrame.setVisibility(View.INVISIBLE);
            recylerOtherEdit.setVisibility(View.INVISIBLE);
            //seekBar.setVisibility(View.INVISIBLE);
            linear.setVisibility(View.INVISIBLE);
            mRecyclerImageColor.add(bmsrc);
//            mRecyclerImageColor.add(ImageProcess.createSepiaToningEffect(bmsrc, 150, 0.7, 0.3, 0.11));
//            mRecyclerImageColor.add(ImageProcess.createSepiaToningEffect(bmsrc, 50, 1, 1, 1));
//            mRecyclerImageColor.add(ImageProcess.createSepiaToningEffect(bmsrc, 150, 0.2, 0.55, 0.3));
//            mRecyclerImageColor.add(ImageProcess.createSepiaToningEffect(bmsrc, 150, 0.11, 0.5, 0.8));
//            mRecyclerImageColor.add(ImageProcess.tintImage(bmsrc, 50));
//            mRecyclerImageColor.add(ImageProcess.doGramma(bmsrc, 0.6, 0.6, 0.6));
//            mRecyclerImageColor.add(ImageProcess.doGramma(bmsrc, 1.8, 1.8, 1.8));
//            mRecyclerImageColor.add(ImageProcess.engrave(bmsrc));
//            mRecyclerImageColor.add(ImageProcess.emboss(bmsrc));
//            mRecyclerImageColor.add(ImageProcess.applySnowEffect(bmsrc));
//            mRecyclerImageColor.add(ImageProcess.applyBlackFilter(bmsrc));
        }
    }

    private void AddFrameRecycler() {

        //recycler view frame
        mDataFrame = new ArrayList<String>();
        mImageFrame = new ArrayList<Bitmap>();
        mRecyclerImageFrame = new ArrayList<Bitmap>();
       // Bitmap bmRoot = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();

        if(drawable == null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Chú ý!");
            builder.setMessage("Bạn chưa lựa chọn ảnh");
            builder.show();
        }else {
            Bitmap bmsrc = BipmapEfficiently.getResizedBitmap(drawable.getBitmap(), imgView.getWidth(), imgView.getHeight());
            Bitmap src = BipmapEfficiently.getResizedBitmap(drawable.getBitmap(), 80, 80);
            mDataFrame.add("None");
            //mImageFrame.add(src);
            mImageFrame.add(bmsrc);

            Bitmap bmRoundCorner = AddFrameImage.roundCorner(src, 180);
            mDataFrame.add("Round Medium");
           // mImageFrame.add(BipmapEfficiently.getResizedBitmap(bmRoundCorner, 80, 80));


            Bitmap bmRoundConerSmall = AddFrameImage.roundCorner(src, 60);
            mDataFrame.add("Round Small");
           // mImageFrame.add(BipmapEfficiently.getResizedBitmap(bmRoundConerSmall, 80, 80));


            Bitmap bmRoundConerBig = AddFrameImage.roundCorner(src, 360);
            mDataFrame.add("Circle");
            //mImageFrame.add(BipmapEfficiently.getResizedBitmap(bmRoundConerBig, 80, 80));

            Bitmap bmRoundWhite = AddFrameImage.roundCornerWhite(src, 0.08f);
            mDataFrame.add("Round White");
           // mImageFrame.add(BipmapEfficiently.getResizedBitmap(bmRoundWhite, 80, 80));

            //Frame rect
            Bitmap bmWhiteFrameRect = AddFrameImage.frameWhite(src, 0.05f, 0);
            mDataFrame.add("Rectagle White");
           // mImageFrame.add(BipmapEfficiently.getResizedBitmap(bmWhiteFrameRect, 80, 80));

            //Frame circle
            Bitmap bmWhiteFrameCircle = AddFrameImage.frameWhite(src, 0.05f, 1);
            mDataFrame.add("Cirlce White");
            //mImageFrame.add(BipmapEfficiently.getResizedBitmap(bmWhiteFrameCircle, 80, 80));

            //imgView.setImageBitmap(bmRoundWhite);

//            for(int i = 0; i < mImageFrame.size(); i++){
//                mRecyclerImageFrame.add(BipmapEfficiently.getResizedBitmap(mImageFrame.get(i), 80, 80));
//            }
            mRecyclerImageFrame.add(bmsrc);
            mRecyclerImageFrame.add(AddFrameImage.roundCorner(bmsrc, 180));
            mRecyclerImageFrame.add(AddFrameImage.roundCorner(bmsrc, 60));
            mRecyclerImageFrame.add(AddFrameImage.roundCorner(bmsrc, 360));
            mRecyclerImageFrame.add(AddFrameImage.roundCornerWhite(bmsrc, 0.08f));
            mRecyclerImageFrame.add(AddFrameImage.frameWhite(bmsrc, 0.05f, 0));
            mRecyclerImageFrame.add(AddFrameImage.frameWhite(bmsrc, 0.05f, 1));
            recylcerFrame.setHasFixedSize(true);
            layoutRecyclerViewFrame = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recylcerFrame.setLayoutManager(layoutRecyclerViewFrame);
            adapterFrame = new ImageAdapter(mDataFrame, mRecyclerImageFrame, this);
            recylcerFrame.setAdapter(adapterFrame);

            recylcerFrame.setVisibility(View.VISIBLE);
            recylcerEditColor.setVisibility(View.INVISIBLE);
            recylerOtherEdit.setVisibility(View.INVISIBLE);
//            seekBar.setVisibility(View.INVISIBLE);
            linear.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
//                BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();
//                Bitmap bmsrcs = drawable.getBitmap();
                int position = intent.getIntExtra(ImageAdapter.POSITION, 0);
                if(tabMode == 2){
                    Bitmap bmapColor = mRecyclerImageColor.get(0);

                    switch (mDataColor.get(position)){
                        case "None":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(bmapColor, imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Sepia":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.createSepiaToningEffect(bmapColor, 150, 0.7, 0.3, 0.11), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Gray":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.createSepiaToningEffect(bmapColor, 50, 1, 1, 1), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Green":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.createSepiaToningEffect(bmapColor, 150, 0.2, 0.55, 0.3), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Blue":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.createSepiaToningEffect(bmapColor, 150, 0.11, 0.5, 0.8), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Tint Color":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.tintImage(bmapColor, 50), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Dark":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.doGramma(bmapColor, 0.6, 0.6, 0.6), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Light":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.doGramma(bmapColor, 1.8, 1.8, 1.8), imgView.getWidth(), imgView.getHeight()));
                            break;

                        case "Snow":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.applySnowEffect(bmapColor), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Black":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.applyBlackFilter(bmapColor), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Shading":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.applyShadingFilter(bmapColor, Color.CYAN), imgView.getWidth(), imgView.getHeight()));
                            break;
                    }
                   // imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(mRecyclerImageColor.get(position), imgView.getWidth(), imgView.getHeight()));
                }else if(tabMode == 1){
                    Bitmap bmapFrame = mImageFrame.get(0);
                    switch (mDataFrame.get(position)){
                        case "None":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(bmapFrame, imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Round Medium":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(AddFrameImage.roundCorner(bmapFrame, 180), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Round Small":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(AddFrameImage.roundCorner(bmapFrame, 60), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Circle":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(AddFrameImage.roundCorner(bmapFrame, 360), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Round White":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(AddFrameImage.roundCornerWhite(bmapFrame, 0.08f), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Rectagle White":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(AddFrameImage.frameWhite(bmapFrame, 0.05f, 0), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Cirlce White":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(AddFrameImage.frameWhite(bmapFrame, 0.05f, 1), imgView.getWidth(), imgView.getHeight()));
                            break;
                    }
                    //imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(mRecyclerImageFrame.get(position), imgView.getWidth(), imgView.getHeight()));
                }else if(tabMode == 3){
                    Bitmap bmap = mRecyclerImageOther.get(0);
                    switch (mDataOther.get(position)){
                        case "None":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(bmap, imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Mean Removal":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.applyMeanRemoval(bmap), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Blur":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.applyGaussianBlur(bmap), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Sharpen":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.sharpen(bmap, 10), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Reflection":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.applyReflection(bmap), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Hue Filter":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.applyHueFilter(bmap, 10), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Saturation Filter":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.applySaturationFilter(bmap, 1), imgView.getWidth(), imgView.getHeight()));
                            break;

                        case "Engrave":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.engrave(bmap), imgView.getWidth(), imgView.getHeight()));
                            break;
                        case "Emboss":
                            imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(ImageProcess.emboss(bmap), imgView.getWidth(), imgView.getHeight()));
                            break;

                    }
                   // imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(mRecyclerImageOther.get(position), imgView.getWidth(), imgView.getHeight()));
                }

            }
        };

        IntentFilter intentFilter = new IntentFilter(ImageAdapter.BROADCAST);
        registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        saveImageToStorage();
        this.unregisterReceiver(broadcastReceiver);
    }

    private void RequestRuntimePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.CAMERA)){
            Toast.makeText(this, "CAMERA permission allow us to access CAMERA app", Toast.LENGTH_LONG).show();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }
    private void addWidgets() {
        imgView = (ImageView) findViewById(R.id.imageSrc);
        bottomview = (BottomNavigationView)findViewById(R.id.bottom_view);
        frameImageEdit = (FrameLayout)findViewById(R.id.frameImageEdit);
        recylcerEditColor = (RecyclerView)findViewById(R.id.recycleViewColor);
        recylcerFrame = (RecyclerView)findViewById(R.id.recycleViewFrame);
        recylerOtherEdit = (RecyclerView)findViewById(R.id.recycleViewOtherEdit);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        linear = (LinearLayout)findViewById(R.id.linearcrop);
        btnCrop1 = (Button)findViewById(R.id.btncrop1);
        btnCrop2 = (Button)findViewById(R.id.btncrop2);
        btnCrop3 = (Button)findViewById(R.id.btncrop3);
        btnCrop4 = (Button)findViewById(R.id.btncrop4);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.btn_camera) {
            CameraOpen();
        } else if (item.getItemId() == R.id.btn_gallery) {
            GalleryOpen();
        } else if(item.getItemId() == R.id.btn_delete){
            Delete();
        }else if(item.getItemId() == R.id.btn_save){
            saveImageToStorage();
        }
        return true;
    }

    private void saveImageToStorage() {
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();

        if(drawable == null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Chú ý!");
            builder.setMessage("Không có ảnh cần lưu");
            builder.show();
        }else{
            Bitmap src = BipmapEfficiently.getResizedBitmap(drawable.getBitmap(), imgView.getWidth(), imgView.getHeight());
            if(src == BipmapEfficiently.getResizedBitmap(bitmap, imgView.getWidth(), imgView.getHeight())){
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Chú ý!");
                builder.setMessage("Ảnh chưa thay đổi");
                builder.show();
            }else{
                SaveImage saveImage = new SaveImage();
                saveImage.SaveImage(this, src);
            }
//                ContextWrapper cw = new ContextWrapper(getApplicationContext());
//                // path to /data/data/yourapp/app_data/imageDir
//                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//                // Create imageDir
//                File mypath=new File(directory,"fileSave" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(mypath);
//                    // Use the compress method on the BitMap object to write image to the OutputStream
//                    src.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        fos.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setTitle("Succeed");
//            builder.setMessage("Ảnh lưu thành công");
//            builder.show();
        }
    }


    private void Delete() {

        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable ();

        if(drawable == null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Chú ý!");
            builder.setMessage("Chưa chọn ảnh");
            builder.show();
        }else {

            imgView.setImageBitmap(bitmap);
        }

    }
    private void GalleryOpen() {

        Intent GalIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select image from Gallery"), PICK_FROM_GALLERY);
    }

    private void CameraOpen() {

        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(Environment.getExternalStorageDirectory(), "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        uri = Uri.fromFile(file);
        try {
            camIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            camIntent.putExtra("return-data", true);
            startActivityForResult(camIntent, PICK_FROM_CAMERA);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        String path = "";
        if (requestCode == PICK_FROM_GALLERY) {
            if (data != null) {
                uri = data.getData();
                path = getFilePathFromUri(uri);
                if(path != null){
                    Log.d("aaaaa", path);
                    //bitmap =BipmapEfficiently.decodeFile(path);
                    bitmap = BipmapEfficiently.getResizedBitmap(BitmapFactory.decodeFile(path), imgView.getWidth(), imgView.getHeight());
                }
            }
            // chọn cái ảnh coi
            imgView.setImageBitmap(bitmap);

        } else if (requestCode == PICK_FROM_CAMERA) {
            path = uri.getPath();

            bitmap = BipmapEfficiently.decodeFile(path);
//            bitmap = BipmapEfficiently.getResizedBitmap(bitmap, imgView.getWidth(), imgView.getHeight());
            imgView.setImageBitmap(bitmap);

        }
        if(requestCode == 1){
            if(data != null){
                Bundle bundl = data.getExtras();
                //Bitmap bitmap = (Bitmap)bundl.getParcelable("image");
                Bitmap bitmap = bundl.getParcelable("data");
                imgView.setImageBitmap(BipmapEfficiently.getResizedBitmap(bitmap, imgView.getWidth(), imgView.getHeight()));
                //imgView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    private String getFilePathFromUri(Uri contentUri) {
        String[] project = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, project, null, null, null);
        if(cursor == null) return null;
        int column_index = cursor.getColumnIndex(project[0]);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RequestPermissionCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission Canceled", Toast.LENGTH_LONG).show();
                }
            }
            break;

        }
    }


}

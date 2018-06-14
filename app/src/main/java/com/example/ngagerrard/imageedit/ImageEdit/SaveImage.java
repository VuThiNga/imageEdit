package com.example.ngagerrard.imageedit.ImageEdit;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Nga Gerrard on 07/05/2017.
 */
public class SaveImage {
    private Context thethis;
    private String nameOfFolder = "/ImageEdit";
    private String nameOfFile = "ImageEdit";

    public void SaveImage(Context context, Bitmap bitmap){
        this.thethis = context;
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + nameOfFolder;
        String currentDateOfTime = getCurrentDateOfTime();
        File dir = new File(file_path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, nameOfFile + currentDateOfTime+ ".jpg");

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            MakeSureFileWasCreatedThenMakeAvailable(file);
            AbleToSave();
        } catch (FileNotFoundException e) {
            unableToSave();
        } catch (IOException e) {
            unableToSave();
        }


    }

    private void unableToSave() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(thethis);
        builder.setTitle("Failure");
        builder.setMessage("Ảnh lưu thất bại");
        builder.show();
    }

    private void AbleToSave() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(thethis);
        builder.setTitle("Succeed");
        builder.setMessage("Ảnh lưu thành công");
        builder.show();
    }

    private void MakeSureFileWasCreatedThenMakeAvailable(File file) {
        MediaScannerConnection.scanFile(thethis, new String[]{file.toString()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {

            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.e("ExternalStorage", "Scaned" + path + ":");
                Log.e("ExternalStorage", "->uri=" + uri);
            }
        });
    }


    private String getCurrentDateOfTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formatedDate = df.format(c.getTime());
        return formatedDate;
    }
}

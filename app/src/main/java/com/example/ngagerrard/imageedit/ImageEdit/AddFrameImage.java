package com.example.ngagerrard.imageedit.ImageEdit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Nga Gerrard on 30/04/2017.
 */
public class AddFrameImage {
    public static Bitmap roundCorner(Bitmap src, float round) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // set canvas for painting
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        // config paint
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        // config rectangle for embedding
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        // draw rect to canvas
        canvas.drawRoundRect(rectF, round, round, paint);

        // create Xfer mode
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // draw source image to canvas
        canvas.drawBitmap(src, rect, rect, paint);

        // return final image
        return result;
    }

    public static Bitmap roundCornerWhite(Bitmap src, float round){
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas newCanvas = new Canvas(result);
        newCanvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        Rect frame = new Rect(
                (int)(width*round),
                (int)(width*round),
                (int)(width*(1- round)),
                (int)(height*(1-round)));
        RectF frameF = new RectF(frame);
        newCanvas.drawRoundRect(frameF, (float)(width*round), (float)(height*round), paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        newCanvas.drawBitmap(src, 0, 0, paint);
        return result;
    }

    public static Bitmap frameWhite(Bitmap src, float round, int cirle){
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create bitmap output
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas newCanvas = new Canvas(result);
        newCanvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        Rect frame = new Rect(
                (int)(width * round),
                (int)(width * round),
                (int)(width * (1 - round)),
                (int)(height * (1 - round)));
        RectF frameF = new RectF(frame);
        newCanvas.drawRoundRect(frameF, width * cirle, height * cirle, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        newCanvas.drawBitmap(src, 0, 0, paint);
        return result;
    }
}

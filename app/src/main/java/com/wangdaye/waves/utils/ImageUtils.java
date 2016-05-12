package com.wangdaye.waves.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.wangdaye.waves.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Bitmap utils.
 * */

public class ImageUtils {

    // calculate size.

    private static int calculateSizeAsPixels(float size, int pixels) {
        return (int) (size * (pixels / 1080.0));
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, float width, float height, int pixels) {
        width = calculateSizeAsPixels(width, pixels);
        height = calculateSizeAsPixels(height, pixels);

        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        int inSampleSize = 1;

        if (originalWidth > width || originalHeight > height) {
            int halfWidth = originalWidth / 2;
            int halfHeight = originalHeight / 2;
            while ((halfWidth / inSampleSize > width) &&(halfHeight / inSampleSize > height)) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // read bitmap.

    public static Bitmap readBitmapFormSrc(Context context, int src, float width, float height, int pixels) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), src, options);
        options.inSampleSize = calculateInSampleSize(options, width, height, pixels);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), src, options);
    }

    // write bitmap.

    public static void writeImageToFile(Context context, byte[] target, String fileName, boolean isGif) throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context,
                    context.getString(R.string.save_failed) + "\n" + context.getString(R.string.cannot_found_sd_card),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        File dirFile1 = new File(Environment.getExternalStorageDirectory(), "Pictures");
        if (!dirFile1.exists()) {
            if (!dirFile1.mkdir()) {
                Toast.makeText(context,
                        context.getString(R.string.save_failed)
                                + "\n"
                                + context.getString(R.string.cannot_create_file_dir) + " -1",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        File dirFile2 = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Waves/");
        if (!dirFile2.exists()) {
            if (!dirFile2.mkdir()) {
                Toast.makeText(context,
                        context.getString(R.string.save_failed)
                                + "\n"
                                + context.getString(R.string.cannot_create_file_dir) + " -2",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String format = ".png";
        if (isGif) {
            format = ".gif";
        }
        File image = new File(dirFile2, fileName + format);
        FileOutputStream fileOutputStream = new FileOutputStream(image);
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        dataOutputStream.write(target);
        dataOutputStream.close();

        Uri uri = Uri.fromFile(image);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(intent);

        Toast.makeText(
                context,
                context.getString(R.string.download_success),
                Toast.LENGTH_SHORT).show();
    }

    // exchange.

    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

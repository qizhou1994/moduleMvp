package com.zq.modulemvp.basemvp.util.img;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import androidx.annotation.NonNull;

import com.zq.modulemvp.basemvp.util.RxUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author zhouqi
 * @version 1.0
 * @desc for bitmap and drawable
 * @since 2020/3/17 16:30
 */
public class BitmapUtil {
    private BitmapUtil() {
    }

    /**
     * get bitmap for a drawable by {@link Canvas}
     * @param drawable drawable
     * @return bitmap
     */
    public static Bitmap getBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, drawable.getOpacity() != PixelFormat.OPAQUE
                ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * get bitmap for a drawable by {@link Canvas}
     * @return bitmap
     */
    public static Bitmap getBitmap(Context context,int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),drawableId);
        Bitmap bitmap1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);;
        if(bitmap!=null){
            bitmap.recycle();
        }
        return bitmap1;
    }

    /**
     * 给图片添加水印，水印会根据图片宽高自动缩放处理
     *
     * @param watermark              水印
     * @param image                  添加水印的图片
     * @param offsetX                添加水印的X轴偏移量
     * @param offsetY                添加水印的Y轴偏移量
     * @param srcWaterMarkImageWidth 水印对应的原图片宽度,即ui制作水印时候参考的图片画布宽度,应该是已知的图片最大宽度
     * @param addInLeft              true 在左下角添加水印，false 在右下角添加水印
     */
    public static Bitmap addWatermark(Bitmap watermark, Bitmap image, int srcWaterMarkImageWidth,
                                    int offsetX, int offsetY, boolean addInLeft,boolean recycle) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        if (0 == imageWidth || 0 == imageHeight) {
            throw new RuntimeException("AlbumBuilder: 加水印的原图宽或高不能为0！");
        }
        int watermarkWidth = watermark.getWidth();
        int watermarkHeight = watermark.getHeight();
        float scale = imageWidth / (float) srcWaterMarkImageWidth;
        if (scale > 1) scale = 1;
        else if (scale < 0.4) scale = 0.4f;
        int scaleWatermarkWidth = (int) (watermarkWidth * scale);
        int scaleWatermarkHeight = (int) (watermarkHeight * scale);
        Bitmap scaleWatermark = Bitmap.createScaledBitmap(watermark, scaleWatermarkWidth, scaleWatermarkHeight, true);

        Canvas canvas = new Canvas(image);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(50);
        if (addInLeft) {
            canvas.drawBitmap(scaleWatermark, offsetX, imageHeight - scaleWatermarkHeight - offsetY, paint);
        } else {
            canvas.drawBitmap(scaleWatermark, imageWidth - offsetX - scaleWatermarkWidth, imageHeight - scaleWatermarkHeight - offsetY, paint);
        }
        scaleWatermark.recycle();
        if(recycle){
            watermark.recycle();
        }
        return image;
    }

    /**
     * 给图片添加带文字和图片的水印，水印会根据图片宽高自动缩放处理
     *
     * @param watermark              水印图片
     * @param image                  要加水印的图片
     * @param srcWaterMarkImageWidth 水印对应的原图片宽度,即ui制作水印时候参考的图片画布宽度,应该是已知的图片最大宽度
     * @param text                   要添加的文字
     * @param offsetX                添加水印的X轴偏移量
     * @param offsetY                添加水印的Y轴偏移量
     * @param addInLeft              true 在左下角添加水印，false 在右下角添加水印
     */
    public static void addWatermarkWithText(@NonNull Bitmap watermark, Bitmap image, int srcWaterMarkImageWidth, @NonNull String text
            , int offsetX, int offsetY, boolean addInLeft,boolean recycle) {
        float imageWidth = image.getWidth();
        float imageHeight = image.getHeight();
        if (0 == imageWidth || 0 == imageHeight) {
            throw new RuntimeException("AlbumBuilder: 加水印的原图宽或高不能为0！");
        }
        float watermarkWidth = watermark.getWidth();
        float watermarkHeight = watermark.getHeight();
        float scale = imageWidth / (float) srcWaterMarkImageWidth;
        if (scale > 1) scale = 1;
        else if (scale < 0.4) scale = 0.4f;
        float scaleWatermarkWidth = watermarkWidth * scale;
        float scaleWatermarkHeight = watermarkHeight * scale;
        Bitmap scaleWatermark = Bitmap.createScaledBitmap(watermark, (int) scaleWatermarkWidth, (int) scaleWatermarkHeight, true);
        Canvas canvas = new Canvas(image);
        Paint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        float textsize = (float) (scaleWatermark.getHeight() * 2) / (float) 3;
        textPaint.setTextSize(textsize);
        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);
        if (addInLeft) {
            canvas.drawText(text, scaleWatermarkWidth + offsetX, imageHeight - textRect.height() - textRect.top - offsetY, textPaint);
        } else {
            canvas.drawText(text, imageWidth - offsetX - textRect.width() - textRect.left, imageHeight - textRect.height() - textRect.top - offsetY, textPaint);
        }

        Paint sacleWatermarkPaint = new Paint();
        sacleWatermarkPaint.setAntiAlias(true);
        if (addInLeft) {
            canvas.drawBitmap(scaleWatermark, offsetX, imageHeight - textRect.height() - offsetY - scaleWatermarkHeight / 6, sacleWatermarkPaint);
        } else {
            canvas.drawBitmap(scaleWatermark, imageWidth - textRect.width() - offsetX - scaleWatermarkWidth / 6, imageHeight - textRect.height() - offsetY - scaleWatermarkHeight / 6, sacleWatermarkPaint);
        }
        if(recycle){
            scaleWatermark.recycle();
        }
    }


    /**
     * 保存Bitmap到指定文件夹
     *
     * @param act         上下文
     * @param dirPath     文件夹全路径
     * @param bitmap      bitmap
     * @param namePrefix  保存文件的前缀名，文件最终名称格式为：前缀名+自动生成的唯一数字字符+.png
     * @param notifyMedia 是否更新到媒体库
     * @param callBack    保存图片后的回调，回调已经处于UI线程
     */
    public static void saveBitmapToDir(final Activity act, final String dirPath, final String namePrefix, final Bitmap bitmap, final boolean notifyMedia, final SaveBitmapCallBack callBack) {
        RxUtils.async(() -> {
            File dirF = new File(dirPath);
            if (!dirF.exists() || !dirF.isDirectory()) {
                if (!dirF.mkdirs()) {
                    return new Object();
                }
            }
            try {
                final File writeFile = File.createTempFile(namePrefix, ".png", dirF);
                FileOutputStream fos = null;
                fos = new FileOutputStream(writeFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                if (notifyMedia) {
//                        EasyPhotos.notifyMedia(act, writeFile);
                }
                return writeFile;
            } catch (IOException e) {
                e.printStackTrace();
                return e;
            }
        }, (type) -> {
                    if(type instanceof File){
                        callBack.onSuccess((File) type);
                    }else if(type instanceof IOException){
                        callBack.onSuccess((File) type);
                    }else{
                        callBack.onCreateDirFailed();
                    }
        });

    }

    /**
     * 给一张Bitmap添加水印文字。
     *
     * @param bitmap      源图片
     * @param content  水印文本
     * @param textSize 水印字体大小 ，单位pix。
     * @param color    水印字体颜色。
     * @param x        起始坐标x
     * @param y        起始坐标y
     *@param positionFlag  居左/居右
     * @param recycle  是否回收
     * @return 已经添加水印后的Bitmap。
     */
    public static Bitmap addTextWatermark(Bitmap bitmap, String content,
                                          int textSize, int color, float x,
                                          float y, boolean positionFlag, boolean recycle) {
        if (isEmptyBitmap(bitmap) || content == null) {
            return null;
        }
        Bitmap ret = bitmap.copy(bitmap.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);

        if (positionFlag) {
            canvas.drawText(content, x, bitmap.getHeight() - bounds.height() - bounds.top - y, paint);
        } else {
            canvas.drawText(content, bitmap.getWidth() - x - bounds.width() - bounds.left, bitmap.getHeight() - bounds.height() - bounds.top - y, paint);

        }
        if (recycle && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return ret;
    }

    /**
     * Bitmap对象是否为空。
     */
    public static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }
}

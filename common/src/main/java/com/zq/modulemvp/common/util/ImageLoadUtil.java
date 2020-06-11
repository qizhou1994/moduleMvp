package com.zq.modulemvp.common.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * desc
 * author zhouqi
 * data 2020/6/11
 */
public class ImageLoadUtil {

    public static void loadImgByUrl(Context context, ImageView imageView, String url){
        Glide.with(context).load(url).into(imageView);
    }
}

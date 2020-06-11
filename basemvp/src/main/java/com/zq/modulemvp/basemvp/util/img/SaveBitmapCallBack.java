package com.zq.modulemvp.basemvp.util.img;

import java.io.File;
import java.io.IOException;

/**
 * @description
 *
 * @author qizhou
 * @version 1.0
 * @date 2020/4/21
 */
abstract class SaveBitmapCallBack {
    
    abstract void onCreateDirFailed();
    abstract void onSuccess(File writeFile);

    public abstract void onIOFailed(IOException e);
}

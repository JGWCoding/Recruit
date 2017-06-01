package com.topine.www.recruit.myutils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.topine.www.recruit.MyApplication;

/**
 * Toast工具类  --->需要在Application里调用ToastUtils.init(this);
 * Created by Administrator on 2016/11/29.
 */

public class ToastUtils {
    static Toast ts;
    static boolean mIsShowToast = true;

    public static void setmIsShowToast(boolean mIsShowToast) {
        ToastUtils.mIsShowToast = mIsShowToast;
    }

    /**
     * 对线程做处理,子和main线程也可以,并只toast一次
     * @param msg
     * @param mContext
     */
    public static void showToast(final String msg, final Context mContext){ //做了线程的处理 --->子线程也可以
        if (mContext == null){
            Log.e("showToastError","Please first initialization in the Application");
            return;
        }
        if (!mIsShowToast) return;
        if (Thread.currentThread()== Looper.getMainLooper().getThread()) { //在主线程
            if (ts == null) {
                ts = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            } else {
                ts.setText(msg);
                ts.setDuration(Toast.LENGTH_SHORT);
            }
            ts.show();
        }else {
            MyApplication.mainThreadExecution(new Runnable() {
                @Override
                public void run() {
                    if (ts == null) {
                        ts = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
                    } else {
                        ts.setText(msg);
                        ts.setDuration(Toast.LENGTH_SHORT);
                    }
                    ts.show();
                }
            });
        }
    }

    /**
     * 一般是用来toast  Strings文件下的字符串
     * @param msg
     * @param cont
     */
    public static void showToast(int msg,Context cont){
        showToast(msg+"", cont);
    }

    public static void closeToast(){
        if (ts != null) {
            ts.cancel();
        }
    }

}

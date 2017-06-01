package com.topine.www.recruit.util;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.topine.www.recruit.MyApplication;

/**Toast工具类
 * Created by Administrator on 2016/11/29.
 */

public class ToastUtils {
    static Toast ts;
    static Context mContext;
    static boolean mIsShowToast = true;

    public static void setmIsShowToast(boolean mIsShowToast) {
        ToastUtils.mIsShowToast = mIsShowToast;
    }

    public static void init(Context context){ //在application里面进行初始化
        mContext = context;
    }
    //主 子 线程也可以show
    public static void showToast(final String msg){ //做了线程的处理 --->子线程也可以
        if (mContext == null){
            Log.e("showToastError","Please first initialization in the Application");
            return;
        }
        if (!mIsShowToast) return;
        if (Thread.currentThread()== Looper.getMainLooper().getThread()) { //在主线程
            if (ts == null) {
                ts = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);
            } else {
                if (msg==null) {
                    ts.setText("给予的字符串是null");
                }else {
                    ts.setText(msg);
                }
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

    public static void showToast(int msg){
        showToast(msg+"");
    }

    public static void closeToast(){
        if (ts != null) {
            ts.cancel();
        }
    }

}

package com.topine.www.recruit;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.topine.www.recruit.util.FileUtils;
import com.topine.www.recruit.util.ToastUtils;


/**
 * Created by Administrator on 2017/4/19.
 */

public class MyApplication extends Application {
    public static Context context ;
    public static Handler handler ;

    @Override
    public void onCreate() {
        super.onCreate();
        FileUtils.initContext(this);
        ToastUtils.init(this);
        context = this;
        handler = new Handler();
//        LeakCanary.install(this);
    }
    public static void mainThreadExecution(Runnable runnable) {
        handler.post(runnable);
    }
}

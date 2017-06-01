package com.topine.www.recruit.myutilssssss;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Administrator on 2017/5/12.
 */

public class AppUtils {
    /**
     * 判断是否是平板
     * @param context
     * @return  平板返回 True，手机返回 False
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}

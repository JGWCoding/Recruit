package com.topine.www.recruit.myutilssssss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Administrator on 2017/5/8.
 */

public class DialogUtils {
    public static ProgressDialog dialog;

    public static void alertDialog(Context context, String title, String message,boolean isCancel, final Runnable task) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title!=null)
        builder.setTitle(title);
        if (message!=null)
        builder.setMessage(message);
        builder.setCancelable(isCancel);
        builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (task!=null) {
                    task.run();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 圆形进度转圈
     *
     * @param context
     * @param title          标题 这个可为null
     * @param message        内容
     * @param cancelable     点击外部是否消失 一般为true可以点击消失
     * @param cancelListener dialog取消监听器
     */
    public static void showLoadingDialog(Context context, String title, String message, Boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        dialog = ProgressDialog.show(context, title, message, false,
                cancelable, cancelListener);
    }

    public static void showLoadingDialog(Context context, String title, String message, Boolean cancelable) {
        dialog = ProgressDialog.show(context, title, message, false,
                cancelable);
    }

    /**
     * 水平进度条
     *
     * @param context
     * @param title
     * @param message
     * @param cancelable
     */
    public static void showHorizhontalLoadingDialog(Context context, String title, String message, Boolean cancelable) {
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setIcon(null);// 设置提示的title的图标，默认是没有的
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setMax(100);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "中立",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                int i = 0;
                while (i < 100) {
                    try {
                        Thread.sleep(200);
                        // 更新进度条的进度,可以在子线程中更新进度条进度
                        dialog.incrementProgressBy(1);
                        // dialog.incrementSecondaryProgressBy(10)//二级进度条更新方式
                        i++;

                    } catch (Exception e) {

                    }
                }
                // 在进度条走完时删除Dialog
                dialog.dismiss();

            }
        }).start();
    }
}

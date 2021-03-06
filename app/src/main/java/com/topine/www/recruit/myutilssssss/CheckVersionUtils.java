package com.topine.www.recruit.myutilssssss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.BuildConfig;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.topine.www.recruit.MyApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * author by john
 * 检查版本更新工具,带有自动更新
 */

public class CheckVersionUtils {

    private static AlertDialog dialog;
    /**
     * 安装apk    需要注意的是为了设配7.0需要在AndroidManifest文件中配置 provider
     * @param context 上下文
     * @param path    文件路径
     */
    public static void installAPK(Context context, String path) {
        if (context==null||!new File(path).exists()) {
            ToastUtils.showToast(context,"安装出错了呀");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //防止打不开应用,开始新的栈
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//允许读取URL权限
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(path));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive"); //安装应用
        } else {
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
    /**
     * 检查版本是否需要更新,使用的是 versionName字段检查更新 如果需要versionCode字段自行改过来
     * @param context
     */
    public static void checkUpdate(final Context context) {
        final int versionName = 2; // TODO 网络获取到的版本 -->自己请求服务器
        final String appName = null;
        final String url = null;
        int currentVersion = (int) getLocalVersion(context);
        if (versionName<=currentVersion) {  //如果网络版本小于或等于本地版本就中断这个方法
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("");
        builder.setMessage("");
        builder.setCancelable(false);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downApp(context,appName,url);
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        DialogUtils.dialog.show();
    }

    /**
     * 下载APP  如果有需要可以在通知栏上显示进度
     * @param context
     * @param appName
     * @param appLine
     */
    private static void downApp(final Context context, final String appName, final String appLine) {
        File cfile =null;
        try {
            cfile = FileUtils.createFile(context, appName);
        } catch (IOException e) {
        }
        if (cfile.exists()) { //如果存在就直接安装  TODO 如果有这个文件,文件是受损的
            installAPK(context,cfile.getAbsolutePath());
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false); // 必须一直下载完，不可取消
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("版本升级中");
//        progressDialog.setMessage("正在下载安装包，请稍后");
        progressDialog.show();
        ThreadUtils.threadPools.execute(new Runnable() {
            @Override
            public void run() {
                //进行网络请求
                URL url = null;
                FileOutputStream fos = null;
                BufferedInputStream bis = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL(appLine);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(5000);
                    //成功
                    if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                        //开始下载
                        //获取网络输入流
                        bis = new BufferedInputStream(connection.getInputStream());
                        //文件大小
                        int length = connection.getContentLength();
                        progressDialog.setMax(length);
                        //缓冲区大小
                        byte[] buf = new byte[1024];
                        int size = 0;
                        //获取存储文件的路径，在该路径下新建一个文件为写入流作准备
                        final File cfile = FileUtils.createFile(context, appName);
                        //如果不存在则新建文件
                        if (!cfile.exists()) {
                            cfile.createNewFile();
                        }
                        //将流与文件绑定
                        fos = new FileOutputStream(cfile);
                        //记录进度条
                        int count=0;
                        //保存文件
                        while ((size = bis.read(buf)) != -1) {
                            fos.write(buf, 0, size);
                            count += size;
                            if (length > 0) {
                                //显示进度条
                                progressDialog.setProgress(count);
                            }
                        }
                        //主线程安装apk
                        MyApplication.mainThreadExecution(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();//隐藏掉progressDialog
                                installAPK(context,cfile.getAbsolutePath());
                            }
                        });
                    }else {
                        //失败
                      ToastUtils.showToast(context,"发生网络错误了");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                   ToastUtils.showToast(context,"发生未知错误了");
                }
            }
        });
    }

    /**
     * 获取本地版本号
     * @param context
     * @return  本地版本号
     */
    private static double getLocalVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            return 0.0;
        }
        return Double.valueOf(info.versionName);
    }
}

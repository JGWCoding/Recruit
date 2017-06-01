package com.topine.www.recruit.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.topine.www.recruit.BuildConfig;
import com.topine.www.recruit.MyApplication;
import com.topine.www.recruit.R;
import com.topine.www.recruit.common.Constants;
import com.topine.www.recruit.threadpool.ThreadPoolProxy;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 更新APP,是WiFi情况下自动更新要不要自动更新,移动网络下询问更新,
 * 需不需要强制更新,需不需要通知栏显示进度,需不需要在后台更新
 */

public class APPUtils  {
    private static AlertDialog dialog;
    private static String apkLink = "" ; //apk下载地址
    /**
     * @param context
     * @param appName
     * @return  true为APP install ,false is APP uninstall;
     */
    public static boolean isAppInstall(Context context,String appName) {
        PackageInfo packageInfo = null ;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(appName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo==null?false:true;
    }
    /**
     * 安装apk
     * @param context 上下文
     * @param path    文件路径
     */
    public static void installAPK(Context context, String path) {
        if (context==null||!new File(path).exists()) {
            ToastUtils.showToast("安装错误");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //防止打不开应用,开始新的栈
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//允许读取URL权限
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(path));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive"); //安装应用
        } else {
//            intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    /**
     * 获取应用程序版本（versionName）
     * @return 当前应用的版本号
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
    public static void checkUpdate(final Context context) {
        final int versionCode = 2; // TODO 网络获取到的版本
        int currentVersion = (int) getLocalVersion(context);
        if (versionCode<=currentVersion) {  //如果网络版本小于或等于本地版本就中断这个方法
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.tip));
        builder.setMessage(context.getResources().getString(R.string.has_new_version));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downApp(context);
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.unUpdate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private static void downApp(final Context context) {
        //点击了确定进行网络请求 ---> 更新本地版本  使用DownLoadManger下载
        //弹出一个进度条显示下载多少百分比
        File cfile = new File(FileUtils.getSDPath(), "Recruit.apk");
        if (cfile.exists()) {
            installAPK(context,cfile.getAbsolutePath());
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false); // 必须一直下载完，不可取消
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在下载安装包，请稍后");
        progressDialog.setTitle("版本升级");
        progressDialog.show();
        ThreadPoolProxy.getNormalThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                //进行网络请求
                URL url = null;
                FileOutputStream fos = null;
                BufferedInputStream bis = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL(Constants.DOWN_NEW_APP);
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
//                                final File cfile = new File(Environment.getExternalStorageDirectory().getPath()+File.separator+
//                                        "upgrade_apk"+File.separator+ "Recruit.apk");
                        final File cfile = new File(FileUtils.getSDPath(), "Recruit.apk");
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
                                progressDialog.dismiss();//销毁progressDialog
                                installAPK(context,cfile.getAbsolutePath());
                            }
                        });
                    }else {
                        //失败
                        ToastUtils.showToast("发生网络错误了");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtils.showToast("发生未知错误了");
                }
            }
        });
    }
}

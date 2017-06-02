package com.topine.www.recruit;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dou361.dialogui.DialogUIUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.topine.www.recruit.common.Constants;
import com.topine.www.recruit.js.JavaScriptObject;
import com.topine.www.recruit.myutilssssss.NetWorkStateUtils;
import com.topine.www.recruit.util.ToastUtils;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    WebView mWebView;
    private WebSettings webSettings;
//    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private boolean isLoadError;
    private JavaScriptObject javaScriptObject;
//    String currentUrl = "http://192.168.2.14/offer/public/recruit";
    String currentUrl = "http://app.zhongdingxing.com/recruit/public/recruit";
//         String   currentUrl = "http://app.zhongdingxing.com/recruit/public"; //记录当前url
//    String currentUrl = "http://192.168.2.14/offer/public/"; //记录当前url  -->以后封装到Constants里面
    private AlertDialog dialog;
    private ProgressDialog progressDialog;
    private Dialog dialogMd;

    //    String    currentUrl = "file:///android_asset/jstest.html"; //记录当前url
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        linearLayout = (LinearLayout) findViewById(R.id.ll);
        findViewById(R.id.img_error).setOnClickListener(this);
        linearLayout.setOnClickListener(this);
        mWebView=(WebView) findViewById(R.id.mWebView);
        initWebView();
//        APPUtils.checkUpdate(this); //检查更新
    }

    private void initWebView() {
        webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); //支持JavaScript

        //TODO 可以设置先不加载图片,到加载完毕(网页别的数据)就加载图片 --->有部分图片加载不出
        webSettings.setLoadsImagesAutomatically(true); //自动加载图片
        webSettings.setSupportZoom(false); //设置支持手势缩放
//        webSettings.setBuiltInZoomControls(true);//设置使用默认的缩放控制器,默认是false
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染页面速度
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置默认缓存
        webSettings.setDomStorageEnabled(true);      //设置文件存储
        webSettings.setAppCacheMaxSize(1024*1024*8);   //缓存大小
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath(); //路径
        webSettings.setAppCachePath(appCachePath); //设置缓存路径
        webSettings.setAllowFileAccess(true); //设置允许文件接收
        webSettings.setAppCacheEnabled(true); //缓存网页

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override//为了有些低于Android6.0版本不走onReceivedError方法 做个适配
            public void onReceivedTitle(WebView view, String title) {
                if(TextUtils.isEmpty(title)||title.toLowerCase().contains("error")||title.toLowerCase().contains("找不到网页")||title.toLowerCase().contains("网页无法打开")){
                    isLoadError = true;
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                progressBar.setProgress(newProgress);
//                if (newProgress>=100) {
//                    progressBar.setVisibility(View.GONE);
//                }else{
//                    progressBar.setVisibility(View.VISIBLE);
//                }
                super.onProgressChanged(view, newProgress);
            }
            @Override        //设置响应js 的Alert()函数
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                final AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();  //切记不可自己处理弹框,让js脚本自己处理,自己处理了js没处理会导致网页不可控
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }
            //设置响应js 的Confirm()函数
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Confirm");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }
//            设置响应js 的Prompt()函数
//            @Override
//            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
//                final View v = View.inflate(MainActivity.this, R.layout.prompt_dialog, null);
//                ((TextView) v.findViewById(R.id.prompt_message_text)).setText(message);
//                ((EditText) v.findViewById(R.id.prompt_input_field)).setText(defaultValue);
//                AlertDialog.Builder b = new AlertDialog.Builder(TestAlertActivity.this);
//                b.setTitle("Prompt");
//                b.setView(v);
//                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String value = ((EditText) v.findViewById(R.id.prompt_input_field)).getText().toString();
//                        result.confirm(value);
//                    }
//                });
//                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        result.cancel();
//                    }
//                });
//                b.create().show();
//                return true;
//            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //TODO 应该跳转页面 在自己的webView里面响应js里面的url
                view.loadUrl(url);
                currentUrl = url;
                return false;
            }
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {   //24版本之后走这个方法
//                //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
//                //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                    view.loadUrl(String.valueOf(request.getUrl()));
//                }
//                System.out.println("我");
//                ToastUtils.showToast("我执行了");
//                return super.shouldOverrideUrlLoading(view,request);
//            }
//加载样式 -->加载时显示加载中并隐藏webView
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) { //webView开始页面加载
                super.onPageStarted(view, url, favicon);
//                progressDialog = ProgressDialog.show(MainActivity.this, null, "正在加载中", false,
//                        false, null); //
//                if(dialogMd!=null&&!dialogMd.isShowing())
                //加载开始 -- 隐藏webView(不显示加载过程) > 如果上次是加载错误了就显示错误页面---> 显示加载中页面 --> 改变标识 -- 没有加载错误
                if(dialogMd==null) {
                    dialogMd = DialogUIUtils.showMdLoadingVertical(MainActivity.this, "加載中...").show();
                } else {
                    dialogMd.show();
                }
                mWebView.setVisibility(View.GONE);
                if (isLoadError) {
                    linearLayout.setVisibility(View.VISIBLE);
                }
                isLoadError = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) { //webView加载页面完毕
                super.onPageFinished(view,url);
//                progressDialog.dismiss();
                //加载完成 --> 进行判断是否加载错误 ---> 加载错误显示加载错误页面(判断没网提示没网) ---> 加载成功显示webView,隐藏错误页面(如果没网提示在就关闭)->关闭加载中页面

//                if (!mWebView.isShown()){  //如果页面加载完毕,webView没显示给予显示
//                    mWebView.setVisibility(View.VISIBLE);
//                }
                if (isLoadError) {
                    linearLayout.setVisibility(View.VISIBLE);
                    if(!NetWorkStateUtils.isNetworkConnected(MainActivity.this)) {
                        showDialog();
                    }
                }else {
                    mWebView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                    if (dialog!=null&&dialog.isShowing())
                    dialog.dismiss();
                }
                if (dialogMd!=null&&dialogMd.isShowing()) {
                    dialogMd.dismiss();
                }

            }

//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {//发生错误
//                super.onReceivedError(view, request, error);
//                ToastUtils.showToast("网络异常");
//                if(linearLayout.isShown()) return;
//                linearLayout.setVisibility(View.VISIBLE);
//                if(!NetWorkStateUtils.isNetworkConnected(MainActivity.this)) {
//                    showDialog();
//                }
//            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                isLoadError = true;
                if(linearLayout.isShown()) return;
                linearLayout.setVisibility(View.VISIBLE);
                if(!NetWorkStateUtils.isNetworkConnected(MainActivity.this)) {
                    showDialog();
                }
            }

        });
        mWebView.loadUrl(currentUrl);
        if(javaScriptObject == null) {
            javaScriptObject = new JavaScriptObject(this, mWebView); //保存这个对象有些方法中可以用到它里面的属性值
        }
        mWebView.addJavascriptInterface(javaScriptObject,"Android"); //给予js调用
//        new JavaScriptObject(this,mWebView).uploadPictures();
//        mWebView.addJavascriptInterface(new JavaScriptObject(this),"Android"); //window.Android.uploadPicture();

//        mWebView.loadUrl("javascript:sum(3,8)");  //加载JavaScript中的函数
        // mWebView.addJavascriptInterface(getHtmlObject(), "jsObj");   window.jsObj.HtmlcallJava(); //js调用Android方法
//        mWebView.evaluateJavascript("javascript:sum(3,8)", new ValueCallback<String>() {
//            @Override
//            public void onReceiveValue(String value) {
//
//            }
//
//        });
        getFCMData();

    }

    private void showDialog() {
        if (dialog==null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("沒有網絡,請檢查後再試");
            builder.setCancelable(false);
            builder.setPositiveButton("確定", null);
            builder.setNegativeButton("取消", null);
            dialog = builder.create();
        }
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl(currentUrl);
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO 点击刷新
        if (v.getId()==R.id.ll) {
            mWebView.setVisibility(View.INVISIBLE);//断网之后重连会看到不好的界面
            linearLayout.setVisibility(View.GONE);//断网之后重连会一直显示
            mWebView.loadUrl(currentUrl);
        }else if (v.getId()==R.id.img_error){
            mWebView.setVisibility(View.INVISIBLE);//断网之后重连会看到不好的界面
            linearLayout.setVisibility(View.GONE);//断网之后重连会一直显示
            mWebView.loadUrl(currentUrl);
        }
    }
    //点击事件
    public void click(View view) {
        Log.e("====",currentUrl);
        mWebView.setVisibility(View.INVISIBLE);//断网之后重连会看到不好的界面
        linearLayout.setVisibility(View.GONE);//断网之后重连会一直显示
        mWebView.loadUrl(currentUrl);
    }
     //从通知栏里传递的数据过来
    public void getFCMData() {  //TODO 只有应用在后台时推送过来点开通知才可以收到 data 信息
        if (getIntent().getExtras() != null) { //自定义的消息键值对在这里取出来作出指定动作 --->解析不了
            Set<String> extras = getIntent().getExtras().keySet();
            for (String key : extras) {
                Object value = getIntent().getExtras().get(key);
              //TODO 根据推送过来的消息作出指定动作
                if(key.equals("title")) {
                    Toast.makeText(this,key+"的值是:"+value,Toast.LENGTH_SHORT).show();
                }else if(key.equals("ico")) {
                    Toast.makeText(this,key+"的值是:"+value,Toast.LENGTH_SHORT).show();
                }
                Log.e("=====",key+"====="+value);
            }
        }
       String token = FirebaseInstanceId.getInstance().getToken(); //得到token值
//        ToastUtils.showToast(token+"");
        Log.e("=====",token+"");
    }
    @Override   //申请权限 打电话回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //判断请求码，确定当前申请的权限
        if (requestCode == Constants.REQUEST_CALL_PHONE_CODE) {
            //判断权限是否申请通过
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //授权成功
                javaScriptObject.call(javaScriptObject.phoneNumber);
            } else {
                //授权失败
                ToastUtils.showToast("没有授权,打不了电话哦");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override  //实现webView中返回的按钮
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==1) {   //双击退出程序
//            finish();
//            return true;
//        }
        if (keyCode==KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_CANCELED && requestCode==Constants.REQUEST_MAP_CODE) {
            ToastUtils.showToast("你取消了启动地图");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        javaScriptObject = null;
    }


}

package com.topine.www.recruit.js;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.topine.www.recruit.MainActivity;
import com.topine.www.recruit.MyApplication;
import com.topine.www.recruit.common.Constants;
import com.topine.www.recruit.myutilssssss.LogUtils;
import com.topine.www.recruit.util.APPUtils;
import com.topine.www.recruit.util.HttpUtil;
import com.topine.www.recruit.util.ToastUtils;
import com.yalantis.ucrop.entity.LocalMedia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 该类用于js函数调用
 */
public class JavaScriptObject {
    public String phoneNumber;
    Activity mContext;
    WebView webView;

    public JavaScriptObject(Activity mContxt, WebView webView) {
        this.mContext = mContxt;
        this.webView = webView;
    }

    @JavascriptInterface //TODO 跳转地图  ----> 难点是不同的地图需要匹配  ---> 还要获取地点
    public void useMap(String place) { //地点
//        String pkg = "com.autonavi.minimap"; //高德地图的报名
//        String act = "android.intent.action.VIEW";
//        if (!APPUtils.isAppInstall(mContext,pkg)) {  //判断是否装了高德地图APP
//            String dat= "androidamap://keywordNavi?sourceApplication=momo&keyword=深圳北站&style=2";
//            Intent intent=new Intent(act, android.net.Uri.parse(dat));
//            intent.setPackage(pkg);
//            mContext.startActivity(intent);
//        }
//        ToastUtils.showToast("启动地图");
//        Intent intent=new Intent(Intent.ACTION_VIEW);
//        String url = "https://maps.google.com/maps?q=深圳北站&z=17&hl=zh";
//        Uri uri = Uri.parse(url);
//        intent.setData(uri);
//        mContext.startActivityForResult(intent,Constants.REQUEST_MAP_CODE);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:0,0?q="+place)); //查询某个具体地点
        try{
            mContext.startActivity(intent);
        }catch (Exception e) {
            ToastUtils.showToast("没有地图应用");
        }
    }

    @JavascriptInterface //TODO whatsAPP跳转
    public void whatsAPP(String phoneNumber) {
        if (APPUtils.isAppInstall(mContext, "com.whatsapp")) { //判断是否装了whatsAPP
            try{
                Uri uri = Uri.parse("smsto:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                ClipboardManager clipboard =
                        (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText("我是我的東西");
                intent.setPackage("com.whatsapp");
                mContext.startActivity(intent);
            }catch(Exception e){
                Log.e("Exception", e.toString());
            }
        }else {
            ToastUtils.showToast("你还没安装whatsAPP");
        }
    }

    @JavascriptInterface  //TODO whatsAPP跳转
    public void whatsAPP1(String contact) {
        if (APPUtils.isAppInstall(mContext, "com.whatsapp")) { //判断是否装了whatsAPP
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            mContext.startActivity(sendIntent);
        }else {
            ToastUtils.showToast("你还没安装whatsAPP");
        }
    }
    @JavascriptInterface   //sdk17版本以上加上注解
    public void print(String data) {
        Log.e("=====", data);
    }

    @JavascriptInterface   //TODO 调用相当于清除上一个url, 在按返回键不会出bug(一直返回上一个url地址)
    public void goBack() {
        mContext.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(0, 0));
    }

    @JavascriptInterface//TODO 用来调用打电话的功能
    public void call(String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent callIntent = new Intent(Intent.ACTION_CALL, uri);//直接打电话
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            this.phoneNumber = phoneNumber; //在这里申请权限,记住下该号码以便申请成功打出去 --->弹出申请权限框
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_CALL_PHONE_CODE);
        } else {
//            Intent dialntent = new Intent(Intent.ACTION_DIAL, uri); //跳到系统拨号处
//            mContext.startActivity(dialntent);
            mContext.startActivity(callIntent);
        }
    }

    @JavascriptInterface//TODO 订阅主题  --->以后可以收到主题推送过来的消息
    public void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic); //订阅主题
    }

    @JavascriptInterface//TODO 取消订阅主题  --->以后不想收到这个主题推送过来的消息
    public void unsubscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic); //取消订阅主题
    }

    @JavascriptInterface   //TODO 推送消息给某个主题  ---->还没封装好
    public void pushTopic(String topic) {
        //封装主题的话
        final String json = "{ \"notification\": {\n" +
                "    \"title\": \"FCM123\",\n" +
                "    \"text\": \"PostMan信息发送\"\n" +
                "  },\n" +
                "  \"data\": {\n" +
                "    \"title\": \"头\",\n" +
                "    \"ico\": \"ico\"\n" +
                "  }\n" +
                "  \"to\" : \"" + FirebaseInstanceId.getInstance().getToken() + "\"\n" +
                "}";
        String token = FirebaseInstanceId.getInstance().getToken();   //得到token值
        System.out.println(token);
        HttpUtil.postJson(json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MyApplication.mainThreadExecution(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("发送失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e("=====", string);
                MyApplication.mainThreadExecution(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("发送成功");
                    }
                });
            }
        });
    }

    @JavascriptInterface   //TODO 推送数据--->具体个人的 --->还没封装好
    public void pushData(String title,String information,String token) {
        final String json = "{ \"data\": {\n" +
                "    \"score\": \"5x1\",\n" +
                "    \"time\": \"15:10\"\n" +
                "  },\n" +
                "  \"to\" : \""+token+"\"\n" +
                "}";
//        String token = FirebaseInstanceId.getInstance().getToken();     //得到token值
        HttpUtil.postJson(json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showToast("发送失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e("=====", string);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int failure = jsonObject.optInt("failure");
                    Log.e("=======", failure + "==" + Thread.currentThread().getName());
                    if (failure > 0) {
                        ToastUtils.showToast("发送失败个数为" + failure);
                    } else {
                        ToastUtils.showToast("发送成功");
                    }
                } catch (JSONException e) {
                    ToastUtils.showToast("发送失败");
                }
            }
        });
    }

    @JavascriptInterface   //TODO 推送通知--->具体个人的
    public void pushNotification(String title,String information,String token) {
        final String json = "{ \"notification\": {\n" +
                "    \"title\": \""+title+"\",\n" +
                "    \"body\": \""+information+"\"\n" +
                "  },\n" +
                "  \"to\" : \""+token+"\"\n" +
                "}";
//        String token = FirebaseInstanceId.getInstance().getToken();     //得到token值
        HttpUtil.postJson(json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showToast("发送失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e("=====", string);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int failure = jsonObject.optInt("failure");
                    Log.e("=======", failure + "==" + Thread.currentThread().getName());
                    if (failure > 0) {
                        ToastUtils.showToast("发送失败个数为" + failure);
                    } else {
                        ToastUtils.showToast("发送成功");
                    }
                } catch (JSONException e) {
                    ToastUtils.showToast("发送失败");
                }
            }
        });
    }

    @JavascriptInterface   //TODO 推送通知和信息--->具体个人的 --->还没封装好
    public void pushNotificationAndData(String title,String information,String token) {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.putOpt("to", FirebaseInstanceId.getInstance().getToken());
//            jsonObject.putOpt("priority", "normal");
//            JSONObject notifiObj = new JSONObject();
//            notifiObj.putOpt("body", "自造json");
//            notifiObj.putOpt("title", "嘿嘿");
//            notifiObj.putOpt("icon", "new");
//            jsonObject.putOpt("notification", notifiObj);
//            JSONObject dataObj = new JSONObject();
//            dataObj.putOpt("volume", "3.21.15");
//            dataObj.putOpt("contents", "www");
//            jsonObject.putOpt("data", dataObj);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        final String json = "{\n" +
                "\t\"to\" : \"" + token + "\",\n" +
                "  \"priority\" : \"normal\",\n" +
                "  \"notification\" : {\n" +
                "    \"body\" : \""+information+"\",\n" +
                "    \"title\" : \""+title+"\",\n" +
                "    \"icon\" : \"new\"\n" +
                "  },\n" +
                "  \"data\" : {\n" +
                "    \"volume\" : \"3.21.15\",\n" +
                "    \"contents\" : \"http://www.news-magazine.com/world-week/21659772\"\n" +
                "  },\n" +
                "  \"time_to_live\" : 130,\n" +
                "  \"collapse_key\" : \"Updates Available\"\n" +
                "}";
        HttpUtil.postJson(json, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showToast("发送失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.e("=====", string);
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    int failure = jsonObject.optInt("failure");
                    Log.e("=======", failure + "==" + Thread.currentThread().getName());
                    if (failure > 0) {
                        ToastUtils.showToast("发送失败个数为" + failure);
                    } else {
                        ToastUtils.showToast("发送成功");
                    }
                } catch (JSONException e) {
                    ToastUtils.showToast("发送失败");
                }
            }
        });
    }

    @JavascriptInterface//TODO 应该在登录之后打开首页调用这个方法,保证登录之后每次都可以获取token值
    public String getToken() {
        LogUtils.e("1234======");
        return FirebaseInstanceId.getInstance().getToken()==null?"1234":FirebaseInstanceId.getInstance().getToken();
    }

    @JavascriptInterface//TODO 上传一张头像,并把图片压缩转字符串
    public void uploadPicture() {
        Log.e("=====", "uploadPicture我被调用了");
        FunctionConfig config = new FunctionConfig();
        //设置图片选择模式  1 TYPE_IMAGE 2 TYPE_VIDEO
        config.setType(LocalMediaLoader.TYPE_IMAGE);
        //默认的裁剪模式
        config.setCopyMode(FunctionConfig.CROP_MODEL_1_1);
        //是否压缩图片
        config.setCompress(true);
        //压缩图片
        config.setEnablePixelCompress(true);
        config.setEnableQualityCompress(true);
        //设置图片多选// 2单选 or 1多选 MODE_MULTIPLE MODE_SINGLE
        config.setSelectMode(FunctionConfig.MODE_SINGLE);
        config.setShowCamera(true);
        //是否允许预览图片
        config.setEnablePreview(false);
        //是否可以裁剪
        config.setEnableCrop(true);
        int cropW = 100;  //设置比原图值大会返回原图
        int cropH = 100;
        config.setCropW(cropW);
        config.setCropH(cropH);
        //qq风格的选择模式
        config.setCompressQuality(80); //100默认无损图片
        config.setImageSpanCount(4);  //相册显示每行的个数
        // 先初始化参数配置，在启动相册
        PictureConfig.init(config);
        PictureConfig.getPictureConfig().openPhoto((MainActivity) (mContext), new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> resultList) {
                if (resultList != null && resultList.size() > 0) {
                    // 注意：如果压缩过，在上传的时候，取 media.getCompressPath(); // 压缩图compressPath
                    // 注意：没有压缩过，在上传的时候，取 media.getPath(); // 原图path
                    // 注意：如果media.getCatPath();不为空的话 就代表裁剪的图片，上传时可取，但是如果又压缩过，则取最终压缩过的compressPath
                    webView.loadUrl("javascript:setImage('" + getPictureString(resultList.get(0)) + "')");
                }
            }
        });
    }

    @JavascriptInterface   //TODO 选择头像,并把图片压缩转字符串,上传多张图片
    public void uploadPictures() {
        Log.e("=====", "uploadPictures我被调用了");
        FunctionConfig config = new FunctionConfig();
        //设置图片选择模式  1 TYPE_IMAGE 2 TYPE_VIDEO
        config.setType(LocalMediaLoader.TYPE_IMAGE);
        //默认的裁剪模式
        config.setCopyMode(FunctionConfig.CROP_MODEL_1_1);
        //是否压缩图片
        config.setCompress(true);
        //压缩图片
        config.setEnablePixelCompress(true);
        config.setEnableQualityCompress(true);
        //设置图片多选// 2单选 or 1多选 MODE_MULTIPLE MODE_SINGLE
        config.setSelectMode(FunctionConfig.MODE_MULTIPLE);
        config.setShowCamera(true);
        //是否允许预览图片
        config.setEnablePreview(true);
        //是否可以裁剪
        config.setEnableCrop(false);
        int cropW = 160;  //设置比原图值大会返回原图
        int cropH = 90;
        config.setCropW(cropW);
        config.setCropH(cropH);
        //qq风格的选择模式
        config.setCheckNumMode(true);
        config.setCompressQuality(80); //100默认无损图片
        config.setImageSpanCount(4);  //相册显示每行的个数
        config.setMaxSelectNum(9); //设置最大选择数量
        // 先初始化参数配置，在启动相册
        PictureConfig.init(config);
        PictureConfig.getPictureConfig().openPhoto((MainActivity) (mContext), new PictureConfig.OnSelectResultCallback() {
            @Override
            public void onSelectSuccess(List<LocalMedia> resultList) {
                if (resultList != null && resultList.size() > 0) {
                    // 注意：如果压缩过，在上传的时候，取 media.getCompressPath(); // 压缩图compressPath
                    // 注意：没有压缩过，在上传的时候，取 media.getPath(); // 原图path
                    // 注意：如果media.getCatPath();不为空的话 就代表裁剪的图片，上传时可取，但是如果又压缩过，则取最终压缩过的compressPath
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < resultList.size(); i++) {
                        String pictureString = getPictureString(resultList.get(i));
                        jsonArray.put(pictureString);
                    }
                    webView.loadUrl("javascript:showData('" + getPictureString(resultList.get(0)) + "')");
                }
            }
        });
    }

    //返回图片加密的base64的字符串
    private String getPictureString(LocalMedia media) {
        File file = new File(media.getCompressPath());
        System.out.println("=====" + file.length());
        if (!file.exists()) return null;
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000); //代表有1000容量的缓存区
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            // TODO 发送String 给网页
            String base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
//                        byte[] decode = Base64.decode(base64, Base64.NO_WRAP);  //转回图片
//                        new ImageView(mContxt).setImageBitmap(BitmapFactory.decodeByteArray(decode,0,decode.length));
//            webView.loadUrl("javascript:setImage('"+base64+"')");
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
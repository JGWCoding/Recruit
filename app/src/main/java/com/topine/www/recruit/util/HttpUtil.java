package com.topine.www.recruit.util;

import android.text.TextUtils;
import android.util.Base64;

import com.topine.www.recruit.MyApplication;
import com.topine.www.recruit.bean.UserBean;
import com.topine.www.recruit.common.Constants;
import com.topine.www.recruit.threadpool.ThreadPoolProxy;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/4/18.
 */

public class HttpUtil {

    public static void uploadFileInThreadByOkHttp(final String actionUrl, final File tempPic, final Callback callback) {
       ThreadPoolProxy.getNormalThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
        final String pic_path = tempPic.getPath();
        String targetPath = FileUtils.getThumbDir()+"compressPic.jpg";
        //调用压缩图片的方法，返回压缩后的图片path
        final String compressImage = PictureUtil.compressImage(pic_path, targetPath, 100);
        final File compressedPic = new File(compressImage);
        if (compressedPic.exists()) {
            uploadPicture(actionUrl, compressedPic,MediaType.parse("image/png"), callback);
            //TODO 上传后要不要删除压缩图片路径?
        }else{//压缩路径不存在直接上传
            uploadPicture(actionUrl, tempPic,MediaType.parse("image/png"), callback);
        }
            }
        });
    }
    public static void uploadPicture(String url, File file, MediaType mediaType, Callback callBack) {  //上传头像

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MediaType.parse("multipart/form-data"));
        if (!file.exists()|| TextUtils.isEmpty(url)){
            return;
        }
        //addFormDataPart视项目自身的情况而定
        //builder.addFormDataPart("description","2.jpg");
//        builder.addFormDataPart("img", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f));
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
        //构建请求体
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        enqueue(request,callBack);
    }


    /**
     * post请求,上传json字符串
     * @param json  json字符串
     * @param callBack 一个请求失败或成功的回调
     */
    public static void postJson(final String json, final Callback callBack) { //我请求推送给我的信息
        //申明给服务端传递一个json串
        //创建一个OkHttpClient对象
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        //json为String类型的json数据
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, json);
        //创建一个请求对象
        Headers heads = new Headers.Builder()
                .add("Content-Type","application/json")
                .add("Authorization",Constants.APP_SERVER_CODE).build();
        Request request = new Request.Builder()
                .url(Constants.GOOGLE_SEND_LINK)  //固定的推送地址
                .headers(heads)
                .post(requestBody)
                .build();
        //发送请求获取响应
        enqueue(request,callBack);
    }

    /**
     * 发送网络请求
     * @param request
     * @param callBack
     */
    private static void enqueue(final Request request, final Callback callBack) { //发送网络请求
        ThreadPoolProxy.getNormalThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                okHttpClient.newCall(request).enqueue(callBack);
            }
        });
    }

    /**
     * 使用ksoap上传用户信息,给予用户头像上传
     * @param bean
     */
    public static void uploadUserInfo(final UserBean bean) {
        final SoapObject request = new SoapObject(Constants.NAME_SPACE, Constants.METHOD_UPLOAD_LOG);
        request.addProperty("postTime",bean.getCreatDate() + " " + bean.getTime()); //上传的传数
        if (!TextUtils.isEmpty(bean.getImgUrls())) { //上传图片核心
            try {
                File file = new File(bean.getImgUrls());
                if (!file.exists()) return;
                FileInputStream stream = new FileInputStream(file);
                ByteArrayOutputStream out = new ByteArrayOutputStream(1000); //代表有1000容量的缓存区
                byte[] b = new byte[1000];
                int n;
                while ((n = stream.read(b)) != -1)
                    out.write(b, 0, n);
                stream.close();
                out.close();
                String base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
                request.addProperty("buffer", base64);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                request.addProperty("buffer", "");
            } catch (IOException e) {
                e.printStackTrace();
                request.addProperty("buffer", "");
            }
        }
        ThreadPoolProxy.getNormalThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER11);
                envelope.bodyOut = request;//由于是发送请求，所以是设置bodyOut
                envelope.dotNet = true;//由于是.net开发的webservice，所以这里要设置为true
                HttpTransportSE httpTransportSE = new HttpTransportSE(Constants.URI);
                try {
                    httpTransportSE.call(Constants.ACTION_UPLOAD_USER, envelope);
                    if(envelope.getResponse()!=null) {
                        String result = envelope.getResponse().toString(); //解析返回的状态
                        String tagStart = "<Status>";
                        String tagEnd = "</Status>";
                        String status = result.substring(result.indexOf(tagStart) + 8, result.indexOf(tagEnd));
                       if (status=="0") {
                           System.out.println("成功了");
                       }
                    }else {
                        System.out.println("没有执行上传了");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("执行上传错误");
                    MyApplication.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast("网络异常");
                        }
                    });
                }
                System.out.println("执行上传了");
            }
        });
    }
}

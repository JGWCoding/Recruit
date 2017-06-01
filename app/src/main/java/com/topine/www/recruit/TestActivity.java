package com.topine.www.recruit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.topine.www.recruit.js.JavaScriptObject;
import com.topine.www.recruit.test.LiveActivity;
import com.topine.www.recruit.util.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.body)
    EditText body;
    @Bind(R.id.button2)
    Button button2;
    @Bind(R.id.activity_test)
    LinearLayout activityTest;
    private JavaScriptObject javascript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        Intent intent = new Intent(this, MyService.class);
//        startService(intent);//开启服务
//        startService(new Intent(this, ServiceA.class));
//        startService(new Intent(this, ServiceB.class));
//        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            startService(new Intent(this, MyJobService.class));
//            LogUtils.e(android.os.Build.VERSION.SDK_INT+"");
//        }
        ButterKnife.bind(this);
        javascript = new JavaScriptObject(this, null);
        startActivity(new Intent(this,LiveActivity.class));
    }

    @OnClick(R.id.button2)
    public void onViewClicked() {
        if (TextUtils.isEmpty(title.getText().toString()) || TextUtils.isEmpty(body.getText().toString())) {
            ToastUtils.showToast("内容不符合");
            return;
        }
        Log.e(TAG, "onViewClicked: " + FirebaseInstanceId.getInstance().getToken());
        String token = "fazMAmHKnGE:APA91bHQ2dGqOiphF7ocqi72fN6e9Kf4jmFwhdTV47mPYY6T0PJFYE6z5NTJv0yXn4jj1Al" +
                "IIyVFnJrUrv0b1tCzB67pXaAFo_0GHQRhi89CLrEkzoVYUk3r9OA9ziqSxtMKX0I1Wt0M";
        //fGbuqQqehbM:APA91bFcjpFeOfgPF1KGmGbrAk8PD_8UszdzOxOHSn9IE223DLzOVFvrtdhUN5Yeso5M5UcTXAuf35vAXir2KHDjTal6yWhin8q4W6b2ABg6FrkRicKu8w4bvb9FxmiszMz0Bvk3GOeO
        String token1 = FirebaseInstanceId.getInstance().getToken();
        javascript.pushNotificationAndData(title.getText().toString(), body.getText().toString(), token1);
//        javascript.pushFromAndroid(title.getText().toString(),body.getText().toString(),FirebaseInstanceId.getInstance().getToken());
    }
}

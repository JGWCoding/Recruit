/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.topine.www.recruit.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.topine.www.recruit.MainActivity;
import com.topine.www.recruit.R;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) { //用于接受谷歌推送过来的信息,接受到不同信息做出不同的反应
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //data信息 -->  数据信息 -->可以在前台和后台进程可以接受到,使用GCM发送
        //notification信息-->通知信息-->在前台进程可以接受,如果是后台进程接受到会自动生成一个通知
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) { //TODO 如果应用在前台进程中收到需要处理数据需要这里
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> map = remoteMessage.getData();
            for (String key: map.keySet()) {
                String value = map.get(key);
                Log.d(TAG, "onMessageReceived: key:"+value);
            }
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob(); //需要长时间运行就使用这个方法(10秒以上) (可以自己开启一个线程等)
            } else {
                // Handle message within 10 seconds
                handleNow();//10秒以内运行就使用这个方法(10秒以内)
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody()); //只有前台自己处理
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *创建和显示一条通知包含接受到的FCM的信息
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title ,String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        //TODO 这里应该传递些传过来的数据
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        //小米系统设置显示图标没用,小米系统只会显示的APP的默认图标
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setTicker("通知来了")//第一次来在状态栏显示一下
                .setContentTitle(title==null?"消息通知":title) //设置标题
                .setContentText(messageBody) //设置内容
                .setAutoCancel(true) //设置自动取消
                .setSmallIcon(R.mipmap.cover) //设置小图标
//                .setOngoing(true) //设置不可清除
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.cover))//设置大图标
                .setSound(defaultSoundUri) //设置声音提示
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

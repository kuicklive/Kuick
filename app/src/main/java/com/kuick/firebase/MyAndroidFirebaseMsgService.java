package com.kuick.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kuick.R;
import com.kuick.SplashScreen.SecondSplashScreen;
import com.kuick.SplashScreen.SplashScreen;
import com.kuick.util.comman.Constants;
import com.kuick.util.comman.Utility;

import org.json.JSONObject;

import java.util.List;

import static com.kuick.base.BaseActivity.baseActivity;
import static com.kuick.util.comman.Constants.INTENT_KEY_DIRECTION_CODE;
import static com.kuick.util.comman.Utility.PrintLog;


public class MyAndroidFirebaseMsgService extends FirebaseMessagingService {
    private final String chaneName = "com.kuick";
    String TAG = "FirebaseMsgService";
    private Intent intent;
    private AppOpenBroadCast appOpenBroadCast;
    private String order_id;
    private String liveStreamingSlug;

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {

            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Utility.PrintLog(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        //Implement this method if you want to store the token on your server
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Utility.PrintLog(TAG, "onMessageReceived" + remoteMessage);


        appOpenBroadCast = new AppOpenBroadCast();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kuick.firebase.AppOpenBroadCast");
        registerReceiver(appOpenBroadCast, filter);

        try {

            Utility.PrintLog(TAG, "notification data - " + remoteMessage.getData());

            Object login_device = remoteMessage.getData().get("logout_device");
            String extra_param = remoteMessage.getData().get("extra_param");
            //Object live_streaming_slug  = remoteMessage.getData().get("live_streaming_slug ");
            Object obj_title = remoteMessage.getData().get("title");
            Object obj_body = remoteMessage.getData().get("body");
            Object obj_type = remoteMessage.getData().get("type");


            if (remoteMessage.getData() != null) {

                Utility.PrintLog("FirebaseNotification", "if part");

                if (obj_title != null && obj_body != null && obj_type != null) {

                    String title = obj_title.toString();
                    String message = obj_body.toString();
                    String type = obj_type.toString();
                    Utility.PrintLog(TAG, "create notification" + type);

                    if (
                            type.equals(Constants.order) ||
                                    type.equals(Constants.order_confirmed) ||
                                    type.equals(Constants.order_rejected) ||
                                    type.equals(Constants.order_shipped) ||
                                    type.equals(Constants.order_delivered) ||
                                    type.equals(Constants.order_returned)

                    ) {
                        JSONObject jsonObject = new JSONObject(extra_param);
                        String order_id = jsonObject.getString("order_id");
                        this.order_id = order_id;
                        createNotificationAccount(message, title, 1, type);
                    } else if (type.equals(Constants.notify_me)) {

                        JSONObject jsonObject = new JSONObject(extra_param);
                        String liveStreamingSlug = jsonObject.getString("live_streaming_slug");

                        Utility.PrintLog(TAG, "extra param live slug : " + liveStreamingSlug);
                        this.liveStreamingSlug = liveStreamingSlug;
                        createNotificationAccount(message, title, 1, type);

                    } else if (type.equals(Constants.logout_device)) {
                        createNotificationAccount(message, title, 1, type);

                    } else if (type.equals(Constants.push_notification)) {
                        createNotificationAccount(message, title, 1, type);
                    } else if (type.equals(Constants.product_in_cart)){
                        createNotificationAccount(message, title, 1, type);
                    } else {
                        createNotificationAccount(message, title, 1, type);
                    }

                    Utility.PrintLog("FirebaseNotification", "if parameter part");

                } else {

                    if (remoteMessage != null) {
                        createNotificationAccount(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), 1, "push");
                        Utility.PrintLog("FirebaseNotification", "else part remoteMessage");
                    }

                    Utility.PrintLog("FirebaseNotification", "else parameter part");
                }

            } else {


                Utility.PrintLog("FirebaseNotification", "else part");

            }

        } catch (Exception e) {
            Utility.PrintLog("FirebaseNotification", "exception part " + e.toString());
        }

    }

    private void createNotificationAccount(String message, String title, int randomNumber, String notificationType) {


        Log.e(TAG, "createNotificationAccountVerify(): ");
        Log.e(TAG, "createNotificationAccountVerify() message : " + message);
        Log.e(TAG, "createNotificationAccountVerify() title : " + title);
        Log.e(TAG, "createNotificationAccountVerify() randomNumber : " + randomNumber);
        Log.e(TAG, "createNotificationAccountVerify() notificationType: " + notificationType);

        Intent intent = null;
        TaskStackBuilder stackBuilder = null;
        PendingIntent pendingIntent = null;

        if (
                notificationType.equals(Constants.order) ||
                        notificationType.equals(Constants.order_confirmed) ||
                        notificationType.equals(Constants.order_rejected) ||
                        notificationType.equals(Constants.order_shipped) ||
                        notificationType.equals(Constants.order_delivered) ||
                        notificationType.equals(Constants.order_returned)
        ) {

            intent = new Intent(this, SplashScreen.class)
                    .putExtra(INTENT_KEY_DIRECTION_CODE, "3")
                    .putExtra(Constants.ORDER_ID, order_id);

        } else if (notificationType.equals(Constants.notify_me)) {

            intent = new Intent(this, SplashScreen.class)
                    .putExtra(INTENT_KEY_DIRECTION_CODE, "1")
                    .putExtra(Constants.INTENT_KEY_STREAM_SLUG, liveStreamingSlug);

        } else if (notificationType.equals(Constants.logout_device)) {

            if (baseActivity != null) {
                if (baseActivity.userPreferences != null && baseActivity.isLogged()) {
                    baseActivity.userPreferences.removeCurrentUser();
                    baseActivity.startActivity(new Intent(baseActivity, SecondSplashScreen.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
            intent = new Intent(this, SplashScreen.class).putExtra(INTENT_KEY_DIRECTION_CODE, "2")
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } else if (notificationType.equals(Constants.product_in_cart)){
            intent = new Intent(this, SplashScreen.class)
                    .putExtra(INTENT_KEY_DIRECTION_CODE, "4");
        } else {

            intent = new Intent(this, SplashScreen.class);
        }

        PrintLog(TAG, "notificationType : " + notificationType);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        /*if(
                        notificationType.equals(Constants.order) ||
                        notificationType.equals(Constants.order_confirmed) ||
                        notificationType.equals(Constants.order_rejected) ||
                        notificationType.equals(Constants.order_shipped) ||
                        notificationType.equals(Constants.order_delivered) ||
                        notificationType.equals(Constants.order_returned))
        {
            pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        }else {

            pendingIntent = PendingIntent.getActivity(this,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }*/

        pendingIntent = PendingIntent.getActivity(this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "All Notifications";
            CharSequence channelName = "adsff";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.bgSplash);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);

            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable._ic_notification_blue)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(MyAndroidFirebaseMsgService.this, R.color.bgSplash))
                    .setSound(notificationSoundURI)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setContentIntent(pendingIntent)
                    .setGroupSummary(true)
                    .setGroup("KEY_NOTIFICATION_GROUP")
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setChannelId(channelId)
                    .build();

            notificationManager.notify(randomNumber, notification);

        } else {

            NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable._ic_notification_blue)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(MyAndroidFirebaseMsgService.this, R.color.bgSplash))
                    .setSound(notificationSoundURI)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentIntent(pendingIntent)
                    .setGroupSummary(true)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setGroup("KEY_NOTIFICATION_GROUP");

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(randomNumber, mNotificationBuilder.build());

        }

    }


}
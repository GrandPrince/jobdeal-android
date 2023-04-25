package com.justraspberry.jobdeal.core;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.BuildConfig;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.event.NewNotificationEvent;
import com.justraspberry.jobdeal.model.Device;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.Notification;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.view.login.LoginActivity;
import com.justraspberry.jobdeal.view.main.MainActivity;

import java.util.concurrent.ExecutionException;

import androidx.annotation.LongDef;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

public class FCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String action = remoteMessage.getData().get("action");
        int senderId = Integer.parseInt(remoteMessage.getData().get("senderId"));
        String value = remoteMessage.getData().get("value"); //notification ID
        Job job = App.getInstance().getGson().fromJson(remoteMessage.getData().get("job"), Job.class);
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");
        Integer badge = Integer.parseInt(remoteMessage.getData().get("badge"));

        Timber.e(remoteMessage.getData().toString());

        if (EventBus.getDefault().hasSubscriberForEvent(NewNotificationEvent.class))
            ApiRestClient.getInstance().getNotificationById(Integer.parseInt(value));
        else {
            if (job != null)
                showNotification(body,
                        title,
                        Integer.parseInt(value),
                        action,
                        badge,
                        job.getId());
            else
                showNotification(body, title, Integer.parseInt(value), action, badge, -1);
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Device device = new Device();
        device.setAppVersion(String.valueOf(BuildConfig.VERSION_CODE));
        device.setModel(Build.MODEL);
        device.setOsVersion(Build.VERSION.CODENAME);
        device.setType("2");
        device.setToken(s);

        ApiRestClient.getInstance().addDevice(device);
    }

    @SuppressLint("CheckResult")
    public void showNotification(String body, String title, int notificationId, String action, int badge, int jobId) {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.putExtra("action", action);
        loginIntent.putExtra("jobId", jobId);
        loginIntent.putExtra("notificationId", notificationId);
        loginIntent.setAction(String.valueOf(notificationId));


        int notificationCount = App.getInstance().getCurrentUser().getNotificationCount();
        App.getInstance().getCurrentUser().setNotificationCount(notificationCount - 1);
        ApiRestClient.getInstance().readNotification(notificationId);


        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, loginIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, loginIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Consts.GENERAL_CHANNEL);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setLights(Color.parseColor("#43e695"), 500, 1000);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setNumber(badge);
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
        builder.setAutoCancel(true);


        /*if (job.getUser().getAvatar() != null) {
            final FutureTarget<Bitmap> futureTarget = Glide.with(getApplicationContext()).asBitmap()
                    .load(job.getUser().getAvatar())
                    .apply(new RequestOptions().transforms(new CenterCrop(), new CircleCrop())).submit();

            LoadImageTask task = new LoadImageTask(new LoadImageTask.OnSuccess() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    builder.setLargeIcon(bitmap);
                    Glide.with(getApplicationContext()).clear(futureTarget);
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify(jobId, builder.build());
                }
            });


            task.execute(futureTarget);
        } else {*/
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(jobId, builder.build());
        //}
    }

    private static class LoadImageTask extends AsyncTask<FutureTarget<Bitmap>, Void, Bitmap> {
        private OnSuccess onSuccess;

        interface OnSuccess {
            void onSuccess(Bitmap bitmap);
        }

        LoadImageTask(OnSuccess onSuccess) {
            this.onSuccess = onSuccess;
        }

        @SafeVarargs
        @Override
        protected final Bitmap doInBackground(FutureTarget<Bitmap>... futureTargets) {
            try {
                return futureTargets[0].get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null)
                onSuccess.onSuccess(bitmap);
        }
    }
}

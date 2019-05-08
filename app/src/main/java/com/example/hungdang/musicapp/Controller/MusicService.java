package com.example.hungdang.musicapp.Controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.hungdang.musicapp.R;

import java.io.IOException;

import static com.example.hungdang.musicapp.Permission.App.CHANNEL_ID;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.bitmap;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.countMusic;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.maxbar;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.mediaPlayer;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.mmr;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.pos_media;
import static com.example.hungdang.musicapp.Controller.MainActivity.listMusic;

public class MusicService extends Service{

    NotificationManager notificationManager;
    NotificationCompat.Builder notification;
    RemoteViews contentView;
    Handler handler;
    public static int quitNotifi;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mediaPlayer == null) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(listMusic.get(countMusic).getPath());
                mediaPlayer.prepare();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pos_media = 0;
        maxbar = mediaPlayer.getDuration() / 1000;
        mediaPlayer.start();
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Khởi tạo notification
        String input = intent.getStringExtra("inputExtra");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
//        Custom notification
        contentView = new RemoteViews(getPackageName(), R.layout.notification);
        contentView.setImageViewBitmap(R.id.image, bitmap);
        contentView.setInt(R.id.next, "setBackgroundResource", R.drawable.next);
        contentView.setInt(R.id.pre, "setBackgroundResource", R.drawable.pre);
        contentView.setTextViewText(R.id.namesong, listMusic.get(countMusic).getNameSong());

        handler = new Handler();
        handler.postDelayed(new Runnable() {

            public void run() {
                if (mediaPlayer.isPlaying()) {
                    contentView.setInt(R.id.play, "setBackgroundResource", R.drawable.pause);
                    contentView.setImageViewBitmap(R.id.image, bitmap);
                    notification.setContent(contentView);
                    notificationManager.notify(1, notification.build());


                }
                if (!mediaPlayer.isPlaying()) {
                    contentView.setInt(R.id.play, "setBackgroundResource", R.drawable.play);
                    notification.setContent(contentView);
                    notificationManager.notify(1, notification.build());
                }
                if (quitNotifi == 1) {
                    handler.removeCallbacks(this);
                    quitNotifi = 0;
                    notificationManager.cancel(1);
                } else {
                    handler.postDelayed(this, 500); //added this line
                }

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        if (!mediaPlayer.isPlaying()) {

                            mediaPlayer.stop();
                            try {
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setDataSource(listMusic.get(countMusic).getPath());
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            contentView.setImageViewBitmap(R.id.image, bitmap);
                            notificationManager.notify(1, notification.build());
                            maxbar = mediaPlayer.getDuration() / 1000;
                            mediaPlayer.start();

                        }
                    }
                });


            }
        }, 500);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("play");
        intentFilter.addAction("quit");
        intentFilter.addAction("next");
        intentFilter.addAction("pre");
        intentFilter.addAction("main");
        registerReceiver(new Broadcast(contentView), intentFilter);


        Intent switchIntent = new Intent();
        switchIntent.setAction("play");
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.play, pendingSwitchIntent);

        switchIntent.setAction("quit");
        pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.quit, pendingSwitchIntent);

        switchIntent.setAction("next");
        pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.next, pendingSwitchIntent);

        switchIntent.setAction("pre");
        pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.pre, pendingSwitchIntent);

        switchIntent.setAction("main");
        sendBroadcast(switchIntent);


        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContent(contentView)
                .setContentTitle("Example Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.iconnoti)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, notification.build());


        startForeground(1, notification.build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaPlayer.pause();
        pos_media = mediaPlayer.getCurrentPosition();
    }

    public class Broadcast extends BroadcastReceiver {
        RemoteViews remoteViews;


        public Broadcast(RemoteViews remoteViews) {
            this.remoteViews = remoteViews;
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals("play")) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    pos_media = mediaPlayer.getCurrentPosition();
                    notification.setContent(remoteViews);
                    notificationManager.notify(1, notification.build());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        FragmentPlayMusic.anim.pause();
                    }
                } else {
                    mediaPlayer.seekTo(pos_media);
                    mediaPlayer.start();
                    notification.setContent(remoteViews);
                    notificationManager.notify(1, notification.build());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        FragmentPlayMusic.anim.resume();
                    }
                }
            }

            if (intent.getAction().equals("quit")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    FragmentPlayMusic.anim.pause();
                }
                stopSelf();
                mediaPlayer.pause();
                quitNotifi = 1;

            }

            if (intent.getAction().equals("next")) {
                mediaPlayer.stop();
                mediaPlayer.seekTo(mediaPlayer.getDuration());
                if (countMusic == (listMusic.size() - 1)) {
                    pos_media = 0;
                    countMusic = 0;
                } else {
                    countMusic++;
                    pos_media = 0;
                }

                try {
                    mediaPlayer.setDataSource(listMusic.get(countMusic).getPath());
                    mmr.setDataSource(getApplicationContext(), Uri.parse(listMusic.get(countMusic).getPath()));//filePath is correct.
                    byte[] img = mmr.getEmbeddedPicture();
                    bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                    contentView.setImageViewBitmap(R.id.image, bitmap);
                    notificationManager.notify(1, notification.build());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                maxbar = mediaPlayer.getDuration() / 1000;
                mediaPlayer.start();
            }

            if (intent.getAction().equals("pre")) {
                mediaPlayer.stop();
                mediaPlayer.seekTo(mediaPlayer.getDuration());
                if (countMusic == 0) {
                    pos_media = 0;
                    countMusic = listMusic.size() - 1;
                } else {
                    countMusic--;
                    pos_media = 0;
                }

                try {
                    mediaPlayer.setDataSource(listMusic.get(countMusic).getPath());
                    mmr.setDataSource(getApplicationContext(), Uri.parse(listMusic.get(countMusic).getPath()));//filePath is correct.
                    byte[] img = mmr.getEmbeddedPicture();
                    bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                    contentView.setImageViewBitmap(R.id.image, bitmap);
                    notificationManager.notify(1, notification.build());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
            }

        }
    }
}


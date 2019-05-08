package com.example.hungdang.musicapp.Controller;

import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.hungdang.musicapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;

import static com.example.hungdang.musicapp.Controller.MainActivity.listMusic;

public class FragmentPlayMusic extends Fragment implements View.OnClickListener {

    public static MediaPlayer mediaPlayer;
    public static int countMusic ;

    public static Bitmap bitmap;
    public static MediaMetadataRetriever mmr;
    public static ObjectAnimator anim;
    public static int maxbar;
    Handler handler;

    private Button btn_play;
    private TextView tv_seekbarleft, tv_seekbarright, btn_pre, btn_next;

    private SeekBar seekbar;
    private Handler mHandler = new Handler();
    public static int pos_media = 0;


    CircularImageView MediaImage;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){


        View view = inflater.inflate(
                R.layout.activity_playmusic, container, false);
        setControl(view);
        setAction();
        return view;
    }

    private void setAction() {
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    if (mediaPlayer != null) {
                        pos_media = progress * 1000;
                        mediaPlayer.seekTo(pos_media);
                        mediaPlayer.start();
                    } else {
                        seekBar.setProgress(0);
                    }
                }
                if (progress == seekBar.getMax()) {
                    if (mediaPlayer != null) {
                        progress = 0;
                        seekBar.setProgress(progress);
                        btn_play.setBackgroundResource(R.drawable.play);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                seekbar.setMax(maxbar);

                if (mediaPlayer != null) {

                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekbar.setProgress(mCurrentPosition);
//                    if(bitmap!=null){
//                        MediaImage.setImageBitmap(bitmap);
//                    }
                    if (mediaPlayer.isPlaying()) {
//
                        tv_seekbarright.setText(getTimeMedia(mediaPlayer.getDuration()));
                        tv_seekbarleft.setText(getTimeMedia(mediaPlayer.getCurrentPosition()));
                    }



                }

                if (mediaPlayer != null) {

                    if (mediaPlayer.isPlaying()) {
                        btn_play.setBackgroundResource(R.drawable.pause);

                    } else if (!mediaPlayer.isPlaying()) {
                        btn_play.setBackgroundResource(R.drawable.play);

                    }

                    MediaImage.setImageBitmap(bitmap);


                    btn_next.setAlpha(0.2f);
                    btn_pre.setAlpha(0.2f);


                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if (!mediaPlayer.isPlaying()) {

                                mediaPlayer.stop();

                                if (countMusic == (listMusic.size() - 1)) {
                                    pos_media = 0;
                                    countMusic = -1;
                                }
                                countMusic++;
                                pos_media = 0;

                                try {
                                    mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setDataSource(listMusic.get(countMusic).getPath());
                                    mediaPlayer.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                mmr.setDataSource(getContext(), Uri.parse(listMusic.get(countMusic).getPath()));//filePath is correct.
                                byte[] img = mmr.getEmbeddedPicture();
                                bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                                if (bitmap != null) {
                                    MediaImage.setImageBitmap(bitmap);
                                }
                                maxbar = mediaPlayer.getDuration() / 1000;
                                seekbar.setMax(maxbar);
                                btn_play.setBackgroundResource(R.drawable.pause);
                                mediaPlayer.start();
                                anim.start();

                            }
                        }
                    });
                }


                mHandler.postDelayed(this, 500);
            }
        });
    }

    private void setControl(View view) {
        mmr = new MediaMetadataRetriever();

        MediaImage = view.findViewById(R.id.MediaImage);

        btn_play = view.findViewById(R.id.btn_play);
        tv_seekbarleft = view.findViewById(R.id.tv_seekbarleft);
        tv_seekbarright = view.findViewById(R.id.tv_seekbarright);
        btn_pre = view.findViewById(R.id.btn_pre);
        btn_next = view.findViewById(R.id.btn_next);
        seekbar = view.findViewById(R.id.seekbar);

        btn_play.setOnClickListener(FragmentPlayMusic.this);
        btn_pre.setOnClickListener(FragmentPlayMusic.this);
        btn_next.setOnClickListener(FragmentPlayMusic.this);

        anim = ObjectAnimator.ofFloat(MediaImage, "rotation", 0, 360);
        anim.setDuration(40000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(ObjectAnimator.RESTART);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_play:
                if (!isMyServiceRunning(MusicService.class)) {
                    getActivity().startService(new Intent(getActivity(), MusicService.class));
                }

                if (mediaPlayer == null) {

                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(listMusic.get(countMusic).getPath());
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mmr.setDataSource(getContext(), Uri.parse(listMusic.get(countMusic).getPath()));//filePath is correct.
                    byte[] img = mmr.getEmbeddedPicture();
                    bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
                    if (bitmap != null) {
                        MediaImage.setImageBitmap(bitmap);
                    }
                    maxbar = mediaPlayer.getDuration() / 1000;
                    seekbar.setMax(maxbar);
                    mediaPlayer.start();
                    btn_play.setBackgroundResource(R.drawable.pause);
                    tv_seekbarright.setText(getTimeMedia(mediaPlayer.getDuration()));
                    tv_seekbarleft.setText(getTimeMedia(mediaPlayer.getCurrentPosition()));

                    anim.start();

                } else if (!mediaPlayer.isPlaying()) {

                    mediaPlayer.start();
                    btn_play.setBackgroundResource(R.drawable.pause);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        anim.resume();
                    }

                } else if (mediaPlayer.isPlaying()) {

                    mediaPlayer.pause();
                    pos_media = mediaPlayer.getCurrentPosition();
                    btn_play.setBackgroundResource(R.drawable.play);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        anim.pause();
                    }
                }

                break;
            case R.id.btn_pre:
                if (mediaPlayer != null) {
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            btn_pre.setAlpha(1);
                            mediaPlayer.stop();
                            mediaPlayer.seekTo(mediaPlayer.getDuration());
                            if (countMusic == 0) {
                                pos_media = 0;
                                countMusic = listMusic.size() - 2;
                            } else {
                                countMusic = countMusic - 2;
                                pos_media = 0;
                            }

                        }
                    }, 100);
                }

                break;
            case R.id.btn_next:
                if (mediaPlayer != null) {
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btn_next.setAlpha(1);
                            mediaPlayer.stop();
                            mediaPlayer.seekTo(mediaPlayer.getDuration());
                        }
                    }, 100);
                    break;
                }
        }
    }

    private String getTimeMedia(int time) {
        time = time / 1000;
        String timeConvert = "";
        int min, sec;
        sec = time % 60;
        min = time / 60;
        timeConvert = min + ":" + sec;
        return timeConvert;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}

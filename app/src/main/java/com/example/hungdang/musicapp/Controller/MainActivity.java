package com.example.hungdang.musicapp.Controller;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.example.hungdang.musicapp.Adapter.AdapterVP;
import com.example.hungdang.musicapp.Interface.ChangeFragment;
import com.example.hungdang.musicapp.Model.DetailMusic;
import com.example.hungdang.musicapp.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.bitmap;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.countMusic;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.maxbar;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.mediaPlayer;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.mmr;
import static com.example.hungdang.musicapp.Controller.FragmentPlayMusic.pos_media;


public class MainActivity extends AppCompatActivity implements ChangeFragment {

    public static List<DetailMusic> listMusic;
    int position;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setControl();
    }

    private void setControl() {

        listMusic = new ArrayList<>();
        ArrayList<HashMap<String, String>> arrayList = getPlayList(Environment.getExternalStorageDirectory() + "/Download");
        if(arrayList!=null){
            for (int i = 0; i < arrayList.size(); i++) {
                DetailMusic detailMusic = new DetailMusic();
                detailMusic.setPath(arrayList.get(i).get("file_path"));
                detailMusic.setNameSong(arrayList.get(i).get("file_name"));
                listMusic.add(detailMusic);
            }

            ArrayList<Fragment> fragments = new ArrayList<>();
            fragments.add(new FragmentListMusic());
            fragments.add(new FragmentPlayMusic());

            viewPager = findViewById(R.id.viewpager);
            AdapterVP adapterVP = new AdapterVP(getSupportFragmentManager(), fragments);
            viewPager.setAdapter(adapterVP);
        }
    }


    ArrayList<HashMap<String, String>> getPlayList(String rootPath) {
        ArrayList<HashMap<String, String>> fileList = new ArrayList<>();


        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath()));
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    fileList.add(song);
                }

            }
            return fileList;
        } catch (Exception e) {
            Log.d("eqweqwe", "loi");
            return null;
        }
    }

    @Override
    public void ChangeFragment(int position) {
        this.position = position;
        viewPager.setCurrentItem(1);

        if (mediaPlayer != null) {
            countMusic = position - 1;

            mediaPlayer.stop();
        } else {
            countMusic = position;
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(listMusic.get(countMusic).getPath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmr.setDataSource(getApplicationContext(), Uri.parse(listMusic.get(countMusic).getPath()));//filePath is correct.
            byte[] img = mmr.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            maxbar = mediaPlayer.getDuration()/1000;
            mediaPlayer.start();

        }

        if (mediaPlayer != null) {
            mediaPlayer.seekTo(pos_media);
        }
        if (!isMyServiceRunning(MusicService.class)) {
            this.startService(new Intent(this, MusicService.class));
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

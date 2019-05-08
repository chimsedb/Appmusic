package com.example.hungdang.musicapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hungdang.musicapp.Model.DetailMusic;
import com.example.hungdang.musicapp.R;

import java.util.List;

import static com.example.hungdang.musicapp.Controller.MainActivity.listMusic;

public abstract class AdapterRC extends RecyclerView.Adapter<AdapterRC.MyViewHolder> {


    private List<DetailMusic> list;
    private Context context;

    public AdapterRC(List<DetailMusic> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_songname;
        ImageView img_song;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_songname = itemView.findViewById(R.id.tv_rc_songname);
            img_song = itemView.findViewById(R.id.img_rc);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_listmusic, viewGroup, false);
        final MyViewHolder vh = new MyViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnButtonClicked(vh.getAdapterPosition());

            }
        });

        return vh;
    }

    protected abstract void OnButtonClicked(int position);

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
//        myViewHolder.img_song.setImageBitmap(bitmap);
        myViewHolder.tv_songname.setText(OpimalSongName(list.get(i).getNameSong()));
        myViewHolder.img_song.setImageBitmap(getBitmap(i));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public Bitmap getBitmap(int i) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Bitmap bitmapimg;
        mmr.setDataSource(context, Uri.parse(listMusic.get(i).getPath()));//filePath is correct.
        byte[] img = mmr.getEmbeddedPicture();
        bitmapimg = BitmapFactory.decodeByteArray(img, 0, img.length);
        return bitmapimg;
    }

    public String OpimalSongName(String songname) {
        String s1 = songname.substring(0, songname.indexOf("."));
        s1.trim();
        if (s1 != "") {
            return s1;
        } else {
            return songname;
        }
    }
}

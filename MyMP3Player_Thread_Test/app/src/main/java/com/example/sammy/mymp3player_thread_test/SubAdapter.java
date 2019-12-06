package com.example.sammy.mymp3player_thread_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.CustomViewHolder> {

    int layout;
    ArrayList<MusicData> list = new ArrayList<>();

    public static int selectPosition;

    public SubAdapter(int layout, ArrayList<MusicData> list) {
        this.layout = layout;
        this.list = list;
    }

    @NonNull
    @Override
    public SubAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubAdapter.CustomViewHolder customViewHolder, final int position) {

        customViewHolder.txtMusicTitle.setText(list.get(position).getMusicTitle());
        customViewHolder.txtSingerName.setText(list.get(position).getSinger());

        //앨범 이미지 가져오기
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(list.get(position).albumImg);

        byte[] data=mmr.getEmbeddedPicture();
        if(data!=null){
            Bitmap bitmap=BitmapFactory.decodeByteArray(data,0,data.length);
            customViewHolder.albumImg.setImageBitmap(bitmap);
        }else {
            customViewHolder.albumImg.setImageResource(R.mipmap.empty_album);
        }

        customViewHolder.albumImg.setAdjustViewBounds(true);
        customViewHolder.albumImg.setTag(position);
        customViewHolder.albumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPosition = position;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView albumImg;
        TextView txtMusicTitle, txtSingerName;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            albumImg = itemView.findViewById(R.id.albumImg);
            txtMusicTitle = itemView.findViewById(R.id.txtMusicTitle);
            txtSingerName = itemView.findViewById(R.id.txtSingerName);
        }
    }
}

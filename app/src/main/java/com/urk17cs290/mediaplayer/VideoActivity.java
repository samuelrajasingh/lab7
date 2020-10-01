package com.urk17cs290.mediaplayer;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        final MediaController mediaController;
        ListView listView = findViewById(R.id.listView);
        String[] vidNames={"Trump !!","Tom & Jerry"};
        final int[] vidIds={R.raw.video1,R.raw.video2};
                final String videoPath = String.format("android.resource://%s/%s", getPackageName(),R.raw.video1);


        final VideoView videoView = (VideoView) findViewById(R.id.video_view);
//        final ImageView thumbnailView=(ImageView)findViewById(R.id.videoView_thumbnail);
//
//        Glide.with(getApplicationContext()).load(videoPath).into(thumbnailView);
        //you can add progress dialog here until video is start playing;

        mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setKeepScreenOn(true);
        videoView.setVideoPath(videoPath);
//        videoView.start();         //call this method for auto playing video

        videoView.setOnPreparedListener(mediaPlayer -> mediaPlayer.seekTo(10));
        videoView.setOnCompletionListener(mediaPlayer -> videoView.stopPlayback());

        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vidNames);
        listView.setAdapter(myArrayAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Uri uri1 = Uri.parse(String.format("android.resource://%s/%s", getPackageName(),vidIds[position]));
            videoView.setVideoURI(uri1);
            mediaController.setMediaPlayer(videoView);
        });

    }
}
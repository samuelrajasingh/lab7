package com.urk17cs290.mediaplayer.music.fragments;


import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.urk17cs290.mediaplayer.music.musicutils.ExtraUtils;
import com.urk17cs290.mediaplayer.music.musicutils.SongUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.squareup.picasso.Picasso;

public class SongDetailsFragment extends Fragment {

    private static final String SONGPARAM = "param1";
    TextView share;
    TextView delete;
    TextView addtoPlaylist;
    TextView songname;
    TextView songgame1;
    TextView album;
    TextView artist;
    TextView year;
    TextView duration;
    TextView location;
    ImageView albumart;
    private Song song;


    public SongDetailsFragment() {
        // Required empty public constructor
    }

    public static SongDetailsFragment newInstance(Long songid) {
        SongDetailsFragment fragment = new SongDetailsFragment();
        Bundle args = new Bundle();
        args.putLong(SONGPARAM, songid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            long s = getArguments().getLong(SONGPARAM);
            song = SongUtils.getSongById(s);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_details, container, false);
        albumart = view.findViewById(R.id.albumArt);
        share = view.findViewById(R.id.share);
        delete = view.findViewById(R.id.delete);
        addtoPlaylist = view.findViewById(R.id.addtoPlaylist);
        songname = view.findViewById(R.id.songName);
        album = view.findViewById(R.id.album);
        artist = view.findViewById(R.id.artist);
        duration = view.findViewById(R.id.duration);
        location = view.findViewById(R.id.location);
        year = view.findViewById(R.id.year);

        songgame1 = view.findViewById(R.id.songName1);
        songgame1.setText(song.getTitle());
        songname.setText(song.getTitle());
        artist.setText(song.getArtist());
        album.setText(song.getAlbum());
        year.setText(String.valueOf(song.getYear()));
        duration.setText(DateUtils.formatElapsedTime(song.getDurationSeconds()));
        location.setText(song.getFilePath());
        Picasso.get().load(ExtraUtils.getUrifromAlbumID(song)).placeholder(R.drawable.ic_launcher_foreground).error(R.drawable.ic_launcher_foreground).into(albumart);
        share.setOnClickListener(v -> shareSong(v));
        delete.setOnClickListener(v -> deleteSong(v));

        addtoPlaylist.setOnClickListener(v -> addtoPlaylist(v));
        return view;
    }

    private void deleteSong(View v) {
        //deleteSong

    }

    private void addtoPlaylist(View v) {
        //addtoPlaylist

    }

    private void shareSong(View v) {
        ExtraUtils.shareSong(v.getContext(),song);

    }


}

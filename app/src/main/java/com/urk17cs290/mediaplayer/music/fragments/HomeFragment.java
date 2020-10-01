package com.urk17cs290.mediaplayer.music.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.urk17cs290.mediaplayer.music.activities.PlayingNowList;
import com.urk17cs290.mediaplayer.music.adapters.MostPlayedSongsAdapter;
import com.urk17cs290.mediaplayer.music.adapters.RecentSongsAdapter;
import com.urk17cs290.mediaplayer.music.musicutils.RecentUtils;
import com.urk17cs290.mediaplayer.music.musicutils.SongUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.urk17cs290.mediaplayer.music.playerMain.Main;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomeFragment extends Fragment {

    RecentSongsAdapter recentSongsAdapter;
    MostPlayedSongsAdapter mostPlayedSongsAdapter;
    LinearLayout recents;
    LinearLayout mostPlayed;
    LinearLayout openAllSongs;
    LinearLayout openAlbums;
    LinearLayout openPlaylists;
    LinearLayout openGenres;
    LinearLayout openArtists;
    TextView recentAll;
    TextView mpall;


    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recents = view.findViewById(R.id.noRecentSongs);
        mostPlayed = view.findViewById(R.id.noMostPlayedSongs);
        openAllSongs = view.findViewById(R.id.openAllSongs);
        openAlbums = view.findViewById(R.id.openAlbums);
        openPlaylists = view.findViewById(R.id.openPlaylists);
        openGenres = view.findViewById(R.id.openGenres);
        openArtists = view.findViewById(R.id.openArtists);
        recentAll = view.findViewById(R.id.playAllRecents);
        mpall = view.findViewById(R.id.playAllMP);

        ViewPager mViewPager = getActivity().findViewById(R.id.container);

        openAllSongs.setOnClickListener(view14 -> mViewPager.setCurrentItem(1, true));

        openGenres.setOnClickListener(view13 -> mViewPager.setCurrentItem(3, true));

        openPlaylists.setOnClickListener(view12 -> mViewPager.setCurrentItem(2, true));

        openAlbums.setOnClickListener(view1 -> mViewPager.setCurrentItem(4, true));

        openArtists.setOnClickListener(view15 -> mViewPager.setCurrentItem(5, true));

        List<Long> recentSongs = RecentUtils.getRecentSongs(getActivity());
        List<Song> mostPlayedSongs = RecentUtils.getMostPlayedSongs(getActivity());

        if (recentSongs == null || recentSongs.isEmpty())
            recents.setVisibility(View.VISIBLE);
        else recents.setVisibility(View.GONE);

        if (mostPlayedSongs.isEmpty()) {
            mostPlayed.setVisibility(View.VISIBLE);
        } else mostPlayed.setVisibility(View.GONE);

        RecyclerView recentRecycler = view.findViewById(R.id.recyclerRecent);
        recentRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recentRecycler.setItemViewCacheSize(20);
        recentRecycler.setDrawingCacheEnabled(true);
        recentRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recentRecycler.setNestedScrollingEnabled(false);
        recentSongsAdapter = new RecentSongsAdapter(recentSongs);
        recentRecycler.setAdapter(recentSongsAdapter);

        RecyclerView mostPlayedRecycler = view.findViewById(R.id.recyclerMostPlayed);
        mostPlayedRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mostPlayedRecycler.setHasFixedSize(true);
        mostPlayedRecycler.setItemViewCacheSize(20);
        mostPlayedRecycler.setDrawingCacheEnabled(true);
        mostPlayedRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mostPlayedRecycler.setNestedScrollingEnabled(false);
        mostPlayedSongsAdapter = new MostPlayedSongsAdapter(getActivity(), mostPlayedSongs);
        mostPlayedRecycler.setAdapter(mostPlayedSongsAdapter);

        recentAll.setOnClickListener(view16 -> {
            Main.musicList.clear();
            List<Long> temmp = RecentUtils.getRecentSongs(getActivity());
            if (temmp == null || temmp.isEmpty()) {
                Toast.makeText(getActivity(), "No Songs to play", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < temmp.size(); i++) {
                Main.musicList.add(SongUtils.getSongById(temmp.get(i)));
            }
            Main.nowPlayingList = Main.musicList;
            Main.musicService.setList(Main.nowPlayingList);
            Intent intent = new Intent(getActivity(), PlayingNowList.class);
            intent.putExtra("playlistname", "Recent Songs");
            getActivity().startActivity(intent);
        });
        mpall.setOnClickListener(view17 -> {
            Main.musicList.clear();
            Main.musicList = RecentUtils.getMostPlayedSongs(getActivity());
            if ( Main.musicList.isEmpty()) {
                Toast.makeText(getActivity(), "No Songs to play", Toast.LENGTH_SHORT).show();
                return;
            }
            Main.nowPlayingList = Main.musicList;
            Main.musicService.setList(Main.nowPlayingList);
            Intent intent = new Intent(getActivity(), PlayingNowList.class);
            intent.putExtra("playlistname", "Most played");
            getActivity().startActivity(intent);
        });

        return view;
    }

}

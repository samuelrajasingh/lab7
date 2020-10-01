package com.urk17cs290.mediaplayer.music.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.urk17cs290.mediaplayer.music.adapters.PlaylistRecyclerViewAdapter;
import com.urk17cs290.mediaplayer.music.musicutils.PlaylistUtils;
import com.urk17cs290.mediaplayer.music.musicutils.RVUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.playerMain.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FragmentPlaylist extends Fragment {

    PlaylistRecyclerViewAdapter playlistRecyclerViewAdapter;
    LinearLayout noData;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentPlaylist() {
        //FragmentPlaylist
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        noData = view.findViewById(R.id.noData);
        refreshLayout = view.findViewById(R.id.refreshPlaylists);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        playlistRecyclerViewAdapter = new PlaylistRecyclerViewAdapter(getPlaylists());
        recyclerView.setAdapter(playlistRecyclerViewAdapter);
        RVUtils.makenoDataVisible(recyclerView, noData);
        ArrayList<String> filtered = new ArrayList<>(getPlaylists());
        EditText search = view.findViewById(R.id.searchPlaylist);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //beforeTextChanged
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filtered.clear();
                charSequence = charSequence.toString().toLowerCase();
                if (charSequence.length() == 0) {
                    filtered.addAll(getPlaylists());
                } else
                    for (int j = 0; j < getPlaylists().size(); j++) {
                        String playlist = getPlaylists().get(j);
                        if (playlist.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(getPlaylists().get(j));
                        }
                    }
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                Collections.sort(filtered, String::compareToIgnoreCase);
                playlistRecyclerViewAdapter.updateData(filtered);
                playlistRecyclerViewAdapter.notifyDataSetChanged();
                RVUtils.makenoDataVisible(recyclerView, noData);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //afterTextChanged

            }
        });

        refreshLayout.setOnRefreshListener(() -> refreshPlaylists());

        return view;
    }

    private void refreshPlaylists() {
        Main.data.updatePlaylists(getActivity(), "external");
        playlistRecyclerViewAdapter.updateData(getPlaylists());
        playlistRecyclerViewAdapter.notifyDataSetChanged();
        RVUtils.makenoDataVisible(recyclerView, noData);
        refreshLayout.setRefreshing(false);
    }

    private List<String> getPlaylists() {
        return PlaylistUtils.getPlaylistNames();
    }

}

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

import com.urk17cs290.mediaplayer.music.adapters.ArtistRecyclerViewAdapter;
import com.urk17cs290.mediaplayer.music.musicutils.DataUtils;
import com.urk17cs290.mediaplayer.music.musicutils.RVUtils;
import com.urk17cs290.mediaplayer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentArtist extends Fragment {

    ArtistRecyclerViewAdapter artistRecyclerViewAdapter;
    EditText search;
    ArrayList<String> artists = DataUtils.getArtists();
    List<String> filtered = new ArrayList<>();
    LinearLayout noData;
    SwipeRefreshLayout swipeRefreshLayout;



    public FragmentArtist() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        noData = view.findViewById(R.id.noData);
        swipeRefreshLayout = view.findViewById(R.id.refreshArtists);

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2)); //can change to create grid layout
        Collections.sort(artists);
        artistRecyclerViewAdapter = new ArtistRecyclerViewAdapter(artists);
        recyclerView.setAdapter(artistRecyclerViewAdapter);
        RVUtils.makenoDataVisible(recyclerView, noData);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            artistRecyclerViewAdapter.updateData(artists);
            artistRecyclerViewAdapter.notifyDataSetChanged();
            RVUtils.makenoDataVisible(recyclerView, noData);
            swipeRefreshLayout.setRefreshing(false);
        });
        search = view.findViewById(R.id.searchArtist);

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
                    filtered.addAll(artists);
                } else
                    for (int j = 0; j < artists.size(); j++) {
                        String genre = artists.get(j);
                        if (genre.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(artists.get(j));
                        }
                    }
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                Collections.sort(filtered, String::compareToIgnoreCase);
                artistRecyclerViewAdapter.updateData(filtered);
                artistRecyclerViewAdapter.notifyDataSetChanged();
                RVUtils.makenoDataVisible(recyclerView, noData);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //afterTextChanged

            }
        });

        return view;
    }

}

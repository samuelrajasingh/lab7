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

import com.urk17cs290.mediaplayer.music.adapters.AlbumRecyclerViewAdapter;
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

public class FragmentAlbum extends Fragment {

    AlbumRecyclerViewAdapter albumRecyclerViewAdapter;
    EditText search;
    List<String> filtered = new ArrayList<>();
    LinearLayout noData;
    SwipeRefreshLayout swipeRefreshLayout;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentAlbum() {
        //FragmentAlbum
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        noData = view.findViewById(R.id.noData);
        swipeRefreshLayout = view.findViewById(R.id.refreshAlbums);

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2)); //can change to create grid layout
        List<String> albums = DataUtils.getAlbums();
        albumRecyclerViewAdapter = new AlbumRecyclerViewAdapter(albums);
        recyclerView.setAdapter(albumRecyclerViewAdapter);
        RVUtils.makenoDataVisible(recyclerView, noData);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            albumRecyclerViewAdapter.updateData(albums);
            albumRecyclerViewAdapter.notifyDataSetChanged();
            RVUtils.makenoDataVisible(recyclerView, noData);
            swipeRefreshLayout.setRefreshing(false);
        });

        search = view.findViewById(R.id.searchAlbum);

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
                    filtered.addAll(albums);
                } else
                    for (int j = 0; j < albums.size(); j++) {
                        String genre = albums.get(j);
                        if (genre.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filtered.add(albums.get(j));
                        }
                    }
                recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
                Collections.sort(filtered, String::compareToIgnoreCase);
                albumRecyclerViewAdapter.updateData(filtered);
                albumRecyclerViewAdapter.notifyDataSetChanged();
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

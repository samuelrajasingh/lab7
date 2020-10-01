package com.urk17cs290.mediaplayer.music.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.urk17cs290.mediaplayer.music.activities.PlayingNowList;
import com.urk17cs290.mediaplayer.music.musicutils.ExtraUtils;
import com.urk17cs290.mediaplayer.music.musicutils.SongUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.urk17cs290.mediaplayer.music.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {
    List<String> mValues;

    public ArtistRecyclerViewAdapter(List<String> names) {
        mValues = names;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_artist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String selectedArtist = mValues.get(position);
        holder.artistname.setText(selectedArtist);
        List<Song> songsList = SongUtils.getSongsByArtist(selectedArtist);
        Picasso.get().load(ExtraUtils.getUrifromAlbumID(songsList.get(0))).fit().centerCrop().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher_foreground).into(holder.albumart);
        holder.mView.setOnClickListener(v -> {
            List<Song> songsList1 = SongUtils.getSongsByArtist(selectedArtist);
            Context context = holder.mView.getContext();
            Main.musicList.clear();
            Main.musicList.addAll(songsList1);
            Main.nowPlayingList = Main.musicList;
            Main.musicService.setList(Main.nowPlayingList);
            Intent intent = new Intent(context, PlayingNowList.class);
            intent.putExtra("playlistname", "Songs by " + selectedArtist);
            context.startActivity(intent);
        });
    }

    public void updateData(List<String> items) {
        this.mValues = items;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView artistname;
        public final ImageView albumart;

        ViewHolder(View view) {
            super(view);
            mView = view;
            artistname = view.findViewById(R.id.ArtistName);
            albumart = view.findViewById(R.id.albumArtArtist);
        }

    }
}

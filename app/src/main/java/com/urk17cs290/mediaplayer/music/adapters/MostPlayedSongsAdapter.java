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
import com.urk17cs290.mediaplayer.music.musicutils.RecentUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.urk17cs290.mediaplayer.music.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MostPlayedSongsAdapter extends RecyclerView.Adapter<MostPlayedSongsAdapter.ViewHolder> {

    private List<Song> songs;
    private Context context;

    public MostPlayedSongsAdapter(Context context, List<Song> songs) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.most_played_songs_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (getItemCount() != 0) {
            holder.songName.setText(songs.get(position).getTitle());
            holder.songBy.setText(songs.get(position).getArtist());
            Picasso.get().load(ExtraUtils.getUrifromAlbumID(songs.get(position))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(holder.imageView);
            holder.timesPlayed.setText(String.valueOf(RecentUtils.getcountSongsPlayed(context, songs.get(position))));
        }

        holder.mView.setOnClickListener(v -> {
            Context context = holder.mView.getContext();
            Main.musicList.clear();
            Main.musicList.add(songs.get(position));
            Main.nowPlayingList = Main.musicList;
            Main.musicService.setList(Main.nowPlayingList);
            Intent intent = new Intent(context, PlayingNowList.class);
            intent.putExtra("playlistname", "Single Song");
            context.startActivity(intent);

        });


    }


    @Override
    public int getItemCount() {
        if (songs != null)
            return songs.size();
        else return 0;
    }

    public void updateData(List<Song> mostPlayedSongs) {
        this.songs = mostPlayedSongs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView songName;
        public final TextView timesPlayed;
        public final TextView songBy;
        final ImageView imageView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.mostPlayedSongName);
            songBy = view.findViewById(R.id.mostPlayedArtistName);
            imageView = view.findViewById(R.id.mostplayedAlbumArt);
            timesPlayed = view.findViewById(R.id.mostPlayedCount);
        }

    }
}

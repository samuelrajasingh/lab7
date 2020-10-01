package com.urk17cs290.mediaplayer.music.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.urk17cs290.mediaplayer.music.activities.PlayingNowList;
import com.urk17cs290.mediaplayer.music.musicutils.ExtraUtils;
import com.urk17cs290.mediaplayer.music.musicutils.PlaylistUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.urk17cs290.mediaplayer.music.visualizers.barVisuals;
import com.urk17cs290.mediaplayer.music.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPlaylistAdapter extends RecyclerView.Adapter<ViewPlaylistAdapter.ViewHolder> {

    String name;
    private List<Song> mValues;

    public ViewPlaylistAdapter(List<Song> items, String playlistname) {
        mValues = items;
        name = playlistname;
    }

    @NonNull
    @Override
    public ViewPlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewPlaylistAdapter.ViewHolder holder, int position) {
        final Song localItem = mValues.get(position);
        holder.songName.setText(localItem.getTitle());
        holder.songBy.setText(localItem.getArtist());
        holder.songName.setSelected(true);
        Picasso.get().load(ExtraUtils.getUrifromAlbumID(localItem)).centerCrop().fit().error(R.mipmap.ic_launcher).into(holder.circleImageView);

        if (Main.mainMenuHasNowPlayingItem) {
            if (Main.musicService.currentSong.getTitle().equals(localItem.getTitle())) {
                holder.barVisuals.setVisibility(View.VISIBLE);
                holder.barVisuals.setColor(ContextCompat.getColor(holder.barVisuals.getContext(), R.color.accent));
                holder.barVisuals.setDensity(1);
                holder.barVisuals.setPlayer(Main.musicService.getAudioSession());
            } else {
                holder.barVisuals.setVisibility(View.GONE);
            }
        } else {
            holder.barVisuals.setVisibility(View.GONE);
        }
        holder.mView.setOnClickListener(v -> {
            Main.musicList.clear();
            Main.musicList.add(localItem);
            Main.nowPlayingList = Main.musicList;
            Main.musicService.setList(Main.nowPlayingList);
            Intent intent = new Intent(v.getContext(), PlayingNowList.class);
            intent.putExtra("playlistname", "Single Song");
            v.getContext().startActivity(intent);
        });
        holder.songOptions.setImageDrawable(ExtraUtils.getThemedIcon(holder.mView.getContext(), holder.mView.getContext().getDrawable(R.drawable.ic_3dots)));
        holder.songOptions.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(view.getContext(), holder.songOptions);
            popup.inflate(R.menu.view_playlist_options);
            popup.setOnMenuItemClickListener(item -> {

                switch (item.getItemId()) {
                    case R.id.one:
                        PlaylistUtils.deletePlaylistTrack(view.getContext(), name, localItem.getId());
                        notifyItemRemoved(holder.getAbsoluteAdapterPosition());
                        return true;
                    case R.id.two:
                        ExtraUtils.shareSong(view.getContext(), localItem);
                        return true;
                    case R.id.three:
                        ExtraUtils.showSongDetails(view.getContext(), localItem.getId());
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void updateData(List<Song> songs) {
        mValues = songs;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView songName;
        public final TextView songBy;
        final View mView;
        final CircleImageView circleImageView;
        ImageView songOptions;
        barVisuals barVisuals;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.songName);
            songBy = view.findViewById(R.id.songBy);
            circleImageView = view.findViewById(R.id.albumArt);
            barVisuals = view.findViewById(R.id.barvisuals);
            songOptions = view.findViewById(R.id.songOptions);
        }
    }
}
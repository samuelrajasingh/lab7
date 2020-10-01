package com.urk17cs290.mediaplayer.music.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.urk17cs290.mediaplayer.music.activities.PlayingNowList;
import com.urk17cs290.mediaplayer.music.musicutils.ExtraUtils;
import com.urk17cs290.mediaplayer.music.musicutils.PlaylistUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.urk17cs290.mediaplayer.music.fragments.ViewPlaylist;
import com.urk17cs290.mediaplayer.music.playerMain.Main;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaylistRecyclerViewAdapter extends RecyclerView.Adapter<PlaylistRecyclerViewAdapter.ViewHolder> {

    private List<String> playlists;

    public PlaylistRecyclerViewAdapter(List<String> items) {
        playlists = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.playlistname.setText(playlists.get(position));
        final String selectedPlaylist = playlists.get(position);
        List<Song> songsList = PlaylistUtils.getSongsByPlaylist(selectedPlaylist);

        Picasso.get().load(ExtraUtils.getUrifromAlbumID(songsList.get(0))).fit().centerCrop().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher_foreground).into(holder.albumart);

        holder.mView.setOnClickListener(v -> {
            List<Song> songsList1 = PlaylistUtils.getSongsByPlaylist(selectedPlaylist);
            Context context = holder.mView.getContext();
            Main.musicList.clear();
            Main.musicList.addAll(songsList1);
            Main.nowPlayingList = Main.musicList;
            Main.musicService.setList(Main.nowPlayingList);
            Intent intent = new Intent(context, PlayingNowList.class);
            intent.putExtra("playlistname", selectedPlaylist);
            context.startActivity(intent);
        });

        holder.more.setOnClickListener(view -> openPopUPMenu(holder, view, selectedPlaylist));

    }

    private void openPopUPMenu(ViewHolder holder, View view, String playlist) {

        PopupMenu popup = new PopupMenu(view.getContext(), holder.more);
        popup.inflate(R.menu.playlist_options);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.one:
                    deletePlaylist(holder, view, playlist);
                    return true;
                case R.id.two:
                    editName(view, playlist);
                    return true;
                case R.id.three:
                    showPlaylist(view, playlist);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void showPlaylist(View view, String selectedPlaylist) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, ViewPlaylist.newInstance(selectedPlaylist)).addToBackStack(null).commit();
    }


    private void editName(View view, String selectedPlaylist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Change Name");

        final EditText input = new EditText(view.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String mText = input.getText().toString();
            if (!mText.equals("") && !playlists.contains(mText)) {
                PlaylistUtils.renamePlaylist(view.getContext(), mText, Long.parseLong(PlaylistUtils.getPlayListId(selectedPlaylist)));
                Toast.makeText(view.getContext(), "Done! Changes might take time.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(view.getContext(), "Cant be Empty, OR already exist", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deletePlaylist(ViewHolder holder, View view, String selectedPlaylist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Sure want to delete?");
        builder.setPositiveButton("OK", (dialog, which) -> {
            PlaylistUtils.deletePlaylist(view.getContext(), selectedPlaylist);
            int newPosition = holder.getAbsoluteAdapterPosition();
            playlists.remove(newPosition);
            notifyItemRemoved(newPosition);
            notifyItemRangeChanged(newPosition, playlists.size());
            Toast.makeText(view.getContext(), "Done!", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void updateData(List<String> items) {
        this.playlists = items;
    }

    @Override
    public int getItemCount() {
        if ((playlists != null) && (!playlists.isEmpty()))
            return playlists.size();

        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        recyclerView.setNestedScrollingEnabled(false);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView playlistname;
        ImageView albumart;
        ImageButton more;
        LinearLayout expandable;
        RecyclerView recyclerView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            playlistname = view.findViewById(R.id.PlaylistName);
            albumart = view.findViewById(R.id.albumArt);
            more = view.findViewById(R.id.more);
            expandable = view.findViewById(R.id.playlistExpanded);
            recyclerView = view.findViewById(R.id.songPlaylistShow);
        }

    }
}

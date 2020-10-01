package com.urk17cs290.mediaplayer.music.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.urk17cs290.mediaplayer.music.activities.PlayingNowList;
import com.urk17cs290.mediaplayer.music.musicutils.ExtraUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.urk17cs290.mediaplayer.music.playerMain.Main;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SongsRecyclerViewAdapter extends RecyclerView.Adapter<SongsRecyclerViewAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private List<Song> songs;
    private List<Song> selected = new ArrayList<>();
    private OnClickAction receiver;

    @NonNull
    @Override
    public String getSectionName(int position) {
        return String.valueOf(songs.get(position).getTitle().charAt(0));
    }

    public interface OnClickAction {
        void onClickAction();
    }

    public SongsRecyclerViewAdapter(List<Song> items) {
        songs = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_songs_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Song localItem = songs.get(position);
        holder.songName.setText(localItem.getTitle());
        holder.songBy.setText(localItem.getArtist());
        holder.songName.setSelected(true);
        Picasso.get().load(ExtraUtils.getUrifromAlbumID(localItem)).centerCrop().fit().error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher_foreground).into(holder.circleImageView);
        Bitmap bitmap = ExtraUtils.getBitmapfromAlbumId(holder.mView.getContext(), localItem);

        holder.mView.setOnLongClickListener(view -> {
            if (selected.contains(localItem)) {
                selected.remove(localItem);
                unhighlightView(holder, bitmap);
            }else {
                selected.add(localItem);
                highlightView(holder);
            }
            try {
                receiver.onClickAction();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        });
        if (selected.contains(localItem))
            highlightView(holder);
        else
            unhighlightView(holder, bitmap);

        holder.mView.setOnClickListener(v -> {
            if (selected.isEmpty()){
                Context context = holder.mView.getContext();
                Main.musicList.clear();
                Main.musicList.add(localItem);
                Main.nowPlayingList = Main.musicList;
                Main.musicService.setList(Main.nowPlayingList);
                Intent intent = new Intent(context, PlayingNowList.class);
                intent.putExtra("playlistname", "Single Song");
                context.startActivity(intent);
            }else {
                if (selected.contains(localItem)) {
                    selected.remove(localItem);
                    unhighlightView(holder, bitmap);
                }else {
                    selected.add(localItem);
                    highlightView(holder);
                }
                try {
                    receiver.onClickAction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            });

        if (selected.contains(localItem))
            highlightView(holder);
        else
            unhighlightView(holder, bitmap);
    }

    private void highlightView(ViewHolder holder) {
        holder.mView.setBackgroundColor(Color.parseColor("#C0C0C0"));
        holder.circleImageView.setImageResource(R.drawable.ic_check);
    }

    private void unhighlightView(ViewHolder holder, Bitmap draw) {
        holder.mView.setBackgroundColor(Color.TRANSPARENT);
        if (draw != null)
            holder.circleImageView.setImageBitmap(draw);
        else holder.circleImageView.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if ((songs != null) && (!songs.isEmpty()))
            return songs.size();

        return 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView songName;
        public final TextView songBy;
        final CircleImageView circleImageView;

        ViewHolder(View view) {
            super(view);
            mView = view;
            songName = view.findViewById(R.id.songName);
            songBy = view.findViewById(R.id.songBy);
            circleImageView = view.findViewById(R.id.albumArt);
        }

    }

    public void addAll(List<Song> items) {
        clearAll(false);
        this.selected = items;
        notifyDataSetChanged();
    }

    public void clearAll(boolean isNotify) {
       // items.clear();
        selected.clear();
        if (isNotify) notifyDataSetChanged();
    }

    public void clearSelected() {
        selected.clear();
        notifyDataSetChanged();
    }

    public void selectAll() {
        selected.clear();
        selected.addAll(songs);
        notifyDataSetChanged();
    }
    public void setActionModeReceiver(OnClickAction receiver) {
        this.receiver = receiver;
    }

    public void updateData(List<Song> list) {
        this.songs = list;
    }
    public List<Song> getSelected() {
        return selected;
    }
}

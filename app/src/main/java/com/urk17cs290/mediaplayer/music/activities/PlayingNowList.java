package com.urk17cs290.mediaplayer.music.activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.urk17cs290.mediaplayer.music.adapters.CurrentPlayListSongsAdapter;
import com.urk17cs290.mediaplayer.music.musicutils.PlaylistUtils;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.playerMain.Main;

import java.util.List;
import java.util.Objects;

public class PlayingNowList extends BaseActivity implements MediaController.MediaPlayerControl {

    private TextView mTitleView;
    private TextView mCounterView;
    private LinearLayout mTitleViewq;
    private FloatingActionButton mFabView;
    private TextView mTimeView;
    private TextView mDurationView;
    private SeekBar seekArc;
    private RecyclerView songListView;

    private ImageView mCoverView;

    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            //PlaybackState is Changed
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
            }
        }
    };

    private void onUpdateProgress(int position, int duration) {
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatElapsedTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatElapsedTime(duration));
        }
        if (seekArc != null) {
            seekArc.setProgress(getCurrentPosition() / 1000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(PlayingNowList.this);
        if (controllerCompat != null) {
            controllerCompat.unregisterCallback(mCallback);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.playerview_list);

        songListView = findViewById(R.id.tracks_nowplaying);

        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.titleTrack);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        seekArc = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);
        mTitleViewq = findViewById(R.id.title);
        mCounterView = findViewById(R.id.counter);
        TextView mPlaylistName = findViewById(R.id.name);

        songListView.setLayoutManager(new LinearLayoutManager(this));
        songListView.setAdapter(new CurrentPlayListSongsAdapter(Main.nowPlayingList));

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("playlistname")) {
            mPlaylistName.setText(Objects.requireNonNull(getIntent().getExtras()).getString("playlistname", "Current Playlist"));
            if (Main.musicService.currentSong != Main.musicList.get(0))
                Main.musicService.playSong();
        }

        MainScreen.addNowPlayingItem();
    }


    public void initTransistion(View view) {
        //noinspection unchecked
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                new android.util.Pair<>(mCoverView, "cover"),
                new android.util.Pair<>(mTitleViewq, "title"),
                new android.util.Pair<>(mTimeView, "time"),
                new android.util.Pair<>(mDurationView, "duration"),
                new android.util.Pair<>(seekArc, "progress"),
                new android.util.Pair<>(mFabView, "fab"));
        startActivity(new Intent(this, PlayerView.class), options.toBundle());
    }

    private void prepareSeekBar() {

        seekArc.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //onStartTrackingTouch

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //onStopTrackingTouch
            }
        });

        final Handler handler = new Handler();
        PlayingNowList.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!Main.mainMenuHasNowPlayingItem)
                    finish();
                if (isPlaying()) {
                    int position = getCurrentPosition() / 1000;
                    int duration = (int) Main.musicService.currentSong.getDurationSeconds();
                    onUpdateProgress(position, duration);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void playlistOptions(View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.current_playlist_options);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.one) {
                showPlaylistDialog();
            }
            return false;
        });
        popup.show();
    }

    /**
     * Activity has become visible.
     */

    @Override
    protected void onResume() {
        super.onResume();

        prepareSeekBar();

        if (Main.mainMenuHasNowPlayingItem) {
            try {
                connectToSession(Main.musicService.getSessionToken());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateMediaDescription(MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }
        //mLine1.setText(description.getTitle());
        songListView.setAdapter(new CurrentPlayListSongsAdapter(Main.nowPlayingList));
        songListView.scrollToPosition(Main.musicService.currentSongPosition);
        mTitleView.setText(description.getTitle());
        mTitleView.setSelected(true);
        mCounterView.setText(String.format("%s Songs", Main.nowPlayingList.size()));
        mCoverView.setImageBitmap(description.getIconBitmap());
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        Log.e("lol", String.valueOf(duration));
        seekArc.setMax(duration);
    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController == null) {
            mediaController = new MediaControllerCompat(PlayingNowList.this, token);
        }
        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }

        MediaControllerCompat.setMediaController(PlayingNowList.this, mediaController);
        mediaController.registerCallback(mCallback);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
    }

    /**
     * Activity is no longer visible.
     */

    @Override
    public void start() {
        Main.musicService.unpausePlayer();
    }

    /**
     * Callback to when the user pressed the `pause` button.
     */
    @Override
    public void pause() {
        Main.musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (Main.musicService != null && Main.musicService.musicBound
                && Main.musicService.isPlaying())
            return Main.musicService.getDuration();
        else
            return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (Main.musicService != null && Main.musicService.musicBound
                && Main.musicService.isPlaying())
            return Main.musicService.getPosition();
        else
            return 0;
    }

    @Override
    public void seekTo(int position) {
        Main.musicService.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        if (Main.musicService != null && Main.musicService.musicBound)
            return Main.musicService.isPlaying();

        return false;
    }

    @Override
    public int getBufferPercentage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return Main.musicService.getAudioSession();
    }


    private void showPlaylistDialog() {
        final ListView listView;
        Button create;
        List<String> allPlaylists = PlaylistUtils.getPlaylistNames();
        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.newplaylistdialog, null);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_newplaylistdialog, allPlaylists);
        listView = view.findViewById(R.id.playlistListview);
        listView.setAdapter(arrayAdapter);
        EditText name = view.findViewById(R.id.newPlaylistName);
        listView.setOnItemClickListener((adapterView, view12, i, l) -> {
            String namenew = listView.getItemAtPosition(i).toString();
            name.setText(namenew);
        });
        create = view.findViewById(R.id.createPlaylist);
        create.setOnClickListener(view13 -> {
            if (name.getText().toString().isEmpty()) {
                name.setError("cant be empty");
                return;
            }
            Main.showProgressDialog(PlayingNowList.this);
            PlaylistUtils.newPlaylist(PlayingNowList.this, "external", name.getText().toString(), Main.nowPlayingList);
            Toast.makeText(PlayingNowList.this, "Done", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            Main.hideProgressDialog();
        });
        Button cancel = view.findViewById(R.id.cancelPlaylist);
        cancel.setOnClickListener(view1 -> dialog.dismiss());
        dialog.setContentView(view);
        dialog.show();
    }


}

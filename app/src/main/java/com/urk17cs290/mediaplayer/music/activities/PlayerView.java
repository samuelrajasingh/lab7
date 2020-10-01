package com.urk17cs290.mediaplayer.music.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.format.DateUtils;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andremion.music.MusicCoverView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.urk17cs290.mediaplayer.music.musicutils.ExtraUtils;
import com.urk17cs290.mediaplayer.music.customviews.TransitionAdapter;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.visualizers.CircleBarVisualizer;
import com.urk17cs290.mediaplayer.music.equalizer.EqualizerFragment;
import com.urk17cs290.mediaplayer.music.playerMain.Main;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class PlayerView extends BaseActivity implements MediaController.MediaPlayerControl {

    ImageView next;
    ImageView previous;
    ImageView rewind;
    ImageView forward;
    ImageView shuffle;
    ImageView repeat;
    ImageView eq;
    private MusicCoverView mCoverView;
    private FloatingActionButton mFabView;
    private TextView mTimeView;
    private TextView mDurationView;
    private CircularSeekBar mProgressView;
    private CircleBarVisualizer circleBarVisualizer;
    private TextView mTitleView;


    private final MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            updatePlaybackState(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                updateMediaDescription(metadata.getDescription());
                updateDuration(metadata);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Main.settings.load(this);
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.playerview);
        RelativeLayout wq = findViewById(R.id.fdsg);
        wq.bringToFront();
        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.titleTrack);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);
        eq = findViewById(R.id.equaButton);
        circleBarVisualizer = findViewById(R.id.visualizer);
        circleBarVisualizer.setColor(ExtraUtils.getThemeAttrColor(this, R.styleable.Theme_primaryColor));

        mCoverView.setCallbacks(new MusicCoverView.Callbacks() {
            @Override
            public void onMorphEnd(MusicCoverView coverView) {
                // Nothing to do
            }

            @Override
            public void onRotateEnd(MusicCoverView coverView) {
                supportFinishAfterTransition();
            }
        });

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                mCoverView.start();
            }
        });
        setclickListeners();
        prepareSeekBar();
    }

    private void setclickListeners() {

        eq.setImageDrawable(ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_equalizer)));
        next = findViewById(R.id.next);
        next.setImageDrawable(ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_skip)));
        previous = findViewById(R.id.previous);
        previous.setImageDrawable(ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_previous)));
        forward = findViewById(R.id.forward);
        forward.setImageDrawable(ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_forward)));
        rewind = findViewById(R.id.rewind);
        rewind.setImageDrawable(ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_rewind)));
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);

        if (!Main.musicService.isShuffle()) {
            shuffle.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_shuffle_off))));
        } else {
            shuffle.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_shuffle_on))));
        }
        if (Main.musicService.isRepeat() == 0) {
            repeat.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_one))));
        } else if (Main.musicService.isRepeat() == 1) {
            repeat.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_on))));
        } else
            repeat.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_repeat_off))));

        next.setOnClickListener(view -> playNext());

        previous.setOnClickListener(view -> playPrevious());

        forward.setOnClickListener(view -> seekTo(getCurrentPosition() + (Main.settings.get("jumpValue", 10) * 1000)));

        rewind.setOnClickListener(view -> seekTo(getCurrentPosition() - (Main.settings.get("jumpValue", 10) * 1000)));

        shuffle.setOnClickListener(view -> {
            Main.musicService.toggleShuffle();
            if (!Main.musicService.isShuffle()) {
                shuffle.setImageDrawable((ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_shuffle_off))));
            } else {
                shuffle.setImageDrawable((ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_shuffle_on))));
            }
        });

        repeat.setOnClickListener(view -> {
            Main.musicService.toggleRepeat();
            if (Main.musicService.isRepeat() == 0) {
                repeat.setImageDrawable((ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_one))));
            } else if (Main.musicService.isRepeat() == 1) {
                repeat.setImageDrawable((ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_on))));
            } else
                repeat.setImageDrawable((ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_repeat_off))));
        });
    }


    public void onFabClick(View view) {
        Main.musicService.togglePlayback();
        if (!Main.musicService.isPaused()) {
            mFabView.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_pause))));
        } else {
            mFabView.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_play))));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Activity has become visible.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            connectToSession(Main.musicService.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity is no longer visible.
     */

    @Override
    public void start() {
        Main.musicService.unpausePlayer();
        mFabView.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_pause))));
    }

    /**
     * Callback to when the user pressed the `pause` button.
     */
    @Override
    public void pause() {
        Main.musicService.pausePlayer();
        mFabView.setImageDrawable((ExtraUtils.getThemedIcon(this, ContextCompat.getDrawable(this, R.drawable.ic_play))));
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

    // Back to the normal methods

    /**
     * Jumps to the next song and starts playing it right now.
     */
    public void playNext() {
        Main.musicService.next();
        Main.musicService.playSong();

    }

    /**
     * Jumps to the previous song and starts playing it right now.
     */
    public void playPrevious() {
        Main.musicService.previous();
        Main.musicService.playSong();
    }

    @Override
    protected void onStop() {
        super.onStop();

        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(PlayerView.this);
        if (controllerCompat != null) {
            controllerCompat.unregisterCallback(mCallback);
        }
    }

    private void prepareSeekBar() {

        mProgressView.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if (fromUser) {
                    seekTo((int) progress * 1000);
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                //onStopTrackingTouch

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                //onStartTrackingTouch

            }
        });

        Handler handler = new Handler();
        PlayerView.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!Main.mainMenuHasNowPlayingItem)
                    finish();
                if (isPlaying()) {
                    int position = getCurrentPosition() / 1000;
                    int duration = (int) Main.musicService.currentSong.getDurationSeconds();
                    onUpdateProgress(position, duration);
                }
                handler.postDelayed(this, 100);
            }
        });

    }

    private void onUpdateProgress(int position, int duration) {
        if (mTimeView != null) {
            mTimeView.setText(DateUtils.formatElapsedTime(position));
        }
        if (mDurationView != null) {
            mDurationView.setText(DateUtils.formatElapsedTime(duration));
        }
        if (mProgressView != null) {
            mProgressView.setProgress(position);
        }
    }

    private void updateMediaDescription(MediaDescriptionCompat description) {
        if (description == null) {
            return;
        }

        mTitleView.setText(description.getTitle());
        circleBarVisualizer.setPlayer(getAudioSessionId());
        mTitleView.setSelected(true);
        mCoverView.setImageBitmap(description.getIconBitmap());
    }

    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mProgressView.setMax(duration);
    }

    private void updatePlaybackState(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }

        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                mFabView.setImageDrawable((ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause))));
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                mFabView.setImageDrawable((ExtraUtils.getThemedIcon(getApplicationContext(), ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play))));
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                finish();
                break;
            default:
            case PlaybackStateCompat.STATE_BUFFERING:
            case PlaybackStateCompat.STATE_CONNECTING:
            case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
            case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
            case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
            case PlaybackStateCompat.STATE_REWINDING:
            case PlaybackStateCompat.STATE_FAST_FORWARDING:
            case PlaybackStateCompat.STATE_ERROR:
            case PlaybackStateCompat.STATE_NONE:
                break;
        }

    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);
        if (mediaController == null) {
            mediaController = new MediaControllerCompat(PlayerView.this, token);
        }
        if (mediaController.getMetadata() == null) {
            finish();
            return;
        }

        MediaControllerCompat.setMediaController(PlayerView.this, mediaController);
        mediaController.registerCallback(mCallback);
        PlaybackStateCompat state = mediaController.getPlaybackState();
        updatePlaybackState(state);
        MediaMetadataCompat metadata = mediaController.getMetadata();
        if (metadata != null) {
            updateMediaDescription(metadata.getDescription());
            updateDuration(metadata);
        }
    }


    public void equalizer(View view) {
        Main.musicService.player.setLooping(true);
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(ExtraUtils.getThemeAttrColor(PlayerView.this, R.styleable.Theme_primaryDarkColor))
                .setAudioSessionId(getAudioSessionId())
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, equalizerFragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        mCoverView.stop();
    }

}

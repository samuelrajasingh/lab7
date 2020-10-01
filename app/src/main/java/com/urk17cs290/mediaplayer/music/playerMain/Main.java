package com.urk17cs290.mediaplayer.music.playerMain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.songdata.Data;
import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.urk17cs290.mediaplayer.music.services.ServicePlayMusic;
import com.urk17cs290.mediaplayer.music.settings.Settings;

import java.util.ArrayList;
import java.util.List;
// Main Logic u can say. Every thing works around this class, again KMP <3

public class Main {

    /**
     * All the data on the device.
     */
    public static Data data = new Data();

    /**
     * Progress dialog for loading screen
     */
    public static ProgressDialog mProgressDialog;

    /**
     * All the app's configurations/preferences/settings.
     */
    public static  Settings settings = new Settings();

    /**
     * Our custom service that allows the music to play
     * even when the app is not on focus.
     */
    public static  ServicePlayMusic musicService = null;

    /**
     * Contains the data that are going to be shown to
     * the user on a particular menu.
     */
    public static  List<Song> musicList = new ArrayList<>();

    /**
     * List of the data being currently played by the user.
     */
    public static  List<Song> nowPlayingList = null;

    /**
     * Flag that tells if the Main Menu has an item that
     * sends the user to the Now Playing Activity.
     * <p>
     * It's here because when firstly initializing the
     * application, there's no Now Playing Activity.
     */
    public static boolean mainMenuHasNowPlayingItem = false;

    // GENERAL PROGRAM INFO
    public static String packageName = "<unknown>";
    public static String versionName = "<unknown>";
    public static int versionCode = -1;
    public static long firstInstalledTime = -1;
    public static long lastUpdatedTime = -1;
    /**
     * The actual connection to the MusicService.
     * We start it with an Intent.
     * <p>
     * These callbacks will bind the MusicService to our internal
     * variables.
     * We can only know it happened through our flag, `musicBound`.
     */
    public static final ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServicePlayMusic.MusicBinder binder = (ServicePlayMusic.MusicBinder) service;

            // Here's where we finally create the MusicService
            musicService = binder.getService();
            musicService.setList(Main.data.songs);
            musicService.musicBound = true;
            Log.w("service", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService.musicBound = false;
            Log.w("service", "onServiceDisconnected");
        }
    };
    /**
     * Our will to start a new music Service.
     * Android requires that we start a service through an Intent.
     */
    private static Intent musicServiceIntent = null;

    /**
     * Creates everything.
     * <p>
     * Must be called only once at the beginning
     * of the program.
     */
    public static void initialize(Context c) {

        Main.packageName = c.getPackageName();

        try {
            // Retrieving several information
            PackageInfo info = c.getPackageManager().getPackageInfo(Main.packageName, 0);

            Main.versionName = info.versionName;
            Main.versionCode = info.versionCode;
            Main.firstInstalledTime = info.firstInstallTime;
            Main.lastUpdatedTime = info.lastUpdateTime;

        } catch (PackageManager.NameNotFoundException e) {
            // Couldn't get package information
            //
            // Won't do anything, since variables are
            // already started with default values.
        }
        startMusicService(c);
    }

    /**
     * Destroys everything.
     * <p>
     * Must be called only once when the program
     * being destroyed.
     */
    public static void destroy() {
        data.destroy();
    }

    /**
     * Initializes the Music Service at Activity/Context c.
     *
     * @note Only starts the service once - does nothing when
     * called multiple times.
     */
    public static void startMusicService(Context c) {

        if (musicServiceIntent != null){
            c.bindService(musicServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
            return;
        }

        if (Main.musicService != null)
            return;

        // Create an intent to bind our Music Connection to
        // the MusicService.
        musicServiceIntent = new Intent(c, ServicePlayMusic.class);
        c.bindService(musicServiceIntent, musicConnection, Context.BIND_AUTO_CREATE);
        c.startService(musicServiceIntent);
        Log.w("service", "startMusicService");

    }

    /**
     * Makes the music Service stop and clean itself at
     * Activity/Context c.
     */
    public static void stopMusicService(Context c) {

        //c.stopService(musicServiceIntent);
        c.unbindService(musicConnection);
    }

    /**
     * Forces the whole application to quit.
     * <p>
     * Please read more info on this StackOverflow answer:
     * http://stackoverflow.com/a/4737595
     *
     * @note This is dangerous, make sure to cleanup
     * everything before calling this.
     */
    public static void forceExit(Activity c) {

        c.finish();
        c.finishAffinity();

    }

    public static void showProgressDialog(Context c) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(c, R.style.progressTheme);
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        mProgressDialog.show();
    }

    public static void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
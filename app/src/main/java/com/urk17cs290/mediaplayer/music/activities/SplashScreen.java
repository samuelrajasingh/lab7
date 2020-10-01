package com.urk17cs290.mediaplayer.music.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.urk17cs290.mediaplayer.music.handlers.PermissionHandler;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.playerMain.Main;

import java.lang.ref.WeakReference;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE;

public class SplashScreen extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE_AUDIO = 201;
    private static final int PERMISSION_REQUEST_CODE_STORAGE = 202;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences recent = getSharedPreferences("com.urk17cs290.mediaplayer.music.RecentSongs", 0);
        SharedPreferences count = getSharedPreferences("com.urk17cs290.mediaplayer.music.SongsPlayedCount", 0);
        SharedPreferences last = getSharedPreferences("com.urk17cs290.mediaplayer.music.LastPlaylist", 0);
        SharedPreferences defaults = PreferenceManager.getDefaultSharedPreferences(this);

        boolean firstRun = defaults.getBoolean("firstRun", true);

        if (firstRun) {
            defaults.edit().clear().apply();
            defaults.edit().putBoolean("firstRun", false).apply();
            recent.edit().clear().apply();
            count.edit().clear().apply();
            last.edit().clear().apply();
        }

        Main.initialize(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionHandler.isStoragePergiven(getApplicationContext())) {
                scanSongs();
            } else {
                PermissionHandler.requestBothPermssion(this, PERMISSION_REQUEST_CODE);
            }

        } else {
            scanSongs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                    if (PermissionHandler.isStoragePergiven(getApplicationContext())) {
                        if (PermissionHandler.isRecordingPergiven(getApplicationContext())) {
                            scanSongs();
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                boolean showRationale = shouldShowRequestPermissionRationale(RECORD_AUDIO);
                                if (showRationale) {
                                    PermissionHandler.requestRecording(SplashScreen.this, PERMISSION_REQUEST_CODE_AUDIO);
                                } else {
                                    scanSongs();
                                }
                            }
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            boolean showRationale = shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE);
                            if (showRationale) {
                                PermissionHandler.requestStorage(this, PERMISSION_REQUEST_CODE_STORAGE);
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "Storage Permission is required", LENGTH_INDEFINITE)
                                        .setAction("Settings", view -> {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                        })
                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light,this.getTheme()))
                                        .show();
                            }
                        }
                    }

                break;
            case PERMISSION_REQUEST_CODE_AUDIO:
                if (!PermissionHandler.isRecordingPergiven(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "Starting without visualizers", Toast.LENGTH_SHORT).show();
                }
                scanSongs();
                break;
            case PERMISSION_REQUEST_CODE_STORAGE:
                if (PermissionHandler.isStoragePergiven(getApplicationContext())) {
                    scanSongs();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        boolean showRationale = shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE);
                        if (showRationale) {
                            PermissionHandler.requestStorage(this, PERMISSION_REQUEST_CODE_STORAGE);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "Storage Permission is required", LENGTH_INDEFINITE)
                                    .setAction("Settings", view -> {
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                        }
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    void scanSongs() {
        new SplashScreen.ScanSongs(this).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Main.stopMusicService(this);
        //his.unbindService(Main.musicConnection);
    }

    static class ScanSongs extends AsyncTask<String, Integer, String> {

        private WeakReference<SplashScreen> activityReference;

        ScanSongs(SplashScreen context) {
            activityReference = new WeakReference<>(context);
        }

        /**
         * The action we'll do in the background.
         */
        @Override
        protected String doInBackground(String... params) {

            // get a reference to the activity if it is still there
            SplashScreen activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return "lol";

            try {
                Main.data.scanSongs(activity, "external");
                return activity.getString(R.string.menu_main_scanning_ok);
            } catch (Exception e) {
                Log.e("Couldn't execute", e.toString());
                e.printStackTrace();
                return activity.getString(R.string.menu_main_scanning_not_ok);
            }
        }

        /**
         * Called once the background processing is done.
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            SplashScreen activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (Intent.ACTION_VIEW.equals(activity.getIntent().getAction())) {
                Uri file = activity.getIntent().getData();
                Intent intent = new Intent(activity, SampleActivity.class);
                intent.putExtra("file", file.toString());
                activity.startActivity(intent);
                activity.finish();

            }  else {
                Intent intent = new Intent(activity, MainScreen.class);
                activity.startActivity(intent);
            }
        }
    }
}

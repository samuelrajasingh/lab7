package com.urk17cs290.mediaplayer.music.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.urk17cs290.mediaplayer.music.adapters.ThemeAdapter;
import com.urk17cs290.mediaplayer.music.musicutils.ExtraUtils;
import com.urk17cs290.mediaplayer.music.musicutils.ThemeUtil;
import com.urk17cs290.mediaplayer.music.handlers.DefaultSPHandler;
import com.urk17cs290.mediaplayer.R;
import com.urk17cs290.mediaplayer.music.fragments.AdvancedSettings;
import com.urk17cs290.mediaplayer.music.playerMain.Main;
import com.urk17cs290.mediaplayer.music.settings.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingActivity extends BaseActivity implements AdvancedSettings.OnFragmentInteractionListener {

    protected static  List<Theme> mThemeList = new ArrayList<>();

    public static int getSelectedTheme() {
        return selectedTheme;
    }

    public static void setSelectedTheme(int selectedTheme) {
        SettingActivity.selectedTheme = selectedTheme;
    }

    private static int selectedTheme = 0;
    LinearLayout mode;
    LinearLayout theme;
    LinearLayout blockfolder;
    MaterialCheckBox pauseHeadphoneUnplugged;
    MaterialCheckBox resumeHeadphonePlugged;
    MaterialCheckBox headphoneControl;
    MaterialCheckBox saveRecent;
    MaterialCheckBox savePlaylist;
    MaterialCheckBox saveCount;
    LinearLayout llBottomSheet;
    private ThemeAdapter mAdapter;
    private BottomSheetBehavior mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ExtraUtils.getThemedIcon(this, getDrawable(R.drawable.ic_backarrow)));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        mode = findViewById(R.id.settingsMode);
        theme = findViewById(R.id.settingsTheme);
        pauseHeadphoneUnplugged = findViewById(R.id.pauseHeadphoneUnplugged);
        resumeHeadphonePlugged = findViewById(R.id.resumeHeadphonePlugged);
        headphoneControl = findViewById(R.id.headphoneControl);
        saveRecent = findViewById(R.id.saveRecent);
        saveCount = findViewById(R.id.saveCount);
        savePlaylist = findViewById(R.id.savePlaylist);
        blockfolder = findViewById(R.id.blockFolder);
        llBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        TextView slds = findViewById(R.id.curVersion);
        slds.setText(Main.versionName);
        setupCheckBoxes();
        setListeners();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.resetSetting) {
            resetDialog();
            return true;
        }
        return false;

    }

    private void resetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Reset App Settings");
        builder.setMessage("It will reset In-App Settings. Also Recent Songs, Counts and Last Played Playlist will be Deleted!");
        builder.setPositiveButton("Save", (dialog, which) -> {
            Main.settings.reset();
            Toast.makeText(SettingActivity.this, "Reset Complete", Toast.LENGTH_SHORT).show();
            recreate();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setupCheckBoxes() {
        pauseHeadphoneUnplugged.setChecked(Main.settings.get("pauseHeadphoneUnplugged", true));
        resumeHeadphonePlugged.setChecked(Main.settings.get("resumeHeadphonePlugged", true));
        headphoneControl.setChecked(Main.settings.get("headphoneControl", true));
        saveRecent.setChecked(Main.settings.get("saveRecent", true));
        saveCount.setChecked(Main.settings.get("saveCount", true));
        savePlaylist.setChecked(Main.settings.get("savePlaylist", true));
    }

    private void setListeners() {

        mode.setOnClickListener(v -> showModeDialog());

        theme.setOnClickListener(v -> changeTheme());

        pauseHeadphoneUnplugged.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Main.settings.set("pauseHeadphoneUnplugged", true);
            } else {
                Main.settings.set("pauseHeadphoneUnplugged", false);
            }
        });

        resumeHeadphonePlugged.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Main.settings.set("resumeHeadphonePlugged", true);
            } else {
                Main.settings.set("resumeHeadphonePlugged", false);
            }

        });

        headphoneControl.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Main.settings.set("headphoneControl", true);
            } else {
                Main.settings.set("headphoneControl", false);
            }

        });

        saveRecent.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Main.settings.set("saveRecent", true);
            } else {
                Main.settings.set("saveRecent", false);
            }

        });

        saveCount.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Main.settings.set("saveCount", true);
            } else {
                Main.settings.set("saveCount", false);
            }
        });

        savePlaylist.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Main.settings.set("savePlaylist", true);
            } else {
                Main.settings.set("savePlaylist", false);
            }

        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_SETTLING:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + newState);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //onSlide

            }
        });
        blockfolder.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

            // Filter to only show results that can be "opened", such as
            // a file (as opposed to a list of contacts or timezones).
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Create a file with the requested MIME type.
            intent.setType("lol/lol");
            intent.putExtra(Intent.EXTRA_TITLE, ".nomedia");
            startActivityForResult(intent, 1122);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1122 && data != null) {
            DefaultSPHandler spHandler = new DefaultSPHandler(getApplicationContext());
            ArrayList<String> lol = spHandler.getListString("blockedURIs");
            if (lol == null)
                lol = new ArrayList<>();
            lol.add(data.getData().toString());
            spHandler.putListString("blockedURIs", lol);
        }
    }

    private void changeTheme() {
        selectedTheme = ThemeUtil.getCurrentActiveTheme();

        openBottomSheet();
        prepareThemeData();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void openBottomSheet() {

        RecyclerView mRecyclerView = findViewById(R.id.recyclerViewBottomSheet);

        mAdapter = new ThemeAdapter(mThemeList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void prepareThemeData() {
        mThemeList.clear();
        mThemeList.addAll(ThemeUtil.getThemeList());
        mAdapter.notifyDataSetChanged();
    }

    private void showModeDialog() {
        CharSequence[] values = {"Day Mode", "Night Mode"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Set Day/Night Mode");
        int checkeditem = Main.settings.get("modes", "Day").equals("Day") ? 0 : 1;
        int[] newcheckeditem = {checkeditem};
        builder.setSingleChoiceItems(values, checkeditem, (dialogInterface, item) -> {
                    if (item == 0) {
                        newcheckeditem[0] = 0;
                    } else if (item == 1) {
                        newcheckeditem[0] = 1;
                    }
        }
        );

        builder.setPositiveButton("Save", (dialog, which) -> {
            if (checkeditem == newcheckeditem[0]) {
                dialog.dismiss();
            } else {
                if (newcheckeditem[0] == 1) {
                    Main.settings.set("modes", "Night");
                } else {
                    Main.settings.set("modes", "Day");
                }
                Toast.makeText(SettingActivity.this, "Changes Made", Toast.LENGTH_SHORT).show();
                recreate();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settingsmenu, menu);
        return true;
    }

    public void sendFeedback(View view) {
        ExtraUtils.sendFeedback(SettingActivity.this);
    }

    public void gotoFAQ(View view) {
        ExtraUtils.openCustomTabs(SettingActivity.this, "mailto:rajasinghsamuelb@karunya.edu.in");
    }

    public void gotoPP(View view) {
        ExtraUtils.openCustomTabs(SettingActivity.this, "https://github.com/samuelrajasingh");
    }

    public void gotoGithub(View view) {
        ExtraUtils.openCustomTabs(SettingActivity.this, "https://github.com/samuelrajasingh");
    }

    public void cancelTheme(View view) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void saveTheme(View view) {
        Main.settings.set("themes", getResources().getStringArray(R.array.themes_values)[selectedTheme]);
        Toast.makeText(SettingActivity.this, "Changes Made", Toast.LENGTH_SHORT).show();
        recreate();
    }

    public void advancedFragment(View view) {
        findViewById(R.id.scrollSettings).setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.replaceAdvaced, AdvancedSettings.newInstance()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            findViewById(R.id.scrollSettings).setVisibility(View.VISIBLE);
        } else super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(String what) {
        switch (what) {
            case "jump":
                createFWDialog();
                break;
            case "rescan":
                rescanMediaStore();
                break;
            case "sleep":
                createSTdialog();
                break;
        }
    }

    private void createSTdialog() {
        CharSequence[] values = {"5 min", "10 min", "15 min", "20 min", "25 min"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Set Sleep timer to close urk17cs290.mediaplayer.music player if paused");
        int checkeditem = Main.settings.get("sleepTimer", 5);
        int[] newcheckeditem = {checkeditem};
        builder.setSingleChoiceItems(values, (checkeditem / 5) - 1, (dialogInterface, item) -> newcheckeditem[0] = item
        );

        builder.setPositiveButton("Save", (dialog, which) -> {
            if (checkeditem == newcheckeditem[0]) {
                dialog.dismiss();
            } else {
                if (newcheckeditem[0] == 0) {
                    Main.settings.set("sleepTimer", 5);
                } else if (newcheckeditem[0] == 1) {
                    Main.settings.set("sleepTimer", 10);
                } else if (newcheckeditem[0] == 2) {
                    Main.settings.set("sleepTimer", 15);
                } else if (newcheckeditem[0] == 3) {
                    Main.settings.set("sleepTimer", 20);
                } else if (newcheckeditem[0] == 4) {
                    Main.settings.set("sleepTimer", 25);
                } else {
                    Main.settings.set("sleepTimer", 5);
                }
                Toast.makeText(SettingActivity.this, "Changes Made", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void createFWDialog() {
        CharSequence[] values = {"5 sec", "10 sec", "15 sec", "20 sec", "25 sec"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Set jump value in forwar/rewind");
        int checkeditem = Main.settings.get("jumpValue", 10);
        int[] newcheckeditem = {checkeditem};
        builder.setSingleChoiceItems(values, (checkeditem / 5) - 1, (dialogInterface, item) -> newcheckeditem[0] = item
        );

        builder.setPositiveButton("Save", (dialog, which) -> {
            if (checkeditem == newcheckeditem[0]) {
                dialog.dismiss();
            } else {
                if (newcheckeditem[0] == 0) {
                    Main.settings.set("jumpValue", 5);
                } else if (newcheckeditem[0] == 1) {
                    Main.settings.set("jumpValue", 10);
                } else if (newcheckeditem[0] == 2) {
                    Main.settings.set("jumpValue", 15);
                } else if (newcheckeditem[0] == 3) {
                    Main.settings.set("jumpValue", 20);
                } else if (newcheckeditem[0] == 4) {
                    Main.settings.set("jumpValue", 25);
                } else {
                    Main.settings.set("jumpValue", 10);
                }

                Toast.makeText(SettingActivity.this, "Changes Made", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void rescanMediaStore() {
        ProgressDialog lol = new ProgressDialog(SettingActivity.this);
        lol.setMessage("Sending BroadCast to Scan");
        lol.setCancelable(false);
        lol.show();
        MediaScannerConnection.scanFile(
                getApplicationContext(),
                new String[]{"file://" + Environment.getExternalStorageDirectory()},
                new String[]{"audio/mp3", "audio/*"},
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onMediaScannerConnected() {
                        //onMediaScannerConnected

                    }

                    public void onScanCompleted(String path, Uri uri) {
                        lol.cancel();
                        runOnUiThread(() -> Toast.makeText(SettingActivity.this, "Remove from recent apps and restart application", Toast.LENGTH_SHORT).show());
                    }
                });

    }
}

package com.urk17cs290.mediaplayer.music.musicutils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.urk17cs290.mediaplayer.music.songdata.Song;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentUtils {

  public static void addsongToRecent(Context context, Song song) {
    List<Long> recent = getRecentSongs(context);
    if (recent == null) recent = new ArrayList<>();
    recent.remove(song.getId());
    recent.add(0, song.getId());
    if (recent.size() > 10) {
      recent.remove(recent.size() - 1);
    }

    SharedPreferences appSharedPrefs =
        context.getSharedPreferences("com.urk17cs290.bop.RecentSongs", Context.MODE_PRIVATE);
    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
    Gson gson = new Gson();
    String json = gson.toJson(recent);
    prefsEditor.putString("RecentSongs", json);
    prefsEditor.apply();
  }

  public static List<Long> getRecentSongs(Context context) {
    Type type = new TypeToken<List<Long>>() {}.getType();
    SharedPreferences appSharedPrefs =
        context.getSharedPreferences("com.urk17cs290.bop.RecentSongs", Context.MODE_PRIVATE);
    Gson gson = new Gson();
    String json = appSharedPrefs.getString("RecentSongs", "");
    return gson.fromJson(json, type);
  }

  public static void addcountSongsPlayed(Context context, Song song) {
    SharedPreferences preferences =
        context.getSharedPreferences("com.urk17cs290.bop.SongsPlayedCount", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    int count = preferences.getInt(String.valueOf(song.getId()), 0);
    editor.putInt(String.valueOf(song.getId()), ++count);
    editor.apply();
  }

  public static int getcountSongsPlayed(Context context, Song song) {
    SharedPreferences preferences =
        context.getSharedPreferences("com.urk17cs290.bop.SongsPlayedCount", Context.MODE_PRIVATE);
    return preferences.getInt(String.valueOf(song.getId()), 0);
  }

  public static List<Song> getMostPlayedSongs(Context context) {
    List<Song> songs = new ArrayList<>();

    HashMap<String, Integer> map = new HashMap<>();
    for (Song s : SongUtils.getSongs()) {
      map.put(String.valueOf(s.getId()), getcountSongsPlayed(context, s));
    }
    Map<String, Integer> mapSorted = ExtraUtils.sortMapByValue(map);

    for (Map.Entry<String, Integer> s : mapSorted.entrySet()) {
      if (s.getValue() > 0) songs.add(SongUtils.getSongById(Long.parseLong(s.getKey())));
      if (songs.size() >= 10) break;
    }
    return songs;
  }

  public static void saveLastPlaylist(Context context, List<Song> theSongs) {
    SharedPreferences appSharedPrefs =
        context.getSharedPreferences("com.urk17cs290.bop.LastPlaylist", Context.MODE_PRIVATE);
    SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
    prefsEditor.clear().apply();
    Gson gson = new Gson();
    // LEL need to clone the songs, Causing Concurrent Modification Error.
    String json = gson.toJson(new ArrayList<>(theSongs));
    prefsEditor.putString("playList", json);
    prefsEditor.apply();
  }

  public static List<Song> getLastPlayList(Context context) {
    Type type = new TypeToken<List<Song>>() {}.getType();
    SharedPreferences appSharedPrefs =
        context.getSharedPreferences("com.urk17cs290.bop.LastPlaylist", Context.MODE_PRIVATE);
    Gson gson = new Gson();
    String json = appSharedPrefs.getString("playList", "");
    return gson.fromJson(json, type);
  }
}

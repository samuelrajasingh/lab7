package com.urk17cs290.mediaplayer.music.musicutils;

import com.urk17cs290.mediaplayer.music.songdata.Song;
import com.urk17cs290.mediaplayer.music.playerMain.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongUtils {

  private static List<Song> songs = Main.data.songs;

  public static List<Song> getSongs() {

    return new ArrayList<>(songs);
  }

  /** Returns a list of Songs belonging to a specified album. */
  public static List<Song> getSongsByAlbum(String desiredAlbum) {
    ArrayList<Song> songsByAlbum = new ArrayList<Song>();

    for (Song song : songs) {
      String currentAlbum = song.getAlbum();

      if (currentAlbum.equals(desiredAlbum)) {
        songsByAlbum.add(song);
      }
    }

    return songsByAlbum;
  }

  /** Returns a list with all data that have the same `genre.` */
  public static List<Song> getSongsByGenre(String genreName) {

    ArrayList<Song> currentSongs = new ArrayList<Song>();

    for (Song song : songs) {

      String currentSongGenre = song.getGenre();
      if (currentSongGenre != null && currentSongGenre.equals(genreName)) currentSongs.add(song);
    }

    return currentSongs;
  }

  /** Returns a list with all data composed at `year`. */
  public static List<Song> getSongsByYear(int year) {

    ArrayList<Song> currentSongs = new ArrayList<Song>();

    for (Song song : songs) {

      int currentYear = song.getYear();

      if (currentYear == year) currentSongs.add(song);
    }

    return currentSongs;
  }

  public static Song getSongById(long id) {

    Song currentSong = null;

    for (Song song : songs)
      if (song.getId() == id) {
        currentSong = song;
        break;
      }

    return currentSong;
  }

  public static Song getSongbyFile(File file) {
    Song song = null;
    for (Song s : songs) {
      if (s.getFilePath().equals(file.getPath())) {
        song = s;
        break;
      }
    }
    return song;
  }

  /** Returns a list of Songs belonging to a specified artist. */
  public static List<Song> getSongsByArtist(String desiredArtist) {
    ArrayList<Song> songsByArtist = new ArrayList<Song>();

    for (Song song : songs) {
      String currentArtist = song.getArtist();

      if (currentArtist.equals(desiredArtist)) songsByArtist.add(song);
    }

    // Sorting resulting list by Album
    Collections.sort(songsByArtist, (a, b) -> a.getAlbum().compareTo(b.getAlbum()));

    return songsByArtist;
  }
}

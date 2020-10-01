package com.urk17cs290.mediaplayer.music.songdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a single audio file on the Android system.
 * <p>
 * It's a simple data container, filled with setters/getters.
 * <p>
 * Only mandatory fields are:
 * - id (which is a unique Android identified for a media file
 * anywhere on the system)
 * - filePath (full path for the file on the filesystem).
 */
public class Song implements Cloneable, Parcelable {

    private long id;
    private String filePath;
    private String title = "";
    private String artist = "";
    private String album = "";

    // optional metadata
    private int year = -1;
    private String genre = "";
    private int trackNo = -1;
    private long durationMs = -1;
    private String albumid = "";

    /**
     * Creates a new Song, with specified `songID` and `filePath`.
     *
     * @note It's a unique Android identifier for a media file
     * anywhere on the system.
     */
    public Song(long id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

    protected Song(Parcel in) {
        id = in.readLong();
        filePath = in.readString();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        year = in.readInt();
        genre = in.readString();
        trackNo = in.readInt();
        durationMs = in.readLong();
        albumid = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    /**
     * Identifier for the song on the Android system.
     * (so we can locate the file anywhere)
     */
    public long getId() {
        return id;
    }

    /**
     * Full path for the music file within the filesystem.
     */
    public String getFilePath() {
        return filePath;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }


    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    public int getTrackNumber() {
        return trackNo;
    }

    public void setTrackNumber(int trackNo) {
        this.trackNo = trackNo;
    }

    /**
     * Returns the duration of the song, in miliseconds.
     */
    public long getDuration() {
        return durationMs;
    }

    /**
     * Sets the duration of the song, in miliseconds.
     */
    public void setDuration(long durationMs) {
        this.durationMs = durationMs;
    }

    public long getDurationSeconds() {
        return getDuration() / 1000;
    }

    public long getDurationMinutes() {
        return getDurationSeconds() / 60;
    }

//    @Override
//    public Song clone() throws CloneNotSupportedException {
//        return (Song) super.clone();
//    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //writeToParcel

        dest.writeLong(id);
        dest.writeString(filePath);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeInt(year);
        dest.writeString(genre);
        dest.writeInt(trackNo);
        dest.writeLong(durationMs);
        dest.writeString(albumid);
    }
}


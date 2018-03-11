package com.idba.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-09 15:16
 * describe:
 **/
public class MusicPlayBean extends iObject implements Parcelable {

    /**
     * 歌曲名称
     */
    private String name;
    /**
     * 艺术家
     */
    private String artist;
    /**
     * 音乐时长（单位秒）
     */
    private int duration;
    /**
     * 权限
     */
    private int author;
    /**
     * 播放进度时长（单位秒）
     */
    private int speed;

    public MusicPlayBean setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public int getAuthor() {
        return author;
    }

    public int getSpeed() {
        return speed;
    }


    public MusicPlayBean() {
        super();
    }


    public MusicPlayBean setName(String name) {
        this.name = name;
        return this;
    }

    public MusicPlayBean setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public MusicPlayBean setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public MusicPlayBean setAuthor(int author) {
        this.author = author;
        return this;
    }

    @Override
    public String toString() {
        return "MusicPlayBean{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", author=" + author +
                ", speed=" + speed +
                '}';
    }


    @Override
    public int describeContents() {
        return 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        不能添加super
//        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeString(this.artist);
        dest.writeInt(this.duration);
        dest.writeInt(this.author);
        dest.writeInt(this.speed);
    }

    protected MusicPlayBean(Parcel in) {
//        不能添加super
//        super(in);
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.name = in.readString();
        this.artist = in.readString();
        this.duration = in.readInt();
        this.author = in.readInt();
        this.speed = in.readInt();
    }

    public static final Creator<MusicPlayBean> CREATOR = new Creator<MusicPlayBean>() {
        @Override
        public MusicPlayBean createFromParcel(Parcel source) {
            return new MusicPlayBean(source);
        }

        @Override
        public MusicPlayBean[] newArray(int size) {
            return new MusicPlayBean[size];
        }
    };
}

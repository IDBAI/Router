package com.idba.common.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * RouterAPP
 * author IDBA
 * email radio.ysh@qq.com
 * created 2017-10-10 10:09
 * describe:
 **/
public class MemoryBean extends iObject implements Parcelable{
    private int relaySize;
    private int totalSize;

    @Override
    public String toString() {
        return "MemoryBean{" +
                "relaySize=" + relaySize +
                ", totalSize=" + totalSize +
                '}';
    }

    @Override
    public int describeContents() {
        return 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.relaySize);
        dest.writeInt(this.totalSize);
    }

    public MemoryBean() {
        super();
    }

    public MemoryBean setRelaySize(int relaySize) {
        this.relaySize = relaySize;
        return this;
    }

    public MemoryBean setTotalSize(int totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    protected MemoryBean(Parcel in) {
        readFromParcel(in);
    }
    public void readFromParcel(Parcel in) {
        this.relaySize = in.readInt();
        this.totalSize = in.readInt();
    }

    public static final Creator<MemoryBean> CREATOR = new Creator<MemoryBean>() {
        @Override
        public MemoryBean createFromParcel(Parcel source) {
            return new MemoryBean(source);
        }

        @Override
        public MemoryBean[] newArray(int size) {
            return new MemoryBean[size];
        }
    };
}

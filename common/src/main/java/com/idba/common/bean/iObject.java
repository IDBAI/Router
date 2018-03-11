package com.idba.common.bean;

import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * author IDBA
 * email radio.ysh@qq.com
 * created 201709 11:18
 **/
public class iObject extends Binder implements Parcelable {


    public static final Creator<iObject> CREATOR = new Creator<iObject>() {
        @Override
        public iObject createFromParcel(Parcel in) {
            return new iObject(in);
        }

        @Override
        public iObject[] newArray(int size) {
            return new iObject[size];
        }
    };

    @Override
    public String toString() {
        return "iObject{}";
    }

    public iObject(Parcel in) {
        readFromParcel(in);
    }

    protected iObject() {
    }

    public void readFromParcel(Parcel in){

    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }


}

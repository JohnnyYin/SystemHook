package com.johnnyyin.systemhook.plugin;

import android.os.Parcel;
import android.os.Parcelable;

public class PluginActivityInfo implements Parcelable {
    public String targetActivityName;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.targetActivityName);
    }

    public PluginActivityInfo() {
    }

    protected PluginActivityInfo(Parcel in) {
        this.targetActivityName = in.readString();
    }

    public static final Creator<PluginActivityInfo> CREATOR = new Creator<PluginActivityInfo>() {
        @Override
        public PluginActivityInfo createFromParcel(Parcel source) {
            return new PluginActivityInfo(source);
        }

        @Override
        public PluginActivityInfo[] newArray(int size) {
            return new PluginActivityInfo[size];
        }
    };
}

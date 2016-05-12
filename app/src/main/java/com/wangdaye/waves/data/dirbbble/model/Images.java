package com.wangdaye.waves.data.dirbbble.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Models links to the various quality of images of a shot.
 */

public class Images implements Parcelable {
    // data
    public String hidpi;
    public String normal;
    public String teaser;

    private static final int[] NORMAL_IMAGE_SIZE = new int[] { 400, 300 };
    private static final int[] TWO_X_IMAGE_SIZE = new int[] { 800, 600 };

    public Images(String hidpi, String normal, String teaser) {
        this.hidpi = hidpi;
        this.normal = normal;
        this.teaser = teaser;
    }

    protected Images(Parcel in) {
        hidpi = in.readString();
        normal = in.readString();
        teaser = in.readString();
    }

    public String best() {
        // return !TextUtils.isEmpty(hidpi) ? hidpi : normal;
        return normal;
    }

    public int[] bestSize() {
        // return !TextUtils.isEmpty(hidpi) ? TWO_X_IMAGE_SIZE : NORMAL_IMAGE_SIZE;
        return NORMAL_IMAGE_SIZE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hidpi);
        dest.writeString(normal);
        dest.writeString(teaser);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Images> CREATOR = new Parcelable.Creator<Images>() {
        @Override
        public Images createFromParcel(Parcel in) {
            return new Images(in);
        }

        @Override
        public Images[] newArray(int size) {
            return new Images[size];
        }
    };
}

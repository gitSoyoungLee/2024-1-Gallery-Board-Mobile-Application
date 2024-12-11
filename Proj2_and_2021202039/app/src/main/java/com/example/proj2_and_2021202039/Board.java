package com.example.proj2_and_2021202039;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Board implements Parcelable {

    private long id;
    private String title;
    private String content;
    private Bitmap image;

    public Board(long id, String title, String content, Bitmap image) {
        this.id=id;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public long getId(){return id;}

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Bitmap getImage() {
        return image;
    }

    // Parcelable 구현 메서드들
    protected Board(Parcel in) {
        id=in.readLong();
        title = in.readString();
        content = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeParcelable(image, flags);
    }
}

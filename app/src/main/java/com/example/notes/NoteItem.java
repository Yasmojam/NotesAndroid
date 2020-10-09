package com.example.notes;

import android.os.Parcel;
import android.os.Parcelable;

public class NoteItem implements Parcelable {
    private int imageResource;
    private String heading;
    private String body;
    private String color="#FFFFFF";

    public NoteItem(int imageResource, String heading, String body, String color) {
        this.imageResource = imageResource;
        this.heading = heading;
        this.body = body;
        this.color = color;
    }

    protected NoteItem(Parcel in) {
        imageResource = in.readInt();
        heading = in.readString();
        body = in.readString();
        color = in.readString();
    }

    public static final Creator<NoteItem> CREATOR = new Creator<NoteItem>() {
        @Override
        public NoteItem createFromParcel(Parcel in) {
            return new NoteItem(in);
        }

        @Override
        public NoteItem[] newArray(int size) {
            return new NoteItem[size];
        }
    };

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageResource);
        dest.writeString(heading);
        dest.writeString(body);
        dest.writeString(color);
    }
}

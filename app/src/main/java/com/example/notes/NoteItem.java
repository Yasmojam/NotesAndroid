package com.example.notes;

import android.graphics.Color;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.sql.Timestamp;
import java.time.Instant;

public class NoteItem implements Parcelable, Comparable<NoteItem> {

    private String id;
    private String heading;
    private String body;
    private int color = Color.parseColor("#c6d8d3");
    private String icon;
    private String timestamp;
    private  Boolean selected;

    /**
     * Constructor with id
     */
    public NoteItem(
            String id,
            String heading,
            String body,
            int color,
            String icon,
            String timestamp
    ) {
        this.id = id;
        this.heading = heading;
        this.body = body;
        this.color = color;
        this.icon = icon;
        this.timestamp = timestamp;
        this.selected = false;
    }

    /**
     * Constructor without id
     */
    public NoteItem(
            String heading,
            String body,
            int color,
            String icon,
            String timestamp
    ) {
        this.heading = heading;
        this.body = body;
        this.color = color;
        this.icon = icon;
        this.timestamp = timestamp;
        this.selected = false;
    }

    protected NoteItem(Parcel in) {
        heading = in.readString();
        body = in.readString();
        color = in.readInt();
        icon = in.readString();
        timestamp = in.readString();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSelected(boolean bool) {
        this.selected = bool;
    }

    public boolean getSelected() {
        return this.selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(heading);
        dest.writeString(body);
        dest.writeInt(color);
        dest.writeString(icon);
        dest.writeString(timestamp);
    }

    /**
     * Comapare timestamps to sort notes list by timestamps.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compareTo(NoteItem noteItem) {
        if (getTimestamp() == null || noteItem.getTimestamp() == null){
            return 0;
        }
        // convert from string to timestamp before comparison
        Instant thisTimestamp = Timestamp.valueOf(getTimestamp()).toInstant();
        Instant comparingTimestamp = Timestamp.valueOf(noteItem.getTimestamp()).toInstant();
        return thisTimestamp.compareTo(comparingTimestamp);
    }

    /**
     * Method which returns an icon int from the associated string name.
     * @param icon
     */
    public int getIconFromString(String icon) {
        switch (icon) {
            case ("android"):
                return R.drawable.ic_android;
            case ("assignment"):
                return R.drawable.ic_assignment;
            case ("bookmark"):
                return R.drawable.ic_bookmark;
            case ("shopping basket"):
                return  R.drawable.ic_shopping_basket;
            case ("done"):
                return R.drawable.ic_done;
            default:
                return R.drawable.ic_error;
        }
    }
}

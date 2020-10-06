package com.example.notes;

public class NoteItem {
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
}

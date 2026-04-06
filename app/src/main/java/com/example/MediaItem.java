package com.example.a433assn4;

public class MediaItem {
    public String path;
    public String tags;
    public String datetime;
    public boolean isSelected;

    public MediaItem(String path, String tags, String datetime) {
        this.path = path;
        this.tags = tags;
        this.datetime = datetime;
        this.isSelected = false;
    }
}
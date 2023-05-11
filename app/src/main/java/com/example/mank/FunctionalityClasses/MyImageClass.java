package com.example.mank.FunctionalityClasses;

import android.net.Uri;

public class MyImageClass {
    private final Uri uri;
    private final String name;
    private final String path;
    private final String type;
    private final long id;

    public MyImageClass(Uri uri, String name, String path, String type, long id) {
        this.uri = uri;
        this.name = name;
        this.path = path;
        this.type = type;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public Uri getUri() {
        return uri;
    }
}


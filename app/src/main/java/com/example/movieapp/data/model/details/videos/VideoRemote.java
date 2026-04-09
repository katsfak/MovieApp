package com.example.movieapp.data.model.details.videos;

import com.google.gson.annotations.SerializedName;

public class VideoRemote {

    @SerializedName("site")
    private String site;

    @SerializedName("type")
    private String type;

    @SerializedName("key")
    private String key;

    @SerializedName("official")
    private boolean official;

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public boolean isOfficial() {
        return official;
    }
}

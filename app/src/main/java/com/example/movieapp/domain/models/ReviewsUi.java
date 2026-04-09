package com.example.movieapp.domain.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewsUi implements Parcelable {

    private String author;
    private String content;
    private String createdAt;
    private String id;
    private String updatedAt;
    private String url;

    public ReviewsUi(String author, String content, String createdAt, String id, String updatedAt, String url) {
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
        this.id = id;
        this.updatedAt = updatedAt;
        this.url = url;
    }

    protected ReviewsUi(Parcel in) {
        author = in.readString();
        content = in.readString();
        createdAt = in.readString();
        id = in.readString();
        updatedAt = in.readString();
        url = in.readString();
    }

    public static final Creator<ReviewsUi> CREATOR = new Creator<ReviewsUi>() {
        @Override
        public ReviewsUi createFromParcel(Parcel in) {
            return new ReviewsUi(in);
        }

        @Override
        public ReviewsUi[] newArray(int size) {
            return new ReviewsUi[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(createdAt);
        dest.writeString(id);
        dest.writeString(updatedAt);
        dest.writeString(url);
    }

    // Getters
    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getUrl() {
        return url;
    }

    // Setters
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ReviewsUi{" +
                "author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", id='" + id + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}


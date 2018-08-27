package com.shifu.user.mynewsfeed.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class JsonArticle {

    @SerializedName("source")
    @Expose
    private JsonASource source;

    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("urlToImage")
    @Expose
    private String urlToImage;

    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public JsonASource getSource() {
        return source;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("\nsource", source)
                .append("\nauthor", author)
                .append("\ntitle", title)
                .append("\ndescription", description)
                .append("\nurl", url)
                .append("\nurlToImage", urlToImage)
                .append("\npublishedAt", publishedAt)
                .toString();
    }

}
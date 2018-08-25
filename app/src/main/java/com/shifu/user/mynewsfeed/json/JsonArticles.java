package com.shifu.user.mynewsfeed.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shifu.user.mynewsfeed.realm.Article;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class JsonArticles {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("totalResults")
    @Expose
    private Long totalResults;

    @SerializedName("articles")
    @Expose
    private List<JsonArticle> articles;


    public String getStatus() {
        return status;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public List <JsonArticle> getArticles() {
        return articles;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("status", status)
                .append("totalResults", totalResults)
                .append("articles", articles)
                .toString();
    }
}

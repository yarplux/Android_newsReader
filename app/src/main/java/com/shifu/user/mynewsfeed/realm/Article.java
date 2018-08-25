package com.shifu.user.mynewsfeed.realm;

import com.shifu.user.mynewsfeed.json.JsonArticle;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import io.realm.RealmObject;

public class Article extends RealmObject {

    final private static String FIELD_PK = "uid";
    final private static String FIELD_NET_ID = "url";

    private final static DateFormat DATE_FORMAT_OUT = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss", Locale.US);

    private static AtomicLong lastID = new AtomicLong();

    public static void setLastID(Long id) {
        lastID.set((id==0)?1:id);
    }

    private static Long increment() {
        return lastID.getAndIncrement();
    }

    private Long uid;

    private String name;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private Date publishedAt;

    public Article () {}

    public Article (JsonArticle article) {

        uid = increment();
        name = article.getSource().getName();
        author = article.getAuthor();
        title = article.getTitle();
        description = article.getDescription();
        url = article.getUrl();
        urlToImage = article.getUrlToImage();
        try {
            publishedAt = DATE_FORMAT_OUT.parse(article.getPublishedAt().substring(0,20));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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

    public Date getPublishedAt() {
        return publishedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("author", author)
                .append("title", title)
                .append("description", description)
                .append("url", url)
                .append("urlToImage", urlToImage)
                .append("publishedAt", publishedAt)
                .toString();
    }

    public static String getPkField() {
        return FIELD_PK;
    }

    public static String getNetIdField() {
        return FIELD_NET_ID;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

}
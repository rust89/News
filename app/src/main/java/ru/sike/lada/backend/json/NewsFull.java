package ru.sike.lada.backend.json;

public class NewsFull {
    long id;
    private String title;
    private String html;
    private String author;
    private String big_picture;
    private int comm_num;
    private String small_picture;
    private String date;
    private String full_link;
    private String short_story;
    private int views;
    private String youtube;
    private String category_name;
    private String source;
    private String source_name;
    private int pr;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getHtml() {
        return html;
    }

    public String getAuthor() {
        return author;
    }

    public String getBigPicture() {
        return big_picture;
    }

    public int getCommNum() {
        return comm_num;
    }

    public String getSmallPicture() {
        return small_picture;
    }

    public String getDate() {
        return date;
    }

    public String getFullLink() {
        return full_link;
    }

    public String getShortStory() {
        return short_story;
    }

    public int getViews() {
        return views;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getCategoryName() {
        return category_name;
    }

    public String getSource() {
        return source;
    }

    public String getSourceName() {
        return source_name;
    }

    public int getPr() {
        return pr;
    }
}

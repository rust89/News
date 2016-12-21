package ru.sike.lada.backend.json;

public class NewsShort {

    private int id;
    private String big_picture;
    private String small_picture;
    private String date;
    private int comm_num;
    private int views;
    private String title;
    private String short_story;
    private int pr;

    public NewsShort () {

    }

    public int getId() {
        return id;
    }

    public String getBigPicture() {
        return big_picture;
    }

    public String getSmallPicture() {
        return small_picture;
    }

    public String getDate() {
        return date;
    }

    public int getCommNum() {
        return comm_num;
    }

    public int getViews() {
        return views;
    }

    public String getTitle() {
        return title;
    }

    public String getShortStory() {
        return short_story;
    }

    public int getPr() {
        return pr;
    }


}

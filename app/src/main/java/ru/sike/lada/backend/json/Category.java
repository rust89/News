package ru.sike.lada.backend.json;

public class Category {
    private long id;
    private String name;
    private String alt_name;
    private String color;
    private int sort_order;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAltName() {
        return alt_name;
    }

    public String getColor() {
        return color;
    }

    public int getSortOrder() {
        return sort_order;
    }
}

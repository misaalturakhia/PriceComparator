package com.misaal.pricecomparator.model;

/**
 * Created by Misaal on 22/04/2015.
 */
public class Store {

    private String logoUrl;

    private int price;

    private String link;

    public Store(String logo, int price, String link){
        this.logoUrl = logo;
        this.price = price;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public int getPrice() {
        return price;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}

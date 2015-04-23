package com.misaal.pricecomparator.model;

/**
 * Created by Misaal on 21/04/2015.
 */
public class Product {

    private String id;

    private String category;

    private String name;

    private int price;

    private String brand;

    private String imgUrl;


    /**
     * Constructor
     * @param id : product id
     * @param category : the category it falls under
     * @param name : its name
     * @param price : its price
     * @param brand : its brand
     * @param imageUrl : url of its image
     */
    public Product(String id, String category, String name, int price, String brand, String imageUrl){
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.imgUrl = imageUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getBrand() {
        return brand;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }
}

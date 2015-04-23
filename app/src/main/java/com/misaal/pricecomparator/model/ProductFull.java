package com.misaal.pricecomparator.model;

import java.util.List;

/**
 * Created by Misaal on 22/04/2015.
 */
public class ProductFull {

    private String name;

    private String imageUrl;

    private List<Store> stores;


    /**
     * Constructor
     * @param name : name of the product
     * @param url : url of the image
     * @param storeList : list of stores where it is available
     */
    public ProductFull(String name, String url, List<Store> storeList){
        this.name = name;
        this.imageUrl = url;
        this.stores = storeList;
    }

    public List<Store> getStores() {
        return stores;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

}

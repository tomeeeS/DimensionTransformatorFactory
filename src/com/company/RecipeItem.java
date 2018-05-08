package com.company;

/**
 * Created by satsaat on 2018. 05. 08..
 */
public class RecipeItem {

    Product.ProductType productType;
    int productCount;

    public RecipeItem(Product.ProductType productType, int productCount) {
        this.productType = productType;
        this.productCount = productCount;
    }
}

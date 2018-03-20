package com.holloway.allergenchecker;

/**
 * A consumable food.
 *
 * @author <a href="https://github.com/ross-holloway94"> Ross Holloway </a>
 * @version 20/03/2018
 */

public class FoodProduct {

    private String name;
    private String upc;
    private String ingredients;

    /**
     * Constructor used to create a FoodProduct object from the retrieved
     * information.
     *
     * @param name        The name of the product.
     * @param upc         The Unique Product Code of the product.
     * @param ingredients The ingredients of the product.
     */
    FoodProduct(String name, String upc, String ingredients) {
        this.name = name;
        this.upc = upc;
        this.ingredients = ingredients;
    }

    /**
     * @return name - The name of the FoodProduct.
     */
    public String getName() {
        return name;
    }

    /**
     * @return upc - The upc of the FoodProduct.
     */
    public String getUpc() {
        return upc;
    }

    /**
     * @return ingredients - The ingredients of the FoodProduct.
     */
    public String getIngredients() {
        return ingredients;
    }
}

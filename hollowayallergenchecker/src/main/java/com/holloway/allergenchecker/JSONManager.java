package com.holloway.allergenchecker;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.atteo.evo.inflector.English;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for handling local JSON file conversions.
 *
 * @author <a href="https://github.com/ross-holloway94"> Ross Holloway </a>
 * @version 20/03/2018
 */

public final class JSONManager {

    private static final JSONManager manager = new JSONManager();
    private final ObjectMapper mapper = new ObjectMapper(); //Used for json conversion.
    private final JsonFactory factory = mapper.getFactory();
    private HashSet<Consumer> consumers;

    /**
     * Path to the folder of which Consumer JSON files are saved.
     */
    private String consumerJSONLocation;

    /**
     * URL of the Open Food Facts database.
     */
    private String OpenFoodFactsURL = "https://uk.openfoodfacts.org/";

    private JSONManager() {
        this.consumers = new HashSet<>();
        //Exists to defeat instantiation. See https://www.javaworld.com/article/2073352/core-java/simply-singleton.html
    }

    /**
     * Checks if a JSONManager object exists, and if not creates one. This keeps
     * the JSONManager class singleton.
     *
     * @return The JSONManager object.
     */
    public static JSONManager getInstance() {
        return manager;
    }

    /**
     * Deserialises existing Consumer JSON files previously saved. Without this,
     * allergies will need to be entered every time.
     *
     * @return A set of the existing consumers.
     */
    public HashSet<Consumer> setUpConsumers() {
        File folder = new File(consumerJSONLocation);

        //For each file in the folder
        for (File consumerFile : folder.listFiles()) {
            Consumer newConsumer = new Consumer();
            try {
                JsonParser parser = factory.createParser(consumerFile);
                if (parser.nextToken() != JsonToken.START_OBJECT) {
                    System.err.println("Expected file to start with an object");
                }

                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    parser.nextToken();
                    String fieldName = parser.getCurrentName();
                    switch (fieldName) {
                        case "name":
                            newConsumer.setName(parser.getText());
                            break;
                        case "allergies":
                            newConsumer.setAllergies(parser.readValueAs(HashSet.class));
                            break;
                        default:
                            System.err.println("Name or allergies field not found whilst loading" + consumerFile);
                            break;
                    }
                }

            } catch (IOException ex) {
                System.err.println("Error reading the consumer file" + consumerFile.toString());
            }

            consumers.add(newConsumer);
        }
        return consumers;
    }

    /**
     * Serialise the Consumer details to a JSON file at
     * {@link JSONManager#consumerJSONLocation} for future use.
     *
     * @param consumer The consumer to be saved.
     */
    private void saveConsumerJSON(Consumer consumer) {
        try {
            //Save to file
            File json = new File(consumerJSONLocation + "/" + consumer.getName());
            json.createNewFile(); //This line will not run if file already exists.
            mapper.writeValue(json, consumer);
        } catch (IOException ex) {
            System.err.println("Consumer " + consumer.getName() + " - details did not save.");
        }
        if (!consumers.contains(consumer)) {
            consumers.add(consumer);
        }
    }

    /**
     * Deserialises a product JSON file from the Open Food Facts database into a
     * new FoodProduct object.
     *
     * @param upc The upc of the FoodProduct to find.
     * @return the FoodProduct object created with those details found in the
     * database.
     */
    private FoodProduct findProduct(String upc) {
        URL url = URLBuilder("/api/v0/product/" + upc + ".json");

        FoodProduct product = null;
        try {
            JsonNode root = mapper.readTree(url);
            String status = root.get("status").asText();
            if (!"0".equals(status)) {
                String productName = root.with("product").get("product_name_en").asText();
                String code = root.get("code").asText();
                String ingredients = root.with("product").get("ingredients_text_en").asText();
                if (productName == null) {
                    System.err.println("product name is null");
                }
                if (ingredients == null) {
                    System.err.println("ingredients are null");
                }
                product = new FoodProduct(productName, code, ingredients);
            }
        } catch (IOException ex) {
            System.err.println("The product cannot be found on the database.");
        }
        return product;
    }

    /**
     * Uses a subDomain at the end of the Open Food Facts URL to find the
     * product on the Open Food Facts database.
     *
     * @param subDomain The text to add at the end of the URL.
     * @return The newly created URL.
     */
    private URL URLBuilder(String subDomain) {
        URL url = null;
        try {
            url = new URL(OpenFoodFactsURL + subDomain + ".json");
        } catch (MalformedURLException ex) {
            System.err.println("URL could not be found for product UPC " + subDomain);
        }
        return url;
    }

    /**
     * @return {@link JSONManager#consumerJSONLocation}
     */
    public String getConsumerJSONLocation() {
        return consumerJSONLocation;
    }

    /**
     * @param consumerJSONLocation {@link JSONManager#consumerJSONLocation}
     */
    public void setConsumerJSONLocation(String consumerJSONLocation) {
        this.consumerJSONLocation = consumerJSONLocation;
    }

    /**
     * Create a new consumer, and serialise.
     *
     * @param consumerName      The name of the consumer.
     * @param consumerAllergies The allergies of the consumer.
     * @return The newly created consumer object.
     */
    public Consumer createNewConsumer(String consumerName, HashSet<String> consumerAllergies) {
        Consumer newConsumer = new Consumer(consumerName, consumerAllergies);
        saveConsumerJSON(newConsumer);
        return newConsumer;
    }

    /**
     * Update allergies for a consumer that already exists and serialise.
     *
     * @param consumer          The consumer to add allergies to.
     * @param consumerAllergies The allergies of the consumer to store.
     * @return The consumer with the allergy list updated.
     */
    public Consumer updateAllergies(Consumer consumer, HashSet<String> consumerAllergies) {
        consumer.setAllergies(consumerAllergies);
        saveConsumerJSON(consumer);
        return consumer;
    }

    /**
     * Update the name of a consumer that already exists and serialise.
     *
     * @param consumer The consumer to change the name of.
     * @param name     The name of the consumer
     * @return The consumer with the updated name.
     */
    public Consumer updateName(Consumer consumer, String name) {
        consumer.setName(name);
        saveConsumerJSON(consumer);
        return consumer;
    }

    /**
     * Compares the allergies of all Consumers with the ingredients of a
     * Product.
     *
     * @param productUPC Unique product code of the Product to be checked.
     * @return Unsafe allergens which are found in the product.
     */
    public Set<String> checkForAllergens(String productUPC) {

        FoodProduct product = findProduct(productUPC);

        Set<String> consumerAllergens = new HashSet<>();
        for (Consumer consumer : consumers) {
            consumerAllergens.addAll(consumer.getAllergies());
        }

        Set<String> allergensFound = new HashSet<>();
        String productIngredients = product.getIngredients();
        for (String allergen : consumerAllergens) {
            boolean doesContainSingle = productIngredients.toLowerCase().contains(allergen.toLowerCase());
            String allergenPlural = English.plural(allergen);
            boolean doesContainPlural = productIngredients.toLowerCase().contains(allergenPlural.toLowerCase());
            if ((doesContainSingle) || doesContainPlural) {
                allergensFound.add(allergen);
            }
        }


        return allergensFound;
    }
}

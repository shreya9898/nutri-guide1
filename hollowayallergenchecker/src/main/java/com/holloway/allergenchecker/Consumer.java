package com.holloway.allergenchecker;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashSet;


/**
 * The person whom will be consuming the food. This may not be the the user.
 *
 * @author <a href="https://github.com/ross-holloway94"> Ross Holloway </a>
 * @version 20/03/2018
 */
public class Consumer {

    private String name; //Not final in case of user mistype.
    private HashSet<String> allergies = new HashSet();
    JSONManager jsonmanager = JSONManager.getInstance();

    /**
     * Default constructor used for JSONManager.setUpConsumers().
     */
    Consumer() {
        //Default constructor
    }

    /**
     * Constructor to set name and allergies upon creation.
     *
     * @param consumerName      The name of the Consumer.
     * @param consumerAllergies Set of consumer's allergies.
     */
    public Consumer(String consumerName, HashSet consumerAllergies) {
        name = consumerName;
        allergies = consumerAllergies;
    }

    /**
     * Sets the name of the Consumer.
     *
     * @param name the name to set
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the consumer's name.
     *
     * @return The name of the consumer.
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Returns the Consumer's allergies.
     *
     * @return The consumer's allergies.
     */
    public HashSet<String> getAllergies() {
        return allergies;
    }

    /**
     * Set the consumer's allergies.
     *
     * @param consumerAllergies the consumer's allergies.
     */
    @JsonProperty("allergies")
    @JsonDeserialize(as = HashSet.class)
    void setAllergies(HashSet<String> consumerAllergies) {
        allergies = consumerAllergies;
    }
}


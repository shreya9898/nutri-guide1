package com.holloway.allergenchecker;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="https://github.com/ross-holloway94"> Ross Holloway </a>
 * @version 20/03/2018
 */

public class JSONManagerTest {
    private static JSONManager instance = JSONManager.getInstance();

    @Before
    public void setUp() throws Exception {
        instance.setConsumerJSONLocation("C:\\Users\\Ross\\Documents\\GitHub\\openfoodfacts-androidapp\\hollowayallergenchecker\\consumers");
    }


    @Test
    public void createNewConsumer() throws Exception {

        //setup
        String consumerName = "jane";
        HashSet<String> consumerAllergies = new HashSet<>();
        consumerAllergies.add("peanut");

        //method
        Consumer result = instance.createNewConsumer(consumerName, consumerAllergies);

        //test that allergies match
        assertEquals("The collections do not match", consumerAllergies, result.getAllergies());
    }

    @Test
    public void updateAllergies() throws Exception {
        //setup
        Consumer ross = new Consumer("ross", null);
        HashSet<String> testAllergies = new HashSet<>();
        testAllergies.add("peanut");
        testAllergies.add("tree nut");
        testAllergies.add("egg");

        //method
        Consumer expResult = ross;
        Consumer result = instance.updateAllergies(ross, testAllergies);

        //test that allergies match
        assertEquals("The collections do not match", testAllergies, ross.getAllergies());
    }

    @Test
    public void updateName() throws Exception {

        HashSet<String> testAllergens = new HashSet<>();
        testAllergens.add("peanut");
        testAllergens.add("hazelnut");
        testAllergens.add("egg");
        Consumer testConsumer = new Consumer("Pierre", testAllergens);

        instance.updateName(testConsumer, "Ross");

        assertEquals("The name did not update", "Ross", testConsumer.getName());
    }

    @Test
    public void checkForAllergens() throws Exception {

        //setup
        HashSet<String> testAllergens = new HashSet<>();
        testAllergens.add("peanut");
        testAllergens.add("hazelnut");
        testAllergens.add("egg");
        Consumer aConsumer = new Consumer("ross", testAllergens);

        //method
        Set<String> result = instance.checkForAllergens("01268584");
        assertTrue("the egg allergen was not detected", result.contains("egg"));
    }

}
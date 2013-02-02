package com.codeminer42.dismantle;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

public class ModelWithoutKeyPathsTest {

    class AddressExample extends Model {

        public String zipcode;
        public Double distance;

        public AddressExample() {
            super();
        }

        public AddressExample(Map<String, Object> rep) {
            super(rep);
        }

        @Override
        public Map<String, String> externalRepresentationKeyPaths() {
            // No need to define keypaths if the property is the same
            // zipcode
            // distance
            return null;
        }
    }

    @Test
    public void testConstructor() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("distance", 0.0001);
        map.put("zipcode", "60140-140");
        AddressExample example = new AddressExample(map);
        assertThat(example.distance, is(0.0001));
        assertThat(example.zipcode, is("60140-140"));
    }

    @Test
    public void testExternalRepresentation() {
        AddressExample example = new AddressExample();
        example.distance = 0.0001;
        example.zipcode  = "60140-140";
        Map<String, Object> rep = example.externalRepresentation();
        assertThat((Double) rep.get("distance"), is(0.0001));
        assertThat((String) rep.get("zipcode"), is("60140-140"));
    }
}

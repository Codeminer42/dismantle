package com.codeminer42.dismantle;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ModelTest {

    class AddressExample extends Model {

        public String zipcode;

        public  AddressExample() {
            super();
        }

        public AddressExample(Map<String, Object> rep) {
            super(rep);
        }

        @Override
        public Map<String, String> externalRepresentationKeyPaths() {
            Map<String, String> extRep = new HashMap<String, String>();
            extRep.put("zipcode", "zipcode");
            return extRep;
        }
    }

    class ModelExample extends Model {

        public String birthdate;
        public Double distance;
        public String content;
        public Boolean smoker;
        public AddressExample address;

        public ModelExample() {
            super();
        }

        public ModelExample(Map<String, Object> rep) {
            super(rep);
        }

        @Override
        public Map<String, String> externalRepresentationKeyPaths() {
            Map<String, String> extRep = new HashMap<String, String>();
            extRep.put("birthdate", "birth_date");
            extRep.put("distance", "distance");
            extRep.put("content", "CONTENT");
            extRep.put("smoker", "user.smoker");
            extRep.put("address", "address");
            return extRep;
        }

        private String transformToBirthdate(Object obj) {
            return (String) obj;
        }

        private Boolean transformToSmoker(Object obj) {
            return (Boolean) obj;
        }

        private Double transformToDistance(Object obj) {
            return Double.valueOf((String) obj);
        }

        private Object transformFromDistance(Double obj) {
            if (obj != null)
                return obj.toString();
            return null;
        }

        private  AddressExample transformToAddress(Object obj) {
            AddressExample address = new AddressExample((Map<String, Object>)obj);
            return address;
        }
    }

    @Test
    public void testPropertiesWithTransformMethods() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("birth_date", "2002");
        map.put("distance", "2000.0");
        ModelExample example = new ModelExample(map);
        assertThat(example.distance, is(2000.0));
        assertThat(example.birthdate, is("2002"));
    }

    @Test
    public void testPropertiesWithoutTransformMethods() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("CONTENT", "Codeminer 42");
        ModelExample example = new ModelExample(map);
        assertThat(example.content, is("Codeminer 42"));
        assertThat(example.distance, nullValue());
        assertThat(example.birthdate, nullValue());
    }

    @Test
    public void testNestedProperty() {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> nestedMap = new HashMap<String, Object>();
        nestedMap.put("smoker", true);
        map.put("user", nestedMap);

        ModelExample example = new ModelExample(map);
        assertThat(example.smoker, is(true));
    }

    @Test
    public void testExternalRepresentationWithCompleteExample() {
        ModelExample example = new ModelExample();
        example.birthdate = "2002";
        example.distance = 200.9;
        example.content = "Codeminer 42";
        example.smoker = true;

        Map<String, Object> rep = example.externalRepresentation();

        assertThat((String) rep.get("birth_date"), is("2002"));
        assertThat((String) rep.get("distance"), is("200.9"));
        assertThat((String) rep.get("CONTENT"), is("Codeminer 42"));
        Map<String, Object> userRep = (Map<String, Object>) rep.get("user");
        assertThat((Boolean) userRep.get("smoker"), is(true));
    }

    @Test
    public void testExternalRepresentationWithIncompleteExample() {
        ModelExample example = new ModelExample();
        example.birthdate = "2002";

        Map<String, Object> rep = example.externalRepresentation();

        assertThat((String) rep.get("birth_date"), is("2002"));
        assertThat((Double) rep.get("distance"), nullValue());
        assertThat((String) rep.get("CONTENT"), nullValue());
    }

    @Test
    public void testTransformToAnotherObject() {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> nestedMap = new HashMap<String, Object>();
        nestedMap.put("zipcode", "12345");
        map.put("address", nestedMap);

        ModelExample example = new ModelExample(map);

        assertThat(example.address, instanceOf(AddressExample.class));
        assertEquals(example.address.zipcode, "12345");
    }

    @Test
    public void testConstructWithExternalRepresentation() {
        ModelExample example = new ModelExample();
        example.birthdate = "2002";
        example.distance = 200.9;
        example.content = "Codeminer 42";
        example.smoker = true;

        Map<String, Object> rep = example.externalRepresentation();

        ModelExample recretedExample = new ModelExample(rep);

        assertEquals(example.birthdate, recretedExample.birthdate);
        assertEquals(example.content, recretedExample.content);
        assertEquals(example.smoker, recretedExample.smoker);
        assertEquals(example.distance, recretedExample.distance);
    }
}

package com.codeminer42.dismantle;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Double.parseDouble;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ModelTest {

    class ModelExample extends Model {

        public String birthdate;
        public Double distance;
        public String content;

        public ModelExample() {
            super();
        }

        public ModelExample(Map<String, Object> rep) {
            super(rep);
        }

        @Override
        protected Map<String, String> externalRepresentationKeyPaths() {
            Map<String, String> extRep = new HashMap<String, String>();
            extRep.put("birthdate", "birth_date");
            extRep.put("distance", "distance");
            extRep.put("content", "CONTENT");
            return extRep;
        }

        public Object transformToBirthdate(Object obj) {
            return obj;
        }

        public Object transformToDistance(Object obj) {
            try {
                return parseDouble((String) obj);
            } catch(Exception e) {
            }
            return null;
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
    public void testExternalRepresentationWithCompleteExample() {
        ModelExample example = new ModelExample();
        example.birthdate = "2002";
        example.distance = 200.9;
        example.content = "Codeminer 42";

        Map<String, Object> rep = example.externalRepresentation();

        assertThat((String) rep.get("birthdate"), is("2002"));
        assertThat((Double) rep.get("distance"), is(200.9));
        assertThat((String) rep.get("content"), is("Codeminer 42"));
    }

    @Test
    public void testExternalRepresentationWithIncompleteExample() {
        ModelExample example = new ModelExample();
        example.birthdate = "2002";

        Map<String, Object> rep = example.externalRepresentation();

        assertThat((String) rep.get("birthdate"), is("2002"));
        assertThat((Double) rep.get("distance"), nullValue());
        assertThat((String) rep.get("content"), nullValue());
    }
}

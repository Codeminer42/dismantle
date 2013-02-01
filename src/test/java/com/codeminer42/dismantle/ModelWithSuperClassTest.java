package com.codeminer42.dismantle;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ModelWithSuperClassTest {

    abstract class ModelSuper extends Model {
        protected String identifier;

        public ModelSuper(Map<String, Object> rep) {
            super(rep);
        }
    }

    class ModelExample extends ModelSuper {

        public ModelExample(Map<String, Object> rep) {
            super(rep);
        }

        @Override
        public Map<String, String> externalRepresentationKeyPaths() {
            Map<String, String> extRep = new HashMap<String, String>();
            extRep.put("identifier", "identifier");
            return extRep;
        }
    }

    @Test
    public void testSetMethodCall() {
        Map<String, Object> rep = new HashMap<String, Object>();
        rep.put("identifier", "comechao");
        ModelExample example = new ModelExample(rep);
        assertThat(example.identifier, is("comechao"));
    }
}

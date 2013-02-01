package com.codeminer42.dismantle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Model {
    abstract public Map<String, String> externalRepresentationKeyPaths();

    protected Model() {

    }

    protected Model(Map<String, Object> externalRepresentation) {
        final Map<String, String> selfRepresentation = this.externalRepresentationKeyPaths();
        for (String property : selfRepresentation.keySet()) {
            String path = selfRepresentation.get(property);
            Object transformable = getData(externalRepresentation, path);
            assignProperty(property, transformable);
        }
    }

    private static Object getData(Map<String, Object> externalRepresentation, String path) {
        if (path.contains(".")) {
            String[] subPaths = path.split("\\.");
            Object nestedObject = externalRepresentation.get(subPaths[0]);
            if (nestedObject instanceof Map) {
                try {
                    Map<String, Object> nestedRepresentation = (Map<String, Object>) nestedObject;
                    return getData(nestedRepresentation, path.substring(subPaths[0].length()+1));
                } catch (ClassCastException e) {
                    // Well, not a Map<String, Object> :/
                    e.printStackTrace();
                }
            }
            return null;
        } else {
            return externalRepresentation.get(path);
        }
    }

    public Map<String, Object> externalRepresentation() {
        final Map<String, String> selfRepresentation = this.externalRepresentationKeyPaths();
        Map<String, Object> representation = new HashMap<String, Object>();
        for (String property : selfRepresentation.keySet()) {
            representation.put(selfRepresentation.get(property), getProperty(property));
        }
        return representation;
    }

    private void assignProperty(String property, Object mapValue) {
        Object result = null;
        try {
            try {
                result = tryToInvokeTransformationTo(property, mapValue);
            } catch (NoSuchMethodException e) {
                // try to setTheField with the result we got
                result = mapValue;
            } catch(Exception e) {
                // Unexpected error while trying to invoke the transform method.
            } finally {
                this.tryToSetField(property, result);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private Object getProperty(String property) {
        Object propertyData = tryToGetField(property);
        Object result;
        try {
            result = tryToInvokeTransformationFrom(property, propertyData);
        } catch (NoSuchMethodException e) {
            result = propertyData;
        }
        return result;
    }

    private Object tryToInvokeTransformationTo(String property, Object mapValue) throws NoSuchMethodException {
        try {
            Method method = this.getClass().getDeclaredMethod("transformTo" + property.substring(0, 1).toUpperCase() + property.substring(1), Object.class);
            method.setAccessible(true);
            return method.invoke(this, mapValue);
        } catch (InvocationTargetException ignored) {
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }

    private Object tryToInvokeTransformationFrom(String property, Object propertyValue) throws NoSuchMethodException {
        try {
            Method method = this.getClass().getDeclaredMethod("transformFrom" + captitalizeFirstLetter(property), Object.class);
            method.setAccessible(true);
            return method.invoke(this, propertyValue);
        } catch (InvocationTargetException ignored) {
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }

    private void tryToSetField(String property, Object data) {
        try {
            Field field = getField(this.getClass(), property);
            field.setAccessible(true);
            field.set(this, data);
        } catch (NoSuchFieldException ignored) {
        } catch (IllegalAccessException ignored) {
        }
    }

    /**
     * Get the field through the super classes
     * @param property
     * @return property data
     */
    private final Object tryToGetField(String property) {
        try {
            Field field = getField(this.getClass(), property);
            field.setAccessible(true);
            return field.get(this);
        } catch (NoSuchFieldException ignored) {
        } catch (IllegalAccessException ignored) {
        }
        return null;
    }

    private static String captitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    private static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }
}

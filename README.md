# dismantle

A [Mantle](https://github.com/github/Mantle) clone in Java.

## Example Usage

A ModelExample with birthdate and distance properties:

```java
class ModelExample {
  public String birthdate;
  public Double distance;
}
```

Now we extends the example class with Model. Giving a map of keypaths, the super() constructor will earn the ability to receive a Map<String, Object> to be matched.
If the simple attribution is not easy (like on distance), you need to develop a method transformToDistance (see below).


```java
class ModelExample extends Model {

  public String birthdate;
  public Double distance;

  public ModelExample(Map<String, Object> rep) {
    super(rep);
  }

  @Override
  public Map<String, String> externalRepresentationKeyPaths() {
    Map<String, String> extRep = new HashMap<String, String>();
    extRep.put("birthdate", "birth_date");
    return extRep;
  }

  private Double transformToDistance(Object obj) {
    return Double.valueof((String) obj);
  }

  private Object transformFromDistance(Double obj) {
    return obj;
  }
}
```

The ModelExample can be instantiated using a map now! :)

You can also describe nested attributes on a map, for example:

```java
  @Override
  public Map<String, String> externalRepresentationKeyPaths() {
    Map<String, String> extRep = new HashMap<String, String>();
    extRep.put("birthdate", "birth_date");
    extRep.put("distance", "location.address");
    return extRep;
  }
```

Now, the constructor will look for a Map inside a Map within the keys: 'location', then 'address'.

Wanna see more complex examples? [ModelTest](https://github.com/edgurgel/dismantle/blob/master/src/test/java/com/codeminer42/dismantle/ModelTest.java)

## License

dismantle is available under the MIT license. See the LICENSE file for more info.

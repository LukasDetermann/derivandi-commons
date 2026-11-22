Provides generally useful and small code generators.

* Generator for Property Constants Based on JavaBeansProperties

## Property Generator 

Generates constants for POJOs annotated with `@GenerateProperties` or `@GeneratePropertiesFor(MyClass.class)`.
Properties have this signature.
```java
@NotNullByDefault
public interface Property<POJO, TYPE> {
   
   // static factories
   
   String getName();

   Function<POJO, TYPE> getGetter();

   Optional<BiConsumer<POJO, TYPE>> getSetter();

   boolean isMutable();
}
```
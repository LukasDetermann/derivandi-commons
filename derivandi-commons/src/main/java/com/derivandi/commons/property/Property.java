package com.derivandi.commons.property;

import org.jetbrains.annotations.NotNullByDefault;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@NotNullByDefault
public interface Property<POJO, TYPE>
{
   static <POJO, TYPE> Property<POJO, TYPE> propertyOf(String name, Function<POJO, TYPE> getter)
   {
      return new PropertyImpl<>(name, getter, null);
   }

   static <POJO, TYPE> Property<POJO, TYPE> propertyOf(String name, Function<POJO, TYPE> getter, BiConsumer<POJO, TYPE> setter)
   {
      return new PropertyImpl<>(name, getter, setter);
   }

   String getName();

   Function<POJO, TYPE> getGetter();

   Optional<BiConsumer<POJO, TYPE>> getSetter();

   boolean isMutable();
}
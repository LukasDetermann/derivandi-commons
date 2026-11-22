package com.derivandi.commons.property;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@NotNullByDefault
record PropertyImpl<POJO, TYPE>(String name,
                                Function<POJO, TYPE> getter,
                                @Nullable BiConsumer<POJO, TYPE> setter)
      implements Property<POJO, TYPE>
{
   @Override
   public String getName()
   {
      return name();
   }

   @Override
   public Function<POJO, TYPE> getGetter()
   {
      return getter();
   }

   @Override
   public Optional<BiConsumer<POJO, TYPE>> getSetter()
   {
      return ofNullable(setter());
   }

   @Override
   public boolean isMutable()
   {
      return setter() != null;
   }
}
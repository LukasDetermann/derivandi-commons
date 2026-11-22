package com.derivandi.commons.property;

import com.derivandi.api.D;
import com.derivandi.api.dsl.RenderingContext;
import com.derivandi.api.dsl.TypeRenderable;
import com.derivandi.api.dsl.field.FieldAdditionalNameStep;
import com.derivandi.api.dsl.import_.ImportRenderable;
import com.derivandi.api.dsl.interface_.InterfaceGenericStep;
import com.derivandi.api.dsl.method.MethodReceiverStep;
import com.derivandi.api.generate.GenerateTypesafeUsage;
import com.derivandi.api.processor.DerivandiProcessor;
import com.derivandi.api.processor.ProcessorBuilder;
import com.derivandi.api.processor.ProcessorConfiguration;
import com.derivandi.api.processor.SimpleContext;

import java.util.stream.Stream;

import static com.derivandi.api.dsl.JavaDsl.*;
import static com.derivandi.api.dsl.RenderingContext.createRenderingContext;
import static java.lang.Character.toUpperCase;

@GenerateTypesafeUsage(GeneratePropertiesFor.class)
public class GeneratePropertiesProcessor
      extends DerivandiProcessor
{
   private static final InterfaceGenericStep PROPERTY_RECORD = interface_().package_("com.derivandi.commons.property").name("Property");
   private static final MethodReceiverStep PROPERTY_OF_STATIC_FACTORY = method().static_().resultType(PROPERTY_RECORD).name("propertyOf");
   private static final ImportRenderable PROPERTY_OF_IMPORT = import_().static_().method(PROPERTY_RECORD, PROPERTY_OF_STATIC_FACTORY);
   private static final RenderingContext RENDERING_CONTEXT = createRenderingContext();

   @Override
   public ProcessorConfiguration buildProcessor(ProcessorBuilder processorBuilder)
   {
      return processorBuilder.process(GeneratePropertiesProcessor::process);
   }

   private static void process(SimpleContext context)
   {
      getAnnotated(context)
            .map(annotated ->
                       interface_().package_(annotated.getPackage().getQualifiedName() + ".property")
                                   .import_(import_().declared(annotated))
                                   .import_(PROPERTY_OF_IMPORT)
                                   .annotate(generated(GeneratePropertiesProcessor.class.getName()))
                                   .public_().name(annotated.getName() + "Properties")
                                   .field(annotated.getProperties()
                                                   .stream()
                                                   .filter(D.Property::isMutable)
                                                   .map(property -> createField(annotated, property))
                                                   .toList()))
            .forEach(context::writeAndCompileSourceFile);
   }

   private static Stream<D.Class> getAnnotated(SimpleContext context)
   {
      return Stream.concat(context.getClassesAnnotatedWith(GenerateProperties.class.getName()).stream(),
                           context.getClassesAnnotatedWith(GeneratePropertiesFor.class.getName()).stream()
                                  .map(aClass -> aClass.getFirstUsageOfOrThrow(GeneratePropertiesFor.class.getName()))
                                  .map(GeneratePropertiesForTypesafeUsage::new)
                                  .map(GeneratePropertiesForTypesafeUsage::valueValue)
                                  .flatMap(typeValue -> typeValue.getValue().stream())
                                  .map(D.AnnotationValue.TypeValue::getValue)
                                  .filter(D.Class.class::isInstance)
                                  .map(D.Class.class::cast));
   }

   private static FieldAdditionalNameStep createField(D.Class annotated, D.Property property)
   {
      return field().type(PROPERTY_RECORD.genericUsage(annotated, asBoxed(property)))
                    .name(toConstantName(property.getName()))
                    .initializer("%s(\"%s\", %s::%s, %s::%s)"
                                       .formatted(PROPERTY_OF_STATIC_FACTORY.renderName(RENDERING_CONTEXT),
                                                  property.getName(),
                                                  annotated.getName(),
                                                  property.getGetter().getName(),
                                                  annotated.getName(),
                                                  property.getSetter().orElseThrow().getName()));
   }

   private static TypeRenderable asBoxed(D.Property property)
   {
      return property.getType() instanceof D.Primitive primitive ? primitive.asBoxed() : property.getType();
   }

   private static String toConstantName(String input)
   {
      StringBuilder sb = new StringBuilder();
      sb.append(toUpperCase(input.charAt(0)));

      for (int i = 1; i < input.length(); i++)
      {
         char c = input.charAt(i);
         if (Character.isUpperCase(c))
         {
            sb.append('_');
         }
         sb.append(toUpperCase(c));
      }
      return sb.toString();
   }
}
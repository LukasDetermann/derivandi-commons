package com.derivandi.commons.property;

import org.junit.jupiter.api.Test;

import static com.derivandi.api.test.ProcessorTest.processorTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratePropertiesProcessorTest
{
   @Test
   void dings()
   {
      processorTest().withCodeToCompile("Person.java",
                                        """
                                        package com.derivandi.commons.property;
                                        
                                        import com.derivandi.commons.property.GenerateProperties;
                                        import java.util.Objects;
                                        import java.util.List;
                                        
                                        @GenerateProperties
                                        public class Person {
                                           private String name;
                                           private int age;
                                           private List<String> synonymes;
                                        
                                           public String getName() {
                                              return name;
                                           }
                                        
                                           public void setName(String name) {
                                              this.name = name;
                                           }
                                        
                                           public int getAge() {
                                              return age;
                                           }
                                        
                                           public void setAge(int age) {
                                              this.age = age;
                                           }
                                        
                                           public void setSynonymes(List<String> synonymes) {
                                              this.synonymes = synonymes;
                                           }
                                        
                                           public List<String> getSynonymes() {
                                              return synonymes;
                                           }
                                        
                                           @Override
                                           public final boolean equals(Object o) {
                                              if (!(o instanceof Person person)) {
                                                 return false;
                                              }
                                        
                                              return age == person.age && Objects.equals(name, person.name);
                                           }
                                        
                                           @Override
                                           public int hashCode() {
                                              int result = Objects.hashCode(name);
                                              result = 31 * result + age;
                                              return result;
                                           }
                                        
                                           @Override
                                           public String toString() {
                                              return "Person{" +
                                                     "name='" + name + '\\'' +
                                                     ", age=" + age +
                                                     '}';
                                           }
                                        }
                                        """)
                     .process(new GeneratePropertiesProcessor())
                     .withJavaSourceFileCallback(javaSourceFiles ->
                                                 {
                                                    assertEquals(1, javaSourceFiles.size());

                                                    assertEquals("""
                                                                 package com.derivandi.commons.property.property;
                                                                 
                                                                 import com.derivandi.commons.property.Person;
                                                                 import static com.derivandi.commons.property.Property.propertyOf;
                                                                 import javax.annotation.processing.Generated;
                                                                 import java.util.List;
                                                                 import com.derivandi.commons.property.Property;
                                                                 import com.derivandi.commons.property.Person;
                                                                 
                                                                 @Generated(value = "com.derivandi.commons.property.GeneratePropertiesProcessor", date = "0")
                                                                 public interface PersonProperties {
                                                                    Property<Person, String> NAME = propertyOf("name", Person::getName, Person::setName);
                                                                 
                                                                    Property<Person, Integer> AGE = propertyOf("age", Person::getAge, Person::setAge);
                                                                 
                                                                    Property<Person, List<String>> SYNONYMES = propertyOf("synonymes", Person::getSynonymes, Person::setSynonymes);
                                                                 }""",
                                                                 javaSourceFiles.getFirst()
                                                                                .getContent()
                                                                                .replaceFirst("date = \"[0-9-T:.]*\"",
                                                                                              "date = \"0\""));
                                                 })
                     .run();
   }
}
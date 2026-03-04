package de.mtgz.logging.common;

public class UuidGeneratorMock extends UuidGenerator {
   private final String id;

   public UuidGeneratorMock(String id) {
      this.id = id;
   }

   @Override
   public String generate() {
      return this.id;
   }
}

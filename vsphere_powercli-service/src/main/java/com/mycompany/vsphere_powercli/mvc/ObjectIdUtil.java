/* Copyright (c) 2012-2018 VMware, Inc. All rights reserved. */
package com.mycompany.vsphere_powercli.mvc;

/**
 * ObjectId encoding/decoding
 *
 */
public class ObjectIdUtil {
   // Forward slash must be encoded in URLs
   private static final String FORWARD_SLASH = "/";
   // Single encoding
   private static final String FORWARD_SLASH_ENCODED1 = "%2F";
   // Double encoding
   private static final String FORWARD_SLASH_ENCODED2 = "%252F";

   /**
    * Encode the given object id.
    *
    * @param objectId The objectId to be encoded
    * @return The encoded objectId
    */
   public static String encodeObjectId(String objectId) {
      if (objectId != null) {
         return objectId.replace(FORWARD_SLASH, FORWARD_SLASH_ENCODED2);
      }
      return objectId;
   }

   /**
    * Decode the given objectId when passed as a path variable in a Spring controller
    * (Spring already performs 1 level of decoding)
    *
    * @param objectId Encoded id
    * @return The decoded object id
    */
   public static String decodePathVariable(String objectId) {
      if (objectId != null) {
         return objectId.replace(FORWARD_SLASH_ENCODED1, FORWARD_SLASH);
      }
      return objectId;
   }

   /**
    * Decode the given objectId when passed as a URL parameter, i.e. reverse the
    * double encoding done by encodeObjectId.
    *
    * @param objectId Encoded id
    * @return The decoded object id
    */
   public static String decodeParameter(String objectId) {
      if (objectId != null) {
         return objectId.replace(FORWARD_SLASH_ENCODED2, FORWARD_SLASH);
      }
      return objectId;
   }

}

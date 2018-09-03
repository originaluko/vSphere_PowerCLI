/* Copyright (c) 2012-2018 VMware, Inc. All rights reserved. */
package com.mycompany.vsphere_powercli.services;

/**
 * Service handling some actions invoked from the UI
 *
 * It must be declared as osgi:service with the same name in
 * main/resources/META-INF/spring/bundle-context-osgi.xml
 */
public interface SampleActionService {
   /**
    * Sample action called on the server.
    *
    * @param objRef   Internal reference to the vCenter object for that action.
    */
   public void sampleAction1(Object objRef);

   /**
    * Sample action called on the server.
    *
    * @param objRef   Internal reference to the vCenter object for that action.
    * @return true is the action is successful, false otherwise.
    */
   public boolean sampleAction2(Object objRef);
}

/* Copyright (c) 2012-2018 VMware, Inc. All rights reserved. */
package com.mycompany.vsphere_powercli.services;

import com.vmware.vise.vim.data.VimObjectReferenceService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of the SampleActionService interface
 */
public class SampleActionServiceImpl implements SampleActionService {

   private static final Log _logger =
         LogFactory.getLog(SampleActionServiceImpl.class);

   // ObjectReferenceService which provides more info for vSphere objects
   private final VimObjectReferenceService _vimObjectReferenceService;

   /**
    *  Constructor used to inject the utility services (see the declaration
    *  in main/resources/spring/bundle-context-osgi.xml)
    *
    * @param vimObjectReferenceService
    *    Service to access vSphere object references information.
    */
   public SampleActionServiceImpl(
            VimObjectReferenceService vimObjectReferenceService) {
      _vimObjectReferenceService = vimObjectReferenceService;
   }

   public void sampleAction1(Object vmReference) {
      // All vCenter objects sent from the UI are serialized into an internal type.
      // You can use VimObjectReferenceService to get the right information.
      // See samples/vsphereviews/vsphere-wssdk-provider for an example using
      // the vSphere Web Service SDK to talk to vCenter.
      String type = _vimObjectReferenceService.getResourceObjectType(vmReference);
      String value = _vimObjectReferenceService.getValue(vmReference);
      _logger.info("sampleAction1 called with object type = " + type +
            ", value = " + value);

      // Note: the action processing should take place on the back-end Server,
      // nothing heavy should run in the vSphere Web Client server JVM!
      // If back-end processing takes time it's better to return right away here
      // and let the UI deals with updates later.
   }

   public boolean sampleAction2(Object vmReference) {
      String type = _vimObjectReferenceService.getResourceObjectType(vmReference);
      String value = _vimObjectReferenceService.getValue(vmReference);
      _logger.info("sampleAction2 called with object type = " + type +
            ", value = " + value);

      return true;
   }
}

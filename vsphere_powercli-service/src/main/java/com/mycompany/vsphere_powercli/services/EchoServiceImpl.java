/* Copyright (c) 2012-2018 VMware, Inc. All rights reserved. */
package com.mycompany.vsphere_powercli.services;

/**
 * Implementation of the EchoService interface
 */
public class EchoServiceImpl implements EchoService {

   /* (non-Javadoc)
    * @see com.mycompany.vsphere_powercli.EchoService#echo(java.lang.String)
    */
   public String echo(String message) {
      return message;
   }
}

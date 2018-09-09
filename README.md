# vSphere PowerCLI

vSphere PowerCLI was created for and entered into the VMworld 2018 Hackathon in Las Vegas.

vSphere PowerCLI is a small vSphere Client plugin that allows a PowerShell / PowerCLI session to be opened within the H5 browser-based vSphere Client.

vSphere PowerCLI is comprised of two components.  A Docker Image, running an AJAX web terminal emulator, along with PowerShell.  As well as a vSphere Client plugin that opens an iFrame window to the Docker Container running the web terminal emulator.

The plugin was created as a POC (Proof of Concept) to see if a PowerShell session could be run within the new HTML5 based vSphere Client.

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/01.jpg" width="500">

## Installation

### Prerequisites 
VMware vCenter Server Appliance 6.7 (internet access required on vCenter to download the plugin)

Docker host

### Configuring the Docker image
There are two options for installing the Docker Image.  **Option A** involves configuring and running a Docker Host on the VCSA 6.7 VM and building & running the vSphere PowerCLI image within the VCSA VM.  This would not be a VMware support method but would be fine in a Test / Dev environment not requiring any additional resources in your environment.  **Option B** involves building and running the image on a pre-built Docker Host.  

#### Option A
This option outlines the processes to install and configure Docker on VCSA 6.7.

##### Step 1.
SSH to the VCSA VM and enter the Shell

*Install the docker package*

`tdnf -y install docker`

##### Step 2.
Load the kernel module to start the Docker client. (This step needs to be re-run if the VCSA is rebooted)

`modprobe bridge --ignore-install`

If successful no information should be returned.

You can check if the module is installed by running the following 

`lsmod | grep bridge`

```bash
root@vc67-1 [ ~ ]# lsmod | grep bridge
bridge                118784  0
stp                    16384  1 bridge
llc                    16384  2 stp,bridge
```

##### Step 3.
*Enable and start the Docker Client*

`systemctl enable docker`

`systemctl start docker`

##### Step 4.
Copy the files Dockerfile and supervisord.conf from the project to a directory on the VCSA VM.  The two files should be located in the same directory.
If you have difficulties copying the two files from the project you can use curl to download them for an online repo of the project using the below two commands.

`curl -O https://labs.ukotic.net/vsphere_powercli/Dockerfile`

`curl -O https://labs.ukotic.net/vsphere_powercli/supervisord.conf`

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/09.jpg" width="500">
 
##### Step 5 (Optional).
Optionally copy the vSphere Client public and private keys to the certificate.pem file in the same location as the Dockerfile and supervisord.conf file.
If this step is skipped Line 31 of the Dockerfile needs to be removed.

`cat /etc/vmware-rhttpproxy/ssl/rui.key >> certificate.pem`

`cat /etc/vmware-rhttpproxy/ssl/rui.crt >> certificate.pem`

##### Step 6.
Build the Docker Image with the below command.

`docker build -t vsphere-powercli .`

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/10.jpg" width="500"> 

The build process will take a few minutes.  

##### Step 7.
Once complete start up a Docker container using this image.

Currently it’s recommended to run the container on the default port 4200

`docker run -d -p 4200:4200 --name vsphere-powercli vsphere-powercli`

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/11.jpg" width="500">

You can check if the docker container is running with the below command.

`docker stats`

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/12.jpg" width="500">
 
You can check if the port has been mapped correctly by running

`docker port vsphere-powercli` 

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/13.jpg" width="500">

##### Step 8.
Open a web browser and test if you can connect directly to the container

https://{my_vcsa}:4200

**Important**

Depending on whether you followed Step 5.  vSphere PowerCLI may generate a Self Signed Certificate.  As a result, you will be presented with a warning prompt to accept the certificate in Step 8.  You must accept this warning once at the start of your browser session prior to running the plugin in the vSphere Client.  This is due to iFrames being used and the web browser not prompting the user when an iFrame uses a Self Signed Certificate.

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/14.jpg" width="500">

Once you accept any warnings you should receive a login prompt.

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/15.jpg" width="500">

Once a login prompt is received you have completed the build and configuration of the docker portion of the setup.

#### Option B
This option assumes you have a docker host already running in your environment.

##### Step 1.
On a host pre-configured with Docker.  Copy the files Dockerfile and supervisord.conf from the project to a directory on the Docker host.  The two files should be located in the same directory.

If you have difficulties copying the two files from the project to the Docker host you can use curl to download them from an online repo of the project using the below two commands.

`curl -O https://labs.ukotic.net/vsphere_powercli/Dockerfile`

`curl -O https://labs.ukotic.net/vsphere_powercli/supervisord.conf`

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/02.jpg" width="500">
 
##### Step 2.
The Dockerfile copies a certificate.pem file created in Option A.  This file is not used in the Option B method and needs to be removed.  Comment out or remove Line 31 in the Dockerfile.

`COPY certificate.pem /certificate.pem`

##### Step 3.
Build the Docker Image with the below command.

`docker build -t vsphere-powercli .`

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/03.jpg" width="500">
 
The build process will take a few minutes.  

##### Step 4.
Once complete start up a Docker container using this image.

Currently it’s recommended to run the container on the default port 4200

`docker run -d -p 4200:4200 --name vsphere-powercli vsphere-powercli`

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/04.jpg" width="500"> 

You can check is the docker container is running with the below command.

`docker stats`
 
<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/05.jpg" width="500">

You can check if the port has been mapped correctly by running

`docker port vsphere-powercli`
 
<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/06.jpg" width="500">

##### Step 5.
Open a web browser and test if you can connect directly to the container

`https://ip_of_container_host:4200`

**Important**

vSphere PowerCLI currently uses Self Signed Certificates in Option B.  As a result, you will be presented with a warning prompt to accept the certificate.  You must accept this warning once at the start of your browser session prior to running the plugin in the vSphere Client.  This is due to iFrames being used and the browser not prompting the user when an iFrame uses a Self Signed Certificate.

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/07.jpg" width="500">
 
Once you accept the warning you should receive a login prompt.

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/08.jpg" width="500"> 

Once a login prompt is received you have completed the build and configuration of the docker portion of the setup.


### Installing the vSphere PowerCLI plugin
Installing the plugin requires registering the plugin in the vSphere MOB (Managed Object Browser)

##### Step 1.
Open a web browser and browse to you VCSA

https://{your_vcsa}/mob/

Sign in with your SSO credentials

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/16.jpg" width="500"> 

##### Step 2.
Click on `content`

Click on `ExtensionManager`

Click on `RegisterExtension`

A new window similar to below will Open

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/17.jpg" width="500"> 

##### Step 3.
This step will install the java plugin package from an online repo.  You can modify the location and host the plugin from your own web server if you wish using the zip file vsphere_powercli_plugin.zip.  Else continue with the below steps.

Replace everything in the VALUE box with the contents of the vsphere_powercli_plugin.txt file that comes with the project.

* You can also view the contents of the file from the online repo at 

https://labs.ukotic.net/vsphere_powercli/vsphere_powercli_plugin.txt

Click `Invoke Method`

If a **void** is return the settings went through successfully.

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/18.jpg" width="500"> 

You can now close the registration window.

## Using the vSphere PowerCLI plugin

Log into the vSphere Web Client UI

https://{my_vcsa}/ui

When you login for the first time after a new plugin has been registered you may be prompted with a message informing you plugins have been installed or updated and you need to log out and log back in.

If so, click Logout in the top right under your login name.

Log back in again.

Click on **Menu -> Shortcuts** and you should now see a new icon called **vSphere PowerCLI**

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/19.jpg" width="500"> 

Click on the **vSphere PowerCLI** shortcut

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/20.jpg" width="500"> 

Enter in the IP of the docker host or VCSA where the docker container is deployed and click **Connect**

A login prompt should appear

Sign in with the user *powercli* and the password *password*.

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/21.jpg" width="500"> 

This should take you into a PowerShell prompt with installed PowerCLI modules.

At this point you should be able to run and execute PowerShell commands.  You should be able to make a connection to vCenter as per normal PowerShell processes to login to a vCenter.

<img src="https://github.com/originaluko/vSphere_PowerCLI/raw/master/images/22.jpg" width="500"> 

 
## Issues

**vSphere PowerCLI doesn’t connect and you are just left with a grey box.**

If the window is grey after you click save check if you can still browse directly to the container.   {https://ip_of_container_host:4200}

You may still need to accept the self signed cert warnings.  iframes don't present this warning and they need to be accepted for your vSphere Client browser session.

Try connecting again by clicking save.

**What is the Username and Password to login**

Username: powercli

Password: password

**How do I scroll up**

Press Ctrl + b, then use the Arrow Up / Down or Page Up / Page Down keys.

**How do I unregister the extension**

Browse to https://{your_vcsa}/mob/

Click on *UnregisterExtension*

Enter in the string value *com.mycompany.vsphere_powercli*

Click on *Invoke Method*

## Licensing

The MIT License (MIT)

Copyright (c) 2018 [ukotic.net](http://blog.ukotic.net)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

[![REUSE status](https://api.reuse.software/badge/github.com/SAP-samples/s4hana-ext-cloud-label-printing)](https://api.reuse.software/info/github.com/SAP-samples/s4hana-ext-cloud-label-printing)

# SAP S/4HANA Cloud Extensions: Label Printing Using SAP Print Service

This repository contains the sample code for the [Label Printing Using SAP Print Service tutorial](http://tiny.cc/s4-cloud-label-printing).

*This code is only one part of the tutorial, so please follow the tutorial before attempting to use this code.*

## Description

The [Label Printing Using SAP Print Service tutorial](http://tiny.cc/s4-cloud-label-printing) uses the SAP Print service to perform cloud printing. It showcases the printing of labels at a shipping point in the logistics department at a company, when goods are picked up from storage location and packed into packages for shipment. 

The label printing application retrieves delivery information from the SAP S/4HANA Cloud system. The shipping clerk can simply add the number of the packages they’ve created for each delivery line item. After choosing the print button, the system calculates the corresponding print labels based on delivery information and user input. 

The system triggers the SAP Forms service by Adobe to render the print form and sends the print request to the print queue. The labels are then printed on the local printer at the shipping point. The label contains the delivery number, position, material number, quantity (per package), and the package number. The same information is also encoded in a QR code which can be used to scan the information easily, by the customer, for example.

#### SAP Extensibility Explorer

This tutorial is one of multiple tutorials that make up the [SAP Extensibility Explorer](https://sap.com/extends4) for SAP S/4HANA Cloud.
SAP Extensibility Explorer is a central place where anyone involved in the extensibility process can gain insight into various types of extensibility options. At the heart of SAP Extensibility Explorer, there is a rich repository of sample scenarios which show, in a hands-on way, how to realize an extensibility requirement leveraging different extensibility patterns.

## Requirements

- An SAP Business Technology Platform subaccount in the Neo environment with the SAP Forms service by Adobe enabled.
- An SAP Business Technology Platform subaccount in the Cloud Foundry environment with the Print Service enabled.
- An SAP S/4HANA Cloud tenant. **This is a commercial paid product.**
- [Java SE 8 Development Kit (JDK)](https://www.oracle.com/technetwork/java/javase/downloads/index.html) to compile the Java application.
- [Apache Maven](http://maven.apache.org/download.cgi) to build the Java application.

## Download and Installation

This repository is a part of the [Download the Application](https://help.sap.com/viewer/24c083dc31104041a202933f34bd2af2/SHIP/en-US/29c512fa3d22440eac8ccbc4884cb277.html) step in the tutorial. Instructions for use can be found in that step.

[Please download the zip file by clicking here](https://github.com/SAP-samples/s4hana-ext-cloud-label-printing/archive/master.zip) so that the code can be used in the tutorial.

## Known Issues

If you are working with an SAP Business Technology Platform _Trial_ account, you must add the following 2 properties to the destination so that the connection to SAP S/4HANA Cloud works:

```
proxyHost = proxy-trial.od.sap.biz
proxyPort = 8080
```

## How to obtain support

[Create an issue](https://github.com/SAP-samples/s4hana-ext-cloud-label-printing/issues/new) in this repository if you find a bug or have questions about the content.

For additional support, [ask a question in SAP Community](https://answers.sap.com/questions/ask.html).

## License
Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved. This project is licensed under the Apache Software License, version 2.0 except as noted otherwise in the [LICENSE](LICENSES/Apache-2.0.txt) file.

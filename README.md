# T-NOVA SDN Control Plane Load Balancer

Load Balancer for the SDN Control traffic based on the OpenDaylight Beryllium release, developed within the T-NOVA EU research project.

![alt tag](https://dl.dropboxusercontent.com/u/4632161/ODL_LB.png)

## Description

Implementation of a Load Balancer in charge of balancing the control traffic between the network elements (i.e. Openflow switches) and a cluster of SDN controllers (i.e. OpenDaylight cluster).

This module works by exploiting the Controller Role messages introduced by Openflow v1.3 ([link](https://www.opennetworking.org/images/stories/downloads/sdn-resources/onf-specifications/openflow/openflow-spec-v1.3.0.pdf)). In OF1.3, switches connecting to multiple controllers can accept/send OF packets only from/to the MASTER (or EQUAL) controller(s).

The goal is to find the best switch-to-controller mapping in order to equally distribute the workload carried by each controller of the cluster. The Load Balancer monitors both the controllers machine resources and the OpenFlow control traffic on each controller. Then, it computes and applies the new mapping by dynamically changing the controller roles for each switch.

To this aim, the following ODL features have been extended:
  * OpenFlow Plugin
  * Forwarding rule manager
  * Statistics manager

In addition, a new feature, namely the Role Manager, has been introduced with the following modules
  * odl-rolemanager-api 
  * odl-rolemanager-impl

The Load Balancer has been developed as standalone Java application in charge of monitoring each controller's instances and its control traffic and provide the best switch-to-controller mapping.

## Requirements
  * Java 7 or 8 JDK;
  * Maven 3.1.1 or later
  * Edit your ~/.m2/settings.xml according to this [page](https://wiki.opendaylight.org/view/GettingStarted:Development_Environment_Setup)

Please refer to the official ODL documentation [here](https://wiki.opendaylight.org/view/GettingStarted:Development_Environment_Setup) for more information on how to setup the environment.

## Download the code
Type the following command: 
```sh
git clone https://github.com/CRAT-EU/T-NOVA.git`
```

## Build the code
#### OpenFlow Plugin (extended)
First of all, build the extended OpenFlow Plugin that will be included in a custom OpenDaylight distibution archive.
From the `T-NOVA/OpenflowPlugin/` repo's directory, run the following command: 

```sh
mvn clean install -DskipTests
```

#### Role Manager and the OpenDaylight distribution
From the `T-NOVA/ODLRoleManager/` repo's directory, run the following command: 

```sh
mvn clean install
```

### Load Balancer Application
From the `T-NOVA/CPLoadBalancer/` repo's directory, build the code with the following command: 

```sh
mvn clean install
```

## Deployment

### Deploy the OpenDaylight Cluster
In order to deploy the ODL cluster, browse to the `T-NOVA/Scripts/cluster-deployer/` repo's folder. Copy there the karaf distribution zip file from the `T-NOVA/ODLRoleManager/distribution-karaf/target` folder. 

Then, follow the instructions [here](https://github.com/CRAT-EU/T-NOVA/tree/master/Scripts/cluster-deployer) to deploy the cluster.

### Run the Load Balancer Application
Before starting the Load Balancer application, configure the cluster instances IPs and SSH user:password in the `config.ini` file. 

Then, execute the jar file built in the `T-NOVA/CPLoadBalancer/target` directory with the following command: 

```sh
java -jar tnovacplb-1.0-SNAPSHOT`
```

### Accessing the Load Balancer GUI
Open your browser at the following address: `http://localhost:8888/`.

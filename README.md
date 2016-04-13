# T-NOVA SDN Control Plane Load Balancer

Load Balancer for the SDN Control traffic based on the OpenDaylight Beryllium release, developed within the T-NOVA EU research project.

## Description

Implementation of a Load Balancer in charge of balancing the control traffic between the network elements (i.e. Openflow switches) and a cluster of SDN controllers (i.e. OpenDaylight cluster).

This module works by exploiting the Controller Role messages introduced by Openflow v1.3 ([link](https://www.opennetworking.org/images/stories/downloads/sdn-resources/onf-specifications/openflow/openflow-spec-v1.3.0.pdf)) by which every OF1.3 switch connecting to multiple controllers can accept/send OF packets only from/to the MASTER (or EQUAL) controller(s).

The goal is to find the best switch-to-controller mapping in order to equally distribute the workload carried by each controller of the cluster. The Load Balancer monitors both the controllers machine resources and the OpenFlow control traffic on each controller Then, it computes and install a new mapping by dynamically changing the controller roles for each switch.

The following ODL features have been extended:
  * OpenFlow Plugin
  * Forwarding rule manager
  * Statistics manager

A new feature, namely the Role Manager, has been introduced with the following modules
  * odl-rolemanager-api 
  * odl-rolemanager-impl

The Load Balancer has been developed as standalone Java application in charge of monitoring each controller's instances and its control traffic and provide the best switch-to-controller mapping.

## Requirements
  * Java 7 or 8 JDK;
  * Maven 3.1.1 or later
  * Edit your ~/.m2/settings.xml according to this [page](https://wiki.opendaylight.org/view/GettingStarted:Development_Environment_Setup)

Refer [here](https://wiki.opendaylight.org/view/GettingStarted:Development_Environment_Setup) for more information about how to setup your development environment.

## Getting the code
Pull the repository with the following command: `git clone https://github.com/CRAT-EU/T-NOVA.git`

## Build the code
#### OpenFlow Plugin (extended)
First of all, build the extended OpenFlow Plugin that will be included in the next OpenDaylight distibution archive.
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
In order to deploy the ODL cluster, browse to the `T-NOVA/Scripts/cluster-deployer/` repo's folder. Copy here the karaf distribution zip file build in the folder `T-NOVA/ODLRoleManager/distribution-karaf/target` and follow the instructions here [here](https://github.com/CRAT-EU/T-NOVA/tree/master/Scripts/cluster-deployer).

### Run the Load Balancer Application
In order to configure the load balancer application, change the cluster instances Ips and user:password into the `config.ini` file.
To run the Load Balancer, execute the jar file build within the `T-NOVA/CPLoadBalancer/target` directory with the following command: 

```sh
java -jar tnovacplb-1.0-SNAPSHOT`
```

### Accessing the Load Balancer GUI
Open your browser at the following address: `http://localhost:8888/`.

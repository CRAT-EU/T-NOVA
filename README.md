# T-NOVA SDN Control Plane Load Balancer

This is the T-NOVA SDN Control Plane Load Balancer repository. OpenDaylight Beryllium release has been chosen as the base SDN controller. Such software balances the control traffic exchanged between the network elements (i.e. Openflow switches) and a cluster of multiple  SDN controllers (i.e. OpenDaylight cluster).
It works by exploiting the Controller Role messages introduced by Openflow v1.3 (MASTER, EQUAL, SLAVE). Every OF1.3 switch connects to all the cluster’s controllers, while accepting/sending OF packets only from/to the MASTER (or EQUAL) controller(s)
The goal is to find the best switch-to-controller mapping in order to equally distribute the workload carried by each controller of the cluster. The Load Balancer monitors both the controllers machine resources and the OpenFlow control traffic on each controller Then, it computes and install a new mapping by dynamically changing the controller roles for each switch.

Different ODL features were extended:
  * OpenFlow Plugin
  * Forwarding rule manager
  * Statistics manager
New karaf module were developed:
  * odl-rolemanager-api and odl-rolemanager-impl

The Load Balancer software was developed as standalone Java application in charge of monitoring each controller's instances, monitor the control traffic and compute and apply the best switch-to-controller mapping.

## Requirements
  * Java 7 or 8 JDK;
  * Maven 3.1.1 or later
  * Edit your ~/.m2/settings.xml according to this [page](https://wiki.opendaylight.org/view/GettingStarted:Development_Environment_Setup)

Refer [here](https://wiki.opendaylight.org/view/GettingStarted:Development_Environment_Setup) for more information about how to setup your development environment.

## Getting the code
Pull the repository with the following command: `git clone https://github.com/CRAT-EU/T-NOVA.git`

## Build the code
### Build the extended OpenFlow Plugin
First of all, build the extended OpenFlow Plugin that will be included in the next OpenDaylight distibution archive.
From the `T-NOVA/OpenflowPlugin/` repo's directory, give the following command: `mvn clean install -DskipTests`
### Build the odl-rolemanafer-* features and the OpenDaylight distribution
From the `T-NOVA/ODLRoleManager/` repo's directory, give the following command: `mvn clean install`
### Build the Load Balancer
From the `T-NOVA/CPLoadBalancer/` repo's directory, build the code with the following command: `mvn clean install`

## Deployment

### Deploy the OpenDaylight Cluster
In order to deploy the ODL cluster, browse to the `T-NOVA/Scripts/cluster-deployer/` repo's folder. Copy here the karaf distribution zip file build in the folder `T-NOVA/ODLRoleManager/distribution-karaf/target` and following the istruction [here](https://github.com/CRAT-EU/T-NOVA/tree/master/Scripts/cluster-deployer).

### Run the Load Balancer Application
In order to configure the load balancer application, change the cluster instances Ips and user:password into the `config.ini` file.
To run the Load Balancer, simply execute the jar file build within the `T-NOVA/CPLoadBalancer/target` directory with the following command: `java -jar tnovacplb-1.0-SNAPSHOT`

### Accessing the Load Balancer GUI
Direct your browser at the following address: `http://localhost:8888/`.

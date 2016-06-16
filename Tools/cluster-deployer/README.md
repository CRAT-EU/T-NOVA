## Cluster Deployer Tool

Deploy a cluster of OpenDaylight controllers

### Requirements
 * Python 2.7, pip and paramiko are installed on the local host
 
 ```sh
  sudo apt-get install python2.7
  sudo apt-get install python-pip
  sudo apt-get install python-paramiko 
 ```
 * 3 Ubuntu 14.04 remote machines (VM or physical) accessible via SSH (user:odl/ password:odl)

### Configure

Edit the deploy_cluster.sh file in order to configure:
* distribution : the ODL distribution zip file to be deployes on the different cluster's machine
* hosts : comma separated list of IP addresses of the machines in charge of hosting each ODL controller
* user : SSH username for the remote hosts (equal for all hosts)
* password : SSH password for the remote hosts  (equal for all hosts)

### Run

Execute the command below from the ODL integration repo folder

  ```sh
  python deploy.py --clean --distribution=distribution-karaf-0.0.4-SNAPSHOT.zip --rootdir=/home/odl --hosts=192.168.1.2,192.168.1.3,192.168.1.1 --user=odl --password=odl --template=lb-test
  ```

where:
* distribution : the ODL distribution to be deployed
* hosts : comma separated list of IP addresses of the machines in charge of hosting each ODL controller
* user : SSH username for the remote host(s)
* password : SSH password for the remote host(s)

The Controller instances will be installed in /home/odl/deploy/current/odl/ folder of the respective remote hosts. To check the features that are installed, simply access via SSH to the machine and start the controller in the following way:

  ```sh
  ./bin/client -u karaf
  feature:list -i
  ```

## Cluster Deployer Tool

Deploy a cluster of OpenDaylight controllers

### Requirements
  * 3 Ubuntu 14.04 machines (VM or physical) accessible via SSH (user:odl/ password:odl)

Make sure Python 2.7 is installed on your host

  ```sh
  sudo apt-get install python2.7
  ```
  
Also, install pip and paramiko

  ```sh
  sudo apt-get install python-pip
  sudo apt-get install python-paramiko 
  ```

Then, run the command below where the Integration repo resides

  ```sh
  python deploy.py --clean --distribution=distribution-karaf-0.0.4-SNAPSHOT.zip --rootdir=/home/odl --hosts=192.168.1.2,192.168.1.3,192.168.1.1 --user=odl --password=odl --template=lb-test
  ```

where:
* hosts : comma separated list of IP addresses of the machines in charge of hosting each ODL controller
* user : SSH username for the remote host(s)
* password : SSH password for the remote host(s)


The Controller clusters will be deployed in /home/odl/deploy/current/odl/ folder in the respective remote hosts. To check the features that are installed, simply access via SSH to the machine and start the controller in the following way:

  ```sh
  ./bin/client -u karaf
  feature:list -i
  ```

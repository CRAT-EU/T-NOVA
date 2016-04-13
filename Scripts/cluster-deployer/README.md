## Cluster Deployer Tool

Deploy a cluster of OpenDaylight controllers

Make sure Python 2.7 is installed on your host

  ```sh
  sudo apt-get install python2.7
  ```
  
Also, install pip and paramiko

  ```sh
  sudo apt-get install python-pip
  sudo apt-get install python-paramiko 
  ```

Then, run the command below where the Integration repo resides (This is for Lithium distribution. If you are deploying with Helium, choose distribution-karaf-0.2.3-Helium-SR3.zip file).

  ```sh
  python deploy.py --clean --distribution=/user/local/distribution-karaf-0.X.Y-SNAPSHOT.zip --rootdir=/root --hosts=10.125.136.51, 10.125.136.52, 10.125.136.53 --user=foo --password=password --template=multi-node-test
  ```

The Controller clusters will be deployed in /root/deploy/current/odl/ folder in the respective machine hosts. To check the features that are installed, start the controller in the following way:

  ```sh
  ./bin/client -u karaf
  feature:list -i
  ```

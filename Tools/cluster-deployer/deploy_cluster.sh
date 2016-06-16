#!/bin/sh
CURR_DIR="$( cd "$( dirname "$0" )" && pwd )"
cd cluster-deployer
python deploy.py --clean --distribution=$CURR_DIR/distribution-karaf-0.0.4-SNAPSHOT.zip --rootdir=/home/odl --hosts=192.168.1.2,192.168.1.3,192.168.1.1 --user=odl --password=odl --template=lb-test

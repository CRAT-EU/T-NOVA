# T-NOVA SDN Control Plane Ansible Installation

This playbook provides the deployment of T-NOVA SDN Control Plane on Ubuntu 14 servers. 


## How to run

```sh
ansible-playbook -i inventory site.yml
```

## Files to edit
**File 1.** ansible_installation/inventory:
In **[openstack-controller]** enter openstack controller ip address, username and python interpreter to be used by ansible (the python interpreter must contain **shade** package - (pip install shade) )
```conf
[openstack-controller]
10.143.0.240 ansible_user=localadmin ansible_python_interpreter="~/ansible_venv/bin/python"

[load-balancer]

[odl-cluster]

```



**File 2.** ansible_installation/group_vars/all:

```yml
# Username for openstack virtual machines
GLOB_OS_SSH_USERNAME: ubuntu
# Password for ssh connection - Use "none" if private key is used
GLOB_OS_SSH_PASSWORD: none
# Local path where ssh private key is located
GLOB_OS_SSH_KEYPATH: /home/yanos/.ssh/os_key.pem
# Remote path where ssh private key will be saved
GLOB_OS_REMOTE_SSH_KEYPATH: /home/ubuntu/.ssh/prv_key
# Directory where python virtual environment containing ansible resides in openstack server
GLOB_OS_PYTHON_VENV: /home/localadmin/ansible_venv/bin/

# Specify Openstack VM names for provisioned ODL Cluster nodes - any number of items can be used
OPENDAYLIGHT_CLUSTER:
  - ansible-odl-node-1
  - ansible-odl-node-2

# Specify Openstack VM name for provisioned Load Balancer  
LOAD_BALANCER:
  - ansible-load-balancer

```


**File 3.** ansible_installation/roles/openstack-vm-provision/vars/main.yml:
```yml
# Openstack Identity URL
OS_AUTH_URL: http://10.143.0.240:5000/v2.0
# Openstack Username & Password
OS_USERNAME: admin
OS_PASSWORD: enter_password_here
# Openstack Tenant name
OS_TENANT_NAME: admin

OS_AUTH:
  auth_url: "{{ OS_AUTH_URL }}"
  username: "{{ OS_USERNAME }}"
  password: "{{ OS_PASSWORD }}"
  project_name: "{{ OS_TENANT_NAME }}"


# Openstack key pair name
KEY_NAME: os_key
# Openstack Internal network name for VM
INTERNAL_NETWORK: int-net
# Openstack Enternal network name for VM
EXTERNAL_NETWORK: ext-net
# Openstack Flavor name for VM
FLAVOR: m1.medium
# Openstack Image or Snapshot name for VM
OSIMG: trusty64cloud
# Variable to host ODL cluster nodes ips - leave empty
ip_list: []
```
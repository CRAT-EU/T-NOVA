#!/usr/bin/python

from mininet.net import Mininet
from mininet.node import Controller, OVSKernelSwitch, OVSSwitch, RemoteController
from mininet.cli import CLI
from mininet.log import setLogLevel, info
from mininet.topolib import TreeTopo
from functools import partial
from time import sleep
import random
import re
import time
from mininet.topo import Topo

class MultiSwitch( OVSKernelSwitch ):
    "Custom Switch() subclass that connects to all the controllers"
    def start( self, controllers ):
        return OVSKernelSwitch.start( self, controllers )

class SingleSwitchTopo(Topo):
        "Single switch connected to n hosts."
        def __init__(self, n=2, **opts):
            # Initialize topology and default options
            Topo.__init__(self, **opts)
            # Python's range(N) generates 0..N-1
            for h in range(n):
                switch = self.addSwitch('s%s' % (h + 1))


def parsePing( pingOutput ):
    "Parse ping output and return all data."
    errorTuple = (1, 0, 0, 0, 0, 0)
    # Check for downed link
    r = r'[uU]nreachable'
    m = re.search( r, pingOutput )
    if m is not None:
        return errorTuple
    r = r'(\d+) packets transmitted, (\d+) received'
    m = re.search( r, pingOutput )
    if m is None:
        error( '*** Error: could not parse ping output: %s\n' %
               pingOutput )
        return errorTuple
    sent, received = int( m.group( 1 ) ), int( m.group( 2 ) )
    r = r'rtt min/avg/max/mdev = '
    r += r'(\d+\.\d+)/(\d+\.\d+)/(\d+\.\d+)/(\d+\.\d+) ms'
    m = re.search( r, pingOutput )
    if m is None:
        if received == 0:
            return errorTuple
        error( '*** Error: could not parse ping output: %s\n' %
               pingOutput )
        return errorTuple
    rttmin = float( m.group( 1 ) )
    rttavg = float( m.group( 2 ) )
    rttmax = float( m.group( 3 ) )
    rttdev = float( m.group( 4 ) )
    return sent, received, rttmin, rttavg, rttmax, rttdev

def ping(h1, h2):
   result = h1.cmd('ping -c1 %s' % h2.IP())
   return parsePing(result)

def myNet():
   MultiSwitch13 = partial( MultiSwitch, protocols='OpenFlow13' )
   #tree_topo = TreeTopo(depth=3,fanout=2)
   tree_topo = SingleSwitchTopo(n=14)

   net = Mininet(controller=RemoteController, topo=tree_topo, switch=MultiSwitch13, build=False, autoSetMacs=True)

   info( '*** Adding controllers\n')
   #c1 = net.addController('c1', controller=RemoteController, ip="127.0.0.1", port=6633)
   c1 = net.addController('c1', controller=RemoteController, ip="192.168.1.1", port=6633)
   c2 = net.addController('c2', controller=RemoteController, ip="192.168.1.2", port=6633)
   c3 = net.addController('c3', controller=RemoteController, ip="192.168.1.3", port=6633)

#   info( '*** Add hosts\n')
#   h1 = net.addHost( 'h1', ip='10.0.0.1' )
#   h2 = net.addHost( 'h2', ip='10.0.0.2' )
#   h3 = net.addHost( 'h3', ip='10.0.0.3' )
#   h4 = net.addHost( 'h4', ip='10.0.0.4' )

#   info( '*** Add switches\n')
#   s1 = net.addSwitch( 's1', cls=OVSKernelSwitch, protocols='OpenFlow13' )
#   s2 = net.addSwitch( 's2', cls=OVSKernelSwitch, protocols='OpenFlow13' )
#   s3 = net.addSwitch( 's3', cls=OVSKernelSwitch, protocols='OpenFlow13' )
#   s4 = net.addSwitch( 's4', cls=OVSKernelSwitch, protocols='OpenFlow13' )

#   info( '*** Add links\n')
#   s1.linkTo( h1 )
#   s1.linkTo( s2 )
#   s2.linkTo( h2 )
#   s2.linkTo( s3 )
#   s3.linkTo( h3 )
#   s3.linkTo( s4 )
#   s4.linkTo( h4 )

   info( '*** Starting network\n')
   net.build()

   info( '*** Starting controllers\n')
   c1.start()
   c2.start()
   c3.start()

#   info( '*** Starting switches\n')
#   s1.start([c1,c2,c3])
#   s2.start([c1,c2,c3])
#   s3.start([c1,c2,c3])
#   s4.start([c1,c2,c3])

   net.start()
   net.staticArp()
#   i = 0;
#   while i < 10:
#     h1, h2  = random.choice(net.hosts), random.choice(net.hosts)
#     print h1.IP(), "-->", h2.IP()
#     sent, received, rttmin, rttavg, rttmax, rttdev = ping(h1, h2)
#     print received,"/",sent
#     i = i + 1
#     sleep(1)
   CLI( net )
   net.stop()

if __name__ == '__main__':
    setLogLevel( 'info' )
    myNet()

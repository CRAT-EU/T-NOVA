/**
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowplugin.applications.topology.manager;

import com.google.common.base.Optional;
import java.util.Collections;
import java.util.List;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TpId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyManagerUtil {

    private static final Logger LOG = LoggerFactory.getLogger(TopologyManagerUtil.class);

    static void removeAffectedLinks(final NodeId id, final ReadWriteTransaction transaction, InstanceIdentifier<Topology> topology) {
        Optional<Topology> topologyOptional = Optional.absent();
        try {
            topologyOptional = transaction.read(LogicalDatastoreType.OPERATIONAL, topology).checkedGet();
        } catch (ReadFailedException e) {
            LOG.warn("Error reading topology data for topology {}: {}", topology, e.getMessage());
            LOG.debug("Error reading topology data for topology.. ", e);
        }
        if (topologyOptional.isPresent()) {
            removeAffectedLinks(id, topologyOptional, transaction, topology);
        }
    }

    static void removeAffectedLinks(final NodeId id, Optional<Topology> topologyOptional, ReadWriteTransaction transaction, final InstanceIdentifier<Topology> topology) {
        if (!topologyOptional.isPresent()) {
            return;
        }

        List<Link> linkList = topologyOptional.get().getLink() != null ?
                topologyOptional.get().getLink() : Collections.<Link> emptyList();
        for (Link link : linkList) {
            if (id.equals(link.getSource().getSourceNode()) ||
                    id.equals(link.getDestination().getDestNode())) {
                transaction.delete(LogicalDatastoreType.OPERATIONAL, linkPath(link, topology));
            }
        }
    }

    static void removeAffectedLinks(final TpId id, final ReadWriteTransaction transaction, final InstanceIdentifier<Topology> topology) {
        Optional<Topology> topologyOptional = Optional.absent();
        try {
            topologyOptional = transaction.read(LogicalDatastoreType.OPERATIONAL, topology).checkedGet();
        } catch (ReadFailedException e) {
            LOG.warn("Error reading topology data for topology {}: {}", topology, e.getMessage());
            LOG.debug("Error reading topology data for topology..", e);
        }
        if (topologyOptional.isPresent()) {
            removeAffectedLinks(id, topologyOptional, transaction, topology);
        }
    }

    static void removeAffectedLinks(final TpId id, Optional<Topology> topologyOptional, ReadWriteTransaction transaction, final InstanceIdentifier<Topology> topology) {
        if (!topologyOptional.isPresent()) {
            return;
        }

        List<Link> linkList = topologyOptional.get().getLink() != null
                ? topologyOptional.get().getLink() : Collections.<Link> emptyList();
        for (Link link : linkList) {
            if (id.equals(link.getSource().getSourceTp()) ||
                    id.equals(link.getDestination().getDestTp())) {
                transaction.delete(LogicalDatastoreType.OPERATIONAL, linkPath(link, topology));
            }
        }
    }

    static InstanceIdentifier<Link> linkPath(final Link link, final InstanceIdentifier<Topology> topology) {
        return topology.child(Link.class, link.getKey());
    }


}

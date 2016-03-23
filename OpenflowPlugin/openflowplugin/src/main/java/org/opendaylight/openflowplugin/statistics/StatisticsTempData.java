/**
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowplugin.statistics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class StatisticsTempData {
    
    public static Map<String, OFNodeStatsCounters> OFNodesStatsCounters = new ConcurrentHashMap<String, OFNodeStatsCounters>();

}

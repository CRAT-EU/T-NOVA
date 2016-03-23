/**
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.openflowplugin.statistics;

import java.math.BigInteger;

import org.opendaylight.openflowplugin.openflow.md.core.sal.ModelDrivenSwitchImpl;
import org.slf4j.Logger;

public class OFNodeStatsCounters{
   
    

    long tsLastSend = 0, tsLastReceived;
    BigInteger countSend = BigInteger.ZERO;
    BigInteger countReceived = BigInteger.ZERO;
    
    public OFNodeStatsCounters(){
        tsLastSend = System.currentTimeMillis();
        tsLastReceived = System.currentTimeMillis();
        countSend = BigInteger.ONE;
        countReceived = BigInteger.ONE;
    }
    
    public void incrementCounterSend(){
        countSend = countSend.add(BigInteger.ONE);
        tsLastSend = System.currentTimeMillis();
    }
    
    public void incrementCounterReceived(){
        countReceived.add(BigInteger.ONE);
        tsLastReceived = System.currentTimeMillis();
    }
    
    public void resetCounters(){
        countSend = BigInteger.ZERO;
        countReceived = BigInteger.ZERO;
        tsLastSend = System.currentTimeMillis();
        tsLastReceived = System.currentTimeMillis();
    }

    public long getTsLastSend() {
        return tsLastSend;
    }

    public void setTsLastSend(long tsLastSend) {
        this.tsLastSend = tsLastSend;
    }

    public long getTsLastReceived() {
        return tsLastReceived;
    }

    public void setTsLastReceived(long tsLastReceived) {
        this.tsLastReceived = tsLastReceived;
    }

    public BigInteger getCountSend() {
        return countSend;
    }

    public void setCountSend(BigInteger countSend) {
        this.countSend = countSend;
    }

    public BigInteger getCountReceived() {
        return countReceived;
    }

    public void setCountReceived(BigInteger countReceived) {
        this.countReceived = countReceived;
    }
    
    @Override
    public String toString() {
        return "OFNodeStatsCounters [tsLastSend=" + tsLastSend + ", tsLastReceived=" + tsLastReceived + ", countSend="
                + countSend + ", countReceived=" + countReceived + "]";
    }
       
}
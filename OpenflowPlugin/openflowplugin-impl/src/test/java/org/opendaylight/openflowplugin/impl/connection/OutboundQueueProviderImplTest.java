package org.opendaylight.openflowplugin.impl.connection;

import junit.framework.TestCase;
import org.junit.Test;
import org.opendaylight.openflowjava.protocol.api.connection.OutboundQueue;
import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.protocol.rev130731.BarrierInput;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OutboundQueueProviderImplTest extends TestCase {

    private static final Long DUMMY_ENTRY_NUMBER = 44L;
    private static final Long DUMMY_XID = 55L;

    private final OutboundQueueProviderImpl outboundQueueProvider = new OutboundQueueProviderImpl(OFConstants.OFP_VERSION_1_3);

    @Test
    public void testReserveEntry() throws Exception {

        outboundQueueProvider.onConnectionQueueChanged(null);
        Long returnValue = outboundQueueProvider.reserveEntry();
        assertEquals(null, returnValue);

        OutboundQueue mockedQueue = mock(OutboundQueue.class);
        when(mockedQueue.reserveEntry()).thenReturn(DUMMY_ENTRY_NUMBER);
        outboundQueueProvider.onConnectionQueueChanged(mockedQueue);
        returnValue = outboundQueueProvider.reserveEntry();
        assertEquals(DUMMY_ENTRY_NUMBER, returnValue);
    }

    @Test
    public void testCreateBarrierRequest() {
        final BarrierInput barrierRequest = outboundQueueProvider.createBarrierRequest(DUMMY_XID);
        assertNotNull(barrierRequest);;
        assertEquals((short)OFConstants.OFP_VERSION_1_3, (short)barrierRequest.getVersion());
        assertEquals(DUMMY_XID, barrierRequest.getXid());
    }
}
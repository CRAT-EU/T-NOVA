/*
 * Copyright (c) 2015 Sapienza University of Rome.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.rolemanager;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.openflowplugin.openflow.md.util.RoleUtil;
import org.opendaylight.openflowplugin.statistics.OFNodeStatsCounters;
import org.opendaylight.openflowplugin.statistics.StatisticsTempData;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.GetRolemanagerStatusOutput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.GetRolemanagerStatusOutputBuilder;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.GetSwitchRoleInput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.GetSwitchRoleOutput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.GetSwitchRoleOutputBuilder;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.GetSwitchStatsInput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.GetSwitchStatsOutput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.GetSwitchStatsOutputBuilder;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.Rolemanager;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.Rolemanager.RolemanagerStatus;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.RolemanagerBuilder;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.RolemanagerService;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.SetSwitchRoleInput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.SetSwitchRoleOutput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.SetSwitchRoleOutputBuilder;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.StartRolemanagerInput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.StartRolemanagerOutput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.StartRolemanagerOutputBuilder;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.StopRolemanagerOutput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.rolemanager.rev150901.StopRolemanagerOutputBuilder;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RolemanagerImpl implements BindingAwareProvider,
        DataChangeListener,
        AutoCloseable,
        RolemanagerService{


    private static final Logger LOG = LoggerFactory.getLogger(RolemanagerImpl.class);
    private static String TAG = "Rolemanager";
    private ProviderContext providerContext;
    private DataBroker dataBroker;
    private ListenerRegistration<DataChangeListener> dcReg;
    private BindingAwareBroker.RpcRegistration<RolemanagerService> rpcReg;
    public static final InstanceIdentifier<Rolemanager> ROLEMANAGER_IID = InstanceIdentifier.builder(Rolemanager.class).build();


    @Override
    public void close() throws Exception {
        dcReg.close();
        rpcReg.close();
        LOG.info("Registrations closed");
    }



    @Override
    public void onSessionInitiated(ProviderContext session) {
        this.providerContext = session;
        this.dataBroker = session.getSALService(DataBroker.class);
        dcReg = dataBroker.registerDataChangeListener(
                LogicalDatastoreType.CONFIGURATION, ROLEMANAGER_IID, this,
                DataChangeScope.SUBTREE);
        rpcReg = session.addRpcImplementation(RolemanagerService.class, this);
        initRolemanagerOperational();
        initRolemanagerConfiguration();
        LOG.info("onSessionInitiated: initialization done");
    }




    @Override
    public void onDataChanged(final AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        DataObject dataObject = change.getUpdatedSubtree();
        if (dataObject instanceof Rolemanager) {
            Rolemanager rolemanager = (Rolemanager) dataObject;
            LOG.info("onDataChanged - new Rolemanager config: {}",
                    rolemanager);
        } else {
            LOG.warn("onDataChanged - not instance of Rolemanager {}",
                    dataObject);
        }
    }



    private void initRolemanagerOperational() {
        Rolemanager rolemanager = new RolemanagerBuilder().setRolemanagerStatus(RolemanagerStatus.Down).build();
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        tx.put(LogicalDatastoreType.OPERATIONAL, ROLEMANAGER_IID, rolemanager);
        Futures.addCallback(tx.submit(), new FutureCallback<Void>() {
            @Override
            public void onSuccess(final Void result) {
                LOG.info("initRolemanagerOperational: transaction succeeded");
            }
            @Override
            public void onFailure(final Throwable t) {
                LOG.error("initRolemanagerOperational: transaction failed");
            }
        });
        LOG.info("initRolemanagerOperational: operational status populated: {}", rolemanager);
    }



    private void initRolemanagerConfiguration() {
        Rolemanager rolemanager = new RolemanagerBuilder()/*.setDarknessFactor((long) 1000)*/.build();
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        tx.put(LogicalDatastoreType.CONFIGURATION, ROLEMANAGER_IID, rolemanager);
        tx.submit();
        LOG.info("initRolemanagerConfiguration: default config populated: {}", rolemanager);
    }






    @Override
    public Future<RpcResult<StartRolemanagerOutput>> startRolemanager(StartRolemanagerInput input) {
        LOG.info("Starting Rolemanager...");
        LOG.info("Write rolemanager status in datastore");
        Rolemanager rolemanager = new RolemanagerBuilder().setRolemanagerStatus(RolemanagerStatus.Up).build();
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        tx.put(LogicalDatastoreType.OPERATIONAL, ROLEMANAGER_IID, rolemanager);
        //
        //TODO
        //
        //
        //
        StartRolemanagerOutputBuilder slbob = new StartRolemanagerOutputBuilder();
        slbob.setResponseCode(1L);
        slbob.setResponseMessage("Rolemanager started");
        LOG.info(TAG, "Rolemanager started!");
        return RpcResultBuilder.success(slbob.build()).buildFuture();
    }





    @Override
    public Future<RpcResult<GetRolemanagerStatusOutput>> getRolemanagerStatus() {
        LOG.info("Get Rolemanager status started...");
        LOG.info("Reading Rolemanager status");
        ReadOnlyTransaction tx = dataBroker.newReadOnlyTransaction();
        Optional<Rolemanager> rolemanager = null;
        GetRolemanagerStatusOutputBuilder glbsob = new GetRolemanagerStatusOutputBuilder();
        try {
            rolemanager = tx.read(LogicalDatastoreType.OPERATIONAL, ROLEMANAGER_IID).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Error when retrieving the Rolemanager status");
            glbsob.setResponseCode(-1L);
        }
        if(rolemanager!=null && rolemanager.isPresent()){
            LOG.error("Rolemanager status null or not present");
            long status = rolemanager.get().getRolemanagerStatus().getIntValue();
            glbsob.setResponseCode(status);
        }
        else
            glbsob.setResponseCode(-1L);
        LOG.info("Returing Rolemanager status");
        return RpcResultBuilder.success(glbsob.build()).buildFuture();
    }






    @Override
    public Future<RpcResult<StopRolemanagerOutput>> stopRolemanager() {
        LOG.info("Stopping Rolemanager...");
        Rolemanager rolemanager = new RolemanagerBuilder().setRolemanagerStatus(RolemanagerStatus.Down).build();
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        tx.put(LogicalDatastoreType.OPERATIONAL, ROLEMANAGER_IID, rolemanager);
        //
        //TODO
        //
        //
        //
        StopRolemanagerOutputBuilder slbob = new StopRolemanagerOutputBuilder();
        slbob.setResponseCode(1L);
        LOG.info("Rolemanager stopped!");
        return RpcResultBuilder.success(slbob.build()).buildFuture();
    }




    @Override
    public Future<RpcResult<SetSwitchRoleOutput>> setSwitchRole(SetSwitchRoleInput input) {
        String s = "Set switches role stated, requested role: "+input.getOfpRole()+"on dpIDs: "+input.getSwitchIds().toArray().toString();
        LOG.info(s);
        String reqRole = input.getOfpRole();
        if(!reqRole.equals("NOCHANGE") &&
                !reqRole.equals("BECOMEMASTER") &&
                !reqRole.equals("BECOMESLAVE") &&
                !reqRole.equals("BECOMEEQUAL")){
            String ss = "Error while parsing the request's role, received: "+reqRole;
            LOG.error(ss);
            SetSwitchRoleOutputBuilder swrob = new SetSwitchRoleOutputBuilder();
            swrob.setResponseCode(-1L);
            swrob.setResponseMessage("Error while parsing the request's role");
            return RpcResultBuilder.success(swrob.build()).buildFuture();
        }
        if(input.getSwitchIds().size()==0){
            LOG.warn("Requested role change is empty dpIDs list, nothing to do...");
            SetSwitchRoleOutputBuilder swrob = new SetSwitchRoleOutputBuilder();
            swrob.setResponseCode(-1L);
            swrob.setResponseMessage("OK, empty dpIDs list");
            return RpcResultBuilder.success(swrob.build()).buildFuture();
        }
        //input valided -> fire role change
        int ofpRole = 0;
        if(reqRole.equals("BECOMEMASTER"))
            ofpRole=1;
        if(reqRole.equals("BECOMESLAVE"))
            ofpRole=2;
        if(reqRole.equals("BECOMEEQUAL"))
            ofpRole=3;
        RoleUtil.fireRoleChange(ofpRole, input.getSwitchIds());
        SetSwitchRoleOutputBuilder swrob = new SetSwitchRoleOutputBuilder();
        swrob.setResponseCode(0L);
        swrob.setResponseMessage("Switch(es) role changed");
        LOG.info("Switch(es) role changed");
        return RpcResultBuilder.success(swrob.build()).buildFuture();
    }



    @Override
    public Future<RpcResult<GetSwitchRoleOutput>> getSwitchRole(GetSwitchRoleInput input) {
        if(input.getSwitchIds().size()==0)
            LOG.info("Getting all switches roles started");
        else{
            String a = "Getting roles for the following OF swithes: "+input.getSwitchIds().toArray().toString();
            LOG.info(a);
        }
        List<String> dpRoles = new ArrayList<String>();
        Map<String, String> swsRoles = RoleUtil.getSwitchesRoles();
        if(swsRoles==null){
            LOG.error("Error while retieving the switches roles");
            GetSwitchRoleOutputBuilder gsrob = new GetSwitchRoleOutputBuilder();
            gsrob.setResponseCode(-1L);
            gsrob.setResponseMessage(new ArrayList<String>());
            return RpcResultBuilder.success(gsrob.build()).buildFuture();
        }
        //return all...
        if(input.getSwitchIds().size()==0){
            for(String r : swsRoles.keySet()){
                dpRoles.add(r.toString()+":"+swsRoles.get(r));
            }
            GetSwitchRoleOutputBuilder gsrob = new GetSwitchRoleOutputBuilder();
            gsrob.setResponseCode(0L);
            gsrob.setResponseMessage(dpRoles);
            LOG.info("Get switches role completed");
            return RpcResultBuilder.success(gsrob.build()).buildFuture();
        }
        //selective return...
        for(String r : swsRoles.keySet()){
            boolean found = false;
            if(input.getSwitchIds().contains(r.toString())){
                found = true;
                dpRoles.add(r.toString()+":"+swsRoles.get(r));
            }
            if(!found){
                LOG.error("Error while retieving the roles, switched IDs not found");
                GetSwitchRoleOutputBuilder gsrob = new GetSwitchRoleOutputBuilder();
                gsrob.setResponseCode(-1L);
                gsrob.setResponseMessage(new ArrayList<String>());
                return RpcResultBuilder.success(gsrob.build()).buildFuture();
            }
        }
        GetSwitchRoleOutputBuilder gsrob = new GetSwitchRoleOutputBuilder();
        gsrob.setResponseCode(0L);
        gsrob.setResponseMessage(dpRoles);
        LOG.info("Get switches role completed");
        return RpcResultBuilder.success(gsrob.build()).buildFuture();
    }



//    @Override
//    public Future<RpcResult<GetSwitchStatisticsOutput>> getSwitchStatistics(final GetSwitchStatisticsInput input) {
//        StatisticsTempData.OFNodesStatsCounters
//        try{
//            LOG.info("Reading OF nodes stats from the datastore...");
//            final InstanceIdentifier<OfStatistics> NODEOFSTATS_IID = InstanceIdentifier.builder(OfStatistics.class).build();
//            ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
//            ListenableFuture<Optional<OfStatistics>> dataFuture = readTx.read(LogicalDatastoreType.OPERATIONAL, NODEOFSTATS_IID);
//
//            Futures.addCallback(dataFuture, new FutureCallback<Optional<OfStatistics>>() {
//                @Override
//                public void onSuccess(final Optional<OfStatistics> result) {
//                    if(result.isPresent()) {
//                        OfStatistics ofs = result.get();
//                        lofn = new ArrayList<OfNode>();
//                        OfNodeBuilder ofn = null;
//                        if(input.getSwitchIds().size()==0){
//                            for(org.opendaylight.yang.gen.v1.urn.opendaylight.nodes.statistics.rev160114.ofstatistics.OfNode n : ofs.getOfNode()){
//                                CounterBuilder cb = new CounterBuilder();
//                                List<Counter> lc = new ArrayList<Counter>();
//                                for(org.opendaylight.yang.gen.v1.urn.opendaylight.nodes.statistics.rev160114.ofstatistics.ofnode.Counter c : n.getCounter()){
//                                    cb.setCounterCount(c.getCounterCount())
//                                        .setCounterFirstPacketTs(c.getCounterFirstPacketTs())
//                                        .setLastCounterCount(c.getLastCounterCount())
//                                        .setLastCounterFirstPacketTs(c.getLastCounterFirstPacketTs())
//                                        .setMsgType(c.getMsgType());
//                                    lc.add(cb.build());
//                                }
//                                ofn = new OfNodeBuilder();
//                                ofn.setNodeId(n.getNodeId());
//                                ofn.setCounter(lc);
//                                lofn.add(ofn.build());
//                            }
//                        }
//                        else{
//                            for(org.opendaylight.yang.gen.v1.urn.opendaylight.nodes.statistics.rev160114.ofstatistics.OfNode n : ofs.getOfNode()){
//                                if(input.getSwitchIds().contains(n.getNodeId())){
//                                    CounterBuilder cb = new CounterBuilder();
//                                    List<Counter> lc = new ArrayList<Counter>();
//                                    for(org.opendaylight.yang.gen.v1.urn.opendaylight.nodes.statistics.rev160114.ofstatistics.ofnode.Counter c : n.getCounter()){
//                                        cb.setCounterCount(c.getCounterCount())
//                                            .setCounterFirstPacketTs(c.getCounterFirstPacketTs())
//                                            .setLastCounterCount(c.getLastCounterCount())
//                                            .setLastCounterFirstPacketTs(c.getLastCounterFirstPacketTs())
//                                            .setMsgType(c.getMsgType());
//                                        lc.add(cb.build());
//                                    }
//                                    ofn = new OfNodeBuilder();
//                                    ofn.setNodeId(n.getNodeId());
//                                    ofn.setCounter(lc);
//                                    lofn.add(ofn.build());
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    else {
//                        LOG.info("OF Node stats not present into datastore");
//                    }
//                }
//                @Override
//                public void onFailure(final Throwable t) {
//                        LOG.info("Failed when reading from datastore");
//                }
//            });
//            GetSwitchStatisticsOutputBuilder gsso = new GetSwitchStatisticsOutputBuilder();
//            gsso.setResponseCode(0L);
//            gsso.setOfNode(lofn);
//            return RpcResultBuilder.success(gsso.build()).buildFuture();
//        }
//        catch(Exception e){
//            LOG.error(e.getMessage());
//            GetSwitchStatisticsOutputBuilder gsso = new GetSwitchStatisticsOutputBuilder();
//            gsso.setResponseCode(-1L);
//            gsso.setOfNode(new ArrayList<OfNode>());
//            return RpcResultBuilder.success(gsso.build()).buildFuture();
//        }
//    }



    @Override
    public Future<RpcResult<GetSwitchStatsOutput>> getSwitchStats(GetSwitchStatsInput input) {
        if(input.getSwitchIds().size()==0)
            LOG.info("Getting all switches stats started");
        else{
            String s = "Getting stats for the following OF swithes: "+input.getSwitchIds().toArray().toString();
            LOG.info(s);
        }
        List<String> dpStats = new ArrayList<String>();
        if(StatisticsTempData.OFNodesStatsCounters==null){
            LOG.error("Error while retieving the switches stats");
            GetSwitchStatsOutputBuilder gsrob = new GetSwitchStatsOutputBuilder();
            gsrob.setResponseCode(-1L);
            gsrob.setResponseMessage(new ArrayList<String>());
            return RpcResultBuilder.success(gsrob.build()).buildFuture();
        }
        //return all...
        if(input.getSwitchIds().size()==0){
           for(String r : StatisticsTempData.OFNodesStatsCounters.keySet()){
                LOG.info(StatisticsTempData.OFNodesStatsCounters.get(r).toString());
                OFNodeStatsCounters ns = StatisticsTempData.OFNodesStatsCounters.get(r);
                dpStats.add(r.toString()+":"+ns.getCountSend()+":"+ns.getTsLastSend()+":"+ns.getCountReceived()+":"+ns.getTsLastReceived());
            }
            GetSwitchStatsOutputBuilder sb = new GetSwitchStatsOutputBuilder();
            sb.setResponseCode(0L);
            sb.setResponseMessage(dpStats);
            LOG.info("Get switches stats completed");
            return RpcResultBuilder.success(sb.build()).buildFuture();
        }
        //selective return...
        for(String r : StatisticsTempData.OFNodesStatsCounters.keySet()){
            boolean found = false;
            if(input.getSwitchIds().contains(r.toString())){
                LOG.info(StatisticsTempData.OFNodesStatsCounters.get(r).toString());
                found = true;
                OFNodeStatsCounters ns = StatisticsTempData.OFNodesStatsCounters.get(r);
                dpStats.add(r.toString()+":"+ns.getCountSend()+":"+ns.getTsLastSend()+":"+ns.getCountReceived()+":"+ns.getTsLastReceived());
            }
            if(!found){
                LOG.error("Error while retieving the stats, switched IDs not found");
                GetSwitchStatsOutputBuilder sb = new GetSwitchStatsOutputBuilder();
                sb.setResponseCode(-1L);
                sb.setResponseMessage(new ArrayList<String>());
                return RpcResultBuilder.success(sb.build()).buildFuture();
            }
        }
        GetSwitchStatsOutputBuilder sb = new GetSwitchStatsOutputBuilder();
        sb.setResponseCode(0L);
        sb.setResponseMessage(dpStats);
        LOG.info("Get switches stats completed");
        return RpcResultBuilder.success(sb.build()).buildFuture();
    }


}
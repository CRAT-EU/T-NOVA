package org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rolemanager.impl.config.rev141210;

import org.opendaylight.rolemanager.RolemanagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RolemanagerImplModule extends org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rolemanager.impl.config.rev141210.AbstractRolemanagerImplModule {

    private static final Logger LOG = LoggerFactory.getLogger(RolemanagerImplModule.class);

    public RolemanagerImplModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public RolemanagerImplModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rolemanager.impl.config.rev141210.RolemanagerImplModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        LOG.info("Creating a new Role ManagerLoadbalancer instance");
        RolemanagerImpl provider = new RolemanagerImpl();
        getBindingAwareBrokerDependency().registerProvider(provider);
        //getBindingAwareBrokerDependency().registerProvider(provider, null);
        return provider;
    }

}

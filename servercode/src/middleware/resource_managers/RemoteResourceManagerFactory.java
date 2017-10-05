package middleware.resource_managers;

import middleware.resource_managers.impl.RMIResourceManagerServerImpl;
import middleware.resource_managers.impl.TCPResourceManagerServerImpl;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.regex.Pattern;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class RemoteResourceManagerFactory {
    private RemoteResourceManagerImplementationTypes factoryType = RemoteResourceManagerImplementationTypes.RMI;

    public RemoteResourceManagerFactory(RemoteResourceManagerImplementationTypes facType) {
        this.factoryType = facType;
    }

    public AbstractRemoteResourceManager createRemoteResourceManager(String rmAddress) {
        if(factoryType == RemoteResourceManagerImplementationTypes.RMI) {
            return new RMIResourceManagerServerImpl(rmAddress);
        } else if(factoryType == RemoteResourceManagerImplementationTypes.TCP) {
            return new TCPResourceManagerServerImpl(rmAddress);
        }

        throw new NotImplementedException();
    }
}

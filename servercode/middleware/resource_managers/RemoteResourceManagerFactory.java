package middleware.resource_managers;

import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.impl.RMIResourceManagerServerImpl;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class RemoteResourceManagerFactory {
    private static final Pattern RMI_URL_PATTERN = Pattern.compile("^\\/\\/([^:]+):([0-9]+)\\/(.*)$");

    private RemoteResourceManagerImplementationTypes factoryType = RemoteResourceManagerImplementationTypes.RMI;

    public AbstractRemoteResourceManager createRemoteResourceManager(String rmAddress) {
        if(factoryType == RemoteResourceManagerImplementationTypes.RMI) {
            return new RMIResourceManagerServerImpl(rmAddress);
        }

        throw new NotImplementedException();
    }
}

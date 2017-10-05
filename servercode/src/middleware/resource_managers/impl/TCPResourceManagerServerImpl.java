package middleware.resource_managers.impl;

import ResImpl.ResourceManagerTCPClient;
import ResImpl.exceptions.RMBaseException;
import ResInterface.ResourceManager;
import middleware.exceptions.MiddlewareBaseException;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.RemoteResourceManagerImplementationTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jpoisson on 2017-10-05.
 */
public class TCPResourceManagerServerImpl extends AbstractRemoteResourceManager {
    private static final Pattern RMI_URL_PATTERN = Pattern.compile("^\\/\\/([^:]+):([0-9]+)\\/(.*)$");

    private ResourceManagerTCPClient rmClient;
    private String hostname;
    private Integer port;

    public TCPResourceManagerServerImpl(String rmAddress) {
        super(RemoteResourceManagerImplementationTypes.RMI);

        this.parseRMAddress(rmAddress);
        this.initializeRmClient();
    }

    private void parseRMAddress(String rmAddress) {
        Matcher rmAddressMatch = RMI_URL_PATTERN.matcher(rmAddress);
        if(!rmAddressMatch.matches()) {
            throw new MiddlewareBaseException("Malformed RMI RMAddress " + rmAddress);
        }

        this.hostname = rmAddressMatch.group(1);
        this.port = Integer.parseInt(rmAddressMatch.group(2));
    }

    private void initializeRmClient() {
        try {
            this.rmClient = new ResourceManagerTCPClient(this.hostname, this.port);
        } catch (RMBaseException e) {
            throw new MiddlewareBaseException("Unable to create the tcp rmClient", e);
        }
    }

    @Override
    public ResourceManager getResourceManager() {
        return this.rmClient;
    }
}

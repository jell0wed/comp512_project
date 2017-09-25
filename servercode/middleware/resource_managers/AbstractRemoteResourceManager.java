package middleware.resource_managers;

import ResInterface.ResourceManager;

/**
 * Created by jpoisson on 2017-09-25.
 */
public abstract class AbstractRemoteResourceManager {
    protected final RemoteResourceManagerTypes resourceManagerType;

    protected AbstractRemoteResourceManager(RemoteResourceManagerTypes type) {
        this.resourceManagerType = type;
    }

    public abstract ResourceManager getResourceManager();
}

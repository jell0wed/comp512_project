package middleware.resource_managers;


import ResInterface.BackendResourceManager;

/**
 * Created by jpoisson on 2017-09-25.
 */
public abstract class AbstractRemoteResourceManager {
    protected final RemoteResourceManagerImplementationTypes resourceManagerType;

    protected AbstractRemoteResourceManager(RemoteResourceManagerImplementationTypes type) {
        this.resourceManagerType = type;
    }

    public abstract BackendResourceManager getResourceManager();
}

package middleware.resource_managers;


import ResInterface.ResourceManager;

/**
 * Created by jpoisson on 2017-09-25.
 */
public abstract class AbstractRemoteResourceManager {
    protected final RemoteResourceManagerImplementationTypes resourceManagerType;
    protected boolean isAlive = true;

    protected AbstractRemoteResourceManager(RemoteResourceManagerImplementationTypes type) {
        this.resourceManagerType = type;
    }

    public abstract ResourceManager getResourceManager();

    public void setIsAliveStatus(boolean alive) {
        this.isAlive = alive;
    }

    public boolean isAlive() {
        return this.isAlive;
    }
}

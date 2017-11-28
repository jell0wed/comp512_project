package middleware.resource_managers;

/**
 * Created by jpoisson on 2017-09-25.
 */
public enum ResourceManagerReplicationTypes {
    FLIGHTS(ResourceManagerTypes.FLIGHTS_ONLY, ResourceManagerTypes.CARS_ONLY),
    CARS(ResourceManagerTypes.CARS_ONLY, ResourceManagerTypes.FLIGHTS_ONLY),
    ROOMS(ResourceManagerTypes.ROOMS_ONLY, ResourceManagerTypes.OTHERS),
    OTHERS(ResourceManagerTypes.OTHERS, ResourceManagerTypes.ROOMS_ONLY);

    private ResourceManagerTypes rmTypeEq;
    private ResourceManagerTypes replicationType;
    private ResourceManagerReplicationTypes(ResourceManagerTypes eq, ResourceManagerTypes replication) {
        this.rmTypeEq = eq;
        this.replicationType = replication;
    }

    public ResourceManagerTypes getReplicationType() {
        return replicationType;
    }

    public static ResourceManagerReplicationTypes fromResourceManagerTypes(ResourceManagerTypes type) {
        for(ResourceManagerReplicationTypes t: ResourceManagerReplicationTypes.values()) {
            if(t.rmTypeEq == type) {
                return t;
            }
        }

        return null;
    }
}

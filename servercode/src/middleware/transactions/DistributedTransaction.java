package middleware.transactions;

import middleware.resource_managers.ResourceManagerTypes;

import java.util.Hashtable;
import java.util.Map;

class DistributedTransaction {
    Map<ResourceManagerTypes, Integer> enlistedRms = new Hashtable<>();

}

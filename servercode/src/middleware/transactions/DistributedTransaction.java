package middleware.transactions;

import middleware.database.ICustomerDatabase;
import middleware.resource_managers.ResourceManagerTypes;

import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

class DistributedTransaction {
    Map<ResourceManagerTypes, Integer> enlistedRms = new Hashtable<>();
    Debouncer<Integer> timeToLive = null;
    Stack<Consumer<ICustomerDatabase>> reservationUndoLogs = new Stack<>();
}

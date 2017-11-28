package middleware;

import ResImpl.Trace;
import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import javafx.util.Pair;
import middleware.entities.CustomerReservations;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.ResourceManagerReplicationTypes;
import middleware.resource_managers.ResourceManagerTypes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class MiddlewareInterface implements ResourceManager {
    private MiddlewareServer middleware;

    public MiddlewareInterface(MiddlewareServer serverInstance) {
        this.middleware = serverInstance;
    }

    private void handleTransactionException(int transId, TransactionException e) {
        Trace.error("Transaction exception on transaction id " + transId + " : " + e.getMessage());
    }

    @Override
    public int startTransaction() throws RemoteException {
        int transId = this.middleware.getTransactionManager().openTransaction();
        return transId;
    }

    @Override
    public boolean commitTransaction(int transId) throws RemoteException {
        try {
            this.middleware.getTransactionManager().commitTransaction(transId);
            return true;
        } catch (TransactionException e) {
            this.handleTransactionException(transId, e);
            return false;
        }
    }

    @Override
    public boolean abortTransaction(int transId) throws RemoteException {
        try {
            this.middleware.getTransactionManager().abortTransaction(transId);
            return true;
        } catch (TransactionException e) {
            this.handleTransactionException(transId, e);
            return false;
        }
    }

    @Override
    public boolean shutdown() throws RemoteException {
        for(AbstractRemoteResourceManager rm : this.middleware.getAllRemoteResourceManager()) {
            rm.getResourceManager().shutdown();
        }

        return true;
    }

    private Collection<Map.Entry<ResourceManagerTypes, Integer>> getReplicationTransactionIds(int id, ResourceManagerTypes type) throws TransactionException {
        LinkedList<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = new LinkedList<>();
        ResourceManagerTypes replicationType = ResourceManagerReplicationTypes.fromResourceManagerTypes(type).getReplicationType();

        AbstractRemoteResourceManager rm = this.middleware.getRemoteResourceManagerForType(type);
        AbstractRemoteResourceManager replRm = this.middleware.getRemoteResourceManagerForType(replicationType);

        if(replRm.isAlive()) {
            int rmReplTransId = this.middleware.getTransactionManager().enlistResourceManager(id, replicationType);
            transactionIds.add(new AbstractMap.SimpleEntry<ResourceManagerTypes, Integer>(replicationType, rmReplTransId));
        }

        if(rm.isAlive()) {
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, type);
            transactionIds.add(new AbstractMap.SimpleEntry<ResourceManagerTypes, Integer>(type, rmTransId));
        }

        if(transactionIds.isEmpty()) {
            throw new RuntimeException("No alive transaction manager for this type.");
        }

        for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
            Trace.info("TransactionType = " + transId.getKey().toString() + ", TransactionId = " + transId.getValue());
        }

        return transactionIds;
    }

    private void handleServerError(ResourceManagerTypes serverType) {
        this.middleware.markDefectiveRemoteResourceManagerForType(serverType);
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.FLIGHTS_ONLY);

            Boolean result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .addFlight(transId.getValue(), flightNum, flightSeats, flightPrice);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.CARS_ONLY);

            Boolean result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .addCars(transId.getValue(), location, numCars, price);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.ROOMS_ONLY);

            Boolean result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .addRooms(transId.getValue(), location, numRooms, price);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }

    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.OTHERS);
            Integer firstCustomerId = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    AbstractRemoteResourceManager rm = this.middleware.getRemoteResourceManagerForType(transId.getKey());
                    if(firstCustomerId == null) {
                        firstCustomerId = rm.getResourceManager().newCustomer(transId.getValue());
                    } else {
                        rm.getResourceManager().newCustomer(transId.getValue(), firstCustomerId);
                    }
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            final Integer finalFirstCustomerId = firstCustomerId;
            MiddlewareCustomerDatabase.getInstance().createCustomer(finalFirstCustomerId);
            this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                middlewareCustomerDatabase.deleteCustomer(finalFirstCustomerId);
            });

            return finalFirstCustomerId;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.OTHERS);
            Boolean result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .newCustomer(transId.getValue(), cid);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            if(result) {
                MiddlewareCustomerDatabase.getInstance().createCustomer(cid);
                this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    middlewareCustomerDatabase.deleteCustomer(cid);
                });
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.FLIGHTS_ONLY);
            Boolean result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .deleteFlight(transId.getValue(), flightNum);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            if(result) {
                // make sure to delete from reservations
                Collection<Integer> affectedCustomers = MiddlewareCustomerDatabase.getInstance().deleteFlight(flightNum);
                this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    for(int customerId: affectedCustomers) {
                        middlewareCustomerDatabase.addReservedFlight(customerId, flightNum);
                    }
                });
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.CARS_ONLY);
            Boolean result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .deleteCars(transId.getValue(), location);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            if(result) {
                // make sure to delete from reservations
                Collection<Integer> affectedCustomers = MiddlewareCustomerDatabase.getInstance().deleteCar(location);
                this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    for(int customerId: affectedCustomers) {
                        middlewareCustomerDatabase.addReservedCar(customerId, "car-" + location);
                    }
                });
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.ROOMS_ONLY);
            Boolean result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .deleteRooms(transId.getValue(), location);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            if(result) {
                // make sure to delete from reservations
                Collection<Integer> affectedCustomers = MiddlewareCustomerDatabase.getInstance().deleteRoom(location);
                this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    for(int customerId: affectedCustomers) {
                        middlewareCustomerDatabase.addReservedRoom(customerId, location);
                    }
                });
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.OTHERS);
            Boolean deleteCustomerSuccess = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    deleteCustomerSuccess = this.middleware.
                            getRemoteResourceManagerForType(transId.getKey()).
                            getResourceManager().
                            deleteCustomer(transId.getValue(), customer);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            if(deleteCustomerSuccess) {
                CustomerReservations custReservations = MiddlewareCustomerDatabase.getInstance().getReservations(customer);

                // give back rooms
                Collection<Map.Entry<ResourceManagerTypes, Integer>> roomTransIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.ROOMS_ONLY);
                for(Map.Entry<ResourceManagerTypes, Integer> roomTransId: roomTransIds) {
                    try {
                        AbstractRemoteResourceManager roomRmManager = this.middleware.getRemoteResourceManagerForType(roomTransId.getKey());
                        for(String roomKey: custReservations.getBookedRooms()) {
                            roomRmManager.getResourceManager().updateReservedQuantities(roomTransId.getValue(), "room-" + roomKey, 1);
                        }
                    } catch (RemoteException e) {
                        this.handleServerError(roomTransId.getKey());
                    }
                }

                // give back cars
                Collection<Map.Entry<ResourceManagerTypes, Integer>> carTransIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.CARS_ONLY);
                for(Map.Entry<ResourceManagerTypes, Integer> carTransId: carTransIds) {
                    try {
                        AbstractRemoteResourceManager carRmManager = this.middleware.getRemoteResourceManagerForType(carTransId.getKey());
                        for(String carKey: custReservations.getBookedCars()) {
                            carRmManager.getResourceManager().updateReservedQuantities(carTransId.getValue(), "car-" + carKey, 1);
                        }
                    } catch (RemoteException e) {
                        this.handleServerError(carTransId.getKey());
                    }
                }

                // give back flights
                Collection<Map.Entry<ResourceManagerTypes, Integer>> flightTransIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.FLIGHTS_ONLY);
                for(Map.Entry<ResourceManagerTypes, Integer> flightTransId: flightTransIds) {
                    try {
                        AbstractRemoteResourceManager flightRmManager = this.middleware.getRemoteResourceManagerForType(flightTransId.getKey());
                        for(Integer flightKey: custReservations.getBookedFlights()) {
                            flightRmManager.getResourceManager().updateReservedQuantities(flightTransId.getValue(), "flight-" + String.valueOf(flightKey), 1);
                        }
                    } catch (RemoteException e) {
                        this.handleServerError(flightTransId.getKey());
                    }
                }

                this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    middlewareCustomerDatabase.createCustomer(customer, new CustomerReservations(custReservations));
                });
                MiddlewareCustomerDatabase.getInstance().deleteCustomer(customer);
            }

            return deleteCustomerSuccess;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.FLIGHTS_ONLY);
            Integer result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .queryFlight(transId.getValue(), flightNumber);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.CARS_ONLY);
            Integer result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .queryCars(transId.getValue(), location);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.ROOMS_ONLY);
            Integer result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .queryRooms(transId.getValue(), location);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.OTHERS);
            String customerInfo = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    customerInfo = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .queryCustomerInfo(transId.getValue(), customer);
                }  catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            CustomerReservations reservations = MiddlewareCustomerDatabase.getInstance().getReservations(customer);

            StringBuilder billBuilder = new StringBuilder();
            for(Integer flightId: reservations.getBookedFlights()) {
                int flightPrice = this.queryFlightPrice(id, flightId);
                billBuilder.append(String.format("- flight-%d $ %d", flightId, flightPrice)).append("\n");
            }

            for(String carLocation: reservations.getBookedCars()) {
                int carPrice = this.queryCarsPrice(id, carLocation);
                billBuilder.append(String.format("- car-%s $ %d", carLocation, carPrice)).append("\n");
            }

            for(String roomLocation: reservations.getBookedRooms()) {
                int roomPrice = this.queryCarsPrice(id, roomLocation);
                billBuilder.append(String.format("- room-%s $ %d", roomLocation, roomPrice)).append("\n");
            }

            return customerInfo + billBuilder.toString();
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return "";
        }
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.FLIGHTS_ONLY);
            Integer result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .queryFlightPrice(transId.getValue(), flightNumber);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.CARS_ONLY);
            Integer result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .queryCarsPrice(transId.getValue(), location);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.ROOMS_ONLY);
            Integer result = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    result = this.middleware
                            .getRemoteResourceManagerForType(transId.getKey())
                            .getResourceManager()
                            .queryRoomsPrice(transId.getValue(), location);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            return result;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.FLIGHTS_ONLY);
            Boolean reserveFlightSuccess = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    reserveFlightSuccess = this.middleware.
                            getRemoteResourceManagerForType(transId.getKey()).
                            getResourceManager().
                            updateReservedQuantities(transId.getValue(), "flight-" + flightNumber, -1);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            if(reserveFlightSuccess) {
                MiddlewareCustomerDatabase.getInstance().addReservedFlight(customer, flightNumber);
                middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    middlewareCustomerDatabase.deleteReservedFlight(customer, flightNumber);
                });
            }

            return reserveFlightSuccess;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.CARS_ONLY);
            Boolean reserveCarSuccess = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    reserveCarSuccess = this.middleware.
                            getRemoteResourceManagerForType(transId.getKey()).
                            getResourceManager().
                            updateReservedQuantities(transId.getValue(), "car-" + location, -1);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            if(reserveCarSuccess) {
                MiddlewareCustomerDatabase.getInstance().addReservedCar(customer, location);
                middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    middlewareCustomerDatabase.deleteReservedCar(customer, location);
                });
            }

            return reserveCarSuccess;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            Collection<Map.Entry<ResourceManagerTypes, Integer>> transactionIds = this.getReplicationTransactionIds(id, ResourceManagerTypes.ROOMS_ONLY);
            Boolean reserveRoomSuccess = null;
            for(Map.Entry<ResourceManagerTypes, Integer> transId: transactionIds) {
                try {
                    reserveRoomSuccess = this.middleware.
                            getRemoteResourceManagerForType(transId.getKey()).
                            getResourceManager().
                            updateReservedQuantities(transId.getValue(), "room-" + locationd, -1);
                } catch (RemoteException e) {
                    this.handleServerError(transId.getKey());
                }
            }

            if(reserveRoomSuccess) {
                MiddlewareCustomerDatabase.getInstance().addReservedRoom(customer, locationd);
                middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    middlewareCustomerDatabase.deleteReservedRoom(customer, locationd);
                });
            }

            return reserveRoomSuccess;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean bookCar, boolean bookRoom) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);

            // make sure to book the appropriate flights
            for(Object flightNoObj: flightNumbers) {
                Integer flightNo = Integer.parseInt((String) flightNoObj);
                if(this.queryFlight(id, flightNo) < 1) {
                    return false;
                }

                this.reserveFlight(id, customer, flightNo);
            }

            // make sure to book the appropriate cars
            if(bookCar) {
                if(this.queryCars(id, location) < 1) {
                    return false;
                }

                this.reserveCar(id, customer, location);
            }

            // make sure to book the appropriate rooms
            if(bookRoom) {
                if(this.queryRooms(id, location) < 1) {
                    return false;
                }

                this.reserveRoom(id, customer, location);
            }

            return true;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean updateReservedQuantities(int id, String key, int incQty) throws RemoteException {
        throw new NotImplementedException(); // operation is not available from the middleware!!
    }


}

package middleware;

import ResImpl.Trace;
import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import middleware.entities.CustomerReservations;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.ResourceManagerTypes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Vector;

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

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.FLIGHTS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                    .getResourceManager()
                    .addFlight(rmTransId, flightNum, flightSeats, flightPrice);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.CARS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                    .getResourceManager()
                    .addCars(rmTransId, location, numCars, price);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.ROOMS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                    .getResourceManager()
                    .addRooms(rmTransId, location, numRooms, price);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }

    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.OTHERS);

            AbstractRemoteResourceManager userRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS);
            int givenCustomerId = userRmManager.getResourceManager().newCustomer(rmTransId); // create the customer first at the others rm

            MiddlewareCustomerDatabase.getInstance().createCustomer(givenCustomerId);
            this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                middlewareCustomerDatabase.deleteCustomer(givenCustomerId);
            });
            return givenCustomerId;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.OTHERS);

            AbstractRemoteResourceManager userRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS);
            boolean newCustomerSuccess = userRmManager.getResourceManager().newCustomer(rmTransId, cid);

            if(newCustomerSuccess) {
                MiddlewareCustomerDatabase.getInstance().createCustomer(cid);
                this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                    middlewareCustomerDatabase.deleteCustomer(cid);
                });
            }

            return newCustomerSuccess;
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.FLIGHTS_ONLY);

            // make sure to delete from reservations
            Collection<Integer> affectedCustomers = MiddlewareCustomerDatabase.getInstance().deleteFlight(flightNum);
            this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                for(int customerId: affectedCustomers) {
                    middlewareCustomerDatabase.addReservedFlight(customerId, flightNum);
                }
            });

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                    .getResourceManager()
                    .deleteFlight(rmTransId, flightNum);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.CARS_ONLY);

            // make sure to delete from reservations
            Collection<Integer> affectedCustomers = MiddlewareCustomerDatabase.getInstance().deleteCar(location);
            this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                for(int customerId: affectedCustomers) {
                    middlewareCustomerDatabase.addReservedCar(customerId, "car-" + location);
                }
            });

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                    .getResourceManager()
                    .deleteCars(rmTransId, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.ROOMS_ONLY);

            // make sure to delete from reservations
            Collection<Integer> affectedCustomers = MiddlewareCustomerDatabase.getInstance().deleteRoom(location);
            this.middleware.getTransactionManager().appendReservationUndoLog(id, middlewareCustomerDatabase -> {
                for(int customerId: affectedCustomers) {
                    middlewareCustomerDatabase.addReservedRoom(customerId, location);
                }
            });

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                    .getResourceManager()
                    .deleteRooms(rmTransId, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int otherTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.OTHERS);

            AbstractRemoteResourceManager userRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS);
            boolean deleteCustomerSuccess = userRmManager.getResourceManager().deleteCustomer(otherTransId, customer);

            if(deleteCustomerSuccess) {
                CustomerReservations custReservations = MiddlewareCustomerDatabase.getInstance().getReservations(customer);

                // give back rooms
                int roomTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.ROOMS_ONLY);
                AbstractRemoteResourceManager roomRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY);
                for(String roomKey: custReservations.getBookedRooms()) {
                    roomRmManager.getResourceManager().updateReservedQuantities(roomTransId, "room-" + roomKey, 1);
                }

                // give back cars
                int carTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.CARS_ONLY);
                AbstractRemoteResourceManager carRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY);
                for(String carKey: custReservations.getBookedCars()) {
                    carRmManager.getResourceManager().updateReservedQuantities(carTransId, "car-" + carKey, 1);
                }

                // give back flights
                int flightTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.FLIGHTS_ONLY);
                AbstractRemoteResourceManager flightRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY);
                for(Integer flightKey: custReservations.getBookedFlights()) {
                    flightRmManager.getResourceManager().updateReservedQuantities(flightTransId, "flight-" + String.valueOf(flightKey), 1);
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
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.FLIGHTS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                    .getResourceManager()
                    .queryFlight(rmTransId, flightNumber);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.CARS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                    .getResourceManager()
                    .queryCars(rmTransId, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.ROOMS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                    .getResourceManager()
                    .queryRooms(rmTransId, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int otherTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.OTHERS);

            String customerInfo = this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS)
                    .getResourceManager()
                    .queryCustomerInfo(otherTransId, customer);

            CustomerReservations reservations = MiddlewareCustomerDatabase.getInstance().getReservations(customer);

            StringBuilder billBuilder = new StringBuilder();
            int flightTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.FLIGHTS_ONLY);
            for(Integer flightId: reservations.getBookedFlights()) {
                int flightPrice = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY).getResourceManager().queryFlightPrice(flightTransId, flightId);
                billBuilder.append(String.format("- flight-%d $ %d", flightId, flightPrice)).append("\n");
            }

            int carTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.CARS_ONLY);
            for(String carLocation: reservations.getBookedCars()) {
                int carPrice = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY).getResourceManager().queryCarsPrice(carTransId, carLocation);
                billBuilder.append(String.format("- car-%s $ %d", carLocation, carPrice)).append("\n");
            }

            int roomTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.ROOMS_ONLY);
            for(String roomLocation: reservations.getBookedRooms()) {
                int roomPrice = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY).getResourceManager().queryRoomsPrice(roomTransId, roomLocation);
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
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.FLIGHTS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                    .getResourceManager()
                    .queryFlightPrice(rmTransId, flightNumber);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.CARS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                    .getResourceManager()
                    .queryCarsPrice(rmTransId, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.ROOMS_ONLY);

            return this.middleware
                    .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                    .getResourceManager()
                    .queryRoomsPrice(rmTransId, location);
        } catch (TransactionException e) {
            this.handleTransactionException(id, e);
            return -1;
        }
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        try {
            this.middleware.getTransactionManager().ensureTransactionExists(id);
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.FLIGHTS_ONLY);

            String flightKey = "flight-" + flightNumber;
            boolean reserveFlightSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY).getResourceManager().updateReservedQuantities(rmTransId, flightKey, -1);

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
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.CARS_ONLY);

            String carKey = "car-" + location;
            boolean reserveCarSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY).getResourceManager().updateReservedQuantities(rmTransId, carKey, -1);

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
            int rmTransId = this.middleware.getTransactionManager().enlistResourceManager(id, ResourceManagerTypes.ROOMS_ONLY);

            String roomKey = "room-" + locationd;
            boolean reserveRoomSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY).getResourceManager().updateReservedQuantities(rmTransId, roomKey, -1);

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

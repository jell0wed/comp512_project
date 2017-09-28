package middleware;

import ResImpl.Trace;
import ResInterface.ResourceManager;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.ResourceManagerTypes;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class MiddlewareInterface implements ResourceManager {
    private MiddlewareServer middleware;

    MiddlewareInterface(MiddlewareServer serverInstance) {
        this.middleware = serverInstance;
    }

    @Override
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                .getResourceManager()
                .addFlight(id, flightNum, flightSeats, flightPrice);
    }

    @Override
    public boolean addCars(int id, String location, int numCars, int price) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                .getResourceManager()
                .addCars(id, location, numCars, price);
    }

    @Override
    public boolean addRooms(int id, String location, int numRooms, int price) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                .getResourceManager()
                .addRooms(id, location, numRooms, price);
    }

    @Override
    public int newCustomer(int id) throws RemoteException {
        AbstractRemoteResourceManager userRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS);
        int givenCustomerId = userRmManager.getResourceManager().newCustomer(id);

        this.middleware.getAllRemoteResourceManager().stream().filter(x -> x != userRmManager).forEach(x -> {
            try {
                x.getResourceManager().newCustomer(id, givenCustomerId);
            } catch (RemoteException e) {
                Trace.error(e.getMessage());
            }
        });

        return givenCustomerId;
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        AbstractRemoteResourceManager userRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS);
        boolean newCustomerSuccess = userRmManager.getResourceManager().newCustomer(id, cid);
        if(newCustomerSuccess) {
            this.middleware.getAllRemoteResourceManager().stream().filter(x -> x != userRmManager).forEach(x -> {
                try {
                    x.getResourceManager().newCustomer(id, cid);
                } catch (RemoteException e) {
                    Trace.error(e.getMessage());
                }
            });
        }

        return newCustomerSuccess;
    }

    @Override
    public boolean deleteFlight(int id, int flightNum) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                .getResourceManager()
                .deleteFlight(id, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                .getResourceManager()
                .deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                .getResourceManager()
                .deleteRooms(id, location);
    }

    @Override
    public boolean deleteCustomer(int id, int customer) throws RemoteException {
        AbstractRemoteResourceManager userRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS);
        boolean deleteCustomerSuccess = userRmManager.getResourceManager().deleteCustomer(id, customer);
        if(deleteCustomerSuccess) {
            this.middleware.getAllRemoteResourceManager().stream().filter(x -> x != userRmManager).forEach(x -> {
                try {
                    x.getResourceManager().deleteCustomer(id, customer);
                } catch (RemoteException e) {
                    Trace.error(e.getMessage());
                }
            });
        }

        return deleteCustomerSuccess;
    }

    @Override
    public int queryFlight(int id, int flightNumber) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                .getResourceManager()
                .queryFlight(id, flightNumber);
    }

    @Override
    public int queryCars(int id, String location) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                .getResourceManager()
                .queryCars(id, location);
    }

    @Override
    public int queryRooms(int id, String location) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                .getResourceManager()
                .queryRooms(id, location);
    }

    @Override
    public String queryCustomerInfo(int id, int customer) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS)
                .getResourceManager()
                .queryCustomerInfo(id, customer);
    }

    @Override
    public int queryFlightPrice(int id, int flightNumber) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                .getResourceManager()
                .queryFlightPrice(id, flightNumber);
    }

    @Override
    public int queryCarsPrice(int id, String location) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                .getResourceManager()
                .queryCarsPrice(id, location);
    }

    @Override
    public int queryRoomsPrice(int id, String location) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                .getResourceManager()
                .queryRoomsPrice(id, location);
    }

    @Override
    public boolean reserveFlight(int id, int customer, int flightNumber) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                .getResourceManager()
                .reserveFlight(id, customer, flightNumber);
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                .getResourceManager()
                .reserveCar(id, customer, location);
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY)
                .getResourceManager()
                .reserveRoom(id, customer, locationd);
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean Car, boolean Room) throws RemoteException {
        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS)
                .getResourceManager()
                .itinerary(id, customer, flightNumbers, location, Car, Room);
    }
}

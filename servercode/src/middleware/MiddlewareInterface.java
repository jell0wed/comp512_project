package middleware;

import ResInterface.ResourceManager;
import middleware.entities.CustomerReservations;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.ResourceManagerTypes;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Created by jpoisson on 2017-09-25.
 */
public class MiddlewareInterface implements ResourceManager {
    private MiddlewareServer middleware;

    public MiddlewareInterface(MiddlewareServer serverInstance) {
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
        int givenCustomerId = userRmManager.getResourceManager().newCustomer(id); // create the customer first at the others rm

        MiddlewareCustomerDatabase.getInstance().createCustomer(givenCustomerId);

        return givenCustomerId;
    }

    @Override
    public boolean newCustomer(int id, int cid) throws RemoteException {
        AbstractRemoteResourceManager userRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS);
        boolean newCustomerSuccess = userRmManager.getResourceManager().newCustomer(id, cid);

        if(newCustomerSuccess) {
            MiddlewareCustomerDatabase.getInstance().createCustomer(cid);
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
            MiddlewareCustomerDatabase.getInstance().deleteCustomer(customer);
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
        String customerInfo = this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.OTHERS)
                .getResourceManager()
                .queryCustomerInfo(id, customer);

        CustomerReservations reservations = MiddlewareCustomerDatabase.getInstance().getReservations(customer);

        StringBuilder billBuilder = new StringBuilder();
        for(Integer flightId: reservations.getBookedFlights()) {
            int flightPrice = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY).getResourceManager().queryFlightPrice(id, flightId);
            billBuilder.append(String.format("- flight-%d $ %d", flightId, flightPrice)).append("\n");
        }

        for(String carLocation: reservations.getBookedCars()) {
            int carPrice = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY).getResourceManager().queryCarsPrice(id, carLocation);
            billBuilder.append(String.format("- car-%s $ %d", carLocation, carPrice)).append("\n");
        }

        for(String roomLocation: reservations.getBookedRooms()) {
            int roomPrice = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY).getResourceManager().queryRoomsPrice(id, roomLocation);
            billBuilder.append(String.format("- room-%s $ %d", roomLocation, roomPrice)).append("\n");
        }

        return customerInfo + billBuilder.toString();
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
        String flightKey = "flight-" + flightNumber;
        boolean reserveFlightSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY).getResourceManager().reserveItem(id, flightKey);

        if(reserveFlightSuccess) {
            MiddlewareCustomerDatabase.getInstance().addReservedFlight(customer, flightNumber);
        }
        return reserveFlightSuccess;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        String carKey = "car-" + location;
        boolean reserveCarSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY).getResourceManager().reserveItem(id, carKey);

        if(reserveCarSuccess) {
            MiddlewareCustomerDatabase.getInstance().addReservedCar(customer, location);
        }
        return reserveCarSuccess;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException {
        String roomKey = "room-" + locationd;
        boolean reserveRoomSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY).getResourceManager().reserveItem(id, roomKey);

        if(reserveRoomSuccess) {
            MiddlewareCustomerDatabase.getInstance().addReservedRoom(customer, locationd);
        }
        return reserveRoomSuccess;
    }

    @Override
    public boolean itinerary(int id, int customer, Vector flightNumbers, String location, boolean bookCar, boolean bookRoom) throws RemoteException {
        // make sure to book the appropriate flights
        for(Object flightNoObj: flightNumbers) {
            Integer flightNo = Integer.parseInt((String) flightNoObj);
            this.reserveFlight(id, customer, flightNo);
        }

        // make sure to book the appropriate cars
        if(bookCar) {
            this.reserveCar(id, customer, location);
        }
        // TODO : what should we do in case we are unable to reserve car or room, are we expected to rollback the flight reservations

        // make sure to book the appropriate rooms
        if(bookRoom) {
            this.reserveRoom(id, customer, location);
        }

        return true;
    }
}

package middleware;

import ResInterface.ResourceManager;
import middleware.entities.CustomerReservations;
import middleware.resource_managers.AbstractRemoteResourceManager;
import middleware.resource_managers.ResourceManagerTypes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        // make sure to delete from reservations
        MiddlewareCustomerDatabase.getInstance().deleteFlight(flightNum);

        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY)
                .getResourceManager()
                .deleteFlight(id, flightNum);
    }

    @Override
    public boolean deleteCars(int id, String location) throws RemoteException {
        // make sure to delete from reservations
        MiddlewareCustomerDatabase.getInstance().deleteCar("car-" + location);

        return this.middleware
                .getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY)
                .getResourceManager()
                .deleteCars(id, location);
    }

    @Override
    public boolean deleteRooms(int id, String location) throws RemoteException {
        // make sure to delete from reservations
        MiddlewareCustomerDatabase.getInstance().deleteCar("room-" + location);

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
            CustomerReservations custReservations = MiddlewareCustomerDatabase.getInstance().getReservations(customer);
            // give back rooms
            AbstractRemoteResourceManager roomRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY);
            for(String roomKey: custReservations.getBookedRooms()) {
                roomRmManager.getResourceManager().updateReservedQuantities(id, roomKey, 1);
            }

            // give back cars
            AbstractRemoteResourceManager carRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY);
            for(String carKey: custReservations.getBookedCars()) {
                carRmManager.getResourceManager().updateReservedQuantities(id, carKey, 1);
            }

            // give back flights
            AbstractRemoteResourceManager flightRmManager = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY);
            for(Integer flightKey: custReservations.getBookedFlights()) {
                flightRmManager.getResourceManager().updateReservedQuantities(id, "flight-" + String.valueOf(flightKey), 1);
            }

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
        boolean reserveFlightSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.FLIGHTS_ONLY).getResourceManager().updateReservedQuantities(id, flightKey, -1);

        if(reserveFlightSuccess) {
            MiddlewareCustomerDatabase.getInstance().addReservedFlight(customer, flightNumber);
        }
        return reserveFlightSuccess;
    }

    @Override
    public boolean reserveCar(int id, int customer, String location) throws RemoteException {
        String carKey = "car-" + location;
        boolean reserveCarSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.CARS_ONLY).getResourceManager().updateReservedQuantities(id, carKey, -1);

        if(reserveCarSuccess) {
            MiddlewareCustomerDatabase.getInstance().addReservedCar(customer, location);
        }
        return reserveCarSuccess;
    }

    @Override
    public boolean reserveRoom(int id, int customer, String locationd) throws RemoteException {
        String roomKey = "room-" + locationd;
        boolean reserveRoomSuccess = this.middleware.getRemoteResourceManagerForType(ResourceManagerTypes.ROOMS_ONLY).getResourceManager().updateReservedQuantities(id, roomKey, -1);

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
    }

    @Override
    public boolean updateReservedQuantities(int id, String key, int incQty) throws RemoteException {
        throw new NotImplementedException(); // operation is not available from the middleware!!
    }
}

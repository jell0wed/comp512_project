package middleware;

import middleware.entities.CustomerReservations;

import java.util.Hashtable;

/**
 * Created by jpoisson on 2017-09-27.
 */
public class MiddlewareCustomerDatabase {
    private static MiddlewareCustomerDatabase instance = null;

    public static MiddlewareCustomerDatabase getInstance() {
        if(instance == null) {
            instance = new MiddlewareCustomerDatabase();
        }

        return instance;
    }

    private Hashtable<Integer, CustomerReservations> customerReservations = new Hashtable<>();

    private MiddlewareCustomerDatabase() {

    }

    public synchronized void createCustomer(int customerId) {
        this.customerReservations.put(customerId, new CustomerReservations());
    }

    public synchronized void deleteCustomer(int customerId) {
        this.customerReservations.remove(customerId);
    }

    public CustomerReservations getReservations(int customerId) {
        return this.customerReservations.get(customerId);
    }

    public synchronized void addReservedFlight(int customerId, int flightId) {
        CustomerReservations reservations = this.customerReservations.get(customerId);
        reservations.addBookedFlight(flightId);

        this.customerReservations.put(customerId, reservations);
    }

    public synchronized void addReservedRoom(int customerId, String location) {
        CustomerReservations reservations = this.customerReservations.get(customerId);
        reservations.addBookedRoom(location);

        this.customerReservations.put(customerId, reservations);
    }

    public synchronized void addReservedCar(int customerId, String location) {
        CustomerReservations reservations = this.customerReservations.get(customerId);
        reservations.addBookedCar(location);

        this.customerReservations.put(customerId, reservations);
    }


}

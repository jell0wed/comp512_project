package middleware;

import middleware.entities.CustomerReservations;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

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

    public synchronized void createCustomer(int customerId, CustomerReservations reservation) {
        this.customerReservations.put(customerId, reservation);
    }

    public synchronized void deleteCustomer(int customerId) {
        this.customerReservations.remove(customerId);
    }

    public CustomerReservations getReservations(int customerId) {
        return this.customerReservations.getOrDefault(customerId, new CustomerReservations());
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

    public synchronized Collection<Integer> deleteRoom(String key) {
        LinkedList<Integer> affectedCustomers = new LinkedList<>();
        for(int customerId: this.customerReservations.keySet()) {
            CustomerReservations reservations = this.customerReservations.get(customerId);
            if(reservations.removeRoom(key)) {
                affectedCustomers.add(customerId);
            }
        }

        return affectedCustomers;
    }

    public synchronized Collection<Integer> deleteCar(String key) {
        LinkedList<Integer> affectedCustomers = new LinkedList<>();
        for(int customerId: this.customerReservations.keySet()) {
            CustomerReservations reservations = this.customerReservations.get(customerId);
            if(reservations.removeCars(key)) {
                affectedCustomers.add(customerId);
            }
        }

        return affectedCustomers;
    }

    public synchronized Collection<Integer> deleteFlight(int no) {
        LinkedList<Integer> affectedCustomers = new LinkedList<>();
        for(int customerId: this.customerReservations.keySet()) {
            CustomerReservations reservations = this.customerReservations.get(customerId);
            if(reservations.removeFlight(no)) {
                affectedCustomers.add(customerId);
            }
        }

        return affectedCustomers;
    }

    public synchronized boolean deleteReservedFlight(int cid, int flight) {
        CustomerReservations reservations = this.customerReservations.get(cid);
        return reservations.removeFlight(flight);
    }

    public synchronized boolean deleteReservedCar(int cid, String location) {
        CustomerReservations reservations = this.customerReservations.get(cid);
        return reservations.removeCars(location);
    }

    public synchronized boolean deleteReservedRoom(int cid, String location) {
        CustomerReservations reservations = this.customerReservations.get(cid);
        return reservations.removeRoom(location);
    }


}

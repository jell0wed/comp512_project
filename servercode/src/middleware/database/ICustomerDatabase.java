package middleware.database;

import middleware.entities.CustomerReservations;

import java.util.Collection;

public interface ICustomerDatabase {
    public void createCustomer(int customerId);
    public void createCustomer(int customerId, CustomerReservations reservation);
    public void deleteCustomer(int customerId);
    public CustomerReservations getReservations(int customerId);
    public void addReservedFlight(int customerId, int flightId);
    public void addReservedRoom(int customerId, String location);
    public void addReservedCar(int customerId, String location);
    public Collection<Integer> deleteRoom(String key);
    public Collection<Integer> deleteCar(String key);
    public Collection<Integer> deleteFlight(int no);
    public boolean deleteReservedFlight(int cid, int flight);
    public boolean deleteReservedCar(int cid, String location);
    public boolean deleteReservedRoom(int cid, String location);
}

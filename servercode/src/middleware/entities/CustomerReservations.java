package middleware.entities;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by jpoisson on 2017-09-27.
 */
public class CustomerReservations {
    private Collection<Integer> bookedFlights = new LinkedList<>();
    private Collection<String> bookedRooms = new LinkedList<>();
    private Collection<String> bookedCars = new LinkedList<>();

    public CustomerReservations() {

    }

    public CustomerReservations(CustomerReservations src) {
        this.bookedFlights = new LinkedList<>(src.bookedFlights);
        this.bookedRooms = new LinkedList<>(src.bookedRooms);
        this.bookedCars = new LinkedList<>(src.bookedCars);
    }

    public void addBookedFlight(Integer flightId) {
        this.bookedFlights.add(flightId);
    }

    public void addBookedRoom(String roomLocation) {
        this.bookedRooms.add(roomLocation);
    }

    public void addBookedCar(String carLocation) {
        this.bookedCars.add(carLocation);
    }

    public Collection<Integer> getBookedFlights() {
        return bookedFlights;
    }

    public Collection<String> getBookedRooms() {
        return bookedRooms;
    }

    public Collection<String> getBookedCars() {
        return bookedCars;
    }

    public boolean removeRoom(String roomKey) {
        return this.bookedRooms.remove(roomKey);
    }

    public boolean removeFlight(int no) {
        return this.bookedFlights.remove(no);
    }

    public boolean removeCars(String carKey) {
        return this.bookedCars.remove(carKey);
    }
}

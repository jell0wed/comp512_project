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

    public void removeRoom(String roomKey) {
        this.bookedRooms.remove(roomKey);
    }

    public void removeFlight(int no) {
        this.bookedFlights.remove(no);
    }

    public void removeCars(String carKey) {
        this.bookedCars.remove(carKey);
    }
}

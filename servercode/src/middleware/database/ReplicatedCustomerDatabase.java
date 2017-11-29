package middleware.database;

import ResImpl.Trace;
import ResInterface.ResourceManager;
import middleware.entities.CustomerReservations;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.function.Consumer;

public class ReplicatedCustomerDatabase implements ICustomerDatabase {
    private ICustomerDatabase currentDb;
    private ResourceManager backupMiddleware;

    public ReplicatedCustomerDatabase(ICustomerDatabase currentDb, ResourceManager backup) {
        this.currentDb = currentDb;
        this.backupMiddleware = backup;
    }

    @Override
    public synchronized void createCustomer(int customerId) {
        this.currentDb.createCustomer(customerId);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.createCustomer(customerId);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }
    }

    @Override
    public synchronized void createCustomer(int customerId, CustomerReservations reservation) {
        this.currentDb.createCustomer(customerId, reservation);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.createCustomer(customerId, reservation);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }
    }

    @Override
    public void deleteCustomer(int customerId) {
        this.currentDb.deleteCustomer(customerId);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.deleteCustomer(customerId);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }
    }

    @Override
    public CustomerReservations getReservations(int customerId) {
        return this.currentDb.getReservations(customerId);
    }

    @Override
    public void addReservedFlight(int customerId, int flightId) {
        this.currentDb.addReservedFlight(customerId, flightId);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.addReservedFlight(customerId, flightId);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }
    }

    @Override
    public void addReservedRoom(int customerId, String location) {
        this.currentDb.addReservedRoom(customerId, location);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.addReservedRoom(customerId, location);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }
    }

    @Override
    public void addReservedCar(int customerId, String location) {
        this.currentDb.addReservedCar(customerId, location);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.addReservedCar(customerId, location);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }
    }

    @Override
    public Collection<Integer> deleteRoom(String key) {
        Collection<Integer> result = this.currentDb.deleteRoom(key);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.deleteRoom(key);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }

        return result;
    }

    @Override
    public Collection<Integer> deleteCar(String key) {
        Collection<Integer> result = this.currentDb.deleteCar(key);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.deleteCar(key);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }

        return result;
    }

    @Override
    public Collection<Integer> deleteFlight(int no) {
        Collection<Integer> result = this.currentDb.deleteFlight(no);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.deleteFlight(no);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }

        return result;
    }

    @Override
    public boolean deleteReservedFlight(int cid, int flight) {
        boolean result = this.currentDb.deleteReservedFlight(cid, flight);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.deleteReservedFlight(cid, flight);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }

        return result;
    }

    @Override
    public boolean deleteReservedCar(int cid, String location) {
        boolean result = this.currentDb.deleteReservedCar(cid, location);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.deleteReservedCar(cid, location);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }

        return result;
    }

    @Override
    public boolean deleteReservedRoom(int cid, String location) {
        boolean result = this.currentDb.deleteReservedRoom(cid, location);
        try {
            this.backupMiddleware.executeReservationOperation((Consumer<ICustomerDatabase> &Serializable) backupDb -> {
                backupDb.deleteReservedRoom(cid, location);
            });
        } catch (RemoteException e) {
            Trace.error("Remote exception");
        }

        return result;
    }
}

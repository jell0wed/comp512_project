package rmi;

import ResInterface.ResourceManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rmi.utils.RMITestUtils;

import java.io.IOException;
import java.rmi.RemoteException;

public class CustomerTestSuite {
    private static ResourceManager rm;

    @BeforeClass
    public static void initializeClient() throws IOException {
        rm = RMITestUtils.initializeRMI();
    }

    private static final int TEST_CUSTOMER_DELETE_CUSTOMER_ID = RMITestUtils.newUniqueId();
    private static final String TEST_CUSTOMER_DELETE_ROOM_LOCATION = "mtl-" + RMITestUtils.newUniqueId();
    private static final int TEST_CUSTOMER_DELETE_ROOM_NUM = 10;
    private static final int TEST_CUSTOMER_DELETE_ROOM_PRICE = 100;
    private static final String TEST_CUSTOMER_DELETE_CAR_LOCATION = "mtl-" + RMITestUtils.newUniqueId();
    private static final int TEST_CUSTOMER_DELETE_CAR_NUM = 20;
    private static final int TEST_CUSTOMER_DELETE_CAR_PRICE = 200;
    private static final int TEST_CUSTOMER_DELETE_FLIGHT_NO = RMITestUtils.newUniqueId();
    private static final int TEST_CUSTOMER_DELETE_FLIGHT_SEATS = 30;
    private static final int TEST_CUSTOMER_DELETE_FLIGHT_PRICE = 300;

    @Test
    public void testDeleteCustomer() throws RemoteException {
        // create new customer
        int transId = rm.startTransaction();
        rm.newCustomer(transId, TEST_CUSTOMER_DELETE_CUSTOMER_ID);

        // create new room
        rm.addRooms(transId, TEST_CUSTOMER_DELETE_ROOM_LOCATION, TEST_CUSTOMER_DELETE_ROOM_NUM, TEST_CUSTOMER_DELETE_ROOM_PRICE);

        // create new car
        rm.addCars(transId, TEST_CUSTOMER_DELETE_CAR_LOCATION, TEST_CUSTOMER_DELETE_CAR_NUM, TEST_CUSTOMER_DELETE_CAR_PRICE);

        // create new flight
        rm.addFlight(transId, TEST_CUSTOMER_DELETE_FLIGHT_NO, TEST_CUSTOMER_DELETE_FLIGHT_SEATS, TEST_CUSTOMER_DELETE_FLIGHT_PRICE);
        rm.commitTransaction(transId);

        int transId2 = rm.startTransaction();
        // reserve room
        rm.reserveRoom(transId2, TEST_CUSTOMER_DELETE_CUSTOMER_ID, TEST_CUSTOMER_DELETE_ROOM_LOCATION);

        // reserve car
        rm.reserveCar(transId2, TEST_CUSTOMER_DELETE_CUSTOMER_ID, TEST_CUSTOMER_DELETE_CAR_LOCATION);

        // reserve flight
        rm.reserveFlight(transId2, TEST_CUSTOMER_DELETE_CUSTOMER_ID, TEST_CUSTOMER_DELETE_FLIGHT_NO);

        // make sure it is commited
        String customerBill = rm.queryCustomerInfo(transId2, TEST_CUSTOMER_DELETE_CUSTOMER_ID);
        int queryRoom = rm.queryRooms(transId2, TEST_CUSTOMER_DELETE_ROOM_LOCATION);
        int queryCar = rm.queryCars(transId2, TEST_CUSTOMER_DELETE_CAR_LOCATION);
        int queryFlight = rm.queryFlight(transId2, TEST_CUSTOMER_DELETE_FLIGHT_NO);

        Assert.assertTrue(customerBill.contains("room-" + TEST_CUSTOMER_DELETE_ROOM_LOCATION));
        Assert.assertTrue(customerBill.contains("car-" + TEST_CUSTOMER_DELETE_CAR_LOCATION));
        Assert.assertTrue(customerBill.contains("flight-"+TEST_CUSTOMER_DELETE_FLIGHT_NO));

        Assert.assertEquals(TEST_CUSTOMER_DELETE_ROOM_NUM - 1, queryRoom);
        Assert.assertEquals(TEST_CUSTOMER_DELETE_CAR_NUM - 1, queryCar);
        Assert.assertEquals(TEST_CUSTOMER_DELETE_FLIGHT_SEATS - 1, queryFlight);


        // abort transaction
        rm.abortTransaction(transId2);

        int transId3 = rm.startTransaction();
        // make sure they are given back
        int queryRoom2 = rm.queryRooms(transId3, TEST_CUSTOMER_DELETE_ROOM_LOCATION);
        int queryCar2 = rm.queryCars(transId3, TEST_CUSTOMER_DELETE_CAR_LOCATION);
        int queryFlight2 = rm.queryFlight(transId3, TEST_CUSTOMER_DELETE_FLIGHT_NO);
        rm.commitTransaction(transId3);

        Assert.assertEquals(TEST_CUSTOMER_DELETE_ROOM_NUM, queryRoom2);
        Assert.assertEquals(TEST_CUSTOMER_DELETE_CAR_NUM, queryCar2);
        Assert.assertEquals(TEST_CUSTOMER_DELETE_FLIGHT_SEATS, queryFlight2);
    }
}

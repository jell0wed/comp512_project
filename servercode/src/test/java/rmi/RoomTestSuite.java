package rmi;

import ResInterface.ResourceManager;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rmi.utils.RMITestUtils;

import java.io.IOException;
import java.rmi.RemoteException;

public class RoomTestSuite extends TestSuite {
    private static ResourceManager rm;

    @BeforeClass
    public static void initializeClient() throws IOException {
        rm = RMITestUtils.initializeRMI();
    }

    private static final String TEST_ROOMOPERATION_ROOM_LOCATION = "mtl" + RMITestUtils.newUniqueId();
    private static final int TEST_ROOMOPERATION_ROOM_NUMROOM = 10;
    private static final int TEST_ROOMOPERATION_ROOM_PRICE = 100;
    private static final String TEST_ROOMOPERATION2_ROOM_LOCATION = "mtl2" + RMITestUtils.newUniqueId();
    private static final int TEST_ROOMOPERATION2_ROOM_NUMROOM = 20;
    private static final int TEST_ROOMOPERATION2_ROOM_PRICE = 200;

    @Test
    public void testROOMTransactionOperations() throws RemoteException {
        // test a normal commit
        int transId = rm.startTransaction();
        rm.addRooms(transId, TEST_ROOMOPERATION_ROOM_LOCATION, TEST_ROOMOPERATION_ROOM_NUMROOM, TEST_ROOMOPERATION_ROOM_PRICE);
        rm.commitTransaction(transId);

        // test an aborted commit
        int transId2 = rm.startTransaction();
        rm.addRooms(transId2, TEST_ROOMOPERATION2_ROOM_LOCATION, TEST_ROOMOPERATION2_ROOM_NUMROOM, TEST_ROOMOPERATION2_ROOM_PRICE);
        rm.abortTransaction(transId2);

        // query to assert
        int transId3 = rm.startTransaction();
        int numROOMOp1 = rm.queryRooms(transId3, TEST_ROOMOPERATION_ROOM_LOCATION);
        int numROOMOp2 = rm.queryRooms(transId3, TEST_ROOMOPERATION2_ROOM_LOCATION);
        rm.commitTransaction(transId3);

        Assert.assertEquals(TEST_ROOMOPERATION_ROOM_NUMROOM, numROOMOp1);
        Assert.assertNotEquals(TEST_ROOMOPERATION2_ROOM_NUMROOM, numROOMOp2);
    }

    private static final int TEST_ROOMRESERVATION_CUSTOMER_ID = RMITestUtils.newUniqueId();
    private static final String TEST_ROOMRESERVATION_ROOM_LOCATION = "mtl" + RMITestUtils.newUniqueId();
    private static final int TEST_ROOMRESERVATION_ROOM_NUMROOM = 10;
    private static final int TEST_ROOMRESERVATION_ROOM_PRICE = 100;

    @Test
    public void testROOMTransactionReservations() throws RemoteException {
        // create a customer
        int transId = rm.startTransaction();
        rm.newCustomer(transId, TEST_ROOMRESERVATION_CUSTOMER_ID);

        // create ROOMs
        rm.addRooms(transId, TEST_ROOMRESERVATION_ROOM_LOCATION, TEST_ROOMRESERVATION_ROOM_NUMROOM, TEST_ROOMRESERVATION_ROOM_PRICE);
        rm.commitTransaction(transId);

        // reserve ROOM then commit
        int transId2 = rm.startTransaction();
        rm.reserveRoom(transId2, TEST_ROOMRESERVATION_CUSTOMER_ID, TEST_ROOMRESERVATION_ROOM_LOCATION);
        int numROOM = rm.queryRooms(transId2, TEST_ROOMRESERVATION_ROOM_LOCATION);
        Assert.assertEquals(TEST_ROOMRESERVATION_ROOM_NUMROOM - 1, numROOM); // makes sure it is 1 commit phase
        rm.commitTransaction(transId2);

        // reserve ROOM then abort
        int transId3 = rm.startTransaction();
        rm.reserveRoom(transId3, TEST_ROOMRESERVATION_CUSTOMER_ID, TEST_ROOMRESERVATION_ROOM_LOCATION);
        int numROOM2 = rm.queryRooms(transId3, TEST_ROOMRESERVATION_ROOM_LOCATION);
        Assert.assertEquals(TEST_ROOMRESERVATION_ROOM_NUMROOM - 2, numROOM2); // makes sure it is 1 commit phase
        rm.abortTransaction(transId3);

        int transId4 = rm.startTransaction();
        int numROOMAfterAbort = rm.queryRooms(transId4, TEST_ROOMRESERVATION_ROOM_LOCATION);
        rm.commitTransaction(transId4);
        Assert.assertEquals(TEST_ROOMRESERVATION_ROOM_NUMROOM - 1, numROOMAfterAbort); // makes sure abort rolled back trans
    }

    private static final int TEST_ROOMRESERVATIONOPERATION_CUSTOMER_ID = RMITestUtils.newUniqueId();
    private static final String TEST_ROOMRESERVATIONOPERATION_ROOM_LOCATION = "mtl" + RMITestUtils.newUniqueId();
    private static final int TEST_ROOMRESERVATIONOPERATION_ROOM_NUMROOM = 10;
    private static final int TEST_ROOMRESERVATIONOPERATION_ROOM_PRICE = 100;
    @Test
    public void testROOMTransactionReservationsOperations() throws RemoteException {
        // create a customer
        int transId = rm.startTransaction();
        rm.newCustomer(transId, TEST_ROOMRESERVATIONOPERATION_CUSTOMER_ID);
        // create ROOM
        rm.addRooms(transId, TEST_ROOMRESERVATIONOPERATION_ROOM_LOCATION, TEST_ROOMRESERVATIONOPERATION_ROOM_NUMROOM, TEST_ROOMRESERVATIONOPERATION_ROOM_PRICE);
        // reserve ROOM
        rm.reserveRoom(transId, TEST_ROOMRESERVATIONOPERATION_CUSTOMER_ID, TEST_ROOMRESERVATIONOPERATION_ROOM_LOCATION);
        // commit
        rm.commitTransaction(transId);

        int transId2 = rm.startTransaction();
        // delete ROOM
        rm.deleteRooms(transId2, TEST_ROOMRESERVATIONOPERATION_ROOM_LOCATION);
        // make sure ROOM are removed from reservations
        String customerBill = rm.queryCustomerInfo(transId2, TEST_ROOMRESERVATIONOPERATION_CUSTOMER_ID);
        Assert.assertFalse(customerBill.contains(TEST_ROOMRESERVATIONOPERATION_ROOM_LOCATION));
        // abort
        rm.abortTransaction(transId2);

        // make sure ROOM are given back
        int transId3 = rm.startTransaction();
        String customerBill2 = rm.queryCustomerInfo(transId3, TEST_ROOMRESERVATIONOPERATION_CUSTOMER_ID);
        Assert.assertTrue(customerBill2.contains(TEST_ROOMRESERVATIONOPERATION_ROOM_LOCATION));
        rm.commitTransaction(transId3);
    }
}

package rmi;

import ResImpl.exceptions.TransactionException;
import ResInterface.ResourceManager;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rmi.utils.RMITestUtils;

import java.io.IOException;
import java.rmi.RemoteException;

public class FlightTestSuite extends TestSuite {
    private static ResourceManager rm;

    @BeforeClass
    public static void initializeClient() throws IOException {
        rm = RMITestUtils.initializeRMI();
    }

    private static final int TEST_FLIGHTOPERATION_FLIGHT_NO = RMITestUtils.newUniqueId();
    private static final int TEST_FLIGHTOPERATION_FLIGHT_NUMSEATS = 10;
    private static final int TEST_FLIGHTOPERATION_FLIGHT_PRICE = 100;
    private static final int TEST_FLIGHTOPERATION2_FLIGHT_NO = RMITestUtils.newUniqueId();
    private static final int TEST_FLIGHTOPERATION2_FLIGHT_NUMSEATS = 20;
    private static final int TEST_FLIGHTOPERATION2_FLIGHT_PRICE = 200;

    @Test
    public void testFlightTransactionOperations() throws RemoteException, TransactionException {
        // test a normal commit
        int transId = rm.startTransaction();
        rm.addFlight(transId, TEST_FLIGHTOPERATION_FLIGHT_NO, TEST_FLIGHTOPERATION_FLIGHT_NUMSEATS, TEST_FLIGHTOPERATION_FLIGHT_PRICE);
        rm.commitTransaction(transId);

        // test an aborted commit
        int transId2 = rm.startTransaction();
        rm.addFlight(transId2, TEST_FLIGHTOPERATION2_FLIGHT_NO, TEST_FLIGHTOPERATION2_FLIGHT_NUMSEATS, TEST_FLIGHTOPERATION2_FLIGHT_PRICE);
        rm.abortTransaction(transId2);

        // query to assert
        int transId3 = rm.startTransaction();
        int numFLIGHTOp1 = rm.queryFlight(transId3, TEST_FLIGHTOPERATION_FLIGHT_NO);
        int numFLIGHTOp2 = rm.queryFlight(transId3, TEST_FLIGHTOPERATION2_FLIGHT_NO);
        rm.commitTransaction(transId3);

        Assert.assertEquals(TEST_FLIGHTOPERATION_FLIGHT_NUMSEATS, numFLIGHTOp1);
        Assert.assertNotEquals(TEST_FLIGHTOPERATION2_FLIGHT_NUMSEATS, numFLIGHTOp2);
    }

    private static final int TEST_FLIGHTRESERVATION_CUSTOMER_ID = RMITestUtils.newUniqueId();
    private static final int TEST_FLIGHTRESERVATION_FLIGHT_NO = RMITestUtils.newUniqueId();
    private static final int TEST_FLIGHTRESERVATION_FLIGHT_NUMSEATS = 10;
    private static final int TEST_FLIGHTRESERVATION_FLIGHT_PRICE = 100;

    @Test
    public void testFlightTransactionReservations() throws RemoteException, TransactionException {
        // create a customer
        int transId = rm.startTransaction();
        rm.newCustomer(transId, TEST_FLIGHTRESERVATION_CUSTOMER_ID);

        // create FLIGHTs
        rm.addFlight(transId, TEST_FLIGHTRESERVATION_FLIGHT_NO, TEST_FLIGHTRESERVATION_FLIGHT_NUMSEATS, TEST_FLIGHTRESERVATION_FLIGHT_PRICE);
        rm.commitTransaction(transId);

        // reserve FLIGHT then commit
        int transId2 = rm.startTransaction();
        rm.reserveFlight(transId2, TEST_FLIGHTRESERVATION_CUSTOMER_ID, TEST_FLIGHTRESERVATION_FLIGHT_NO);
        int numFLIGHT = rm.queryFlight(transId2, TEST_FLIGHTRESERVATION_FLIGHT_NO);
        Assert.assertEquals(TEST_FLIGHTRESERVATION_FLIGHT_NUMSEATS - 1, numFLIGHT); // makes sure it is 1 commit phase
        rm.commitTransaction(transId2);

        // reserve FLIGHT then abort
        int transId3 = rm.startTransaction();
        rm.reserveFlight(transId3, TEST_FLIGHTRESERVATION_CUSTOMER_ID, TEST_FLIGHTRESERVATION_FLIGHT_NO);
        int numFLIGHT2 = rm.queryFlight(transId3, TEST_FLIGHTRESERVATION_FLIGHT_NO);
        Assert.assertEquals(TEST_FLIGHTRESERVATION_FLIGHT_NUMSEATS - 2, numFLIGHT2); // makes sure it is 1 commit phase
        rm.abortTransaction(transId3);

        int transId4 = rm.startTransaction();
        int numFLIGHTAfterAbort = rm.queryFlight(transId4, TEST_FLIGHTRESERVATION_FLIGHT_NO);
        rm.commitTransaction(transId4);
        Assert.assertEquals(TEST_FLIGHTRESERVATION_FLIGHT_NUMSEATS - 1, numFLIGHTAfterAbort); // makes sure abort rolled back trans
    }

    private static final int TEST_FLIGHTRESERVATIONOPERATION_CUSTOMER_ID = RMITestUtils.newUniqueId();
    private static final int TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_NO = RMITestUtils.newUniqueId();
    private static final int TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_NUMSEATS = 10;
    private static final int TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_PRICE = 100;
    @Test
    public void testFLIGHTTransactionReservationsOperations() throws RemoteException, TransactionException {
        // create a customer
        int transId = rm.startTransaction();
        rm.newCustomer(transId, TEST_FLIGHTRESERVATIONOPERATION_CUSTOMER_ID);
        // create FLIGHT
        rm.addFlight(transId, TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_NO, TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_NUMSEATS, TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_PRICE);
        // reserve FLIGHT
        rm.reserveFlight(transId, TEST_FLIGHTRESERVATIONOPERATION_CUSTOMER_ID, TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_NO);
        // commit
        rm.commitTransaction(transId);

        int transId2 = rm.startTransaction();
        // delete FLIGHT
        rm.deleteFlight(transId2, TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_NO);
        // make sure FLIGHT are removed from reservations
        String customerBill = rm.queryCustomerInfo(transId2, TEST_FLIGHTRESERVATIONOPERATION_CUSTOMER_ID);
        Assert.assertFalse(customerBill.contains("flight-"+TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_NO));
        // abort
        rm.abortTransaction(transId2);

        // make sure FLIGHT are given back
        int transId3 = rm.startTransaction();
        String customerBill2 = rm.queryCustomerInfo(transId3, TEST_FLIGHTRESERVATIONOPERATION_CUSTOMER_ID);
        Assert.assertTrue(customerBill2.contains("flight-"+TEST_FLIGHTRESERVATIONOPERATION_FLIGHT_NO));
        rm.commitTransaction(transId3);
    }
}

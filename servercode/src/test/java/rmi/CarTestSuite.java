package rmi;

import ResInterface.ResourceManager;
import junit.framework.TestSuite;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rmi.utils.RMITestUtils;

import java.io.IOException;
import java.rmi.RemoteException;

public class CarTestSuite extends TestSuite {
    private static ResourceManager rm;

    @BeforeClass
    public static void initializeClient() throws IOException {
        rm = RMITestUtils.initializeRMI();
    }

    private static final String TEST_CAROPERATION_CAR_LOCATION = "mtl" + RMITestUtils.newUniqueId();
    private static final int TEST_CAROPERATION_CAR_NUMCAR = 10;
    private static final int TEST_CAROPERATION_CAR_PRICE = 100;
    private static final String TEST_CAROPERATION2_CAR_LOCATION = "mtl2" + RMITestUtils.newUniqueId();
    private static final int TEST_CAROPERATION2_CAR_NUMCAR = 20;
    private static final int TEST_CAROPERATION2_CAR_PRICE = 200;

    @Test
    public void testCarTransactionOperations() throws RemoteException {
        // test a normal commit
        int transId = rm.startTransaction();
        rm.addCars(transId, TEST_CAROPERATION_CAR_LOCATION, TEST_CAROPERATION_CAR_NUMCAR, TEST_CAROPERATION_CAR_PRICE);
        rm.commitTransaction(transId);

        // test an aborted commit
        int transId2 = rm.startTransaction();
        rm.addCars(transId2, TEST_CAROPERATION2_CAR_LOCATION, TEST_CAROPERATION2_CAR_NUMCAR, TEST_CAROPERATION2_CAR_PRICE);
        rm.abortTransaction(transId2);

        // query to assert
        int transId3 = rm.startTransaction();
        int numCarOp1 = rm.queryCars(transId3, TEST_CAROPERATION_CAR_LOCATION);
        int numCarOp2 = rm.queryCars(transId3, TEST_CAROPERATION2_CAR_LOCATION);
        rm.commitTransaction(transId3);

        Assert.assertEquals(TEST_CAROPERATION_CAR_NUMCAR, numCarOp1);
        Assert.assertNotEquals(TEST_CAROPERATION2_CAR_NUMCAR, numCarOp2);
    }

    private static final int TEST_CARRESERVATION_CUSTOMER_ID = RMITestUtils.newUniqueId();
    private static final String TEST_CARRESERVATION_CAR_LOCATION = "mtl" + RMITestUtils.newUniqueId();
    private static final int TEST_CARRESERVATION_CAR_NUMCAR = 10;
    private static final int TEST_CARRESERVATION_CAR_PRICE = 100;

    @Test
    public void testCarTransactionReservations() throws RemoteException {
        // create a customer
        int transId = rm.startTransaction();
        rm.newCustomer(transId, TEST_CARRESERVATION_CUSTOMER_ID);

        // create cars
        rm.addCars(transId, TEST_CARRESERVATION_CAR_LOCATION, TEST_CARRESERVATION_CAR_NUMCAR, TEST_CARRESERVATION_CAR_PRICE);
        rm.commitTransaction(transId);

        // reserve car then commit
        int transId2 = rm.startTransaction();
        rm.reserveCar(transId2, TEST_CARRESERVATION_CUSTOMER_ID, TEST_CARRESERVATION_CAR_LOCATION);
        int numCar = rm.queryCars(transId2, TEST_CARRESERVATION_CAR_LOCATION);
        Assert.assertEquals(TEST_CARRESERVATION_CAR_NUMCAR - 1, numCar); // makes sure it is 1 commit phase
        rm.commitTransaction(transId2);

        // reserve car then abort
        int transId3 = rm.startTransaction();
        rm.reserveCar(transId3, TEST_CARRESERVATION_CUSTOMER_ID, TEST_CARRESERVATION_CAR_LOCATION);
        int numCar2 = rm.queryCars(transId3, TEST_CARRESERVATION_CAR_LOCATION);
        Assert.assertEquals(TEST_CARRESERVATION_CAR_NUMCAR - 2, numCar2); // makes sure it is 1 commit phase
        rm.abortTransaction(transId3);

        int transId4 = rm.startTransaction();
        int numCarAfterAbort = rm.queryCars(transId4, TEST_CARRESERVATION_CAR_LOCATION);
        rm.commitTransaction(transId4);
        Assert.assertEquals(TEST_CARRESERVATION_CAR_NUMCAR - 1, numCarAfterAbort); // makes sure abort rolled back trans
    }

    private static final int TEST_CARRESERVATIONOPERATION_CUSTOMER_ID = RMITestUtils.newUniqueId();
    private static final String TEST_CARRESERVATIONOPERATION_CAR_LOCATION = "mtl" + RMITestUtils.newUniqueId();
    private static final int TEST_CARRESERVATIONOPERATION_CAR_NUMCAR = 10;
    private static final int TEST_CARRESERVATIONOPERATION_CAR_PRICE = 100;
    @Test
    public void testCarTransactionReservationsOperations() throws RemoteException {
        // create a customer
        int transId = rm.startTransaction();
        rm.newCustomer(transId, TEST_CARRESERVATIONOPERATION_CUSTOMER_ID);
        // create car
        rm.addCars(transId, TEST_CARRESERVATIONOPERATION_CAR_LOCATION, TEST_CARRESERVATIONOPERATION_CAR_NUMCAR, TEST_CARRESERVATIONOPERATION_CAR_PRICE);
        // reserve car
        rm.reserveCar(transId, TEST_CARRESERVATIONOPERATION_CUSTOMER_ID, TEST_CARRESERVATIONOPERATION_CAR_LOCATION);
        // commit
        rm.commitTransaction(transId);

        int transId2 = rm.startTransaction();
        // delete car
        rm.deleteCars(transId2, TEST_CARRESERVATIONOPERATION_CAR_LOCATION);
        // make sure car are removed from reservations
        String customerBill = rm.queryCustomerInfo(transId2, TEST_CARRESERVATIONOPERATION_CUSTOMER_ID);
        Assert.assertFalse(customerBill.contains(TEST_CARRESERVATIONOPERATION_CAR_LOCATION));
        // abort
        rm.abortTransaction(transId2);

        // make sure car are given back
        int transId3 = rm.startTransaction();
        String customerBill2 = rm.queryCustomerInfo(transId3, TEST_CARRESERVATIONOPERATION_CUSTOMER_ID);
        Assert.assertTrue(customerBill2.contains(TEST_CARRESERVATIONOPERATION_CAR_LOCATION));
        rm.commitTransaction(transId3);
    }
}

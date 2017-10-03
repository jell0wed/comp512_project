package tcp;

import junit.framework.TestSuite;
import middleware.impl.tcp.requests.impl.*;
import middleware.impl.tcp.responses.MiddlewareBaseTCPResponse;
import middleware.impl.tcp.responses.MiddlewareTCPResponseTypes;
import org.junit.BeforeClass;
import org.junit.Test;
import tcp.utils.TestUtils;

import java.io.IOException;

import static middleware.impl.tcp.responses.MiddlewareTCPResponseTypes.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by jpoisson on 2017-10-01.
 */
public class CarTestSuite extends TestSuite {
    @BeforeClass
    public static void initializeClient() throws IOException {
        TestUtils.initializeClientSock();
    }

    private static final String CAR_LOCATION = String.valueOf(TestUtils.newUniqueId());
    private static final int CAR_NUM = 10;
    private static final int CAR_PRICE = 100;
    @Test
    public void testAddQueryCar() throws IOException, ClassNotFoundException {
        // add a car
        AddCarsRequest addCartReq = new AddCarsRequest(CAR_LOCATION, CAR_NUM, CAR_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addCartReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // query it
        QueryCarRequest queryCarQtyReq = new QueryCarRequest(CAR_LOCATION);
        MiddlewareBaseTCPResponse respQty = TestUtils.send(queryCarQtyReq);
        assertEquals(MiddlewareTCPResponseTypes.INTEGER_RESPONSE, respQty.type);
        assertEquals(CAR_NUM, respQty.asIntegerResponse().value);
    }

    private static final String DELETE_CAR_LOC = String.valueOf(TestUtils.newUniqueId());
    @Test
    public void testDeleteCar() throws IOException, ClassNotFoundException {
        // add a car
        AddCarsRequest addCarReq = new AddCarsRequest(DELETE_CAR_LOC, 1, 100);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addCarReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // delete it
        DeleteCarRequest delCarReq = new DeleteCarRequest(DELETE_CAR_LOC);
        MiddlewareBaseTCPResponse delResp = TestUtils.send(delCarReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, delResp.type);
        assertTrue(delResp.asSuccessFailureResponse().success);

        // make sure it returns 0
        QueryCarRequest queryCarReq = new QueryCarRequest(DELETE_CAR_LOC);
        MiddlewareBaseTCPResponse carResp = TestUtils.send(queryCarReq);
        assertEquals(INTEGER_RESPONSE, carResp.type);
        assertEquals(0, carResp.asIntegerResponse().value);
    }

    private static final String QUERY_CAR_PRICE_LOC = String.valueOf(TestUtils.newUniqueId());
    private static final int QUERY_CAR_PRICE = 150;
    @Test
    public void testQueryCarPrice() throws IOException, ClassNotFoundException {
        // add a car
        AddCarsRequest addCarReq = new AddCarsRequest(QUERY_CAR_PRICE_LOC, 1, QUERY_CAR_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addCarReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // query price
        QueryCarPriceRequest carPriceReq = new QueryCarPriceRequest(QUERY_CAR_PRICE_LOC);
        MiddlewareBaseTCPResponse priceResp = TestUtils.send(carPriceReq);
        assertEquals(INTEGER_RESPONSE, priceResp.type);
        assertEquals(QUERY_CAR_PRICE, priceResp.asIntegerResponse().value);
    }

    private static final String RESERVE_CAR_LOC = String.valueOf(TestUtils.newUniqueId());
    private static final int RESERVE_CAR_PRICE = 150;
    private static final int RESERVE_CAR_NUM = 10;
    private static final int RESERVE_CAR_CUST_ID = TestUtils.newUniqueId();
    @Test
    public void testReserveCar() throws IOException, ClassNotFoundException {
        // add a car
        AddCarsRequest addCarReq = new AddCarsRequest(RESERVE_CAR_LOC, RESERVE_CAR_NUM, RESERVE_CAR_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addCarReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // add a customer with given id
        NewCustomerWithIdRequest newCustReq = new NewCustomerWithIdRequest(RESERVE_CAR_CUST_ID);
        MiddlewareBaseTCPResponse newCustResp = TestUtils.send(newCustReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertTrue(newCustResp.asSuccessFailureResponse().success);

        // reserve the car
        ReserveCarRequest reserveCarReq = new ReserveCarRequest(RESERVE_CAR_CUST_ID, RESERVE_CAR_LOC);
        MiddlewareBaseTCPResponse reserveCarResp = TestUtils.send(reserveCarReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, reserveCarResp.type);
        assertTrue(reserveCarResp.asSuccessFailureResponse().success);

        // make sure the car has one less avail.
        QueryCarRequest queryCarReq = new QueryCarRequest(RESERVE_CAR_LOC);
        MiddlewareBaseTCPResponse queryCarResp = TestUtils.send(queryCarReq);
        assertEquals(INTEGER_RESPONSE, queryCarResp.type);
        assertEquals(RESERVE_CAR_NUM - 1, queryCarResp.asIntegerResponse().value);

        // make sure the customer has the car on his bill
        QueryCustomerInfoRequest queryCustInfoReq = new QueryCustomerInfoRequest(RESERVE_CAR_CUST_ID);
        MiddlewareBaseTCPResponse queryCustInfoResp = TestUtils.send(queryCustInfoReq);
        assertEquals(STRING_RESPONSE, queryCustInfoResp.type);
        assertTrue(queryCustInfoResp.asStringResponse().value.contains(String.valueOf("car-" + String.valueOf(RESERVE_CAR_LOC))));
    }
}

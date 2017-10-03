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
public class FlightTestSuite extends TestSuite {
    @BeforeClass
    public static void initializeClient() throws IOException {
        TestUtils.initializeClientSock();
    }

    private static final int FLIGHT_NO = TestUtils.newUniqueId();
    private static final int FLIGHT_SEATS = 10;
    private static final int FLIGHT_PRICE = 100;
    @Test
    public void testAddQueryFlight() throws IOException, ClassNotFoundException {
        // add a flight
        AddFlightRequest addFlightReq = new AddFlightRequest(FLIGHT_NO, FLIGHT_SEATS, FLIGHT_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addFlightReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // query it
        QueryFlightRequest queryFlightQtyReq = new QueryFlightRequest(FLIGHT_NO);
        MiddlewareBaseTCPResponse respQty = TestUtils.send(queryFlightQtyReq);
        assertEquals(MiddlewareTCPResponseTypes.INTEGER_RESPONSE, respQty.type);
        assertEquals(FLIGHT_SEATS, respQty.asIntegerResponse().value);
    }

    private static final int DELETE_FLIGHT_NO = TestUtils.newUniqueId();
    @Test
    public void testDeleteFlight() throws IOException, ClassNotFoundException {
        // add a flight
        AddFlightRequest addFlightReq = new AddFlightRequest(DELETE_FLIGHT_NO, 1, 100);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addFlightReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // delete it
        DeleteFlightRequest delFlightReq = new DeleteFlightRequest(DELETE_FLIGHT_NO);
        MiddlewareBaseTCPResponse delResp = TestUtils.send(delFlightReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, delResp.type);
        assertTrue(delResp.asSuccessFailureResponse().success);

        // make sure it returns 0
        QueryFlightRequest queryFlightReq = new QueryFlightRequest(DELETE_FLIGHT_NO);
        MiddlewareBaseTCPResponse flightResp = TestUtils.send(queryFlightReq);
        assertEquals(INTEGER_RESPONSE, flightResp.type);
        assertEquals(0, flightResp.asIntegerResponse().value);
    }

    private static final int QUERY_FLIGHT_PRICE_NO = TestUtils.newUniqueId();
    private static final int QUERY_FLIGHT_PRICE = 150;
    @Test
    public void testQueryFlightPrice() throws IOException, ClassNotFoundException {
        // add a flight
        AddFlightRequest addFlightReq = new AddFlightRequest(QUERY_FLIGHT_PRICE_NO, 1, QUERY_FLIGHT_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addFlightReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // query price
        QueryFlightPriceRequest flightPriceReq = new QueryFlightPriceRequest(QUERY_FLIGHT_PRICE_NO);
        MiddlewareBaseTCPResponse priceResp = TestUtils.send(flightPriceReq);
        assertEquals(INTEGER_RESPONSE, priceResp.type);
        assertEquals(QUERY_FLIGHT_PRICE, priceResp.asIntegerResponse().value);
    }

    private static final int RESERVE_FLIGHT_NO = TestUtils.newUniqueId();
    private static final int RESERVE_FLIGHT_PRICE = 150;
    private static final int RESERVE_FLIGHT_SEATS = 10;
    private static final int RESERVE_FLIGHT_CUST_ID = TestUtils.newUniqueId();
    @Test
    public void testReserveFlight() throws IOException, ClassNotFoundException {
        // add a flight
        AddFlightRequest addFlightReq = new AddFlightRequest(RESERVE_FLIGHT_NO, RESERVE_FLIGHT_SEATS, RESERVE_FLIGHT_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addFlightReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // add a customer with given id
        NewCustomerWithIdRequest newCustReq = new NewCustomerWithIdRequest(RESERVE_FLIGHT_CUST_ID);
        MiddlewareBaseTCPResponse newCustResp = TestUtils.send(newCustReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertTrue(newCustResp.asSuccessFailureResponse().success);

        // reserve the flight
        ReserveFlightRequest reserveFlightReq = new ReserveFlightRequest(RESERVE_FLIGHT_CUST_ID, RESERVE_FLIGHT_NO);
        MiddlewareBaseTCPResponse reserveFlightResp = TestUtils.send(reserveFlightReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, reserveFlightResp.type);
        assertTrue(reserveFlightResp.asSuccessFailureResponse().success);

        // make sure the flight has one less seat
        QueryFlightRequest queryFlightSeatsReq = new QueryFlightRequest(RESERVE_FLIGHT_NO);
        MiddlewareBaseTCPResponse queryFlightSeatsResp = TestUtils.send(queryFlightSeatsReq);
        assertEquals(INTEGER_RESPONSE, queryFlightSeatsResp.type);
        assertEquals(RESERVE_FLIGHT_SEATS - 1, queryFlightSeatsResp.asIntegerResponse().value);

        // make sure the customer has the flight on his bill
        QueryCustomerInfoRequest queryCustInfoReq = new QueryCustomerInfoRequest(RESERVE_FLIGHT_CUST_ID);
        MiddlewareBaseTCPResponse queryCustInfoResp = TestUtils.send(queryCustInfoReq);
        assertEquals(STRING_RESPONSE, queryCustInfoResp.type);
        assertTrue(queryCustInfoResp.asStringResponse().value.contains(String.valueOf("flight-" + String.valueOf(RESERVE_FLIGHT_NO))));
    }
}

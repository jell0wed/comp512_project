package tcp;

import junit.framework.TestSuite;
import protocol.requests.impl.*;
import protocol.responses.BaseTCPResponse;
import protocol.responses.TCPResponseTypes;
import org.junit.BeforeClass;
import org.junit.Test;
import tcp.utils.TestUtils;

import java.io.IOException;
import java.util.Vector;

import static protocol.responses.TCPResponseTypes.STRING_RESPONSE;
import static protocol.responses.TCPResponseTypes.SUCCESS_FAILURE_RESPONSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jpoisson on 2017-10-02.
 */
public class CustomerTestSuite extends TestSuite {
    @BeforeClass
    public static void initializeClient() throws IOException {
        TestUtils.initializeClientSock();
    }

    @Test
    public void testNewCustomer() throws IOException, ClassNotFoundException {
        // add customer
        NewCustomerRequest newCustReq = new NewCustomerRequest();
        BaseTCPResponse resp = TestUtils.send(newCustReq);
        assertEquals(TCPResponseTypes.INTEGER_RESPONSE, resp.type);

        // make sure specified user exists
        QueryCustomerInfoRequest queryCustInfoReq = new QueryCustomerInfoRequest(resp.asIntegerResponse().value);
        BaseTCPResponse respCustInfo = TestUtils.send(queryCustInfoReq);
        assertEquals(TCPResponseTypes.STRING_RESPONSE, respCustInfo.type);
        assertTrue(respCustInfo.asStringResponse().value.contains(String.valueOf(resp.asIntegerResponse().value)));
    }

    public static final int NEW_CUSTOMER_WITH_ID = TestUtils.newUniqueId();
    @Test
    public void testNewCustomerWithId() throws IOException, ClassNotFoundException {
        // add customer
        NewCustomerWithIdRequest newCustIdReq = new NewCustomerWithIdRequest(NEW_CUSTOMER_WITH_ID);
        BaseTCPResponse resp = TestUtils.send(newCustIdReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertTrue(resp.asSuccessFailureResponse().success);

        // make sure specified user exists
        QueryCustomerInfoRequest queryCustInfoReq = new QueryCustomerInfoRequest(NEW_CUSTOMER_WITH_ID);
        BaseTCPResponse respCustInfo = TestUtils.send(queryCustInfoReq);
        assertEquals(TCPResponseTypes.STRING_RESPONSE, respCustInfo.type);
        assertTrue(respCustInfo.asStringResponse().value.contains(String.valueOf(NEW_CUSTOMER_WITH_ID)));
    }

    public static final int DELETE_CUSTOMER_ID = TestUtils.newUniqueId();
    @Test
    public void testDeleteCustomer() throws IOException, ClassNotFoundException {
        // add customer
        NewCustomerWithIdRequest newCustIdReq = new NewCustomerWithIdRequest(DELETE_CUSTOMER_ID);
        BaseTCPResponse resp = TestUtils.send(newCustIdReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertTrue(resp.asSuccessFailureResponse().success);

        // delete customer
        DeleteCustomerRequest delCustReq = new DeleteCustomerRequest(DELETE_CUSTOMER_ID);
        BaseTCPResponse delCustResp = TestUtils.send(delCustReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, delCustResp.type);
        assertTrue(delCustResp.asSuccessFailureResponse().success);

        // make sure the specified user does not exists
        QueryCustomerInfoRequest queryCustInfoReq = new QueryCustomerInfoRequest(DELETE_CUSTOMER_ID);
        BaseTCPResponse respCustInfo = TestUtils.send(queryCustInfoReq);
        assertEquals(STRING_RESPONSE, respCustInfo.type);
        assertEquals("", respCustInfo.asStringResponse().value);
    }

    // TODO: Make sure to test when reserving car rooms flight
    public static final int ITINERARY_NEW_CUST_ID = TestUtils.newUniqueId();

    public static final int ITINERARY_FLIGHT_1_NO = TestUtils.newUniqueId();
    public static final int ITINERARY_FLIGHT_1_SEATS = 10;
    public static final int ITINERARY_FLIGHT_1_PRICE = 100;

    public static final int ITINERARY_FLIGHT_2_NO = TestUtils.newUniqueId();
    public static final int ITINERARY_FLIGHT_2_SEATS = 5;
    public static final int ITINERARY_FLIGHT_2_PRICE = 500;

    public static final String ITINERARY_CAR_ROOM_LOC = String.valueOf(TestUtils.newUniqueId());

    public static final int ITINERARY_CAR_NUM = 10;
    public static final int ITINERARY_CAR_PRICE = 200;

    public static final int ITINERARY_ROOM_NUM = 20;
    public static final int ITINERARY_ROOM_PRICE = 1000;
    @Test
    public void testItinerary() throws IOException, ClassNotFoundException {
        // add customer
        NewCustomerWithIdRequest newCustReq = new NewCustomerWithIdRequest(ITINERARY_NEW_CUST_ID);
        BaseTCPResponse newCustResp = TestUtils.send(newCustReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, newCustResp.type);
        assertTrue(newCustResp.asSuccessFailureResponse().success);

        // add 2 flights
        AddFlightRequest flight1AddReq = new AddFlightRequest(ITINERARY_FLIGHT_1_NO, ITINERARY_FLIGHT_1_SEATS, ITINERARY_FLIGHT_1_PRICE);
        BaseTCPResponse flight1AddResp = TestUtils.send(flight1AddReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, flight1AddResp.type);
        assertTrue(flight1AddResp.asSuccessFailureResponse().success);

        AddFlightRequest flight2AddReq = new AddFlightRequest(ITINERARY_FLIGHT_2_NO, ITINERARY_FLIGHT_2_SEATS, ITINERARY_FLIGHT_2_PRICE);
        BaseTCPResponse flight2AddResp = TestUtils.send(flight2AddReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, flight2AddResp.type);
        assertTrue(flight2AddResp.asSuccessFailureResponse().success);

        // add car
        AddCarsRequest carAddReq = new AddCarsRequest(ITINERARY_CAR_ROOM_LOC, ITINERARY_CAR_NUM, ITINERARY_CAR_PRICE);
        BaseTCPResponse addCarResp = TestUtils.send(carAddReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, addCarResp.type);
        assertTrue(addCarResp.asSuccessFailureResponse().success);

        // add room
        AddRoomsRequest roomAddReq = new AddRoomsRequest(ITINERARY_CAR_ROOM_LOC, ITINERARY_ROOM_NUM, ITINERARY_ROOM_PRICE);
        BaseTCPResponse addRoomResp = TestUtils.send(roomAddReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, addRoomResp.type);
        assertTrue(addRoomResp.asSuccessFailureResponse().success);

        // book itinerary
        Vector<String> flightNos = new Vector<>();
        flightNos.add(String.valueOf(ITINERARY_FLIGHT_1_NO));
        flightNos.add(String.valueOf(ITINERARY_FLIGHT_2_NO));

        ItineraryRequest itiReq = new ItineraryRequest(ITINERARY_NEW_CUST_ID, flightNos, ITINERARY_CAR_ROOM_LOC, true, true);
        BaseTCPResponse itiResp = TestUtils.send(itiReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, itiResp.type);
        assertTrue(itiResp.asSuccessFailureResponse().success);

        // make sure the bill contains ALL of the reserved stuff
        QueryCustomerInfoRequest custInfoReq = new QueryCustomerInfoRequest(ITINERARY_NEW_CUST_ID);
        BaseTCPResponse custInfoResp = TestUtils.send(custInfoReq);
        assertEquals(STRING_RESPONSE, custInfoResp.type);
        assertTrue(custInfoResp.asStringResponse().value.contains("flight-" + String.valueOf(ITINERARY_FLIGHT_1_NO)));
        assertTrue(custInfoResp.asStringResponse().value.contains("flight-" + String.valueOf(ITINERARY_FLIGHT_2_NO)));
        assertTrue(custInfoResp.asStringResponse().value.contains("car-" + String.valueOf(ITINERARY_CAR_ROOM_LOC)));
        assertTrue(custInfoResp.asStringResponse().value.contains("room-" + String.valueOf(ITINERARY_CAR_ROOM_LOC)));
    }

}

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
public class RoomTestSuite extends TestSuite {
    @BeforeClass
    public static void initializeClient() throws IOException {
        TestUtils.initializeClientSock();
    }

    private static final String ROOM_LOCATION = String.valueOf(TestUtils.newUniqueId());
    private static final int ROOM_NUM = 10;
    private static final int ROOM_PRICE = 100;
    @Test
    public void testAddQueryRoom() throws IOException, ClassNotFoundException {
        // add a room
        AddRoomsRequest addRoomReq = new AddRoomsRequest(ROOM_LOCATION, ROOM_NUM, ROOM_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addRoomReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // query it
        QueryRoomRequest queryRoomQtyReq = new QueryRoomRequest(ROOM_LOCATION);
        MiddlewareBaseTCPResponse respQty = TestUtils.send(queryRoomQtyReq);
        assertEquals(MiddlewareTCPResponseTypes.INTEGER_RESPONSE, respQty.type);
        assertEquals(ROOM_NUM, respQty.asIntegerResponse().value);
    }

    private static final String DELETE_ROOM_LOC = String.valueOf(TestUtils.newUniqueId());
    @Test
    public void testDeleteRoom() throws IOException, ClassNotFoundException {
        // add a room
        AddRoomsRequest addRoomReq = new AddRoomsRequest(DELETE_ROOM_LOC, 1, 100);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addRoomReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // delete it
        DeleteRoomRequest delRoomReq = new DeleteRoomRequest(DELETE_ROOM_LOC);
        MiddlewareBaseTCPResponse delResp = TestUtils.send(delRoomReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, delResp.type);
        assertTrue(delResp.asSuccessFailureResponse().success);

        // make sure it returns 0
        QueryRoomRequest queryRoomReq = new QueryRoomRequest(DELETE_ROOM_LOC);
        MiddlewareBaseTCPResponse roomResp = TestUtils.send(queryRoomReq);
        assertEquals(INTEGER_RESPONSE, roomResp.type);
        assertEquals(0, roomResp.asIntegerResponse().value);
    }

    private static final String QUERY_ROOM_PRICE_LOC = String.valueOf(TestUtils.newUniqueId());
    private static final int QUERY_ROOM_PRICE = 150;
    @Test
    public void testQueryRoomPrice() throws IOException, ClassNotFoundException {
        // add a room
        AddRoomsRequest addRoomReq = new AddRoomsRequest(QUERY_ROOM_PRICE_LOC, 1, QUERY_ROOM_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addRoomReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // query price
        QueryRoomPriceRequest roomPriceReq = new QueryRoomPriceRequest(QUERY_ROOM_PRICE_LOC);
        MiddlewareBaseTCPResponse priceResp = TestUtils.send(roomPriceReq);
        assertEquals(INTEGER_RESPONSE, priceResp.type);
        assertEquals(QUERY_ROOM_PRICE, priceResp.asIntegerResponse().value);
    }

    private static final String RESERVE_ROOM_LOC = String.valueOf(TestUtils.newUniqueId());
    private static final int RESERVE_ROOM_PRICE = 150;
    private static final int RESERVE_ROOM_NUM = 10;
    private static final int RESERVE_ROOM_CUST_ID = TestUtils.newUniqueId();
    @Test
    public void testReserveRoom() throws IOException, ClassNotFoundException {
        // add a room
        AddRoomsRequest addRoomReq = new AddRoomsRequest(RESERVE_ROOM_LOC, RESERVE_ROOM_NUM, RESERVE_ROOM_PRICE);
        MiddlewareBaseTCPResponse resp = TestUtils.send(addRoomReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertEquals(true, resp.asSuccessFailureResponse().success);

        // add a customer with given id
        NewCustomerWithIdRequest newCustReq = new NewCustomerWithIdRequest(RESERVE_ROOM_CUST_ID);
        MiddlewareBaseTCPResponse newCustResp = TestUtils.send(newCustReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, resp.type);
        assertTrue(newCustResp.asSuccessFailureResponse().success);

        // reserve the room
        ReserveRoomRequest reserveRoomReq = new ReserveRoomRequest(RESERVE_ROOM_CUST_ID, RESERVE_ROOM_LOC);
        MiddlewareBaseTCPResponse reserveRoomResp = TestUtils.send(reserveRoomReq);
        assertEquals(SUCCESS_FAILURE_RESPONSE, reserveRoomResp.type);
        assertTrue(reserveRoomResp.asSuccessFailureResponse().success);

        // make sure the room has one less avail.
        QueryRoomRequest queryRoomReq = new QueryRoomRequest(RESERVE_ROOM_LOC);
        MiddlewareBaseTCPResponse queryRoomResp = TestUtils.send(queryRoomReq);
        assertEquals(INTEGER_RESPONSE, queryRoomResp.type);
        assertEquals(RESERVE_ROOM_NUM - 1, queryRoomResp.asIntegerResponse().value);

        // make sure the customer has the flight on his bill
        QueryCustomerInfoRequest queryCustInfoReq = new QueryCustomerInfoRequest(RESERVE_ROOM_CUST_ID);
        MiddlewareBaseTCPResponse queryCustInfoResp = TestUtils.send(queryCustInfoReq);
        assertEquals(STRING_RESPONSE, queryCustInfoResp.type);
        assertTrue(queryCustInfoResp.asStringResponse().value.contains(String.valueOf("room-" + String.valueOf(RESERVE_ROOM_LOC))));
    }
}

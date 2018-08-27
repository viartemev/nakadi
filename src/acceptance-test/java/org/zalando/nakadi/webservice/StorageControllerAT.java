package org.zalando.nakadi.webservice;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.zalando.nakadi.utils.EventTypeTestBuilder;
import org.zalando.nakadi.webservice.utils.NakadiTestUtils;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;

public class StorageControllerAT extends BaseAT {

    @Ignore("Temporary ignore this test (zk_address should be passed as an env argument)")
    @Test
    public void shouldChangeDefaultStorageWhenRequested() throws Exception {
        given()
                .body("{\"id\": \"default-test\",\"kafka_configuration\": {\"exhibitor_address\": null," +
                        "\"exhibitor_port\": 0,\"zk_address\": \"127.0.0.1:2181\",\"zk_path\": \"\"}," +
                        "\"storage_type\": \"kafka\"}")
                .contentType(JSON).post("/storages");

        NakadiTestUtils.createEventTypeInNakadi(EventTypeTestBuilder.builder().name("event_a").build());
        String storageId = (String) NakadiTestUtils.listTimelines("event_a").get(0).get("storage_id");
        Assert.assertEquals("default", storageId);

        given().contentType(JSON).put("/storages/default/default-test");
        NakadiTestUtils.createEventTypeInNakadi(EventTypeTestBuilder.builder().name("event_b").build());
        storageId = (String) NakadiTestUtils.listTimelines("event_b").get(0).get("storage_id");
        Assert.assertEquals("default-test", storageId);

        // cleanup
        given().contentType(JSON).put("/storages/default/default");
    }
}

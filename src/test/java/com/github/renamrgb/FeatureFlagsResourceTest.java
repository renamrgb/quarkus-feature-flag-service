package com.github.renamrgb;

import com.github.renamrgb.domain.FeatureFlag;
import com.github.renamrgb.infra.rest.FeatureFlagResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(FeatureFlagResource.class)
class FeatureFlagsResourceTest {

    @Test
    void testCreateWithSuccess() {
        FeatureFlag featureFlag = getFeatureFlag();

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .body(featureFlag)
                .post()
          .then()
                .statusCode(201);

    }

    private FeatureFlag getFeatureFlag() {
        return new FeatureFlag("flag", "identity", "module");
    }
}
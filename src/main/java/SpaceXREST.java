import io.restassured.path.json.JsonPath;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.List;

public class SpaceXREST {

    Client client;

    public SpaceXREST() {
        client = ClientBuilder.newBuilder().register(JacksonFeature.class).register(MultiPartFeature.class).build();
        client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);
        client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
    }

    private Response getRequest(String uri) {
        client = ClientBuilder.newClient();
        Response res = client.target(uri).request("text/plain").get();
        return res;
    }

    @Test(description = "Get all launches details ")
    public void getLaunches() {
        try {
            Response res = getRequest("https://api.spacexdata.com/v4/launches/latest");
            Assert.assertEquals(res.getStatus(), 200, "Not able to get company details");
            String outputCompany = res.readEntity(String.class);
            System.out.println(outputCompany);
            String smallLink = JsonPath.with(outputCompany).get("links.patch.small");
            String largeLink = JsonPath.with(outputCompany).get("links.patch.large");

            System.out.println("Small link:" + smallLink);
            System.out.println("large link:" + largeLink);

            Assert.assertTrue(!smallLink.isEmpty(), "Small links should not be empty");
            Assert.assertTrue(!largeLink.isEmpty(), "large links should not be empty");

        } catch (Exception e) {
            System.out.println("Error while getting links details ");
        }

    }


    @Test(description = "Check ship core ")
    public void checkShipCoreIsNotBlank() {
        try {
            Response res = getRequest("https://api.spacexdata.com/v4/launches/latest");
            Assert.assertEquals(res.getStatus(), 200, "Not able to get company details");
            String output = res.readEntity(String.class);
            System.out.println(output);
            List<String> shipCore = JsonPath.with(output).get("cores");

            Assert.assertEquals(shipCore.size(), 1, "Core element not found");
            String coreId = JsonPath.with(output).get(
                    "cores[" + 0 + "].core");
            Assert.assertTrue(!shipCore.get(0).isEmpty(), "Core ID should not be blank");

        } catch (Exception e) {
            System.out.println("Error while getting core details ");
        }

    }


    @Test(description = "Check flight number should not blank ")
    public void checkFlightNumberIsNotBlank() {
        try {
            Response res = getRequest("https://api.spacexdata.com/v4/launches/latest");
            Assert.assertEquals(res.getStatus(), 200, "Not able to get company details");
            String output = res.readEntity(String.class);
            System.out.println(output);
            String flightNumber = JsonPath.with(output).get("flight_number");
            Assert.assertTrue(!flightNumber.isEmpty(), "Core ID should not be blank");

        } catch (Exception e) {
            System.out.println("Error while getting flight number ");
        }

    }


}

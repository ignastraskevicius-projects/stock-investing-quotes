package org.ignast.stockinvesting.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CompanyTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldDefineCompany() throws JSONException {
        String url = "http://localhost:" + port + "/";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.stockinvesting.estimates-v1.hal+json");
        ResponseEntity<String> rootResponse = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertThat(rootResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JSONObject root = new JSONObject(rootResponse.getBody());

        String companiesHref = root.getJSONObject("_links").getJSONObject("stocks:company").getString("href");
        ResponseEntity<String> companyDefinition = restTemplate.postForEntity(companiesHref, "", String.class);
        assertThat(companyDefinition.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
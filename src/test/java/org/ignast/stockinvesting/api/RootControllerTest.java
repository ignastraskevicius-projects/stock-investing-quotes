package org.ignast.stockinvesting.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.ignast.stockinvesting.api.test.HateoasJsonMatchers.hasRel;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ RootController.class, HalConfig.class })
public class RootControllerTest {

    private @Autowired MockMvc mockMvc;

    private String V1_MEDIA_TYPE = "application/vnd.stockinvesting.estimates-v1.hal+json";

    @Test
    public void rootResourceShouldLinkToCompanies() throws Exception {
        ResultActions root = mockMvc.perform(get("/").accept(V1_MEDIA_TYPE));
        root.andExpect(status().isOk()).andExpect(header().string(CONTENT_TYPE, V1_MEDIA_TYPE))
                .andExpect(content().string(hasRel("stocks:company").withHrefContaining("/companies")));
    }

    @Test
    public void shouldRejectUnversionedRequests() throws Exception {
        mockMvc.perform(get("/").accept("application/hal+json")).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldRejectNonHalRequests() throws Exception {
        mockMvc.perform(get("/").accept("application/json")).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldNotBeModifiableResource() throws Exception {
        mockMvc.perform(post("/")).andExpect(status().isMethodNotAllowed());
    }
}

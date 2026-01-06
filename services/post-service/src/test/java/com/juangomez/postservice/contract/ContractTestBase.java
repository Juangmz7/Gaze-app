package com.juangomez.postservice.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.juangomez.postservice.controller.PostController;
import com.juangomez.postservice.model.dto.CreatePostRequest;
import com.juangomez.postservice.model.dto.CreatePostResponse;
import com.juangomez.postservice.service.contract.PostService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = com.juangomez.postservice.PostServiceApplication.class)
public class ContractTestBase {

    @Autowired
    private PostController postController;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RestAssuredMockMvc.standaloneSetup(
                MockMvcBuilders.standaloneSetup(postController)
                        .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
        );

        // Define the Mock Response
        CreatePostResponse mockResponse = new CreatePostResponse();
        mockResponse.setPostId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        mockResponse.setCreatedAt(OffsetDateTime.now());
        mockResponse.setUpdatedAt(OffsetDateTime.now());

        given(postService.createPendingPost(any(CreatePostRequest.class)))
                .willReturn(mockResponse);
    }
}
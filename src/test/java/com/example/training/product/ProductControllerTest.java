package com.example.training.product;

import com.example.training.WithRedis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WithRedis
public class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void clearCache() {
    try {
      // Reset the cache before each test
      mockMvc.perform(put("/product/resetPlansAndAddonsCache")
              .param("setUpFee", "true")
              .param("category", "true"))
          .andExpect(status().isOk());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void testResetPlansAndAddonsCache() throws Exception {
    // Test resetting the cache with specific brandId
    String brandId = "test-brand";

    MvcResult result = mockMvc.perform(put("/product/resetPlansAndAddonsCache")
            .param("brandId", brandId)
            .param("setUpFee", "true")
            .param("category", "true"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
        .andReturn();

    // Parse response
    String responseContent = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseContent);

    // Verify response indicates success
    assertTrue(jsonNode.path("success").asBoolean());
    assertTrue(jsonNode.has(brandId));
    assertTrue(jsonNode.path(brandId).asBoolean());
  }

  @Test
  void testResetPlansAndAddonsCacheWithoutBrandId() throws Exception {
    // Test resetting the cache without providing a brandId
    MvcResult result = mockMvc.perform(put("/product/resetPlansAndAddonsCache")
            .param("setUpFee", "true")
            .param("category", "true"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
        .andReturn();

    // Parse response
    String responseContent = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseContent);

    // Verify response indicates success
    assertTrue(jsonNode.path("success").asBoolean());
  }

  @Test
  void testGetStaticPlansAndAddonsV2() throws Exception {
    // First reset cache for specific brand to ensure data exists
    String brandId = "test-brand-123";

    mockMvc.perform(put("/product/resetPlansAndAddonsCache")
            .param("brandId", brandId)
            .param("setUpFee", "true")
            .param("category", "true"))
        .andExpect(status().isOk());

    // Then test getting the plans and addons for that brand
    MvcResult result = mockMvc.perform(get("/product/v2/getStaticPlansAndAddons/{brandId}", brandId)
            .param("category", "premium")
            .param("addKeyForFreeTrialPlan", "true"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andReturn();

    // Parse response
    String responseContent = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseContent);

    // Verify response
    assertEquals("success", jsonNode.path("status").asText());
    assertTrue(jsonNode.has("data"));
    assertTrue(jsonNode.path("data").has("plans"));
    assertTrue(jsonNode.path("data").has("addons"));
  }

  @Test
  void testGetStaticPlansAndAddonsV2WithoutCache() throws Exception {
    // Test getting plans and addons for a brand that's not in cache
    // This should still work by generating mock data
    String brandId = "uncached-brand";

    MvcResult result = mockMvc.perform(get("/product/v2/getStaticPlansAndAddons/{brandId}", brandId)
            .param("addKeyForFreeTrialPlan", "false"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andReturn();

    // Parse response
    String responseContent = result.getResponse().getContentAsString();
    JsonNode jsonNode = objectMapper.readTree(responseContent);

    // Verify response
    assertEquals("success", jsonNode.path("status").asText());
    assertTrue(jsonNode.has("data"));
    assertTrue(jsonNode.path("data").has("plans"));
    assertTrue(jsonNode.path("data").has("addons"));
  }
}

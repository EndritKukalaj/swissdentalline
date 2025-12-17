package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.TestDataCleanup;
import ch.zhaw.swissdentalline.dto.BehandlungsartCreateDTO;
import ch.zhaw.swissdentalline.security.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BehandlungsartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String behandlungsartId;

    @Test
    @Order(1)
    public void testCreateBehandlungsartAsZahnarzt() throws Exception {
        BehandlungsartCreateDTO behandlungsartDTO = new BehandlungsartCreateDTO();
        behandlungsartDTO.setName("Zahnreinigung");
        behandlungsartDTO.setBeschreibung("Professionelle Zahnreinigung");

        MvcResult result = mockMvc.perform(post("/api/behandlungsarten")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(behandlungsartDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Zahnreinigung"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        behandlungsartId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(2)
    public void testCreateBehandlungsartAsPatient_Forbidden() throws Exception {
        BehandlungsartCreateDTO behandlungsartDTO = new BehandlungsartCreateDTO();
        behandlungsartDTO.setName("Wurzelbehandlung");
        behandlungsartDTO.setBeschreibung("Endodontische Behandlung");

        mockMvc.perform(post("/api/behandlungsarten")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(behandlungsartDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    public void testGetBehandlungsartByIdAsPatient() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten/{id}", behandlungsartId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(behandlungsartId))
                .andExpect(jsonPath("$.name").value("Zahnreinigung"));
    }

    @Test
    @Order(4)
    public void testGetBehandlungsartByIdAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten/{id}", behandlungsartId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(behandlungsartId))
                .andExpect(jsonPath("$.name").value("Zahnreinigung"));
    }

    @Test
    @Order(5)
    public void testGetAllBehandlungsartenAsPatient() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(6)
    public void testGetAllBehandlungsartenAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(7)
    public void testUpdateBehandlungsartAsZahnarzt() throws Exception {
        BehandlungsartCreateDTO updateDTO = new BehandlungsartCreateDTO();
        updateDTO.setName("Zahnreinigung Updated");
        updateDTO.setBeschreibung("Professionelle Zahnreinigung Updated");

        mockMvc.perform(put("/api/behandlungsarten/{id}", behandlungsartId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Zahnreinigung Updated"));
    }

    @Test
    @Order(8)
    public void testUpdateBehandlungsartAsPatient_Forbidden() throws Exception {
        BehandlungsartCreateDTO updateDTO = new BehandlungsartCreateDTO();
        updateDTO.setName("Unauthorized Update");
        updateDTO.setBeschreibung("This should not work");

        mockMvc.perform(put("/api/behandlungsarten/{id}", behandlungsartId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(9)
    public void testDeleteBehandlungsartAsPatient_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/behandlungsarten/{id}", behandlungsartId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    public void testDeleteBehandlungsartAsZahnarzt() throws Exception {
        mockMvc.perform(delete("/api/behandlungsarten/{id}", behandlungsartId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Additional tests for better coverage

    @Test
    @Order(11)
    public void testCreateBehandlungsartWithoutAuth_Forbidden() throws Exception {
        BehandlungsartCreateDTO behandlungsartDTO = new BehandlungsartCreateDTO();
        behandlungsartDTO.setName("Test");
        behandlungsartDTO.setBeschreibung("Test");

        mockMvc.perform(post("/api/behandlungsarten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(behandlungsartDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(12)
    public void testGetBehandlungsartById_NotFound() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(13)
    public void testGetBehandlungsartByIdWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten/{id}", behandlungsartId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(14)
    public void testGetAllBehandlungsartenWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(15)
    public void testUpdateBehandlungsartWithoutAuth_Forbidden() throws Exception {
        BehandlungsartCreateDTO updateDTO = new BehandlungsartCreateDTO();
        updateDTO.setName("Test");
        updateDTO.setBeschreibung("Test");

        mockMvc.perform(put("/api/behandlungsarten/{id}", behandlungsartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(16)
    public void testUpdateBehandlungsart_NotFound() throws Exception {
        BehandlungsartCreateDTO updateDTO = new BehandlungsartCreateDTO();
        updateDTO.setName("Test");
        updateDTO.setBeschreibung("Test");

        mockMvc.perform(put("/api/behandlungsarten/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(17)
    public void testDeleteBehandlungsartWithoutAuth_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/behandlungsarten/{id}", behandlungsartId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(18)
    public void testDeleteBehandlungsart_NotFound() throws Exception {
        mockMvc.perform(delete("/api/behandlungsarten/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound());
    }

    // Tests for exception handling paths to achieve 100% coverage
    @Test
    @Order(19)
    public void testGetBehandlungsartByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    // Tests with GUEST role to achieve 100% branch coverage
    @Test
    @Order(20)
    public void testGetBehandlungsartByIdAsGuest() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(21)
    public void testGetAllBehandlungsartenAsGuest() throws Exception {
        mockMvc.perform(get("/api/behandlungsarten")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

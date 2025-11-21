package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.TestDataCleanup;
import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.model.AdressTyp;
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
public class ZahnarztControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String zahnarztId;
    private static String praxisAdresseId;

    @Test
    @Order(1)
    public void testCreatePraxisAdresseAsZahnarzt() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Bahnhofstrasse 10");
        adresseDTO.setPlz("8001");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.PRAXIS);
        adresseDTO.setBezeichnung("Zahnarztpraxis Zürich");

        MvcResult result = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        praxisAdresseId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(2)
    public void testCreateZahnarztAsZahnarzt() throws Exception {
        ZahnarztCreateDTO zahnarztDTO = new ZahnarztCreateDTO();
        zahnarztDTO.setName("Dr. Hans Müller");
        zahnarztDTO.setPraxisAdresseId(praxisAdresseId);

        MvcResult result = mockMvc.perform(post("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zahnarztDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Dr. Hans Müller"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        zahnarztId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(3)
    public void testCreateZahnarztAsPatient_Forbidden() throws Exception {
        ZahnarztCreateDTO zahnarztDTO = new ZahnarztCreateDTO();
        zahnarztDTO.setName("Dr. Anna Schmidt");
        zahnarztDTO.setPraxisAdresseId(praxisAdresseId);

        mockMvc.perform(post("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zahnarztDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    public void testGetZahnarztByIdAsPatient() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/{id}", zahnarztId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(zahnarztId))
                .andExpect(jsonPath("$.name").value("Dr. Hans Müller"));
    }

    @Test
    @Order(5)
    public void testGetZahnarztByIdAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/{id}", zahnarztId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(zahnarztId))
                .andExpect(jsonPath("$.name").value("Dr. Hans Müller"));
    }

    @Test
    @Order(6)
    public void testGetAllZahnaerzteAsPatient() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(7)
    public void testGetAllZahnaerzteAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(8)
    public void testUpdateZahnarztAsZahnarzt() throws Exception {
        ZahnarztCreateDTO updateDTO = new ZahnarztCreateDTO();
        updateDTO.setName("Dr. Hans Müller Updated");
        updateDTO.setPraxisAdresseId(praxisAdresseId);

        mockMvc.perform(put("/api/zahnaerzte/{id}", zahnarztId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Hans Müller Updated"));
    }

    @Test
    @Order(9)
    public void testUpdateZahnarztAsPatient_Forbidden() throws Exception {
        ZahnarztCreateDTO updateDTO = new ZahnarztCreateDTO();
        updateDTO.setName("Unauthorized Update");
        updateDTO.setPraxisAdresseId(praxisAdresseId);

        mockMvc.perform(put("/api/zahnaerzte/{id}", zahnarztId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    public void testDeleteZahnarztAsPatient_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/zahnaerzte/{id}", zahnarztId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    public void testDeleteZahnarztAsZahnarzt() throws Exception {
        mockMvc.perform(delete("/api/zahnaerzte/{id}", zahnarztId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

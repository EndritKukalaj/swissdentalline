package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.TestDataCleanup;
import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
import ch.zhaw.swissdentalline.dto.PatientCreateDTO;
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

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String patientId;
    private static String adresseId;

    @Test
    @Order(1)
    public void testCreateAdresseAsZahnarzt() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Teststrasse 1");
        adresseDTO.setPlz("8000");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.HOME);
        adresseDTO.setBezeichnung("Patientenadresse");

        MvcResult result = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        adresseId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(2)
    public void testCreatePatientAsPatient() throws Exception {
        PatientCreateDTO patientDTO = new PatientCreateDTO();
        patientDTO.setName("Max Mustermann");
        patientDTO.setGeburtsdatum(Instant.parse("1990-05-15T00:00:00Z"));
        patientDTO.setKrankenkasse("Helsana");
        patientDTO.setAdresseId(adresseId);

        MvcResult result = mockMvc.perform(post("/api/patienten")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Max Mustermann"))
                .andExpect(jsonPath("$.krankenkasse").value("Helsana"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        patientId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(3)
    public void testCreatePatientAsZahnarzt_Forbidden() throws Exception {
        PatientCreateDTO patientDTO = new PatientCreateDTO();
        patientDTO.setName("Anna Meier");
        patientDTO.setGeburtsdatum(Instant.parse("1985-03-20T00:00:00Z"));
        patientDTO.setKrankenkasse("CSS");
        patientDTO.setAdresseId(adresseId);

        mockMvc.perform(post("/api/patienten")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    public void testGetPatientByIdAsPatient() throws Exception {
        mockMvc.perform(get("/api/patienten/{id}", patientId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId))
                .andExpect(jsonPath("$.name").value("Max Mustermann"));
    }

    @Test
    @Order(5)
    public void testGetPatientByIdAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/patienten/{id}", patientId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId))
                .andExpect(jsonPath("$.name").value("Max Mustermann"));
    }

    @Test
    @Order(6)
    public void testGetAllPatientsAsPatient() throws Exception {
        mockMvc.perform(get("/api/patienten")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(7)
    public void testGetAllPatientsAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/patienten")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(8)
    public void testUpdatePatientAsPatient() throws Exception {
        PatientCreateDTO updateDTO = new PatientCreateDTO();
        updateDTO.setName("Max Mustermann Updated");
        updateDTO.setGeburtsdatum(Instant.parse("1990-05-15T00:00:00Z"));
        updateDTO.setKrankenkasse("Swica");
        updateDTO.setAdresseId(adresseId);

        mockMvc.perform(put("/api/patienten/{id}", patientId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max Mustermann Updated"))
                .andExpect(jsonPath("$.krankenkasse").value("Swica"));
    }

    @Test
    @Order(9)
    public void testUpdatePatientAsZahnarzt_Forbidden() throws Exception {
        PatientCreateDTO updateDTO = new PatientCreateDTO();
        updateDTO.setName("Unauthorized Update");
        updateDTO.setGeburtsdatum(Instant.parse("1990-05-15T00:00:00Z"));
        updateDTO.setKrankenkasse("Helsana");
        updateDTO.setAdresseId(adresseId);

        mockMvc.perform(put("/api/patienten/{id}", patientId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    public void testDeletePatientAsZahnarzt_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/patienten/{id}", patientId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    public void testDeletePatientAsPatient() throws Exception {
        mockMvc.perform(delete("/api/patienten/{id}", patientId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.TestDataCleanup;
import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
import ch.zhaw.swissdentalline.dto.RezensionCreateDTO;
import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
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
public class RezensionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String rezensionId;
    private static String zahnarztId;
    private static String patientId;
    private static String praxisAdresseId;
    private static String patientAdresseId;

    @Test
    @Order(1)
    public void testSetupData() throws Exception {
        // Create addresses
        AdresseCreateDTO praxisAdresse = new AdresseCreateDTO();
        praxisAdresse.setStrasse("Praxisstrasse 50");
        praxisAdresse.setPlz("8005");
        praxisAdresse.setOrt("Zürich");
        praxisAdresse.setTyp(AdressTyp.PRAXIS);
        praxisAdresse.setBezeichnung("Zahnarztpraxis");

        MvcResult praxisResult = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(praxisAdresse)))
                .andExpect(status().isCreated())
                .andReturn();
        praxisAdresseId = objectMapper.readTree(praxisResult.getResponse().getContentAsString()).get("id").asText();

        AdresseCreateDTO patientAdresse = new AdresseCreateDTO();
        patientAdresse.setStrasse("Patientstrasse 60");
        patientAdresse.setPlz("8006");
        patientAdresse.setOrt("Zürich");
        patientAdresse.setTyp(AdressTyp.HOME);
        patientAdresse.setBezeichnung("Wohnadresse");

        MvcResult patientAdresseResult = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientAdresse)))
                .andExpect(status().isCreated())
                .andReturn();
        patientAdresseId = objectMapper.readTree(patientAdresseResult.getResponse().getContentAsString()).get("id").asText();

        // Create Zahnarzt
        ZahnarztCreateDTO zahnarztDTO = new ZahnarztCreateDTO();
        zahnarztDTO.setName("Dr. Test Zahnarzt");
        zahnarztDTO.setPraxisAdresseId(praxisAdresseId);

        MvcResult zahnarztResult = mockMvc.perform(post("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zahnarztDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        zahnarztId = objectMapper.readTree(zahnarztResult.getResponse().getContentAsString()).get("id").asText();

        // Create Patient
        PatientCreateDTO patientDTO = new PatientCreateDTO();
        patientDTO.setName("Test Patient");
        patientDTO.setGeburtsdatum(Instant.parse("1995-01-01T00:00:00Z"));
        patientDTO.setKrankenkasse("CSS");
        patientDTO.setAdresseId(patientAdresseId);

        MvcResult patientResult = mockMvc.perform(post("/api/patienten")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        patientId = objectMapper.readTree(patientResult.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    @Order(2)
    public void testCreateRezensionAsPatient() throws Exception {
        RezensionCreateDTO rezensionDTO = new RezensionCreateDTO();
        rezensionDTO.setZahnarztId(zahnarztId);
        rezensionDTO.setPatientId(patientId);
        rezensionDTO.setBewertung(5);
        rezensionDTO.setText("Sehr gute Behandlung!");
        rezensionDTO.setDatum(Instant.now());

        MvcResult result = mockMvc.perform(post("/api/rezensionen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rezensionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bewertung").value(5))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        rezensionId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(3)
    public void testCreateRezensionAsZahnarzt_Forbidden() throws Exception {
        RezensionCreateDTO rezensionDTO = new RezensionCreateDTO();
        rezensionDTO.setZahnarztId(zahnarztId);
        rezensionDTO.setPatientId(patientId);
        rezensionDTO.setBewertung(4);
        rezensionDTO.setText("Test");
        rezensionDTO.setDatum(Instant.now());

        mockMvc.perform(post("/api/rezensionen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rezensionDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    public void testGetRezensionByIdAsPatient() throws Exception {
        mockMvc.perform(get("/api/rezensionen/{id}", rezensionId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rezensionId))
                .andExpect(jsonPath("$.bewertung").value(5));
    }

    @Test
    @Order(5)
    public void testGetRezensionByIdAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/rezensionen/{id}", rezensionId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rezensionId))
                .andExpect(jsonPath("$.bewertung").value(5));
    }

    @Test
    @Order(6)
    public void testGetAllRezensionenAsPatient() throws Exception {
        mockMvc.perform(get("/api/rezensionen")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(7)
    public void testGetAllRezensionenAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/rezensionen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(8)
    public void testUpdateRezensionAsPatient() throws Exception {
        RezensionCreateDTO updateDTO = new RezensionCreateDTO();
        updateDTO.setZahnarztId(zahnarztId);
        updateDTO.setPatientId(patientId);
        updateDTO.setBewertung(4);
        updateDTO.setText("Sehr gute Behandlung! Updated");
        updateDTO.setDatum(Instant.now());

        mockMvc.perform(put("/api/rezensionen/{id}", rezensionId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bewertung").value(4))
                .andExpect(jsonPath("$.text").value("Sehr gute Behandlung! Updated"));
    }

    @Test
    @Order(9)
    public void testUpdateRezensionAsZahnarzt_Forbidden() throws Exception {
        RezensionCreateDTO updateDTO = new RezensionCreateDTO();
        updateDTO.setZahnarztId(zahnarztId);
        updateDTO.setPatientId(patientId);
        updateDTO.setBewertung(3);
        updateDTO.setText("Unauthorized Update");
        updateDTO.setDatum(Instant.now());

        mockMvc.perform(put("/api/rezensionen/{id}", rezensionId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    public void testDeleteRezensionAsZahnarzt_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/rezensionen/{id}", rezensionId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    public void testDeleteRezensionAsPatient() throws Exception {
        mockMvc.perform(delete("/api/rezensionen/{id}", rezensionId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

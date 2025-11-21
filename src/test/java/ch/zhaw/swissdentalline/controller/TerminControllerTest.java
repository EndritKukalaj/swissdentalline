package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.TestDataCleanup;
import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
import ch.zhaw.swissdentalline.dto.BehandlungsartCreateDTO;
import ch.zhaw.swissdentalline.dto.PatientCreateDTO;
import ch.zhaw.swissdentalline.dto.TerminCreateDTO;
import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.model.AdressTyp;
import ch.zhaw.swissdentalline.model.TerminStatus;
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
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TerminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String terminId;
    private static String zahnarztId;
    private static String behandlungsartId;
    private static String praxisAdresseId;
    private static String patientId;
    private static String patientAdresseId;

    @Test
    @Order(1)
    public void testCreatePraxisAdresseAsZahnarzt() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Teststrasse 5");
        adresseDTO.setPlz("8002");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.PRAXIS);
        adresseDTO.setBezeichnung("Zahnarztpraxis Test");

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
        zahnarztDTO.setName("Dr. Test Zahnarzt");
        zahnarztDTO.setPraxisAdresseId(praxisAdresseId);

        MvcResult result = mockMvc.perform(post("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zahnarztDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        zahnarztId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(3)
    public void testCreateBehandlungsartAsZahnarzt() throws Exception {
        BehandlungsartCreateDTO behandlungsartDTO = new BehandlungsartCreateDTO();
        behandlungsartDTO.setName("Kontrolluntersuchung");
        behandlungsartDTO.setBeschreibung("Routine-Kontrolluntersuchung");

        MvcResult result = mockMvc.perform(post("/api/behandlungsarten")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(behandlungsartDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        behandlungsartId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(4)
    public void testCreatePatientAdresseAndPatient() throws Exception {
        // Create patient address
        AdresseCreateDTO patientAdresse = new AdresseCreateDTO();
        patientAdresse.setStrasse("Patientstrasse 10");
        patientAdresse.setPlz("8003");
        patientAdresse.setOrt("Zürich");
        patientAdresse.setTyp(AdressTyp.HOME);
        patientAdresse.setBezeichnung("Patient Wohnadresse");

        MvcResult adresseResult = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientAdresse)))
                .andExpect(status().isCreated())
                .andReturn();
        patientAdresseId = objectMapper.readTree(adresseResult.getResponse().getContentAsString()).get("id").asText();

        // Create patient
        PatientCreateDTO patientDTO = new PatientCreateDTO();
        patientDTO.setName("Test Patient");
        patientDTO.setGeburtsdatum(Instant.parse("1990-01-01T00:00:00Z"));
        patientDTO.setKrankenkasse("Swica");
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
    @Order(5)
    public void testCreateTerminAsZahnarzt() throws Exception {
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId(zahnarztId);
        terminDTO.setBehandlungsartId(behandlungsartId);
        terminDTO.setDatum(Instant.now().plus(7, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(120.0);
        terminDTO.setStatus(TerminStatus.FREI);
        terminDTO.setWartelisteAktiv(false);

        MvcResult result = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.zahnarztId").value(zahnarztId))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        terminId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(6)
    public void testCreateTerminAsPatient_Forbidden() throws Exception {
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId(zahnarztId);
        terminDTO.setBehandlungsartId(behandlungsartId);
        terminDTO.setDatum(Instant.now().plus(8, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(120.0);
        terminDTO.setStatus(TerminStatus.FREI);
        terminDTO.setWartelisteAktiv(false);

        mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    public void testGetTerminByIdAsPatient() throws Exception {
        mockMvc.perform(get("/api/termine/{id}", terminId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(terminId));
    }

    @Test
    @Order(8)
    public void testGetTerminByIdAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/termine/{id}", terminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(terminId));
    }

    @Test
    @Order(9)
    public void testGetAllTermineAsPatient() throws Exception {
        mockMvc.perform(get("/api/termine")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(10)
    public void testGetAllTermineAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(11)
    public void testBookTerminAsPatient() throws Exception {
        // Book the termin first so it can be completed later
        mockMvc.perform(put("/api/termine/{id}/buchen", terminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("GEBUCHT"));
    }

    @Test
    @Order(12)
    public void testCompleteTerminAsZahnarzt() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/abschliessen", terminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABGESCHLOSSEN"));
    }

    @Test
    @Order(13)
    public void testCompleteTerminAsPatient_Forbidden() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/abschliessen", terminId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(14)
    public void testDeleteTerminAsPatient_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/termine/{id}", terminId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(15)
    public void testDeleteTerminAsZahnarzt() throws Exception {
        mockMvc.perform(delete("/api/termine/{id}", terminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

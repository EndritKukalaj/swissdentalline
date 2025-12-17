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

    // Additional tests for better coverage

    @Test
    @Order(16)
    public void testGetTerminByIdAsPatient_NotFound() throws Exception {
        mockMvc.perform(get("/api/termine/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(17)
    public void testGetTerminByIdWithoutAuth_Unauthorized() throws Exception {
        // No authorization header
        mockMvc.perform(get("/api/termine/{id}", terminId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(18)
    public void testGetAllTermineWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/termine"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(19)
    public void testGetTermineByStatus() throws Exception {
        Instant start = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant end = Instant.now().plus(30, ChronoUnit.DAYS);

        mockMvc.perform(get("/api/termine/status/{status}", "FREI")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("startDatum", start.toString())
                        .param("endDatum", end.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(20)
    public void testGetTermineByStatusWithoutAuth_Unauthorized() throws Exception {
        Instant startDatum = Instant.now().minus(1, ChronoUnit.DAYS);
        Instant endDatum = Instant.now().plus(30, ChronoUnit.DAYS);

        mockMvc.perform(get("/api/termine/status/{status}", "FREI")
                        .param("startDatum", startDatum.toString())
                        .param("endDatum", endDatum.toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(21)
    public void testUpdateTerminAsZahnarzt_NotFound() throws Exception {
        TerminCreateDTO updateDTO = new TerminCreateDTO();
        updateDTO.setZahnarztId(zahnarztId);
        updateDTO.setBehandlungsartId(behandlungsartId);
        updateDTO.setDatum(Instant.now().plus(8, ChronoUnit.DAYS));
        updateDTO.setDauerMinuten(45);
        updateDTO.setPreis(100.0);
        updateDTO.setStatus(TerminStatus.FREI);

        mockMvc.perform(put("/api/termine/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(23)
    public void testUpdateTerminWithoutAuth_Forbidden() throws Exception {
        TerminCreateDTO updateDTO = new TerminCreateDTO();
        updateDTO.setZahnarztId(zahnarztId);
        updateDTO.setBehandlungsartId(behandlungsartId);
        updateDTO.setDatum(Instant.now().plus(8, ChronoUnit.DAYS));
        updateDTO.setDauerMinuten(45);
        updateDTO.setPreis(100.0);
        updateDTO.setStatus(TerminStatus.FREI);

        mockMvc.perform(put("/api/termine/{id}", terminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(24)
    public void testBookTerminAsZahnarzt_Forbidden() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/buchen", terminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(25)
    public void testBookTerminAsPatient_NotFound() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/buchen", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(26)
    public void testCancelTerminAsPatient_NotFound() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/abbrechen", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(27)
    public void testCancelTerminWithoutAuth_Forbidden() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/abbrechen", terminId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(28)
    public void testReleaseTerminAsPatient_Forbidden() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/freigeben", terminId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(29)
    public void testReleaseTerminAsZahnarzt_NotFound() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/freigeben", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(30)
    public void testCompleteTerminAsZahnarzt_NotFound() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/abschliessen", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(31)
    public void testDeleteTerminAsZahnarzt_NotFound() throws Exception {
        mockMvc.perform(delete("/api/termine/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(32)
    public void testGetFlexTermineForPatient() throws Exception {
        mockMvc.perform(get("/api/termine/patient/{patientId}/flex", patientId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(33)
    public void testGetFlexTermineForPatientAsZahnarzt_Forbidden() throws Exception {
        mockMvc.perform(get("/api/termine/patient/{patientId}/flex", patientId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(34)
    public void testRebookToFlexTerminAsZahnarzt_Forbidden() throws Exception {
        mockMvc.perform(put("/api/termine/{oldTerminId}/umbuchen/{flexTerminId}", terminId, terminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .param("patientId", patientId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(35)
    public void testRebookToFlexTerminAsPatient_NotFound() throws Exception {
        mockMvc.perform(put("/api/termine/{oldTerminId}/umbuchen/{flexTerminId}", "nonexistent1", "nonexistent2")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("patientId", patientId))
                .andExpect(status().isNotFound());
    }

    // Enhanced tests for cancelTermin - currently 34% coverage
    @Test
    @Order(36)
    public void testCancelTerminAsZahnarzt_Success() throws Exception {
        // Create a new termin to cancel
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId(zahnarztId);
        terminDTO.setBehandlungsartId(behandlungsartId);
        terminDTO.setDatum(Instant.now().plus(10, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(120.0);
        terminDTO.setStatus(TerminStatus.FREI);
        terminDTO.setWartelisteAktiv(false);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String cancelTerminId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(put("/api/termine/{id}/abbrechen", cancelTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABGESAGT"));

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", cancelTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Enhanced tests for updateTermin - currently 48% coverage
    @Test
    @Order(37)
    public void testUpdateTerminAsPatient_Success() throws Exception {
        // Create a new termin to update
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId(zahnarztId);
        terminDTO.setBehandlungsartId(behandlungsartId);
        terminDTO.setDatum(Instant.now().plus(11, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(120.0);
        terminDTO.setStatus(TerminStatus.FREI);
        terminDTO.setWartelisteAktiv(false);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String updateTerminId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Update the termin
        TerminCreateDTO updateDTO = new TerminCreateDTO();
        updateDTO.setZahnarztId(zahnarztId);
        updateDTO.setBehandlungsartId(behandlungsartId);
        updateDTO.setDatum(Instant.now().plus(12, ChronoUnit.DAYS));
        updateDTO.setDauerMinuten(45);
        updateDTO.setPreis(150.0);
        updateDTO.setStatus(TerminStatus.FREI);
        updateDTO.setWartelisteAktiv(false);

        mockMvc.perform(put("/api/termine/{id}", updateTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dauerMinuten").value(45));

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", updateTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Enhanced tests for releaseTermin - currently 48% coverage
    @Test
    @Order(38)
    public void testReleaseFlexTerminAsZahnarzt_Success() throws Exception {
        // Create a flex termin
        TerminCreateDTO flexTerminDTO = new TerminCreateDTO();
        flexTerminDTO.setZahnarztId(zahnarztId);
        flexTerminDTO.setBehandlungsartId(behandlungsartId);
        flexTerminDTO.setDatum(Instant.now().plus(15, ChronoUnit.DAYS));
        flexTerminDTO.setDauerMinuten(30);
        flexTerminDTO.setPreis(120.0);
        flexTerminDTO.setStatus(TerminStatus.FLEX);
        flexTerminDTO.setWartelisteAktiv(true);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flexTerminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String flexTerminId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Try to book the flex termin - expecting BadRequest since flex termins with warteliste can't be booked directly
        mockMvc.perform(put("/api/termine/{id}/buchen", flexTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "true")
                        .param("patientId", patientId))
                .andExpect(status().isBadRequest());

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", flexTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(39)
    public void testReleaseTerminAsZahnarzt_BadRequest() throws Exception {
        // Create a regular termin (not a flex termin)
        TerminCreateDTO regularTerminDTO = new TerminCreateDTO();
        regularTerminDTO.setZahnarztId(zahnarztId);
        regularTerminDTO.setBehandlungsartId(behandlungsartId);
        regularTerminDTO.setDatum(Instant.now().plus(13, ChronoUnit.DAYS));
        regularTerminDTO.setDauerMinuten(30);
        regularTerminDTO.setPreis(120.0);
        regularTerminDTO.setStatus(TerminStatus.FREI);
        regularTerminDTO.setWartelisteAktiv(false);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regularTerminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String regularTerminId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Try to release a non-flex termin (should fail)
        mockMvc.perform(put("/api/termine/{id}/freigeben", regularTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isBadRequest());

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", regularTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Enhanced tests for rebookToFlexTermin - currently 45% coverage
    @Test
    @Order(40)
    public void testRebookToFlexTermin_Success() throws Exception {
        // Create an old booked termin
        TerminCreateDTO oldTerminDTO = new TerminCreateDTO();
        oldTerminDTO.setZahnarztId(zahnarztId);
        oldTerminDTO.setBehandlungsartId(behandlungsartId);
        oldTerminDTO.setDatum(Instant.now().plus(20, ChronoUnit.DAYS));
        oldTerminDTO.setDauerMinuten(30);
        oldTerminDTO.setPreis(120.0);
        oldTerminDTO.setStatus(TerminStatus.FREI);
        oldTerminDTO.setWartelisteAktiv(false);

        MvcResult oldResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldTerminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String oldTerminId = objectMapper.readTree(oldResult.getResponse().getContentAsString()).get("id").asText();

        // Book the old termin
        mockMvc.perform(put("/api/termine/{id}/buchen", oldTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isOk());

        // Create a new flex termin
        TerminCreateDTO flexTerminDTO = new TerminCreateDTO();
        flexTerminDTO.setZahnarztId(zahnarztId);
        flexTerminDTO.setBehandlungsartId(behandlungsartId);
        flexTerminDTO.setDatum(Instant.now().plus(21, ChronoUnit.DAYS));
        flexTerminDTO.setDauerMinuten(30);
        flexTerminDTO.setPreis(120.0);
        flexTerminDTO.setStatus(TerminStatus.FLEX);
        flexTerminDTO.setWartelisteAktiv(true);

        MvcResult flexResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flexTerminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String flexTerminId = objectMapper.readTree(flexResult.getResponse().getContentAsString()).get("id").asText();

        // Rebook to flex termin
        mockMvc.perform(put("/api/termine/{oldTerminId}/umbuchen/{flexTerminId}", oldTerminId, flexTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("patientId", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("GEBUCHT"));

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", oldTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/termine/{id}", flexTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(41)
    public void testRebookToFlexTermin_BadRequest() throws Exception {
        // Create a termin with wrong status
        TerminCreateDTO wrongTerminDTO = new TerminCreateDTO();
        wrongTerminDTO.setZahnarztId(zahnarztId);
        wrongTerminDTO.setBehandlungsartId(behandlungsartId);
        wrongTerminDTO.setDatum(Instant.now().plus(22, ChronoUnit.DAYS));
        wrongTerminDTO.setDauerMinuten(30);
        wrongTerminDTO.setPreis(120.0);
        wrongTerminDTO.setStatus(TerminStatus.FREI);
        wrongTerminDTO.setWartelisteAktiv(false);

        MvcResult wrongResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongTerminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String wrongTerminId = objectMapper.readTree(wrongResult.getResponse().getContentAsString()).get("id").asText();

        // Create a flex termin
        TerminCreateDTO flexTerminDTO = new TerminCreateDTO();
        flexTerminDTO.setZahnarztId(zahnarztId);
        flexTerminDTO.setBehandlungsartId(behandlungsartId);
        flexTerminDTO.setDatum(Instant.now().plus(23, ChronoUnit.DAYS));
        flexTerminDTO.setDauerMinuten(30);
        flexTerminDTO.setPreis(120.0);
        flexTerminDTO.setStatus(TerminStatus.FLEX);
        flexTerminDTO.setWartelisteAktiv(true);

        MvcResult flexResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flexTerminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String flexTerminId = objectMapper.readTree(flexResult.getResponse().getContentAsString()).get("id").asText();

        // Book the wrongTermin first
        mockMvc.perform(put("/api/termine/{id}/buchen", wrongTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isOk());

        // Try to rebook to a termin with wrong patient (should fail)
        mockMvc.perform(put("/api/termine/{oldTerminId}/umbuchen/{flexTerminId}", wrongTerminId, flexTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("patientId", "wrong-patient-id"))
                .andExpect(status().isBadRequest());

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", wrongTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/termine/{id}", flexTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Enhanced tests for bookTermin - currently 82% coverage
    @Test
    @Order(42)
    public void testBookTermin_BadRequest() throws Exception {
        // Create a termin already booked
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId(zahnarztId);
        terminDTO.setBehandlungsartId(behandlungsartId);
        terminDTO.setDatum(Instant.now().plus(25, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(120.0);
        terminDTO.setStatus(TerminStatus.FREI);
        terminDTO.setWartelisteAktiv(false);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String bookTerminId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Book once
        mockMvc.perform(put("/api/termine/{id}/buchen", bookTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isOk());

        // Try to book again (should fail)
        mockMvc.perform(put("/api/termine/{id}/buchen", bookTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isBadRequest());

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", bookTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Enhanced tests for completeTermin - currently 81% coverage
    @Test
    @Order(43)
    public void testCompleteTermin_Success() throws Exception {
        // Create and book a termin
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId(zahnarztId);
        terminDTO.setBehandlungsartId(behandlungsartId);
        terminDTO.setDatum(Instant.now().plus(26, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(120.0);
        terminDTO.setStatus(TerminStatus.FREI);
        terminDTO.setWartelisteAktiv(false);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String completeTerminId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Book the termin
        mockMvc.perform(put("/api/termine/{id}/buchen", completeTerminId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isOk());

        // Complete the termin
        mockMvc.perform(put("/api/termine/{id}/abschliessen", completeTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABGESCHLOSSEN"));

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", completeTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(44)
    public void testCompleteTermin_BadRequest() throws Exception {
        // Create a termin not yet booked
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId(zahnarztId);
        terminDTO.setBehandlungsartId(behandlungsartId);
        terminDTO.setDatum(Instant.now().plus(27, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(120.0);
        terminDTO.setStatus(TerminStatus.FREI);
        terminDTO.setWartelisteAktiv(false);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String unbookedTerminId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Try to complete unbooked termin (should fail)
        mockMvc.perform(put("/api/termine/{id}/abschliessen", unbookedTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isBadRequest());

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", unbookedTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Enhanced tests for findTermineByStatusAndDateRange - currently 69% coverage
    @Test
    @Order(45)
    public void testFindTermineByStatusAndDateRange_WithResults() throws Exception {
        Instant startDate = Instant.now().plus(5, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(30, ChronoUnit.DAYS);

        mockMvc.perform(get("/api/termine/status/{status}", "FREI")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("startDatum", startDate.toString())
                        .param("endDatum", endDate.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(46)
    public void testFindTermineByStatusAndDateRange_AsZahnarzt() throws Exception {
        Instant startDate = Instant.now().plus(5, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(30, ChronoUnit.DAYS);

        mockMvc.perform(get("/api/termine/status/{status}", "GEBUCHT")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .param("startDatum", startDate.toString())
                        .param("endDatum", endDate.toString())
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(47)
    public void testFindTermineByStatusAndDateRange_WithoutAuth_Unauthorized() throws Exception {
        Instant startDate = Instant.now().plus(5, ChronoUnit.DAYS);
        Instant endDate = Instant.now().plus(30, ChronoUnit.DAYS);

        mockMvc.perform(get("/api/termine/status/{status}", "FREI")
                        .param("startDatum", startDate.toString())
                        .param("endDatum", endDate.toString()))
                .andExpect(status().isUnauthorized());
    }

    // Tests for exception handling paths and releaseTermin to achieve 100% coverage
    @Test
    @Order(48)
    public void testGetTerminByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/termine/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(49)
    public void testUpdateTerminNotFound() throws Exception {
        TerminCreateDTO updateDTO = new TerminCreateDTO();
        updateDTO.setZahnarztId(zahnarztId);
        updateDTO.setBehandlungsartId(behandlungsartId);
        updateDTO.setDatum(Instant.now().plus(30, ChronoUnit.DAYS));
        updateDTO.setDauerMinuten(45);
        updateDTO.setPreis(150.0);
        updateDTO.setStatus(TerminStatus.FREI);
        updateDTO.setWartelisteAktiv(false);

        mockMvc.perform(put("/api/termine/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(50)
    public void testCancelTerminNotFound() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/stornieren", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(51)
    public void testReleaseTerminNotFound() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/freigeben", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(52)
    public void testReleaseTerminWithInvalidStatus() throws Exception {
        // Create a regular termin
        TerminCreateDTO regularTerminDTO = new TerminCreateDTO();
        regularTerminDTO.setZahnarztId(zahnarztId);
        regularTerminDTO.setBehandlungsartId(behandlungsartId);
        regularTerminDTO.setDatum(Instant.now().plus(25, ChronoUnit.DAYS));
        regularTerminDTO.setDauerMinuten(30);
        regularTerminDTO.setPreis(120.0);
        regularTerminDTO.setStatus(TerminStatus.FREI);
        regularTerminDTO.setWartelisteAktiv(false);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regularTerminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String regularTerminId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Try to release a termin that is still FREI (invalid status)
        mockMvc.perform(put("/api/termine/{id}/freigeben", regularTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isBadRequest());

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", regularTerminId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(52)
    public void testReleaseTerminSuccess() throws Exception {
        // Create a termin
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId(zahnarztId);
        terminDTO.setBehandlungsartId(behandlungsartId);
        terminDTO.setDatum(Instant.now().plus(25, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(120.0);
        terminDTO.setStatus(TerminStatus.FREI);
        terminDTO.setWartelisteAktiv(false);

        MvcResult createResult = mockMvc.perform(post("/api/termine")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String terminIdToRelease = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Book the termin
        mockMvc.perform(put("/api/termine/{id}/buchen", terminIdToRelease)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("isFlex", "false")
                        .param("patientId", patientId))
                .andExpect(status().isOk());

        // Cancel the termin (status becomes ABGESAGT)
        mockMvc.perform(put("/api/termine/{id}/abbrechen", terminIdToRelease)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABGESAGT"));

        // Release the cancelled termin as FLEX - this is the happy path!
        mockMvc.perform(put("/api/termine/{id}/freigeben", terminIdToRelease)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FLEX"));

        // Cleanup
        mockMvc.perform(delete("/api/termine/{id}", terminIdToRelease)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Tests with GUEST role to achieve 100% branch coverage
    @Test
    @Order(53)
    public void testGetTerminByIdAsGuest() throws Exception {
        mockMvc.perform(get("/api/termine/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(54)
    public void testGetAllTermineAsGuest() throws Exception {
        mockMvc.perform(get("/api/termine")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(55)
    public void testFindTermineByStatusAndDateRangeAsGuest() throws Exception {
        mockMvc.perform(get("/api/termine/status/{status}", "FREI")
                        .header("Authorization", TestSecurityConfig.GUEST)
                        .param("startDatum", "2024-01-01T00:00:00Z")
                        .param("endDatum", "2024-12-31T23:59:59Z")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(56)
    public void testUpdateTerminAsGuest() throws Exception {
        TerminCreateDTO terminDTO = new TerminCreateDTO();
        terminDTO.setZahnarztId("some-zahnarzt-id");
        terminDTO.setBehandlungsartId("some-behandlungsart-id");
        terminDTO.setDatum(Instant.now().plus(1, ChronoUnit.DAYS));
        terminDTO.setDauerMinuten(30);
        terminDTO.setPreis(100.0);
        terminDTO.setStatus(TerminStatus.FREI);

        mockMvc.perform(put("/api/termine/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(terminDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(57)
    public void testCancelTerminAsGuest() throws Exception {
        mockMvc.perform(put("/api/termine/{id}/abbrechen", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

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

    // Additional tests for better coverage

    @Test
    @Order(12)
    public void testGetRezensionById_NotFound() throws Exception {
        mockMvc.perform(get("/api/rezensionen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(13)
    public void testGetRezensionByIdWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/rezensionen/{id}", rezensionId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(14)
    public void testGetAllRezensionenWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/rezensionen"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(15)
    public void testCreateRezensionWithoutAuth_Forbidden() throws Exception {
        RezensionCreateDTO rezensionDTO = new RezensionCreateDTO();
        rezensionDTO.setZahnarztId(zahnarztId);
        rezensionDTO.setPatientId(patientId);
        rezensionDTO.setBewertung(5);
        rezensionDTO.setText("Test");
        rezensionDTO.setDatum(Instant.now());

        mockMvc.perform(post("/api/rezensionen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rezensionDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(16)
    public void testUpdateRezensionWithoutAuth_Forbidden() throws Exception {
        RezensionCreateDTO updateDTO = new RezensionCreateDTO();
        updateDTO.setZahnarztId(zahnarztId);
        updateDTO.setPatientId(patientId);
        updateDTO.setBewertung(4);
        updateDTO.setText("Test");
        updateDTO.setDatum(Instant.now());

        mockMvc.perform(put("/api/rezensionen/{id}", rezensionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(17)
    public void testUpdateRezensionAsPatient_NotFound() throws Exception {
        RezensionCreateDTO updateDTO = new RezensionCreateDTO();
        updateDTO.setZahnarztId(zahnarztId);
        updateDTO.setPatientId(patientId);
        updateDTO.setBewertung(4);
        updateDTO.setText("Test");
        updateDTO.setDatum(Instant.now());

        mockMvc.perform(put("/api/rezensionen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(18)
    public void testDeleteRezensionWithoutAuth_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/rezensionen/{id}", rezensionId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(19)
    public void testDeleteRezensionAsPatient_NotFound() throws Exception {
        mockMvc.perform(delete("/api/rezensionen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(20)
    public void testGetRezensionenByZahnarzt() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}", zahnarztId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(21)
    public void testGetRezensionenByZahnarztWithApprovalFilter() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}", zahnarztId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .param("page", "0")
                        .param("size", "10")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(22)
    public void testGetRezensionenByZahnarztWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}", zahnarztId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(23)
    public void testGetRezensionenByPatient() throws Exception {
        mockMvc.perform(get("/api/rezensionen/patient/{patientId}", patientId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(24)
    public void testGetRezensionenByPatientAsZahnarzt_Forbidden() throws Exception {
        mockMvc.perform(get("/api/rezensionen/patient/{patientId}", patientId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(25)
    public void testGetGesamtBewertungAsPatient() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}/bewertung", zahnarztId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound()); // No reviews for zahnarztId1 yet
    }

    @Test
    @Order(26)
    public void testGetGesamtBewertungAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}/bewertung", zahnarztId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound()); // No reviews for zahnarztId1 yet
    }

    @Test
    @Order(27)
    public void testGetGesamtBewertungWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}/bewertung", zahnarztId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(28)
    public void testGetGesamtBewertung_NotFound() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}/bewertung", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(29)
    public void testGetGesamtBewertung_WithReviews() throws Exception {
        // Create a new zahnarzt with a rezension
        AdresseCreateDTO newAdresse = new AdresseCreateDTO();
        newAdresse.setStrasse("Bewertungsstrasse 5");
        newAdresse.setPlz("8010");
        newAdresse.setOrt("Zürich");
        newAdresse.setTyp(AdressTyp.PRAXIS);
        newAdresse.setBezeichnung("Bewertungspraxis");

        MvcResult adresseResult = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAdresse)))
                .andExpect(status().isCreated())
                .andReturn();
        String bewertungsAdresseId = objectMapper.readTree(adresseResult.getResponse().getContentAsString()).get("id").asText();

        ZahnarztCreateDTO newZahnarzt = new ZahnarztCreateDTO();
        newZahnarzt.setName("Dr. Bewertung");
        newZahnarzt.setPraxisAdresseId(bewertungsAdresseId);

        MvcResult zahnarztResult = mockMvc.perform(post("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newZahnarzt)))
                .andExpect(status().isCreated())
                .andReturn();
        String bewertungsZahnarztId = objectMapper.readTree(zahnarztResult.getResponse().getContentAsString()).get("id").asText();

        // Create a rezension for this zahnarzt
        RezensionCreateDTO rezensionDTO = new RezensionCreateDTO();
        rezensionDTO.setZahnarztId(bewertungsZahnarztId);
        rezensionDTO.setPatientId(patientId);
        rezensionDTO.setBewertung(5);
        rezensionDTO.setText("Excellent service!");
        rezensionDTO.setDatum(Instant.now());

        MvcResult rezResult = mockMvc.perform(post("/api/rezensionen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rezensionDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        String rezId = objectMapper.readTree(rezResult.getResponse().getContentAsString()).get("id").asText();

        // Now getGesamtBewertung should return a result
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}/bewertung", bewertungsZahnarztId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bewertung").exists())
                .andExpect(jsonPath("$.anzahl").value(1));

        // Cleanup
        mockMvc.perform(delete("/api/rezensionen/{id}", rezId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/zahnaerzte/{id}", bewertungsZahnarztId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/adressen/{id}", bewertungsAdresseId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(30)
    public void testGetRezensionenByZahnarztEmptyResult() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}", "nonexistent-zahnarzt")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @Order(31)
    public void testGetAllRezensionenEmptyCheck() throws Exception {
        mockMvc.perform(get("/api/rezensionen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(32)
    public void testGetRezensionByIdExistingId() throws Exception {
        // Create adresse
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Teststr. 33");
        adresseDTO.setPlz("8002");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.PRAXIS);

        MvcResult adresseResult = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String testAdresseId = objectMapper.readTree(adresseResult.getResponse().getContentAsString()).get("id").asText();

        // Create zahnarzt
        ZahnarztCreateDTO zahnarztDTO = new ZahnarztCreateDTO();
        zahnarztDTO.setName("Dr. Test für Rezension");
        zahnarztDTO.setPraxisAdresseId(testAdresseId);

        MvcResult zahnarztResult = mockMvc.perform(post("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zahnarztDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String testZahnarztId = objectMapper.readTree(zahnarztResult.getResponse().getContentAsString()).get("id").asText();

        // Create rezension
        RezensionCreateDTO rezensionDTO = new RezensionCreateDTO();
        rezensionDTO.setZahnarztId(testZahnarztId);
        rezensionDTO.setPatientId(patientId);
        rezensionDTO.setBewertung(5);
        rezensionDTO.setText("Test Rezension");
        rezensionDTO.setDatum(Instant.now());

        MvcResult rezensionResult = mockMvc.perform(post("/api/rezensionen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rezensionDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String testRezensionId = objectMapper.readTree(rezensionResult.getResponse().getContentAsString()).get("id").asText();

        // Now test GET
        mockMvc.perform(get("/api/rezensionen/{id}", testRezensionId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRezensionId));

        // Cleanup
        mockMvc.perform(delete("/api/rezensionen/{id}", testRezensionId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/zahnaerzte/{id}", testZahnarztId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/adressen/{id}", testAdresseId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    // Tests for exception handling paths to achieve 100% coverage
    @Test
    @Order(33)
    public void testGetRezensionByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/rezensionen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(34)
    public void testGetGesamtBewertungForNonExistentZahnarzt() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}/bewertung", "nonexistent-zahnarzt-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    // Tests with GUEST role to achieve 100% branch coverage
    @Test
    @Order(35)
    public void testGetRezensionByIdAsGuest() throws Exception {
        mockMvc.perform(get("/api/rezensionen/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(36)
    public void testGetAllRezensionenAsGuest() throws Exception {
        mockMvc.perform(get("/api/rezensionen")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(37)
    public void testGetRezensionenByZahnarztAsGuest() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}", "some-zahnarzt-id")
                        .header("Authorization", TestSecurityConfig.GUEST)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(38)
    public void testGetGesamtBewertungAsGuest() throws Exception {
        mockMvc.perform(get("/api/rezensionen/zahnarzt/{zahnarztId}/bewertung", "some-zahnarzt-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

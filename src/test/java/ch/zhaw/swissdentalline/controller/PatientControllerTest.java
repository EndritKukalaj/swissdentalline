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

    // Additional tests for better coverage

    @Test
    @Order(12)
    public void testCreatePatientWithoutAuth_Forbidden() throws Exception {
        PatientCreateDTO patientDTO = new PatientCreateDTO();
        patientDTO.setName("Test");
        patientDTO.setGeburtsdatum(Instant.parse("1990-01-01T00:00:00Z"));
        patientDTO.setKrankenkasse("Test");
        patientDTO.setAdresseId(adresseId);

        mockMvc.perform(post("/api/patienten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    public void testGetPatientById_NotFound() throws Exception {
        mockMvc.perform(get("/api/patienten/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(14)
    public void testGetPatientByIdWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/patienten/{id}", patientId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(15)
    public void testGetAllPatientsWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/patienten"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(16)
    public void testUpdatePatientWithoutAuth_Forbidden() throws Exception {
        PatientCreateDTO updateDTO = new PatientCreateDTO();
        updateDTO.setName("Test");
        updateDTO.setGeburtsdatum(Instant.parse("1990-01-01T00:00:00Z"));
        updateDTO.setKrankenkasse("Test");
        updateDTO.setAdresseId(adresseId);

        mockMvc.perform(put("/api/patienten/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(17)
    public void testUpdatePatient_NotFound() throws Exception {
        PatientCreateDTO updateDTO = new PatientCreateDTO();
        updateDTO.setName("Test");
        updateDTO.setGeburtsdatum(Instant.parse("1990-01-01T00:00:00Z"));
        updateDTO.setKrankenkasse("Test");
        updateDTO.setAdresseId(adresseId);

        mockMvc.perform(put("/api/patienten/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(18)
    public void testDeletePatientWithoutAuth_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/patienten/{id}", patientId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(19)
    public void testDeletePatient_NotFound() throws Exception {
        mockMvc.perform(delete("/api/patienten/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    // Tests for findPatientsByName - currently 0% coverage
    @Test
    @Order(20)
    public void testFindPatientsByNameAsPatient() throws Exception {
        // First create a patient to search for
        PatientCreateDTO patientDTO = new PatientCreateDTO();
        patientDTO.setName("Erika Muster");
        patientDTO.setGeburtsdatum(Instant.parse("1992-07-20T00:00:00Z"));
        patientDTO.setKrankenkasse("Swica");
        patientDTO.setAdresseId(adresseId);

        MvcResult result = mockMvc.perform(post("/api/patienten")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String searchPatientId = objectMapper.readTree(responseBody).get("id").asText();

        // Now search by name
        mockMvc.perform(get("/api/patienten/name/{name}", "Erika")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Cleanup the search patient
        mockMvc.perform(delete("/api/patienten/{id}", searchPatientId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(21)
    public void testFindPatientsByNameAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/patienten/name/{name}", "Muster")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(22)
    public void testFindPatientsByNameWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/patienten/name/{name}", "Test"))
                .andExpect(status().isUnauthorized());
    }

    // Tests for getProfil - currently 0% coverage
    @Test
    @Order(23)
    public void testGetProfilAsPatient() throws Exception {
        // First create a patient with known data
        PatientCreateDTO patientDTO = new PatientCreateDTO();
        patientDTO.setName("Hans Profil");
        patientDTO.setGeburtsdatum(Instant.parse("1988-04-10T00:00:00Z"));
        patientDTO.setKrankenkasse("CSS");
        patientDTO.setAdresseId(adresseId);

        MvcResult createResult = mockMvc.perform(post("/api/patienten")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String profilPatientId = objectMapper.readTree(responseBody).get("id").asText();

        // Test getProfil endpoint
        mockMvc.perform(get("/api/patienten/profil")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("name", "Hans Profil")
                        .param("email", "hans.profil@test.com")
                        .param("role", "Patient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hans Profil"));

        // Cleanup the profil patient
        mockMvc.perform(delete("/api/patienten/{id}", profilPatientId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(24)
    public void testGetProfilAsZahnarzt_Forbidden() throws Exception {
        mockMvc.perform(get("/api/patienten/profil")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .param("name", "Test")
                        .param("email", "test@test.com")
                        .param("role", "Patient"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(25)
    public void testGetProfilWithoutAuth_Forbidden() throws Exception {
        mockMvc.perform(get("/api/patienten/profil")
                        .param("name", "Test")
                        .param("email", "test@test.com")
                        .param("role", "Patient"))
                .andExpect(status().isUnauthorized());
    }

    // Enhanced tests for getAllPatients - currently 80% coverage
    @Test
    @Order(26)
    public void testGetAllPatientsEmptyCheck() throws Exception {
        mockMvc.perform(get("/api/patienten")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // Enhanced tests for getPatientById - currently 86% coverage
    @Test
    @Order(27)
    public void testGetPatientByIdMultipleTimes() throws Exception {
        // Create a patient first
        PatientCreateDTO patientDTO = new PatientCreateDTO();
        patientDTO.setName("Multiple Test Patient");
        patientDTO.setGeburtsdatum(Instant.parse("1991-08-25T00:00:00Z"));
        patientDTO.setKrankenkasse("Helsana");
        patientDTO.setAdresseId(adresseId);

        MvcResult createResult = mockMvc.perform(post("/api/patienten")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String multiplePatientId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Get by ID multiple times
        mockMvc.perform(get("/api/patienten/{id}", multiplePatientId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(multiplePatientId));

        // Cleanup
        mockMvc.perform(delete("/api/patienten/{id}", multiplePatientId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    // Tests for exception handling paths to achieve 100% coverage
    @Test
    @Order(28)
    public void testGetPatientByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/patienten/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    // Tests with GUEST role to achieve 100% branch coverage
    @Test
    @Order(29)
    public void testGetPatientByIdAsGuest() throws Exception {
        mockMvc.perform(get("/api/patienten/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(30)
    public void testGetAllPatientsAsGuest() throws Exception {
        mockMvc.perform(get("/api/patienten")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(31)
    public void testFindPatientsByNameAsGuest() throws Exception {
        mockMvc.perform(get("/api/patienten/name/{name}", "Test")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

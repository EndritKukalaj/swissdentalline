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

    // Additional tests for better coverage

    @Test
    @Order(12)
    public void testCreateZahnarztWithoutAuth_Forbidden() throws Exception {
        ZahnarztCreateDTO zahnarztDTO = new ZahnarztCreateDTO();
        zahnarztDTO.setName("Test");
        zahnarztDTO.setPraxisAdresseId(praxisAdresseId);

        mockMvc.perform(post("/api/zahnaerzte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zahnarztDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    public void testGetZahnarztById_NotFound() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(14)
    public void testGetZahnarztByIdWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/{id}", zahnarztId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(15)
    public void testGetAllZahnaerzteWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(16)
    public void testUpdateZahnarztWithoutAuth_Forbidden() throws Exception {
        ZahnarztCreateDTO updateDTO = new ZahnarztCreateDTO();
        updateDTO.setName("Test");
        updateDTO.setPraxisAdresseId(praxisAdresseId);

        mockMvc.perform(put("/api/zahnaerzte/{id}", zahnarztId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(17)
    public void testUpdateZahnarzt_NotFound() throws Exception {
        ZahnarztCreateDTO updateDTO = new ZahnarztCreateDTO();
        updateDTO.setName("Test");
        updateDTO.setPraxisAdresseId(praxisAdresseId);

        mockMvc.perform(put("/api/zahnaerzte/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(18)
    public void testDeleteZahnarztWithoutAuth_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/zahnaerzte/{id}", zahnarztId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(19)
    public void testDeleteZahnarzt_NotFound() throws Exception {
        mockMvc.perform(delete("/api/zahnaerzte/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(20)
    public void testFindZahnaerzteByPraxisAdresseAsPatient() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/praxis/{praxisAdresseId}", praxisAdresseId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(21)
    public void testFindZahnaerzteByPraxisAdresseAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/praxis/{praxisAdresseId}", praxisAdresseId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(22)
    public void testFindZahnaerzteByPraxisAdresseWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/praxis/{praxisAdresseId}", praxisAdresseId))
                .andExpect(status().isUnauthorized());
    }

    // Tests for findZahnaerzteByName - currently 0% coverage
    @Test
    @Order(23)
    public void testFindZahnaerzteByNameAsPatient() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/name/{name}", "Müller")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(24)
    public void testFindZahnaerzteByNameAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/name/{name}", "Dr")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(25)
    public void testFindZahnaerzteByNameWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/name/{name}", "Test"))
                .andExpect(status().isUnauthorized());
    }

    // Tests for getProfil - currently 0% coverage
    @Test
    @Order(26)
    public void testGetProfilAsZahnarzt() throws Exception {
        // First create a zahnarzt with known data
        ZahnarztCreateDTO zahnarztDTO = new ZahnarztCreateDTO();
        zahnarztDTO.setName("Dr. Hans Profil");
        zahnarztDTO.setPraxisAdresseId(praxisAdresseId);

        MvcResult createResult = mockMvc.perform(post("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zahnarztDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        String profilZahnarztId = objectMapper.readTree(responseBody).get("id").asText();

        // Test getProfil endpoint
        mockMvc.perform(get("/api/zahnaerzte/profil")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .param("name", "Dr. Hans Profil")
                        .param("email", "hans.profil@zahnarzt.com")
                        .param("role", "Zahnarzt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dr. Hans Profil"));

        // Cleanup the profil zahnarzt
        mockMvc.perform(delete("/api/zahnaerzte/{id}", profilZahnarztId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(27)
    public void testGetProfilAsPatient_Forbidden() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/profil")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("name", "Test")
                        .param("email", "test@test.com")
                        .param("role", "Zahnarzt"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(28)
    public void testGetProfilWithoutAuth_Forbidden() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/profil")
                        .param("name", "Test")
                        .param("email", "test@test.com")
                        .param("role", "Zahnarzt"))
                .andExpect(status().isUnauthorized());
    }

    // Enhanced tests for getAllZahnaerzte - currently 80% coverage
    @Test
    @Order(29)
    public void testGetAllZahnaerzteEmptyCheck() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    // Tests for exception handling paths to achieve 100% coverage
    @Test
    @Order(30)
    public void testGetZahnarztByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    // Tests with GUEST role to achieve 100% branch coverage
    @Test
    @Order(31)
    public void testGetZahnarztByIdAsGuest() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(32)
    public void testGetAllZahnaerzteAsGuest() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(33)
    public void testFindZahnaerzteByPraxisAdresseAsGuest() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/praxis/{praxisAdresseId}", "some-praxis-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(34)
    public void testFindZahnaerzteByNameAsGuest() throws Exception {
        mockMvc.perform(get("/api/zahnaerzte/name/{name}", "Test")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.TestDataCleanup;
import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
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
public class AdresseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String adresseId;

    @Test
    @Order(1)
    public void testCreateAdresseAsPatient() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Musterstrasse 10");
        adresseDTO.setPlz("8003");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.HOME);
        adresseDTO.setBezeichnung("Hauptwohnsitz");

        MvcResult result = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.strasse").value("Musterstrasse 10"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        adresseId = objectMapper.readTree(responseBody).get("id").asText();
    }

    @Test
    @Order(2)
    public void testCreateAdresseAsZahnarzt() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Praxisstrasse 20");
        adresseDTO.setPlz("8004");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.PRAXIS);
        adresseDTO.setBezeichnung("Zahnarztpraxis");

        mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Order(3)
    public void testGetAdresseByIdAsPatient() throws Exception {
        mockMvc.perform(get("/api/adressen/{id}", adresseId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adresseId))
                .andExpect(jsonPath("$.strasse").value("Musterstrasse 10"));
    }

    @Test
    @Order(4)
    public void testGetAdresseByIdAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/adressen/{id}", adresseId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adresseId))
                .andExpect(jsonPath("$.strasse").value("Musterstrasse 10"));
    }

    @Test
    @Order(5)
    public void testGetAllAdressenAsPatient() throws Exception {
        mockMvc.perform(get("/api/adressen")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(6)
    public void testGetAllAdressenAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/adressen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(7)
    public void testUpdateAdresseAsPatient() throws Exception {
        AdresseCreateDTO updateDTO = new AdresseCreateDTO();
        updateDTO.setStrasse("Musterstrasse 10 Updated");
        updateDTO.setPlz("8003");
        updateDTO.setOrt("Zürich");
        updateDTO.setTyp(AdressTyp.HOME);
        updateDTO.setBezeichnung("Hauptwohnsitz Updated");

        mockMvc.perform(put("/api/adressen/{id}", adresseId)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strasse").value("Musterstrasse 10 Updated"));
    }

    @Test
    @Order(8)
    public void testUpdateAdresseAsZahnarzt() throws Exception {
        AdresseCreateDTO updateDTO = new AdresseCreateDTO();
        updateDTO.setStrasse("Musterstrasse 10 Zahnarzt Updated");
        updateDTO.setPlz("8003");
        updateDTO.setOrt("Zürich");
        updateDTO.setTyp(AdressTyp.HOME);
        updateDTO.setBezeichnung("Updated by Zahnarzt");

        mockMvc.perform(put("/api/adressen/{id}", adresseId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.strasse").value("Musterstrasse 10 Zahnarzt Updated"));
    }

    @Test
    @Order(9)
    public void testDeleteAdresseAsPatient() throws Exception {
        mockMvc.perform(delete("/api/adressen/{id}", adresseId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    // Additional tests for better coverage

    @Test
    @Order(10)
    public void testCreateAdresseWithoutAuth_Forbidden() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Test");
        adresseDTO.setPlz("8000");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.HOME);

        mockMvc.perform(post("/api/adressen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(11)
    public void testGetAdresseById_NotFound() throws Exception {
        mockMvc.perform(get("/api/adressen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(12)
    public void testGetAdresseByIdWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/adressen/{id}", adresseId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(13)
    public void testGetAllAdressenWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/adressen"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(14)
    public void testGetAdressenByTypAsZahnarzt() throws Exception {
        mockMvc.perform(get("/api/adressen/typ/{typ}", "PRAXIS")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray()) // Returns Page object with content array
                .andExpect(jsonPath("$.content[0].typ").value("PRAXIS"));
    }

    @Test
    @Order(15)
    public void testGetAdressenByTypWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/adressen/typ/{typ}", "PRAXIS"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(16)
    public void testUpdateAdresseWithoutAuth_Forbidden() throws Exception {
        AdresseCreateDTO updateDTO = new AdresseCreateDTO();
        updateDTO.setStrasse("Test");
        updateDTO.setPlz("8000");
        updateDTO.setOrt("Zürich");
        updateDTO.setTyp(AdressTyp.HOME);

        mockMvc.perform(put("/api/adressen/{id}", adresseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(17)
    public void testUpdateAdresse_NotFound() throws Exception {
        AdresseCreateDTO updateDTO = new AdresseCreateDTO();
        updateDTO.setStrasse("Test");
        updateDTO.setPlz("8000");
        updateDTO.setOrt("Zürich");
        updateDTO.setTyp(AdressTyp.HOME);

        mockMvc.perform(put("/api/adressen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(18)
    public void testDeleteAdresseWithoutAuth_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/adressen/{id}", adresseId))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(19)
    public void testDeleteAdresse_NotFound() throws Exception {
        mockMvc.perform(delete("/api/adressen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    // Enhanced tests for deleteAdresse - currently 66% instruction, 25% branch coverage
    @Test
    @Order(20)
    public void testDeleteAdresseAsPatient_Success() throws Exception {
        // Create a new adresse to delete
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Delete Test 1");
        adresseDTO.setPlz("8005");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.HOME);
        adresseDTO.setBezeichnung("Test Delete");

        MvcResult createResult = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String deleteAdresseId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Delete the adresse
        mockMvc.perform(delete("/api/adressen/{id}", deleteAdresseId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(21)
    public void testDeleteAdresseAsZahnarzt_Success() throws Exception {
        // Create a new adresse to delete
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Delete Test 2");
        adresseDTO.setPlz("8006");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.PRAXIS);
        adresseDTO.setBezeichnung("Test Delete Praxis");

        MvcResult createResult = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String deleteAdresseId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        // Delete the adresse
        mockMvc.perform(delete("/api/adressen/{id}", deleteAdresseId)
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isNoContent());
    }

    // Enhanced tests for getAdressenByType - currently 83% coverage
    @Test
    @Order(22)
    public void testGetAdressenByTypeWithDifferentPageSize() throws Exception {
        mockMvc.perform(get("/api/adressen/typ/{typ}", AdressTyp.HOME)
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // Enhanced tests for getAllAdressen - currently 80% coverage
    @Test
    @Order(23)
    public void testGetAllAdressenEmptyCheck() throws Exception {
        mockMvc.perform(get("/api/adressen")
                        .header("Authorization", TestSecurityConfig.ZAHNARZT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(26)
    public void testCreateAdresseMultiple() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Multiple Test");
        adresseDTO.setPlz("8007");
        adresseDTO.setOrt("Zürich");
        adresseDTO.setTyp(AdressTyp.HOME);
        adresseDTO.setBezeichnung("Multiple");

        MvcResult result = mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        String multipleAdresseId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        // Cleanup
        mockMvc.perform(delete("/api/adressen/{id}", multipleAdresseId)
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNoContent());
    }

    // Tests for exception handling paths to achieve 100% coverage
    @Test
    @Order(27)
    public void testGetAdresseByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/adressen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(28)
    public void testUpdateAdresseNotFound() throws Exception {
        AdresseCreateDTO updateDTO = new AdresseCreateDTO();
        updateDTO.setStrasse("Updated Str. 99");
        updateDTO.setPlz("9999");
        updateDTO.setOrt("Updated City");
        updateDTO.setTyp(AdressTyp.PRAXIS);

        mockMvc.perform(put("/api/adressen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(29)
    public void testDeleteAdresseNotFound() throws Exception {
        mockMvc.perform(delete("/api/adressen/{id}", "nonexistent-id")
                        .header("Authorization", TestSecurityConfig.PATIENT))
                .andExpect(status().isNotFound());
    }

    // Tests for authorization paths to achieve >90% coverage
    @Test
    @Order(30)
    public void testCreateAdresseWithoutAuth() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Test Str. 1");
        adresseDTO.setPlz("8000");
        adresseDTO.setOrt("Zurich");
        adresseDTO.setTyp(AdressTyp.PRAXIS);

        mockMvc.perform(post("/api/adressen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(31)
    public void testGetAdresseByIdWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/adressen/{id}", "some-id"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(32)
    public void testGetAllAdressenWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/adressen"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(33)
    public void testGetAdressenByTypeWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/adressen/typ/{typ}", "PRAXIS"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(34)
    public void testUpdateAdresseWithoutAuth() throws Exception {
        AdresseCreateDTO updateDTO = new AdresseCreateDTO();
        updateDTO.setStrasse("Updated Str.");
        updateDTO.setPlz("8001");
        updateDTO.setOrt("Zurich");
        updateDTO.setTyp(AdressTyp.PRAXIS);

        mockMvc.perform(put("/api/adressen/{id}", "some-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(35)
    public void testDeleteAdresseWithoutAuth() throws Exception {
        mockMvc.perform(delete("/api/adressen/{id}", "some-id"))
                .andExpect(status().isForbidden());
    }

    // Tests with authenticated user but without Patient or Zahnarzt role
    @Test
    @Order(36)
    public void testCreateAdresseAsGuest() throws Exception {
        AdresseCreateDTO adresseDTO = new AdresseCreateDTO();
        adresseDTO.setStrasse("Guest Str. 1");
        adresseDTO.setPlz("8000");
        adresseDTO.setOrt("Zurich");
        adresseDTO.setTyp(AdressTyp.PRAXIS);

        mockMvc.perform(post("/api/adressen")
                        .header("Authorization", TestSecurityConfig.GUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adresseDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(37)
    public void testGetAdresseByIdAsGuest() throws Exception {
        mockMvc.perform(get("/api/adressen/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(38)
    public void testGetAllAdressenAsGuest() throws Exception {
        mockMvc.perform(get("/api/adressen")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(39)
    public void testGetAdressenByTypeAsGuest() throws Exception {
        mockMvc.perform(get("/api/adressen/typ/{typ}", "PRAXIS")
                        .header("Authorization", TestSecurityConfig.GUEST)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(40)
    public void testUpdateAdresseAsGuest() throws Exception {
        AdresseCreateDTO updateDTO = new AdresseCreateDTO();
        updateDTO.setStrasse("Updated Guest");
        updateDTO.setPlz("8010");
        updateDTO.setOrt("Zurich");
        updateDTO.setTyp(AdressTyp.PRAXIS);

        mockMvc.perform(put("/api/adressen/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(41)
    public void testDeleteAdresseAsGuest() throws Exception {
        mockMvc.perform(delete("/api/adressen/{id}", "some-id")
                        .header("Authorization", TestSecurityConfig.GUEST))
                .andExpect(status().isForbidden());
    }

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

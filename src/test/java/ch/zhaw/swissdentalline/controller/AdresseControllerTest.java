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

    @AfterAll
    public static void cleanup(@Autowired TestDataCleanup testDataCleanup) {
        testDataCleanup.cleanupAllData();
    }
}

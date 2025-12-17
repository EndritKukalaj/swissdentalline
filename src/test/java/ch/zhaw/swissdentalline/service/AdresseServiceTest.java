package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
import ch.zhaw.swissdentalline.model.Adresse;
import ch.zhaw.swissdentalline.model.AdressTyp;
import ch.zhaw.swissdentalline.repositories.AdresseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdresseServiceTest {

    @Mock
    AdresseRepository adresseRepository;

    @InjectMocks
    AdresseService adresseService;

    private AdresseCreateDTO testDTO;
    private Adresse testEntity;

    @BeforeEach
    void setUp() {
        testDTO = new AdresseCreateDTO();
        testDTO.setStrasse("Bahnhofstrasse 1");
        testDTO.setPlz("8001");
        testDTO.setOrt("Zürich");
        testDTO.setTyp(AdressTyp.PRAXIS);

        testEntity = new Adresse();
        testEntity.setId("ce63e54dc48b477e35ccc36e");
        testEntity.setStrasse(testDTO.getStrasse());
        testEntity.setPlz(testDTO.getPlz());
        testEntity.setOrt(testDTO.getOrt());
        testEntity.setTyp(testDTO.getTyp());
    }

    @Test
    void shouldCreateAdresse() {
        when(adresseRepository.save(any(Adresse.class))).thenReturn(testEntity);

        Adresse result = adresseService.createAdresse(testDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("ce63e54dc48b477e35ccc36e");
    }

    @Test
    void updateAdresse_whenNotFound_throws() {
        AdresseCreateDTO dto = new AdresseCreateDTO();
        when(adresseRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adresseService.updateAdresse("nonexistent", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Adresse mit id: nonexistent nicht gefunden");
    }

    @Test
    void getAdresseById_happy() {
        Adresse a = new Adresse();
        a.setId("cbd584a516c5614b9bed0d2f");
        when(adresseRepository.findById("cbd584a516c5614b9bed0d2f")).thenReturn(Optional.of(a));

        Optional<Adresse> found = adresseService.getAdresseById("cbd584a516c5614b9bed0d2f");
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("cbd584a516c5614b9bed0d2f");
    }

    @Test
    void getAdresseById_empty() {
        when(adresseRepository.findById("missing")).thenReturn(Optional.empty());
        Optional<Adresse> notFound = adresseService.getAdresseById("missing");
        assertThat(notFound).isEmpty();
    }

    @Test
    void getAllAdressen_happy() {
        Adresse a1 = new Adresse();
        a1.setId("cbd584a516c5614b9bed0d2f");
        Adresse a2 = new Adresse();
        a2.setId("cb3abe666028006cf7bbf403");
        when(adresseRepository.findAll()).thenReturn(List.of(a1, a2));
        List<Adresse> first = adresseService.getAllAdressen();
        assertThat(first).hasSize(2);
    }

    @Test
    void getAllAdressen_empty() {
        when(adresseRepository.findAll()).thenReturn(List.of());
        List<Adresse> second = adresseService.getAllAdressen();
        assertThat(second).isEmpty();
    }

    @Test
    void getAdressenByType_happy() {
        Pageable pageable = PageRequest.of(0, 10);
        Adresse a = new Adresse();
        a.setId("ce63e54dc48b477e35ccc36e");

        Page<Adresse> pageWith = new PageImpl<>(List.of(a), pageable, 1);

        when(adresseRepository.findByTyp(AdressTyp.PRAXIS, pageable)).thenReturn(pageWith);
        Page<Adresse> p1 = adresseService.getAdressenByType(AdressTyp.PRAXIS, pageable);
        assertThat(p1.getContent()).hasSize(1);
    }

    @Test
    void getAdressenByType_empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Adresse> pageEmpty = new PageImpl<>(List.of(), pageable, 0);
        when(adresseRepository.findByTyp(AdressTyp.PRAXIS, pageable)).thenReturn(pageEmpty);
        Page<Adresse> p2 = adresseService.getAdressenByType(AdressTyp.PRAXIS, pageable);
        assertThat(p2.getContent()).isEmpty();
    }

    @Test
    void updateAdresse_happy() {
        AdresseCreateDTO dto = new AdresseCreateDTO();
        dto.setStrasse("Avenue de la Gare 7");
        dto.setPlz("1003");
        dto.setOrt("Lausanne");
        dto.setTyp(AdressTyp.HOME);

        Adresse existing = new Adresse();
        existing.setId("1834d339daee4305922cfd46");

        when(adresseRepository.findById("1834d339daee4305922cfd46")).thenReturn(Optional.of(existing));
        when(adresseRepository.save(existing)).thenReturn(existing);

        Adresse updated = adresseService.updateAdresse("1834d339daee4305922cfd46", dto);

        assertThat(updated.getOrt()).isEqualTo("Lausanne");
        assertThat(updated.getTyp()).isEqualTo(AdressTyp.HOME);
    }

    @Test
    void deleteAdresse_happy() {
        when(adresseRepository.existsById("10e7cb9c928d9cb715b0295e")).thenReturn(true);
        doNothing().when(adresseRepository).deleteById("10e7cb9c928d9cb715b0295e");
        adresseService.deleteAdresse("10e7cb9c928d9cb715b0295e");
        verify(adresseRepository).deleteById("10e7cb9c928d9cb715b0295e");
    }

    @Test
    void deleteAdresse_notFound() {
        when(adresseRepository.existsById("missing")).thenReturn(false);
        assertThatThrownBy(() -> adresseService.deleteAdresse("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Adresse mit id: missing nicht gefunden");
    }

    // Validation tests for duplicate Praxis Bezeichnung
    @Test
    void create_praxisWithDuplicateBezeichnung_throws() {
        AdresseCreateDTO dto = new AdresseCreateDTO();
        dto.setStrasse("Bahnhofstrasse 1");
        dto.setPlz("8001");
        dto.setOrt("Zürich");
        dto.setTyp(AdressTyp.PRAXIS);
        dto.setBezeichnung("Zahnarztpraxis am Bahnhof");

        Adresse existing = new Adresse();
        existing.setId("existingId");
        existing.setBezeichnung("Zahnarztpraxis am Bahnhof");

        when(adresseRepository.findByBezeichnung("Zahnarztpraxis am Bahnhof")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> adresseService.createAdresse(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Praxis mit Bezeichnung 'Zahnarztpraxis am Bahnhof' existiert bereits");
    }

    @Test
    void update_praxisWithDuplicateBezeichnung_throws() {
        AdresseCreateDTO dto = new AdresseCreateDTO();
        dto.setStrasse("Bahnhofstrasse 1");
        dto.setPlz("8001");
        dto.setOrt("Zürich");
        dto.setTyp(AdressTyp.PRAXIS);
        dto.setBezeichnung("Zahnarztpraxis am Bahnhof");

        Adresse existing = new Adresse();
        existing.setId("id1");
        existing.setBezeichnung("Alte Praxis");
        existing.setTyp(AdressTyp.PRAXIS);

        Adresse duplicate = new Adresse();
        duplicate.setId("id2");
        duplicate.setBezeichnung("Zahnarztpraxis am Bahnhof");

        when(adresseRepository.findById("id1")).thenReturn(Optional.of(existing));
        when(adresseRepository.findByBezeichnung("Zahnarztpraxis am Bahnhof")).thenReturn(Optional.of(duplicate));

        assertThatThrownBy(() -> adresseService.updateAdresse("id1", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Praxis mit Bezeichnung 'Zahnarztpraxis am Bahnhof' existiert bereits");
    }

    // NEW TESTS FOR 100% COVERAGE

    @Test
    void update_praxisWithSameBezeichnung_success() {
        AdresseCreateDTO dto = new AdresseCreateDTO();
        dto.setStrasse("Neue Strasse 10");
        dto.setPlz("8002");
        dto.setOrt("Zürich");
        dto.setTyp(AdressTyp.PRAXIS);
        dto.setBezeichnung("Zahnarztpraxis Zentrum");

        Adresse existing = new Adresse();
        existing.setId("id1");
        existing.setBezeichnung("Zahnarztpraxis Zentrum"); // Same name
        existing.setTyp(AdressTyp.PRAXIS);

        when(adresseRepository.findById("id1")).thenReturn(Optional.of(existing));
        when(adresseRepository.save(existing)).thenReturn(existing);

        Adresse updated = adresseService.updateAdresse("id1", dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getStrasse()).isEqualTo("Neue Strasse 10");
        verify(adresseRepository, never()).findByBezeichnung(anyString());
    }

    @Test
    void update_nonPraxisAdresse_doesNotCheckBezeichnung() {
        // Test the false branch of Praxis check (Line 52)
        AdresseCreateDTO dto = new AdresseCreateDTO();
        dto.setStrasse("Musterstrasse 10");
        dto.setPlz("8001");
        dto.setOrt("Zürich");
        dto.setTyp(AdressTyp.HOME); // Not a Praxis
        dto.setBezeichnung(null);

        Adresse existing = new Adresse();
        existing.setId("adresse123");
        existing.setStrasse("Alte Strasse 5");
        existing.setPlz("8001");
        existing.setOrt("Zürich");
        existing.setTyp(AdressTyp.HOME);
        existing.setBezeichnung(null);

        when(adresseRepository.findById("adresse123")).thenReturn(Optional.of(existing));
        when(adresseRepository.save(existing)).thenReturn(existing);

        Adresse result = adresseService.updateAdresse("adresse123", dto);

        assertThat(result).isNotNull();
        assertThat(result.getStrasse()).isEqualTo("Musterstrasse 10");
        // findByBezeichnung should not be called for non-Praxis addresses
        verify(adresseRepository, never()).findByBezeichnung(any());
    }

    @Test
    void update_praxisWithNullBezeichnung_doesNotCheckDuplicate() {
        // Test when Bezeichnung is null (Line 52 - second condition)
        AdresseCreateDTO dto = new AdresseCreateDTO();
        dto.setStrasse("Bahnhofstrasse 100");
        dto.setPlz("8000");
        dto.setOrt("Zürich");
        dto.setTyp(AdressTyp.PRAXIS);
        dto.setBezeichnung(null); // Null Bezeichnung

        Adresse existing = new Adresse();
        existing.setId("praxis123");
        existing.setStrasse("Bahnhofstrasse 50");
        existing.setPlz("8000");
        existing.setOrt("Zürich");
        existing.setTyp(AdressTyp.PRAXIS);
        existing.setBezeichnung(null);

        when(adresseRepository.findById("praxis123")).thenReturn(Optional.of(existing));
        when(adresseRepository.save(existing)).thenReturn(existing);

        Adresse result = adresseService.updateAdresse("praxis123", dto);

        assertThat(result).isNotNull();
        // findByBezeichnung should not be called when Bezeichnung is null
        verify(adresseRepository, never()).findByBezeichnung(any());
    }
}

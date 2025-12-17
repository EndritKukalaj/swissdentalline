package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.model.Zahnarzt;
import ch.zhaw.swissdentalline.repositories.ZahnarztRepository;
import ch.zhaw.swissdentalline.repositories.AdresseRepository;
import ch.zhaw.swissdentalline.model.Adresse;
import ch.zhaw.swissdentalline.model.AdressTyp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZahnarztServiceTest {

    @Mock
    ZahnarztRepository repo;

    @Mock
    AdresseRepository adresseRepository;

    @InjectMocks
    ZahnarztService service;

    private ZahnarztCreateDTO testDTO;
    private Zahnarzt testEntity;

    @BeforeEach
    void setUp() {
        testDTO = new ZahnarztCreateDTO();
        testDTO.setName("Dr. Markus Huber");
        testDTO.setPraxisAdresseId("ce63e54dc48b477e35ccc36e");

        testEntity = new Zahnarzt();
        testEntity.setId("17981e8ac4cbe09d6b9334bc");
        testEntity.setName(testDTO.getName());
        testEntity.setPraxisAdresseId(testDTO.getPraxisAdresseId());
    }

    @Test
    void shouldCreateZahnarzt() {
        // Validations: PraxisAdresse muss existieren und Typ PRAXIS; kein Duplikat
        Adresse praxis = new Adresse();
        praxis.setId(testDTO.getPraxisAdresseId());
        praxis.setTyp(AdressTyp.PRAXIS);
        when(adresseRepository.findById(testDTO.getPraxisAdresseId())).thenReturn(Optional.of(praxis));
        when(repo.findByNameAndPraxisAdresseId(testDTO.getName(), testDTO.getPraxisAdresseId())).thenReturn(Optional.empty());
        when(repo.save(any(Zahnarzt.class))).thenReturn(testEntity);

        Zahnarzt result = service.createZahnarzt(testDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("17981e8ac4cbe09d6b9334bc");
    }

    @Test
    void updateZahnarzt_whenNotFound_throws() {
        ZahnarztCreateDTO dto = new ZahnarztCreateDTO();
        when(repo.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateZahnarzt("missing", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Zahnarzt mit id: missing nicht gefunden");
    }

    @Test
    void getById_happy() {
        Zahnarzt z = new Zahnarzt(); z.setId("cfe29d3f75b5ab53e8a07b8d");
        when(repo.findById("cfe29d3f75b5ab53e8a07b8d")).thenReturn(Optional.of(z));
        assertThat(service.getZahnarztById("cfe29d3f75b5ab53e8a07b8d")).isPresent();
    }

    @Test
    void getById_empty() {
        when(repo.findById("missing")).thenReturn(Optional.empty());
        assertThat(service.getZahnarztById("missing")).isEmpty();
    }

    @Test
    void getAll_happy() {
        Zahnarzt z1 = new Zahnarzt(); Zahnarzt z2 = new Zahnarzt();
        when(repo.findAll()).thenReturn(List.of(z1, z2));
        assertThat(service.getAllZahnaerzte()).hasSize(2);
    }

    @Test
    void getAll_empty() {
        when(repo.findAll()).thenReturn(List.of());
        assertThat(service.getAllZahnaerzte()).isEmpty();
    }

    @Test
    void findByPraxisAdresse_happy() {
        Zahnarzt z = new Zahnarzt();
        when(repo.findByPraxisAdresseId("df7ce79e44542f6e4b4ba4e3")).thenReturn(List.of(z));
        assertThat(service.findByPraxisAdresse("df7ce79e44542f6e4b4ba4e3")).hasSize(1);
    }

    @Test
    void findByPraxisAdresse_empty() {
        when(repo.findByPraxisAdresseId("missing")).thenReturn(List.of());
        assertThat(service.findByPraxisAdresse("missing")).isEmpty();
    }

    @Test
    void findByName_happy() {
        Zahnarzt z = new Zahnarzt();
        when(repo.findByName("Dr. Anna Schmid")).thenReturn(List.of(z));
        assertThat(service.findByName("Dr. Anna Schmid")).hasSize(1);
    }

    @Test
    void findByName_empty() {
        when(repo.findByName("Nobody")).thenReturn(List.of());
        assertThat(service.findByName("Nobody")).isEmpty();
    }

    @Test
    void update_happy() {
        ZahnarztCreateDTO dto = new ZahnarztCreateDTO();
        dto.setName("Dr. Julia Keller");
        dto.setPraxisAdresseId("ce63e54dc48b477e35ccc36e");
        Adresse praxis = new Adresse();
        praxis.setId(dto.getPraxisAdresseId());
        praxis.setTyp(AdressTyp.PRAXIS);
        when(adresseRepository.findById(dto.getPraxisAdresseId())).thenReturn(Optional.of(praxis));
        when(repo.findByNameAndPraxisAdresseId(dto.getName(), dto.getPraxisAdresseId())).thenReturn(Optional.empty());

        Zahnarzt existing = new Zahnarzt(); existing.setId("7122c64e5a182215e0ba1585");
        when(repo.findById("7122c64e5a182215e0ba1585")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        Zahnarzt updated = service.updateZahnarzt("7122c64e5a182215e0ba1585", dto);
        assertThat(updated.getName()).isEqualTo("Dr. Julia Keller");
        assertThat(updated.getPraxisAdresseId()).isEqualTo("ce63e54dc48b477e35ccc36e");
    }

    @Test
    void delete_happy() {
        when(repo.existsById("ok")).thenReturn(true);
        doNothing().when(repo).deleteById("ok");
        service.deleteZahnarzt("ok");
        verify(repo).deleteById("ok");
    }

    @Test
    void delete_notFound() {
        when(repo.existsById("missing")).thenReturn(false);
        assertThatThrownBy(() -> service.deleteZahnarzt("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Zahnarzt mit id: missing nicht gefunden");
    }

    // Validation tests for PraxisAdresse foreign key, type PRAXIS, and duplicate
    @Test
    void create_withNonExistentPraxisAdresse_throws() {
        ZahnarztCreateDTO dto = new ZahnarztCreateDTO();
        dto.setName("Dr. Test");
        dto.setPraxisAdresseId("nonexistent");

        when(adresseRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createZahnarzt(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Praxis-Adresse mit id: nonexistent nicht gefunden");
    }

    @Test
    void create_withNonPraxisAdresse_throws() {
        ZahnarztCreateDTO dto = new ZahnarztCreateDTO();
        dto.setName("Dr. Test");
        dto.setPraxisAdresseId("homeAdresseId");

        Adresse homeAdresse = new Adresse();
        homeAdresse.setId("homeAdresseId");
        homeAdresse.setTyp(AdressTyp.HOME);

        when(adresseRepository.findById("homeAdresseId")).thenReturn(Optional.of(homeAdresse));

        assertThatThrownBy(() -> service.createZahnarzt(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Adresse mit id: homeAdresseId ist keine Praxis-Adresse");
    }

    @Test
    void create_withDuplicateZahnarzt_throws() {
        ZahnarztCreateDTO dto = new ZahnarztCreateDTO();
        dto.setName("Dr. Markus Huber");
        dto.setPraxisAdresseId("ce63e54dc48b477e35ccc36e");

        Adresse praxis = new Adresse();
        praxis.setId(dto.getPraxisAdresseId());
        praxis.setTyp(AdressTyp.PRAXIS);

        Zahnarzt existing = new Zahnarzt();
        existing.setId("existingId");
        existing.setName("Dr. Markus Huber");

        when(adresseRepository.findById(dto.getPraxisAdresseId())).thenReturn(Optional.of(praxis));
        when(repo.findByNameAndPraxisAdresseId(dto.getName(), dto.getPraxisAdresseId()))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.createZahnarzt(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Zahnarzt mit gleichem Namen in dieser Praxis existiert bereits");
    }

    @Test
    void update_withNonExistentPraxisAdresse_throws() {
        ZahnarztCreateDTO dto = new ZahnarztCreateDTO();
        dto.setName("Dr. Test");
        dto.setPraxisAdresseId("nonexistent");

        Zahnarzt existing = new Zahnarzt();
        existing.setId("zahnarztId");

        when(repo.findById("zahnarztId")).thenReturn(Optional.of(existing));
        when(adresseRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateZahnarzt("zahnarztId", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Praxis-Adresse mit id: nonexistent nicht gefunden");
    }

    @Test
    void update_withNonPraxisAdresse_throws() {
        ZahnarztCreateDTO dto = new ZahnarztCreateDTO();
        dto.setName("Dr. Test");
        dto.setPraxisAdresseId("homeAdresseId");

        Zahnarzt existing = new Zahnarzt();
        existing.setId("zahnarztId");

        Adresse homeAdresse = new Adresse();
        homeAdresse.setId("homeAdresseId");
        homeAdresse.setTyp(AdressTyp.HOME);

        when(repo.findById("zahnarztId")).thenReturn(Optional.of(existing));
        when(adresseRepository.findById("homeAdresseId")).thenReturn(Optional.of(homeAdresse));

        assertThatThrownBy(() -> service.updateZahnarzt("zahnarztId", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Adresse mit id: homeAdresseId ist keine Praxis-Adresse");
    }

    @Test
    void update_withDuplicateZahnarzt_throws() {
        ZahnarztCreateDTO dto = new ZahnarztCreateDTO();
        dto.setName("Dr. Markus Huber");
        dto.setPraxisAdresseId("ce63e54dc48b477e35ccc36e");

        Zahnarzt existing = new Zahnarzt();
        existing.setId("id1");

        Adresse praxis = new Adresse();
        praxis.setId(dto.getPraxisAdresseId());
        praxis.setTyp(AdressTyp.PRAXIS);

        Zahnarzt duplicate = new Zahnarzt();
        duplicate.setId("id2");
        duplicate.setName("Dr. Markus Huber");

        when(repo.findById("id1")).thenReturn(Optional.of(existing));
        when(adresseRepository.findById(dto.getPraxisAdresseId())).thenReturn(Optional.of(praxis));
        when(repo.findByNameAndPraxisAdresseId(dto.getName(), dto.getPraxisAdresseId()))
                .thenReturn(Optional.of(duplicate));

        assertThatThrownBy(() -> service.updateZahnarzt("id1", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Zahnarzt mit gleichem Namen in dieser Praxis existiert bereits");
    }

    // NEW TESTS FOR 100% COVERAGE

    @Test
    void getProfilByName_whenZahnarztDoesNotExist_returnsMinimalProfile() {
        String name = "NewZahnarzt";
        String email = "new@zahnarzt.ch";
        String role = "ZAHNARZT";

        when(repo.findByName("Dr. " + name)).thenReturn(List.of());

        ch.zhaw.swissdentalline.dto.ZahnarztProfilDTO result = service.getProfilByName(name, email, role);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getRole()).isEqualTo(role);
        assertThat(result.getPraxisname()).isNull();
        assertThat(result.getPraxisadresse()).isNull();
    }

    @Test
    void getProfilByName_whenZahnarztExists_returnsFullProfile() {
        String name = "Markus Huber";
        String email = "markus@zahnarzt.ch";
        String role = "ZAHNARZT";

        Zahnarzt zahnarzt = new Zahnarzt();
        zahnarzt.setId("zahnarzt1");
        zahnarzt.setName("Dr. Markus Huber");
        zahnarzt.setPraxisAdresseId("praxis1");

        Adresse praxisAdresse = new Adresse();
        praxisAdresse.setId("praxis1");
        praxisAdresse.setTyp(AdressTyp.PRAXIS);
        praxisAdresse.setBezeichnung("Zahnarztpraxis Zürich");
        praxisAdresse.setStrasse("Bahnhofstrasse 12");
        praxisAdresse.setPlz("8001");
        praxisAdresse.setOrt("Zürich");

        when(repo.findByName("Dr. " + name)).thenReturn(List.of(zahnarzt));
        when(adresseRepository.findById("praxis1")).thenReturn(Optional.of(praxisAdresse));

        ch.zhaw.swissdentalline.dto.ZahnarztProfilDTO result = service.getProfilByName(name, email, role);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getRole()).isEqualTo(role);
        assertThat(result.getPraxisname()).isEqualTo("Zahnarztpraxis Zürich");
        assertThat(result.getPraxisadresse()).isEqualTo("Bahnhofstrasse 12, 8001 Zürich");
    }

    @Test
    void getProfilByName_whenZahnarztExistsButPraxisAdresseNotFound_returnsProfileWithoutPraxisInfo() {
        String name = "Anna Schmid";
        String email = "anna@zahnarzt.ch";
        String role = "ZAHNARZT";

        Zahnarzt zahnarzt = new Zahnarzt();
        zahnarzt.setId("zahnarzt2");
        zahnarzt.setName("Dr. Anna Schmid");
        zahnarzt.setPraxisAdresseId("praxis2");

        when(repo.findByName("Dr. " + name)).thenReturn(List.of(zahnarzt));
        when(adresseRepository.findById("praxis2")).thenReturn(Optional.empty());

        ch.zhaw.swissdentalline.dto.ZahnarztProfilDTO result = service.getProfilByName(name, email, role);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getRole()).isEqualTo(role);
        assertThat(result.getPraxisname()).isNull();
        assertThat(result.getPraxisadresse()).isNull();
    }

    // EDGE CASE TESTS FOR 100% COVERAGE

    @Test
    void update_withDuplicateNameAndPraxis_whenIsDifferentZahnarzt_throws() {
        // Test the full duplicate check branch (Line 77)
        ZahnarztCreateDTO updateDTO = new ZahnarztCreateDTO();
        updateDTO.setName("Dr. Meier");
        updateDTO.setPraxisAdresseId("praxis123");

        Zahnarzt existing = new Zahnarzt();
        existing.setId("zahnarzt1");

        Zahnarzt duplicate = new Zahnarzt();
        duplicate.setId("zahnarzt2"); // Different ID
        duplicate.setName("Dr. Meier");
        duplicate.setPraxisAdresseId("praxis123");

        Adresse praxisAdresse = new Adresse();
        praxisAdresse.setId("praxis123");
        praxisAdresse.setTyp(AdressTyp.PRAXIS);

        when(repo.findById("zahnarzt1")).thenReturn(Optional.of(existing));
        when(adresseRepository.findById("praxis123")).thenReturn(Optional.of(praxisAdresse));
        when(repo.findByNameAndPraxisAdresseId("Dr. Meier", "praxis123"))
                .thenReturn(Optional.of(duplicate));

        assertThatThrownBy(() -> service.updateZahnarzt("zahnarzt1", updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Zahnarzt mit gleichem Namen in dieser Praxis existiert bereits");
    }
}

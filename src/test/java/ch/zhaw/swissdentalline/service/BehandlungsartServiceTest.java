package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.BehandlungsartCreateDTO;
import ch.zhaw.swissdentalline.model.Behandlungsart;
import ch.zhaw.swissdentalline.repositories.BehandlungsartRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BehandlungsartServiceTest {

    @Mock
    BehandlungsartRepository repo;

    @InjectMocks
    BehandlungsartService service;

    private BehandlungsartCreateDTO testDTO;
    private Behandlungsart testEntity;

    @BeforeEach
    void setUp() {
        testDTO = new BehandlungsartCreateDTO();
        testDTO.setName("Dentalhygiene");
        testDTO.setBeschreibung("Professionelle Zahnreinigung");

        testEntity = new Behandlungsart();
        testEntity.setId("f14c448a7fbd631240f5fd65");
        testEntity.setName(testDTO.getName());
        testEntity.setBeschreibung(testDTO.getBeschreibung());
    }

    @Test
    void shouldCreateBehandlungsart() {
        when(repo.findByName(testDTO.getName())).thenReturn(Optional.empty());
        when(repo.save(any(Behandlungsart.class))).thenReturn(testEntity);

        Behandlungsart result = service.createBehandlungsart(testDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("f14c448a7fbd631240f5fd65");
        assertThat(result.getName()).isEqualTo("Dentalhygiene");
    }

    @Test
    void update_whenNotFound_throws() {
        BehandlungsartCreateDTO dto = new BehandlungsartCreateDTO();
        when(repo.findById("missing")).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> service.updateBehandlungsart("missing", dto));
        assertTrue(ex.getMessage().contains("missing"));
    }

    @Test
    void getById_happy() {
        Behandlungsart b = new Behandlungsart();
        b.setId("f14c448a7fbd631240f5fd65");
        when(repo.findById("f14c448a7fbd631240f5fd65")).thenReturn(Optional.of(b));
        assertThat(service.getBehandlungsartById("f14c448a7fbd631240f5fd65")).isPresent();
    }

    @Test
    void getById_empty() {
        when(repo.findById("missing")).thenReturn(Optional.empty());
        assertThat(service.getBehandlungsartById("missing")).isEmpty();
    }

    @Test
    void getAll_happy() {
        Behandlungsart b1 = new Behandlungsart();
        b1.setId("id1");
        Behandlungsart b2 = new Behandlungsart();
        b2.setId("id2");
        when(repo.findAll()).thenReturn(List.of(b1, b2));
        assertThat(service.getAllBehandlungsarten()).hasSize(2);
    }

    @Test
    void getAll_empty() {
        when(repo.findAll()).thenReturn(List.of());
        assertThat(service.getAllBehandlungsarten()).isEmpty();
    }

    @Test
    void update_happy() {
        BehandlungsartCreateDTO newDto = new BehandlungsartCreateDTO();
        newDto.setName("Zahnaufhellung");
        newDto.setBeschreibung("Bleaching");

        Behandlungsart existing = new Behandlungsart();
        existing.setId("f3e4a3dc565c278142bf0b44");
        existing.setName("Alte Behandlung");

        when(repo.findById("f3e4a3dc565c278142bf0b44")).thenReturn(Optional.of(existing));
        when(repo.findByName(newDto.getName())).thenReturn(Optional.empty());
        when(repo.save(existing)).thenReturn(existing);

        Behandlungsart updated = service.updateBehandlungsart("f3e4a3dc565c278142bf0b44", newDto);

        assertThat(updated.getName()).isEqualTo("Zahnaufhellung");
        assertThat(updated.getBeschreibung()).isEqualTo("Bleaching");
    }

    @Test
    void delete_happy() {
        when(repo.existsById("f3e4a3dc565c278142bf0b44")).thenReturn(true);
        doNothing().when(repo).deleteById("f3e4a3dc565c278142bf0b44");
        service.deleteBehandlungsart("f3e4a3dc565c278142bf0b44");
        verify(repo).deleteById("f3e4a3dc565c278142bf0b44");
    }

    @Test
    void delete_notFound() {
        when(repo.existsById("missing")).thenReturn(false);
        assertThatThrownBy(() -> service.deleteBehandlungsart("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Behandlungsart mit id: missing nicht gefunden");
    }

    // Validation tests for duplicate name
    @Test
    void create_withDuplicateName_throws() {
        BehandlungsartCreateDTO dto = new BehandlungsartCreateDTO();
        dto.setName("Dentalhygiene");
        dto.setBeschreibung("Duplicate test");

        Behandlungsart existing = new Behandlungsart();
        existing.setId("existingId");
        existing.setName("Dentalhygiene");

        when(repo.findByName("Dentalhygiene")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.createBehandlungsart(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Behandlungsart mit Name 'Dentalhygiene' existiert bereits");
    }

    @Test
    void update_withDuplicateName_throws() {
        BehandlungsartCreateDTO dto = new BehandlungsartCreateDTO();
        dto.setName("Dentalhygiene");
        dto.setBeschreibung("Update test");

        Behandlungsart existing = new Behandlungsart();
        existing.setId("id1");
        existing.setName("Alte Behandlung");

        Behandlungsart duplicate = new Behandlungsart();
        duplicate.setId("id2");
        duplicate.setName("Dentalhygiene");

        when(repo.findById("id1")).thenReturn(Optional.of(existing));
        when(repo.findByName("Dentalhygiene")).thenReturn(Optional.of(duplicate));

        assertThatThrownBy(() -> service.updateBehandlungsart("id1", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Behandlungsart mit Name 'Dentalhygiene' existiert bereits");
    }

    // NEW TESTS FOR 100% COVERAGE

    @Test
    void update_withSameName_success() {
        BehandlungsartCreateDTO dto = new BehandlungsartCreateDTO();
        dto.setName("Dentalhygiene");
        dto.setBeschreibung("Updated description");

        Behandlungsart existing = new Behandlungsart();
        existing.setId("id1");
        existing.setName("Dentalhygiene"); // Same name

        when(repo.findById("id1")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        Behandlungsart updated = service.updateBehandlungsart("id1", dto);

        assertThat(updated).isNotNull();
        assertThat(updated.getBeschreibung()).isEqualTo("Updated description");
        verify(repo, never()).findByName(anyString());
    }
}

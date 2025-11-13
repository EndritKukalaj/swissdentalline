package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.mapper.ZahnarztMapper;
import ch.zhaw.swissdentalline.model.Zahnarzt;
import ch.zhaw.swissdentalline.repositories.ZahnarztRepository;
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
    ZahnarztMapper mapper;

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
        when(mapper.toEntity(testDTO)).thenReturn(testEntity);
        when(repo.save(testEntity)).thenReturn(testEntity);

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
}

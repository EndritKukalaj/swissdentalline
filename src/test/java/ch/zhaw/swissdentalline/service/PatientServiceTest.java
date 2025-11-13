package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.PatientCreateDTO;
import ch.zhaw.swissdentalline.mapper.PatientMapper;
import ch.zhaw.swissdentalline.model.Patient;
import ch.zhaw.swissdentalline.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    PatientRepository repo;

    @Mock
    PatientMapper mapper;

    @InjectMocks
    PatientService service;

    private PatientCreateDTO testDTO;
    private Patient testEntity;

    @BeforeEach
    void setUp() {
        testDTO = new PatientCreateDTO();
        testDTO.setName("Laura Meier");
        testDTO.setKrankenkasse("CSS");
        testDTO.setGeburtsdatum(new Date().toInstant());
        testDTO.setAdresseId("cbd584a516c5614b9bed0d2f");

        testEntity = new Patient();
        testEntity.setId("8aae436c3591dd4f961332e8");
        testEntity.setName(testDTO.getName());
        testEntity.setGeburtsdatum(testDTO.getGeburtsdatum());
        testEntity.setKrankenkasse(testDTO.getKrankenkasse());
        testEntity.setAdresseId(testDTO.getAdresseId());
    }

    @Test
    void shouldCreatePatient() {
        when(mapper.toEntity(testDTO)).thenReturn(testEntity);
        when(repo.save(testEntity)).thenReturn(testEntity);

        Patient result = service.createPatient(testDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("8aae436c3591dd4f961332e8");
    }

    @Test
    void updatePatient_whenNotFound_throws() {
        PatientCreateDTO dto = new PatientCreateDTO();
        when(repo.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updatePatient("missing", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient mit id: missing nicht gefunden");
    }

    @Test
    void getById_happy() {
        Patient p = new Patient();
        p.setId("8aae436c3591dd4f961332e8");
        when(repo.findById("8aae436c3591dd4f961332e8")).thenReturn(Optional.of(p));
        assertThat(service.getPatientById("8aae436c3591dd4f961332e8")).isPresent();
    }

    @Test
    void getById_empty() {
        when(repo.findById("missing")).thenReturn(Optional.empty());
        assertThat(service.getPatientById("missing")).isEmpty();
    }

    @Test
    void getAll_happy() {
        Patient p1 = new Patient();
        Patient p2 = new Patient();
        when(repo.findAll()).thenReturn(List.of(p1, p2));
        assertThat(service.getAllPatients()).hasSize(2);
    }

    @Test
    void getAll_empty() {
        when(repo.findAll()).thenReturn(List.of());
        assertThat(service.getAllPatients()).isEmpty();
    }

    @Test
    void findByName_happy() {
        Patient p = new Patient();
        when(repo.findByName("Laura Meier")).thenReturn(List.of(p));
        assertThat(service.findByName("Laura Meier")).hasSize(1);
    }

    @Test
    void findByName_empty() {
        when(repo.findByName("Nobody")).thenReturn(List.of());
        assertThat(service.findByName("Nobody")).isEmpty();
    }

    @Test
    void update_happy() {
        PatientCreateDTO dto = new PatientCreateDTO();
        dto.setName("Tim Baumann");
        dto.setAdresseId("cb3abe666028006cf7bbf403");
        dto.setGeburtsdatum(new Date().toInstant());
        dto.setKrankenkasse("SWICA");
        
        Patient existing = new Patient();
        existing.setId("5d027a7977ac31d8c88cccfb");

        when(repo.findById("5d027a7977ac31d8c88cccfb")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        Patient updated = service.updatePatient("5d027a7977ac31d8c88cccfb", dto);
        assertThat(updated.getName()).isEqualTo("Tim Baumann");
        assertThat(updated.getAdresseId()).isEqualTo("cb3abe666028006cf7bbf403");
    }

    @Test
    void delete_happy() {
        when(repo.existsById("ok")).thenReturn(true);
        doNothing().when(repo).deleteById("ok");
        service.deletePatient("ok");
        verify(repo).deleteById("ok");
    }

    @Test
    void delete_notFound() {
        when(repo.existsById("missing")).thenReturn(false);
        assertThatThrownBy(() -> service.deletePatient("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient mit id: missing nicht gefunden");
    }
}

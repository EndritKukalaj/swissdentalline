package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.EinnahmenProMonatDTO;
import ch.zhaw.swissdentalline.dto.TerminCreateDTO;
import ch.zhaw.swissdentalline.dto.TerminStatusAggregationDTO;
import ch.zhaw.swissdentalline.mapper.TerminMapper;
import ch.zhaw.swissdentalline.model.Termin;
import ch.zhaw.swissdentalline.model.TerminStatus;
import ch.zhaw.swissdentalline.repositories.TerminRepository;
import ch.zhaw.swissdentalline.repositories.ZahnarztRepository;
import ch.zhaw.swissdentalline.repositories.BehandlungsartRepository;
import ch.zhaw.swissdentalline.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TerminServiceTest {

    @Mock
    TerminRepository repo;

    @Mock
    TerminMapper mapper;

    @Mock
    ZahnarztRepository zahnarztRepository;

    @Mock
    BehandlungsartRepository behandlungsartRepository;

    @Mock
    PatientRepository patientRepository;

    @InjectMocks
    TerminService service;

    private TerminCreateDTO testDTO;
    private Termin testEntity;

    @BeforeEach
    void setUp() {
        testDTO = new TerminCreateDTO();
        testDTO.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        testDTO.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        testDTO.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        testDTO.setDauerMinuten(45);
        testDTO.setPreis(120.0);
        testDTO.setStatus(TerminStatus.FREI);

        testEntity = new Termin();
        testEntity.setId("78892595995796e6de28e995");
        testEntity.setZahnarztId(testDTO.getZahnarztId());
        testEntity.setBehandlungsartId(testDTO.getBehandlungsartId());
        testEntity.setDatum(testDTO.getDatum());
        testEntity.setDauerMinuten(testDTO.getDauerMinuten());
        testEntity.setPreis(testDTO.getPreis());
        testEntity.setStatus(testDTO.getStatus());
    }

    @Test
    void shouldCreateTermin() {
        // Arrange
        when(zahnarztRepository.existsById(testDTO.getZahnarztId())).thenReturn(true);
        when(behandlungsartRepository.existsById(testDTO.getBehandlungsartId())).thenReturn(true);
        when(mapper.toEntity(testDTO)).thenReturn(testEntity);
        when(repo.save(testEntity)).thenReturn(testEntity);

        Termin result = service.createTermin(testDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("78892595995796e6de28e995");
    }

    @Test
    void bookTermin_whenNotFree_throws() {
        Termin t = new Termin();
        t.setId("78892595995796e6de28e995");
        t.setStatus(TerminStatus.GEBUCHT);

        when(repo.findById("78892595995796e6de28e995")).thenReturn(Optional.of(t));

        assertThatThrownBy(() -> service.bookTermin("78892595995796e6de28e995", false, "8aae436c3591dd4f961332e8"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Termin ist nicht verfügbar");
    }

    @Test
    void getById_happy() {
        Termin t = new Termin();
        t.setId("78892595995796e6de28e995");
        when(repo.findById("78892595995796e6de28e995")).thenReturn(Optional.of(t));
        assertThat(service.getTerminById("78892595995796e6de28e995")).isPresent();
    }

    @Test
    void getById_empty() {
        when(repo.findById("missing")).thenReturn(Optional.empty());
        assertThat(service.getTerminById("missing")).isEmpty();
    }

    @Test
    void getAll_happy() {
        Termin t1 = new Termin();
        Termin t2 = new Termin();
        when(repo.findAll()).thenReturn(List.of(t1, t2));
        assertThat(service.getAllTermine()).hasSize(2);
    }

    @Test
    void getAll_empty() {
        when(repo.findAll()).thenReturn(List.of());
        assertThat(service.getAllTermine()).isEmpty();
    }

    @Test
    void findByStatusAndDateRange_happy() {
        Pageable pageable = PageRequest.of(0, 10);
        Termin t = new Termin();
        Page<Termin> pageWith = new PageImpl<>(List.of(t), pageable, 1);

        Instant start = Instant.parse("2025-01-10T00:00:00Z");
        Instant end = Instant.parse("2025-01-11T00:00:00Z");

        when(repo.findByStatusAndDatumBetween(TerminStatus.GEBUCHT, start, end, pageable)).thenReturn(pageWith);
        assertThat(service.findTermineByStatusAndDateRange(TerminStatus.GEBUCHT, start, end, pageable).getContent())
                .hasSize(1);
    }

    @Test
    void findByStatusAndDateRange_empty() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Termin> pageEmpty = new PageImpl<>(List.of(), pageable, 0);
        Instant start = Instant.parse("2025-01-10T00:00:00Z");
        Instant end = Instant.parse("2025-01-11T00:00:00Z");
        when(repo.findByStatusAndDatumBetween(TerminStatus.GEBUCHT, start, end, pageable)).thenReturn(pageEmpty);
        assertThat(service.findTermineByStatusAndDateRange(TerminStatus.GEBUCHT, start, end, pageable).getContent())
                .isEmpty();
    }

    @Test
    void searchAvailable_happy() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Termin> pageWith = new PageImpl<>(List.of(new Termin()), pageable, 1);
        when(repo.findByStatus(TerminStatus.FREI, pageable)).thenReturn(pageWith);
        assertThat(service.searchAvailableTermine(pageable).getContent()).hasSize(1);
    }

    @Test
    void searchAvailable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Termin> pageEmpty = new PageImpl<>(List.of(), pageable, 0);
        when(repo.findByStatus(TerminStatus.FREI, pageable)).thenReturn(pageEmpty);
        assertThat(service.searchAvailableTermine(pageable).getContent()).isEmpty();
    }

    @Test
    void findFlexTermine_happy() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Termin> pageWith = new PageImpl<>(List.of(new Termin()), pageable, 1);
        when(repo.findByStatus(TerminStatus.ABGESAGT, pageable)).thenReturn(pageWith);
        assertThat(service.findFlexTermine(pageable).getContent()).hasSize(1);
    }

    @Test
    void findFlexTermine_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Termin> pageEmpty = new PageImpl<>(List.of(), pageable, 0);
        when(repo.findByStatus(TerminStatus.ABGESAGT, pageable)).thenReturn(pageEmpty);
        assertThat(service.findFlexTermine(pageable).getContent()).isEmpty();
    }

    @Test
    void getTerminStateAggregation_happy() {
        TerminStatusAggregationDTO dto = new TerminStatusAggregationDTO();
        dto.setStatus(TerminStatus.FREI);
        dto.setAnzahl(3L);
        dto.setTerminIds(List.of("id1"));
        List<TerminStatusAggregationDTO> agg = List.of(dto);
        when(repo.getTerminStateAggregation("cfe29d3f75b5ab53e8a07b8d")).thenReturn(agg);
        assertThat(service.getTerminStateAggregation("cfe29d3f75b5ab53e8a07b8d")).hasSize(1);
    }

    @Test
    void getTerminStateAggregation_empty() {
        when(repo.getTerminStateAggregation("cfe29d3f75b5ab53e8a07b8d")).thenReturn(List.of());
        assertThat(service.getTerminStateAggregation("cfe29d3f75b5ab53e8a07b8d")).isEmpty();
    }

    @Test
    void getEinnahmenProMonatByZahnarzt_happy() {
        List<EinnahmenProMonatDTO> rev = List.of(new EinnahmenProMonatDTO("2025-01", 120.0, 1L));
        Instant start = Instant.parse("2025-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-01-31T23:59:59Z");
        when(repo.getEinnahmenProMonatById("cfe29d3f75b5ab53e8a07b8d", start, end)).thenReturn(rev);
        assertThat(service.getEinnahmenProMonatByZahnarzt("cfe29d3f75b5ab53e8a07b8d", start, end)).hasSize(1);
    }

    @Test
    void getEinnahmenProMonatByZahnarzt_empty() {
        Instant start = Instant.parse("2025-01-01T00:00:00Z");
        Instant end = Instant.parse("2025-01-31T23:59:59Z");
        when(repo.getEinnahmenProMonatById("cfe29d3f75b5ab53e8a07b8d", start, end)).thenReturn(List.of());
        assertThat(service.getEinnahmenProMonatByZahnarzt("cfe29d3f75b5ab53e8a07b8d", start, end)).isEmpty();
    }

    @Test
    void update_happy() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setStatus(TerminStatus.FREI);
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(150.0);

        when(zahnarztRepository.existsById(dto.getZahnarztId())).thenReturn(true);
        when(behandlungsartRepository.existsById(dto.getBehandlungsartId())).thenReturn(true);

        Termin existing = new Termin();
        existing.setId("78892595995796e6de28e995");
        when(repo.findById("78892595995796e6de28e995")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);
        Termin updated = service.updateTermin("78892595995796e6de28e995", dto);
        assertThat(updated.getZahnarztId()).isEqualTo("cfe29d3f75b5ab53e8a07b8d");
    }

    @Test
    void update_notFound() {
        TerminCreateDTO dto = new TerminCreateDTO();
        when(repo.findById("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateTermin("missing", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Termin mit id: missing nicht gefunden");
    }

    @Test
    void delete_happy() {
        when(repo.existsById("ok")).thenReturn(true);
        doNothing().when(repo).deleteById("ok");
        service.deleteTermin("ok");
        verify(repo).deleteById("ok");
    }

    @Test
    void delete_notFound() {
        when(repo.existsById("missing")).thenReturn(false);
        assertThatThrownBy(() -> service.deleteTermin("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Termin mit id: missing nicht gefunden");
    }

    @Test
    void bookTermin_happy() {
        Termin frei = new Termin();
        frei.setId("498242db8eedaefbb8f764cb");
        frei.setStatus(TerminStatus.FREI);
        frei.setDatum(Instant.parse("2025-01-10T11:45:00Z"));
        frei.setDauerMinuten(45);
        when(repo.findById("498242db8eedaefbb8f764cb")).thenReturn(Optional.of(frei));
        // Foreign key + overlap validations
        when(patientRepository.existsById("8aae436c3591dd4f961332e8")).thenReturn(true);
        when(repo.findByPatientIdAndDatumBetween(eq("8aae436c3591dd4f961332e8"), any(), any())).thenReturn(List.of());
        when(repo.save(frei)).thenReturn(frei);
        Termin booked = service.bookTermin("498242db8eedaefbb8f764cb", true, "8aae436c3591dd4f961332e8");
        assertThat(booked.getStatus()).isEqualTo(TerminStatus.GEBUCHT);
        assertThat(booked.getPatientId()).isEqualTo("8aae436c3591dd4f961332e8");
    }

    @Test
    void cancelTermin_happy() {
        Termin toCancel = new Termin();
        toCancel.setId("7fead2286c81cbba349bb958");
        when(repo.findById("7fead2286c81cbba349bb958")).thenReturn(Optional.of(toCancel));
        when(repo.save(toCancel)).thenReturn(toCancel);
        Termin canceled = service.cancelTermin("7fead2286c81cbba349bb958");
        assertThat(canceled.getStatus()).isEqualTo(TerminStatus.ABGESAGT);
        assertThat(canceled.getPatientId()).isNull();
    }

    @Test
    void cancelTermin_notFound() {
        when(repo.findById("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.cancelTermin("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Termin mit id: missing nicht gefunden");
    }

    @Test
    void completeTermin_happy() {
        Termin toComplete = new Termin();
        toComplete.setId("775e2da08278718f54b7cd96");
        toComplete.setStatus(TerminStatus.GEBUCHT);
        when(repo.findById("775e2da08278718f54b7cd96")).thenReturn(Optional.of(toComplete));
        when(repo.save(toComplete)).thenReturn(toComplete);
        Termin completed = service.completeTermin("775e2da08278718f54b7cd96");
        assertThat(completed.getStatus()).isEqualTo(TerminStatus.ABGESCHLOSSEN);
    }

    @Test
    void completeTermin_errorNotGebucht() {
        Termin wrong = new Termin();
        wrong.setId("wrong");
        wrong.setStatus(TerminStatus.FREI);
        when(repo.findById("wrong")).thenReturn(Optional.of(wrong));
        assertThatThrownBy(() -> service.completeTermin("wrong"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Nur gebuchte Termine können abgeschlossen werden");
    }

    // Validation tests for foreign keys and overlap
    @Test
    void create_withNonExistentZahnarzt_throws() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("nonexistent");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.FREI);

        when(zahnarztRepository.existsById("nonexistent")).thenReturn(false);

        assertThatThrownBy(() -> service.createTermin(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Zahnarzt mit id: nonexistent nicht gefunden");
    }

    @Test
    void create_withNonExistentBehandlungsart_throws() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("nonexistent");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.FREI);

        when(zahnarztRepository.existsById("cfe29d3f75b5ab53e8a07b8d")).thenReturn(true);
        when(behandlungsartRepository.existsById("nonexistent")).thenReturn(false);

        assertThatThrownBy(() -> service.createTermin(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Behandlungsart mit id: nonexistent nicht gefunden");
    }

    @Test
    void create_withNonExistentPatient_throws() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setPatientId("nonexistent");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.GEBUCHT);

        when(zahnarztRepository.existsById("cfe29d3f75b5ab53e8a07b8d")).thenReturn(true);
        when(behandlungsartRepository.existsById("f3e4a3dc565c278142bf0b44")).thenReturn(true);
        when(patientRepository.existsById("nonexistent")).thenReturn(false);

        assertThatThrownBy(() -> service.createTermin(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient mit id: nonexistent nicht gefunden");
    }

    @Test
    void create_withOverlappingPatientTermin_throws() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setPatientId("8aae436c3591dd4f961332e8");
        dto.setDatum(Instant.parse("2025-01-10T09:00:00Z"));
        dto.setDauerMinuten(60);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.GEBUCHT);

        Termin overlapping = new Termin();
        overlapping.setId("overlappingId");
        overlapping.setDatum(Instant.parse("2025-01-10T09:30:00Z"));
        overlapping.setDauerMinuten(45);

        when(zahnarztRepository.existsById("cfe29d3f75b5ab53e8a07b8d")).thenReturn(true);
        when(behandlungsartRepository.existsById("f3e4a3dc565c278142bf0b44")).thenReturn(true);
        when(patientRepository.existsById("8aae436c3591dd4f961332e8")).thenReturn(true);
        when(repo.findByPatientIdAndDatumBetween(eq("8aae436c3591dd4f961332e8"), any(), any()))
                .thenReturn(List.of(overlapping));

        assertThatThrownBy(() -> service.createTermin(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Patient hat bereits einen Termin am")
                .hasMessageContaining("der sich mit dem neuen Termin überschneidet");
    }

    @Test
    void bookTermin_withNonExistentPatient_throws() {
        Termin frei = new Termin();
        frei.setId("terminId");
        frei.setStatus(TerminStatus.FREI);

        when(repo.findById("terminId")).thenReturn(Optional.of(frei));
        when(patientRepository.existsById("nonexistent")).thenReturn(false);

        assertThatThrownBy(() -> service.bookTermin("terminId", true, "nonexistent"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient mit id: nonexistent nicht gefunden");
    }

    @Test
    void bookTermin_withOverlappingPatientTermin_throws() {
        Termin frei = new Termin();
        frei.setId("terminId");
        frei.setStatus(TerminStatus.FREI);
        frei.setDatum(Instant.parse("2025-01-10T10:00:00Z"));
        frei.setDauerMinuten(45);

        Termin overlapping = new Termin();
        overlapping.setId("overlappingId");
        overlapping.setDatum(Instant.parse("2025-01-10T10:15:00Z"));
        overlapping.setDauerMinuten(30);

        when(repo.findById("terminId")).thenReturn(Optional.of(frei));
        when(patientRepository.existsById("8aae436c3591dd4f961332e8")).thenReturn(true);
        when(repo.findByPatientIdAndDatumBetween(eq("8aae436c3591dd4f961332e8"), any(), any()))
                .thenReturn(List.of(overlapping));

        assertThatThrownBy(() -> service.bookTermin("terminId", true, "8aae436c3591dd4f961332e8"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Patient hat bereits einen Termin am")
                .hasMessageContaining("der sich mit dem neuen Termin überschneidet");
    }

    @Test
    void update_withNonExistentZahnarzt_throws() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("nonexistent");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.FREI);

        Termin existing = new Termin();
        existing.setId("terminId");

        when(repo.findById("terminId")).thenReturn(Optional.of(existing));
        when(zahnarztRepository.existsById("nonexistent")).thenReturn(false);

        assertThatThrownBy(() -> service.updateTermin("terminId", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Zahnarzt mit id: nonexistent nicht gefunden");
    }

    @Test
    void update_withOverlappingPatientTermin_throws() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setPatientId("8aae436c3591dd4f961332e8");
        dto.setDatum(Instant.parse("2025-01-10T09:00:00Z"));
        dto.setDauerMinuten(60);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.GEBUCHT);

        Termin existing = new Termin();
        existing.setId("id1");

        Termin overlapping = new Termin();
        overlapping.setId("id2");
        overlapping.setDatum(Instant.parse("2025-01-10T09:30:00Z"));
        overlapping.setDauerMinuten(45);

        when(repo.findById("id1")).thenReturn(Optional.of(existing));
        when(zahnarztRepository.existsById("cfe29d3f75b5ab53e8a07b8d")).thenReturn(true);
        when(behandlungsartRepository.existsById("f3e4a3dc565c278142bf0b44")).thenReturn(true);
        when(patientRepository.existsById("8aae436c3591dd4f961332e8")).thenReturn(true);
        when(repo.findByPatientIdAndDatumBetween(eq("8aae436c3591dd4f961332e8"), any(), any()))
                .thenReturn(List.of(overlapping));

        assertThatThrownBy(() -> service.updateTermin("id1", dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Patient hat bereits einen Termin am")
                .hasMessageContaining("der sich mit dem neuen Termin überschneidet");
    }
}

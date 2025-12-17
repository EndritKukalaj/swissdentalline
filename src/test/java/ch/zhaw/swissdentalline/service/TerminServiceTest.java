package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.TerminCreateDTO;
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
        when(repo.save(any(Termin.class))).thenReturn(testEntity);

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

    // NEW TESTS FOR 100% COVERAGE

    @Test
    void update_withNonExistentBehandlungsart_throws() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("nonexistent");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.FREI);

        Termin existing = new Termin();
        existing.setId("terminId");

        when(repo.findById("terminId")).thenReturn(Optional.of(existing));
        when(zahnarztRepository.existsById("cfe29d3f75b5ab53e8a07b8d")).thenReturn(true);
        when(behandlungsartRepository.existsById("nonexistent")).thenReturn(false);

        assertThatThrownBy(() -> service.updateTermin("terminId", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Behandlungsart mit id: nonexistent nicht gefunden");
    }

    @Test
    void update_withNonExistentPatient_throws() {
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setPatientId("nonexistent");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.GEBUCHT);

        Termin existing = new Termin();
        existing.setId("terminId");

        when(repo.findById("terminId")).thenReturn(Optional.of(existing));
        when(zahnarztRepository.existsById("cfe29d3f75b5ab53e8a07b8d")).thenReturn(true);
        when(behandlungsartRepository.existsById("f3e4a3dc565c278142bf0b44")).thenReturn(true);
        when(patientRepository.existsById("nonexistent")).thenReturn(false);

        assertThatThrownBy(() -> service.updateTermin("terminId", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Patient mit id: nonexistent nicht gefunden");
    }

    @Test
    void findRelevantFlexTermineForPatient_withNoBookedAppointments_returnsEmpty() {
        String patientId = "patient123";
        when(repo.findByPatientIdAndStatusAndWartelisteAktiv(patientId, TerminStatus.GEBUCHT, true))
                .thenReturn(List.of());

        List<Termin> result = service.findRelevantFlexTermineForPatient(patientId);

        assertThat(result).isEmpty();
        verify(repo, never()).findByStatusAndBehandlungsartId(any(), any());
    }

    @Test
    void findRelevantFlexTermineForPatient_withBookedAppointments_returnsRelevantFlexTermine() {
        String patientId = "patient123";
        String behandlungsartId1 = "behandlung1";
        String behandlungsartId2 = "behandlung2";

        Termin bookedTermin1 = new Termin();
        bookedTermin1.setId("booked1");
        bookedTermin1.setPatientId(patientId);
        bookedTermin1.setStatus(TerminStatus.GEBUCHT);
        bookedTermin1.setWartelisteAktiv(true);
        bookedTermin1.setBehandlungsartId(behandlungsartId1);

        Termin bookedTermin2 = new Termin();
        bookedTermin2.setId("booked2");
        bookedTermin2.setPatientId(patientId);
        bookedTermin2.setStatus(TerminStatus.GEBUCHT);
        bookedTermin2.setWartelisteAktiv(true);
        bookedTermin2.setBehandlungsartId(behandlungsartId2);

        Termin flexTermin1 = new Termin();
        flexTermin1.setId("flex1");
        flexTermin1.setStatus(TerminStatus.FLEX);
        flexTermin1.setBehandlungsartId(behandlungsartId1);
        flexTermin1.setDatum(Instant.now().plusSeconds(86400 * 10)); // 10 days in future

        Termin flexTermin2Past = new Termin();
        flexTermin2Past.setId("flex2");
        flexTermin2Past.setStatus(TerminStatus.FLEX);
        flexTermin2Past.setBehandlungsartId(behandlungsartId1);
        flexTermin2Past.setDatum(Instant.now().minusSeconds(86400)); // Past

        Termin flexTermin3 = new Termin();
        flexTermin3.setId("flex3");
        flexTermin3.setStatus(TerminStatus.FLEX);
        flexTermin3.setBehandlungsartId(behandlungsartId2);
        flexTermin3.setDatum(Instant.now().plusSeconds(86400 * 5)); // 5 days in future

        when(repo.findByPatientIdAndStatusAndWartelisteAktiv(patientId, TerminStatus.GEBUCHT, true))
                .thenReturn(List.of(bookedTermin1, bookedTermin2));
        when(repo.findByStatusAndBehandlungsartId(TerminStatus.FLEX, behandlungsartId1))
                .thenReturn(List.of(flexTermin1, flexTermin2Past));
        when(repo.findByStatusAndBehandlungsartId(TerminStatus.FLEX, behandlungsartId2))
                .thenReturn(List.of(flexTermin3));

        List<Termin> result = service.findRelevantFlexTermineForPatient(patientId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Termin::getId).containsExactly("flex3", "flex1"); // sorted by date
    }

    @Test
    void rebookToFlexTermin_whenOldTerminNotGebucht_throws() {
        String oldTerminId = "old";
        String flexTerminId = "flex";
        String patientId = "patient123";

        Termin oldTermin = new Termin();
        oldTermin.setId(oldTerminId);
        oldTermin.setPatientId(patientId);
        oldTermin.setStatus(TerminStatus.ABGESAGT);

        when(repo.findById(oldTerminId)).thenReturn(Optional.of(oldTermin));

        assertThatThrownBy(() -> service.rebookToFlexTermin(oldTerminId, flexTerminId, patientId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Nur gebuchte Termine können umgebucht werden");
    }

    @Test
    void rebookToFlexTermin_whenFlexTerminNotFlex_throws() {
        String oldTerminId = "old";
        String flexTerminId = "flex";
        String patientId = "patient123";

        Termin oldTermin = new Termin();
        oldTermin.setId(oldTerminId);
        oldTermin.setPatientId(patientId);
        oldTermin.setStatus(TerminStatus.GEBUCHT);

        Termin flexTermin = new Termin();
        flexTermin.setId(flexTerminId);
        flexTermin.setStatus(TerminStatus.GEBUCHT);

        when(repo.findById(oldTerminId)).thenReturn(Optional.of(oldTermin));
        when(repo.findById(flexTerminId)).thenReturn(Optional.of(flexTermin));

        assertThatThrownBy(() -> service.rebookToFlexTermin(oldTerminId, flexTerminId, patientId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Termin ist kein verfügbarer Flex-Termin");
    }

    @Test
    void rebookToFlexTermin_whenBehandlungsartDoesNotMatch_throws() {
        String oldTerminId = "old";
        String flexTerminId = "flex";
        String patientId = "patient123";

        Termin oldTermin = new Termin();
        oldTermin.setId(oldTerminId);
        oldTermin.setPatientId(patientId);
        oldTermin.setStatus(TerminStatus.GEBUCHT);
        oldTermin.setBehandlungsartId("behandlung1");

        Termin flexTermin = new Termin();
        flexTermin.setId(flexTerminId);
        flexTermin.setStatus(TerminStatus.FLEX);
        flexTermin.setBehandlungsartId("behandlung2");

        when(repo.findById(oldTerminId)).thenReturn(Optional.of(oldTermin));
        when(repo.findById(flexTerminId)).thenReturn(Optional.of(flexTermin));

        assertThatThrownBy(() -> service.rebookToFlexTermin(oldTerminId, flexTerminId, patientId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Behandlungsart des Flex-Termins stimmt nicht überein");
    }

    @Test
    void rebookToFlexTermin_within7Days_applies10PercentDiscount() {
        String oldTerminId = "old";
        String flexTerminId = "flex";
        String patientId = "patient123";

        Termin oldTermin = new Termin();
        oldTermin.setId(oldTerminId);
        oldTermin.setPatientId(patientId);
        oldTermin.setStatus(TerminStatus.GEBUCHT);
        oldTermin.setBehandlungsartId("behandlung1");
        oldTermin.setWartelisteAktiv(true);

        Termin flexTermin = new Termin();
        flexTermin.setId(flexTerminId);
        flexTermin.setStatus(TerminStatus.FLEX);
        flexTermin.setBehandlungsartId("behandlung1");
        flexTermin.setDatum(Instant.now().plusSeconds(86400 * 5)); // 5 days in future
        flexTermin.setPreis(100.0);

        when(repo.findById(oldTerminId)).thenReturn(Optional.of(oldTermin));
        when(repo.findById(flexTerminId)).thenReturn(Optional.of(flexTermin));
        when(repo.save(any(Termin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Termin result = service.rebookToFlexTermin(oldTerminId, flexTerminId, patientId);

        assertThat(result.getPreis()).isEqualTo(90.0); // 10% discount
        assertThat(result.getStatus()).isEqualTo(TerminStatus.GEBUCHT);
        assertThat(result.getPatientId()).isEqualTo(patientId);
    }

    @Test
    void rebookToFlexTermin_within8to14Days_applies7PercentDiscount() {
        String oldTerminId = "old";
        String flexTerminId = "flex";
        String patientId = "patient123";

        Termin oldTermin = new Termin();
        oldTermin.setId(oldTerminId);
        oldTermin.setPatientId(patientId);
        oldTermin.setStatus(TerminStatus.GEBUCHT);
        oldTermin.setBehandlungsartId("behandlung1");
        oldTermin.setWartelisteAktiv(false);

        Termin flexTermin = new Termin();
        flexTermin.setId(flexTerminId);
        flexTermin.setStatus(TerminStatus.FLEX);
        flexTermin.setBehandlungsartId("behandlung1");
        flexTermin.setDatum(Instant.now().plusSeconds(86400 * 10)); // 10 days in future
        flexTermin.setPreis(100.0);

        when(repo.findById(oldTerminId)).thenReturn(Optional.of(oldTermin));
        when(repo.findById(flexTerminId)).thenReturn(Optional.of(flexTermin));
        when(repo.save(any(Termin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Termin result = service.rebookToFlexTermin(oldTerminId, flexTerminId, patientId);

        assertThat(result.getPreis()).isEqualTo(93.0); // 7% discount
        assertThat(result.getStatus()).isEqualTo(TerminStatus.GEBUCHT);
        assertThat(result.getPatientId()).isEqualTo(patientId);
        assertThat(result.isWartelisteAktiv()).isFalse();
    }

    @Test
    void releaseFlexTermin_happy() {
        String terminId = "termin123";
        Termin termin = new Termin();
        termin.setId(terminId);
        termin.setStatus(TerminStatus.ABGESAGT);
        termin.setPatientId("patient123");

        when(repo.findById(terminId)).thenReturn(Optional.of(termin));
        when(repo.save(any(Termin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Termin result = service.releaseFlexTermin(terminId);

        assertThat(result.getStatus()).isEqualTo(TerminStatus.FLEX);
        assertThat(result.getPatientId()).isNull();
        assertThat(result.isWartelisteAktiv()).isFalse();
    }

    @Test
    void releaseFlexTermin_whenNotAbgesagt_throws() {
        String terminId = "termin123";
        Termin termin = new Termin();
        termin.setId(terminId);
        termin.setStatus(TerminStatus.GEBUCHT);

        when(repo.findById(terminId)).thenReturn(Optional.of(termin));

        assertThatThrownBy(() -> service.releaseFlexTermin(terminId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Nur abgesagte Termine können als Flex-Termin freigegeben werden");
    }

    // EDGE CASE TESTS FOR 100% COVERAGE

    @Test
    void create_withPatientIdSet_callsOverlapCheck() {
        // Test that checkPatientOverlap is actually called when PatientId is set (Line 48)
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setPatientId("8aae436c3591dd4f961332e8");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.GEBUCHT);

        when(zahnarztRepository.existsById(dto.getZahnarztId())).thenReturn(true);
        when(behandlungsartRepository.existsById(dto.getBehandlungsartId())).thenReturn(true);
        when(patientRepository.existsById(dto.getPatientId())).thenReturn(true);
        when(repo.findByPatientIdAndDatumBetween(eq(dto.getPatientId()), any(), any())).thenReturn(List.of());
        when(repo.save(any(Termin.class))).thenReturn(testEntity);

        Termin result = service.createTermin(dto);

        assertThat(result).isNotNull();
        verify(repo).findByPatientIdAndDatumBetween(eq(dto.getPatientId()), any(), any());
    }

    @Test
    void update_withPatientIdSet_callsOverlapCheck() {
        // Test that checkPatientOverlap is actually called in update when PatientId is set (Line 115)
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setPatientId("8aae436c3591dd4f961332e8");
        dto.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        dto.setDauerMinuten(45);
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.GEBUCHT);

        Termin existing = new Termin();
        existing.setId("terminId");

        when(repo.findById("terminId")).thenReturn(Optional.of(existing));
        when(zahnarztRepository.existsById(dto.getZahnarztId())).thenReturn(true);
        when(behandlungsartRepository.existsById(dto.getBehandlungsartId())).thenReturn(true);
        when(patientRepository.existsById(dto.getPatientId())).thenReturn(true);
        when(repo.findByPatientIdAndDatumBetween(eq(dto.getPatientId()), any(), any())).thenReturn(List.of());
        when(repo.save(existing)).thenReturn(existing);

        Termin result = service.updateTermin("terminId", dto);

        assertThat(result).isNotNull();
        verify(repo).findByPatientIdAndDatumBetween(eq(dto.getPatientId()), any(), any());
    }

    @Test
    void checkPatientOverlap_skipsOwnTerminWhenUpdating() {
        // Test self-exclusion logic (Line 68-69)
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

        // Same appointment in the query window, but should be skipped
        Termin sameTermin = new Termin();
        sameTermin.setId("id1"); // Same ID
        sameTermin.setDatum(Instant.parse("2025-01-10T09:00:00Z"));
        sameTermin.setDauerMinuten(60);

        when(repo.findById("id1")).thenReturn(Optional.of(existing));
        when(zahnarztRepository.existsById(dto.getZahnarztId())).thenReturn(true);
        when(behandlungsartRepository.existsById(dto.getBehandlungsartId())).thenReturn(true);
        when(patientRepository.existsById(dto.getPatientId())).thenReturn(true);
        when(repo.findByPatientIdAndDatumBetween(eq(dto.getPatientId()), any(), any()))
                .thenReturn(List.of(sameTermin)); // Returns the same appointment
        when(repo.save(existing)).thenReturn(existing);

        // Should NOT throw because it's the same appointment being updated
        Termin result = service.updateTermin("id1", dto);

        assertThat(result).isNotNull();
    }

    @Test
    void checkPatientOverlap_noOverlapWhenAppointmentsDoNotActuallyOverlap() {
        // Test the false branch of overlap detection (Line 76)
        TerminCreateDTO dto = new TerminCreateDTO();
        dto.setZahnarztId("cfe29d3f75b5ab53e8a07b8d");
        dto.setBehandlungsartId("f3e4a3dc565c278142bf0b44");
        dto.setPatientId("8aae436c3591dd4f961332e8");
        dto.setDatum(Instant.parse("2025-01-10T09:00:00Z"));
        dto.setDauerMinuten(30); // 9:00 - 9:30
        dto.setPreis(120.0);
        dto.setStatus(TerminStatus.GEBUCHT);

        // Existing appointment that's in the query window but doesn't overlap
        Termin existingBefore = new Termin();
        existingBefore.setId("before");
        existingBefore.setDatum(Instant.parse("2025-01-10T08:00:00Z"));
        existingBefore.setDauerMinuten(30); // 8:00 - 8:30, no overlap

        when(zahnarztRepository.existsById(dto.getZahnarztId())).thenReturn(true);
        when(behandlungsartRepository.existsById(dto.getBehandlungsartId())).thenReturn(true);
        when(patientRepository.existsById(dto.getPatientId())).thenReturn(true);
        when(repo.findByPatientIdAndDatumBetween(eq(dto.getPatientId()), any(), any()))
                .thenReturn(List.of(existingBefore));
        when(repo.save(any(Termin.class))).thenReturn(testEntity);

        // Should NOT throw because appointments don't overlap
        Termin result = service.createTermin(dto);

        assertThat(result).isNotNull();
    }

    @Test
    void rebookToFlexTermin_whenFlexTerminNotFound_throws() {
        // Test the lambda exception (Line 244)
        String oldTerminId = "old";
        String flexTerminId = "nonexistent";
        String patientId = "patient123";

        Termin oldTermin = new Termin();
        oldTermin.setId(oldTerminId);
        oldTermin.setPatientId(patientId);
        oldTermin.setStatus(TerminStatus.GEBUCHT);
        oldTermin.setBehandlungsartId("behandlung1");

        when(repo.findById(oldTerminId)).thenReturn(Optional.of(oldTermin));
        when(repo.findById(flexTerminId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rebookToFlexTermin(oldTerminId, flexTerminId, patientId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Flex-Termin mit id: nonexistent nicht gefunden");
    }
}

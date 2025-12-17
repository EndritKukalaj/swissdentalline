package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.GesamtBewertungDTO;
import ch.zhaw.swissdentalline.dto.RezensionCreateDTO;
import ch.zhaw.swissdentalline.dto.RezensionModerationDTO;
import ch.zhaw.swissdentalline.model.Rezension;
import ch.zhaw.swissdentalline.repositories.RezensionRepository;
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
class RezensionServiceTest {

    @Mock
    RezensionRepository repo;

    @Mock
    ReviewModerationService reviewModerationService;

    @InjectMocks
    RezensionService service;

    private RezensionCreateDTO testDTO;
    private Rezension testEntity;

    @BeforeEach
    void setUp() {
        testDTO = new RezensionCreateDTO();
        testDTO.setZahnarztId("17981e8ac4cbe09d6b9334bc");
        testDTO.setPatientId("8aae436c3591dd4f961332e8");
        testDTO.setBewertung(4);
        testDTO.setText("Sehr freundlich und kompetent.");
        testDTO.setDatum(Instant.parse("2024-12-15T08:00:00Z"));

        testEntity = new Rezension();
        testEntity.setId("f9b7dcff6c49bcc18bba692e");
        testEntity.setZahnarztId(testDTO.getZahnarztId());
        testEntity.setPatientId(testDTO.getPatientId());
        testEntity.setBewertung(testDTO.getBewertung());
        testEntity.setText(testDTO.getText());
        testEntity.setDatum(testDTO.getDatum());
    }

    @Test
    void shouldCreateRezension_andBeInitiallyNotApproved() {
        // Mock moderation service to return approved=false
        ReviewModerationService.ModerationResult moderationResult = 
                new ReviewModerationService.ModerationResult(false, null);
        when(reviewModerationService.moderateReview(anyString())).thenReturn(moderationResult);
        when(repo.save(any(Rezension.class))).thenReturn(testEntity);

        Rezension result = service.createRezension(testDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isApproved()).isFalse();
    }

    @Test
    void getById_happy() {
        Rezension r = new Rezension();
        r.setId("f9b7dcff6c49bcc18bba692e");
        when(repo.findById("f9b7dcff6c49bcc18bba692e")).thenReturn(Optional.of(r));
        assertThat(service.getRezensionById("f9b7dcff6c49bcc18bba692e")).isPresent();
    }

    @Test
    void getById_empty() {
        when(repo.findById("missing")).thenReturn(Optional.empty());
        assertThat(service.getRezensionById("missing")).isEmpty();
    }

    @Test
    void getAll_happy() {
        Rezension r1 = new Rezension();
        Rezension r2 = new Rezension();
        when(repo.findAll()).thenReturn(List.of(r1, r2));
        assertThat(service.getAllRezensionen()).hasSize(2);
    }

    @Test
    void getAll_empty() {
        when(repo.findAll()).thenReturn(List.of());
        assertThat(service.getAllRezensionen()).isEmpty();
    }

    @Test
    void getRezensionenByZahnarzt_happy() {
        Pageable pageable = PageRequest.of(0, 5);
        Rezension r = new Rezension();
        Page<Rezension> pageWith = new PageImpl<>(List.of(r), pageable, 1);
        when(repo.findByZahnarztId("17981e8ac4cbe09d6b9334bc", pageable)).thenReturn(pageWith);
        assertThat(service.getRezensionenByZahnarzt("17981e8ac4cbe09d6b9334bc", pageable)).hasSize(1);
    }

    @Test
    void getRezensionenByZahnarzt_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Rezension> pageEmpty = new PageImpl<>(List.of(), pageable, 0);
        when(repo.findByZahnarztId("17981e8ac4cbe09d6b9334bc", pageable)).thenReturn(pageEmpty);
        assertThat(service.getRezensionenByZahnarzt("17981e8ac4cbe09d6b9334bc", pageable)).isEmpty();
    }

    @Test
    void getGesamtBewertungById_happy() {
        GesamtBewertungDTO dto = new GesamtBewertungDTO();
        dto.setBewertung(4.5);
        dto.setAnzahl(10L);
        when(repo.getGesamtBewertungById("17981e8ac4cbe09d6b9334bc")).thenReturn(List.of(dto));
        assertThat(service.getGesamtBewertungById("17981e8ac4cbe09d6b9334bc")).isPresent();
    }

    @Test
    void getGesamtBewertungById_empty() {
        when(repo.getGesamtBewertungById("17981e8ac4cbe09d6b9334bc")).thenReturn(List.of());
        assertThat(service.getGesamtBewertungById("17981e8ac4cbe09d6b9334bc")).isEmpty();
    }

    @Test
    void moderateRezension_happy() {
        Rezension existing = new Rezension();
        existing.setId("f9b7dcff6c49bcc18bba692e");
        when(repo.findById("f9b7dcff6c49bcc18bba692e")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        RezensionModerationDTO mod = new RezensionModerationDTO();
        mod.setApproved(true);
        mod.setAiKommentar("ok");

        Rezension res = service.moderateRezension("f9b7dcff6c49bcc18bba692e", mod);
        assertThat(res.isApproved()).isTrue();
        assertThat(res.getAiKommentar()).isEqualTo("ok");
    }

    @Test
    void updateRezension_happy() {
        Rezension existing = new Rezension();
        existing.setId("f9b7dcff6c49bcc18bba692e");
        when(repo.findById("f9b7dcff6c49bcc18bba692e")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        // Mock moderation service
        ReviewModerationService.ModerationResult moderationResult = 
                new ReviewModerationService.ModerationResult(false, null);
        when(reviewModerationService.moderateReview(anyString())).thenReturn(moderationResult);

        RezensionCreateDTO dto = new RezensionCreateDTO();
        dto.setZahnarztId("17981e8ac4cbe09d6b9334bc");
        dto.setPatientId("8aae436c3591dd4f961332e8");
        dto.setBewertung(5);
        dto.setText("Empfehlenswert!");
        dto.setDatum(Instant.parse("2024-12-14T08:00:00Z"));

        Rezension res = service.updateRezension("f9b7dcff6c49bcc18bba692e", dto);
        assertThat(res.getZahnarztId()).isEqualTo("17981e8ac4cbe09d6b9334bc");
        assertThat(res.isApproved()).isFalse();
        assertThat(res.getAiKommentar()).isNull();
    }

    @Test
    void updateRezension_notFound() {
        RezensionCreateDTO dto = new RezensionCreateDTO();
        when(repo.findById("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateRezension("missing", dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rezension mit id: missing nicht gefunden");
    }

    @Test
    void deleteRezension_happy() {
        when(repo.existsById("ok")).thenReturn(true);
        doNothing().when(repo).deleteById("ok");
        service.deleteRezension("ok");
        verify(repo).deleteById("ok");
    }

    @Test
    void deleteRezension_notFound() {
        when(repo.existsById("missing")).thenReturn(false);
        assertThatThrownBy(() -> service.deleteRezension("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rezension mit id: missing nicht gefunden");
    }

    @Test
    void moderateRezension_whenNotFound_throws() {
        RezensionModerationDTO mod = new RezensionModerationDTO();
        mod.setApproved(true);
        mod.setAiKommentar("Looks good");

        when(repo.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.moderateRezension("missing", mod))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rezension mit id: missing nicht gefunden");
    }
}

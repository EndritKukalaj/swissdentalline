package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.GesamtBewertungDTO;
import ch.zhaw.swissdentalline.dto.RezensionCreateDTO;
import ch.zhaw.swissdentalline.dto.RezensionModerationDTO;
import ch.zhaw.swissdentalline.model.Rezension;
import ch.zhaw.swissdentalline.repositories.RezensionRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RezensionService {

    @Autowired
    private RezensionRepository rezensionRepository;
    @Autowired
    private ReviewModerationService reviewModerationService;

    public Rezension createRezension(RezensionCreateDTO createDTO) {
        Rezension rezension = Rezension.fromDTO(createDTO);
        
        // AI-Moderation durchführen
        ReviewModerationService.ModerationResult moderationResult = 
                reviewModerationService.moderateReview(rezension.getText());
        
        rezension.setApproved(moderationResult.isApproved());
        
        if (!moderationResult.isApproved()) {
            rezension.setAiKommentar(moderationResult.getReason());
        }
        
        return rezensionRepository.save(rezension);
    }

    public Optional<Rezension> getRezensionById(String id) {
        return rezensionRepository.findById(id);
    }

    public List<Rezension> getAllRezensionen() {
        return rezensionRepository.findAll();
    }

    public Page<Rezension> getRezensionenByZahnarzt(String zahnarztId, Pageable pageable) {
        return rezensionRepository.findByZahnarztId(zahnarztId, pageable);
    }
    
    public Page<Rezension> getRezensionenByZahnarztApproved(String zahnarztId, boolean approved, Pageable pageable) {
        return rezensionRepository.findByZahnarztIdAndApproved(zahnarztId, approved, pageable);
    }
    
    public Page<Rezension> getRezensionenByPatient(String patientId, Pageable pageable) {
        return rezensionRepository.findByPatientId(patientId, pageable);
    }

    public Optional<GesamtBewertungDTO> getGesamtBewertungById(String id) {
        List<GesamtBewertungDTO> result = rezensionRepository.getGesamtBewertungById(id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public Rezension moderateRezension(String id, RezensionModerationDTO moderationDTO) {
        Rezension rezension = rezensionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rezension mit id: " + id + " nicht gefunden"));

        rezension.setApproved(moderationDTO.isApproved());
        rezension.setAiKommentar(moderationDTO.getAiKommentar());

        return rezensionRepository.save(rezension);
    }

    public Rezension updateRezension(String id, RezensionCreateDTO updateDTO) {
        Rezension existing = rezensionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rezension mit id: " + id + " nicht gefunden"));

        existing.setZahnarztId(updateDTO.getZahnarztId());
        existing.setPatientId(updateDTO.getPatientId());
        existing.setBewertung(updateDTO.getBewertung());
        existing.setText(updateDTO.getText());
        existing.setDatum(updateDTO.getDatum());
        
        // AI-Moderation bei Update durchführen
        ReviewModerationService.ModerationResult moderationResult = 
                reviewModerationService.moderateReview(updateDTO.getText());
        
        existing.setApproved(moderationResult.isApproved());
        existing.setAiKommentar(moderationResult.isApproved() ? null : moderationResult.getReason());

        return rezensionRepository.save(existing);
    }

    public void deleteRezension(String id) {
        if (!rezensionRepository.existsById(id)) {
            throw new IllegalArgumentException("Rezension mit id: " + id + " nicht gefunden");
        }
        rezensionRepository.deleteById(id);
    }
}

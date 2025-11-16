package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.mapper.ZahnarztMapper;
import ch.zhaw.swissdentalline.model.Adresse;
import ch.zhaw.swissdentalline.model.AdressTyp;
import ch.zhaw.swissdentalline.model.Zahnarzt;
import ch.zhaw.swissdentalline.repositories.AdresseRepository;
import ch.zhaw.swissdentalline.repositories.ZahnarztRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZahnarztService {

    private final ZahnarztRepository zahnarztRepository;
    private final ZahnarztMapper zahnarztMapper;
    private final AdresseRepository adresseRepository;

    public Zahnarzt createZahnarzt(ZahnarztCreateDTO createDTO) {
        // Validate that PraxisAdresseId exists and is of type PRAXIS
        Adresse praxisAdresse = adresseRepository.findById(createDTO.getPraxisAdresseId())
                .orElseThrow(() -> new IllegalArgumentException("Praxis-Adresse mit id: " + createDTO.getPraxisAdresseId() + " nicht gefunden"));
        
        if (praxisAdresse.getTyp() != AdressTyp.PRAXIS) {
            throw new IllegalArgumentException("Adresse mit id: " + createDTO.getPraxisAdresseId() + " ist keine Praxis-Adresse");
        }

        // Check for duplicate Zahnarzt (same name and praxis)
        if (zahnarztRepository.findByNameAndPraxisAdresseId(
                createDTO.getName(), createDTO.getPraxisAdresseId()).isPresent()) {
                throw new IllegalArgumentException("Zahnarzt mit gleichem Namen in dieser Praxis existiert bereits");
        }

        Zahnarzt zahnarzt = zahnarztMapper.toEntity(createDTO);
        return zahnarztRepository.save(zahnarzt);
    }

    public Optional<Zahnarzt> getZahnarztById(String id) {
        return zahnarztRepository.findById(id);
    }

    public List<Zahnarzt> getAllZahnaerzte() {
        return zahnarztRepository.findAll();
    }

    public List<Zahnarzt> findByPraxisAdresse(String praxisAdresseId) {
        return zahnarztRepository.findByPraxisAdresseId(praxisAdresseId);
    }

    public List<Zahnarzt> findByName(String name) {
        return zahnarztRepository.findByName(name);
    }

    public Zahnarzt updateZahnarzt(String id, ZahnarztCreateDTO updateDTO) {
        Zahnarzt existing = zahnarztRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Zahnarzt mit id: " + id + " nicht gefunden"));

        // Validate PraxisAdresse
        Adresse praxisAdresse = adresseRepository.findById(updateDTO.getPraxisAdresseId())
                .orElseThrow(() -> new IllegalArgumentException("Praxis-Adresse mit id: " + updateDTO.getPraxisAdresseId() + " nicht gefunden"));
        
        if (praxisAdresse.getTyp() != AdressTyp.PRAXIS) {
            throw new IllegalArgumentException("Adresse mit id: " + updateDTO.getPraxisAdresseId() + " ist keine Praxis-Adresse");
        }

        // Check for duplicate
        Optional<Zahnarzt> duplicate = zahnarztRepository.findByNameAndPraxisAdresseId(
                updateDTO.getName(), updateDTO.getPraxisAdresseId());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new IllegalArgumentException("Zahnarzt mit gleichem Namen in dieser Praxis existiert bereits");
        }

        existing.setName(updateDTO.getName());
        existing.setPraxisAdresseId(updateDTO.getPraxisAdresseId());

        return zahnarztRepository.save(existing);
    }

    public void deleteZahnarzt(String id) {
        if (!zahnarztRepository.existsById(id)) {
            throw new IllegalArgumentException("Zahnarzt mit id: " + id + " nicht gefunden");
        }
        zahnarztRepository.deleteById(id);
    }
}

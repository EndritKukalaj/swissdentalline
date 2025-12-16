package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.dto.ZahnarztProfilDTO;
import ch.zhaw.swissdentalline.model.Adresse;
import ch.zhaw.swissdentalline.model.AdressTyp;
import ch.zhaw.swissdentalline.model.Zahnarzt;
import ch.zhaw.swissdentalline.repositories.AdresseRepository;
import ch.zhaw.swissdentalline.repositories.ZahnarztRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZahnarztService {

    @Autowired
    private ZahnarztRepository zahnarztRepository;
    @Autowired
    private AdresseRepository adresseRepository;

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

        Zahnarzt zahnarzt = new Zahnarzt(createDTO);
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

    public ZahnarztProfilDTO getProfilByName(String name, String email, String role) {
        List<Zahnarzt> zahnaerzte = zahnarztRepository.findByName("Dr. " + name);
        
        if (zahnaerzte.isEmpty()) {
            // Zahnarzt existiert noch nicht in Collection - gebe minimales Profil zurück
            ZahnarztProfilDTO dto = new ZahnarztProfilDTO();
            dto.setName(name);
            dto.setEmail(email);
            dto.setRole(role);
            return dto;
        }
        
        // Nehme ersten Treffer (bei mehreren Zahnärzten mit gleichem Namen)
        Zahnarzt zahnarzt = zahnaerzte.get(0);
        
        // Lade Praxisadresse
        String praxisname = null;
        String praxisadresseFormatiert = null;
        
        if (zahnarzt.getPraxisAdresseId() != null) {
            Optional<Adresse> praxisAdresse = adresseRepository.findById(zahnarzt.getPraxisAdresseId());
            if (praxisAdresse.isPresent()) {
                Adresse adr = praxisAdresse.get();
                praxisname = adr.getBezeichnung(); 
                praxisadresseFormatiert = String.format("%s, %s %s", adr.getStrasse(), adr.getPlz(), adr.getOrt());
            }
        }
        
        ZahnarztProfilDTO dto = new ZahnarztProfilDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setRole(role);
        dto.setPraxisname(praxisname);
        dto.setPraxisadresse(praxisadresseFormatiert);
        
        return dto;
    }
}

package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
import ch.zhaw.swissdentalline.model.Adresse;
import ch.zhaw.swissdentalline.model.AdressTyp;
import ch.zhaw.swissdentalline.repositories.AdresseRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdresseService {

    @Autowired
    private AdresseRepository adresseRepository;

    public Adresse createAdresse(AdresseCreateDTO createDTO) {
        // Check for duplicate Praxis Bezeichnung
        if (createDTO.getTyp() == AdressTyp.PRAXIS 
                && createDTO.getBezeichnung() != null 
                && adresseRepository.findByBezeichnung(createDTO.getBezeichnung()).isPresent()) {
            throw new IllegalArgumentException("Praxis mit Bezeichnung '" + createDTO.getBezeichnung() + "' existiert bereits");
        }
        Adresse adresse = Adresse.fromDTO(createDTO);
        return adresseRepository.save(adresse);
    }

    public Optional<Adresse> getAdresseById(String id) {
        return adresseRepository.findById(id);
    }

    public List<Adresse> getAllAdressen() {
        return adresseRepository.findAll();
    }

    public Page<Adresse> getAdressenByType(AdressTyp typ, Pageable pageable) {
        return adresseRepository.findByTyp(typ, pageable);
    }

    public Adresse updateAdresse(String id, AdresseCreateDTO updateDTO) {
        Adresse existingAdresse = adresseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Adresse mit id: " + id + " nicht gefunden"));

        // Check for duplicate Praxis Bezeichnung on update
        if (updateDTO.getTyp() == AdressTyp.PRAXIS 
                && updateDTO.getBezeichnung() != null 
                && !updateDTO.getBezeichnung().equals(existingAdresse.getBezeichnung())
                && adresseRepository.findByBezeichnung(updateDTO.getBezeichnung()).isPresent()) {
            throw new IllegalArgumentException("Praxis mit Bezeichnung '" + updateDTO.getBezeichnung() + "' existiert bereits");
        }

        existingAdresse.setStrasse(updateDTO.getStrasse());
        existingAdresse.setPlz(updateDTO.getPlz());
        existingAdresse.setOrt(updateDTO.getOrt());
        existingAdresse.setTyp(updateDTO.getTyp());
        existingAdresse.setBezeichnung(updateDTO.getBezeichnung());

        return adresseRepository.save(existingAdresse);
    }

    public void deleteAdresse(String id) {
        if (!adresseRepository.existsById(id)) {
            throw new IllegalArgumentException("Adresse mit id: " + id + " nicht gefunden");
        }
        adresseRepository.deleteById(id);
    }
}

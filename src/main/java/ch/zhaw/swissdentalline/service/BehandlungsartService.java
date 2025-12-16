package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.BehandlungsartCreateDTO;
import ch.zhaw.swissdentalline.model.Behandlungsart;
import ch.zhaw.swissdentalline.repositories.BehandlungsartRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BehandlungsartService {

    @Autowired
    private BehandlungsartRepository behandlungsartRepository;

    public Behandlungsart createBehandlungsart(BehandlungsartCreateDTO createDTO) {
        // Check for duplicate name
        if (behandlungsartRepository.findByName(createDTO.getName()).isPresent()) {
              throw new IllegalArgumentException("Behandlungsart mit Name '" + createDTO.getName() + "' existiert bereits");
        }
        Behandlungsart behandlungsart = Behandlungsart.fromDTO(createDTO);
        return behandlungsartRepository.save(behandlungsart);
    }

    public Optional<Behandlungsart> getBehandlungsartById(String id) {
        return behandlungsartRepository.findById(id);
    }

    public List<Behandlungsart> getAllBehandlungsarten() {
        return behandlungsartRepository.findAll();
    }

    public Behandlungsart updateBehandlungsart(String id, BehandlungsartCreateDTO updateDTO) {
        Behandlungsart existing = behandlungsartRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Behandlungsart mit id: " + id + " nicht gefunden"));

        // Check if name change causes duplicate
        if (!existing.getName().equals(updateDTO.getName())) {
            if (behandlungsartRepository.findByName(updateDTO.getName()).isPresent()) {
                    throw new IllegalArgumentException("Behandlungsart mit Name '" + updateDTO.getName() + "' existiert bereits");
            }
        }

        existing.setName(updateDTO.getName());
        existing.setBeschreibung(updateDTO.getBeschreibung());

        return behandlungsartRepository.save(existing);
    }

    public void deleteBehandlungsart(String id) {
        if (!behandlungsartRepository.existsById(id)) {
            throw new IllegalArgumentException("Behandlungsart mit id: " + id + " nicht gefunden");
        }
        behandlungsartRepository.deleteById(id);
    }
}

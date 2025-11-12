package ch.zhaw.swissdentalline.service;

import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.mapper.ZahnarztMapper;
import ch.zhaw.swissdentalline.model.Zahnarzt;
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

    public Zahnarzt createZahnarzt(ZahnarztCreateDTO createDTO) {
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

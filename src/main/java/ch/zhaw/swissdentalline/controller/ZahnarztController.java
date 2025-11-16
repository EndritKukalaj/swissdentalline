package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.dto.ZahnarztCreateDTO;
import ch.zhaw.swissdentalline.model.Zahnarzt;
import ch.zhaw.swissdentalline.service.ZahnarztService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ZahnarztController {

    @Autowired
    ZahnarztService zahnarztService;

    @PostMapping("/zahnaerzte")
    public ResponseEntity<Zahnarzt> createZahnarzt(@Valid @RequestBody ZahnarztCreateDTO zahnarztDTO) {
        Zahnarzt created = zahnarztService.createZahnarzt(zahnarztDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/zahnaerzte/{id}")
    public ResponseEntity<Zahnarzt> getZahnarztById(@PathVariable String id) {
        Optional<Zahnarzt> zahnarzt = zahnarztService.getZahnarztById(id);
        if (zahnarzt.isPresent()) {
            return new ResponseEntity<>(zahnarzt.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/zahnaerzte")
    public ResponseEntity<List<Zahnarzt>> getAllZahnaerzte() {
        List<Zahnarzt> zahnaerzte = zahnarztService.getAllZahnaerzte();
        return new ResponseEntity<>(zahnaerzte, HttpStatus.OK);
    }

    @GetMapping("/zahnaerzte/praxis/{praxisAdresseId}")
    public ResponseEntity<List<Zahnarzt>> findZahnaerzteByPraxisAdresse(@PathVariable String praxisAdresseId) {
        List<Zahnarzt> zahnaerzte = zahnarztService.findByPraxisAdresse(praxisAdresseId);
        return new ResponseEntity<>(zahnaerzte, HttpStatus.OK);
    }

    @GetMapping("/zahnaerzte/name/{name}")
    public ResponseEntity<List<Zahnarzt>> findZahnaerzteByName(@PathVariable String name) {
        List<Zahnarzt> zahnaerzte = zahnarztService.findByName(name);
        return new ResponseEntity<>(zahnaerzte, HttpStatus.OK);
    }

    @PutMapping("/zahnaerzte/{id}")
    public ResponseEntity<Zahnarzt> updateZahnarzt(
            @PathVariable String id,
            @Valid @RequestBody ZahnarztCreateDTO zahnarztDTO) {
        try {
            Zahnarzt updated = zahnarztService.updateZahnarzt(id, zahnarztDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/zahnaerzte/{id}")
    public ResponseEntity<Void> deleteZahnarzt(@PathVariable String id) {
        try {
            zahnarztService.deleteZahnarzt(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

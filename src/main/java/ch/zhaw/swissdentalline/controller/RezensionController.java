package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.dto.GesamtBewertungDTO;
import ch.zhaw.swissdentalline.dto.RezensionCreateDTO;
import ch.zhaw.swissdentalline.dto.RezensionModerationDTO;
import ch.zhaw.swissdentalline.model.Rezension;
import ch.zhaw.swissdentalline.service.RezensionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RezensionController {

    @Autowired
    RezensionService rezensionService;

    @PostMapping("/rezensionen")
    public ResponseEntity<Rezension> createRezension(@Valid @RequestBody RezensionCreateDTO rezensionDTO) {
        Rezension created = rezensionService.createRezension(rezensionDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/rezensionen/{id}")
    public ResponseEntity<Rezension> getRezensionById(@PathVariable String id) {
        Optional<Rezension> rezension = rezensionService.getRezensionById(id);
        if (rezension.isPresent()) {
            return new ResponseEntity<>(rezension.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/rezensionen")
    public ResponseEntity<List<Rezension>> getAllRezensionen() {
        List<Rezension> rezensionen = rezensionService.getAllRezensionen();
        return new ResponseEntity<>(rezensionen, HttpStatus.OK);
    }

    @GetMapping("/rezensionen/zahnarzt/{zahnarztId}")
    public ResponseEntity<Page<Rezension>> getRezensionenByZahnarzt(
            @PathVariable String zahnarztId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Rezension> rezensionen = rezensionService.getRezensionenByZahnarzt(zahnarztId, pageable);
        return new ResponseEntity<>(rezensionen, HttpStatus.OK);
    }

    @GetMapping("/rezensionen/zahnarzt/{zahnarztId}/bewertung")
    public ResponseEntity<GesamtBewertungDTO> getGesamtBewertung(@PathVariable String zahnarztId) {
        Optional<GesamtBewertungDTO> bewertung = rezensionService.getGesamtBewertungById(zahnarztId);
        if (bewertung.isPresent()) {
            return new ResponseEntity<>(bewertung.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/rezensionen/{id}")
    public ResponseEntity<Rezension> updateRezension(
            @PathVariable String id,
            @Valid @RequestBody RezensionCreateDTO rezensionDTO) {
        try {
            Rezension updated = rezensionService.updateRezension(id, rezensionDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/rezensionen/{id}/moderieren")
    public ResponseEntity<Rezension> moderateRezension(
            @PathVariable String id,
            @Valid @RequestBody RezensionModerationDTO moderationDTO) {
        try {
            Rezension moderated = rezensionService.moderateRezension(id, moderationDTO);
            return new ResponseEntity<>(moderated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/rezensionen/{id}")
    public ResponseEntity<Void> deleteRezension(@PathVariable String id) {
        try {
            rezensionService.deleteRezension(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.dto.BehandlungsartCreateDTO;
import ch.zhaw.swissdentalline.model.Behandlungsart;
import ch.zhaw.swissdentalline.service.BehandlungsartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BehandlungsartController {

    @Autowired
    BehandlungsartService behandlungsartService;

    @PostMapping("/behandlungsarten")
    public ResponseEntity<Behandlungsart> createBehandlungsart(@Valid @RequestBody BehandlungsartCreateDTO behandlungsartDTO) {
        Behandlungsart created = behandlungsartService.createBehandlungsart(behandlungsartDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/behandlungsarten/{id}")
    public ResponseEntity<Behandlungsart> getBehandlungsartById(@PathVariable String id) {
        Optional<Behandlungsart> behandlungsart = behandlungsartService.getBehandlungsartById(id);
        if (behandlungsart.isPresent()) {
            return new ResponseEntity<>(behandlungsart.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/behandlungsarten")
    public ResponseEntity<List<Behandlungsart>> getAllBehandlungsarten() {
        List<Behandlungsart> behandlungsarten = behandlungsartService.getAllBehandlungsarten();
        return new ResponseEntity<>(behandlungsarten, HttpStatus.OK);
    }

    @PutMapping("/behandlungsarten/{id}")
    public ResponseEntity<Behandlungsart> updateBehandlungsart(
            @PathVariable String id,
            @Valid @RequestBody BehandlungsartCreateDTO behandlungsartDTO) {
        try {
            Behandlungsart updated = behandlungsartService.updateBehandlungsart(id, behandlungsartDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/behandlungsarten/{id}")
    public ResponseEntity<Void> deleteBehandlungsart(@PathVariable String id) {
        try {
            behandlungsartService.deleteBehandlungsart(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

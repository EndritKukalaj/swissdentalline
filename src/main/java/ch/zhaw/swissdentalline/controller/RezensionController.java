package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.constants.UserRoles;
import ch.zhaw.swissdentalline.dto.GesamtBewertungDTO;
import ch.zhaw.swissdentalline.dto.RezensionCreateDTO;
import ch.zhaw.swissdentalline.model.Rezension;
import ch.zhaw.swissdentalline.service.RezensionService;
import ch.zhaw.swissdentalline.service.UserService;
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

    @Autowired
    UserService userService;

    @PostMapping("/rezensionen")
    public ResponseEntity<Rezension> createRezension(@Valid @RequestBody RezensionCreateDTO rezensionDTO) {
        if (!userService.userHasRole(UserRoles.PATIENT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Rezension created = rezensionService.createRezension(rezensionDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/rezensionen/{id}")
    public ResponseEntity<Rezension> getRezensionById(@PathVariable String id) {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Rezension> rezension = rezensionService.getRezensionById(id);
        if (rezension.isPresent()) {
            return new ResponseEntity<>(rezension.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/rezensionen")
    public ResponseEntity<List<Rezension>> getAllRezensionen() {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Rezension> rezensionen = rezensionService.getAllRezensionen();
        return new ResponseEntity<>(rezensionen, HttpStatus.OK);
    }

    @GetMapping("/rezensionen/zahnarzt/{zahnarztId}")
    public ResponseEntity<Page<Rezension>> getRezensionenByZahnarzt(
            @PathVariable String zahnarztId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean approved) {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Rezension> rezensionen;
        
        if (approved != null) {
            rezensionen = rezensionService.getRezensionenByZahnarztApproved(zahnarztId, approved, pageable);
        } else {
            rezensionen = rezensionService.getRezensionenByZahnarzt(zahnarztId, pageable);
        }
        
        return new ResponseEntity<>(rezensionen, HttpStatus.OK);
    }
    
    @GetMapping("/rezensionen/patient/{patientId}")
    public ResponseEntity<Page<Rezension>> getRezensionenByPatient(
            @PathVariable String patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!userService.userHasRole(UserRoles.PATIENT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Rezension> rezensionen = rezensionService.getRezensionenByPatient(patientId, pageable);
        return new ResponseEntity<>(rezensionen, HttpStatus.OK);
    }

    @GetMapping("/rezensionen/zahnarzt/{zahnarztId}/bewertung")
    public ResponseEntity<GesamtBewertungDTO> getGesamtBewertung(@PathVariable String zahnarztId) {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
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
        if (!userService.userHasRole(UserRoles.PATIENT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Rezension updated = rezensionService.updateRezension(id, rezensionDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/rezensionen/{id}")
    public ResponseEntity<Void> deleteRezension(@PathVariable String id) {
        if (!userService.userHasRole(UserRoles.PATIENT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            rezensionService.deleteRezension(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

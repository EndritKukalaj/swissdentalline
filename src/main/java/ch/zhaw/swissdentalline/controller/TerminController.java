package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.dto.TerminCreateDTO;
import ch.zhaw.swissdentalline.model.Termin;
import ch.zhaw.swissdentalline.model.TerminStatus;
import ch.zhaw.swissdentalline.service.TerminService;
import ch.zhaw.swissdentalline.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TerminController {

    @Autowired
    TerminService terminService;

    @Autowired
    UserService userService;

    @PostMapping("/termine")
    public ResponseEntity<Termin> createTermin(@Valid @RequestBody TerminCreateDTO terminDTO) {
        if (!userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Termin created = terminService.createTermin(terminDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/termine/{id}")
    public ResponseEntity<Termin> getTerminById(@PathVariable String id) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Termin> termin = terminService.getTerminById(id);
        if (termin.isPresent()) {
            return new ResponseEntity<>(termin.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/termine")
    public ResponseEntity<List<Termin>> getAllTermine() {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Termin> termine = terminService.getAllTermine();
        return new ResponseEntity<>(termine, HttpStatus.OK);
    }

    @GetMapping("/termine/status/{status}")
    public ResponseEntity<Page<Termin>> findTermineByStatusAndDateRange(
            @PathVariable TerminStatus status,
            @RequestParam Instant startDatum,
            @RequestParam Instant endDatum,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Termin> termine = terminService.findTermineByStatusAndDateRange(status, startDatum, endDatum, pageable);
        return new ResponseEntity<>(termine, HttpStatus.OK);
    }

    @PutMapping("/termine/{id}")
    public ResponseEntity<Termin> updateTermin(
            @PathVariable String id,
            @Valid @RequestBody TerminCreateDTO terminDTO) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Termin updated = terminService.updateTermin(id, terminDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/termine/{id}/buchen")
    public ResponseEntity<Termin> bookTermin(
            @PathVariable String id,
            @RequestParam boolean isFlex,
            @RequestParam String patientId) {
        if (!userService.userHasRole("Patient")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Termin booked = terminService.bookTermin(id, isFlex, patientId);
            return new ResponseEntity<>(booked, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/termine/{id}/abbrechen")
    public ResponseEntity<Termin> cancelTermin(@PathVariable String id) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Termin cancelled = terminService.cancelTermin(id);
            return new ResponseEntity<>(cancelled, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/termine/{id}/freigeben")
    public ResponseEntity<Termin> releaseTermin(@PathVariable String id) {
        if (!userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Termin released = terminService.releaseFlexTermin(id);
            return new ResponseEntity<>(released, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/termine/{id}/abschliessen")
    public ResponseEntity<Termin> completeTermin(@PathVariable String id) {
        if (!userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Termin completed = terminService.completeTermin(id);
            return new ResponseEntity<>(completed, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/termine/{id}")
    public ResponseEntity<Void> deleteTermin(@PathVariable String id) {
        if (!userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            terminService.deleteTermin(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/termine/patient/{patientId}/flex")
    public ResponseEntity<List<Termin>> getRelevantFlexTermineForPatient(@PathVariable String patientId) {
        if (!userService.userHasRole("Patient")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Termin> flexTermine = terminService.findRelevantFlexTermineForPatient(patientId);
        return new ResponseEntity<>(flexTermine, HttpStatus.OK);
    }

    @PutMapping("/termine/{oldTerminId}/umbuchen/{flexTerminId}")
    public ResponseEntity<Termin> rebookToFlexTermin(
            @PathVariable String oldTerminId,
            @PathVariable String flexTerminId,
            @RequestParam String patientId) {
        if (!userService.userHasRole("Patient")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Termin rebooked = terminService.rebookToFlexTermin(oldTerminId, flexTerminId, patientId);
            return new ResponseEntity<>(rebooked, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.constants.UserRoles;
import ch.zhaw.swissdentalline.dto.PatientCreateDTO;
import ch.zhaw.swissdentalline.dto.PatientProfilDTO;
import ch.zhaw.swissdentalline.model.Patient;
import ch.zhaw.swissdentalline.service.PatientService;
import ch.zhaw.swissdentalline.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PatientController {

    @Autowired
    PatientService patientService;

    @Autowired
    UserService userService;

    @PostMapping("/patienten")
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientCreateDTO patientDTO) {
        if (!userService.userHasRole(UserRoles.PATIENT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Patient created = patientService.createPatient(patientDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/patienten/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isPresent()) {
            return new ResponseEntity<>(patient.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/patienten")
    public ResponseEntity<List<Patient>> getAllPatients() {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Patient> patienten = patientService.getAllPatients();
        return new ResponseEntity<>(patienten, HttpStatus.OK);
    }

    @GetMapping("/patienten/name/{name}")
    public ResponseEntity<List<Patient>> findPatientsByName(@PathVariable String name) {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Patient> patienten = patientService.findByName(name);
        return new ResponseEntity<>(patienten, HttpStatus.OK);
    }

    @PutMapping("/patienten/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable String id,
            @Valid @RequestBody PatientCreateDTO patientDTO) {
        if (!userService.userHasRole(UserRoles.PATIENT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Patient updated = patientService.updatePatient(id, patientDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/patienten/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        if (!userService.userHasRole(UserRoles.PATIENT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            patientService.deletePatient(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/patienten/profil")
    public ResponseEntity<PatientProfilDTO> getProfil(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String role) {
        if (!userService.userHasRole(UserRoles.PATIENT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        PatientProfilDTO profil = patientService.getProfilByName(name, email, role);
        return new ResponseEntity<>(profil, HttpStatus.OK);
    }
}

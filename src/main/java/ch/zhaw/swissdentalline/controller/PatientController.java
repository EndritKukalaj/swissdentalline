package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.dto.PatientCreateDTO;
import ch.zhaw.swissdentalline.model.Patient;
import ch.zhaw.swissdentalline.service.PatientService;
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

    @PostMapping("/patienten")
    public ResponseEntity<Patient> createPatient(@RequestBody PatientCreateDTO patientDTO) {
        Patient created = patientService.createPatient(patientDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/patienten/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isPresent()) {
            return new ResponseEntity<>(patient.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/patienten")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patienten = patientService.getAllPatients();
        return new ResponseEntity<>(patienten, HttpStatus.OK);
    }

    @GetMapping("/patienten/name/{name}")
    public ResponseEntity<List<Patient>> findPatientsByName(@PathVariable String name) {
        List<Patient> patienten = patientService.findByName(name);
        return new ResponseEntity<>(patienten, HttpStatus.OK);
    }

    @PutMapping("/patienten/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable String id,
            @RequestBody PatientCreateDTO patientDTO) {
        try {
            Patient updated = patientService.updatePatient(id, patientDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/patienten/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        try {
            patientService.deletePatient(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

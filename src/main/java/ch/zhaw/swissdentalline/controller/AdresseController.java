package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.dto.AdresseCreateDTO;
import ch.zhaw.swissdentalline.dto.AdresseKompaktDTO;
import ch.zhaw.swissdentalline.model.Adresse;
import ch.zhaw.swissdentalline.model.AdressTyp;
import ch.zhaw.swissdentalline.service.AdresseService;
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
public class AdresseController {

    @Autowired
    AdresseService adresseService;

    @Autowired
    UserService userService;

    @PostMapping("/adressen")
    public ResponseEntity<Adresse> createAdresse(@Valid @RequestBody AdresseCreateDTO adresseDTO) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Adresse created = adresseService.createAdresse(adresseDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/adressen/{id}")
    public ResponseEntity<Adresse> getAdresseById(@PathVariable String id) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Adresse> adresse = adresseService.getAdresseById(id);
        if (adresse.isPresent()) {
            return new ResponseEntity<>(adresse.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/adressen")
    public ResponseEntity<List<Adresse>> getAllAdressen() {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Adresse> adressen = adresseService.getAllAdressen();
        return new ResponseEntity<>(adressen, HttpStatus.OK);
    }

    @GetMapping("/adressen/typ/{typ}")
    public ResponseEntity<Page<Adresse>> getAdressenByType(
            @PathVariable AdressTyp typ,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Adresse> adressen = adresseService.getAdressenByType(typ, pageable);
        return new ResponseEntity<>(adressen, HttpStatus.OK);
    }

    @GetMapping("/adressen/{id}/kompakt")
    public ResponseEntity<AdresseKompaktDTO> getAdresseKompakt(@PathVariable String id) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            AdresseKompaktDTO kompakt = adresseService.getAdresseKompakt(id);
            return new ResponseEntity<>(kompakt, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/adressen/{id}")
    public ResponseEntity<Adresse> updateAdresse(
            @PathVariable String id,
            @Valid @RequestBody AdresseCreateDTO adresseDTO) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Adresse updated = adresseService.updateAdresse(id, adresseDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/adressen/{id}")
    public ResponseEntity<Void> deleteAdresse(@PathVariable String id) {
        if (!userService.userHasRole("Patient") && !userService.userHasRole("Zahnarzt")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            adresseService.deleteAdresse(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

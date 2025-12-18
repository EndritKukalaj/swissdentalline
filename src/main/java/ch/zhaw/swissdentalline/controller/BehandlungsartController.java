package ch.zhaw.swissdentalline.controller;

import ch.zhaw.swissdentalline.constants.UserRoles;
import ch.zhaw.swissdentalline.dto.BehandlungsartCreateDTO;
import ch.zhaw.swissdentalline.model.Behandlungsart;
import ch.zhaw.swissdentalline.service.BehandlungsartService;
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
public class BehandlungsartController {

    @Autowired
    BehandlungsartService behandlungsartService;

    @Autowired
    UserService userService;

    @PostMapping("/behandlungsarten")
    public ResponseEntity<Behandlungsart> createBehandlungsart(@Valid @RequestBody BehandlungsartCreateDTO behandlungsartDTO) {
        if (!userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Behandlungsart created = behandlungsartService.createBehandlungsart(behandlungsartDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/behandlungsarten/{id}")
    public ResponseEntity<Behandlungsart> getBehandlungsartById(@PathVariable String id) {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Optional<Behandlungsart> behandlungsart = behandlungsartService.getBehandlungsartById(id);
        if (behandlungsart.isPresent()) {
            return new ResponseEntity<>(behandlungsart.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/behandlungsarten")
    public ResponseEntity<List<Behandlungsart>> getAllBehandlungsarten() {
        if (!userService.userHasRole(UserRoles.PATIENT) && !userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<Behandlungsart> behandlungsarten = behandlungsartService.getAllBehandlungsarten();
        return new ResponseEntity<>(behandlungsarten, HttpStatus.OK);
    }

    @PutMapping("/behandlungsarten/{id}")
    public ResponseEntity<Behandlungsart> updateBehandlungsart(
            @PathVariable String id,
            @Valid @RequestBody BehandlungsartCreateDTO behandlungsartDTO) {
        if (!userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            Behandlungsart updated = behandlungsartService.updateBehandlungsart(id, behandlungsartDTO);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/behandlungsarten/{id}")
    public ResponseEntity<Void> deleteBehandlungsart(@PathVariable String id) {
        if (!userService.userHasRole(UserRoles.ZAHNARZT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            behandlungsartService.deleteBehandlungsart(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

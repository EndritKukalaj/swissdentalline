package ch.zhaw.swissdentalline;

import ch.zhaw.swissdentalline.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestDataCleanup {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ZahnarztRepository zahnarztRepository;

    @Autowired
    private TerminRepository terminRepository;

    @Autowired
    private AdresseRepository adresseRepository;

    @Autowired
    private BehandlungsartRepository behandlungsartRepository;

    @Autowired
    private RezensionRepository rezensionRepository;

    public void cleanupAllData() {
        // Delete in correct order to respect dependencies
        rezensionRepository.deleteAll();
        terminRepository.deleteAll();
        patientRepository.deleteAll();
        zahnarztRepository.deleteAll();
        behandlungsartRepository.deleteAll();
        adresseRepository.deleteAll();
    }
}

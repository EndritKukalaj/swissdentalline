package ch.zhaw.swissdentalline.constants;

/**
 * Konstanten für Benutzerrollen im SwissDentalLine System.
 * Diese Klasse definiert alle verfügbaren Benutzerrollen als Konstanten,
 * um String-Duplikation zu vermeiden und Wartbarkeit zu verbessern.
 */
public final class UserRoles {
    
    /**
     * Rolle für Patienten im System.
     */
    public static final String PATIENT = "Patient";
    
    /**
     * Rolle für Zahnärzte im System.
     */
    public static final String ZAHNARZT = "Zahnarzt";
    
    // Private Konstruktor verhindert Instanziierung dieser Utility-Klasse
    private UserRoles() {
        throw new IllegalStateException("Utility class");
    }
}

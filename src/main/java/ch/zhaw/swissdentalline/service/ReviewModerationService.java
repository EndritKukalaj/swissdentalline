package ch.zhaw.swissdentalline.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewModerationService {

    @Autowired
    private ChatClient chatClient;

    /**
     * Prüft eine Rezension auf problematische Inhalte
     * @param reviewText Der zu prüfende Rezensionstext
     * @return ModerationResult mit Entscheidung und detaillierter Begründung
     */
    public ModerationResult moderateReview(String reviewText) {
        String prompt = """
                Du bist ein Moderator für Zahnarztbewertungen. Prüfe den folgenden Rezensionstext auf:
                1. Beleidigungen oder Beschimpfungen gegen Personen
                2. Diskriminierende Äußerungen (Geschlecht, Herkunft, Religion, etc.)
                3. Spam oder irrelevante Werbung
                4. Hassrede oder bedrohliche Inhalte
                
                Antworte im folgenden Format:
                - Wenn akzeptabel: "APPROVED"
                - Wenn nicht akzeptabel: "REJECTED: [1-2 Sätze mit konkreter Begründung, was genau problematisch ist und warum]"
                
                Sei spezifisch und beziehe dich auf den konkreten Text. Bei Beleidigungen nenne die problematischen Formulierungen.
                
                Text: "%s"
                """.formatted(reviewText);

        try {
            String response = chatClient.prompt(prompt)
                    .call()
                    .content();

            response = response.trim();
            
            if (response.toUpperCase().startsWith("APPROVED")) {
                return new ModerationResult(true, null);
            } else if (response.toUpperCase().startsWith("REJECTED:")) {
                String reason = response.substring(response.indexOf(":") + 1).trim();
                return new ModerationResult(false, reason);
            } else {
                // Fallback: Bei unklarer Antwort -> manuelle Prüfung erforderlich
                return new ModerationResult(false, "Die AI-Moderation konnte keine eindeutige Entscheidung treffen. Bitte überprüfen Sie den Inhalt manuell.");
            }
        } catch (Exception e) {
            // Bei Fehler: manuelle Prüfung erforderlich
            return new ModerationResult(false, "Technischer Fehler bei der AI-Moderation: " + e.getMessage());
        }
    }

    /**
     * Ergebnis der Moderation
     */
    public static class ModerationResult {
        private final boolean approved;
        private final String reason;

        public ModerationResult(boolean approved, String reason) {
            this.approved = approved;
            this.reason = reason;
        }

        public boolean isApproved() {
            return approved;
        }

        public String getReason() {
            return reason;
        }
    }
}

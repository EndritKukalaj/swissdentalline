package ch.zhaw.swissdentalline.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ReviewModerationServiceTest {

    private ChatClient chatClient;
    private ReviewModerationService service;
    private ChatClient.ChatClientRequestSpec requestSpec;
    private CallResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        chatClient = mock(ChatClient.class);
        requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        responseSpec = mock(CallResponseSpec.class);
        service = new ReviewModerationService();
        
        // Use reflection to inject the mock
        try {
            var field = ReviewModerationService.class.getDeclaredField("chatClient");
            field.setAccessible(true);
            field.set(service, chatClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        // Setup default mock chain
        when(chatClient.prompt(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
    }

    @Test
    void moderateReview_whenTextIsApproved_returnsApprovedResult() {
        String reviewText = "Great service, very professional!";
        when(responseSpec.content()).thenReturn("APPROVED");

        ReviewModerationService.ModerationResult result = service.moderateReview(reviewText);

        assertThat(result).isNotNull();
        assertThat(result.isApproved()).isTrue();
        assertThat(result.getReason()).isNull();
    }

    @Test
    void moderateReview_whenTextIsRejected_returnsRejectedResultWithReason() {
        String reviewText = "This dentist is terrible and stupid!";
        String expectedReason = "Der Text enthält Beleidigungen wie 'stupid'. Solche persönlichen Angriffe sind nicht akzeptabel.";
        when(responseSpec.content()).thenReturn("REJECTED: " + expectedReason);

        ReviewModerationService.ModerationResult result = service.moderateReview(reviewText);

        assertThat(result).isNotNull();
        assertThat(result.isApproved()).isFalse();
        assertThat(result.getReason()).isEqualTo(expectedReason);
    }

    @Test
    void moderateReview_whenResponseIsUnclear_returnsRejectedWithManualReviewMessage() {
        String reviewText = "Some review text";
        when(responseSpec.content()).thenReturn("UNKNOWN_RESPONSE_FORMAT");

        ReviewModerationService.ModerationResult result = service.moderateReview(reviewText);

        assertThat(result).isNotNull();
        assertThat(result.isApproved()).isFalse();
        assertThat(result.getReason()).isEqualTo("Die AI-Moderation konnte keine eindeutige Entscheidung treffen. Bitte überprüfen Sie den Inhalt manuell.");
    }

    @Test
    void moderateReview_whenExceptionOccurs_returnsRejectedWithErrorMessage() {
        String reviewText = "Some review text";
        when(chatClient.prompt(anyString())).thenThrow(new RuntimeException("AI Service unavailable"));

        ReviewModerationService.ModerationResult result = service.moderateReview(reviewText);

        assertThat(result).isNotNull();
        assertThat(result.isApproved()).isFalse();
        assertThat(result.getReason()).startsWith("Technischer Fehler bei der AI-Moderation:");
        assertThat(result.getReason()).contains("AI Service unavailable");
    }
}

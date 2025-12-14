<script>
    import { goto } from "$app/navigation";
    import { enhance } from "$app/forms";

    let { data, form } = $props();
    let { termin, zahnarzt, behandlungsart, praxis, patientId } = data;

    let rating = $state(0);
    let hoverRating = $state(0);
    let text = $state("");
    let isSubmitting = $state(false);

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("de-CH", {
            weekday: "long",
            year: "numeric",
            month: "long",
            day: "numeric",
        });
    };

    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString("de-CH", {
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const setRating = (value) => {
        rating = value;
    };

    const setHoverRating = (value) => {
        hoverRating = value;
    };

    const clearHoverRating = () => {
        hoverRating = 0;
    };

    const displayRating = $derived(hoverRating > 0 ? hoverRating : rating);

    const ratingText = {
        1: "Sehr unzufrieden",
        2: "Unzufrieden",
        3: "Neutral",
        4: "Zufrieden",
        5: "Sehr zufrieden",
    };
</script>

<div class="review-container">
    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={() => goto(`/termine/${termin.id}`)}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
        <div class="header-content-rezension">
            <div class="header-icon">
                <i class="bi bi-star-fill"></i>
            </div>
            <h1 class="page-title">Zahnarzt bewerten</h1>
            <p class="page-subtitle">
                Teilen Sie Ihre Erfahrung mit anderen Patienten
            </p>
        </div>
    </div>

    <!-- Termin Info Card -->
    <div class="termin-info-card">
        <div class="info-header">
            <i class="bi bi-calendar-check"></i>
            <h2>Behandlungsinformationen</h2>
        </div>
        <div class="info-grid">
            <div class="info-item">
                <span class="label">Zahnarzt:</span>
                <span class="value">{zahnarzt?.name || "Unbekannt"}</span>
            </div>
            <div class="info-item">
                <span class="label">Praxis:</span>
                <span class="value">{praxis?.bezeichnung || "Unbekannt"}</span>
            </div>
            <div class="info-item">
                <span class="label">Behandlung:</span>
                <span class="value">{behandlungsart?.name || "Unbekannt"}</span>
            </div>
            <div class="info-item">
                <span class="label">Datum:</span>
                <span class="value">{formatDate(termin.datum)}</span>
            </div>
            <div class="info-item">
                <span class="label">Uhrzeit:</span>
                <span class="value">{formatTime(termin.datum)} Uhr</span>
            </div>
            <div class="info-item">
                <span class="label">Preis:</span>
                <span class="value">{termin.preis + " CHF" || "Unbekannt"}</span
                >
            </div>
        </div>
    </div>

    <!-- Review Form -->
    <form
        method="POST"
        action="?/createReview"
        use:enhance={() => {
            isSubmitting = true;
            return async ({ update }) => {
                await update();
                isSubmitting = false;
            };
        }}
    >
        <input
            type="hidden"
            name="zahnarztId"
            value={termin.zahnarztId || termin.zahnarzt_id}
        />
        <input type="hidden" name="patientId" value={patientId} />
        <input type="hidden" name="rating" value={rating} />

        <div class="review-form">
            <!-- Rating Section -->
            <div class="form-section">
                <label class="section-label" for="rating">
                    <i class="bi bi-star"></i>
                    Ihre Bewertung *
                </label>
                <p class="section-hint">
                    Wie zufrieden waren Sie mit der Behandlung?
                </p>

                <div class="rating-container">
                    <div class="stars-row">
                        {#each [1, 2, 3, 4, 5] as star}
                            <!-- svelte-ignore a11y_click_events_have_key_events -->
                            <!-- svelte-ignore a11y_no_static_element_interactions -->
                            <div
                                class="star"
                                onclick={() => setRating(star)}
                                onmouseenter={() => setHoverRating(star)}
                                onmouseleave={clearHoverRating}
                            >
                                <i
                                    class="bi {displayRating >= star
                                        ? 'bi-star-fill'
                                        : 'bi-star'}"
                                ></i>
                            </div>
                        {/each}
                    </div>
                    {#if displayRating > 0}
                        <div class="rating-text">
                            {ratingText[displayRating]}
                        </div>
                    {/if}
                </div>

                {#if form?.error && rating === 0}
                    <p class="error-message">
                        <i class="bi bi-exclamation-circle"></i>
                        Bitte wählen Sie eine Bewertung aus
                    </p>
                {/if}
            </div>

            <!-- Text Section -->
            <div class="form-section">
                <label class="section-label" for="review-text">
                    <i class="bi bi-chat-left-text"></i>
                    Ihre Erfahrung *
                </label>
                <p class="section-hint">
                    Beschreiben Sie Ihre Erfahrung (mindestens 10 Zeichen)
                </p>

                <textarea
                    id="review-text"
                    name="text"
                    bind:value={text}
                    placeholder="Teilen Sie Ihre Erfahrung mit diesem Zahnarzt..."
                    rows="6"
                    class="review-textarea"
                    required
                    minlength="10"
                ></textarea>

                <div class="char-counter" class:warning={text.length < 10}>
                    {text.length} / 10 Zeichen (mindestens)
                </div>

                {#if form?.error && text.length < 10}
                    <p class="error-message">
                        <i class="bi bi-exclamation-circle"></i>
                        {form.error}
                    </p>
                {/if}
            </div>

            <!-- Info Box -->
            <div class="info-box">
                <i class="bi bi-info-circle"></i>
                <div class="info-content">
                    <strong>Hinweis zur Moderation:</strong>
                    <p>
                        Ihre Bewertung wird vor der Veröffentlichung geprüft.
                        Dies dient dem Schutz vor unangemessenen Inhalten und
                        stellt sicher, dass alle Bewertungen unseren Richtlinien
                        entsprechen.
                    </p>
                </div>
            </div>

            <!-- Action Buttons -->
            <div class="action-buttons">
                <button
                    type="button"
                    class="btn btn-secondary"
                    onclick={() => goto(`/termine/${termin.id}`)}
                    disabled={isSubmitting}
                >
                    <i class="bi bi-x-circle"></i>
                    Abbrechen
                </button>

                <button
                    type="submit"
                    class="btn btn-primary"
                    disabled={isSubmitting || rating === 0 || text.length < 10}
                >
                    {#if isSubmitting}
                        <span
                            class="spinner-border spinner-border-sm"
                            role="status"
                            aria-hidden="true"
                        ></span>
                        Wird gespeichert...
                    {:else}
                        <i class="bi bi-check-circle-fill"></i>
                        Bewertung abschicken
                    {/if}
                </button>
            </div>
        </div>
    </form>
</div>

<style>
    .review-container {
        padding: 2rem;
        max-width: 900px;
        margin: 0 auto;
    }

    .page-header {
        margin-bottom: 2rem;
    }

    .back-btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.25rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: #4a5568;
        background: white;
        border: 2px solid #e2e8f0;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-bottom: 1.5rem;
    }

    .back-btn:hover {
        border-color: #009688;
        color: #009688;
        background: #f0fffe;
    }

    .header-content-rezension {
        text-align: center;
    }

    .header-icon {
        width: 64px;
        height: 64px;
        margin: 0 auto 1rem;
        background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 2rem;
    }

    .page-title {
        font-size: 2rem;
        font-weight: 700;
        color: #1a202c;
        margin: 0 0 0.5rem;
    }

    .page-subtitle {
        font-size: 1rem;
        color: #718096;
        margin: 0;
    }

    .termin-info-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin-bottom: 2rem;
    }

    .info-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 1.25rem;
        padding-bottom: 1rem;
        border-bottom: 2px solid #e2e8f0;
    }

    .info-header i {
        font-size: 1.5rem;
        color: #009688;
    }

    .info-header h2 {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1a202c;
        margin: 0;
    }

    .info-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
    }

    .info-item {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
    }

    .info-item .label {
        font-size: 0.875rem;
        color: #718096;
        font-weight: 500;
    }

    .info-item .value {
        font-size: 1rem;
        color: #1a202c;
        font-weight: 600;
    }

    .review-form {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .form-section {
        margin-bottom: 2rem;
    }

    .section-label {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 1.125rem;
        font-weight: 600;
        color: #1a202c;
        margin-bottom: 0.5rem;
    }

    .section-label i {
        color: #009688;
    }

    .section-hint {
        font-size: 0.875rem;
        color: #718096;
        margin: 0 0 1rem 0;
    }

    .rating-container {
        height: 150px;
        text-align: center;
        padding: 1.5rem;
        background: linear-gradient(135deg, #f0fffe 0%, #e0f2f1 100%);
        border-radius: 12px;
        border: 2px solid #b2dfdb;
    }

    .stars-row {
        display: flex;
        justify-content: center;
        gap: 0.75rem;
        margin-bottom: 1rem;
    }

    .star {
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .star i {
        font-size: 2.5rem;
        color: #fbbf24;
        transition: all 0.2s ease;
    }

    .star:hover i {
        transform: scale(1.2);
    }

    .rating-text {
        font-size: 1.125rem;
        font-weight: 600;
        color: #009688;
        animation: fadeIn 0.3s ease;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(-5px);
        }

        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .review-textarea {
        width: 100%;
        padding: 1rem;
        font-size: 1rem;
        font-family: inherit;
        border: 2px solid #e2e8f0;
        border-radius: 8px;
        resize: vertical;
        transition: all 0.2s ease;
    }

    .review-textarea:focus {
        outline: none;
        border-color: #009688;
        box-shadow: 0 0 0 3px rgba(0, 150, 136, 0.1);
    }

    .char-counter {
        font-size: 0.875rem;
        color: #718096;
        margin-top: 0.5rem;
        text-align: right;
    }

    .char-counter.warning {
        color: #f59e0b;
    }

    .error-message {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        color: #dc2626;
        font-size: 0.875rem;
        margin-top: 0.5rem;
    }

    .info-box {
        display: flex;
        gap: 1rem;
        padding: 1rem;
        background: #f0fffe;
        border-left: 4px solid #009688;
        border-radius: 8px;
        margin-bottom: 2rem;
    }

    .info-box i {
        font-size: 1.25rem;
        color: #009688;
        flex-shrink: 0;
        margin-top: 0.25rem;
    }

    .info-content strong {
        display: block;
        color: #1a202c;
        margin-bottom: 0.25rem;
    }

    .info-content p {
        margin: 0;
        font-size: 0.875rem;
        color: #4a5568;
        line-height: 1.5;
    }

    .action-buttons {
        display: flex;
        gap: 1rem;
        justify-content: flex-end;
    }

    .btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.875rem 1.5rem;
        font-size: 1rem;
        font-weight: 600;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .btn-secondary {
        background: white;
        color: #4a5568;
        border: 2px solid #cbd5e0;
    }

    .btn-secondary:hover:not(:disabled) {
        background: #f7fafc;
        border-color: #a0aec0;
    }

    .btn-primary {
        background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
        color: white;
        box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
    }

    .btn-primary:hover:not(:disabled) {
        box-shadow: 0 6px 16px rgba(0, 150, 136, 0.4);
        transform: translateY(-1px);
    }

    @media (max-width: 640px) {
        .review-container {
            padding: 0.75rem;
        }

        .page-header {
            margin-bottom: 1rem;
        }

        .back-btn {
            padding: 0.5rem 1rem;
            font-size: 0.8125rem;
            margin-bottom: 1rem;
        }

        .page-title {
            font-size: 1.25rem;
            margin-bottom: 0.25rem;
        }

        .page-subtitle {
            font-size: 0.8125rem;
        }

        .header-icon {
            width: 40px;
            height: 40px;
            font-size: 1.25rem;
            margin-bottom: 0.75rem;
        }

        .termin-info-card {
            padding: 1rem;
            margin-bottom: 1rem;
        }

        .info-header {
            margin-bottom: 0.75rem;
            padding-bottom: 0.75rem;
        }

        .info-header h2 {
            font-size: 1rem;
        }

        .info-header i {
            font-size: 1.125rem;
        }

        .review-form {
            padding: 1rem;
        }

        .form-section {
            margin-bottom: 1.25rem;
        }

        .section-label {
            font-size: 1rem;
            margin-bottom: 0.375rem;
        }

        .section-hint {
            font-size: 0.8125rem;
            margin-bottom: 0.75rem;
        }

        .info-grid {
            grid-template-columns: 1fr 1fr;
            gap: 0.75rem;
            font-size: 0.8125rem;
        }

        .info-item .label {
            font-size: 0.75rem;
        }

        .info-item .value {
            font-size: 0.875rem;
        }

        .rating-container {
            padding: 1rem;
        }

        .stars-row {
            gap: 0.5rem;
            margin-bottom: 0.75rem;
        }

        .star i {
            font-size: 1.75rem;
        }

        .rating-text {
            font-size: 1rem;
        }

        .review-textarea {
            padding: 0.75rem;
            font-size: 0.9375rem;
        }

        .info-box {
            padding: 0.75rem;
            font-size: 0.8125rem;
            margin-bottom: 1.25rem;
        }

        .info-box i {
            font-size: 1rem;
        }

        .info-content strong {
            font-size: 0.8125rem;
        }

        .info-content p {
            font-size: 0.75rem;
        }

        .action-buttons {
            flex-direction: column-reverse;
            gap: 0.75rem;
        }

        .btn {
            width: 100%;
            justify-content: center;
            padding: 0.75rem 1.25rem;
            font-size: 0.9375rem;
        }
    }
</style>

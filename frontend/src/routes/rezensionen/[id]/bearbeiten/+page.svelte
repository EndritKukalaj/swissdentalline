<script>
    import { goto } from '$app/navigation';
    import { enhance } from '$app/forms';
    
    let { data, form } = $props();
    
    let bewertung = $state(data.review.bewertung);
    let text = $state(data.review.text);
    let hoveredStar = $state(0);
    let isSubmitting = $state(false);
    
    const handleStarClick = (rating) => {
        bewertung = rating;
    };
    
    const handleStarHover = (rating) => {
        hoveredStar = rating;
    };
    
    const handleStarLeave = () => {
        hoveredStar = 0;
    };
</script>

<svelte:head>
    <title>Rezension bearbeiten - Swissdentalline</title>
</svelte:head>

<div class="edit-container">
    <div class="page-header">
        <button class="back-btn" onclick={() => goto('/rezensionen')}>
            <i class="bi bi-arrow-left"></i>
            Zurück zur Übersicht
        </button>
        <h1 class="page-title">Rezension bearbeiten</h1>
        <p class="page-subtitle">
            Rezension für <strong>{data.zahnarzt?.name || 'Zahnarzt'}</strong>
        </p>
    </div>

    {#if form?.error}
        <div class="error-banner">
            <i class="bi bi-exclamation-circle"></i>
            <span>{form.error}</span>
        </div>
    {/if}

    {#if data.review.approved}
        <div class="info-banner warning">
            <i class="bi bi-info-circle"></i>
            <div>
                <strong>Hinweis:</strong> Diese Rezension wurde bereits veröffentlicht. 
                Nach der Bearbeitung wird sie erneut durch unsere KI-Moderation überprüft.
            </div>
        </div>
    {/if}

    <form method="POST" action="?/updateReview" use:enhance={() => {
        isSubmitting = true;
        return async ({ update }) => {
            await update();
            isSubmitting = false;
        };
    }}>
        <div class="form-card">
            <!-- Rating Section -->
            <div class="form-group">
                <label for="bewertung">
                    <i class="bi bi-star-fill"></i>
                    Bewertung
                </label>
                <div class="star-rating">
                    {#each [1, 2, 3, 4, 5] as star}
                        <button
                            type="button"
                            class="star-btn {(hoveredStar || bewertung) >= star ? 'active' : ''}"
                            onclick={() => handleStarClick(star)}
                            onmouseenter={() => handleStarHover(star)}
                            onmouseleave={handleStarLeave}
                            aria-label={`Bewertung ${star} Sterne`}
                        >
                            <i class="bi {(hoveredStar || bewertung) >= star ? 'bi-star-fill' : 'bi-star'}"></i>
                        </button>
                    {/each}
                    <span class="rating-text">
                        {bewertung > 0 ? `${bewertung} von 5 Sternen` : 'Keine Bewertung'}
                    </span>
                </div>
                <input type="hidden" name="bewertung" value={bewertung} required />
            </div>

            <!-- Text Section -->
            <div class="form-group">
                <label for="text">
                    <i class="bi bi-chat-left-text"></i>
                    Ihr Kommentar
                </label>
                <textarea
                    id="text"
                    name="text"
                    bind:value={text}
                    placeholder="Teilen Sie Ihre Erfahrung mit anderen Patienten..."
                    rows="6"
                    required
                    minlength="10"
                ></textarea>
                <span class="char-count {text.length < 10 ? 'error' : ''}">
                    {text.length} Zeichen (mindestens 10)
                </span>
            </div>

            <!-- Action Buttons -->
            <div class="form-actions">
                <button 
                    type="button" 
                    class="btn btn-secondary"
                    onclick={() => goto('/rezensionen')}
                    disabled={isSubmitting}
                >
                    <i class="bi bi-x-circle"></i>
                    Abbrechen
                </button>
                <button 
                    type="submit" 
                    class="btn btn-primary"
                    disabled={isSubmitting || bewertung === 0 || text.length < 10}
                >
                    <i class="bi {isSubmitting ? 'bi-hourglass-split' : 'bi-check-circle'}"></i>
                    {isSubmitting ? 'Wird gespeichert...' : 'Speichern'}
                </button>
            </div>
        </div>
    </form>
</div>

<style>
    .edit-container {
        max-width: 800px;
        margin: 0 auto;
        padding: 2rem;
    }

    .page-header {
        margin-bottom: 2rem;
    }

    .back-btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        background: white;
        border: 2px solid #e2e8f0;
        border-radius: 8px;
        color: #4a5568;
        font-size: 0.875rem;
        font-weight: 500;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-bottom: 1rem;
    }

    .back-btn:hover {
        border-color: #009688;
        color: #009688;
        background: #f0fdfa;
    }

    .back-btn i {
        font-size: 1rem;
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

    .error-banner {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 1rem;
        background: #fee;
        border-left: 4px solid #ef4444;
        border-radius: 8px;
        color: #991b1b;
        margin-bottom: 1.5rem;
        font-weight: 500;
    }

    .info-banner {
        display: flex;
        align-items: flex-start;
        gap: 1rem;
        padding: 1rem;
        background: #e0f7f4;
        border-left: 4px solid #009688;
        border-radius: 8px;
        margin-bottom: 1.5rem;
    }

    .info-banner.warning {
        background: #fef3c7;
        border-left-color: #f59e0b;
    }

    .info-banner i {
        font-size: 1.25rem;
        color: #009688;
        margin-top: 0.125rem;
    }

    .info-banner.warning i {
        color: #f59e0b;
    }

    .form-card {
        background: white;
        border-radius: 12px;
        padding: 2rem;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    .form-group {
        margin-bottom: 2rem;
    }

    .form-group label {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 1rem;
        font-weight: 600;
        color: #2d3748;
        margin-bottom: 0.75rem;
    }

    .star-rating {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .star-btn {
        background: none;
        border: none;
        font-size: 2rem;
        color: #cbd5e0;
        cursor: pointer;
        transition: all 0.2s ease;
        padding: 0;
    }

    .star-btn.active {
        color: #fbbf24;
    }

    .star-btn:hover {
        transform: scale(1.1);
    }

    .rating-text {
        font-size: 0.875rem;
        color: #718096;
        margin-left: 0.5rem;
    }

    textarea {
        width: 100%;
        padding: 1rem;
        border: 2px solid #e2e8f0;
        border-radius: 8px;
        font-size: 1rem;
        font-family: inherit;
        color: #2d3748;
        resize: vertical;
        transition: border-color 0.2s ease;
    }

    textarea:focus {
        outline: none;
        border-color: #009688;
    }

    textarea::placeholder {
        color: #a0aec0;
    }

    .char-count {
        display: block;
        margin-top: 0.5rem;
        font-size: 0.875rem;
        color: #718096;
    }

    .char-count.error {
        color: #ef4444;
    }

    .form-actions {
        display: flex;
        gap: 1rem;
        justify-content: flex-end;
        margin-top: 2rem;
        padding-top: 2rem;
        border-top: 1px solid #e2e8f0;
    }

    .btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.5rem;
        border: none;
        border-radius: 8px;
        font-size: 1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .btn-primary {
        background: #009688;
        color: white;
    }

    .btn-primary:hover:not(:disabled) {
        background: #00bfa5;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
    }

    .btn-secondary {
        background: white;
        color: #4a5568;
        border: 2px solid #e2e8f0;
    }

    .btn-secondary:hover:not(:disabled) {
        background: #f7fafc;
        border-color: #cbd5e0;
    }

    .btn i {
        font-size: 1.125rem;
    }

    @media (max-width: 768px) {
        .edit-container {
            padding: 0.75rem;
        }

        .page-header {
            margin-bottom: 1rem;
        }

        .back-btn {
            padding: 0.375rem 0.75rem;
            font-size: 0.8rem;
            margin-bottom: 0.75rem;
        }

       .page-title {
            font-size: 1.25rem;
            margin-bottom: 0.25rem;
        }

        .page-subtitle {
            font-size: 0.875rem;
        }

        .error-banner,
        .info-banner {
            padding: 0.75rem;
            font-size: 0.875rem;
            gap: 0.5rem;
            margin-bottom: 1rem;
        }

        .error-banner i,
        .info-banner i {
            font-size: 1rem;
        }

        .form-card {
            padding: 1rem;
            border-radius: 10px;
        }

        .form-group {
            margin-bottom: 1.25rem;
        }

        .form-group label {
            font-size: 0.875rem;
            margin-bottom: 0.5rem;
        }

        .star-rating {
            gap: 0.25rem;
        }

        .star-btn {
            font-size: 1.5rem;
        }

        .rating-text {
            font-size: 0.75rem;
            margin-left: 0.25rem;
        }

        textarea {
            padding: 0.75rem;
            font-size: 0.9rem;
            border-radius: 6px;
        }

        .char-count {
            font-size: 0.75rem;
            margin-top: 0.375rem;
        }

        .form-actions {
            flex-direction: column-reverse;
            gap: 0.75rem;
            margin-top: 1.25rem;
            padding-top: 1.25rem;
        }

        .btn {
            width: 100%;
            justify-content: center;
            padding: 0.625rem 1rem;
            font-size: 0.9rem;
        }
    }
</style>

<script>
    import { goto } from '$app/navigation';
    import { enhance } from '$app/forms';
    
    let { data, form } = $props();
    let { termin, zahnarzt, behandlungsart, praxis, patientId } = data;
    
    let rating = $state(0);
    let hoverRating = $state(0);
    let text = $state('');
    let isSubmitting = $state(false);
    
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-CH', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    };
    
    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString('de-CH', { hour: '2-digit', minute: '2-digit' });
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
        1: 'Sehr unzufrieden',
        2: 'Unzufrieden',
        3: 'Neutral',
        4: 'Zufrieden',
        5: 'Sehr zufrieden'
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
            <p class="page-subtitle">Teilen Sie Ihre Erfahrung mit anderen Patienten</p>
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
                <span class="value">{zahnarzt?.name || 'Unbekannt'}</span>
            </div>
            <div class="info-item">
                <span class="label">Praxis:</span>
                <span class="value">{praxis?.bezeichnung || 'Unbekannt'}</span>
            </div>
            <div class="info-item">
                <span class="label">Behandlung:</span>
                <span class="value">{behandlungsart?.name || 'Unbekannt'}</span>
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
                <span class="value">{termin.preis + ' CHF' || 'Unbekannt'}</span>
            </div>
        </div>
    </div>
    
    <!-- Review Form -->
    <form method="POST" action="?/createReview" use:enhance={() => {
        isSubmitting = true;
        return async ({ update }) => {
            await update();
            isSubmitting = false;
        };
    }}>
        <input type="hidden" name="zahnarztId" value={termin.zahnarztId || termin.zahnarzt_id} />
        <input type="hidden" name="patientId" value={patientId} />
        <input type="hidden" name="rating" value={rating} />
        
        <div class="review-form">
            <!-- Rating Section -->
            <div class="form-section">
                <label class="section-label" for="rating">
                    <i class="bi bi-star"></i>
                    Ihre Bewertung *
                </label>
                <p class="section-hint">Wie zufrieden waren Sie mit der Behandlung?</p>
                
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
                                <i class="bi {displayRating >= star ? 'bi-star-fill' : 'bi-star'}"></i>
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
                <p class="section-hint">Beschreiben Sie Ihre Erfahrung (mindestens 10 Zeichen)</p>
                
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
                    <p>Ihre Bewertung wird vor der Veröffentlichung geprüft. Dies dient dem Schutz vor unangemessenen Inhalten und stellt sicher, dass alle Bewertungen unseren Richtlinien entsprechen.</p>
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
                        <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
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

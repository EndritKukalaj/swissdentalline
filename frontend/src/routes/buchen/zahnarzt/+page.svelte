<script>
    import './styles.css';
    import { goto } from "$app/navigation";
    import BookingProgressBar from "$lib/components/BookingProgressBar.svelte";

    let { data } = $props();
    
    // Reactive state
    let termin = $state(data.termin);
    let zahnarzt = $state(data.zahnarzt);
    let behandlungsart = $state(data.behandlungsart);
    let praxis = $state(data.praxis);
    let rezensionen = $state(data.rezensionen);
    let gesamtBewertung = $state(data.gesamtBewertung);
    let currentPage = $state(data.reviewPagination ? data.reviewPagination.currentPage + 1 : 1);
    let nrOfPages = $state(data.reviewPagination ? data.reviewPagination.totalPages : 1);
    const pageSize = 6;
    
    // Update when server data changes
    $effect(() => {
        termin = data.termin;
        zahnarzt = data.zahnarzt;
        behandlungsart = data.behandlungsart;
        praxis = data.praxis;
        rezensionen = data.rezensionen;
        gesamtBewertung = data.gesamtBewertung;
        if (data.reviewPagination) {
            currentPage = data.reviewPagination.currentPage + 1;
            nrOfPages = data.reviewPagination.totalPages;
        }
    });

    const handleContinue = () => {
        goto(`/buchen/warteliste?terminId=${termin.id}`);
    };

    const handleBack = () => {
        goto(`/buchen/termin?behandlungsartId=${termin.behandlungsartId}`);
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const options = {
            weekday: "long",
            year: "numeric",
            month: "long",
            day: "numeric",
        };
        return date.toLocaleDateString("de-DE", options);
    };

    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString("de-DE", {
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getFullAddress = (praxis) => {
        if (!praxis) return "Adresse nicht verfügbar";
        return `${praxis.strasse || ""}, ${praxis.plz || ""} ${praxis.ort || ""}`.trim();
    };
    
    const formatReviewDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-CH', { 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    };
</script>

<div class="buchen-container">
    <BookingProgressBar currentStep={3} />

    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={handleBack}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
        <h1 class="page-title">Ihr Zahnarzt</h1>
        <p class="page-subtitle">
            Überprüfen Sie die Details Ihres ausgewählten Zahnarzt
        </p>
    </div>

    <!-- Hero Card with Selected Appointment -->
    <div class="hero-card">
        <div class="hero-pattern"></div>
        <div class="hero-content">
            <div class="appointment-icon-large">
                <i class="bi bi-calendar-check-fill"></i>
            </div>
            <div class="appointment-summary">
                <h2>{behandlungsart?.name || "Behandlung"}</h2>
                <div class="appointment-meta">
                    <div class="meta-item">
                        <i class="bi bi-calendar3"></i>
                        <span>{formatDate(termin.datum)}</span>
                    </div>
                    <div class="meta-divider"></div>
                    <div class="meta-item">
                        <i class="bi bi-clock"></i>
                        <span>{formatTime(termin.datum)} Uhr</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Zahnarzt Card -->
    <div class="zahnarzt-section">
        <div class="detail-card zahnarzt-card">
            <div class="card-icon-header">
                <div class="icon-circle">
                    <i class="bi bi-person-circle"></i>
                </div>
                <div>
                    <h2>{zahnarzt?.name}</h2>
                    <p class="card-subtitle">Behandelnder Arzt</p>
                </div>
            </div>

            {#if praxis}
                <div class="card-body">
                    <div class="detail-section">
                        <div class="section-header">
                            <i class="bi bi-cash-coin"></i>
                            <span>Behandlungskosten</span>
                        </div>
                        <p class="section-value price-value">
                            {#if termin?.preis}
                                CHF {termin.preis.toFixed(2)}
                            {:else}
                                Preis auf Anfrage
                            {/if}
                        </p>
                    </div>

                    <div class="detail-section">
                        <div class="section-header">
                            <i class="bi bi-building"></i>
                            <span>Zahnarztpraxis</span>
                        </div>
                        <p class="section-value">{praxis.bezeichnung}</p>
                    </div>

                    <div class="detail-section">
                        <div class="section-header">
                            <i class="bi bi-geo-alt-fill"></i>
                            <span>Praxisadresse</span>
                        </div>
                        <p class="section-value">{getFullAddress(praxis)}</p>
                    </div>
                </div>
            {/if}
        </div>
    </div>
    
    <!-- Action Buttons -->
    <div class="action-buttons">
        <button class="btn-back" onclick={handleBack}>
            <i class="bi bi-arrow-left"></i>
            Anderen Termin wählen
        </button>
        <button class="btn-primary" onclick={handleContinue}>
            Termin bestätigen
            <i class="bi bi-arrow-right-circle-fill"></i>
        </button>
    </div>
    <br />
    <br />

    <!-- Reviews Section -->
    {#if rezensionen && rezensionen.length > 0}
        <div class="reviews-section">
            <div class="reviews-header">
                <h2>Patientenbewertungen</h2>
                {#if gesamtBewertung && gesamtBewertung.anzahl > 0}
                    <p class="reviews-subtitle">
                        {gesamtBewertung.bewertung.toFixed(1)} ★ aus {gesamtBewertung.anzahl} {gesamtBewertung.anzahl === 1 ? 'Bewertung' : 'Bewertungen'}
                    </p>
                {/if}
            </div>
            
            <div class="reviews-grid">
                {#each rezensionen as review}
                    <div class="patient-review-card">
                        <div class="patient-info">
                            <div class="patient-avatar">
                                <i class="bi bi-person-fill"></i>
                            </div>
                            <div class="patient-details">
                                <h3 class="patient-name">{review.patientName || 'Patient'}</h3>
                                <span class="verified-badge">
                                    <i class="bi bi-patch-check-fill"></i>
                                    Verifiziert
                                </span>
                            </div>
                        </div>
                        
                        <div class="review-content">
                            <div class="review-stars">
                                {#each [1, 2, 3, 4, 5] as star}
                                    <i class="bi {review.bewertung >= star ? 'bi-star-fill' : 'bi-star'}"></i>
                                {/each}
                            </div>
                            <p class="review-text">{review.text}</p>
                        </div>
                        
                        <div class="review-footer">
                            <span class="review-date">
                                <i class="bi bi-calendar3"></i>
                                {formatReviewDate(review.datum)}
                            </span>
                        </div>
                    </div>
                {/each}
            </div>
            
            <!-- Review Pagination (always visible) -->
            <div class="review-pagination">
                <a 
                    class="btn btn-secondary-pagination"
                    class:disabled={currentPage === 1}
                    href="/buchen/zahnarzt?terminId={termin.id}&reviewPage={currentPage - 2}"
                    aria-disabled={currentPage === 1}
                >
                    <i class="bi bi-chevron-left"></i>
                    <span>Zurück</span>
                </a>
                
                <span class="page-info">
                    Seite {currentPage} von {nrOfPages}
                </span>
                
                <a 
                    class="btn btn-secondary-pagination"
                    class:disabled={currentPage >= nrOfPages}
                    href="/buchen/zahnarzt?terminId={termin.id}&reviewPage={currentPage}"
                    aria-disabled={currentPage >= nrOfPages}
                >
                    <span>Weiter</span>
                    <i class="bi bi-chevron-right"></i>
                </a>
            </div>
        </div>
    {/if}
</div>
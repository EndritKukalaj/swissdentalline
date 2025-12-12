<script>
    import { goto } from "$app/navigation";
    import { BookingProgressBar, RezensionCard, Pagination } from '$lib';

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
            Überprüfen Sie die Details Ihres ausgewählten Zahnarztes
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
                        {gesamtBewertung.bewertung.toFixed(1)} Sterne aus {gesamtBewertung.anzahl} {gesamtBewertung.anzahl === 1 ? 'Bewertung' : 'Bewertungen'}
                    </p>
                {/if}
            </div>
            
            <div class="reviews-grid">
                {#each rezensionen as review}
                    <RezensionCard
                        patientName={review.patientName || 'Patient'}
                        bewertung={review.bewertung}
                        text={review.text}
                        datum={review.datum}
                        verified={true}
                        variant="turquoise"
                    />
                {/each}
            </div>
            
            <!-- Review Pagination (always visible) -->
            <Pagination
                currentPage={currentPage}
                totalPages={nrOfPages}
                baseUrl="/buchen/zahnarzt?terminId={termin.id}&reviewPage=PAGE_NUMBER"
                variant="turquoise"
            />
        </div>
    {/if}
</div>

<style>
.buchen-container {
    min-height: 100vh;
    background: #f8fafc;
    padding: 2rem;
}

/* Header */
.page-header {
    max-width: 1200px;
    margin: 0 auto 2rem;
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

.page-title {
    font-size: 2rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 0.5rem 0;
}

.page-subtitle {
    font-size: 1.125rem;
    color: #64748b;
    margin: 0;
}

/* Hero Card */
.hero-card {
    max-width: 1200px;
    margin: 0 auto 2rem;
    background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
    border-radius: 16px;
    padding: 2rem;
    position: relative;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 150, 136, 0.2);
}

.hero-pattern {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: radial-gradient(circle at 20% 50%,
            rgba(255, 255, 255, 0.08) 0%,
            transparent 50%),
        radial-gradient(circle at 80% 80%,
            rgba(255, 255, 255, 0.08) 0%,
            transparent 50%);
    pointer-events: none;
}

.hero-content {
    position: relative;
    display: flex;
    align-items: center;
    gap: 1.5rem;
    color: white;
}

.appointment-icon-large {
    flex-shrink: 0;
    width: 64px;
    height: 64px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
}

.appointment-icon-large i {
    font-size: 2.25rem;
}

.appointment-summary h2 {
    margin: 0 0 0.75rem 0;
    font-size: 1.5rem;
    font-weight: 700;
    line-height: 1.3;
    color: white;
}

.appointment-meta {
    display: flex;
    align-items: center;
    gap: 1rem;
    flex-wrap: wrap;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.9375rem;
    font-weight: 500;
    color: white;
}

.meta-item i {
    font-size: 1.125rem;
    opacity: 0.95;
}

.meta-divider {
    width: 1px;
    height: 20px;
    background: rgba(255, 255, 255, 0.4);
}

/* Zahnarzt Section */
.zahnarzt-section {
    max-width: 1200px;
    margin: 0 auto 3rem;
}

/* Detail Cards */
.detail-card {
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    overflow: hidden;
    transition: all 0.3s ease;
}

.detail-card:hover {
    border-color: #009688;
    box-shadow: 0 8px 16px rgba(0, 150, 136, 0.1);
}

.card-icon-header {
    background: linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%);
    padding: 1.5rem;
    display: flex;
    align-items: center;
    gap: 1rem;
}

.icon-circle {
    width: 60px;
    height: 60px;
    background: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    box-shadow: 0 2px 8px rgba(0, 150, 136, 0.15);
}

.icon-circle i {
    font-size: 2rem;
    color: #009688;
}

.card-subtitle {
    margin: 0.25rem 0 0 0;
    font-size: 0.875rem;
    color: #64748b;
    font-weight: 500;
}

.card-body {
    padding: 2rem;
}

/* Detail Sections */
.detail-section {
    margin-bottom: 1.5rem;
}

.detail-section:last-child {
    margin-bottom: 0;
}

.section-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.875rem;
    font-weight: 600;
    color: #64748b;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 0.5rem;
}

.section-header i {
    color: #009688;
    font-size: 1rem;
}

.section-value {
    margin: 0;
    font-size: 1.125rem;
    color: #1a202c;
    font-weight: 500;
    line-height: 1.6;
}

.price-value {
    font-size: 1.5rem;
    color: #009688;
    font-weight: 700;
}

/* Action Buttons */
.action-buttons {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    gap: 1rem;
}

.btn-primary,
.btn-back {
    flex: 1;
    padding: 1.125rem 2rem;
    border-radius: 12px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    border: none;
}

.btn-primary {
    background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 150, 136, 0.4);
}

.btn-back {
    background: white;
    color: #009688;
    border: 2px solid #009688;
}

.btn-back:hover {
    background: #f0fffe;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 150, 136, 0.15);
}

/* Responsive Design */
@media (max-width: 1024px) {
    .reviews-grid {
        grid-template-columns: 1fr;
    }
}

/* Reviews Section */
.reviews-section {
    max-width: 1200px;
    margin: 0 auto 3rem;
}

.reviews-header {
    text-align: center;
    margin-bottom: 2rem;
    padding-bottom: 1.5rem;
    border-bottom: 2px solid #e2e8f0;
}

.reviews-header h2 {
    font-size: 1.875rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 0.5rem 0;
}

.reviews-subtitle {
    font-size: 1rem;
    color: #64748b;
    margin: 0;
    font-weight: 500;
}

.reviews-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 1.5rem;
}

@media (max-width: 768px) {
    .buchen-container {
        padding: 1rem;
    }

    .page-header {
        margin-bottom: 1.5rem;
    }

    .back-btn {
        padding: 0.5rem 0.875rem;
        font-size: 0.8125rem;
        margin-bottom: 0.75rem;
    }

    .page-title {
        font-size: 1.5rem;
        margin-bottom: 0.25rem;
    }

    .page-subtitle {
        font-size: 0.875rem;
    }

    .hero-card {
        padding: 1.25rem;
        border-radius: 12px;
        margin-bottom: 1.5rem;
    }

    .hero-content {
        flex-direction: column;
        text-align: center;
        gap: 1rem;
    }

    .appointment-icon-large {
        width: 56px;
        height: 56px;
    }

    .appointment-icon-large i {
        font-size: 2rem;
    }

    .appointment-summary h2 {
        font-size: 1.125rem;
    }

    .appointment-meta {
        justify-content: center;
        font-size: 0.8125rem;
    }

    .meta-item {
        font-size: 0.8125rem;
    }

    .meta-item i {
        font-size: 0.9375rem;
    }

    .zahnarzt-section {
        margin-bottom: 2rem;
    }

    .detail-card {
        border-radius: 12px;
    }

    .card-icon-header {
        padding: 1rem;
    }
    .icon-circle {
        width: 40px;
        height: 40px;
    }

    .icon-circle i {
        font-size: 1.25rem;
    }

    .card-subtitle {
        font-size: 0.75rem;
    }

    .card-body {
        padding: 1.25rem;
    }

    .detail-section {
        margin-bottom: 1.25rem;
    }

    .section-header {
        font-size: 0.75rem;
        margin-bottom: 0.375rem;
    }

    .section-header i {
        font-size: 0.875rem;
    }

    .section-value {
        font-size: 0.9375rem;
    }

    .price-value {
        font-size: 1.25rem;
    }

    .action-buttons {
        flex-direction: column;
        gap: 0.75rem;
    }

    .btn-primary,
    .btn-back {
        padding: 0.875rem 1.5rem;
        font-size: 0.9375rem;
    }

    .reviews-grid {
        grid-template-columns: 1fr;
        gap: 1rem;
    }

    .reviews-header {
        margin-bottom: 1.25rem;
    }

    .reviews-header h2 {
        font-size: 1.25rem;
    }
}</style>

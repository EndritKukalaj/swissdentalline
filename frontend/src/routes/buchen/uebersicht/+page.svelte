<script>
    import { goto } from '$app/navigation';
    import { enhance } from '$app/forms';
    import BookingProgressBar from '$lib/components/BookingProgressBar.svelte';
    
    let { data, form } = $props();
    let { termin, zahnarzt, behandlungsart, praxis, warteliste } = data;
    
    let isSubmitting = $state(false);
    
    const handleBack = () => {
        goto(`/buchen/warteliste?terminId=${termin.id}`);
    };

    const cancelBooking = () => {
        goto(`/buchen/behandlung`);
    };
    
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        return date.toLocaleDateString('de-DE', options);
    };
    
    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString('de-DE', { hour: '2-digit', minute: '2-digit' });
    };
    
    const getFullAddress = (praxis) => {
        if (!praxis) return 'Adresse nicht verfügbar';
        return `${praxis.strasse || ''}, ${praxis.plz || ''} ${praxis.ort || ''}`.trim();
    };
    
    // Handle successful booking
    $effect(() => {
        if (form?.success && !isSubmitting) {
            // Redirect to termin details with success flag
            setTimeout(() => {
                goto(`/termine/${form.terminId}?bookingSuccess=true`);
            }, 500);
        }
    });
</script>

<div class="buchen-container">
    <BookingProgressBar currentStep={5} />
    
    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={handleBack}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
    </div>
    
    <!-- Hero Card with Appointment Summary -->
    <div class="hero-card">
        <div class="hero-pattern"></div>
        <div class="hero-content">
            <div class="appointment-icon-large">
                <i class="bi bi-calendar-check-fill"></i>
            </div>
            <div class="appointment-summary">
                <h2>Ihre Terminbuchung</h2>
                <p class="hero-subtitle">Bitte bestätigen Sie die folgenden Angaben</p>
            </div>
        </div>
    </div>
    
    <!-- Main Content Grid -->
    <div class="content-grid">
        <!-- Appointment Details Card -->
        <div class="detail-card">
            <div class="card-icon-header">
                <div class="icon-circle">
                    <i class="bi bi-calendar3"></i>
                </div>
                <div>
                    <h3>Termindetails</h3>
                    <p class="card-subtitle">Datum und Uhrzeit</p>
                </div>
            </div>
            
            <div class="card-body">
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-calendar-event"></i>
                        <span>Datum</span>
                    </div>
                    <div class="detail-value">{formatDate(termin.datum)}</div>
                </div>
                
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-clock"></i>
                        <span>Uhrzeit</span>
                    </div>
                    <div class="detail-value">{formatTime(termin.datum)} Uhr</div>
                </div>
                
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-activity"></i>
                        <span>Behandlung</span>
                    </div>
                    <div class="detail-value">{behandlungsart?.name || 'Behandlung'}</div>
                </div>
                
                {#if behandlungsart?.dauerMinuten}
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-hourglass-split"></i>
                            <span>Dauer</span>
                        </div>
                        <div class="detail-value">ca. {behandlungsart.dauerMinuten} Minuten</div>
                    </div>
                {/if}
                
                {#if termin?.preis}
                    <div class="detail-row highlight">
                        <div class="detail-label">
                            <i class="bi bi-cash-coin"></i>
                            <span>Kosten</span>
                        </div>
                        <div class="detail-value price">CHF {termin.preis.toFixed(2)}</div>
                    </div>
                {/if}
            </div>
        </div>
        
        <!-- Doctor and Practice Card -->
        <div class="detail-card">
            <div class="card-icon-header">
                <div class="icon-circle">
                    <i class="bi bi-person-circle"></i>
                </div>
                <div>
                    <h3>Zahnarzt & Praxis</h3>
                    <p class="card-subtitle">Ihr behandelnder Arzt</p>
                </div>
            </div>
            
            <div class="card-body">
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-person-badge"></i>
                        <span>Zahnarzt</span>
                    </div>
                    <div class="detail-value">{zahnarzt?.name || 'Dr.'}</div>
                </div>
                
                {#if praxis}
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-building"></i>
                            <span>Praxis</span>
                        </div>
                        <div class="detail-value">{praxis.bezeichnung}</div>
                    </div>
                    
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-geo-alt"></i>
                            <span>Adresse</span>
                        </div>
                        <div class="detail-value">{getFullAddress(praxis)}</div>
                    </div>
                {/if}
            </div>
        </div>
    </div>
    
    <!-- Waitlist Info -->
    {#if warteliste}
        <div class="waitlist-banner">
            <i class="bi bi-list-check"></i>
            <div>
                <h4>Warteliste aktiviert</h4>
                <p>Sie werden benachrichtigt, falls ein früherer Termin verfügbar wird.</p>
            </div>
        </div>
    {/if}
    
    <!-- Error Message -->
    {#if form?.error}
        <div class="error-banner">
            <i class="bi bi-exclamation-triangle-fill"></i>
            <div>
                <h4>Buchung fehlgeschlagen</h4>
                <p>{form.error}</p>
            </div>
        </div>
    {/if}
    
    <!-- Important Notice -->
    <div class="notice-card">
        <i class="bi bi-info-circle-fill"></i>
        <div class="notice-content">
            <h4>Wichtige Hinweise</h4>
            <ul>
                <li>Bitte erscheinen Sie 10 Minuten vor Terminbeginn in der Praxis</li>
                <li>Bei Verhinderung bitten wir um rechtzeitige Absage</li>
            </ul>
        </div>
    </div>
    
    <!-- Action Buttons -->
    <div class="action-buttons">
        <button class="btn-secondary" onclick={cancelBooking} disabled={isSubmitting}>
            <i class="bi bi-x"></i>
            Buchung abbrechen
        </button>
        
        <form method="POST" action="?/bookAppointment" use:enhance={() => {
            isSubmitting = true;
            return async ({ update }) => {
                await update();
                isSubmitting = false;
            };
        }}>
            <input type="hidden" name="terminId" value={termin.id} />
            <input type="hidden" name="warteliste" value={warteliste} />
            <button type="submit" class="btn-primary" disabled={isSubmitting}>
                {#if isSubmitting}
                    <i class="bi bi-hourglass-split"></i>
                    Wird gebucht...
                {:else}
                    <i class="bi bi-check-circle-fill"></i>
                    Verbindlich buchen
                {/if}
            </button>
        </form>
    </div>
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
    margin: 0 auto 0;
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

.back-btn:hover:not(:disabled) {
    border-color: #009688;
    color: #009688;
    background: #f0fffe;
}

.back-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
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
    background-image: 
        radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.08) 0%, transparent 50%),
        radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.08) 0%, transparent 50%);
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
    margin: 0 0 0.5rem 0;
    font-size: 1.75rem;
    font-weight: 700;
    color: white;
}

.hero-subtitle {
    margin: 0;
    font-size: 1rem;
    color: rgba(255, 255, 255, 0.95);
    font-weight: 500;
}

/* Content Grid */
.content-grid {
    max-width: 1200px;
    margin: 0 auto 2rem;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
    gap: 2rem;
}

/* Detail Cards */
.detail-card {
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    overflow: hidden;
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

.card-icon-header h3 {
    margin: 0 0 0.25rem 0;
    font-size: 1.25rem;
    font-weight: 700;
    color: #1a202c;
}

.card-subtitle {
    margin: 0;
    font-size: 0.875rem;
    color: #64748b;
    font-weight: 500;
}

.card-body {
    padding: 2rem;
}

/* Detail Rows */
.detail-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 1rem 0;
    border-bottom: 1px solid #f1f5f9;
    gap: 1rem;
}

.detail-row:last-child {
    border-bottom: none;
}

.detail-row.highlight {
    background: linear-gradient(to right, rgba(0, 150, 136, 0.03), transparent);
    margin: 0 -2rem;
    padding: 1rem 2rem;
    border-bottom: 2px solid #d1f4f0;
}

.detail-label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.9375rem;
    font-weight: 600;
    color: #64748b;
    min-width: 140px;
}

.detail-label i {
    font-size: 1.125rem;
    color: #009688;
}

.detail-value {
    font-size: 1rem;
    font-weight: 500;
    color: #1a202c;
    text-align: right;
    flex: 1;
}

.detail-value.price {
    font-size: 1.5rem;
    font-weight: 700;
    color: #009688;
}

/* Waitlist Banner */
.waitlist-banner {
    max-width: 1200px;
    margin: 0 auto 2rem;
    background: linear-gradient(135deg, #e0f2f1 0%, #b2dfdb 100%);
    border: 2px solid #009688;
    border-radius: 12px;
    padding: 1.25rem 1.5rem;
    display: flex;
    align-items: center;
    gap: 1rem;
}

.waitlist-banner i {
    flex-shrink: 0;
    font-size: 2rem;
    color: #009688;
}

.waitlist-banner h4 {
    margin: 0 0 0.25rem 0;
    font-size: 1rem;
    font-weight: 700;
    color: #00695c;
}

.waitlist-banner p {
    margin: 0;
    font-size: 0.9375rem;
    color: #00796b;
}

/* Error Banner */
.error-banner {
    max-width: 1200px;
    margin: 0 auto 2rem;
    background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
    border: 2px solid #f44336;
    border-radius: 12px;
    padding: 1.25rem 1.5rem;
    display: flex;
    align-items: center;
    gap: 1rem;
}

.error-banner i {
    flex-shrink: 0;
    font-size: 2rem;
    color: #d32f2f;
}

.error-banner h4 {
    margin: 0 0 0.25rem 0;
    font-size: 1rem;
    font-weight: 700;
    color: #c62828;
}

.error-banner p {
    margin: 0;
    font-size: 0.9375rem;
    color: #d32f2f;
}

/* Notice Card */
.notice-card {
    max-width: 1200px;
    margin: 0 auto 2rem;
    background: #f0f9ff;
    border: 2px solid #bae6fd;
    border-radius: 12px;
    padding: 1.5rem;
    display: flex;
    gap: 1rem;
}

.notice-card i {
    flex-shrink: 0;
    font-size: 1.75rem;
    color: #0284c7;
    margin-top: 0.125rem;
}

.notice-content h4 {
    margin: 0 0 0.75rem 0;
    font-size: 1rem;
    font-weight: 700;
    color: #0c4a6e;
}

.notice-content ul {
    margin: 0;
    padding-left: 1.25rem;
    color: #0c4a6e;
}

.notice-content li {
    font-size: 0.9375rem;
    line-height: 1.6;
    margin-bottom: 0.5rem;
}

.notice-content li:last-child {
    margin-bottom: 0;
}

/* Action Buttons */
.action-buttons {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    gap: 1rem;
}

.action-buttons form {
    flex: 1;
}

.btn-primary,
.btn-secondary {
    width: 100%;
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

.btn-primary:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 150, 136, 0.4);
}

.btn-primary:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-secondary {
    background: white;
    color: #64748b;
    border: 2px solid #e2e8f0;
    flex: 0 0 auto;
    width: auto;
}

.btn-secondary:hover:not(:disabled) {
    background: #f8fafc;
    border-color: #cbd5e1;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.btn-secondary:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

@media (max-width: 1024px) {
    .content-grid {
        grid-template-columns: 1fr;
    }
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

    .card-icon-header {
        padding: 1rem;
    }

    .card-icon-header h3 {
        font-size: 1rem;
    }

    .icon-circle {
        width: 40px;
        height: 40px;
    }

    .icon-circle i {
        font-size: 1.25rem;
    }

    .card-body {
        padding: 1.25rem;
    }
    
    .detail-row {
        flex-direction: column;
        gap: 0.375rem;
        padding: 0.75rem 0;
    }
    
    .detail-label {
        min-width: auto;
        font-size: 0.8125rem;
    }
    
    .detail-value {
        text-align: left;
        font-size: 0.9375rem;
    }
    
    .action-buttons {
        flex-direction: column;
        gap: 0.75rem;
    }
    
    .btn-primary,
    .btn-secondary {
        padding: 0.875rem 1.5rem;
        font-size: 0.9375rem;
    }
}</style>


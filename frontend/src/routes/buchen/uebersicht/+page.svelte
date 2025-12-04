<script>
    import './styles.css';
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
            // Redirect to success page or home
            setTimeout(() => {
                goto(`/termine/${termin.id}`);
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

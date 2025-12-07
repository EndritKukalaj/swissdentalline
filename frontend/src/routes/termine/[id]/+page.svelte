<script>
    import './styles.css';
    import { goto } from '$app/navigation';
    import { enhance } from '$app/forms';
    import { onMount } from 'svelte';
    
    let { data, form } = $props();
    let { termin, behandlungsart, zahnarzt, adresse, patient, userRole, rebookSuccess, reviewSuccess, bookingSuccess } = data;
    
    let isCanceling = $state(false);
    let showCancelDialog = $state(false);
    let cancelError = $state(null);
    let showSuccessBanner = $state(false);
    let successMessage = $state('');
    
    // Show success banner on mount if rebookSuccess or reviewSuccess is true
    onMount(() => {
        if (rebookSuccess) {
            showSuccessBanner = true;
            successMessage = 'Ihr Flex-Termin wurde erfolgreich gebucht.';
            autoHideSuccessBanner();
        } else if (reviewSuccess) {
            showSuccessBanner = true;
            successMessage = 'Ihre Bewertung wurde erfolgreich gespeichert und wird geprüft.';
            autoHideSuccessBanner();
        } else if (bookingSuccess) {
            showSuccessBanner = true;
            successMessage = 'Ihr Termin wurde erfolgreich gebucht!';
            autoHideSuccessBanner();
        }
    });
    
    const autoHideSuccessBanner = () => {
        // Auto-hide after 5 seconds
        setTimeout(() => {
            showSuccessBanner = false;
            // Remove query parameter from URL
            const url = new URL(window.location.href);
            url.searchParams.delete('rebookSuccess');
            url.searchParams.delete('reviewSuccess');
            url.searchParams.delete('bookingSuccess');
            window.history.replaceState({}, '', url);
        }, 5000);
    };
    
    // Redirect to termine list after successful cancellation
    $effect(() => {
        if (form?.success) {
            goto('/termine');
        } else if (form?.error) {
            cancelError = form.error;
            isCanceling = false;
        }
    });
    
    // Determine variant based on role
    const variant = userRole === 'Zahnarzt' ? 'blue' : 'turquoise';
    
    // Format date
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
        return date.toLocaleTimeString('de-CH', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    };
    
    const formatShortDate = (dateString) => {
        const date = new Date(dateString);
        return {
            day: date.getDate(),
            month: date.toLocaleDateString('de-CH', { month: 'short' }).toUpperCase()
        };
    };
    
    // Status badge color
    const getStatusColor = (status) => {
        switch(status) {
            case 'GEBUCHT': return '#009688';
            case 'ABGESCHLOSSEN': return '#4caf50';
            case 'ABGESAGT': return '#f44336';
            default: return '#64748b';
        }
    };
    
    const getStatusText = (status) => {
        switch(status) {
            case 'GEBUCHT': return 'Gebucht';
            case 'ABGESCHLOSSEN': return 'Abgeschlossen';
            case 'ABGESAGT': return 'Abgesagt';
            default: return status;
        }
    };
    
    const dateInfo = formatShortDate(termin.datum);
</script>

<div class="details-container {variant}-variant">
    <!-- Success Banner for Rebooking -->
    {#if showSuccessBanner}
        <div class="success-banner">
            <div class="success-content">
                <i class="bi bi-check-circle-fill"></i>
                <div class="success-text">
                    <strong>Erfolgreich!</strong>
                    <span>{successMessage}</span>
                </div>
            </div>
            <button class="close-btn" aria-label="Close Banner" onclick={() => showSuccessBanner = false}>
                <i class="bi bi-x"></i>
            </button>
        </div>
    {/if}
    
    <!-- Back Button -->
    <button class="back-btn" onclick={() => goto('/termine')}>
        <i class="bi bi-arrow-left"></i>
        Zurück
    </button>
    
    <!-- Header Card -->
    <div class="header-card">
        <div class="header-content">
            <div class="date-badge-large">
                <div class="day">{dateInfo.day}</div>
                <div class="month">{dateInfo.month}</div>
            </div>
            
            <div class="header-info">
                <h1 class="termin-title">{behandlungsart?.name || 'Behandlung'}</h1>
                <div class="header-meta">
                    <span class="status-badge" style="background-color: {getStatusColor(termin.status)}">
                        {getStatusText(termin.status)}
                    </span>
                    {#if termin.wartelisteAktiv || termin.warteliste_aktiv}
                        <span class="warteliste-badge">
                            <i class="bi bi-clock-history"></i>
                            Auf Warteliste
                        </span>
                    {/if}
                </div>
                <p class="termin-date">{formatDate(termin.datum)}</p>
            </div>
        </div>
    </div>
    
    <!-- Main Details Grid -->
    <div class="details-grid">
        <!-- Termindetails Card -->
        <div class="detail-card">
            <div class="card-header">
                <i class="bi bi-calendar-event"></i>
                <h2>Termindetails</h2>
            </div>
            <div class="card-content">
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-clock"></i>
                        Uhrzeit
                    </div>
                    <div class="detail-value">{formatTime(termin.datum)} Uhr</div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-hourglass-split"></i>
                        Dauer
                    </div>
                    <div class="detail-value">{termin.dauerMinuten || termin.dauer_minuten} Minuten</div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-cash-coin"></i>
                        Preis
                    </div>
                    <div class="detail-value highlight">CHF {termin.preis?.toFixed(2)}</div>
                </div>
            </div>
        </div>
        
        <!-- Behandlungsart Card -->
        {#if behandlungsart}
            <div class="detail-card">
                <div class="card-header">
                    <i class="bi bi-bandaid"></i>
                    <h2>Behandlung</h2>
                </div>
                <div class="card-content">
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-tag"></i>
                            Behandlungsart
                        </div>
                        <div class="detail-value">{behandlungsart.name}</div>
                    </div>
                    {#if behandlungsart.beschreibung}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-info-circle"></i>
                                Beschreibung
                            </div>
                            <div class="detail-value">{behandlungsart.beschreibung}</div>
                        </div>
                    {/if}
                </div>
            </div>
        {/if}
        
        <!-- Zahnarzt & Praxis Card (Patient View) -->
        {#if zahnarzt && userRole === 'Patient'}
            <div class="detail-card">
                <div class="card-header">
                    <i class="bi bi-person-badge"></i>
                    <h2>Zahnarzt & Praxis</h2>
                </div>
                <div class="card-content">
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-person"></i>
                            Name
                        </div>
                        <div class="detail-value">
                            {zahnarzt.name || `${zahnarzt.vorname || ''} ${zahnarzt.nachname || ''}`.trim()}
                        </div>
                    </div>
                    {#if zahnarzt.spezialisierung}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-award"></i>
                                Spezialisierung
                            </div>
                            <div class="detail-value">{zahnarzt.spezialisierung}</div>
                        </div>
                    {/if}
                    {#if zahnarzt.email}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-envelope"></i>
                                E-Mail
                            </div>
                            <div class="detail-value">
                                <a href="mailto:{zahnarzt.email}">{zahnarzt.email}</a>
                            </div>
                        </div>
                    {/if}
                    
                    {#if adresse}
                        <div class="section-divider"></div>
                        
                        {#if adresse.bezeichnung}
                            <div class="detail-row">
                                <div class="detail-label">
                                    <i class="bi bi-hospital"></i>
                                    Praxis
                                </div>
                                <div class="detail-value">{adresse.bezeichnung}</div>
                            </div>
                        {/if}
                        
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-building"></i>
                                Adresse
                            </div>
                            <div class="detail-value">
                                {adresse.strasse} {adresse.hausnummer}<br>
                                {adresse.plz} {adresse.ort}
                            </div>
                        </div>
                        {#if adresse.telefon}
                            <div class="detail-row">
                                <div class="detail-label">
                                    <i class="bi bi-telephone"></i>
                                    Telefon
                                </div>
                                <div class="detail-value">
                                    <a href="tel:{adresse.telefon}">{adresse.telefon}</a>
                                </div>
                            </div>
                        {/if}
                    {/if}
                </div>
            </div>
        {/if}
        
        <!-- Patient Card (Zahnarzt View) -->
        {#if patient && userRole === 'Zahnarzt'}
            <div class="detail-card">
                <div class="card-header">
                    <i class="bi bi-person-fill"></i>
                    <h2>Patient</h2>
                </div>
                <div class="card-content">
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-person"></i>
                            Name
                        </div>
                        <div class="detail-value">
                            {patient.name || `${patient.vorname || ''} ${patient.nachname || ''}`.trim()}
                        </div>
                    </div>
                    {#if patient.email}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-envelope"></i>
                                E-Mail
                            </div>
                            <div class="detail-value">
                                <a href="mailto:{patient.email}">{patient.email}</a>
                            </div>
                        </div>
                    {/if}
                    {#if patient.telefonnummer}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-telephone"></i>
                                Telefon
                            </div>
                            <div class="detail-value">
                                <a href="tel:{patient.telefonnummer}">{patient.telefonnummer}</a>
                            </div>
                        </div>
                    {/if}
                    {#if patient.geburtsdatum}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-calendar"></i>
                                Geburtsdatum
                            </div>
                            <div class="detail-value">
                                {new Date(patient.geburtsdatum).toLocaleDateString('de-CH', { 
                                    day: '2-digit', 
                                    month: 'long', 
                                    year: 'numeric' 
                                })}
                            </div>
                        </div>
                    {/if}
                </div>
            </div>
        {/if}
    </div>
    
    <!-- Action Buttons -->
    <div class="action-section">
        {#if termin.status === 'GEBUCHT' && userRole === 'Patient'}
            <button 
                class="action-btn cancel-btn" 
                onclick={() => showCancelDialog = true}
                disabled={isCanceling}
            >
                <i class="bi bi-x-circle"></i>
                Termin stornieren
            </button>
        {/if}
        
        {#if termin.status === 'ABGESCHLOSSEN' && userRole === 'Patient'}
            <button class="action-btn review-btn" onclick={() => goto(`/termine/${termin.id}/bewerten`)}>
                <i class="bi bi-star-fill"></i>
                Zahnarzt bewerten
            </button>
        {/if}
    </div>
</div>

<!-- Cancel Confirmation Dialog -->
{#if showCancelDialog}
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="dialog-overlay" onclick={() => showCancelDialog = false}>
        <div class="dialog-content" onclick={(e) => e.stopPropagation()}>
            <div class="dialog-header">
                <i class="bi bi-exclamation-triangle"></i>
                <h3>Termin stornieren</h3>
            </div>
            <div class="dialog-body">
                <p>Möchten Sie diesen Termin wirklich stornieren?</p>
                <div class="dialog-info">
                    <div class="info-row">
                        <strong>Behandlung:</strong>
                        <span>{behandlungsart?.name || 'Unbekannt'}</span>
                    </div>
                    <div class="info-row">
                        <strong>Datum:</strong>
                        <span>{formatDate(termin.datum)}</span>
                    </div>
                    <div class="info-row">
                        <strong>Uhrzeit:</strong>
                        <span>{formatTime(termin.datum)} Uhr</span>
                    </div>
                </div>
                <p class="warning-text">
                    <i class="bi bi-info-circle"></i>
                    Diese Aktion kann nicht rückgängig gemacht werden.
                </p>
                {#if cancelError}
                    <div class="error-message">
                        <i class="bi bi-exclamation-circle"></i>
                        {cancelError}
                    </div>
                {/if}
            </div>
            <div class="dialog-actions">
                <button 
                    class="dialog-btn cancel-dialog-btn" 
                    onclick={() => { showCancelDialog = false; cancelError = null; }}
                    disabled={isCanceling}
                >
                    Abbrechen
                </button>
                <form method="POST" action="?/cancelTermin" use:enhance={() => {
                    isCanceling = true;
                    cancelError = null;
                    return async ({ result, update }) => {
                        await update();
                    };
                }}>
                    <button 
                        type="submit" 
                        class="dialog-btn confirm-btn"
                        disabled={isCanceling}
                    >
                        {#if isCanceling}
                            <i class="bi bi-hourglass-split"></i>
                            Wird storniert...
                        {:else}
                            <i class="bi bi-check-circle"></i>
                            Termin stornieren
                        {/if}
                    </button>
                </form>
            </div>
        </div>
    </div>
{/if}
<script>
    import { goto, invalidateAll } from '$app/navigation';
    import { enhance } from '$app/forms';
    import { onMount } from 'svelte';
    import { ConfirmDialog } from '$lib';
    
    let { data, form } = $props();
    
    // Reactive destructuring to update when data changes
    $effect(() => {
        termin = data.termin;
        behandlungsart = data.behandlungsart;
        zahnarzt = data.zahnarzt;
        adresse = data.adresse;
        patient = data.patient;
        userRole = data.userRole;
        rebookSuccess = data.rebookSuccess;
        reviewSuccess = data.reviewSuccess;
        bookingSuccess = data.bookingSuccess;
        slotCreated = data.slotCreated;
        slotUpdated = data.slotUpdated;
    });
    
    let termin = $state(data.termin);
    let behandlungsart = $state(data.behandlungsart);
    let zahnarzt = $state(data.zahnarzt);
    let adresse = $state(data.adresse);
    let patient = $state(data.patient);
    let userRole = $state(data.userRole);
    let rebookSuccess = $state(data.rebookSuccess);
    let reviewSuccess = $state(data.reviewSuccess);
    let bookingSuccess = $state(data.bookingSuccess);
    let slotCreated = $state(data.slotCreated);
    let slotUpdated = $state(data.slotUpdated);
    
    let isCanceling = $state(false);
    let showCancelDialog = $state(false);
    let cancelError = $state(null);
    let showSuccessBanner = $state(false);
    let successMessage = $state('');
    
    let isCompleting = $state(false);
    let showCompleteDialog = $state(false);
    let completeError = $state(null);
    
    let isReleasingToFlex = $state(false);
    let showReleaseFlexDialog = $state(false);
    let releaseFlexError = $state(null);
    
    let isDeleting = $state(false);
    let showDeleteDialog = $state(false);
    let deleteError = $state(null);
    
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
        } else if (slotCreated) {
            showSuccessBanner = true;
            successMessage = 'Freier Terminslot wurde erfolgreich erfasst!';
            autoHideSuccessBanner();
        } else if (slotUpdated) {
            showSuccessBanner = true;
            successMessage = 'Terminslot wurde erfolgreich aktualisiert!';
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
            url.searchParams.delete('slotCreated');
            url.searchParams.delete('slotUpdated');
            window.history.replaceState({}, '', url);
        }, 5000);
    };
    
    const handleActionSuccess = (action, message) => {
        invalidateAll();
        showSuccessBanner = true;
        successMessage = message;
        autoHideSuccessBanner();
    };
    
    // Handle form responses
    $effect(() => {
        if (form?.success) {
            const actionHandlers = {
                complete: () => {
                    showCompleteDialog = false;
                    isCompleting = false;
                    handleActionSuccess('complete', 'Termin wurde erfolgreich als abgeschlossen markiert.');
                },
                releaseFlex: () => {
                    showReleaseFlexDialog = false;
                    isReleasingToFlex = false;
                    handleActionSuccess('releaseFlex', 'Termin wurde erfolgreich als Flex-Termin freigegeben.');
                },
                cancel: () => {
                    showCancelDialog = false;
                    isCanceling = false;
                    handleActionSuccess('cancel', 'Termin wurde erfolgreich storniert.');
                },
                delete: () => {
                    // Redirect to termine list after deletion
                    goto('/termine');
                }
            };
            
            actionHandlers[form.action]?.();
        } else if (form?.error) {
            const errorHandlers = {
                complete: () => { completeError = form.error; isCompleting = false; },
                releaseFlex: () => { releaseFlexError = form.error; isReleasingToFlex = false; },
                cancel: () => { cancelError = form.error; isCanceling = false; },
                delete: () => { deleteError = form.error; isDeleting = false; }
            };
            
            errorHandlers[form.action]?.();
        }
    });
    
    // Determine variant based on role
    const variant = $derived(userRole === 'Zahnarzt' ? 'blue' : 'turquoise');
    
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
            case 'FLEX': return '#FFC107';
            default: return '#64748b';
        }
    };
    
    const getStatusText = (status) => {
        switch(status) {
            case 'GEBUCHT': return 'Gebucht';
            case 'ABGESCHLOSSEN': return 'Abgeschlossen';
            case 'ABGESAGT': return 'Abgesagt';
            case 'FLEX': return 'Flex-Termin';
            default: return status;
        }
    };
    
    const dateInfo = $derived(formatShortDate(termin.datum));
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
        {#if (termin.status === 'FREI' || termin.status === 'FLEX') && userRole === 'Zahnarzt'}
            <button 
                class="action-btn edit-btn" 
                onclick={() => goto(`/slots/${termin.id}`)}
            >
                <i class="bi bi-pencil-square"></i>
                Slot bearbeiten
            </button>
            <button 
                class="action-btn delete-btn" 
                onclick={() => showDeleteDialog = true}
                disabled={isDeleting}
            >
                <i class="bi bi-trash"></i>
                Slot löschen
            </button>
        {/if}
        
        {#if termin.status === 'GEBUCHT'}
            <button 
                class="action-btn cancel-btn" 
                onclick={() => showCancelDialog = true}
                disabled={isCanceling}
            >
                <i class="bi bi-x-circle"></i>
                Termin stornieren
            </button>
        {/if}
        
        {#if termin.status === 'GEBUCHT' && userRole === 'Zahnarzt'}
            <button 
                class="action-btn review-btn" 
                onclick={() => showCompleteDialog = true}
                disabled={isCompleting}
            >
                <i class="bi bi-check-circle"></i>
                Als abgeschlossen markieren
            </button>
        {/if}
        
        {#if termin.status === 'ABGESAGT' && userRole === 'Zahnarzt'}
            <button 
                class="action-btn review-btn" 
                onclick={() => showReleaseFlexDialog = true}
                disabled={isReleasingToFlex}
            >
                <i class="bi bi-lightning"></i>
                Als Flex-Termin freigeben
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
<ConfirmDialog
    show={showCancelDialog}
    title="Termin stornieren"
    message="Möchten Sie diesen Termin wirklich stornieren?"
    icon="exclamation-triangle"
    iconColor="#f44336"
    confirmText="Termin stornieren"
    isSubmitting={isCanceling}
    variant={userRole === 'Zahnarzt' ? 'blue' : 'turquoise'}
    infoRows={[
        { label: 'Behandlung', value: behandlungsart?.name || 'Unbekannt' },
        { label: 'Datum', value: formatDate(termin.datum) },
        { label: 'Uhrzeit', value: formatTime(termin.datum) + ' Uhr' }
    ]}
    warningText="Diese Aktion kann nicht rückgängig gemacht werden."
    errorMessage={cancelError}
    onClose={() => { showCancelDialog = false; cancelError = null; }}
    useForm={true}
    formAction="?/cancelTermin"
    formEnhance={() => {
        isCanceling = true;
        cancelError = null;
        return async ({ result, update }) => {
            await update();
        };
    }}
/>

<!-- Complete Confirmation Dialog -->
{#if showCompleteDialog}
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="dialog-overlay" onclick={() => showCompleteDialog = false}>
        <div class="dialog-content" onclick={(e) => e.stopPropagation()}>
            <div class="dialog-header">
                <i class="bi bi-check-circle" style="color: #4caf50;"></i>
                <h3>Termin abschliessen</h3>
            </div>
            <div class="dialog-body">
                <p>Möchten Sie diesen Termin als abgeschlossen markieren?</p>
                <div class="dialog-info">
                    <div class="info-row">
                        <strong>Patient:</strong>
                        <span>{patient?.name || `${patient?.vorname || ''} ${patient?.nachname || ''}`.trim() || 'Unbekannt'}</span>
                    </div>
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
                <p class="warning-text" style="background: #e8f5e9; color: #2e7d32;">
                    <i class="bi bi-info-circle"></i>
                    Nach dem Abschliessen kann der Patient Ihnen eine Bewertung hinterlassen.
                </p>
                {#if completeError}
                    <div class="error-message">
                        <i class="bi bi-exclamation-circle"></i>
                        {completeError}
                    </div>
                {/if}
            </div>
            <div class="dialog-actions">
                <button 
                    class="dialog-btn cancel-dialog-btn" 
                    onclick={() => { showCompleteDialog = false; completeError = null; }}
                    disabled={isCompleting}
                >
                    Abbrechen
                </button>
                <form method="POST" action="?/completeTermin" use:enhance={() => {
                    isCompleting = true;
                    completeError = null;
                    return async ({ result, update }) => {
                        await update();
                    };
                }}>
                    <button 
                        type="submit" 
                        class="dialog-btn confirm-btn"
                        style="background: linear-gradient(135deg, #4caf50 0%, #66bb6a 100%); box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);"
                        disabled={isCompleting}
                    >
                        {#if isCompleting}
                            <i class="bi bi-hourglass-split"></i>
                            Wird abgeschlossen...
                        {:else}
                            <i class="bi bi-check-circle"></i>
                            Als abgeschlossen markieren
                        {/if}
                    </button>
                </form>
            </div>
        </div>
    </div>
{/if}

<!-- Release to Flex Confirmation Dialog -->
{#if showReleaseFlexDialog}
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="dialog-overlay" onclick={() => showReleaseFlexDialog = false}>
        <div class="dialog-content" onclick={(e) => e.stopPropagation()}>
            <div class="dialog-header">
                <i class="bi bi-lightning" style="color: #FFC107;"></i>
                <h3>Flex-Termin freigeben</h3>
            </div>
            <div class="dialog-body">
                <p>Möchten Sie diesen abgesagten Termin als Flex-Termin freigeben?</p>
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
                <p class="warning-text" style="background: #fff9e6; color: #856404;">
                    <i class="bi bi-info-circle"></i>
                    Der Termin wird als Flex-Termin für Patienten auf der Warteliste verfügbar.
                </p>
                {#if releaseFlexError}
                    <div class="error-message">
                        <i class="bi bi-exclamation-circle"></i>
                        {releaseFlexError}
                    </div>
                {/if}
            </div>
            <div class="dialog-actions">
                <button 
                    class="dialog-btn cancel-dialog-btn" 
                    onclick={() => { showReleaseFlexDialog = false; releaseFlexError = null; }}
                    disabled={isReleasingToFlex}
                >
                    Abbrechen
                </button>
                <form method="POST" action="?/releaseToFlex" use:enhance={() => {
                    isReleasingToFlex = true;
                    releaseFlexError = null;
                    return async ({ result, update }) => {
                        await update();
                    };
                }}>
                    <button 
                        type="submit" 
                        class="dialog-btn confirm-btn"
                        style="background: linear-gradient(135deg, #FFC107 0%, #FFB300 100%); box-shadow: 0 2px 8px rgba(255, 193, 7, 0.3);"
                        disabled={isReleasingToFlex}
                    >
                        {#if isReleasingToFlex}
                            <i class="bi bi-hourglass-split"></i>
                            Wird freigegeben...
                        {:else}
                            <i class="bi bi-lightning"></i>
                            Als Flex-Termin freigeben
                        {/if}
                    </button>
                </form>
            </div>
        </div>
    </div>
{/if}

<!-- Delete Confirmation Dialog -->
{#if showDeleteDialog}
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="dialog-overlay" onclick={() => showDeleteDialog = false}>
        <div class="dialog-content" onclick={(e) => e.stopPropagation()}>
            <div class="dialog-header">
                <i class="bi bi-trash" style="color: #dc2626;"></i>
                <h3>Slot löschen</h3>
            </div>
            <div class="dialog-body">
                <p>Möchten Sie diesen freien Slot wirklich löschen?</p>
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
                <p class="warning-text" style="background: #fee2e2; color: #991b1b;">
                    <i class="bi bi-exclamation-triangle"></i>
                    Diese Aktion kann nicht rückgängig gemacht werden.
                </p>
                {#if deleteError}
                    <div class="error-message">
                        <i class="bi bi-exclamation-circle"></i>
                        {deleteError}
                    </div>
                {/if}
            </div>
            <div class="dialog-actions">
                <button 
                    class="dialog-btn cancel-dialog-btn" 
                    onclick={() => { showDeleteDialog = false; deleteError = null; }}
                    disabled={isDeleting}
                >
                    Abbrechen
                </button>
                <form method="POST" action="?/deleteSlot" use:enhance={() => {
                    isDeleting = true;
                    deleteError = null;
                    return async ({ result, update }) => {
                        await update();
                    };
                }}>
                    <button 
                        type="submit" 
                        class="dialog-btn confirm-btn"
                        style="background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%); box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);"
                        disabled={isDeleting}
                    >
                        {#if isDeleting}
                            <i class="bi bi-hourglass-split"></i>
                            Wird gelöscht...
                        {:else}
                            <i class="bi bi-trash"></i>
                            Slot löschen
                        {/if}
                    </button>
                </form>
            </div>
        </div>
    </div>
{/if}

<style>
/* Termin Details Page Specific Styles */

.details-container {
    padding: 2rem;
    max-width: 1200px;
    margin: 0 auto;
    --detail-primary: #009688;
    --detail-accent: #00bfa5;
    --detail-bg-light: #f0fffe;
    --detail-hover-bg: rgba(0, 150, 136, 0.05);
    --detail-border: rgba(0, 150, 136, 0.1);
    --detail-shadow: rgba(0, 150, 136, 0.15);
    --detail-focus: rgba(0, 150, 136, 0.1);
}

.details-container.blue-variant {
    --detail-primary: #30B0C7;
    --detail-accent: #268a9c;
    --detail-bg-light: #E8F8FA;
    --detail-hover-bg: rgba(48, 176, 199, 0.05);
    --detail-border: rgba(38, 138, 156, 0.1);
    --detail-shadow: rgba(38, 138, 156, 0.15);
    --detail-focus: rgba(48, 176, 199, 0.1);
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
    border-color: var(--detail-primary);
    color: var(--detail-primary);
    background: var(--detail-bg-light);
}

.header-card {
    background: linear-gradient(135deg, #ffffff 0%, var(--detail-bg-light) 100%);
    border-radius: 20px;
    padding: 2rem;
    margin-bottom: 2rem;
    box-shadow: 0 8px 24px var(--detail-shadow);
    border: 2px solid var(--detail-border);
    border-top: 6px solid;
    border-image: linear-gradient(90deg, var(--detail-primary) 0%, var(--detail-accent) 100%) 1;
}

.header-content {
    display: flex;
    gap: 2rem;
    align-items: flex-start;
}

.date-badge-large {
    background: linear-gradient(135deg, var(--detail-primary) 0%, var(--detail-accent) 100%);
    border-radius: 16px;
    padding: 1.5rem;
    text-align: center;
    color: white;
    box-shadow: 0 8px 16px var(--detail-shadow);
    min-width: 120px;
    flex-shrink: 0;
}

.date-badge-large .day {
    font-size: 3rem;
    font-weight: 700;
    line-height: 1;
    margin-bottom: 0.25rem;
}

.date-badge-large .month {
    font-size: 1rem;
    font-weight: 600;
    letter-spacing: 0.5px;
    opacity: 0.95;
}

.header-info {
    flex: 1;
}

.termin-title {
    font-size: 2rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 0.75rem 0;
}

.header-meta {
    display: flex;
    gap: 0.75rem;
    margin-bottom: 0.75rem;
    flex-wrap: wrap;
}

.warteliste-badge {
    padding: 0.375rem 0.875rem;
    border-radius: 6px;
    background: linear-gradient(135deg, #FFC107 0%, #FFB300 100%);
    color: white;
    font-size: 0.875rem;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 0.375rem;
}

.termin-date {
    font-size: 1.125rem;
    color: #4a5568;
    margin: 0;
}

.details-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
}

.section-divider {
    height: 2px;
    background: linear-gradient(90deg, var(--detail-bg-light) 0%, var(--detail-primary) 50%, var(--detail-bg-light) 100%);
    margin: 0.5rem 0;
    border-radius: 2px;
}

/* Color variants for details */
.details-container .detail-card:hover {
    box-shadow: 0 8px 24px var(--detail-shadow);
}

.details-container .card-header {
    border-bottom: 2px solid var(--detail-bg-light);
}

.details-container .card-header i {
    color: var(--detail-primary);
}

.details-container .detail-label i {
    color: var(--detail-primary);
}

.details-container .detail-value.highlight {
    color: var(--detail-primary);
}

.details-container .detail-value a {
    color: var(--detail-primary);
}

.details-container .detail-value a:hover {
    color: var(--detail-accent);
}

.action-section {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
    justify-content: center;
    margin-top: 2rem;
    padding-top: 2rem;
    border-top: 2px solid var(--detail-bg-light);
}

.action-btn {
    padding: 1rem 2rem;
    font-size: 1rem;
    font-weight: 600;
    border-radius: 12px;
    border: none;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    position: relative;
}

.action-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.cancel-btn {
    background: linear-gradient(135deg, #f44336 0%, #e53935 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
}

.cancel-btn:hover:not(:disabled) {
    box-shadow: 0 6px 20px rgba(244, 67, 54, 0.4);
    transform: translateY(-2px);
}

.review-btn {
    background: linear-gradient(135deg, #FFC107 0%, #FFB300 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
}

.review-btn:hover:not(:disabled) {
    box-shadow: 0 6px 20px rgba(255, 193, 7, 0.4);
    transform: translateY(-2px);
}

.coming-soon {
    position: absolute;
    top: -0.5rem;
    right: -0.5rem;
    background: #64748b;
    color: white;
    font-size: 0.625rem;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

@media (max-width: 768px) {
    .details-container {
        padding: 1rem;
    }

    .back-btn {
        padding: 0.5rem 0.875rem;
        font-size: 0.8125rem;
        margin-bottom: 0.75rem;
    }

    .header-card {
        padding: 1.25rem;
        border-radius: 12px;
        margin-bottom: 1.5rem;
    }

    .header-content {
        flex-direction: column;
        gap: 1rem;
    }

    .date-badge-large {
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.75rem;
        padding: 0.875rem 1.25rem;
        border-radius: 10px;
    }

    .date-badge-large .day {
        font-size: 2rem;
    }

    .date-badge-large .month {
        font-size: 0.875rem;
    }

    .termin-title {
        font-size: 1.25rem;
        margin-bottom: 0.5rem;
    }

    .header-meta {
        gap: 0.5rem;
        margin-bottom: 0.5rem;
    }

    .warteliste-badge {
        padding: 0.25rem 0.625rem;
        font-size: 0.75rem;
        gap: 0.25rem;
    }

    .termin-date {
        font-size: 0.9375rem;
    }

    .details-grid {
        grid-template-columns: 1fr;
        gap: 1rem;
        margin-bottom: 1.5rem;
    }

    .detail-card {
        border-radius: 10px;
    }

    .card-header {
        padding: 1rem;
    }

    .card-header i {
        font-size: 1.125rem;
    }

    .card-body {
        padding: 1rem;
    }

    .detail-row {
        padding: 0.625rem 0;
        flex-direction: row;
    }

    .detail-label {
        font-size: 0.75rem;
        margin-bottom: 0.25rem;
    }

    .detail-label i {
        font-size: 0.875rem;
    }

    .detail-value {
        font-size: 0.9375rem;
    }

    .detail-value.highlight {
        font-size: 1rem;
    }

    .action-section {
        flex-direction: column;
        gap: 0.75rem;
        margin-top: 1.5rem;
        padding-top: 1.5rem;
    }

    .action-btn {
        width: 100%;
        justify-content: center;
        padding: 0.875rem 1.5rem;
        font-size: 0.9375rem;
        gap: 0.5rem;
    }

    .coming-soon {
        font-size: 0.625rem;
        padding: 0.1875rem 0.375rem;
    }
}

/* Dialog Styles */
.dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    z-index: 1000;
    animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

.dialog-content {
    background: white;
    border-radius: 16px;
    padding: 0;
    max-width: 500px;
    width: 100%;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    animation: slideUp 0.3s ease;
}

@keyframes slideUp {
    from {
        transform: translateY(20px);
        opacity: 0;
    }
    to {
        transform: translateY(0);
        opacity: 1;
    }
}

.dialog-header {
    padding: 1.5rem;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.dialog-header i {
    font-size: 1.5rem;
    color: #f44336;
}

.dialog-header h3 {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: #1a202c;
}

.dialog-body {
    padding: 1.5rem;
}

.dialog-body > p {
    margin: 0 0 1.5rem 0;
    color: #4a5568;
    font-size: 1rem;
    line-height: 1.5;
}

.dialog-info {
    background: #f7fafc;
    border-radius: 8px;
    padding: 1rem;
    margin-bottom: 1rem;
}

.info-row {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
}

.info-row:not(:last-child) {
    border-bottom: 1px solid #e2e8f0;
}

.info-row strong {
    color: #2d3748;
    font-weight: 600;
}

.info-row span {
    color: #4a5568;
    text-align: right;
}

.warning-text {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem;
    background: #fff3cd;
    border-radius: 8px;
    color: #856404;
    font-size: 0.875rem;
    margin: 1rem 0 0 0;
}

.warning-text i {
    font-size: 1rem;
}

.error-message {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem;
    background: #fee;
    border-radius: 8px;
    color: #c53030;
    font-size: 0.875rem;
    margin-top: 1rem;
}

.error-message i {
    font-size: 1rem;
}

.dialog-actions {
    padding: 1.5rem;
    border-top: 1px solid #e2e8f0;
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
}

.dialog-actions form {
    display: contents;
}

.dialog-btn {
    padding: 0.75rem 1.5rem;
    border-radius: 8px;
    font-size: 0.875rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    border: none;
}

.cancel-dialog-btn {
    background: white;
    color: #4a5568;
    border: 2px solid #cbd5e0;
}

.cancel-dialog-btn:hover:not(:disabled) {
    background: #f7fafc;
    border-color: #a0aec0;
}

.confirm-btn {
    background: linear-gradient(135deg, #f44336 0%, #e53935 100%);
    color: white;
    box-shadow: 0 2px 8px rgba(244, 67, 54, 0.3);
}

.confirm-btn:hover:not(:disabled) {
    box-shadow: 0 4px 12px rgba(244, 67, 54, 0.4);
    transform: translateY(-1px);
}

.dialog-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

/* Action Section Styles */
.action-section {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    margin-top: 2rem;
}

.action-btn {
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

.action-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.cancel-btn {
    background: linear-gradient(135deg, #f44336 0%, #e53935 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
}

.cancel-btn:hover:not(:disabled) {
    box-shadow: 0 6px 16px rgba(244, 67, 54, 0.4);
    transform: translateY(-1px);
}

.review-btn {
    background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(251, 191, 36, 0.3);
}

.review-btn:hover:not(:disabled) {
    box-shadow: 0 6px 16px rgba(251, 191, 36, 0.4);
    transform: translateY(-1px);
}

/* Success Banner Styles */
.success-banner {
    position: relative;
    background: linear-gradient(135deg, #4caf50 0%, #66bb6a 100%);
    color: white;
    padding: 1rem 1.5rem;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    margin-bottom: 1.5rem;
    animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
    from {
        opacity: 0;
        transform: translateY(-20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.success-content {
    display: flex;
    align-items: center;
    gap: 1rem;
    flex: 1;
}

.success-content > i {
    font-size: 1.5rem;
    flex-shrink: 0;
}

.success-text {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.success-text strong {
    font-size: 1rem;
    font-weight: 600;
}

.success-text span {
    font-size: 0.875rem;
    opacity: 0.95;
}

.close-btn {
    background: rgba(255, 255, 255, 0.2);
    color: white;
    border: none;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s ease;
    flex-shrink: 0;
}

.close-btn:hover {
    background: rgba(255, 255, 255, 0.3);
}


.close-btn i {
    font-size: 1.25rem;
}

.edit-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.5rem;
    font-size: 0.9375rem;
    font-weight: 600;
    color: white;
    background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(48, 176, 199, 0.3);
}

.edit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(48, 176, 199, 0.4);
}

.edit-btn:active {
    transform: translateY(0);
    box-shadow: 0 2px 6px rgba(48, 176, 199, 0.3);
}

.delete-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.5rem;
    font-size: 0.9375rem;
    font-weight: 600;
    color: white;
    background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);
}

.delete-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(220, 38, 38, 0.4);
}

.delete-btn:active {
    transform: translateY(0);
    box-shadow: 0 2px 6px rgba(220, 38, 38, 0.3);
}

@media (max-width: 640px) {
    .success-banner {
        padding: 0.875rem 1rem;
        border-radius: 10px;
    }

    .success-content > i {
        font-size: 1.25rem;
    }

    .success-text strong {
        font-size: 0.875rem;
    }

    .success-text span {
        font-size: 0.75rem;
    }

    .close-btn {
        width: 28px;
        height: 28px;
    }

    .close-btn i {
        font-size: 1rem;
    }

    .edit-btn,
    .delete-btn {
        width: 100%;
        justify-content: center;
        padding: 0.625rem 1.25rem;
        font-size: 0.875rem;
    }

    .dialog-overlay {
        padding: 0;
        align-items: flex-end;
    }

    .dialog-content {
        border-radius: 16px 16px 0 0;
        max-width: 100%;
    }

    .dialog-actions {
        flex-direction: column-reverse;
    }

    .dialog-btn {
        width: 100%;
        justify-content: center;
    }
}

</style>

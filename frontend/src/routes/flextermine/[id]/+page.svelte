<script>
    import './styles.css';
    import { goto } from '$app/navigation';
    import { enhance } from '$app/forms';
    
    let { data } = $props();
    let { flexTermin, oldTermin, behandlungsart, flexZahnarzt, oldZahnarzt, patientId } = data;
    
    let isSubmitting = $state(false);
    
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-DE', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    };
    
    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString('de-DE', { hour: '2-digit', minute: '2-digit' });
    };
    
    const getDateParts = (dateString) => {
        const date = new Date(dateString);
        return {
            day: date.getDate(),
            month: date.toLocaleDateString('de-DE', { month: 'short' }).toUpperCase(),
            weekday: date.toLocaleDateString('de-DE', { weekday: 'short' }).toUpperCase()
        };
    };
    
    const calculateDiscount = () => {
        const now = new Date();
        const terminDate = new Date(flexTermin.datum);
        const daysUntil = Math.floor((terminDate - now) / (1000 * 60 * 60 * 24));
        
        if (daysUntil <= 7) return 10;
        if (daysUntil <= 14) return 7;
        return 5;
    };
    
    const discount = calculateDiscount();
    const originalPrice = flexTermin.preis;
    const discountedPrice = originalPrice * (1 - discount / 100);
    
    const oldDateParts = getDateParts(oldTermin.datum);
    const flexDateParts = getDateParts(flexTermin.datum);
</script>

<div class="flex-detail-container">
    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={() => goto('/flextermine')}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
        <div class="header-content">
            <div class="header-icon">
                <i class="bi bi-lightning-charge-fill"></i>
            </div>
            <h1 class="page-title">Flex-Termin annehmen?</h1>
        </div>
    </div>
    
    <!-- Comparison Section -->
    <div class="comparison-section">
        <h2 class="section-title">Da Sie den neuen Termin kurzfristig wahrnehmen, erhalten Sie {discount}% Rabatt auf die Behandlung!</h2>
        <p class="section-subtitle">Möchten Sie diesen früheren Termin wahrnehmen?</p>
        
        <div class="comparison-cards">
            <!-- Old Termin Card -->
            <div class="termin-comparison-card old-termin">
                <div class="card-header">
                    <div class="card-badge old">
                        <i class="bi bi-calendar-x"></i>
                        Ursprünglicher Termin
                    </div>
                </div>
                
                <div class="card-body">
                    <div class="date-display">
                        <div class="date-large">
                            <div class="weekday">{oldDateParts.weekday}</div>
                            <div class="day">{oldDateParts.day}</div>
                            <div class="month">{oldDateParts.month}</div>
                        </div>
                        <div class="time-display">
                            <i class="bi bi-clock"></i>
                            <span>{formatTime(oldTermin.datum)} Uhr</span>
                        </div>
                    </div>
                    
                    <div class="termin-details-flex">
                        <h3 class="behandlung-title">{behandlungsart?.name || 'Behandlung'}</h3>
                        
                        <div class="detail-row">
                            <i class="bi bi-person-circle"></i>
                            <span>{oldZahnarzt?.name || ''}</span>
                        </div>
                        
                        <div class="detail-row">
                            <i class="bi bi-hourglass-split"></i>
                            <span>{oldTermin.dauerMinuten} Minuten</span>
                        </div>
                        
                        <div class="detail-row">
                            <i class="bi bi-cash-coin"></i>
                            <span>CHF {oldTermin.preis.toFixed(2)}</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Arrow -->
            <div class="arrow-container">
                <i class="bi bi-arrow-right-circle-fill"></i>
            </div>
            
            <!-- New Flex Termin Card -->
            <div class="termin-comparison-card flex-termin">
                <div class="card-header">
                    <div class="card-badge new">
                        <i class="bi bi-lightning-charge-fill"></i>
                        Neuer Flex-Termin
                    </div>
                    <div class="discount-label">
                        <i class="bi bi-gift-fill"></i>
                        {discount}% Rabatt
                    </div>
                </div>
                
                <div class="card-body">
                    <div class="date-display">
                        <div class="date-large highlighted">
                            <div class="weekday">{flexDateParts.weekday}</div>
                            <div class="day">{flexDateParts.day}</div>
                            <div class="month">{flexDateParts.month}</div>
                        </div>
                        <div class="time-display highlighted">
                            <i class="bi bi-clock-fill"></i>
                            <span>{formatTime(flexTermin.datum)} Uhr</span>
                        </div>
                    </div>
                    
                    <div class="termin-details-flex">
                        <h3 class="behandlung-title">{behandlungsart?.name || 'Behandlung'}</h3>
                        
                        <div class="detail-row">
                            <i class="bi bi-person-circle"></i>
                            <span>{flexZahnarzt?.name || ''}</span>
                        </div>
                        
                        <div class="detail-row">
                            <i class="bi bi-hourglass-split"></i>
                            <span>{flexTermin.dauerMinuten} Minuten</span>
                        </div>
                        
                        <div class="detail-row price-row">
                            <i class="bi bi-cash-coin"></i>
                            <div class="price-info">
                                <span class="original-price">CHF {originalPrice.toFixed(2)}</span>
                                <span class="discounted-price">CHF {discountedPrice.toFixed(2)}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Benefits Section -->
    <div class="benefits-section">
        <h3>Ihre Vorteile</h3>
        <div class="benefits-grid">
            <div class="benefit-card">
                <div class="benefit-icon">
                    <i class="bi bi-calendar-check-fill"></i>
                </div>
                <h4>Früherer Termin</h4>
                <p>Sie erhalten Ihre Behandlung früher</p>
            </div>
            
            <div class="benefit-card">
                <div class="benefit-icon">
                    <i class="bi bi-piggy-bank-fill"></i>
                </div>
                <h4>{discount}% Ersparnis</h4>
                <p>Sie sparen CHF {(originalPrice - discountedPrice).toFixed(2)}</p>
            </div>
            
            <div class="benefit-card">
                <div class="benefit-icon">
                    <i class="bi bi-shield-check"></i>
                </div>
                <h4>Gleiche Qualität</h4>
                <p>Dieselbe Behandlung zum besseren Preis</p>
            </div>
        </div>
    </div>
    
    <!-- Action Buttons -->
    <form method="POST" action="?/confirmRebooking" use:enhance={() => {
        isSubmitting = true;
        return async ({ update }) => {
            await update();
            isSubmitting = false;
        };
    }}>
        <input type="hidden" name="oldTerminId" value={oldTermin.id} />
        
        <div class="action-buttons">
            <button type="button" class="btn btn-secondary" onclick={() => goto('/flextermine')} disabled={isSubmitting}>
                <i class="bi bi-x-circle"></i>
                Nein, Termin behalten
            </button>
            
            <button type="submit" class="btn btn-primary" disabled={isSubmitting}>
                {#if isSubmitting}
                    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                    Wird umgebucht...
                {:else}
                    <i class="bi bi-check-circle-fill"></i>
                    Ja, Termin Bestätigen
                {/if}
            </button>
        </div>
    </form>
</div>
<script>
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

<style>
.flex-detail-container {
    min-height: 100vh;
}

.flex-detail-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem 1rem;
}

.page-header {
    margin-bottom: 2rem;
}

.back-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;
    background: transparent;
    border: none;
    color: #009688;
    font-size: 1rem;
    cursor: pointer;
    transition: all 0.2s;
    margin-bottom: 1rem;
}

.back-btn:hover {
    color: #00796B;
    transform: translateX(-4px);
}

.header-content {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.header-icon {
    font-size: 2.5rem;
    color: #FFB84D;
}

.page-title {
    font-size: 2rem;
    font-weight: 700;
    color: #333;
    margin: 0;
}

.comparison-section {
    background: white;
    border-radius: 16px;
    padding: 2rem;
    margin-bottom: 2rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
    font-size: 1.4rem;
    font-weight: 600;
    color: #004D40;
    margin: 0 0 0.5rem 0;
    text-align: center;
}

.section-subtitle {
    font-size: 1.1rem;
    color: #666;
    margin: 0 0 2rem 0;
    text-align: center;
}

.comparison-cards {
    display: grid;
    grid-template-columns: 1fr auto 1fr;
    gap: 2rem;
    align-items: center;
}

.termin-comparison-card {
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.3s ease;
}

.old-termin {
    border: 2px solid #e0e0e0;
    opacity: 0.85;
}

.flex-termin {
    border: 3px solid #009688;
    box-shadow: 0 4px 16px rgba(0, 150, 136, 0.2);
}

.card-header {
    padding: 1rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #f8f9fa;
    margin-bottom: 0;
}

.card-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;
    border-radius: 20px;
    font-weight: 600;
    font-size: 0.9rem;
}

.card-badge.old {
    background: #e0e0e0;
    color: #666;
}

.card-badge.new {
    background: linear-gradient(135deg, #009688 0%, #00796B 100%);
    color: white;
}

.discount-label {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    background: #FFB84D;
    color: white;
    padding: 0.5rem 1rem;
    border-radius: 20px;
    font-weight: 600;
    font-size: 0.9rem;
}

.card-body {
    padding: 1.5rem;
}

.date-display {
    text-align: center;
    margin-bottom: 1.5rem;
}

.date-large {
    background: #f8f9fa;
    border-radius: 12px;
    padding: 1rem;
    margin-bottom: 1rem;
}

.date-large.highlighted {
    background: linear-gradient(135deg, #E0F2F1 0%, #B2DFDB 100%);
}

.weekday {
    font-size: 0.9rem;
    font-weight: 600;
    color: #666;
    text-transform: uppercase;
}

.day {
    font-size: 3rem;
    font-weight: 700;
    color: #333;
    line-height: 1;
    margin: 0.25rem 0;
}

.month {
    font-size: 1rem;
    font-weight: 600;
    color: #666;
    text-transform: uppercase;
}

.time-display {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;
    background: white;
    border: 2px solid #e0e0e0;
    border-radius: 20px;
    font-size: 1.1rem;
    font-weight: 600;
}

.time-display.highlighted {
    background: #009688;
    color: white;
    border-color: #009688;
}

.termin-details-flex {
    border-top: 2px solid #e0e0e0;
    padding-top: 1.5rem;
}

.behandlung-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 1rem 0;
}

.detail-row {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    margin-bottom: 0.75rem;
    color: #666;
}

.detail-row i {
    color: #009688;
    font-size: 1.1rem;
}

.price-row {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 1px solid #e0e0e0;
}

.price-info {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.original-price {
    text-decoration: line-through;
    color: #999;
    font-size: 0.9rem;
}

.discounted-price {
    font-size: 1.3rem;
    font-weight: 700;
    color: #009688;
}

.arrow-container {
    font-size: 3rem;
    color: #009688;
    text-align: center;
}

.benefits-section {
    background: white;
    border-radius: 16px;
    padding: 2rem;
    margin-bottom: 2rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.benefits-section h3 {
    font-size: 1.5rem;
    font-weight: 600;
    color: #333;
    margin: 0 0 1.5rem 0;
    text-align: center;
}

.benefits-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
}

.benefit-card {
    text-align: center;
    padding: 1.5rem;
    background: #E0F2F1;
    border-radius: 12px;
}

.benefit-icon {
    font-size: 2.5rem;
    color: #009688;
    margin-bottom: 1rem;
}

.benefit-card h4 {
    font-size: 1.1rem;
    font-weight: 600;
    color: #004D40;
    margin: 0 0 0.5rem 0;
}

.benefit-card p {
    color: #666;
    margin: 0;
}

.action-buttons {
    display: flex;
    gap: 1rem;
    justify-content: center;
    padding: 2rem 0;
}

.btn {
    display: inline-flex;
    align-items: center;
    gap: 0.75rem;
    padding: 1rem 2rem;
    border-radius: 8px;
    font-size: 1.1rem;
    font-weight: 600;
    border: none;
    cursor: pointer;
    transition: all 0.3s ease;
}

.btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-secondary {
    background: #f8f9fa;
    color: #666;
    border: 2px solid #e0e0e0;
}

.btn-secondary:hover:not(:disabled) {
    background: #e9ecef;
    border-color: #ccc;
}

.btn-primary {
    background: linear-gradient(135deg, #009688 0%, #00796B 100%);
    color: white;
    box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
}

.btn-primary:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 150, 136, 0.4);
}

@media (max-width: 992px) {
    .flex-detail-container {
        padding: 1rem 0.75rem;
    }

    .page-header {
        margin-bottom: 1rem;
    }

    .header-content {
        gap: 0.75rem;
    }

    .header-icon {
        font-size: 1.75rem;
    }

    .page-title {
        font-size: 1.4rem;
    }

    .comparison-section {
        padding: 1.25rem;
        margin-bottom: 1.25rem;
    }

    .section-title {
        font-size: 1.1rem;
        margin-bottom: 0.25rem;
    }

    .section-subtitle {
        font-size: 0.95rem;
        margin-bottom: 1.25rem;
    }

    .comparison-cards {
        grid-template-columns: 1fr;
        gap: 1rem;
    }

    .card-header {
        padding: 0.75rem;
        flex-direction: column;
        gap: 0.5rem;
        align-items: flex-start;
    }

    .card-badge,
    .discount-label {
        font-size: 0.8rem;
        padding: 0.4rem 0.75rem;
    }

    .card-body {
        padding: 1rem;
    }

    .date-display {
        margin-bottom: 1rem;
    }

    .date-large {
        padding: 0.75rem;
        margin-bottom: 0.75rem;
    }

    .day {
        font-size: 2.25rem;
    }

    .weekday,
    .month {
        font-size: 0.85rem;
    }

    .time-display {
        font-size: 0.95rem;
        padding: 0.4rem 0.75rem;
    }

    .behandlung-title {
        font-size: 1rem;
        margin-bottom: 0.75rem;
    }

    .detail-row {
        flex-direction: row;
        gap: 0.5rem;
        margin-bottom: 0.5rem;
        font-size: 0.9rem;
    }

    .detail-row i {
        font-size: 1rem;
    }

    .price-info {
        gap: 0.15rem;
    }

    .original-price {
        font-size: 0.85rem;
    }

    .discounted-price {
        font-size: 1.1rem;
    }

    .arrow-container {
        font-size: 2rem;
        transform: rotate(90deg);
        margin: 0;
    }

    .benefits-section {
        padding: 1.25rem;
        margin-bottom: 1.25rem;
    }

    .benefits-section h3 {
        font-size: 1.2rem;
        margin-bottom: 1rem;
    }

    .benefits-grid {
        grid-template-columns: 1fr;
        gap: 0.75rem;
    }

    .benefit-card {
        padding: 1rem;
        display: flex;
        gap: 1rem;
        text-align: left;
    }

    .benefit-icon {
        font-size: 1.75rem;
        margin-bottom: 0;
        flex-shrink: 0;
    }

    .benefit-card h4 {
        font-size: 0.95rem;
        margin-bottom: 0.25rem;
    }

    .benefit-card p {
        font-size: 0.85rem;
    }

    .action-buttons {
        flex-direction: column;
        gap: 0.75rem;
        padding: 1.25rem 0;
    }

    .btn {
        width: 100%;
        justify-content: center;
        padding: 0.85rem 1.5rem;
        font-size: 1rem;
    }
}</style>
<script>
    import { goto } from '$app/navigation';
    
    let { data } = $props();
    let { flexTermine, patientTermine, behandlungsarten, zahnaerzte, patientId } = data;
    
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const day = date.getDate();
        const month = date.toLocaleDateString('de-DE', { month: 'short' }).toUpperCase();
        return { day, month };
    };
    
    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString('de-DE', { hour: '2-digit', minute: '2-digit' });
    };
    
    const formatFullDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-DE', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    };
    
    const handleFlexTerminClick = (flexTermin) => {
        // Find matching patient termin with same behandlungsart
        const matchingTermin = patientTermine.find(
            t => t.behandlungsartId === flexTermin.behandlungsartId
        );
        
        if (matchingTermin) {
            goto(`/flextermine/${flexTermin.id}?oldTerminId=${matchingTermin.id}`);
        }
    };
    
    const getDiscount = (flexTerminDate) => {
        const now = new Date();
        const terminDate = new Date(flexTerminDate);
        const daysUntil = Math.floor((terminDate - now) / (1000 * 60 * 60 * 24));
        
        if (daysUntil <= 7) return 10;
        if (daysUntil <= 14) return 7;
        return 5;
    };
</script>

<div class="flex-termine-container">
    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={() => goto('/')}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
        <div class="header-content-flex">
            <div class="header-icon">
                <i class="bi bi-lightning-charge-fill"></i>
            </div>
            <div>
                <h1 class="page-title">Flexibler Termin Verfügbar!</h1>
                <p class="page-subtitle">Ein früherer Termin ist für Sie verfügbar und Sie sparen bis zu 10%!</p>
            </div>
        </div>
    </div>
    
    {#if flexTermine.length === 0}
        <!-- No Flex Termine Available -->
        <div class="empty-state">
            <div class="empty-icon">
                <i class="bi bi-calendar-x"></i>
            </div>
            <h3>Keine Flex-Termine verfügbar</h3>
            <p>Aktuell sind keine früheren Termine verfügbar, die zu Ihren gebuchten Terminen passen.</p>
            <p class="info-text">
                <i class="bi bi-info-circle"></i>
                Flex-Termine sind kurzfristig freigewordene Termine. Sie werden benachrichtigt, sobald ein passender Termin verfügbar ist.
            </p>
        </div>
    {:else}
        <!-- Info Banner -->
        <div class="info-banner">
            <div class="banner-icon">
                <i class="bi bi-gift-fill"></i>
            </div>
            <div class="banner-content">
                <h4>Da Sie den neuen Termin kurzfristig wahrnehmen, erhalten Sie 10% Rabatt auf die Behandlung!</h4>
                <p>Möchten Sie diese früheren Termine wahrnehmen?</p>
            </div>
        </div>
        
        <!-- Flex Termine List -->
        <div class="flex-termine-list">
            {#each flexTermine as flexTermin}
                {@const behandlung = behandlungsarten[flexTermin.behandlungsartId]}
                {@const zahnarzt = zahnaerzte[flexTermin.zahnarztId]}
                {@const dateInfo = formatDate(flexTermin.datum)}
                {@const discount = getDiscount(flexTermin.datum)}
                {@const matchingTermin = patientTermine.find(t => t.behandlungsartId === flexTermin.behandlungsartId)}
                
                <div class="flex-termin-card" 
                     role="button" 
                     tabindex="0" 
                     onclick={() => handleFlexTerminClick(flexTermin)}
                     onkeydown={(e) => e.key === 'Enter' && handleFlexTerminClick(flexTermin)}>
                    <!-- Date Badge -->
                    <div class="date-badge">
                        <div class="date-month">{dateInfo.month}</div>
                        <div class="date-day">{dateInfo.day}</div>
                    </div>
                    
                    <!-- Termin Info -->
                    <div class="termin-info">
                        <div class="termin-header">
                            <h3 class="behandlung-name">{behandlung?.name || 'Behandlung'}</h3>
                            <div class="discount-badge">
                                <i class="bi bi-percent"></i>
                                {discount}% Rabatt
                            </div>
                        </div>
                        
                        <div class="termin-details">
                            <div class="detail-item">
                                <i class="bi bi-clock"></i>
                                <span>{formatTime(flexTermin.datum)} Uhr</span>
                            </div>
                            
                            {#if zahnarzt}
                                <div class="detail-item">
                                    <i class="bi bi-person"></i>
                                    <span>{zahnarzt.name}</span>
                                </div>
                            {/if}
                            
                            <div class="detail-item">
                                <i class="bi bi-hourglass-split"></i>
                                <span>{flexTermin.dauerMinuten} Min.</span>
                            </div>
                        </div>
                        
                        {#if matchingTermin}
                            <div class="replacement-info">
                                <i class="bi bi-arrow-repeat"></i>
                                <span>Ersetzt Ihren Termin vom {formatFullDate(matchingTermin.datum)}</span>
                            </div>
                        {/if}
                    </div>
                    
                    <!-- Arrow Icon -->
                    <div class="arrow-icon">
                        <i class="bi bi-chevron-right"></i>
                    </div>
                </div>
            {/each}
        </div>
    {/if}
</div>


<style>
.flex-termine-container {
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

.header-content-flex {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    padding: 1rem 0;
}

.header-icon {
    font-size: 3rem;
        color: #FFB84D;
    animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {

    0%,
    100% {
        transform: scale(1);
    }

    50% {
        transform: scale(1.1);
    }
}

.page-title {
    font-size: 1.75rem;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    color: #004D40;
}

.page-subtitle {
    font-size: 1.1rem;
    margin: 0;
    color: #00695C;
}

.info-banner {
    display: flex;
    gap: 1rem;
    background: #E0F2F1;
    border: 2px solid #009688;
    border-radius: 12px;
    padding: 1.5rem;
    margin-bottom: 2rem;
}

.banner-icon {
    font-size: 2rem;
    color: #009688;
}

.banner-content h4 {
    margin: 0 0 0.5rem 0;
    color: #004D40;
    font-size: 1.1rem;
}

.banner-content p {
    margin: 0;
    color: #00695C;
}

.flex-termine-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.flex-termin-card {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    background: white;
    border: 2px solid #e0e0e0;
    border-radius: 16px;
    padding: 1.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
}

.flex-termin-card:hover {
    border-color: #009688;
    box-shadow: 0 4px 12px rgba(0, 150, 136, 0.15);
    transform: translateY(-2px);
}

.date-badge {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #009688 0%, #00796B 100%);
    color: white;
    border-radius: 12px;
    padding: 1rem;
    min-width: 80px;
    text-align: center;
}

.date-month {
    font-size: 0.85rem;
    font-weight: 600;
    text-transform: uppercase;
    opacity: 0.9;
}

.date-day {
    font-size: 2rem;
    font-weight: 700;
    line-height: 1;
    margin-top: 0.25rem;
}

.termin-info {
    flex: 1;
}

.termin-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0.75rem;
}

.behandlung-name {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: #333;
}

.discount-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.4rem;
    background: #FFB84D;
    color: white;
    padding: 0.4rem 0.8rem;
    border-radius: 20px;
    font-size: 0.9rem;
    font-weight: 600;
}

.termin-details {
    display: flex;
    flex-wrap: wrap;
    gap: 1.5rem;
    margin-bottom: 0.75rem;
}

.detail-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #666;
    font-size: 0.95rem;
}

.detail-item i {
    color: #009688;
}

.replacement-info {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #666;
    font-size: 0.9rem;
    padding-top: 0.75rem;
    border-top: 1px solid #e0e0e0;
}

.replacement-info i {
    color: #FFB84D;
}

.arrow-icon {
    font-size: 1.5rem;
    color: #009688;
    transition: transform 0.3s ease;
}

.flex-termin-card:hover .arrow-icon {
    transform: translateX(4px);
}

.empty-state {
    text-align: center;
    padding: 4rem 2rem;
    background: white;
    border-radius: 16px;
    border: 2px dashed #e0e0e0;
}

.empty-icon {
    font-size: 4rem;
    color: #ccc;
    margin-bottom: 1rem;
}

.empty-state h3 {
    color: #333;
    margin-bottom: 0.5rem;
}

.empty-state p {
    color: #666;
    margin-bottom: 1rem;
}

.info-text {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    background: #E0F2F1;
    color: #00695C;
    padding: 0.75rem 1rem;
    border-radius: 8px;
    font-size: 0.9rem;
}

.info-text i {
    color: #009688;
}

@media (max-width: 768px) {
    .flex-termine-container {
        padding: 1rem 0.75rem;
    }

    .page-header {
        margin-bottom: 1.25rem;
    }

    .back-btn {
        padding: 0.4rem 0.75rem;
        font-size: 0.9rem;
        margin-bottom: 0.75rem;
    }

    .header-content-flex {
        flex-direction: row;
        text-align: left;
        gap: 1rem;
    }

    .header-icon {
        font-size: 2rem;
    }

    .page-title {
        font-size: 1.25rem;
        margin: 0 0 0.25rem 0;
    }

    .page-subtitle {
        font-size: 0.9rem;
    }

    .info-banner {
        padding: 1rem;
        margin-bottom: 1.25rem;
    }

    .banner-icon {
        font-size: 1.5rem;
    }

    .banner-content h4 {
        font-size: 0.95rem;
        margin-bottom: 0.25rem;
    }

    .banner-content p {
        font-size: 0.85rem;
    }

    .flex-termine-list {
        gap: 0.75rem;
    }

    .flex-termin-card {
        flex-direction: row;
        align-items: center;
        padding: 1rem;
        gap: 1rem;
    }

    .date-badge {
        min-width: 60px;
        padding: 0.75rem;
        flex-shrink: 0;
    }

    .date-month {
        font-size: 0.75rem;
    }

    .date-day {
        font-size: 1.5rem;
    }

    .termin-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
        margin-bottom: 0.5rem;
    }

    .behandlung-name {
        font-size: 1rem;
    }

    .discount-badge {
        padding: 0.3rem 0.6rem;
        font-size: 0.8rem;
    }

    .termin-details {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0.5rem;
    }

    .detail-item {
        font-size: 0.85rem;
    }

    .replacement-info {
        font-size: 0.8rem;
        padding-top: 0.5rem;
        margin-top: 0.5rem;
    }

    .arrow-icon {
        display: block;
        font-size: 1.25rem;
    }

    .empty-state {
        padding: 2rem 1rem;
    }

    .empty-icon {
        font-size: 2.5rem;
        margin-bottom: 0.75rem;
    }

    .empty-state h3 {
        font-size: 1.1rem;
        margin-bottom: 0.5rem;
    }

    .empty-state p {
        font-size: 0.9rem;
        margin-bottom: 0.75rem;
    }

    .info-text {
        font-size: 0.85rem;
        padding: 0.6rem 0.8rem;
    }
}</style>
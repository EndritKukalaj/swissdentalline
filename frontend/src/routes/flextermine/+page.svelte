<script>
    import './styles.css';
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

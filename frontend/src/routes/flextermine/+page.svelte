<script>
    import { goto } from '$app/navigation';
    import { InfoBanner, EmptyState, FlexTerminCard } from '$lib';
    
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
        <EmptyState
            icon="calendar-x"
            title="Keine Flex-Termine verfügbar"
            message="Aktuell sind keine früheren Termine verfügbar, die zu Ihren gebuchten Terminen passen."
            variant="turquoise"
        >
            <p class="info-text">
                <i class="bi bi-info-circle"></i>
                Flex-Termine sind kurzfristig freigewordene Termine. Sie werden benachrichtigt, sobald ein passender Termin verfügbar ist.
            </p>
        </EmptyState>
    {:else}
        <!-- Info Banner -->
        <InfoBanner
            type="gift"
            variant="turquoise"
        >
            <h4 class="alert-title">Da Sie den neuen Termin kurzfristig wahrnehmen, erhalten Sie 10% Rabatt auf die Behandlung!</h4>
            <p>Möchten Sie diese früheren Termine wahrnehmen?</p>
        </InfoBanner>
        
        <!-- Flex Termine List -->
        <div class="flex-termine-list">
            {#each flexTermine as flexTermin}
                {@const behandlung = behandlungsarten[flexTermin.behandlungsartId]}
                {@const zahnarzt = zahnaerzte[flexTermin.zahnarztId]}
                {@const dateInfo = formatDate(flexTermin.datum)}
                {@const discount = getDiscount(flexTermin.datum)}
                {@const matchingTermin = patientTermine.find(t => t.behandlungsartId === flexTermin.behandlungsartId)}
                
                <FlexTerminCard
                    dateMonth={dateInfo.month}
                    dateDay={dateInfo.day}
                    behandlungName={behandlung?.name || 'Behandlung'}
                    discount={discount}
                    time={formatTime(flexTermin.datum)}
                    zahnarztName={zahnarzt?.name}
                    dauer={flexTermin.dauerMinuten}
                    replacementText={matchingTermin ? `Ersetzt Ihren Termin vom ${formatFullDate(matchingTermin.datum)}` : ''}
                    onClick={() => handleFlexTerminClick(flexTermin)}
                    variant="turquoise"
                />
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

.flex-termine-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
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

    .flex-termine-list {
        gap: 0.75rem;
    }



    .info-text {
        font-size: 0.85rem;
        padding: 0.6rem 0.8rem;
    }
}</style>
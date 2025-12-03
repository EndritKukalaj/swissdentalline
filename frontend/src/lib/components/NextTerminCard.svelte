<script>
    import { goto } from '$app/navigation';
    
    let { termin, variant = 'turquoise' } = $props();
    
    const handleClick = () => {
        goto(`/termine/${termin.id}`);
    };
    
    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString('de-CH', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    };
    
    const getDay = (dateString) => {
        const date = new Date(dateString);
        return date.getDate();
    };
    
    const getMonth = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-CH', { month: 'short' }).toUpperCase();
    };
</script>

<div class="next-termin-wrapper">
    <h3 class="next-termin-title">Nächster Termin</h3>
    
    <div class="next-termin-card {variant === 'purple' ? 'purple-variant' : ''}" role="button" tabindex="0" onclick={handleClick} onkeydown={(e) => e.key === 'Enter' && handleClick()}>
        <div class="card-accent"></div>
        
        <div class="card-content">
            <!-- First Row: Date Badge + Card Header -->
            <div class="header-row">
                <div class="date-badge">
                    <div class="day">{getDay(termin.datum)}</div>
                    <div class="month">{getMonth(termin.datum)}</div>
                </div>
                
                <div class="card-header">
                    <h5 class="treatment-name">
                        <i class="bi bi-clipboard2-pulse-fill"></i>
                        {termin.behandlungsartName || 'Termin'}
                    </h5>
                </div>
            </div>
            
            <!-- Second Row: Card Details -->
            <div class="card-details">
                <div class="detail-row">
                    <div class="detail-icon-wrapper">
                        <i class="bi bi-clock-fill"></i>
                    </div>
                    <span class="detail-text">{formatTime(termin.datum)} Uhr</span>
                </div>
                
                <div class="detail-row">
                    <div class="detail-icon-wrapper">
                        <i class="bi bi-hourglass-split"></i>
                    </div>
                    <span class="detail-text">{termin.dauerMinuten} Min.</span>
                </div>
                
                <div class="detail-row">
                    <div class="detail-icon-wrapper">
                        <i class="bi bi-wallet2"></i>
                    </div>
                    <span class="detail-text price">{termin.preis} CHF</span>
                </div>
            </div>

            <!-- Third Row: Zahnarzt/Patient & Praxis Info -->
            {#if termin.zahnarzt || termin.patient || termin.adresse}
            <div class="additional-info">
                {#if termin.zahnarzt}
                <div class="info-row">
                    <div class="info-icon-wrapper">
                        <i class="bi bi-person-badge-fill"></i>
                    </div>
                    <span class="info-text">{termin.zahnarzt.name || 'Zahnarzt'}</span>
                </div>
                {/if}
                
                {#if termin.patient}
                <div class="info-row">
                    <div class="info-icon-wrapper">
                        <i class="bi bi-person-fill"></i>
                    </div>
                    <span class="info-text">{termin.patient.name || 'Patient'}</span>
                </div>
                {/if}
                
                {#if termin.adresse}
                <div class="info-row">
                    <div class="info-icon-wrapper">
                        <i class="bi bi-geo-alt-fill"></i>
                    </div>
                    <div class="info-text-wrapper">
                        {#if termin.adresse.bezeichnung}
                        <span class="info-text praxis-name">{termin.adresse.bezeichnung}</span>
                        {/if}
                        <span class="info-text address">
                            {termin.adresse.strasse}, {termin.adresse.plz} {termin.adresse.ort}
                        </span>
                    </div>
                </div>
                {/if}
            </div>
            {/if}
        </div>
    </div>
</div>

<style>
/* NextTerminCard styles */
.next-termin-wrapper {
    margin-bottom: 2rem;
}

.next-termin-title {
    font-size: 1.75rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 1.25rem 0;
    text-align: left;
}

.next-termin-card {
    position: relative;
    background: linear-gradient(135deg, #ffffff 0%, var(--card-bg-end, #f0fffe) 100%);
    border-radius: 20px;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 8px 24px var(--card-shadow, rgba(0, 150, 136, 0.2));
    border: 2px solid var(--card-border, #009688);
    cursor: pointer;
}

.next-termin-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px var(--card-shadow-hover, rgba(0, 150, 136, 0.3));
}

.next-termin-card:focus {
    outline: 2px solid var(--card-border, #009688);
    outline-offset: 2px;
}

.next-termin-card.purple-variant {
    --card-bg-end: #F5EFFC;
    --card-shadow: rgba(171, 71, 188, 0.22);
    --card-shadow-hover: rgba(171, 71, 188, 0.3);
    --card-border: #8E24AA;
    --accent-gradient: linear-gradient(90deg, #B388FF 0%, #AB47BC 100%);
    --date-badge-bg: linear-gradient(135deg, #B388FF 0%, #AB47BC 100%);
    --date-badge-shadow: 0 6px 16px rgba(171, 71, 188, 0.35);
    --icon-color: #8E24AA;
    --detail-icon-bg: linear-gradient(135deg, #E9DDF6 0%, #DCCEF6 100%);
    --border-color: rgba(171, 71, 188, 0.18);
}

.card-accent {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 6px;
    background: var(--accent-gradient, linear-gradient(90deg, #009688 0%, #00bfa5 100%));
}

.card-content {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
    padding: 1.75rem;
    padding-top: 2rem;
}

.header-row {
    display: flex;
    align-items: center;
    gap: 1.25rem;
    width: 100%;
}

.date-badge {
    flex-shrink: 0;
    width: 80px;
    height: 80px;
    background: var(--date-badge-bg, linear-gradient(135deg, #009688 0%, #00968796 100%));
    border-radius: 16px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: white;
    box-shadow: var(--date-badge-shadow, 0 6px 16px rgba(0, 150, 136, 0.4));
}

.date-badge .day {
    font-size: 2rem;
    font-weight: 800;
    line-height: 1;
    margin-bottom: 0.125rem;
}

.date-badge .month {
    font-size: 0.875rem;
    font-weight: 600;
    letter-spacing: 0.5px;
    opacity: 0.95;
}

.card-header {
    flex: 1;
    display: flex;
    align-items: flex-start;
}

.treatment-name {
    font-size: 1.5rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.treatment-name i {
    color: var(--icon-color, #009688);
    font-size: 1.75rem;
}

.card-details {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    align-items: center;
    gap: 1.25rem;
    width: 100%;
}

.detail-row {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 0.5rem;
    flex-shrink: 0;
}

.detail-icon-wrapper {
    width: 36px;
    height: 36px;
    background: var(--detail-icon-bg, linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%));
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.detail-icon-wrapper i {
    font-size: 1rem;
    color: var(--icon-color, #009688);
}

.detail-text {
    font-size: 1.0625rem;
    font-weight: 600;
    color: #4a5568;
    white-space: nowrap;
    line-height: 1;
}

.detail-text.price {
    font-weight: 700;
    color: #2d3748;
    font-size: 1.125rem;
}

.additional-info {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    margin-top: 0.5rem;
    padding-top: 1rem;
    border-top: 2px solid var(--border-color, rgba(0, 150, 136, 0.1));
    width: 100%;
}

.info-row {
    display: flex;
    align-items: center;
    gap: 0.625rem;
}

.info-icon-wrapper {
    flex-shrink: 0;
    width: 32px;
    height: 32px;
    background: var(--detail-icon-bg, linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%));
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.info-icon-wrapper i {
    font-size: 0.9375rem;
    color: var(--icon-color, #009688);
}

.info-text-wrapper {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
}

.info-text {
    font-size: 0.9375rem;
    font-weight: 600;
    color: #4a5568;
    line-height: 1.5;
}

.info-text.praxis-name {
    font-weight: 700;
    color: #2d3748;
}

.info-text.address {
    font-size: 0.875rem;
    font-weight: 500;
    color: #718096;
}

@media (max-width: 640px) {
    .card-content {
        gap: 0.625rem;
        padding: 1.25rem;
    }

    .header-row {
        gap: 0.75rem;
    }

    .date-badge {
        width: 64px;
        height: 64px;
    }

    .date-badge .day {
        font-size: 1.5rem;
    }

    .date-badge .month {
        font-size: 0.6875rem;
    }

    .treatment-name {
        font-size: 1.125rem;
    }

    .treatment-name i {
        font-size: 1.25rem;
    }

    .card-details {
        flex-direction: row;
        gap: 0.5rem;
        flex-wrap: wrap;
        align-items: center;
    }

    .detail-row {
        flex-direction: row;
        gap: 0.3rem;
        flex-shrink: 0;
    }

    .detail-icon-wrapper {
        width: 24px;
        height: 24px;
    }

    .detail-icon-wrapper i {
        font-size: 0.6875rem;
    }

    .detail-text {
        font-size: 0.75rem;
    }

    .detail-text.price {
        font-size: 0.8125rem;
        font-weight: 700;
    }

    .additional-info {
        gap: 0.5rem;
        margin-top: 0.25rem;
        padding-top: 0.75rem;
    }

    .info-icon-wrapper {
        width: 24px;
        height: 24px;
    }

    .info-icon-wrapper i {
        font-size: 0.6875rem;
    }

    .info-text {
        font-size: 0.6875rem;
    }

    .info-text.address {
        font-size: 0.625rem;
    }
}
</style>
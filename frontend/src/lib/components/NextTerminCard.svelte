<script>
    import { goto } from '$app/navigation';
    import './NextTerminCard.css';
    
    let { termin, variant = 'turquoise' } = $props();
    
    const handleClick = () => {
        goto(`/termine/${termin.id}`);
    };
    
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-CH', { 
            day: '2-digit', 
            month: 'long', 
            year: 'numeric' 
        });
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


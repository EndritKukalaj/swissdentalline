<script>
    import { goto } from '$app/navigation';
    import './TerminCard.css';
    
    let { termin, variant = 'turquoise' } = $props();
    
    const handleClick = () => {
        goto(`/termine/${termin.id}`);
    };
    
    // Format date and time
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-CH', { 
            weekday: 'short',
            day: '2-digit', 
            month: 'short', 
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
    
    // Status color mapping
    const getStatusColor = (status) => {
        switch(status) {
            case 'GEBUCHT': return '#4caf50';
            case 'ABGESCHLOSSEN': return '#30B0C7';
            case 'VERFUEGBAR': return '#FFC107';
            default: return '#9e9e9e';
        }
    };
    
    const getStatusLabel = (status) => {
        switch(status) {
            case 'GEBUCHT': return 'Gebucht';
            case 'ABGESCHLOSSEN': return 'Abgeschlossen';
            case 'VERFUEGBAR': return 'Verfügbar';
            default: return status;
        }
    };
</script>

<div class="termin-card {variant === 'purple' ? 'purple-variant' : ''}" role="button" tabindex="0" onclick={handleClick} onkeydown={(e) => e.key === 'Enter' && handleClick()}>
    <div class="card-accent" style="background: {getStatusColor(termin.status)}"></div>
    
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
                <span class="status-badge" style="background: {getStatusColor(termin.status)}">
                    {getStatusLabel(termin.status)}
                </span>
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
            
            <!-- Waitlist Indicator -->
            {#if termin.wartelisteAktiv}
                <div class="waitlist-indicator">
                    <i class="bi bi-list-check"></i>
                    Warteliste aktiv
                </div>
            {/if}
        </div>
    </div>
</div>


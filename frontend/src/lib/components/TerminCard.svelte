<script>
    let { termin } = $props();
    
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

<div class="termin-card">
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

<style>
    .termin-card {
        position: relative;
        background: linear-gradient(135deg, #ffffff 0%, #f0fffe 100%);
        border-radius: 20px;
        overflow: hidden;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        border: 1px solid rgba(0, 0, 0, 0.05);
    }
    
    .termin-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
    }
    
    .card-accent {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
    }
    
    .card-content {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        padding: 1.5rem;
        padding-top: 1.75rem;
    }
    
    .header-row {
        display: flex;
        align-items: center;
        gap: 1.25rem;
    }
    
    .date-badge {
        flex-shrink: 0;
        width: 70px;
        height: 70px;
        background: linear-gradient(135deg, #009688 0%, #00968796 100%);
        border-radius: 16px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: white;
        box-shadow: 0 4px 12px rgba(0,150,136,0.3);
    }
    
    .date-badge .day {
        font-size: 1.75rem;
        font-weight: 800;
        line-height: 1;
        margin-bottom: 0.125rem;
    }
    
    .date-badge .month {
        font-size: 0.75rem;
        font-weight: 600;
        letter-spacing: 0.5px;
        opacity: 0.95;
    }
    
    .card-header {
        flex: 1;
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: 0.75rem;
        padding-top: 1rem;
        padding-bottom: 1rem;
    }
    
    .treatment-name {
        font-size: 1.25rem;
        font-weight: 700;
        color: #1a202c;
        margin: 0;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        flex: 1 1 auto;
        min-width: 0;
    }
    
    .treatment-name i {
        color: #009688;
        font-size: 1.375rem;
    }
    
    .status-badge {
        padding: 0.375rem 0.875rem;
        border-radius: 20px;
        font-size: 0.75rem;
        font-weight: 600;
        color: white;
        white-space: nowrap;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        flex-shrink: 0;
    }
    
    .card-details {
        display: flex;
        flex-wrap: wrap;
        gap: 1rem;
    }
    
    .detail-row {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .detail-icon-wrapper {
        width: 32px;
        height: 32px;
        background: linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .detail-icon-wrapper i {
        font-size: 0.875rem;
        color: #009688;
    }
    
    .detail-text {
        font-size: 0.9375rem;
        font-weight: 500;
        color: #4a5568;
    }
    
    .detail-text.price {
        font-weight: 700;
        color: #2d3748;
        font-size: 1rem;
    }
    
    .waitlist-indicator {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
        border-radius: 12px;
        font-size: 0.875rem;
        font-weight: 600;
        color: #92400e;
        margin-left: auto;
    }
    
    .waitlist-indicator i {
        font-size: 1rem;
    }
    
    @media (max-width: 640px) {
        .waitlist-indicator {
            margin-left: 0;
        }
        
        .card-content {
            gap: 0.75rem;
            padding: 1.25rem;
        }
        
        .header-row {
            gap: 0.875rem;
        }
        
        .date-badge {
            width: 60px;
            height: 60px;
        }
        
        .date-badge .day {
            font-size: 1.5rem;
        }
        
        .date-badge .month {
            font-size: 0.7rem;
        }
        
        .card-header {
            gap: 0.75rem;
        }
        
        .treatment-name {
            font-size: 1.125rem;
        }
        
        .treatment-name i {
            font-size: 1.25rem;
        }
        
        .status-badge {
            padding: 0.3rem 0.75rem;
            font-size: 0.7rem;
        }
        
        .card-details {
            gap: 0.75rem;
        }
        
        .detail-icon-wrapper {
            width: 28px;
            height: 28px;
        }
        
        .detail-icon-wrapper i {
            font-size: 0.8rem;
        }
        
        .detail-text {
            font-size: 0.875rem;
        }
        
        .detail-text.price {
            font-size: 0.9375rem;
        }
        
        .waitlist-indicator {
            padding: 0.4rem 0.875rem;
            font-size: 0.8125rem;
        }
        
        .waitlist-indicator i {
            font-size: 0.9rem;
        }
    }
</style>

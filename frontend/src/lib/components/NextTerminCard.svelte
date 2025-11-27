<script>
    import { goto } from '$app/navigation';
    
    let { termin } = $props();
    
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
    
    <div class="next-termin-card" role="button" tabindex="0" onclick={handleClick} onkeydown={(e) => e.key === 'Enter' && handleClick()}>
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
        </div>
    </div>
</div>

<style>
    .next-termin-wrapper {
        margin-bottom: 2rem;
    }
    
    .next-termin-title {
        font-size: 1.75rem;
        font-weight: 700;
        color: #1a202c;
        margin: 0 0 1.25rem 0;
        text-align: center;
    }
    
    .next-termin-card {
        position: relative;
        background: linear-gradient(135deg, #ffffff 0%, #f0fffe 100%);
        border-radius: 20px;
        overflow: hidden;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        box-shadow: 0 8px 24px rgba(0, 150, 136, 0.2);
        border: 2px solid #009688;
        cursor: pointer;
    }
    
    .next-termin-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 12px 32px rgba(0, 150, 136, 0.3);
    }
    
    .next-termin-card:focus {
        outline: 2px solid #009688;
        outline-offset: 2px;
    }
    
    .card-accent {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 6px;
        background: linear-gradient(90deg, #009688 0%, #00bfa5 100%);
    }
    
    .card-content {
        display: flex;
        flex-direction: column;
        gap: 1rem;
        padding: 1.75rem;
        padding-top: 2rem;
    }
    
    .header-row {
        display: flex;
        align-items: center;
        gap: 1.25rem;
    }
    
    .date-badge {
        flex-shrink: 0;
        width: 80px;
        height: 80px;
        background: linear-gradient(135deg, #009688 0%, #00968796 100%);
        border-radius: 16px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: white;
        box-shadow: 0 6px 16px rgba(0,150,136,0.4);
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
        align-items: center;
        padding-top: 1rem;
        padding-bottom: 1rem;
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
        color: #009688;
        font-size: 1.75rem;
    }
    
    .card-details {
        display: flex;
        flex-wrap: wrap;
        gap: 1.25rem;
    }
    
    .detail-row {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }
    
    .detail-icon-wrapper {
        width: 36px;
        height: 36px;
        background: linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%);
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    
    .detail-icon-wrapper i {
        font-size: 1rem;
        color: #009688;
    }
    
    .detail-text {
        font-size: 1.0625rem;
        font-weight: 600;
        color: #4a5568;
    }
    
    .detail-text.price {
        font-weight: 700;
        color: #2d3748;
        font-size: 1.125rem;
    }
    
    @media (max-width: 640px) {
        .card-content {
            gap: 0.75rem;
            padding: 1.5rem;
        }
        
        .header-row {
            gap: 1rem;
        }
        
        .date-badge {
            width: 70px;
            height: 70px;
        }
        
        .date-badge .day {
            font-size: 1.75rem;
        }
        
        .date-badge .month {
            font-size: 0.75rem;
        }
        
        .treatment-name {
            font-size: 1.25rem;
        }
        
        .treatment-name i {
            font-size: 1.5rem;
        }
        
        .card-details {
            gap: 1rem;
        }
        
        .detail-icon-wrapper {
            width: 32px;
            height: 32px;
        }
        
        .detail-icon-wrapper i {
            font-size: 0.9rem;
        }
        
        .detail-text {
            font-size: 0.875rem;
        }
        
        .detail-text.price {
            font-size: 0.9375rem;
        }
    }
</style>

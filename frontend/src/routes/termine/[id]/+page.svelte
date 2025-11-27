<script>
    import { goto } from '$app/navigation';
    
    let { data } = $props();
    let { termin, behandlungsart, zahnarzt, adresse, patient, userRole } = data;
    
    // Format date
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-CH', { 
            weekday: 'long', 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    };
    
    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString('de-CH', { 
            hour: '2-digit', 
            minute: '2-digit' 
        });
    };
    
    const formatShortDate = (dateString) => {
        const date = new Date(dateString);
        return {
            day: date.getDate(),
            month: date.toLocaleDateString('de-CH', { month: 'short' }).toUpperCase()
        };
    };
    
    // Status badge color
    const getStatusColor = (status) => {
        switch(status) {
            case 'GEBUCHT': return '#009688';
            case 'ABGESCHLOSSEN': return '#4caf50';
            case 'ABGESAGT': return '#f44336';
            default: return '#64748b';
        }
    };
    
    const getStatusText = (status) => {
        switch(status) {
            case 'GEBUCHT': return 'Gebucht';
            case 'ABGESCHLOSSEN': return 'Abgeschlossen';
            case 'ABGESAGT': return 'Abgesagt';
            default: return status;
        }
    };
    
    const dateInfo = formatShortDate(termin.datum);
</script>

<div class="details-container">
    <!-- Back Button -->
    <button class="back-btn" onclick={() => goto('/')}>
        <i class="bi bi-arrow-left"></i>
        Zurück zur Übersicht
    </button>
    
    <!-- Header Card -->
    <div class="header-card">
        <div class="header-content">
            <div class="date-badge-large">
                <div class="day">{dateInfo.day}</div>
                <div class="month">{dateInfo.month}</div>
            </div>
            
            <div class="header-info">
                <h1 class="termin-title">{behandlungsart?.name || 'Behandlung'}</h1>
                <div class="header-meta">
                    <span class="status-badge" style="background-color: {getStatusColor(termin.status)}">
                        {getStatusText(termin.status)}
                    </span>
                    {#if termin.wartelisteAktiv || termin.warteliste_aktiv}
                        <span class="warteliste-badge">
                            <i class="bi bi-clock-history"></i>
                            Auf Warteliste
                        </span>
                    {/if}
                </div>
                <p class="termin-date">{formatDate(termin.datum)}</p>
            </div>
        </div>
    </div>
    
    <!-- Main Details Grid -->
    <div class="details-grid">
        <!-- Termindetails Card -->
        <div class="detail-card">
            <div class="card-header">
                <i class="bi bi-calendar-event"></i>
                <h2>Termindetails</h2>
            </div>
            <div class="card-content">
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-clock"></i>
                        Uhrzeit
                    </div>
                    <div class="detail-value">{formatTime(termin.datum)} Uhr</div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-hourglass-split"></i>
                        Dauer
                    </div>
                    <div class="detail-value">{termin.dauerMinuten || termin.dauer_minuten} Minuten</div>
                </div>
                <div class="detail-row">
                    <div class="detail-label">
                        <i class="bi bi-cash-coin"></i>
                        Preis
                    </div>
                    <div class="detail-value highlight">CHF {termin.preis?.toFixed(2)}</div>
                </div>
            </div>
        </div>
        
        <!-- Behandlungsart Card -->
        {#if behandlungsart}
            <div class="detail-card">
                <div class="card-header">
                    <i class="bi bi-bandaid"></i>
                    <h2>Behandlung</h2>
                </div>
                <div class="card-content">
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-tag"></i>
                            Behandlungsart
                        </div>
                        <div class="detail-value">{behandlungsart.name}</div>
                    </div>
                    {#if behandlungsart.beschreibung}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-info-circle"></i>
                                Beschreibung
                            </div>
                            <div class="detail-value">{behandlungsart.beschreibung}</div>
                        </div>
                    {/if}
                </div>
            </div>
        {/if}
        
        <!-- Zahnarzt & Praxis Card (Patient View) -->
        {#if zahnarzt && userRole === 'Patient'}
            <div class="detail-card">
                <div class="card-header">
                    <i class="bi bi-person-badge"></i>
                    <h2>Zahnarzt & Praxis</h2>
                </div>
                <div class="card-content">
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-person"></i>
                            Name
                        </div>
                        <div class="detail-value">
                            {zahnarzt.name || `${zahnarzt.vorname || ''} ${zahnarzt.nachname || ''}`.trim()}
                        </div>
                    </div>
                    {#if zahnarzt.spezialisierung}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-award"></i>
                                Spezialisierung
                            </div>
                            <div class="detail-value">{zahnarzt.spezialisierung}</div>
                        </div>
                    {/if}
                    {#if zahnarzt.email}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-envelope"></i>
                                E-Mail
                            </div>
                            <div class="detail-value">
                                <a href="mailto:{zahnarzt.email}">{zahnarzt.email}</a>
                            </div>
                        </div>
                    {/if}
                    
                    {#if adresse}
                        <div class="section-divider"></div>
                        
                        {#if adresse.bezeichnung}
                            <div class="detail-row">
                                <div class="detail-label">
                                    <i class="bi bi-hospital"></i>
                                    Praxis
                                </div>
                                <div class="detail-value">{adresse.bezeichnung}</div>
                            </div>
                        {/if}
                        
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-building"></i>
                                Adresse
                            </div>
                            <div class="detail-value">
                                {adresse.strasse} {adresse.hausnummer}<br>
                                {adresse.plz} {adresse.ort}
                            </div>
                        </div>
                        {#if adresse.telefon}
                            <div class="detail-row">
                                <div class="detail-label">
                                    <i class="bi bi-telephone"></i>
                                    Telefon
                                </div>
                                <div class="detail-value">
                                    <a href="tel:{adresse.telefon}">{adresse.telefon}</a>
                                </div>
                            </div>
                        {/if}
                    {/if}
                </div>
            </div>
        {/if}
        
        <!-- Patient Card (Zahnarzt View) -->
        {#if patient && userRole === 'Zahnarzt'}
            <div class="detail-card">
                <div class="card-header">
                    <i class="bi bi-person-fill"></i>
                    <h2>Patient</h2>
                </div>
                <div class="card-content">
                    <div class="detail-row">
                        <div class="detail-label">
                            <i class="bi bi-person"></i>
                            Name
                        </div>
                        <div class="detail-value">
                            {patient.name || `${patient.vorname || ''} ${patient.nachname || ''}`.trim()}
                        </div>
                    </div>
                    {#if patient.email}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-envelope"></i>
                                E-Mail
                            </div>
                            <div class="detail-value">
                                <a href="mailto:{patient.email}">{patient.email}</a>
                            </div>
                        </div>
                    {/if}
                    {#if patient.telefonnummer}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-telephone"></i>
                                Telefon
                            </div>
                            <div class="detail-value">
                                <a href="tel:{patient.telefonnummer}">{patient.telefonnummer}</a>
                            </div>
                        </div>
                    {/if}
                    {#if patient.geburtsdatum}
                        <div class="detail-row">
                            <div class="detail-label">
                                <i class="bi bi-calendar"></i>
                                Geburtsdatum
                            </div>
                            <div class="detail-value">
                                {new Date(patient.geburtsdatum).toLocaleDateString('de-CH', { 
                                    day: '2-digit', 
                                    month: 'long', 
                                    year: 'numeric' 
                                })}
                            </div>
                        </div>
                    {/if}
                </div>
            </div>
        {/if}
    </div>
    
    <!-- Action Buttons -->
    <div class="action-section">
        {#if termin.status === 'GEBUCHT'}
            <button class="action-btn cancel-btn" disabled>
                <i class="bi bi-x-circle"></i>
                Termin stornieren
                <span class="coming-soon">Bald verfügbar</span>
            </button>
        {/if}
        
        {#if termin.status === 'ABGESCHLOSSEN'}
            <button class="action-btn review-btn" disabled>
                <i class="bi bi-star"></i>
                Zahnarzt bewerten
                <span class="coming-soon">Bald verfügbar</span>
            </button>
        {/if}
    </div>
</div>

<style>
    .details-container {
        padding: 2rem;
        max-width: 1200px;
        margin: 0 auto;
    }
    
    .back-btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.25rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: #4a5568;
        background: white;
        border: 2px solid #e2e8f0;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;
        margin-bottom: 1.5rem;
    }
    
    .back-btn:hover {
        border-color: #009688;
        color: #009688;
        background: #f0fffe;
    }
    
    .header-card {
        background: linear-gradient(135deg, #ffffff 0%, #f0fffe 100%);
        border-radius: 20px;
        padding: 2rem;
        margin-bottom: 2rem;
        box-shadow: 0 8px 24px rgba(0, 150, 136, 0.15);
        border: 2px solid rgba(0, 150, 136, 0.1);
        border-top: 6px solid;
        border-image: linear-gradient(90deg, #009688 0%, #00bfa5 100%) 1;
    }
    
    .header-content {
        display: flex;
        gap: 2rem;
        align-items: flex-start;
    }
    
    .date-badge-large {
        background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
        border-radius: 16px;
        padding: 1.5rem;
        text-align: center;
        color: white;
        box-shadow: 0 8px 16px rgba(0, 150, 136, 0.3);
        min-width: 120px;
        flex-shrink: 0;
    }
    
    .date-badge-large .day {
        font-size: 3rem;
        font-weight: 700;
        line-height: 1;
        margin-bottom: 0.25rem;
    }
    
    .date-badge-large .month {
        font-size: 1rem;
        font-weight: 600;
        letter-spacing: 0.5px;
        opacity: 0.95;
    }
    
    .header-info {
        flex: 1;
    }
    
    .termin-title {
        font-size: 2rem;
        font-weight: 700;
        color: #1a202c;
        margin: 0 0 0.75rem 0;
    }
    
    .header-meta {
        display: flex;
        gap: 0.75rem;
        margin-bottom: 0.75rem;
        flex-wrap: wrap;
    }
    
    .status-badge {
        padding: 0.375rem 0.875rem;
        border-radius: 6px;
        color: white;
        font-size: 0.875rem;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }
    
    .warteliste-badge {
        padding: 0.375rem 0.875rem;
        border-radius: 6px;
        background: linear-gradient(135deg, #FFC107 0%, #FFB300 100%);
        color: white;
        font-size: 0.875rem;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 0.375rem;
    }
    
    .termin-date {
        font-size: 1.125rem;
        color: #4a5568;
        margin: 0;
    }
    
    .details-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }
    
    .detail-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        border: 1px solid #e2e8f0;
        transition: all 0.3s ease;
    }
    
    .detail-card:hover {
        box-shadow: 0 8px 24px rgba(0, 150, 136, 0.12);
        transform: translateY(-2px);
    }
    
    .card-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 1.25rem;
        padding-bottom: 0.75rem;
        border-bottom: 2px solid #f0fffe;
    }
    
    .card-header i {
        font-size: 1.5rem;
        color: #009688;
    }
    
    .card-header h2 {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1a202c;
        margin: 0;
    }
    
    .card-content {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }
    
    .section-divider {
        height: 2px;
        background: linear-gradient(90deg, #f0fffe 0%, #009688 50%, #f0fffe 100%);
        margin: 0.5rem 0;
        border-radius: 2px;
    }
    
    .detail-row {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 1rem;
    }
    
    .detail-label {
        font-size: 0.875rem;
        font-weight: 600;
        color: #64748b;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        flex-shrink: 0;
    }
    
    .detail-label i {
        font-size: 1rem;
        color: #009688;
    }
    
    .detail-value {
        font-size: 1rem;
        color: #1a202c;
        font-weight: 500;
        text-align: right;
    }
    
    .detail-value.highlight {
        color: #009688;
        font-weight: 700;
        font-size: 1.125rem;
    }
    
    .detail-value a {
        color: #009688;
        text-decoration: none;
        transition: color 0.2s ease;
    }
    
    .detail-value a:hover {
        color: #00bfa5;
        text-decoration: underline;
    }
    
    .action-section {
        display: flex;
        gap: 1rem;
        flex-wrap: wrap;
        justify-content: center;
        margin-top: 2rem;
        padding-top: 2rem;
        border-top: 2px solid #f0fffe;
    }
    
    .action-btn {
        padding: 1rem 2rem;
        font-size: 1rem;
        font-weight: 600;
        border-radius: 12px;
        border: none;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        gap: 0.75rem;
        position: relative;
    }
    
    .action-btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }
    
    .cancel-btn {
        background: linear-gradient(135deg, #f44336 0%, #e53935 100%);
        color: white;
        box-shadow: 0 4px 12px rgba(244, 67, 54, 0.3);
    }
    
    .cancel-btn:hover:not(:disabled) {
        box-shadow: 0 6px 20px rgba(244, 67, 54, 0.4);
        transform: translateY(-2px);
    }
    
    .review-btn {
        background: linear-gradient(135deg, #FFC107 0%, #FFB300 100%);
        color: white;
        box-shadow: 0 4px 12px rgba(255, 193, 7, 0.3);
    }
    
    .review-btn:hover:not(:disabled) {
        box-shadow: 0 6px 20px rgba(255, 193, 7, 0.4);
        transform: translateY(-2px);
    }
    
    .coming-soon {
        position: absolute;
        top: -0.5rem;
        right: -0.5rem;
        background: #64748b;
        color: white;
        font-size: 0.625rem;
        padding: 0.25rem 0.5rem;
        border-radius: 4px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }
    
    @media (max-width: 768px) {
        .details-container {
            padding: 1.5rem;
        }
        
        .header-content {
            flex-direction: column;
            gap: 1.5rem;
        }
        
        .date-badge-large {
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 1rem;
            padding: 1rem 1.5rem;
        }
        
        .date-badge-large .day {
            font-size: 2.5rem;
        }
        
        .termin-title {
            font-size: 1.5rem;
        }
        
        .details-grid {
            grid-template-columns: 1fr;
        }
        
        .detail-row {
            flex-direction: column;
            align-items: flex-start;
        }
        
        .detail-value {
            text-align: left;
        }
        
        .action-section {
            flex-direction: column;
        }
        
        .action-btn {
            width: 100%;
            justify-content: center;
        }
    }
</style>

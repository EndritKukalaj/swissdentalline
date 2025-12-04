<script>
    import './styles.css';
    import { goto } from '$app/navigation';
    import BookingProgressBar from '$lib/components/BookingProgressBar.svelte';
    
    let { data } = $props();
    let { termin, behandlungsart } = data;
    
    const handleBack = () => {
        goto(`/buchen/zahnarzt?terminId=${termin.id}`);
    };
    
    const handleContinue = (warteliste) => {
        goto(`/buchen/uebersicht?terminId=${termin.id}&warteliste=${warteliste}`);
    };
    
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        return date.toLocaleDateString('de-DE', options);
    };
    
    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString('de-DE', { hour: '2-digit', minute: '2-digit' });
    };
</script>

<div class="buchen-container">
    <BookingProgressBar currentStep={4} />
    
    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={handleBack}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
        <h1 class="page-title">Warteliste</h1>
        <p class="page-subtitle">Möchten Sie sich für einen früheren Termin auf die Warteliste setzen?</p>
    </div>
    
    <!-- Main Content -->
    <div class="content-container">
        <!-- Waitlist Info Card -->
        <div class="info-card">
            <div class="card-icon-header">
                <div class="icon-circle">
                    <i class="bi bi-list-check"></i>
                </div>
                <div>
                    <h4>Wartelisteneintrag</h4>
                    <p class="card-subtitle">Profitieren Sie von kurzfristigen Terminabsagen</p>
                </div>
            </div>
            
            <div class="card-body">
                <div class="info-section">
                    <h3>Was bedeutet das?</h3>
                    <p>Wenn Sie sich auf die Warteliste setzen, werden Sie automatisch benachrichtigt, falls ein früherer Termin durch eine Absage verfügbar wird.</p>
                </div>
                
                <div class="benefits-list">
                    <div class="benefit-item">
                        <div class="benefit-icon">
                            <i class="bi bi-check-circle-fill"></i>
                        </div>
                        <div class="benefit-text">
                            <h4>Frühere Termine möglich</h4>
                            <p>Erhalten Sie Zugriff auf kurzfristig freigewordene Termine</p>
                        </div>
                    </div>
                    
                    <div class="benefit-item">
                        <div class="benefit-icon">
                            <i class="bi bi-bell-fill"></i>
                        </div>
                        <div class="benefit-text">
                            <h4>Automatische Benachrichtigung</h4>
                            <p>Wir informieren Sie sofort, wenn ein Termin frei wird</p>
                        </div>
                    </div>
                    
                    <div class="benefit-item">
                        <div class="benefit-icon">
                            <i class="bi bi-x-circle-fill"></i>
                        </div>
                        <div class="benefit-text">
                            <h4>Jederzeit kündbar</h4>
                            <p>Sie können sich jederzeit von der Warteliste abmelden</p>
                        </div>
                    </div>
                </div>
                
                <div class="info-note">
                    <i class="bi bi-info-circle-fill"></i>
                    <p>Ihr aktueller Termin bleibt in jedem Fall bestehen. Die Warteliste ist ein zusätzlicher Service.</p>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Action Buttons -->
    <div class="action-buttons">
        <button class="btn-secondary" onclick={() => handleContinue(false)}>
            <i class="bi bi-x-circle"></i>
            Nein, danke
        </button>
        
        <button class="btn-primary" onclick={() => handleContinue(true)}>
            <i class="bi bi-check-circle-fill"></i>
            Ja, auf Warteliste setzen
        </button>
    </div>
</div>

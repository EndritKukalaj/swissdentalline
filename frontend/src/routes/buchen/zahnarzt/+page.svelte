<script>
    import './styles.css';
    import { goto } from "$app/navigation";
    import BookingProgressBar from "$lib/components/BookingProgressBar.svelte";

    let { data } = $props();
    let { termin, zahnarzt, behandlungsart, praxis } = data;

    console.log("Page data:", { termin, zahnarzt, behandlungsart, praxis });

    const handleContinue = () => {
        goto(`/buchen/warteliste?terminId=${termin.id}`);
    };

    const handleBack = () => {
        goto(`/buchen/termin?behandlungsartId=${termin.behandlungsartId}`);
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const options = {
            weekday: "long",
            year: "numeric",
            month: "long",
            day: "numeric",
        };
        return date.toLocaleDateString("de-DE", options);
    };

    const formatTime = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleTimeString("de-DE", {
            hour: "2-digit",
            minute: "2-digit",
        });
    };

    const getFullAddress = (praxis) => {
        if (!praxis) return "Adresse nicht verfügbar";
        return `${praxis.strasse || ""}, ${praxis.plz || ""} ${praxis.ort || ""}`.trim();
    };
</script>

<div class="buchen-container">
    <BookingProgressBar currentStep={3} />

    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={handleBack}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
        <h1 class="page-title">Ihr Zahnarzt</h1>
        <p class="page-subtitle">
            Überprüfen Sie die Details Ihres ausgewählten Zahnarzt
        </p>
    </div>

    <!-- Hero Card with Selected Appointment -->
    <div class="hero-card">
        <div class="hero-pattern"></div>
        <div class="hero-content">
            <div class="appointment-icon-large">
                <i class="bi bi-calendar-check-fill"></i>
            </div>
            <div class="appointment-summary">
                <h2>{behandlungsart?.name || "Behandlung"}</h2>
                <div class="appointment-meta">
                    <div class="meta-item">
                        <i class="bi bi-calendar3"></i>
                        <span>{formatDate(termin.datum)}</span>
                    </div>
                    <div class="meta-divider"></div>
                    <div class="meta-item">
                        <i class="bi bi-clock"></i>
                        <span>{formatTime(termin.datum)} Uhr</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Main Content Grid -->
    <div class="content-grid">
        <!-- Zahnarzt Card -->
        <div class="detail-card zahnarzt-card">
            <div class="card-icon-header">
                <div class="icon-circle">
                    <i class="bi bi-person-circle"></i>
                </div>
                <div>
                    <h2>{zahnarzt?.name}</h2>
                    <p class="card-subtitle">Behandelnder Arzt</p>
                </div>
            </div>

            {#if praxis}
                <div class="card-body">
                    <div class="detail-section">
                        <div class="section-header">
                            <i class="bi bi-cash-coin"></i>
                            <span>Behandlungskosten</span>
                        </div>
                        <p class="section-value price-value">
                            {#if termin?.preis}
                                CHF {termin.preis.toFixed(2)}
                            {:else}
                                Preis auf Anfrage
                            {/if}
                        </p>
                    </div>

                    <div class="detail-section">
                        <div class="section-header">
                            <i class="bi bi-building"></i>
                            <span>Zahnarztpraxis</span>
                        </div>
                        <p class="section-value">{praxis.bezeichnung}</p>
                    </div>

                    <div class="detail-section">
                        <div class="section-header">
                            <i class="bi bi-geo-alt-fill"></i>
                            <span>Praxisadresse</span>
                        </div>
                        <p class="section-value">{getFullAddress(praxis)}</p>
                    </div>
                </div>
            {/if}
        </div>
    </div>

    <!-- Action Buttons -->
    <div class="action-buttons">
        <button class="btn-secondary" onclick={handleBack}>
            <i class="bi bi-arrow-left"></i>
            Anderen Termin wählen
        </button>
        <button class="btn-primary" onclick={handleContinue}>
            Termin bestätigen
            <i class="bi bi-arrow-right-circle-fill"></i>
        </button>
    </div>
</div>
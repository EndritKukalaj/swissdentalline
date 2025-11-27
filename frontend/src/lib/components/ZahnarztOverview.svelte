<script>
    import TerminCard from "./TerminCard.svelte";
    import StatCard from "./StatCard.svelte";
    import NextTerminCard from "./NextTerminCard.svelte";

    let { termine = [], nextTermin = null, stats = {} } = $props();

    // Search functionality
    let searchQuery = $state("");

    // Filter functionality
    let activeStatusFilter = $state("ALLE");
    let showOnlyFreieSlots = $state(false);

    // Filter termine
    let filteredTermine = $derived(() => {
        let result = termine;

        // Apply status filter
        if (activeStatusFilter !== "ALLE") {
            result = result.filter(
                (termin) => termin.status === activeStatusFilter,
            );
        }

        // Apply freie slots filter
        if (showOnlyFreieSlots) {
            result = result.filter(
                (termin) => termin.status === "FREI" && !termin.patientId,
            );
        }

        // Apply search query
        if (searchQuery.trim()) {
            const query = searchQuery.toLowerCase().trim();
            result = result.filter((termin) => {
                const nameMatch = termin.behandlungsartName
                    ?.toLowerCase()
                    .includes(query);
                const date = new Date(termin.datum);
                const dateString = date.toLocaleDateString("de-CH");
                const dateMatch = dateString.includes(query);
                const timeString = date.toLocaleTimeString("de-CH", {
                    hour: "2-digit",
                    minute: "2-digit",
                });
                const timeMatch = timeString.includes(query);
                const duration =
                    termin.dauerMinuten || termin.dauer_minuten || 0;
                const durationString = `${duration}`;
                const durationMatch =
                    durationString.includes(query) ||
                    `${duration} min`.includes(query);
                const priceString = `${termin.preis}`;
                const priceMatch =
                    priceString.includes(query) ||
                    `chf ${termin.preis}`.toLowerCase().includes(query);

                return (
                    nameMatch ||
                    dateMatch ||
                    timeMatch ||
                    durationMatch ||
                    priceMatch
                );
            });
        }

        return result;
    });
</script>

<div class="zahnarzt-dashboard">
    <h1 class="dashboard-title">Terminübersicht</h1>

    <!-- Stats Section -->
    <div class="stats-grid">
        <StatCard
            icon="bi-calendar-check"
            title="Geplante Termine"
            value={stats.geplanteTermine || 0}
            variant="purple"
        />
        <StatCard
            icon="bi-calendar-plus"
            title="Freie Slots"
            value={stats.freieSlots || 0}
            variant="purple"
        />
    </div>

    <!-- Next Appointment Section -->
    {#if nextTermin}
        <div class="next-termin-section">
            <NextTerminCard termin={nextTermin} variant="purple" />
        </div>
    {/if}

    <!-- Additional Stats Row -->
    <h2 class="details-summary-title">Weitere Details</h2>
    <div class="stats-grid secondary-stats">
        <StatCard
            icon="bi-x-circle"
            title="Abgesagte Termine"
            value={stats.abgesagteTermine || 0}
            variant="purple"
        />
        <StatCard
            icon="bi-clock-history"
            title="Warteliste verfügbar"
            value={stats.wartelisteVerfuegbar || 0}
            variant="purple"
        />
        <StatCard
            icon="bi-cash-coin"
            title="Monatliche Einnahmen"
            value={`${stats.monatlicheEinnahmen || 0} CHF`}
            variant="purple"
        />
    </div>

    <!-- Appointments List -->
    <div class="termine-section">
        <div class="section-header">
            <h2 class="section-title">Alle Termine</h2>

            <!-- Filter Buttons -->
            <div class="filter-section">
                <div class="filter-group">
                    <button
                        class="filter-btn purple-theme {activeStatusFilter ===
                        'ALLE'
                            ? 'active'
                            : ''}"
                        onclick={() => (activeStatusFilter = "ALLE")}
                    >
                        Alle
                    </button>
                    <button
                        class="filter-btn purple-theme {activeStatusFilter ===
                        'GEBUCHT'
                            ? 'active'
                            : ''}"
                        onclick={() => (activeStatusFilter = "GEBUCHT")}
                    >
                        <i class="bi bi-calendar-check"></i> Gebucht
                    </button>
                    <button
                        class="filter-btn purple-theme {activeStatusFilter ===
                        'FREI'
                            ? 'active'
                            : ''}"
                        onclick={() => (activeStatusFilter = "FREI")}
                    >
                        <i class="bi bi-calendar-plus"></i> Frei
                    </button>
                    <button
                        class="filter-btn purple-theme {activeStatusFilter ===
                        'ABGESCHLOSSEN'
                            ? 'active'
                            : ''}"
                        onclick={() => (activeStatusFilter = "ABGESCHLOSSEN")}
                    >
                        <i class="bi bi-check-circle"></i> Abgeschlossen
                    </button>
                    <button
                        class="filter-btn purple-theme {activeStatusFilter ===
                        'ABGESAGT'
                            ? 'active'
                            : ''}"
                        onclick={() => (activeStatusFilter = "ABGESAGT")}
                    >
                        <i class="bi bi-x-circle"></i> Abgesagt
                    </button>
                    <button
                        class="filter-btn freie-slots-btn {showOnlyFreieSlots
                            ? 'active'
                            : ''}"
                        onclick={() =>
                            (showOnlyFreieSlots = !showOnlyFreieSlots)}
                    >
                        <i class="bi bi-calendar-plus"></i>
                        {showOnlyFreieSlots
                            ? "Alle anzeigen"
                            : "Nur freie Slots"}
                    </button>
                </div>
            </div>

            <!-- Search Bar -->
            <div class="search-container">
                <div class="search-input-wrapper">
                    <i class="bi bi-search search-icon"></i>
                    <input
                        type="text"
                        class="search-input purple-theme"
                        placeholder="Suche nach Name, Datum, Uhrzeit, Dauer oder Preis..."
                        bind:value={searchQuery}
                    />
                    {#if searchQuery}
                        <button
                            class="clear-search"
                            onclick={() => (searchQuery = "")}
                            aria-label="Suche löschen"
                        >
                            <i class="bi bi-x-circle-fill"></i>
                        </button>
                    {/if}
                </div>
                {#if (searchQuery || activeStatusFilter !== "ALLE" || showOnlyFreieSlots) && filteredTermine().length < termine.length}
                    <p class="search-results-info">
                        {filteredTermine().length} von {termine.length} Terminen
                        gefunden
                    </p>
                {/if}
            </div>
        </div>

        {#if filteredTermine().length > 0}
            <div class="termine-list">
                {#each filteredTermine() as termin (termin.id)}
                    <TerminCard {termin} variant="purple" />
                {/each}
            </div>
        {:else if searchQuery || activeStatusFilter !== "ALLE" || showOnlyFreieSlots}
            <div class="empty-state">
                <i class="bi bi-search"></i>
                <p>Keine Termine gefunden</p>
                <button
                    class="btn btn-secondary"
                    onclick={() => {
                        searchQuery = "";
                        activeStatusFilter = "ALLE";
                        showOnlyFreieSlots = false;
                    }}
                >
                    Filter zurücksetzen
                </button>
            </div>
        {:else}
            <div class="empty-state">
                <i class="bi bi-calendar-x"></i>
                <p>Keine Termine vorhanden.</p>
            </div>
        {/if}
    </div>
</div>

<style>
    .zahnarzt-dashboard {
        padding: 2rem;
        max-width: 1400px;
        margin: 0 auto;
    }
    
    .dashboard-title {
        font-size: 2.5rem;
        font-weight: 700;
        font-weight: 700;
        color: var(--text-dark); 
       margin-bottom: 2rem;
    }
    
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 1.5rem;
        margin-bottom: 2rem;
    }
    
    .secondary-stats {
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    }

    .details-summary-title {
        font-size: 1.75rem;
        font-weight: 600;
        color: #2d3748;
        margin: 0 0 1rem 0;
    }
    
    .next-termin-section {
        margin-bottom: 2rem;
    }
    
    .termine-section {
        margin-top: 2rem;
        margin-bottom: 1.5rem;
    }
    
    .filter-section {
        display: flex;
        flex-wrap: wrap;
        gap: 1rem;
        margin-bottom: 1.5rem;
        align-items: center;
    }
    
    .filter-group {
        display: flex;
        flex-wrap: wrap;
        gap: 0.5rem;
        align-items: center;
    }
    
    .filter-btn {
        padding: 0.5rem 1rem;
        font-size: 0.875rem;
        font-weight: 500;
        border: 2px solid #e2e8f0;
        border-radius: 8px;
        background: white;
        color: #4a5568;
        cursor: pointer;
        transition: all 0.2s ease;
        display: flex;
        align-items: center;
        gap: 0.375rem;
        white-space: nowrap;
    }
    
    .filter-btn.purple-theme:hover {
        border-color: #8E24AA; /* sanfteres Lila */
        color: #6A1B9A; /* Lila Textfarbe */
        background: linear-gradient(135deg, #F1EAF9 0%, #E9DDF6 100%);
    }
    
    .filter-btn.purple-theme.active {
        border-color: #8E24AA;
        /* Angenehmes Lila ohne Türkis, reduzierter Kontrast */
        background: linear-gradient(135deg, #B388FF 0%, #8E24AA 100%);
        color: #ffffff;
    }
    
    .filter-btn i {
        font-size: 1rem;
    }
    
    .freie-slots-btn {
        background: linear-gradient(135deg, #ffffff 0%, #F5EFFC 100%);
    }
    
    .freie-slots-btn.active {
        background: linear-gradient(135deg, #DCCEF6 0%, #C7B7E6 100%);
        border-color: #8E24AA;
        color: #6A1B9A; /* Lila Textfarbe */
    }
    
    .search-container {
        margin-bottom: 1.5rem;
    }
    
    .search-input-wrapper {
        position: relative;
        display: flex;
        align-items: center;
        max-width: 600px;
    }
    
    .search-icon {
        position: absolute;
        left: 1rem;
        color: #8E24AA; /* sanftes Lila */
        font-size: 1.25rem;
        pointer-events: none;
    }
    
    .search-input {
        width: 100%;
        padding: 0.875rem 3rem 0.875rem 3rem;
        font-size: 1rem;
        border: 2px solid #e2e8f0;
        border-radius: 12px;
        background: white;
        transition: all 0.3s ease;
        outline: none;
    }
    
    .search-input.purple-theme:focus {
        border-color: #B388FF; /* Lila Fokusrand */
        box-shadow: 0 0 0 3px rgba(179, 136, 255, 0.18);
    }
    
    .search-input::placeholder {
        color: #94a3b8;
    }
    
    .clear-search {
        position: absolute;
        right: 0.75rem;
        background: none;
        border: none;
        color: #64748b;
        font-size: 1.25rem;
        cursor: pointer;
        padding: 0.25rem;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: color 0.2s ease;
    }
    
    .clear-search:hover {
        color: #8E24AA;
    }
    
    .search-results-info {
        margin-top: 0.75rem;
        font-size: 0.875rem;
        color: #64748b;
        font-weight: 500;
    }
    
    .termine-list {
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }
    
    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        color: #64748b;
    }
    
    .empty-state i {
        font-size: 4rem;
        margin-bottom: 1rem;
        opacity: 0.45;
        color: #AB47BC; /* etwas heller als zuvor */
    }
    
    .empty-state p {
        font-size: 1.25rem;
        margin: 0 0 1.5rem 0;
    }
    
    .empty-state .btn {
        margin-top: 1rem;
    }

    @media (max-width: 640px) {
        .filter-section {
            flex-direction: column;
            align-items: stretch;
        }
        
        .filter-group {
            width: 100%;
            justify-content: flex-start;
        }
        
        .filter-btn {
            flex: 1;
            justify-content: center;
            min-width: fit-content;
        }
        
        .search-input {
            padding: 0.75rem 2.5rem 0.75rem 2.5rem;
            font-size: 0.875rem;
        }
        
        .search-icon {
            font-size: 1rem;
        }
    }

    @media (min-width: 1024px) {
        .stats-grid {
            grid-template-columns: repeat(2, 1fr);
        }
        
        .secondary-stats {
            grid-template-columns: repeat(3, 1fr);
        }
    }
</style>

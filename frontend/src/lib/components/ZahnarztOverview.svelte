<script>
    import './ZahnarztOverview.css';
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

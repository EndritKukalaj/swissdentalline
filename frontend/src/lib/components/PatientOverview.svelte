<script>
    import "./PatientOverview.css";
    import TerminCard from "$lib/components/TerminCard.svelte";
    import StatCard from "$lib/components/StatCard.svelte";
    import NextTerminCard from "$lib/components/NextTerminCard.svelte";

    let { termine = [], nextTermin = null, stats = {} } = $props();

    // Search functionality
    let searchQuery = $state("");

    // Filter functionality
    let activeStatusFilter = $state("ALLE"); // ALLE, GEBUCHT, ABGESCHLOSSEN, ABGESAGT
    let showOnlyWarteliste = $state(false);

    // Filter termine based on search query and filters
    let filteredTermine = $derived(() => {
        let result = termine;

        // Apply status filter
        if (activeStatusFilter !== "ALLE") {
            result = result.filter(
                (termin) => termin.status === activeStatusFilter,
            );
        }

        // Apply warteliste filter
        if (showOnlyWarteliste) {
            result = result.filter(
                (termin) => termin.wartelisteAktiv || termin.warteliste_aktiv,
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

<div class="dashboard-container">
    <h1 class="dashboard-title">Meine Termine</h1>

    <!-- Stats Section -->
    <div class="stats-grid">
        <StatCard
            icon="bi-calendar-check"
            title="Geplante Termine"
            value={stats.geplanteTermine || 0}
            variant="turquoise"
        />
        <StatCard
            icon="bi-clock-history"
            title="Offene Wartelisten"
            value={stats.offeneWartelisten || 0}
            variant="turquoise"
        />
    </div>

    <!-- Next Appointment Section -->
    {#if nextTermin}
        <NextTerminCard termin={nextTermin} variant="turquoise" />
    {/if}

    <!-- Appointments List -->
    <div class="termine-section">
        <h2 class="section-title">Alle Termine</h2>

        <!-- Filter Buttons -->
        <div class="filter-section">
            <div class="filter-group">
                <button
                    class="filter-btn {activeStatusFilter === 'ALLE'
                        ? 'active'
                        : ''}"
                    onclick={() => (activeStatusFilter = "ALLE")}
                >
                    Alle
                </button>
                <button
                    class="filter-btn {activeStatusFilter === 'GEBUCHT'
                        ? 'active'
                        : ''}"
                    onclick={() => (activeStatusFilter = "GEBUCHT")}
                >
                    <i class="bi bi-calendar-check"></i> Gebucht
                </button>
                <button
                    class="filter-btn {activeStatusFilter === 'ABGESCHLOSSEN'
                        ? 'active'
                        : ''}"
                    onclick={() => (activeStatusFilter = "ABGESCHLOSSEN")}
                >
                    <i class="bi bi-check-circle"></i> Abgeschlossen
                </button>
                <button
                    class="filter-btn {activeStatusFilter === 'ABGESAGT'
                        ? 'active'
                        : ''}"
                    onclick={() => (activeStatusFilter = "ABGESAGT")}
                >
                    <i class="bi bi-x-circle"></i> Abgesagt
                </button>
                <button
                    class="filter-btn warteliste-btn {showOnlyWarteliste
                        ? 'active'
                        : ''}"
                    onclick={() => (showOnlyWarteliste = !showOnlyWarteliste)}
                >
                    <i class="bi bi-clock-history"></i>
                    {showOnlyWarteliste ? "Alle anzeigen" : "Nur Warteliste"}
                </button>
            </div>
        </div>

        <!-- Search Bar -->
        <div class="search-container">
            <div class="search-input-wrapper">
                <i class="bi bi-search search-icon"></i>
                <input
                    type="text"
                    class="search-input"
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
            {#if (searchQuery || activeStatusFilter !== "ALLE" || showOnlyWarteliste) && filteredTermine().length < termine.length}
                <p class="search-results-info">
                    {filteredTermine().length} von {termine.length} Terminen gefunden
                </p>
            {/if}
        </div>
    </div>
    {#if filteredTermine().length > 0}
        <div class="termine-list">
            {#each filteredTermine() as termin (termin.id)}
                <TerminCard {termin} variant="turquoise" />
            {/each}
        </div>
    {:else if searchQuery || activeStatusFilter !== "ALLE" || showOnlyWarteliste}
        <div class="empty-state">
            <i class="bi bi-search"></i>
            <p>Keine Termine gefunden</p>
            <button
                class="btn btn-secondary"
                onclick={() => {
                    searchQuery = "";
                    activeStatusFilter = "ALLE";
                    showOnlyWarteliste = false;
                }}
            >
                Filter zurücksetzen
            </button>
        </div>
    {:else}
        <div class="empty-state">
            <i class="bi bi-calendar-x"></i>
            <p>Sie haben noch keine Termine gebucht.</p>
        </div>
    {/if}
</div>

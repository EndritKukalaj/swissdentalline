<script>
    import TerminCard from '$lib/components/TerminCard.svelte';
    import StatCard from '$lib/components/StatCard.svelte';
    import NextTerminCard from '$lib/components/NextTerminCard.svelte';

    let { termine = [], nextTermin = null, stats = {} } = $props();

    // Search functionality
    let searchQuery = $state('');

    // Filter functionality
    let activeStatusFilter = $state('ALLE'); // ALLE, GEBUCHT, ABGESCHLOSSEN, ABGESAGT
    let showOnlyWarteliste = $state(false);

    // Filter termine based on search query and filters
    let filteredTermine = $derived(() => {
        let result = termine;
        
        // Apply status filter
        if (activeStatusFilter !== 'ALLE') {
            result = result.filter(termin => termin.status === activeStatusFilter);
        }
        
        // Apply warteliste filter
        if (showOnlyWarteliste) {
            result = result.filter(termin => termin.wartelisteAktiv || termin.warteliste_aktiv);
        }
        
        // Apply search query
        if (searchQuery.trim()) {
            const query = searchQuery.toLowerCase().trim();
            result = result.filter(termin => {
                const nameMatch = termin.behandlungsartName?.toLowerCase().includes(query);
                const date = new Date(termin.datum);
                const dateString = date.toLocaleDateString('de-CH');
                const dateMatch = dateString.includes(query);
                const timeString = date.toLocaleTimeString('de-CH', { hour: '2-digit', minute: '2-digit' });
                const timeMatch = timeString.includes(query);
                const duration = termin.dauerMinuten || termin.dauer_minuten || 0;
                const durationString = `${duration}`;
                const durationMatch = durationString.includes(query) || `${duration} min`.includes(query);
                const priceString = `${termin.preis}`;
                const priceMatch = priceString.includes(query) || `chf ${termin.preis}`.toLowerCase().includes(query);
                return nameMatch || dateMatch || timeMatch || durationMatch || priceMatch;
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
        <div class="section-header">
            <h2 class="section-title">Alle Termine</h2>
            
            <!-- Filter Buttons -->
            <div class="filter-section">
                <div class="filter-group">
                    <button
                        class="filter-btn {activeStatusFilter === 'ALLE' ? 'active' : ''}"
                        onclick={() => activeStatusFilter = 'ALLE'}
                    >
                        Alle
                    </button>
                    <button
                        class="filter-btn {activeStatusFilter === 'GEBUCHT' ? 'active' : ''}"
                        onclick={() => activeStatusFilter = 'GEBUCHT'}
                    >
                        <i class="bi bi-calendar-check"></i> Gebucht
                    </button>
                    <button
                        class="filter-btn {activeStatusFilter === 'ABGESCHLOSSEN' ? 'active' : ''}"
                        onclick={() => activeStatusFilter = 'ABGESCHLOSSEN'}
                    >
                        <i class="bi bi-check-circle"></i> Abgeschlossen
                    </button>
                    <button
                        class="filter-btn {activeStatusFilter === 'ABGESAGT' ? 'active' : ''}"
                        onclick={() => activeStatusFilter = 'ABGESAGT'}
                    >
                        <i class="bi bi-x-circle"></i> Abgesagt
                    </button>
                    <button
                        class="filter-btn warteliste-btn {showOnlyWarteliste ? 'active' : ''}"
                        onclick={() => showOnlyWarteliste = !showOnlyWarteliste}
                    >
                        <i class="bi bi-clock-history"></i> 
                        {showOnlyWarteliste ? 'Alle anzeigen' : 'Nur Warteliste'}
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
                            onclick={() => searchQuery = ''}
                            aria-label="Suche löschen"
                        >
                            <i class="bi bi-x-circle-fill"></i>
                        </button>
                    {/if}
                </div>
                {#if (searchQuery || activeStatusFilter !== 'ALLE' || showOnlyWarteliste) && filteredTermine().length < termine.length}
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
        {:else if searchQuery || activeStatusFilter !== 'ALLE' || showOnlyWarteliste}
            <div class="empty-state">
                <i class="bi bi-search"></i>
                <p>Keine Termine gefunden</p>
                <button class="btn btn-secondary" onclick={() => {
                    searchQuery = '';
                    activeStatusFilter = 'ALLE';
                    showOnlyWarteliste = false;
                }}>
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
</div>

<style>
    .dashboard-container {
        padding: 2rem;
        max-width: 1200px;
        margin: 0 auto;
    }
    
    .dashboard-title {
        font-size: 2.5rem;
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
    
    .termine-section {
        margin-top: 2rem;
    }
    
    .section-header {
        margin-bottom: 1.5rem;
    }
    
    .section-title {
        font-size: 1.75rem;
        font-weight: 600;
        color: var(--text-dark);
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
    
    .filter-btn:hover {
        border-color: var(--primary-turquoise);
        color: var(--primary-turquoise);
        background: #f0fffe;
    }
    
    .filter-btn.active {
        border-color: var(--primary-turquoise);
        background: var(--primary-turquoise);
        color: white;
    }
    
    .filter-btn i {
        font-size: 1rem;
    }
    
    .warteliste-btn {
        background: linear-gradient(135deg, #ffffff 0%, #fff9e6 100%);
    }
    
    .warteliste-btn.active {
        background: linear-gradient(135deg, #FFC107 0%, #FFB300 100%);
        border-color: #FFC107;
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
        color: var(--primary-turquoise);
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
    
    .search-input:focus {
        border-color: var(--primary-turquoise);
        box-shadow: 0 0 0 3px rgba(0, 150, 136, 0.1);
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
        color: var(--primary-turquoise);
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
        color: var(--text-muted);
    }
    
    .empty-state i {
        font-size: 4rem;
        margin-bottom: 1rem;
        opacity: 0.5;
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
        
        .search-input::placeholder {
            font-size: 0.875rem;
        }
    }
</style>
<script>
    import TerminCard from '$lib/components/TerminCard.svelte';
    import StatCard from '$lib/components/StatCard.svelte';
    import NextTerminCard from '$lib/components/NextTerminCard.svelte';
    import ZahnarztOverview from '$lib/components/ZahnarztOverview.svelte';
    
    let { data } = $props();
    let { isAuthenticated, termine = [], nextTermin = null, stats = {}, userRole = null } = data;
    
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
            // Search in behandlungsart name
            const nameMatch = termin.behandlungsartName?.toLowerCase().includes(query);
            
            // Search in date (formatted as DD.MM.YYYY)
            const date = new Date(termin.datum);
            const dateString = date.toLocaleDateString('de-CH');
            const dateMatch = dateString.includes(query);
            
            // Search in time (extracted from datum, formatted as HH:MM)
            const timeString = date.toLocaleTimeString('de-CH', { hour: '2-digit', minute: '2-digit' });
            const timeMatch = timeString.includes(query);
            
            // Search in duration (using dauer_minuten or dauerMinuten)
            const duration = termin.dauerMinuten || termin.dauer_minuten || 0;
            const durationString = `${duration}`;
            const durationMatch = durationString.includes(query) || `${duration} min`.includes(query);
            
            // Search in price
            const priceString = `${termin.preis}`;
            const priceMatch = priceString.includes(query) || `chf ${termin.preis}`.toLowerCase().includes(query);
            
            return nameMatch || dateMatch || timeMatch || durationMatch || priceMatch;
            });
        }
        
        return result;
    });
</script>

{#if isAuthenticated}
    {#if userRole === 'Zahnarzt'}
        <ZahnarztOverview {termine} {nextTermin} {stats} />
    {:else}
        <div class="dashboard-container">
        <h1 class="dashboard-title">Meine Termine</h1>
        
        <!-- Stats Section -->
        <div class="stats-grid">
            <StatCard 
                icon="bi-calendar-check" 
                title="Geplante Termine" 
                value={stats.geplanteTermine || 0} 
            />
            <StatCard 
                icon="bi-clock-history" 
                title="Offene Wartelisten" 
                value={stats.offeneWartelisten || 0} 
            />
        </div>
        
        <!-- Next Appointment Section -->
        {#if nextTermin}
            <NextTerminCard termin={nextTermin} />
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
                        <TerminCard {termin} />
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
    {/if}
{:else}
    <div class="home-container">
        <div class="home-wrapper">
            <!-- Logo/Brand Section -->
            <div class="brand-section">
                <div>
                    <h1 class="brand-title text-center">SwissDentalLine</h1>
                    <hr />
                </div>
            </div>

            <!-- Hero Image Section -->
            <div class="hero-section">
                <div class="hero-image-wrapper">
                    <img
                        src="/images/nahaufnahme.jpg"
                        alt="Dental Care"
                        class="hero-image"
                    />
                    <div class="hero-overlay">
                        <div class="hero-content text-center">
                            <h2 class="hero-title">
                                Finden Sie verfügbare Zahnärzte, bevor Sie
                                suchen müssen
                            </h2>
                            <p class="hero-subtitle">
                                Freie Zahnarzttermine in Echtzeit
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- CTA Section -->
            <div class="cta-section">
                <a href="/login" class="btn btn-primary btn-lg btn-block">
                    Loslegen
                </a>
            </div>
        </div>
    </div>
{/if}

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
        border-color: #009688;
        color: #009688;
        background: #f0fffe;
    }
    
    .filter-btn.active {
        border-color: #009688;
        background: #009688;
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
        color: #009688;
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
        border-color: #009688;
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
        color: #009688;
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

    .home-container {
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        background-color: var(--white);
    }

    .home-wrapper {
        flex: 1;
        display: flex;
        flex-direction: column;
    }

    .brand-section {
        padding: 2rem 1.5rem 1rem;
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .brand-title {
        font-size: 2rem;
        font-weight: 700;
        line-height: 1.2;
        color: var(--text-dark);
        margin: 0;
    }

    .hero-section {
        position: relative;
        overflow: hidden;
    }

    .hero-image-wrapper {
        position: relative;
        width: 100%;
        height: 500px;
    }

    .hero-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        object-position: center;
        transform: scale(1.1);
    }

    .hero-overlay {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: linear-gradient(
            to top,
            rgba(0, 150, 136, 1) 0%,
            rgba(0, 150, 136, 0.95) 30%,
            rgba(0, 150, 136, 0.85) 50%,
            rgba(0, 150, 136, 0.5) 70%,
            rgba(0, 150, 136, 0) 100%
        );
        padding: 3rem 1.5rem 2rem;
    }

    .hero-content {
        max-width: 800px;
        margin: 0 auto;
    }

    .hero-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--white);
        margin-bottom: 0.5rem;
        line-height: 1.3;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }

    .hero-subtitle {
        font-size: 1rem;
        color: var(--white);
        margin: 0;
        font-weight: 500;
        font-style: italic;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }

    .cta-section {
        padding: 2rem 1.5rem;
        text-align: center;
    }

    .btn-block {
        width: 100%;
        max-width: 400px;
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

    @media (min-width: 768px) {
        .brand-section {
            justify-content: space-between;
            align-items: center;
        }

        .brand-title {
            font-size: 2.5rem;
        }

        .hero-image-wrapper {
            height: 500px;
        }

        .hero-title {
            font-size: 2rem;
        }

        .hero-subtitle {
            font-size: 1.25rem;
        }

        .hero-overlay {
            padding: 4rem 2rem 3rem;
        }
    }

    @media (min-width: 1024px) {
        .brand-title {
            font-size: 3rem;
        }

        .hero-image-wrapper {
            min-height: 600px;
        }

        .hero-title {
            font-size: 2.5rem;
        }
    }
</style>

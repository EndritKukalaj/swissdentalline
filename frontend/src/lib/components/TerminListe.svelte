<script>
    import { goto } from '$app/navigation';
    import TerminCard from '$lib/components/TerminCard.svelte';

    let { termine = [], stats = {}, role = 'Patient' } = $props();

    const isZahnarzt = role === 'Zahnarzt';
    const variant = isZahnarzt ? 'blue' : 'turquoise';

    // Search and Filters
    let searchQuery = $state('');
    let activeStatusFilter = $state('ALLE');
    let showOnlyWarteliste = $state(false); // Patient
    let showOnlyFreieSlots = $state(false); // Zahnarzt

    let filteredTermine = $derived(() => {
        let result = termine;

        // Status filter
        if (activeStatusFilter !== 'ALLE') {
            result = result.filter((t) => t.status === activeStatusFilter);
        }

        // Role-specific toggles
        if (!isZahnarzt && showOnlyWarteliste) {
            result = result.filter((t) => t.wartelisteAktiv || t.warteliste_aktiv);
        }
        if (isZahnarzt && showOnlyFreieSlots) {
            result = result.filter((t) => t.status === 'FREI' && !t.patientId);
        }

        // Search
        if (searchQuery.trim()) {
            const q = searchQuery.toLowerCase().trim();
            result = result.filter((t) => {
                const name = t.behandlungsartName?.toLowerCase() || '';
                const date = new Date(t.datum);
                const dateStr = date.toLocaleDateString('de-CH');
                const timeStr = date.toLocaleTimeString('de-CH', { hour: '2-digit', minute: '2-digit' });
                const duration = t.dauerMinuten || t.dauer_minuten || 0;
                const price = `${t.preis ?? ''}`;
                return (
                    name.includes(q) ||
                    dateStr.includes(q) ||
                    timeStr.includes(q) ||
                    `${duration}`.includes(q) || `${duration} min`.includes(q) ||
                    price.includes(q) || `chf ${price}`.toLowerCase().includes(q)
                );
            });
        }

        return result;
    });
</script>

<div class="termine-page {isZahnarzt ? 'blue-variant' : ''}">
    <div class="page-header">
        <button class="back-btn" onclick={() => goto('/')}>
            <i class="bi bi-arrow-left"></i>
            Zurück zur Übersicht
        </button>
        
        <div class="header-content-wrapper">
            <div class="header-text">
                <h1 class="page-title">{isZahnarzt ? 'Terminübersicht' : 'Meine Termine'}</h1>
                <p class="page-subtitle">
                    Verwalten Sie Ihre {isZahnarzt ? 'Praxis-' : ''}Termine an einem Ort
                </p>
            </div>
            
            <!-- Stats badges -->
            <div class="stats-badges">
                <div class="stat-badge stat-badge-primary">
                    <i class="bi bi-calendar3"></i>
                    <div class="stat-content">
                        <span class="stat-value">{termine.length || 0}</span>
                        <span class="stat-label">Gesamt</span>
                    </div>
                </div>
                
                <div class="stat-badge">
                    <i class="bi bi-calendar-check-fill"></i>
                    <div class="stat-content">
                        <span class="stat-value">{stats.geplanteTermine || 0}</span>
                        <span class="stat-label">Geplant</span>
                    </div>
                </div>
                
                {#if !isZahnarzt}
                    <div class="stat-badge">
                        <i class="bi bi-clock-history"></i>
                        <div class="stat-content">
                            <span class="stat-value">{stats.offeneWartelisten || 0}</span>
                            <span class="stat-label">Wartelisten</span>
                        </div>
                    </div>
                {:else}
                    <div class="stat-badge">
                        <i class="bi bi-calendar-plus"></i>
                        <div class="stat-content">
                            <span class="stat-value">{stats.freieSlots || 0}</span>
                            <span class="stat-label">Freie Slots</span>
                        </div>
                    </div>
                {/if}
                
                <div class="stat-badge">
                    <i class="bi bi-check-circle-fill"></i>
                    <div class="stat-content">
                        <span class="stat-value">{stats.abgeschlosseneTermine || 0}</span>
                        <span class="stat-label">Abgeschlossen</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="termine-content">
        <h2 class="section-title">Alle Termine</h2>

        <!-- Filters -->
        <div class="filter-section">
            <div class="filter-group">
                <button class="filter-btn {isZahnarzt ? 'blue-theme' : ''} {activeStatusFilter === 'ALLE' ? 'active' : ''}"
                    onclick={() => (activeStatusFilter = 'ALLE')}>Alle</button>
                <button class="filter-btn {isZahnarzt ? 'blue-theme' : ''} {activeStatusFilter === 'GEBUCHT' ? 'active' : ''}"
                    onclick={() => (activeStatusFilter = 'GEBUCHT')}>
                    <i class="bi bi-calendar-check"></i> Gebucht
                </button>
                {#if isZahnarzt}
                    <button class="filter-btn {isZahnarzt ? 'blue-theme' : ''} {activeStatusFilter === 'FREI' ? 'active' : ''}"
                        onclick={() => (activeStatusFilter = 'FREI')}>
                        <i class="bi bi-calendar-plus"></i> Frei
                    </button>
                {/if}
                <button class="filter-btn {isZahnarzt ? 'blue-theme' : ''} {activeStatusFilter === 'ABGESCHLOSSEN' ? 'active' : ''}"
                    onclick={() => (activeStatusFilter = 'ABGESCHLOSSEN')}>
                    <i class="bi bi-check-circle"></i> Abgeschlossen
                </button>
                <button class="filter-btn {isZahnarzt ? 'blue-theme' : ''} {activeStatusFilter === 'ABGESAGT' ? 'active' : ''}"
                    onclick={() => (activeStatusFilter = 'ABGESAGT')}>
                    <i class="bi bi-x-circle"></i> Abgesagt
                </button>

                <!-- Role toggles -->
                {#if !isZahnarzt}
                    <button class="filter-btn warteliste-btn {showOnlyWarteliste ? 'active' : ''}"
                        onclick={() => (showOnlyWarteliste = !showOnlyWarteliste)}>
                        <i class="bi bi-clock-history"></i>
                        {showOnlyWarteliste ? 'Alle anzeigen' : 'Nur Warteliste'}
                    </button>
                {:else}
                    <button class="filter-btn freie-slots-btn {showOnlyFreieSlots ? 'active' : ''}"
                        onclick={() => (showOnlyFreieSlots = !showOnlyFreieSlots)}>
                        <i class="bi bi-calendar-plus"></i>
                        {showOnlyFreieSlots ? 'Alle anzeigen' : 'Nur freie Slots'}
                    </button>
                {/if}
            </div>
        </div>

        <!-- Search -->
        <div class="search-container">
            <div class="search-input-wrapper">
                <i class="bi bi-search search-icon"></i>
                <input type="text" class="search-input {isZahnarzt ? 'blue-theme' : ''}"
                    placeholder="Suche nach Name, Datum, Uhrzeit, Dauer oder Preis..." bind:value={searchQuery} />
                {#if searchQuery}
                    <button class="clear-search" onclick={() => (searchQuery = '')} aria-label="Suche löschen">
                        <i class="bi bi-x-circle-fill"></i>
                    </button>
                {/if}
            </div>
            {#if (searchQuery || activeStatusFilter !== 'ALLE' || showOnlyWarteliste || showOnlyFreieSlots) && filteredTermine().length < termine.length}
                <p class="search-results-info">{filteredTermine().length} von {termine.length} Terminen gefunden</p>
            {/if}
        </div>

        <!-- List -->
        {#if filteredTermine().length > 0}
            <div class="termine-list">
                {#each filteredTermine() as termin (termin.id)}
                    <TerminCard {termin} variant={variant} />
                {/each}
            </div>
        {:else if searchQuery || activeStatusFilter !== 'ALLE' || showOnlyWarteliste || showOnlyFreieSlots}
            <div class="empty-state">
                <i class="bi bi-search"></i>
                <p>Keine Termine gefunden</p>
                <button class="btn btn-secondary" onclick={() => { searchQuery=''; activeStatusFilter='ALLE'; showOnlyWarteliste=false; showOnlyFreieSlots=false; }}>Filter zurücksetzen</button>
            </div>
        {:else}
            <div class="empty-state">
                <i class="bi bi-calendar-x"></i>
                <p>{isZahnarzt ? 'Keine Termine vorhanden.' : 'Sie haben noch keine Termine gebucht.'}</p>
            </div>
        {/if}
    </div>
</div>

<style>
/* Page layout */
.termine-page {
    min-height: 100vh;
    background: #f8fafc;
}

.page-header {
    background: linear-gradient(135deg, var(--hdr-start, #009688) 0%, var(--hdr-end, #00bfa5) 100%);
    padding: 2rem;
    color: white;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.blue-variant .page-header {
    --hdr-start: #30B0C7;
    --hdr-end: #268a9c;
}

.header-content-wrapper {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.header-text {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.page-title {
    font-size: 2.5rem;
    font-weight: 700;
    margin: 0;
    color: #ffffff;
}

.page-subtitle {
    font-size: 1.125rem;
    opacity: .90;
    margin: 0;
    color: #ffffff;
    font-weight: 400;
}

.stats-badges {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
}

.stat-badge {
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.25);
    border-radius: 12px;
    padding: 1rem 1.5rem;
    display: flex;
    align-items: center;
    gap: 1rem;
    transition: all 0.3s ease;
    flex: 1;
    min-width: 140px;
}

.stat-badge:hover {
    background: rgba(255, 255, 255, 0.25);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-badge-primary {
    background: rgba(255, 255, 255, 0.25);
    border: 2px solid rgba(255, 255, 255, 0.4);
}

.stat-badge-primary:hover {
    background: rgba(255, 255, 255, 0.35);
}

.stat-badge i {
    font-size: 2rem;
    color: white;
    opacity: 0.9;
}

.stat-content {
    display: flex;
    flex-direction: column;
    gap: 0.125rem;
}

.stat-value {
    font-size: 1.75rem;
    font-weight: 700;
    line-height: 1;
    color: white;
}

.stat-label {
    font-size: 0.875rem;
    font-weight: 500;
    color: white;
    opacity: 0.85;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.back-btn {
    display: inline-flex;
    align-items: center;
    gap: .5rem;
    padding: .75rem 1.25rem;
    font-size: .875rem;
    font-weight: 500;
    color: white;
    background: rgba(255, 255, 255, .2);
    border: 2px solid rgba(255, 255, 255, .3);
    border-radius: 8px;
    cursor: pointer;
    transition: all .2s ease;
    margin-bottom: 1.5rem;
}

.back-btn:hover {
    background: rgba(255, 255, 255, .3);
    border-color: rgba(255, 255, 255, .5);
}

.termine-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
}

.section-title {
    font-size: 1.75rem;
    font-weight: 600;
    color: var(--text-dark);
    margin-bottom: 1.5rem;
}

.termine-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

/* Filters */
.filter-section {
    margin-bottom: 1.5rem;
}

.filter-group {
    display: flex;
    flex-wrap: wrap;
    gap: .5rem;
}

.filter-btn {
    padding: .5rem 1rem;
    border-radius: 10px;
    border: 1px solid #e5e7eb;
    background: white;
    font-weight: 600;
    cursor: pointer;
    transition: all .15s ease-in-out;
}

/* Active states by role */
/* Turquoise (Patient) */
.termine-page:not(.blue-variant) .filter-btn.active {
    background: linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%);
    border-color: #009688;
    color: #00695c;
    box-shadow: 0 0 0 3px rgba(0, 150, 136, 0.18);
}

/* Blue (Zahnarzt) */
.blue-variant .filter-btn.active {
    background: linear-gradient(135deg, #D0F0F5 0%, #b9e7fa 100%);
    border-color: #30B0C7;
    color: #1a5f6f;
    box-shadow: 0 0 0 3px rgba(48, 176, 199, 0.18);
}

.warteliste-btn {
    background: linear-gradient(135deg, #ffffff 0%, #fff9e6 100%);
}

.warteliste-btn.active {
    background: linear-gradient(135deg, #FFC107 0%, #FFB300 100%);
    border-color: #FFC107;
    color: #6A1B9A;
}

.freie-slots-btn {
    background: linear-gradient(135deg, #ffffff 0%, #E8F8FA 100%);
}

.freie-slots-btn.active {
    background: linear-gradient(135deg, #b9e7fa 0%, #b6e2f8 100%);
    border-color: #30B0C7;
    color: #1a5f6f;
}

/* Search */
.search-container {
    margin-top: 1rem;
}

.search-input-wrapper {
    display: flex;
    align-items: center;
    gap: .5rem;
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    padding: .5rem .75rem;
    width: 100%;
    max-width: 560px;
}

.search-icon {
    color: #009688;
}

.blue-variant .search-icon {
    color: #30B0C7;
}

.search-input {
    flex: 1;
    border: none;
    outline: none;
    font-size: .95rem;
}

.search-input.blue-theme:focus {
    border-color: #30B0C7;
    box-shadow: 0 0 0 3px rgba(48, 176, 199, 0.18);
}

.clear-search {
    background: none;
    border: none;
    color: #9ca3af;
    cursor: pointer;
}

.search-results-info {
    margin-top: 0.5rem;
    font-size: 0.875rem;
    color: #6b7280;
}

/* Empty state */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: .5rem;
    color: #6b7280;
    padding: 2rem 0;
}

.empty-state i {
    font-size: 3rem;
    margin-bottom: 0.5rem;
}

.btn {
    padding: 0.5rem 1rem;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s ease;
}

.btn-secondary {
    background: #e5e7eb;
    color: #1f2937;
}

.btn-secondary:hover {
    background: #d1d5db;
}

/* Responsive */
@media (max-width: 768px) {
    .page-header {
        padding: 1.5rem 1rem;
    }

    .header-content-wrapper {
        gap: 1rem;
    }

    .page-title {
        font-size: 1.75rem;
    }

    .page-subtitle {
        font-size: .9375rem;
    }

    .stats-badges {
        gap: 0.75rem;
    }

    .stat-badge {
        padding: 0.75rem 1rem;
        min-width: 120px;
        gap: 0.75rem;
    }

    .stat-badge i {
        font-size: 1.5rem;
    }

    .stat-value {
        font-size: 1.5rem;
    }

    .stat-label {
        font-size: 0.75rem;
    }

    .termine-content {
        padding: 1.5rem 1rem;
    }
}
</style>
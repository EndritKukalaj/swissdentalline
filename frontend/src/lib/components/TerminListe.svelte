<script>
    import { goto } from '$app/navigation';
    import TerminCard from '$lib/components/TerminCard.svelte';
    import './TerminListe.css';

    let { termine = [], stats = {}, role = 'Patient' } = $props();

    const isZahnarzt = role === 'Zahnarzt';
    const variant = isZahnarzt ? 'purple' : 'turquoise';

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

<div class="termine-page {isZahnarzt ? 'purple-variant' : ''}">
    <div class="page-header">
        <button class="back-btn" onclick={() => goto('/')}>
            <i class="bi bi-arrow-left"></i>
            Zurück zur Übersicht
        </button>
        <h1 class="page-title">{isZahnarzt ? 'Terminübersicht' : 'Meine Termine'}</h1>
        <p class="page-subtitle">
            {stats.geplanteTermine || 0} geplante Termine
            {#if !isZahnarzt && (stats.offeneWartelisten > 0)}
                · {stats.offeneWartelisten} offene Wartelisten
            {/if}
            {#if isZahnarzt && (stats.freieSlots > 0)}
                · {stats.freieSlots} freie Slots
            {/if}
        </p>
    </div>

    <div class="termine-content">
        <h2 class="section-title">Alle Termine</h2>

        <!-- Filters -->
        <div class="filter-section">
            <div class="filter-group">
                <button class="filter-btn {isZahnarzt ? 'purple-theme' : ''} {activeStatusFilter === 'ALLE' ? 'active' : ''}"
                    onclick={() => (activeStatusFilter = 'ALLE')}>Alle</button>
                <button class="filter-btn {isZahnarzt ? 'purple-theme' : ''} {activeStatusFilter === 'GEBUCHT' ? 'active' : ''}"
                    onclick={() => (activeStatusFilter = 'GEBUCHT')}>
                    <i class="bi bi-calendar-check"></i> Gebucht
                </button>
                {#if isZahnarzt}
                    <button class="filter-btn {isZahnarzt ? 'purple-theme' : ''} {activeStatusFilter === 'FREI' ? 'active' : ''}"
                        onclick={() => (activeStatusFilter = 'FREI')}>
                        <i class="bi bi-calendar-plus"></i> Frei
                    </button>
                {/if}
                <button class="filter-btn {isZahnarzt ? 'purple-theme' : ''} {activeStatusFilter === 'ABGESCHLOSSEN' ? 'active' : ''}"
                    onclick={() => (activeStatusFilter = 'ABGESCHLOSSEN')}>
                    <i class="bi bi-check-circle"></i> Abgeschlossen
                </button>
                <button class="filter-btn {isZahnarzt ? 'purple-theme' : ''} {activeStatusFilter === 'ABGESAGT' ? 'active' : ''}"
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
                <input type="text" class="search-input {isZahnarzt ? 'purple-theme' : ''}"
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



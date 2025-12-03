<script>
    import { goto } from '$app/navigation';
    import StatCard from "./StatCard.svelte";
    import NextTerminCard from "./NextTerminCard.svelte";

    let { nextTermin = null, stats = {} } = $props();
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
            onclick={() => goto('/termine')}
            clickable={true}
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
</div>

<style>
/* ZahnarztOverview Component Styles */
.zahnarzt-dashboard {
    padding: 2rem;
    max-width: 1400px;
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

@media (min-width: 1024px) {
    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }

    .secondary-stats {
        grid-template-columns: repeat(3, 1fr);
    }
}
</style>
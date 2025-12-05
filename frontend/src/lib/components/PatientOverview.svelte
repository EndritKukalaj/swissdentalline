<script>
    import { goto } from '$app/navigation';
    import StatCard from "$lib/components/StatCard.svelte";
    import NextTerminCard from "$lib/components/NextTerminCard.svelte";

    let { nextTermin = null, stats = {} } = $props();
</script>

<div class="dashboard-container">
    <div class="header-section">
        <h1 class="dashboard-title">Meine Termine</h1>
        <button class="btn-book-appointment" onclick={() => goto('/buchen')}>
            <i class="bi bi-calendar-plus"></i>
            Termin buchen
        </button>
    </div>

    <!-- Stats Section -->
    <div class="stats-grid">
        <StatCard
            icon="bi-calendar-check"
            title="Geplante Termine"
            value={stats.geplanteTermine || 0}
            variant="turquoise"
            onclick={() => goto('/termine')}
            clickable={true}
        />
        <StatCard
            icon="bi-clock-history"
            title="Offene Wartelisten"
            value={stats.offeneWartelisten || 0}
            variant="turquoise"
        />
        <StatCard
            icon="bi-lightning-charge-fill"
            title="Verfügbare Flex-Termine"
            value={stats.verfuegbareFlexTermine || 0}
            variant="yellow"
            onclick={() => goto('/flextermine')}
            clickable={true}
        />
    </div>

    <!-- Next Appointment Section -->
    {#if nextTermin}
        <NextTerminCard termin={nextTermin} variant="turquoise" />
    {/if}
</div>

<style>
/* PatientOverview Component Styles */
.dashboard-container {
    padding: 2rem;
    max-width: 1200px;
    margin: 0 auto;
}

.header-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    flex-wrap: wrap;
    gap: 1rem;
}

.dashboard-title {
    font-size: 2.5rem;
    font-weight: 700;
    color: var(--text-dark);
    margin: 0;
}

.btn-book-appointment {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 1rem 2rem;
    font-size: 1rem;
    font-weight: 600;
    color: white;
    background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
}

.btn-book-appointment:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 150, 136, 0.4);
}

.btn-book-appointment i {
    font-size: 1.25rem;
}

.stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
}
</style>
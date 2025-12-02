<script>
    import "./PatientOverview.css";
    import { goto } from '$app/navigation';
    import StatCard from "$lib/components/StatCard.svelte";
    import NextTerminCard from "$lib/components/NextTerminCard.svelte";

    let { nextTermin = null, stats = {} } = $props();
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
            onclick={() => goto('/termine')}
            clickable={true}
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
</div>

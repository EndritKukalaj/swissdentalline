<script>
    import { goto } from "$app/navigation";
    import StatCard from "$lib/components/cards/StatCard.svelte";
    import NextTerminCard from "$lib/components/cards/NextTerminCard.svelte";
    import EmptyState from "$lib/components/functional/EmptyState.svelte";

    let { nextTermin = null, stats = {} } = $props();
</script>

<div class="zahnarzt-dashboard">
    <h1 class="dashboard-title">Ihr Praxis-Dashboard</h1>

    <!-- Stats Section -->
    <div class="stats-grid secondary-stats">
        <StatCard
            icon="bi-calendar-check"
            title="Geplante Termine"
            value={stats.geplanteTermine || 0}
            variant="blue"
            onclick={() => goto("/termine?status=GEBUCHT")}
            clickable={true}
        />
        <StatCard
            icon="bi-calendar-plus"
            title="Freie Slots"
            value={stats.freieSlots || 0}
            variant="blue"
            onclick={() => goto("/termine?status=FREI")}
            clickable={true}
        />

        <StatCard
            icon="bi-x-circle"
            title="Abgesagte Termine"
            value={stats.abgesagteTermine || 0}
            variant="blue"
            onclick={() => goto("/termine?status=ABGESAGT")}
            clickable={true}
        />
        <StatCard
            icon="bi-lightning"
            title="Flex-Termine freigegeben"
            value={stats.wartelisteVerfuegbar || 0}
            variant="blue"
            onclick={() => goto("/termine?status=FLEX")}
            clickable={true}
        />
        <StatCard
            icon="bi-cash-coin"
            title="Monatliche Einnahmen"
            value={`${stats.monatlicheEinnahmen || 0} CHF`}
            variant="blue"
            onclick={() => goto("/statistik")}
            clickable={true}
        />
        <StatCard
            icon="bi-star-fill"
            title="Durchschn. Bewertung"
            value={`${stats.durchschnittsBewertung || "0.0"}`}
            variant="blue"
            onclick={() => goto("/rezensionen")}
            clickable={true}
        />
    </div>

    <!-- Next Appointment Section -->
    {#if nextTermin}
        <div class="next-termin-section">
            <NextTerminCard termin={nextTermin} variant="blue" />
        </div>
    {:else}
        <EmptyState
            icon="calendar-plus"
            title="Noch keine Termine geplant"
            message="Sie haben aktuell keine geplanten Termine. Öffnen Sie neue Slots, um Patient:innen die Buchung zu ermöglichen, oder verwalten Sie Ihre Terminverfügbarkeit."
            buttonText="Slots freigeben"
            buttonHref="/slots"
            variant="blue"
        />
    {/if}
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

    .next-termin-section {
        margin-bottom: 2rem;
    }

    @media (max-width: 768px) {
        .zahnarzt-dashboard {
            padding: 1rem;
        }

        .dashboard-title {
            font-size: 1.5rem;
            margin-bottom: 1.25rem;
        }

        .stats-grid {
            grid-template-columns: repeat(2, 1fr);
            gap: 0.5rem;
            margin-bottom: 1rem;
        }

        .secondary-stats {
            grid-template-columns: repeat(2, 1fr);
            gap: 0.5rem;
            margin-bottom: 1rem;
        }

        .next-termin-section {
            margin-bottom: 1.5rem;
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

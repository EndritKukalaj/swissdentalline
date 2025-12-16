<script>
    import { goto } from "$app/navigation";
    import { BookingProgressBar, EmptyState, BehandlungCard } from '$lib';

    let { data } = $props();
    let { behandlungsarten } = data;

    const handleBehandlungSelect = (behandlungsart) => {
        goto(`/buchen/termin?behandlungsartId=${behandlungsart.id}`);
    };
</script>

<div class="buchen-container">
    <BookingProgressBar currentStep={1} />

    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={() => goto("/")}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
        <h1 class="page-title">Wählen Sie Ihre Behandlung aus</h1>
        <p class="page-subtitle">
            Welche zahnmedizinische Behandlung benötigen Sie?
        </p>
    </div>

    <!-- Behandlungsarten Grid -->
    <div class="behandlungen-grid">
        {#each behandlungsarten as behandlung (behandlung.id)}
            <BehandlungCard
                name={behandlung.name}
                beschreibung={behandlung.beschreibung}
                onClick={() => handleBehandlungSelect(behandlung)}
                variant="turquoise"
            />
        {/each}
    </div>

    {#if behandlungsarten.length === 0}
        <EmptyState
            icon="inbox"
            title="Keine Behandlungsarten verfügbar"
        />
    {/if}
</div>

<style>
.buchen-container {
    min-height: 100vh;
    background: #f8fafc;
    padding: 2rem;
}

/* Header */
.page-header {
    max-width: 1200px;
    margin: 0 auto 2rem;
}

.back-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.25rem;
    font-size: 0.875rem;
    font-weight: 500;
    color: #4a5568;
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    margin-bottom: 1rem;
}

.back-btn:hover {
    border-color: #009688;
    color: #009688;
    background: #f0fffe;
}

.page-title {
    font-size: 2rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 0.5rem 0;
}

.page-subtitle {
    font-size: 1.125rem;
    color: #64748b;
    margin: 0;
}

/* Behandlungen Grid */
.behandlungen-grid {
    display: grid;
    gap: 1rem;
    max-width: 1200px;
    margin: 0 auto;
}

/* Responsive */
@media (max-width: 768px) {
    .buchen-container {
        padding: 1rem;
    }

    .page-title {
        font-size: 1.25rem;
    }

    .page-subtitle {
        font-size: 0.875rem;
    }

    .back-btn {
        padding: 0.5rem 0.875rem;
        font-size: 0.8125rem;
        margin-bottom: 0.75rem;
    }

    .behandlungen-grid {
        grid-template-columns: 1fr;
        gap: 0.75rem;
    }
}
</style>

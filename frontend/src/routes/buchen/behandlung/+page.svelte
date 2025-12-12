<script>
    import { goto } from "$app/navigation";
    import BookingProgressBar from "$lib/components/BookingProgressBar.svelte";

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
            <button
                class="behandlung-card"
                onclick={() => handleBehandlungSelect(behandlung)}
            >
                <div class="card-icon">
                    <i class="bi bi-clipboard2-pulse"></i>
                </div>
                <div class="card-content">
                    <h3 class="behandlung-name">{behandlung.name}</h3>
                    {#if behandlung.beschreibung}
                        <p class="behandlung-beschreibung">
                            {behandlung.beschreibung}
                        </p>
                    {/if}
                </div>
                <div class="card-button">
                    <i class="bi bi-arrow-right-circle"></i>
                </div>
            </button>
        {/each}
    </div>

    {#if behandlungsarten.length === 0}
        <div class="empty-state">
            <i class="bi bi-inbox"></i>
            <p>Keine Behandlungsarten verfügbar</p>
        </div>
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

.behandlung-card {
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 16px;
    padding: 1.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: left;
    display: flex;
    gap: 1rem;
}

.behandlung-card:hover {
    border-color: #009688;
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 150, 136, 0.15);
}

.card-icon {
    flex-shrink: 0;
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.card-icon i {
    font-size: 2rem;
    color: #009688;
}

.card-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.behandlung-name {
    font-size: 1.25rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0;
}

.behandlung-beschreibung {
    font-size: 0.9375rem;
    color: #64748b;
    margin: 0;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.card-button {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-top: 1px solid #f1f5f9;
}

.card-button>i {
    font-size: 1.5rem;
    color: #009688;
}

/* Empty State */
.empty-state {
    text-align: center;
    padding: 4rem 2rem;
    color: #9ca3af;
}

.empty-state i {
    font-size: 4rem;
    margin-bottom: 1rem;
}

.empty-state p {
    font-size: 1.125rem;
    font-weight: 500;
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

    .behandlung-card {
        padding: 1rem;
        border-radius: 12px;
    }

    .card-icon {
        width: 48px;
        height: 48px;
        border-radius: 10px;
    }

    .card-icon i {
        font-size: 1.5rem;
    }

    .card-content h3 {
        font-size: 1rem;
    }

    .card-content p {
        font-size: 0.8125rem;
    }
}
</style>

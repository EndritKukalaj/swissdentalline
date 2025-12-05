<script>
    import './styles.css';
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

<script module>
    function getBehandlungIcon(name) {
        const iconMap = {
            Prophylaxe: "shield-check",
            Kontrolle: "clipboard2-check",
            Zahnreinigung: "droplet",
            Bleaching: "brightness-high",
            Füllungen: "bandaid",
            Wurzelbehandlung: "heart-pulse",
            Zahnersatz: "tooth",
            Kieferorthopädie: "arrows-angle-contract",
            Notfall: "exclamation-triangle",
        };

        const lowerName = name.toLowerCase();
        for (const [key, icon] of Object.entries(iconMap)) {
            if (lowerName.includes(key.toLowerCase())) {
                return icon;
            }
        }

        return "clipboard2-pulse";
    }
</script>

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
                    <i class="bi bi-{getBehandlungIcon(behandlung.name)}"></i>
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

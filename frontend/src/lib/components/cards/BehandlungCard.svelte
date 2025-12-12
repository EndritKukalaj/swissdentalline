<script>
  /**
   * BehandlungCard - Wiederverwendbare Card für Behandlungsarten-Auswahl
   */
  
  let { 
    icon = 'clipboard2-pulse',
    name,
    beschreibung = '',
    onClick,
    variant = 'turquoise'
  } = $props();
</script>

<button class="behandlung-card {variant}-variant" onclick={onClick}>
  <div class="card-icon">
    <i class="bi bi-{icon}"></i>
  </div>
  <div class="card-content">
    <h3 class="behandlung-name">{name}</h3>
    {#if beschreibung}
      <p class="behandlung-beschreibung">{beschreibung}</p>
    {/if}
  </div>
  <div class="card-button">
    <i class="bi bi-arrow-right-circle"></i>
  </div>
</button>

<style>
/* Behandlung Card Styles */
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
    width: 100%;
}

.behandlung-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.card-icon {
    flex-shrink: 0;
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.card-icon i {
    font-size: 2rem;
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
    justify-content: center;
    width: 40px;
    height: 40px;
    flex-shrink: 0;
    transition: transform 0.3s ease;
}

.card-button i {
    font-size: 1.5rem;
    transition: transform 0.3s ease;
}

.behandlung-card:hover .card-button i {
    transform: translateX(4px);
}

/* Turquoise Variant (Patient) */
.turquoise-variant .card-icon {
    background: linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%);
}

.turquoise-variant .card-icon i {
    color: #009688;
}

.turquoise-variant:hover {
    border-color: #009688;
    box-shadow: 0 12px 24px rgba(0, 150, 136, 0.15);
}

.turquoise-variant .card-button i {
    color: #009688;
}

/* Blue Variant (Zahnarzt) */
.blue-variant .card-icon {
    background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
}

.blue-variant .card-icon i {
    color: #30B0C7;
}

.blue-variant:hover {
    border-color: #30B0C7;
    box-shadow: 0 12px 24px rgba(48, 176, 199, 0.15);
}

.blue-variant .card-button i {
    color: #30B0C7;
}

/* Responsive */
@media (max-width: 768px) {
    .behandlung-card {
        padding: 1.25rem;
    }

    .card-icon {
        width: 50px;
        height: 50px;
    }

    .card-icon i {
        font-size: 1.75rem;
    }

    .behandlung-name {
        font-size: 1.125rem;
    }

    .behandlung-beschreibung {
        font-size: 0.875rem;
    }

    .card-button {
        width: 36px;
        height: 36px;
    }

    .card-button i {
        font-size: 1.25rem;
    }
}

@media (max-width: 480px) {
    .behandlung-card {
        padding: 1rem;
        gap: 0.75rem;
    }

    .card-icon {
        width: 45px;
        height: 45px;
    }

    .card-icon i {
        font-size: 1.5rem;
    }

    .behandlung-name {
        font-size: 1rem;
    }

    .card-button {
        display: none;
    }
}
</style>



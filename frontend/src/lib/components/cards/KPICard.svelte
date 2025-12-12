<script>
  /**
   * KpiCard - Wiederverwendbare KPI Card für Statistiken
   */
  
  let { 
    icon,
    label,
    value,
    subtext = '',
    iconType = 'auslastung',
    progressValue,
    variant = 'blue'
  } = $props();
</script>

<div class="kpi-card {variant}-variant">
  <div class="kpi-icon {iconType}">
    <i class="bi bi-{icon}"></i>
  </div>
  <div class="kpi-content">
    <div class="kpi-label">{label}</div>
    <div class="kpi-value">
      {value}
      {#if subtext}
        <span class="kpi-subtext">{subtext}</span>
      {/if}
    </div>
  </div>
  {#if progressValue !== undefined && progressValue !== null}
    <div class="kpi-bar">
      <div class="kpi-bar-fill" style="width: {progressValue}%"></div>
    </div>
  {/if}
</div>

<style>
/* KPI Card Styles */
.kpi-card {
    background: white;
    padding: 1.5rem;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;
    gap: 1rem;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.kpi-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.kpi-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(90deg, var(--icon-color-start), var(--icon-color-end));
    opacity: 0;
    transition: opacity 0.3s ease;
}

.kpi-card:hover::before {
    opacity: 1;
}

.kpi-icon {
    width: 60px;
    height: 60px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 0.5rem;
}

.kpi-icon i {
    font-size: 1.75rem;
    color: white;
}

.kpi-content {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.kpi-label {
    font-size: 0.875rem;
    color: #64748b;
    font-weight: 500;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.kpi-value {
    font-size: 2rem;
    font-weight: 700;
    color: #1a202c;
    line-height: 1;
}

.kpi-subtext {
    font-size: 1rem;
    font-weight: 500;
    color: #94a3b8;
    margin-left: 0.5rem;
}

.kpi-bar {
    height: 8px;
    background: #e2e8f0;
    border-radius: 4px;
    overflow: hidden;
    margin-top: 0.5rem;
}

.kpi-bar-fill {
    height: 100%;
    border-radius: 4px;
    transition: width 0.6s ease;
}

/* Icon Type Colors */
.kpi-icon.auslastung {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.kpi-card:has(.kpi-icon.auslastung) {
    --icon-color-start: #3b82f6;
    --icon-color-end: #2563eb;
}

.kpi-card:has(.kpi-icon.auslastung) .kpi-bar-fill {
    background: linear-gradient(90deg, #3b82f6, #2563eb);
}

.kpi-icon.einnahmen {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.kpi-card:has(.kpi-icon.einnahmen) {
    --icon-color-start: #10b981;
    --icon-color-end: #059669;
}

.kpi-icon.termine {
    background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
}

.kpi-card:has(.kpi-icon.termine) {
    --icon-color-start: #8b5cf6;
    --icon-color-end: #7c3aed;
}

.kpi-icon.bewertung {
    background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.kpi-card:has(.kpi-icon.bewertung) {
    --icon-color-start: #f59e0b;
    --icon-color-end: #d97706;
}

/* Blue Variant (Zahnarzt) */
.blue-variant.kpi-card {
    border-left: 4px solid #30B0C7;
}

/* Turquoise Variant (Patient) */
.turquoise-variant.kpi-card {
    border-left: 4px solid #009688;
}

/* Responsive */
@media (max-width: 768px) {
    .kpi-card {
        padding: 1.25rem;
    }

    .kpi-icon {
        width: 50px;
        height: 50px;
    }

    .kpi-icon i {
        font-size: 1.5rem;
    }

    .kpi-value {
        font-size: 1.75rem;
    }

    .kpi-label {
        font-size: 0.8125rem;
    }
}

@media (max-width: 480px) {
    .kpi-card {
        padding: 1rem;
    }

    .kpi-icon {
        width: 45px;
        height: 45px;
    }

    .kpi-icon i {
        font-size: 1.25rem;
    }

    .kpi-value {
        font-size: 1.5rem;
    }

    .kpi-subtext {
        font-size: 0.875rem;
    }
}
</style>

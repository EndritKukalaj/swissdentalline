<script>
  /**
   * TerminSlotCard - Wiederverwendbare Card für Termin-Zeitslots
   */
  
  let { 
    time,
    weekday,
    zahnarztName = '',
    onClick,
    variant = 'turquoise'
  } = $props();
</script>

<button class="time-slot-card {variant}-variant" onclick={onClick}>
  <div class="card-content-wrapper">
    <div class="card-info">
      <div class="time-info">
        <i class="bi bi-clock"></i>
        <span class="time">{time}</span>
        <span class="weekday">{weekday}</span>
      </div>
      
      {#if zahnarztName}
        <div class="zahnarzt-info">
          <i class="bi bi-person-badge"></i>
          <span>{zahnarztName}</span>
        </div>
      {/if}
    </div>
    
    <div class="slot-action">
      <i class="bi bi-arrow-right-circle"></i>
    </div>
  </div>
</button>

<style>
/* Time Slot Card Styles */
.time-slot-card {
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    padding: 1rem 1.25rem;
    cursor: pointer;
    transition: all 0.3s ease;
    width: 100%;
    text-align: left;
}

.time-slot-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.card-content-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
}

.card-info {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    flex: 1;
}

.time-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.time-info i {
    font-size: 1.125rem;
}

.time {
    font-size: 1.125rem;
    font-weight: 600;
    color: #1a202c;
}

.weekday {
    font-size: 0.875rem;
    color: #64748b;
    font-weight: 500;
}

.zahnarzt-info {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.875rem;
    color: #64748b;
}

.zahnarzt-info i {
    font-size: 1rem;
    color: #94a3b8;
}

.slot-action {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    flex-shrink: 0;
    transition: transform 0.3s ease;
}

.slot-action i {
    font-size: 1.5rem;
    transition: transform 0.3s ease;
}

.time-slot-card:hover .slot-action i {
    transform: translateX(4px);
}

/* Turquoise Variant (Patient) */
.turquoise-variant .time-info i {
    color: #009688;
}

.turquoise-variant:hover {
    border-color: #009688;
    box-shadow: 0 8px 16px rgba(0, 150, 136, 0.15);
}

.turquoise-variant .slot-action i {
    color: #009688;
}

/* Blue Variant (Zahnarzt) */
.blue-variant .time-info i {
    color: #30B0C7;
}

.blue-variant:hover {
    border-color: #30B0C7;
    box-shadow: 0 8px 16px rgba(48, 176, 199, 0.15);
}

.blue-variant .slot-action i {
    color: #30B0C7;
}

/* Responsive */
@media (max-width: 768px) {
    .time-slot-card {
        padding: 0.875rem 1rem;
    }

    .time {
        font-size: 1rem;
    }

    .weekday {
        font-size: 0.8125rem;
    }

    .zahnarzt-info {
        font-size: 0.8125rem;
    }

    .slot-action {
        width: 32px;
        height: 32px;
    }

    .slot-action i {
        font-size: 1.25rem;
    }
}

@media (max-width: 480px) {
    .time-slot-card {
        padding: 0.75rem;
    }

    .card-content-wrapper {
        gap: 0.5rem;
    }

    .card-info {
        gap: 0.375rem;
    }

    .time-info {
        gap: 0.5rem;
        flex-wrap: wrap;
    }

    .time {
        font-size: 0.9375rem;
    }

    .weekday {
        font-size: 0.75rem;
    }

    .slot-action {
        display: none;
    }
}
</style>

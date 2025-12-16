<script>
  /**
   * FlexTerminCard - Wiederverwendbare Card für Flex-Termine
   */
  
  let { 
    dateMonth,
    dateDay,
    behandlungName,
    discount,
    time,
    zahnarztName = '',
    dauer,
    replacementText = '',
    onClick,
    variant = 'turquoise'
  } = $props();
  
  function handleKeydown(e) {
    if (e.key === 'Enter' && onClick) {
      onClick();
    }
  }
</script>

<div 
  class="flex-termin-card {variant}-variant" 
  role="button" 
  tabindex="0" 
  onclick={onClick}
  onkeydown={handleKeydown}
>
  <!-- Date Badge -->
  <div class="date-badge">
    <div class="date-month">{dateMonth}</div>
    <div class="date-day">{dateDay}</div>
  </div>
  
  <!-- Termin Info -->
  <div class="termin-info">
    <div class="termin-header">
      <h3 class="behandlung-name">{behandlungName}</h3>
      <div class="discount-badge">
        <i class="bi bi-percent"></i>
        {discount}% Rabatt
      </div>
    </div>
    
    <div class="termin-details">
      <div class="detail-item">
        <i class="bi bi-clock"></i>
        <span>{time} Uhr</span>
      </div>
      
      {#if zahnarztName}
        <div class="detail-item">
          <i class="bi bi-person"></i>
          <span>{zahnarztName}</span>
        </div>
      {/if}
      
      <div class="detail-item">
        <i class="bi bi-hourglass-split"></i>
        <span>{dauer} Min.</span>
      </div>
    </div>
    
    {#if replacementText}
      <div class="replacement-info">
        <i class="bi bi-arrow-repeat"></i>
        <span>{replacementText}</span>
      </div>
    {/if}
  </div>
  
  <!-- Arrow Icon -->
  <div class="arrow-icon">
    <i class="bi bi-chevron-right"></i>
  </div>
</div>

<style>
/* Flex Termin Card Styles */
.flex-termin-card {
    background: white;
    border-radius: 16px;
    padding: 1.5rem;
    display: grid;
    grid-template-columns: auto 1fr auto;
    gap: 1.5rem;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    cursor: pointer;
    border-left: 4px solid transparent;
}

.flex-termin-card:hover {
    transform: translateX(8px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.flex-termin-card:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(0, 150, 136, 0.2);
}

/* Date Badge */
.date-badge {
    width: 80px;
    height: 80px;
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: 600;
    flex-shrink: 0;
}

.date-month {
    font-size: 0.875rem;
    text-transform: uppercase;
    opacity: 0.9;
    letter-spacing: 0.5px;
}

.date-day {
    font-size: 2rem;
    line-height: 1;
    margin-top: 0.25rem;
}

/* Termin Info */
.termin-info {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    min-width: 0;
}

.termin-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    flex-wrap: wrap;
}

.behandlung-name {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: #1a202c;
}

.discount-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.375rem;
    padding: 0.375rem 0.875rem;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
    border-radius: 20px;
    font-size: 0.875rem;
    font-weight: 600;
    white-space: nowrap;
}

.discount-badge i {
    font-size: 0.875rem;
}

.termin-details {
    display: flex;
    flex-wrap: wrap;
    gap: 1.5rem;
}

.detail-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #64748b;
    font-size: 0.9375rem;
}

.detail-item i {
    font-size: 1rem;
    color: #94a3b8;
}

.replacement-info {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    background: #f1f5f9;
    border-radius: 8px;
    font-size: 0.875rem;
    color: #475569;
    margin-top: 0.25rem;
}

.replacement-info i {
    font-size: 1rem;
    color: #64748b;
}

/* Arrow Icon */
.arrow-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: #f8fafc;
    transition: all 0.3s ease;
    flex-shrink: 0;
}

.arrow-icon i {
    font-size: 1.25rem;
    color: #94a3b8;
    transition: transform 0.3s ease;
}

.flex-termin-card:hover .arrow-icon {
    background: #e2e8f0;
}

.flex-termin-card:hover .arrow-icon i {
    transform: translateX(4px);
}

/* Turquoise Variant (Patient) */
.turquoise-variant .date-badge {
    background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
}

.turquoise-variant:hover {
    border-left-color: #009688;
}

.turquoise-variant:focus {
    box-shadow: 0 0 0 3px rgba(0, 150, 136, 0.2);
}

.turquoise-variant .arrow-icon i {
    color: #009688;
}

/* Blue Variant (Zahnarzt) */
.blue-variant .date-badge {
    background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
}

.blue-variant:hover {
    border-left-color: #30B0C7;
}

.blue-variant:focus {
    box-shadow: 0 0 0 3px rgba(48, 176, 199, 0.2);
}

.blue-variant .arrow-icon i {
    color: #30B0C7;
}

/* Responsive */
@media (max-width: 768px) {
    .flex-termin-card {
        grid-template-columns: auto 1fr;
        gap: 1rem;
    }

    .arrow-icon {
        display: none;
    }

    .date-badge {
        width: 70px;
        height: 70px;
    }

    .date-day {
        font-size: 1.75rem;
    }

    .behandlung-name {
        font-size: 1.125rem;
    }

    .termin-details {
        gap: 1rem;
    }
}

@media (max-width: 480px) {
    .flex-termin-card {
        padding: 1rem;
        grid-template-columns: 1fr;
    }

    .date-badge {
        width: 100%;
        height: auto;
        padding: 0.75rem;
        flex-direction: row;
        justify-content: center;
        gap: 0.5rem;
    }

    .date-day {
        font-size: 1.5rem;
        margin-top: 0;
    }

    .date-month {
        font-size: 0.875rem;
    }

    .termin-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
    }

    .termin-details {
        flex-direction: column;
        gap: 0.5rem;
    }

    .replacement-info {
        font-size: 0.8125rem;
        padding: 0.625rem 0.75rem;
    }
}
</style>

<script>
  import { enhance } from '$app/forms';
  import { goto } from '$app/navigation';
  import { InfoBanner } from '$lib';
  
  let { data, form } = $props();
  
  let formLoading = $state(false);
  let showSuccessBanner = $state(false);
  
  // Form State
  let slotForm = $state({
    behandlungsartId: '',
    datum: '',
    uhrzeit: '',
    dauerMinuten: '',
    preis: ''
  });
  
  // Auto-set duration and price when treatment is selected
  $effect(() => {
    if (slotForm.behandlungsartId) {
      const selectedBehandlung = data.behandlungsarten.find(
        b => b.id === slotForm.behandlungsartId
      );
      if (selectedBehandlung) {
        slotForm.dauerMinuten = selectedBehandlung.dauer?.toString() || '';
        slotForm.preis = selectedBehandlung.preis?.toString() || '';
      }
    }
  });
  
  // Handle form success
  $effect(() => {
    if (form?.success) {
      showSuccessBanner = true;
      // Reset form
      slotForm = {
        behandlungsartId: '',
        datum: '',
        uhrzeit: '',
        dauerMinuten: '',
        preis: ''
      };
      // Auto-hide banner
      setTimeout(() => {
        showSuccessBanner = false;
      }, 5000);
    }
  });
  
  const handleCancel = () => {
    goto('/termine');
  };
  
  // Get min date (today)
  const getMinDate = () => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  };
</script>

<div class="slots-wrapper blue-variant">
  <!-- Header -->
  <div class="slots-header">
    <button class="back-btn" onclick={handleCancel} aria-label="Zurück">
      <i class="bi bi-arrow-left"></i>
    </button>
    <div class="header-content">
      <h1 class="header-title">
        <i class="bi bi-calendar-plus"></i>
        Neuen Slot erstellen
      </h1>
      <p class="header-subtitle">Erstellen Sie einen freien Terminslot für Ihre Patienten</p>
    </div>
  </div>

  <!-- Content -->
  <div class="slots-content">
    <InfoBanner
      type="success"
      message="Slot wurde erfolgreich erstellt!"
      bind:show={showSuccessBanner}
      variant="blue"
      autoClose={true}
      autoCloseDelay={5000}
    />

    <InfoBanner
      type="error"
      message={form?.error}
      show={!!form?.error}
      variant="blue"
    />

    <!-- Slot Creation Form -->
    <form method="POST" action="?/createSlot" use:enhance={() => {
      formLoading = true;
      return async ({ update }) => {
        await update();
        formLoading = false;
      };
    }}>
      <div class="form-section">
        <div class="section-header">
          <i class="bi bi-clipboard2-pulse"></i>
          <h2>Behandlungsinformationen</h2>
        </div>

        <div class="form-grid">
          <div class="form-group span-2">
            <label for="behandlungsartId">
              <i class="bi bi-bandaid"></i>
              Behandlungsart *
            </label>
            <select 
              id="behandlungsartId" 
              name="behandlungsartId" 
              bind:value={slotForm.behandlungsartId}
              required
            >
              <option value="">-- Bitte wählen --</option>
              {#each data.behandlungsarten as behandlung}
                <option value={behandlung.id}>
                  {behandlung.name}
                </option>
              {/each}
            </select>
          </div>

          <div class="form-group">
            <label for="dauerMinuten">
              <i class="bi bi-hourglass-split"></i>
              Dauer (Minuten) *
            </label>
            <input 
              type="number" 
              id="dauerMinuten" 
              name="dauerMinuten" 
              bind:value={slotForm.dauerMinuten}
              min="15"
              step="15"
              required 
              placeholder="z.B. 30"
            />
          </div>

          <div class="form-group">
            <label for="preis">
              <i class="bi bi-cash-coin"></i>
              Preis (CHF) *
            </label>
            <input 
              type="number" 
              id="preis" 
              name="preis" 
              bind:value={slotForm.preis}
              min="0"
              step="0.05"
              required 
              placeholder="z.B. 150.00"
            />
          </div>
        </div>
      </div>

      <div class="form-section">
        <div class="section-header">
          <i class="bi bi-calendar-event"></i>
          <h2>Termin & Zeitplanung</h2>
        </div>

        <div class="form-grid">
          <div class="form-group">
            <label for="datum">
              <i class="bi bi-calendar"></i>
              Datum *
            </label>
            <input 
              type="date" 
              id="datum" 
              name="datum" 
              bind:value={slotForm.datum}
              min={getMinDate()}
              required
            />
          </div>

          <div class="form-group">
            <label for="uhrzeit">
              <i class="bi bi-clock"></i>
              Uhrzeit *
            </label>
            <input 
              type="time" 
              id="uhrzeit" 
              name="uhrzeit" 
              bind:value={slotForm.uhrzeit}
              required
            />
          </div>
        </div>
      </div>

      <!-- Mehrerfassung Feature - Coming Soon -->
      <div class="coming-soon-section">
        <button type="button" class="btn-coming-soon" disabled>
          <div class="coming-soon-content">
            <div class="coming-soon-icon">
              <i class="bi bi-grid-3x3-gap"></i>
            </div>
            <div class="coming-soon-text">
              <span class="coming-soon-title">Mehrfacherfassung (Serientermine)</span>
              <span class="coming-soon-badge">Bald verfügbar</span>
            </div>
          </div>
          <i class="bi bi-lock-fill"></i>
        </button>
        <p class="coming-soon-description">
          <i class="bi bi-info-circle"></i>
          Erstellen Sie in Zukunft mehrere Slots auf einmal mit flexiblen Serienmustern (täglich, wöchentlich, monatlich)
        </p>
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn-secondary" onclick={handleCancel} disabled={formLoading}>
          <i class="bi bi-x-circle"></i>
          Abbrechen
        </button>
        <button type="submit" class="btn btn-primary" disabled={formLoading}>
          {#if formLoading}
            <i class="bi bi-arrow-repeat spinning"></i>
            Erstellen...
          {:else}
            <i class="bi bi-check-circle"></i>
            Slot erstellen
          {/if}
        </button>
      </div>
    </form>
  </div>
</div>


<style>
/* Slots Page Specific Styles */

.slots-wrapper {
    min-height: 100vh;
    background: linear-gradient(to bottom, #f8fafc 0%, #e2e8f0 100%);
    --slots-primary: #30B0C7;
    --slots-accent: #268a9c;
    --slots-gradient: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
}

/* Header */
.slots-header {
    background: var(--slots-gradient);
    padding: 2rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    position: relative;
}

.back-btn {
    position: absolute;
    top: 1rem;
    left: 1rem;
    width: 40px;
    height: 40px;
    background: rgba(255, 255, 255, 0.95);
    border: none;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.back-btn i {
    font-size: 1.125rem;
    color: #1a202c;
}

.back-btn:hover {
    background: white;
    transform: translateX(-4px);
}

.header-content {
    max-width: 900px;
    margin: 0 auto;
    padding-top: 1rem;
    text-align: center;
    color: white;
}

.header-title {
    font-size: 2rem;
    color: white;
    font-weight: 700;
    margin: 0 0 0.5rem 0;
    display: block;
}

.header-title i {
    font-size: 1.75rem;
}

.header-subtitle {
    font-size: 1rem;
    opacity: 0.95;
    margin: 0;
}

/* Content */
.slots-content {
    max-width: 900px;
    margin: 0 auto;
    padding: 2rem;
}

/* Form Section */
.form-section {
    background: white;
    padding: 2rem;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    margin-bottom: 1.5rem;
}

.section-header {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    margin-bottom: 1.5rem;
    padding-bottom: 0.75rem;
    border-bottom: 2px solid #e2e8f0;
}

.section-header i {
    font-size: 1.5rem;
    color: var(--slots-primary);
}

.section-header h2 {
    font-size: 1.25rem;
    font-weight: 600;
    color: #1a202c;
    margin: 0;
}

/* Form Grid */
.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.25rem;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.form-group.span-2 {
    grid-column: span 2;
}

.form-group label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-weight: 500;
    color: #475569;
    font-size: 0.9375rem;
}

.form-group label i {
    color: var(--slots-primary);
    font-size: 1rem;
}

.form-group input,
.form-group select {
    padding: 0.75rem 1rem;
    border: 2px solid #e2e8f0;
    border-radius: 10px;
    font-size: 1rem;
    transition: all 0.3s ease;
    background: white;
}

.form-group input:focus,
.form-group select:focus {
    outline: none;
    border-color: var(--slots-primary);
    box-shadow: 0 0 0 3px rgba(48, 176, 199, 0.1);
}

.form-group input[type="number"]::-webkit-inner-spin-button,
.form-group input[type="number"]::-webkit-outer-spin-button {
    opacity: 1;
}

/* Form Actions */
.form-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    padding-top: 1.5rem;
}

/* Coming Soon Section */
.coming-soon-section {
    margin-bottom: 1.5rem;
}

.btn-coming-soon {
    width: 100%;
    padding: 1.25rem 1.5rem;
    background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
    border: 2px dashed #cbd5e1;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    cursor: not-allowed;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
}

.btn-coming-soon::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    animation: shimmer 3s infinite;
}

@keyframes shimmer {
    0% {
        left: -100%;
    }
    50%, 100% {
        left: 100%;
    }
}

.coming-soon-content {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.coming-soon-icon {
    width: 48px;
    height: 48px;
    background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.coming-soon-icon i {
    font-size: 1.5rem;
    color: #64748b;
}

.coming-soon-text {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
    text-align: left;
}

.coming-soon-title {
    font-size: 1rem;
    font-weight: 600;
    color: #475569;
}

.coming-soon-badge {
    display: inline-flex;
    align-items: center;
    padding: 0.25rem 0.625rem;
    background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
    color: white;
    font-size: 0.75rem;
    font-weight: 600;
    border-radius: 6px;
    width: fit-content;
    text-transform: uppercase;
    letter-spacing: 0.025em;
}

.btn-coming-soon > i {
    font-size: 1.25rem;
    color: #94a3b8;
}

.coming-soon-description {
    margin: 0.75rem 0 0 0;
    padding: 0.875rem 1rem;
    background: rgba(241, 245, 249, 0.5);
    border-left: 3px solid #cbd5e1;
    border-radius: 8px;
    color: #64748b;
    font-size: 0.875rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    line-height: 1.5;
}

.coming-soon-description i {
    color: #94a3b8;
    font-size: 1rem;
    flex-shrink: 0;
}


.btn {
    padding: 0.75rem 1.5rem;
    border-radius: 10px;
    font-weight: 500;
    font-size: 1rem;
    cursor: pointer;
    transition: all 0.3s ease;
    border: none;
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.btn i {
    font-size: 1.125rem;
}

.btn-primary {
    background: var(--slots-gradient);
    color: white;
    box-shadow: 0 2px 12px rgba(48, 176, 199, 0.3);
}

.btn-primary:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(48, 176, 199, 0.4);
}

.btn-primary:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-secondary {
    background: #e2e8f0;
    color: #475569;
}

.btn-secondary:hover:not(:disabled) {
    background: #cbd5e1;
}

.btn-secondary:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.spinning {
    animation: spin 1s linear infinite;
}

@keyframes spin {
    from {
        transform: rotate(0deg);
    }
    to {
        transform: rotate(360deg);
    }
}

@keyframes slideDown {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* Mobile Responsive */
@media (max-width: 768px) {
    .slots-header {
        padding: 1.5rem 1rem;
    }

    .back-btn {
        width: 36px;
        height: 36px;
        top: 0.875rem;
        left: 0.875rem;
    }

    .header-title {
        font-size: 1.5rem;
    }

    .header-title i {
        font-size: 1.25rem;
    }

    .header-subtitle {
        font-size: 0.875rem;
    }

    .slots-content {
        padding: 1.5rem 1rem;
    }

    .form-section {
        padding: 1.25rem;
    }

    .form-grid {
        grid-template-columns: 1fr;
    }

    .form-group.span-2 {
        grid-column: span 1;
    }

    .form-actions {
        flex-direction: column;
    }

    .btn {
        width: 100%;
        justify-content: center;
    }
    
    .btn-coming-soon {
        padding: 1rem;
    }
    
    .coming-soon-content {
        gap: 0.75rem;
    }
    
    .coming-soon-icon {
        width: 40px;
        height: 40px;
    }
    
    .coming-soon-icon i {
        font-size: 1.25rem;
    }
    
    .coming-soon-title {
        font-size: 0.9375rem;
    }
    
    .coming-soon-description {
        font-size: 0.8125rem;
        padding: 0.75rem;
    }
}
</style>
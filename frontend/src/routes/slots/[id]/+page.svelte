<script>
  import { enhance } from '$app/forms';
  import { goto } from '$app/navigation';
  
  let { data, form } = $props();
  
  let formLoading = $state(false);
  
  // Extract date and time from termin.datum
  const extractDateTime = (isoString) => {
    const date = new Date(isoString);
    const datum = date.toISOString().split('T')[0];
    const uhrzeit = date.toTimeString().slice(0, 5);
    return { datum, uhrzeit };
  };
  
  const { datum: initialDatum, uhrzeit: initialUhrzeit } = extractDateTime(data.termin.datum);
  
  // Form State
  let slotForm = $state({
    behandlungsartId: data.termin.behandlungsartId || '',
    datum: initialDatum,
    uhrzeit: initialUhrzeit,
    dauerMinuten: data.termin.dauerMinuten?.toString() || data.termin.dauer_minuten?.toString() || '',
    preis: data.termin.preis?.toString() || ''
  });
  
  // Auto-set duration and price when treatment is selected
  $effect(() => {
    if (slotForm.behandlungsartId) {
      const selectedBehandlung = data.behandlungsarten.find(
        b => b.id === slotForm.behandlungsartId
      );
      if (selectedBehandlung) {
        slotForm.dauerMinuten = selectedBehandlung.dauer?.toString() || slotForm.dauerMinuten;
        slotForm.preis = selectedBehandlung.preis?.toString() || slotForm.preis;
      }
    }
  });
  
  const handleCancel = () => {
    goto(`/termine/${data.termin.id}`);
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
        <i class="bi bi-pencil-square"></i>
        Slot bearbeiten
      </h1>
      <p class="header-subtitle">Bearbeiten Sie die Details des freien Terminslots</p>
    </div>
  </div>

  <!-- Content -->
  <div class="slots-content">
    {#if form?.error}
      <div class="alert alert-error">
        <i class="bi bi-exclamation-circle"></i>
        <span>{form.error}</span>
      </div>
    {/if}

    <!-- Slot Edit Form -->
    <form method="POST" action="?/updateSlot" use:enhance={() => {
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
              step="0.01"
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

      <div class="form-actions">
        <button type="button" class="btn btn-secondary" onclick={handleCancel} disabled={formLoading}>
          <i class="bi bi-x-circle"></i>
          Abbrechen
        </button>
        <button type="submit" class="btn btn-primary" disabled={formLoading}>
          {#if formLoading}
            <i class="bi bi-arrow-repeat spinning"></i>
            Speichern...
          {:else}
            <i class="bi bi-check-circle"></i>
            Änderungen speichern
          {/if}
        </button>
      </div>
    </form>
  </div>
</div>

<style>
/* Slots Edit Page Specific Styles */

.slots-wrapper {
    min-height: 100vh;
    background: linear-gradient(to bottom, #f8fafc 0%, #e2e8f0 100%);
    --slots-primary: #30B0C7;
    --slots-accent: #268a9c;
    --slots-gradient: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
}

.blue-variant {
    --slots-primary: #30B0C7;
    --slots-accent: #268a9c;
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
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.75rem;
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

/* Alerts */
.alert {
    padding: 1rem 1.25rem;
    border-radius: 12px;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    margin-bottom: 1.5rem;
    animation: slideDown 0.3s ease;
}

.alert i {
    font-size: 1.25rem;
}

.alert-error {
    background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
    color: #721c24;
    border: 1px solid #f5c6cb;
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

.form-group select {
    cursor: pointer;
}

/* Form Actions */
.form-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    padding-top: 1.5rem;
}

.btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.875rem 1.75rem;
    font-size: 1rem;
    font-weight: 600;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.btn-secondary {
    background: white;
    color: #4a5568;
    border: 2px solid #cbd5e0;
}

.btn-secondary:hover:not(:disabled) {
    background: #f7fafc;
    border-color: #a0aec0;
}

.btn-primary {
    background: var(--slots-gradient);
    color: white;
    box-shadow: 0 4px 12px rgba(48, 176, 199, 0.3);
}

.btn-primary:hover:not(:disabled) {
    box-shadow: 0 6px 16px rgba(48, 176, 199, 0.4);
    transform: translateY(-2px);
}

.btn i {
    font-size: 1.125rem;
}

/* Spinning Animation */
@keyframes spin {
    from {
        transform: rotate(0deg);
    }
    to {
        transform: rotate(360deg);
    }
}

.spinning {
    animation: spin 1s linear infinite;
}

/* Responsive */
@media (max-width: 768px) {
    .slots-header {
        padding: 1.5rem 1rem;
    }

    .back-btn {
        width: 36px;
        height: 36px;
    }

    .back-btn i {
        font-size: 1rem;
    }

    .header-content {
        padding-top: 0.5rem;
    }

    .header-title {
        font-size: 1.5rem;
        flex-direction: column;
        gap: 0.5rem;
    }

    .header-title i {
        font-size: 1.5rem;
    }

    .header-subtitle {
        font-size: 0.875rem;
    }

    .slots-content {
        padding: 1rem;
    }

    .form-section {
        padding: 1.25rem;
        margin-bottom: 1rem;
    }

    .section-header {
        margin-bottom: 1rem;
        padding-bottom: 0.75rem;
    }

    .section-header i {
        font-size: 1.25rem;
    }

    .section-header h2 {
        font-size: 1.125rem;
    }

    .form-grid {
        grid-template-columns: 1fr;
        gap: 1rem;
    }

    .form-group.span-2 {
        grid-column: span 1;
    }

    .form-group label {
        font-size: 0.875rem;
    }

    .form-group input,
    .form-group select {
        padding: 0.625rem 0.875rem;
        font-size: 0.9375rem;
    }

    .form-actions {
        flex-direction: column-reverse;
        gap: 0.75rem;
        padding-top: 1rem;
    }

    .btn {
        width: 100%;
        justify-content: center;
        padding: 0.75rem 1.5rem;
        font-size: 0.9375rem;
    }

    .alert {
        padding: 0.875rem 1rem;
        font-size: 0.875rem;
    }

    .alert i {
        font-size: 1.125rem;
    }
}

@media (max-width: 480px) {
    .slots-header {
        padding: 1.25rem 0.75rem;
    }

    .header-title {
        font-size: 1.25rem;
    }

    .header-subtitle {
        font-size: 0.8125rem;
    }

    .slots-content {
        padding: 0.75rem;
    }

    .form-section {
        padding: 1rem;
    }

    .section-header h2 {
        font-size: 1rem;
    }

    .form-group label {
        font-size: 0.8125rem;
    }

    .form-group input,
    .form-group select {
        padding: 0.5rem 0.75rem;
        font-size: 0.875rem;
    }

    .btn {
        padding: 0.625rem 1.25rem;
        font-size: 0.875rem;
    }
}
</style>

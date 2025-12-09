<script>
  import '../styles.css';
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

<script>
  import { goto } from '$app/navigation';
  import { enhance } from '$app/forms';
  
  let { data, form } = $props();

  let showNewAdresseForm = $state(false);
  let formLoading = $state(false);
  let adresseFormLoading = $state(false);

  const formatDateForInput = (instant) => {
    if (!instant) return '';
    const date = new Date(instant);
    return date.toISOString().split('T')[0];
  };

  // Variant ableiten (Patient = turquoise, Zahnarzt = purple)
  const variant = data.userRole === 'Zahnarzt' ? 'purple' : 'turquoise';

  // Patient Form State
  let patientForm = $state({
    name: data.entityData?.name || data.profile.name || '',
    geburtsdatum: formatDateForInput(data.entityData?.geburtsdatum || data.profile.geburtsdatum) || '',
    krankenkasse: data.entityData?.krankenkasse || data.profile.krankenkasse || '',
    adresseId: data.currentAdresse?.id || '',
    strasse: data.currentAdresse?.strasse || '',
    plz: data.currentAdresse?.plz || '',
    ort: data.currentAdresse?.ort || ''
  });

  // Zahnarzt Form State
  let zahnarztForm = $state({
    name: data.entityData?.name || data.profile.name || '',
    praxisAdresseId: data.entityData?.praxisAdresseId || ''
  });

  // Neue Adresse Form State
  let newAdresseForm = $state({
    strasse: '',
    plz: '',
    ort: '',
    typ: data.userRole === 'Zahnarzt' ? 'PRAXIS' : 'HOME',
    bezeichnung: ''
  });

  const handleCancel = () => {
    goto('/profil');
  };

  const toggleNewAdresseForm = () => {
    showNewAdresseForm = !showNewAdresseForm;
    if (!showNewAdresseForm) {
      // Reset form
      newAdresseForm = {
        strasse: '',
        plz: '',
        ort: '',
        typ: data.userRole === 'Zahnarzt' ? 'PRAXIS' : 'HOME',
        bezeichnung: ''
      };
    }
  };
</script>

<div class="edit-wrapper {variant}-variant">
  <!-- Header -->
  <div class="edit-header">
    <button class="back-btn" onclick={handleCancel} aria-label="Zurück">
      <i class="bi bi-arrow-left"></i>
    </button>
    <div class="header-content">
      <h1 class="header-title">
        <i class="bi bi-pencil-square"></i>
        Profil bearbeiten
      </h1>
      <p class="header-subtitle">Aktualisieren Sie Ihre persönlichen Informationen</p>
    </div>
  </div>

  <!-- Content -->
  <div class="edit-content">
    {#if !data.entityId}
      <div class="alert alert-error">
        <i class="bi bi-exclamation-triangle"></i>
        <span>Fehler: Ihre Benutzer-ID konnte nicht geladen werden. Bitte laden Sie die Seite neu oder kontaktieren Sie den Support.</span>
      </div>
    {/if}

    {#if form?.error}
      <div class="alert alert-error">
        <i class="bi bi-exclamation-circle"></i>
        <span>{form.error}</span>
      </div>
    {/if}

    {#if form?.success && form?.adresse}
      <div class="alert alert-success">
        <i class="bi bi-check-circle"></i>
        <span>Adresse erfolgreich erstellt!</span>
      </div>
    {/if}

    <!-- Patient Edit Form -->
    {#if data.userRole === 'Patient'}
      <form method="POST" action="?/updatePatient" use:enhance={() => {
        formLoading = true;
        return async ({ update }) => {
          await update();
          formLoading = false;
        };
      }}>
        <input type="hidden" name="id" value={data.entityId || ''} />
        
        <div class="form-section">
          <div class="section-header">
            <i class="bi bi-person-fill"></i>
            <h2>Persönliche Daten</h2>
          </div>

          <div class="form-grid">
            <div class="form-group">
              <label for="name">
                <i class="bi bi-person-badge"></i>
                Name
              </label>
              <input 
                type="text" 
                id="name" 
                name="name" 
                bind:value={patientForm.name}
                readonly
                placeholder="Ihr vollständiger Name"
              />
              <small class="help-text">Der Name kann nicht geändert werden</small>
            </div>

            <div class="form-group">
              <label for="email">
                <i class="bi bi-envelope"></i>
                E-Mail
              </label>
              <input 
                type="email" 
                id="email" 
                name="email" 
                value={data.profile.email}
                disabled
                placeholder="ihre.email@beispiel.ch"
              />
              <small class="help-text">Die E-Mail-Adresse kann nicht geändert werden</small>
            </div>

            <div class="form-group">
              <label for="geburtsdatum">
                <i class="bi bi-calendar-event"></i>
                Geburtsdatum
              </label>
              <input 
                type="date" 
                id="geburtsdatum" 
                name="geburtsdatum" 
                bind:value={patientForm.geburtsdatum}
              />
            </div>

            <div class="form-group">
              <label for="krankenkasse">
                <i class="bi bi-hospital"></i>
                Krankenkasse
              </label>
              <input 
                type="text" 
                id="krankenkasse" 
                name="krankenkasse" 
                bind:value={patientForm.krankenkasse}
                placeholder="z.B. Swica, CSS, Helsana"
              />
            </div>
          </div>
        </div>

        <input type="hidden" name="adresseId" value={patientForm.adresseId} />
        
        <div class="form-section">
          <div class="section-header">
            <i class="bi bi-geo-alt-fill"></i>
            <h2>Wohnadresse</h2>
          </div>

          <div class="form-grid">
            <div class="form-group span-2">
              <label for="strasse">
                <i class="bi bi-signpost"></i>
                Strasse & Nr.
              </label>
              <input 
                type="text" 
                id="strasse" 
                name="strasse" 
                bind:value={patientForm.strasse}
                required 
                placeholder="z.B. Musterstrasse 123"
              />
            </div>

            <div class="form-group">
              <label for="plz">
                <i class="bi bi-mailbox"></i>
                PLZ
              </label>
              <input 
                type="text" 
                id="plz" 
                name="plz" 
                bind:value={patientForm.plz}
                required 
                placeholder="8000"
              />
            </div>

            <div class="form-group">
              <label for="ort">
                <i class="bi bi-pin-map"></i>
                Ort
              </label>
              <input 
                type="text" 
                id="ort" 
                name="ort" 
                bind:value={patientForm.ort}
                required 
                placeholder="Zürich"
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
    {/if}

    <!-- Zahnarzt Edit Form -->
    {#if data.userRole === 'Zahnarzt'}
      <form method="POST" action="?/updateZahnarzt" use:enhance={() => {
        formLoading = true;
        return async ({ update }) => {
          await update();
          formLoading = false;
        };
      }}>
        <input type="hidden" name="id" value={data.entityId || ''} />
        
        <div class="form-section">
          <div class="section-header">
            <i class="bi bi-person-fill"></i>
            <h2>Persönliche Daten</h2>
          </div>

          <div class="form-grid">
            <div class="form-group">
              <label for="name">
                <i class="bi bi-person-badge"></i>
                Name
              </label>
              <input 
                type="text" 
                id="name" 
                name="name" 
                bind:value={zahnarztForm.name}
                readonly
                placeholder="Ihr vollständiger Name (z.B. Dr. Max Muster)"
              />
              <small class="help-text">Der Name kann nicht geändert werden</small>
            </div>

            <div class="form-group">
              <label for="email">
                <i class="bi bi-envelope"></i>
                E-Mail
              </label>
              <input 
                type="email" 
                id="email" 
                name="email" 
                value={data.profile.email}
                disabled
                placeholder="ihre.email@beispiel.ch"
              />
              <small class="help-text">Die E-Mail-Adresse kann nicht geändert werden</small>
            </div>
          </div>
        </div>

        <div class="form-section">
          <div class="section-header">
            <i class="bi bi-building"></i>
            <h2>Praxisinformationen</h2>
          </div>

          <div class="form-group">
            <label for="praxisAdresseId">
              <i class="bi bi-geo-alt"></i>
              Praxisadresse auswählen
            </label>
            <select 
              id="praxisAdresseId" 
              name="praxisAdresseId" 
              bind:value={zahnarztForm.praxisAdresseId}
              required
            >
              <option value="">-- Bitte wählen --</option>
              {#each data.adressen.filter(a => a.typ === 'PRAXIS') as adresse}
                <option value={adresse.id}>
                  {adresse.bezeichnung ? `${adresse.bezeichnung} - ` : ''}{adresse.strasse}, {adresse.plz} {adresse.ort}
                </option>
              {/each}
            </select>
          </div>

          <button type="button" class="btn-link" onclick={toggleNewAdresseForm}>
            <i class="bi bi-plus-circle"></i>
            {showNewAdresseForm ? 'Formular schliessen' : 'Neue Praxisadresse erstellen'}
          </button>
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
    {/if}

    <!-- Neue Adresse Formular (Collapsible) -->
    {#if showNewAdresseForm}
      <div class="new-adresse-section">
        <form method="POST" action="?/createAdresse" use:enhance={() => {
          adresseFormLoading = true;
          return async ({ update }) => {
            await update({ reset: false });
            adresseFormLoading = false;
          };
        }}>
          <div class="form-section">
            <div class="section-header">
              <i class="bi bi-plus-square"></i>
              <h2>Neue {data.userRole === 'Zahnarzt' ? 'Praxis' : 'Wohn'}adresse erstellen</h2>
            </div>

            <div class="form-grid">
              <div class="form-group span-2">
                <label for="strasse">
                  <i class="bi bi-signpost"></i>
                  Strasse & Nr.
                </label>
                <input 
                  type="text" 
                  id="strasse" 
                  name="strasse" 
                  bind:value={newAdresseForm.strasse}
                  required 
                  placeholder="z.B. Musterstrasse 123"
                />
              </div>

              <div class="form-group">
                <label for="plz">
                  <i class="bi bi-mailbox"></i>
                  PLZ
                </label>
                <input 
                  type="text" 
                  id="plz" 
                  name="plz" 
                  bind:value={newAdresseForm.plz}
                  required 
                  placeholder="8000"
                />
              </div>

              <div class="form-group">
                <label for="ort">
                  <i class="bi bi-pin-map"></i>
                  Ort
                </label>
                <input 
                  type="text" 
                  id="ort" 
                  name="ort" 
                  bind:value={newAdresseForm.ort}
                  required 
                  placeholder="Zürich"
                />
              </div>

              {#if data.userRole === 'Zahnarzt'}
                <div class="form-group span-2">
                  <label for="bezeichnung">
                    <i class="bi bi-tag"></i>
                    Praxisbezeichnung
                  </label>
                  <input 
                    type="text" 
                    id="bezeichnung" 
                    name="bezeichnung" 
                    bind:value={newAdresseForm.bezeichnung}
                    placeholder="z.B. Zahnarztpraxis Dr. Muster"
                  />
                </div>
              {/if}

              <input type="hidden" name="typ" value={newAdresseForm.typ} />
            </div>

            <div class="form-actions">
              <button type="button" class="btn btn-secondary" onclick={toggleNewAdresseForm} disabled={adresseFormLoading}>
                <i class="bi bi-x-circle"></i>
                Abbrechen
              </button>
              <button type="submit" class="btn btn-success" disabled={adresseFormLoading}>
                {#if adresseFormLoading}
                  <i class="bi bi-arrow-repeat spinning"></i>
                  Erstellen...
                {:else}
                  <i class="bi bi-plus-circle"></i>
                  Adresse erstellen
                {/if}
              </button>
            </div>
          </div>
        </form>
      </div>
    {/if}
  </div>
</div>

<style>
  .edit-wrapper {
    min-height: 100vh;
    background: linear-gradient(to bottom, #f8fafc 0%, #e2e8f0 100%);
    --edit-primary: #009688;
    --edit-accent: #00bfa5;
    --edit-gradient: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
  }

  .edit-wrapper.purple-variant {
    --edit-primary: #8E24AA;
    --edit-accent: #AB47BC;
    --edit-gradient: linear-gradient(135deg, #8E24AA 0%, #AB47BC 100%);
  }

  /* Header */
  .edit-header {
    background: var(--edit-gradient);
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
  .edit-content {
    max-width: 900px;
    margin: 0 auto;
    padding: 2rem;
  }

  /* Alerts */
  .alert {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 1rem 1.25rem;
    border-radius: 12px;
    margin-bottom: 1.5rem;
    font-weight: 500;
  }

  .alert i {
    font-size: 1.25rem;
  }

  .alert-error {
    background: #fee;
    color: #c33;
    border: 1px solid #fcc;
  }

  .alert-success {
    background: #efe;
    color: #3c3;
    border: 1px solid #cfc;
  }

  /* Form Section */
  .form-section {
    background: white;
    border-radius: 16px;
    padding: 1.5rem;
    margin-bottom: 1.5rem;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 2px solid #f1f5f9;
  }

  .section-header i {
    font-size: 1.5rem;
    color: var(--edit-primary);
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
    font-size: 0.875rem;
    font-weight: 600;
    color: #475569;
  }

  .form-group label i {
    font-size: 1rem;
    color: var(--edit-primary);
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
    border-color: var(--edit-primary);
    box-shadow: 0 0 0 3px rgba(0, 150, 136, 0.1);
  }

  .form-group input::placeholder {
    color: #94a3b8;
  }

  .form-group input:read-only {
    background-color: #f1f5f9;
    color: #64748b;
    cursor: not-allowed;
  }

  /* Button Link */
  .btn-link {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1rem;
    background: transparent;
    border: 2px dashed #cbd5e1;
    border-radius: 10px;
    color: var(--edit-primary);
    font-size: 0.875rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-top: 0.5rem;
  }

  .btn-link:hover {
    background: rgba(0, 150, 136, 0.05);
    border-color: var(--edit-primary);
  }

  .btn-link i {
    font-size: 1rem;
  }

  /* Form Actions */
  .form-actions {
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
    margin-top: 2rem;
  }

  .btn {
    display: flex;
    align-items: center;
    gap: 0.625rem;
    padding: 0.875rem 1.75rem;
    border: none;
    border-radius: 10px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .btn i {
    font-size: 1.125rem;
  }

  .btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .btn-primary {
    background: var(--edit-gradient);
    color: white;
  }

  .btn-primary:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }

  .btn-secondary {
    background: #e2e8f0;
    color: #475569;
  }

  .btn-secondary:hover:not(:disabled) {
    background: #cbd5e1;
  }

  .btn-success {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
  }

  .btn-success:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3);
  }

  /* Spinning Icon */
  @keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }

  .spinning {
    animation: spin 1s linear infinite;
  }

  /* New Adresse Section */
  .new-adresse-section {
    margin-top: 1.5rem;
    animation: slideDown 0.3s ease;
  }

  @keyframes slideDown {
    from {
      opacity: 0;
      transform: translateY(-20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  /* Mobile Responsive */
  @media (max-width: 768px) {
    .edit-header {
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

    .edit-content {
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
  }
</style>

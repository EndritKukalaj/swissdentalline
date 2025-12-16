<script>
  import { goto } from '$app/navigation';
  import { enhance } from '$app/forms';
  import { InfoBanner } from '$lib';
  
  let { data, form } = $props();

  let showNewAdresseForm = $state(false);
  let showEditAdresseForm = $state(false);
  let formLoading = $state(false);
  let adresseFormLoading = $state(false);

  const formatDateForInput = (instant) => {
    if (!instant) return '';
    const date = new Date(instant);
    return date.toISOString().split('T')[0];
  };

  // Variant ableiten (Patient = turquoise, Zahnarzt = blue)
  const variant = data.userRole === 'Zahnarzt' ? 'blue' : 'turquoise';

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

  // Edit Adresse Form State
  let editAdresseForm = $state({
    id: '',
    strasse: '',
    plz: '',
    ort: '',
    bezeichnung: ''
  });

  const handleCancel = () => {
    goto('/profil');
  };

  // Close edit form after successful update
  $effect(() => {
    if (form?.success && form?.updated) {
      showEditAdresseForm = false;
    }
    // Close new form and select newly created address
    if (form?.success && form?.adresse) {
      showNewAdresseForm = false;
      // Wähle die neu erstellte Praxis aus
      if (data.userRole === 'Zahnarzt' && form.adresse.id) {
        zahnarztForm.praxisAdresseId = form.adresse.id;
      }
    }
  });

  const toggleNewAdresseForm = () => {
    showNewAdresseForm = !showNewAdresseForm;
    if (showNewAdresseForm) {
      // Schließe Edit-Formular wenn New-Formular geöffnet wird
      showEditAdresseForm = false;
    }
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

  const toggleEditAdresseForm = () => {
    if (!zahnarztForm.praxisAdresseId) {
      return; // Keine Praxis ausgewählt
    }

    showEditAdresseForm = !showEditAdresseForm;
    
    if (showEditAdresseForm) {
      // Schließe New-Formular wenn Edit-Formular geöffnet wird
      showNewAdresseForm = false;
      
      // Lade ausgewählte Praxis-Daten
      const selectedAdresse = data.adressen.find(a => a.id === zahnarztForm.praxisAdresseId);
      if (selectedAdresse) {
        editAdresseForm = {
          id: selectedAdresse.id,
          strasse: selectedAdresse.strasse,
          plz: selectedAdresse.plz,
          ort: selectedAdresse.ort,
          bezeichnung: selectedAdresse.bezeichnung || ''
        };
      }
    } else {
      // Reset form
      editAdresseForm = {
        id: '',
        strasse: '',
        plz: '',
        ort: '',
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
    <InfoBanner
      type="error"
      message="Fehler: Ihre Benutzer-ID konnte nicht geladen werden. Bitte laden Sie die Seite neu oder kontaktieren Sie den Support."
      show={!data.entityId}
      variant={data.userRole === 'Zahnarzt' ? 'blue' : 'turquoise'}
    />

    <InfoBanner
      type="error"
      message={form?.error}
      show={!!form?.error}
      variant={data.userRole === 'Zahnarzt' ? 'blue' : 'turquoise'}
    />

    <InfoBanner
      type="success"
      message="Adresse erfolgreich erstellt!"
      show={!!(form?.success && form?.adresse)}
      variant={data.userRole === 'Zahnarzt' ? 'blue' : 'turquoise'}
      autoClose={true}
    />

    <InfoBanner
      type="success"
      message="Adresse erfolgreich aktualisiert!"
      show={!!(form?.success && form?.updated)}
      variant={data.userRole === 'Zahnarzt' ? 'blue' : 'turquoise'}
      autoClose={true}
    />

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
            <div class="input-with-action">
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
              {#if zahnarztForm.praxisAdresseId}
                <button 
                  type="button" 
                  class="btn-icon" 
                  onclick={toggleEditAdresseForm}
                  title="Praxis bearbeiten"
                >
                  <i class="bi bi-pencil"></i>
                </button>
              {/if}
            </div>
          </div>

          <div class="action-buttons">
            <button type="button" class="btn-link" onclick={toggleEditAdresseForm} disabled={!zahnarztForm.praxisAdresseId}>
              <i class="bi bi-pencil-square"></i>
              {showEditAdresseForm ? 'Bearbeitung schliessen' : 'Ausgewählte Praxis bearbeiten'}
            </button>
            <button type="button" class="btn-link" onclick={toggleNewAdresseForm}>
              <i class="bi bi-plus-circle"></i>
              {showNewAdresseForm ? 'Formular schliessen' : 'Neue Praxisadresse erstellen'}
            </button>
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

    <!-- Edit Adresse Formular (Collapsible) -->
    {#if showEditAdresseForm}
      <div class="edit-adresse-section">
        <form method="POST" action="?/updateAdresse" use:enhance={() => {
          adresseFormLoading = true;
          return async ({ update }) => {
            await update({ reset: false });
            adresseFormLoading = false;
          };
        }}>
          <input type="hidden" name="id" value={editAdresseForm.id} />
          <input type="hidden" name="typ" value="PRAXIS" />
          
          <div class="form-section">
            <div class="section-header">
              <i class="bi bi-pencil-square"></i>
              <h2>Praxisadresse bearbeiten</h2>
            </div>

            <div class="form-grid">
              <div class="form-group span-2">
                <label for="edit-strasse">
                  <i class="bi bi-signpost"></i>
                  Strasse & Nr.
                </label>
                <input 
                  type="text" 
                  id="edit-strasse" 
                  name="strasse" 
                  bind:value={editAdresseForm.strasse}
                  required 
                  placeholder="z.B. Musterstrasse 123"
                />
              </div>

              <div class="form-group">
                <label for="edit-plz">
                  <i class="bi bi-mailbox"></i>
                  PLZ
                </label>
                <input 
                  type="text" 
                  id="edit-plz" 
                  name="plz" 
                  bind:value={editAdresseForm.plz}
                  required 
                  placeholder="8000"
                />
              </div>

              <div class="form-group">
                <label for="edit-ort">
                  <i class="bi bi-pin-map"></i>
                  Ort
                </label>
                <input 
                  type="text" 
                  id="edit-ort" 
                  name="ort" 
                  bind:value={editAdresseForm.ort}
                  required 
                  placeholder="Zürich"
                />
              </div>

              <div class="form-group span-2">
                <label for="edit-bezeichnung">
                  <i class="bi bi-tag"></i>
                  Praxisbezeichnung
                </label>
                <input 
                  type="text" 
                  id="edit-bezeichnung" 
                  name="bezeichnung" 
                  bind:value={editAdresseForm.bezeichnung}
                  placeholder="z.B. Zahnarztpraxis Dr. Muster"
                />
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn btn-secondary" onclick={toggleEditAdresseForm} disabled={adresseFormLoading}>
                <i class="bi bi-x-circle"></i>
                Abbrechen
              </button>
              <button type="submit" class="btn btn-primary" disabled={adresseFormLoading}>
                {#if adresseFormLoading}
                  <i class="bi bi-arrow-repeat spinning"></i>
                  Speichern...
                {:else}
                  <i class="bi bi-check-circle"></i>
                  Änderungen speichern
                {/if}
              </button>
            </div>
          </div>
        </form>
      </div>
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
/* Profile Edit Page Specific Styles */

.edit-wrapper {
    min-height: 100vh;
    background: linear-gradient(to bottom, #f8fafc 0%, #e2e8f0 100%);
    --edit-primary: #009688;
    --edit-accent: #00bfa5;
    --edit-gradient: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
}

.edit-wrapper.blue-variant {
    --edit-primary: #30B0C7;
    --edit-accent: #268a9c;
    --edit-gradient: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
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
    display: block;
    text-align: center;
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

/* Color variants for forms */
.edit-wrapper .section-header i {
    color: var(--edit-primary);
}

.edit-wrapper .form-group label i {
    color: var(--edit-primary);
}

.edit-wrapper .form-group input:focus,
.edit-wrapper .form-group select:focus {
    border-color: var(--edit-primary);
    box-shadow: 0 0 0 3px rgba(0, 150, 136, 0.1);
}

.edit-wrapper.blue-variant .form-group input:focus,
.edit-wrapper.blue-variant .form-group select:focus {
    box-shadow: 0 0 0 3px rgba(48, 176, 199, 0.1);
}

.edit-wrapper .btn-link {
    color: var(--edit-primary);
}

.edit-wrapper .btn-link:hover {
    background: rgba(0, 150, 136, 0.05);
    border-color: var(--edit-primary);
}

.edit-wrapper.blue-variant .btn-link:hover {
    background: rgba(48, 176, 199, 0.05);
}

.edit-wrapper .btn-primary {
    background: var(--edit-gradient);
    color: white;
}

.edit-wrapper .btn-primary:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.edit-wrapper .btn-secondary {
    background: #e2e8f0;
    color: #475569;
}

.edit-wrapper .btn-secondary:hover:not(:disabled) {
    background: #cbd5e1;
}

/* New Adresse Section */
.new-adresse-section,
.edit-adresse-section {
    margin-top: 1.5rem;
    animation: slideDown 0.3s ease;
}

/* Input with Action Button */
.input-with-action {
    display: flex;
    gap: 0.5rem;
    align-items: stretch;
}

.input-with-action select {
    flex: 1;
    min-width: 0; /* Allow flexbox to shrink below content size */
    overflow: hidden;
    text-overflow: ellipsis;
}

.btn-icon {
    background: var(--edit-gradient);
    color: white;
    border: none;
    border-radius: 8px;
    width: 44px;
    height: 44px;
    flex-shrink: 0; /* Prevent icon button from shrinking */
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-icon:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-icon i {
    font-size: 1.125rem;
}

/* Action Buttons Container */
.action-buttons {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin-top: 0.75rem;
}

.action-buttons .btn-link {
    text-align: left;
    justify-content: flex-start;
}

.action-buttons .btn-link:disabled {
    opacity: 0.5;
    cursor: not-allowed;
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

    .input-with-action {
        gap: 0.375rem;
    }

    .btn-icon {
        width: 40px;
        height: 40px;
    }

    .input-with-action select {
        font-size: 0.875rem;
    }
}
</style>

<script>
  import './styles.css';
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

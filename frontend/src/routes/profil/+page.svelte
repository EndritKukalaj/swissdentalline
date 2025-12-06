<script>
  import './styles.css';
  import { goto } from '$app/navigation';
  
  let { data } = $props();

  const formatDate = (instant) => {
    if (!instant) return null;
    const date = new Date(instant);
    return date.toLocaleDateString('de-CH', { 
      day: '2-digit', 
      month: 'long', 
      year: 'numeric' 
    });
  };

  // Variant ableiten (Patient = turquoise, Zahnarzt = blue)
  const variant = data.userRole === 'Zahnarzt' ? 'blue' : 'turquoise';
</script>

<div class="profile-wrapper {variant}-variant">
  <!-- Hero Header with Cover Image -->
  <div class="profile-hero">
    <div class="hero-pattern"></div>
    <button class="back-btn-float" onclick={() => goto('/')} aria-label="Zurück zur Übersicht">
      <i class="bi bi-arrow-left"></i>
    </button>
    <div class="hero-content">
      <div class="profile-avatar-container">
        <div class="avatar-ring">
          <div class="avatar-circle">
            <i class="bi bi-person-fill"></i>
          </div>
        </div>
      </div>
      <div class="hero-text">
        <h1 class="hero-name">{data.profile.name}</h1>
        <div class="hero-role">
          <i class="bi {data.userRole === 'Patient' ? 'bi-heart-pulse-fill' : 'bi-journal-medical'}"></i>
          {data.userRole}
        </div>
      </div>
    </div>
  </div>

  <!-- Profile Content -->
  <div class="profile-content">
    <!-- Info Sections -->
    <div class="info-section">
      <div class="section-title">
        <i class="bi bi-person-vcard-fill"></i>
        <h2>Kontaktinformationen</h2>
      </div>
      <div class="info-grid">
        <div class="info-item">
          <div class="info-icon">
            <i class="bi bi-envelope-fill"></i>
          </div>
          <div class="info-content">
            <span class="info-label">E-Mail-Adresse</span>
            <a href="mailto:{data.profile.email}" class="info-value">{data.profile.email}</a>
          </div>
        </div>
        {#if data.userRole === 'Patient'}
          <div class="info-item">
            <div class="info-icon">
              <i class="bi bi-calendar-event-fill"></i>
            </div>
            <div class="info-content">
              <span class="info-label">Geburtsdatum</span>
              <span class="info-value">{data.profile.geburtsdatum ? formatDate(data.profile.geburtsdatum) : 'Nicht vorhanden'}</span>
            </div>
          </div>
        {/if}
      </div>
    </div>

    <!-- Adresse Section für Patient -->
    {#if data.userRole === 'Patient'}
      <div class="info-section">
        <div class="section-title">
          <i class="bi bi-geo-alt-fill"></i>
          <h2>Wohnadresse</h2>
        </div>
        <div class="address-display">
          <div class="address-line">
            <i class="bi bi-geo-alt-fill"></i>
            <span>{data.profile.adresse || 'Nicht vorhanden'}</span>
          </div>
        </div>
      </div>
    {/if}

    <!-- Praxisinformationen für Zahnarzt -->
    {#if data.userRole === 'Zahnarzt'}
      <div class="info-section">
        <div class="section-title">
          <i class="bi bi-building-fill"></i>
          <h2>Praxisinformationen</h2>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <div class="info-icon">
              <i class="bi bi-star-fill"></i>
            </div>
            <div class="info-content">
              <span class="info-label">Praxisname</span>
              <span class="info-value">{data.profile.praxisname || 'Nicht vorhanden'}</span>
            </div>
          </div>
          <div class="info-item">
            <div class="info-icon">
              <i class="bi bi-geo-alt-fill"></i>
            </div>
            <div class="info-content">
              <span class="info-label">Praxisadresse</span>
              <span class="info-value">{data.profile.praxisadresse || 'Nicht vorhanden'}</span>
            </div>
          </div>
        </div>
      </div>
    {/if}

    <!-- Patient Insurance -->
    {#if data.userRole === 'Patient'}
      <div class="info-section">
        <div class="section-title">
          <i class="bi bi-shield-fill-check"></i>
          <h2>Versicherungsinformationen</h2>
        </div>
        {#if data.profile.krankenkasse}
          <div class="insurance-badge">
            <i class="bi bi-hospital-fill"></i>
            <span>{data.profile.krankenkasse}</span>
          </div>
        {:else}
          <div class="info-item">
            <div class="info-icon">
              <i class="bi bi-hospital"></i>
            </div>
            <div class="info-content">
              <span class="info-label">Krankenkasse</span>
              <span class="info-value">Nicht vorhanden</span>
            </div>
          </div>
        {/if}
      </div>
    {/if}

    <!-- Action Button -->
    <div class="profile-actions">
      <button class="edit-profile-btn" onclick={() => goto('/profil/bearbeiten')}>
        <span class="btn-content">
          <i class="bi bi-pencil-square"></i>
          <span>Profil bearbeiten</span>
        </span>
      </button>
      {#if data.userRole === 'Patient'}
        <button class="reviews-btn" onclick={() => goto('/rezensionen')}>
          <span class="btn-content">
            <i class="bi bi-star-fill"></i>
            <span>Meine Rezensionen</span>
          </span>
        </button>
      {/if}
      {#if data.userRole === 'Zahnarzt'}
        <button class="reviews-btn zahnarzt" onclick={() => goto('/rezensionen')}>
          <span class="btn-content">
            <i class="bi bi-star-fill"></i>
            <span>Erhaltene Bewertungen</span>
          </span>
        </button>
      {/if}
    </div>
  </div>
</div>

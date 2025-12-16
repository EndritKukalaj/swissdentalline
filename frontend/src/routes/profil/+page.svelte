<script>
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

<style>
/* Profile Page Specific Styles */

.profile-wrapper {
    min-height: 100vh;
    background: #f8fafc;
    --profile-primary: #009688;
    --profile-accent: #00bfa5;
    --profile-gradient: linear-gradient(135deg, rgba(0, 150, 136, 0.85) 0%, rgba(0, 191, 165, 0.75) 100%);
    --profile-gradient-soft: linear-gradient(135deg, rgba(0, 150, 136, 0.1) 0%, rgba(0, 191, 165, 0.05) 100%);
}

.profile-wrapper.blue-variant {
    --profile-primary: #30B0C7;
    --profile-accent: #268a9c;
    --profile-gradient: linear-gradient(135deg, rgba(48, 176, 199, 0.85) 0%, rgba(38, 138, 156, 0.75) 100%);
    --profile-gradient-soft: linear-gradient(135deg, rgba(48, 176, 199, 0.1) 0%, rgba(38, 138, 156, 0.05) 100%);
}

/* Hero Section */
.profile-hero {
    position: relative;
    height: 240px;
    background: var(--profile-gradient);
    overflow: hidden;
}

.hero-pattern {
    position: absolute;
    inset: 0;
    background-image: 
        radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
        radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.15) 0%, transparent 50%);
    opacity: 0.8;
}

.back-btn-float {
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
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    z-index: 10;
}

.back-btn-float i {
    font-size: 1.125rem;
    color: #1a202c;
}

.back-btn-float:hover {
    background: white;
    transform: translateX(-4px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.hero-content {
    position: absolute;
    bottom: -50px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.75rem;
    z-index: 5;
}

.profile-avatar-container {
    position: relative;
}

.avatar-ring {
    width: 120px;
    height: 120px;
    background: white;
    border-radius: 50%;
    padding: 5px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.avatar-circle {
    width: 100%;
    height: 100%;
    background: var(--profile-gradient);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.avatar-circle i {
    font-size: 3.5rem;
    color: white;
}

.hero-text {
    text-align: center;
    background: white;
    padding: 0.75rem 2rem 1rem;
    border-radius: 50px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.hero-name {
    font-size: 1.5rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 0.375rem 0;
}

.hero-role {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.375rem 1rem;
    background: var(--profile-gradient-soft);
    color: var(--profile-primary);
    font-size: 0.875rem;
    font-weight: 600;
    border-radius: 20px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
}

.hero-role i {
    font-size: 1rem;
}

/* Content Section */
.profile-content {
    max-width: 900px;
    margin: 0 auto;
    padding: 4rem 2rem 2rem;
}

/* Actions */
.profile-actions {
    margin-top: 1.5rem;
    display: flex;
    justify-content: center;
    gap: 1rem;
    flex-wrap: wrap;
}

.edit-profile-btn,
.reviews-btn {
    position: relative;
    padding: 1rem 2.5rem;
    background: var(--profile-gradient);
    color: white;
    border: none;
    border-radius: 50px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.reviews-btn {
    background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
}

.reviews-btn.zahnarzt {
    background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
}

.edit-profile-btn:hover,
.reviews-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 28px rgba(0, 0, 0, 0.2);
}

.btn-content {
    display: flex;
    align-items: center;
    gap: 0.625rem;
}

.btn-content i {
    font-size: 1.125rem;
}

/* Color variants for info items */
.profile-wrapper .info-item {
    background: var(--profile-gradient-soft);
    border-left-color: var(--profile-primary);
}

.profile-wrapper .info-icon i,
.profile-wrapper .section-title i {
    color: var(--profile-primary);
}

.profile-wrapper .address-line {
    background: var(--profile-gradient-soft);
}

.profile-wrapper .address-line i {
    color: var(--profile-primary);
}

.profile-wrapper .insurance-badge {
    background: var(--profile-gradient);
}

@media (max-width: 768px) {
    .profile-hero {
        height: 200px;
    }

    .back-btn-float {
        width: 32px;
        height: 32px;
        top: 0.75rem;
        left: 0.75rem;
    }

    .back-btn-float i {
        font-size: 0.875rem;
    }

    .hero-content {
        bottom: -45px;
    }

    .avatar-ring {
        width: 90px;
        height: 90px;
        padding: 4px;
    }

    .avatar-circle i {
        font-size: 2.5rem;
    }

    .hero-text {
        padding: 0.5rem 1.25rem 0.75rem;
        border-radius: 40px;
    }

    .hero-name {
        font-size: 1.125rem;
        margin-bottom: 0.25rem;
    }

    .hero-role {
        font-size: 0.75rem;
        padding: 0.25rem 0.75rem;
        gap: 0.375rem;
    }

    .hero-role i {
        font-size: 0.875rem;
    }

    .profile-content {
        padding: 3rem 1rem 1.5rem;
    }

    .section-title {
        font-size: 1rem;
        margin-bottom: 0.75rem;
    }

    .section-title i {
        font-size: 1.125rem;
    }

    .info-section {
        padding: 1rem;
        border-radius: 12px;
        margin-bottom: 1rem;
    }

    .info-item {
        padding: 0.75rem;
        border-radius: 8px;
        margin-bottom: 0.75rem;
    }

    .info-icon {
        width: 36px;
        height: 36px;
    }

    .info-icon i {
        font-size: 1.125rem;
    }

    .info-label {
        font-size: 0.75rem;
        margin-bottom: 0.1875rem;
    }

    .info-value {
        font-size: 0.9375rem;
    }

    .address-line {
        padding: 0.625rem;
        font-size: 0.875rem;
    }

    .address-line i {
        font-size: 0.9375rem;
    }

    .insurance-badge {
        padding: 0.5rem 0.875rem;
        font-size: 0.8125rem;
    }

    .insurance-badge i {
        font-size: 1rem;
    }

    .profile-actions {
        margin-top: 1.25rem;
        gap: 0.75rem;
        flex-direction: column;
    }

    .edit-profile-btn,
    .reviews-btn {
        width: 100%;
        padding: 0.875rem 2rem;
        font-size: 0.9375rem;
        border-radius: 40px;
    }

    .btn-content {
        gap: 0.5rem;
    }

    .btn-content i {
        font-size: 1rem;
    }
}
</style>

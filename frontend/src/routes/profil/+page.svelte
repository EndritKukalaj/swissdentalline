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

  // Variant ableiten (Patient = turquoise, Zahnarzt = purple)
  const variant = data.userRole === 'Zahnarzt' ? 'purple' : 'turquoise';
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
        {#if data.userRole === 'Patient' && data.profile.geburtsdatum}
          <div class="info-item">
            <div class="info-icon">
              <i class="bi bi-calendar-event-fill"></i>
            </div>
            <div class="info-content">
              <span class="info-label">Geburtsdatum</span>
              <span class="info-value">{formatDate(data.profile.geburtsdatum)}</span>
            </div>
          </div>
        {/if}
      </div>
    </div>

    <!-- Adresse Section für Patient -->
    {#if data.userRole === 'Patient' && data.profile.adresse}
      <div class="info-section">
        <div class="section-title">
          <i class="bi bi-geo-alt-fill"></i>
          <h2>Wohnadresse</h2>
        </div>
        <div class="address-display">
          <div class="address-line">
            <i class="bi bi-geo-alt-fill"></i>
            <span>{data.profile.adresse}</span>
          </div>
        </div>
      </div>
    {/if}

    <!-- Praxisinformationen für Zahnarzt -->
    {#if data.userRole === 'Zahnarzt' && (data.profile.praxisname || data.profile.praxisadresse)}
      <div class="info-section">
        <div class="section-title">
          <i class="bi bi-building-fill"></i>
          <h2>Praxisinformationen</h2>
        </div>
        <div class="info-grid">
          {#if data.profile.praxisname}
            <div class="info-item">
              <div class="info-icon">
                <i class="bi bi-star-fill"></i>
              </div>
              <div class="info-content">
                <span class="info-label">Praxisname</span>
                <span class="info-value">{data.profile.praxisname}</span>
              </div>
            </div>
          {/if}
          {#if data.profile.praxisadresse}
            <div class="info-item">
              <div class="info-icon">
                <i class="bi bi-geo-alt-fill"></i>
              </div>
              <div class="info-content">
                <span class="info-label">Praxisadresse</span>
                <span class="info-value">{data.profile.praxisadresse}</span>
              </div>
            </div>
          {/if}
        </div>
      </div>
    {/if}

    <!-- Patient Insurance -->
    {#if data.userRole === 'Patient' && data.profile.krankenkasse}
      <div class="info-section">
        <div class="section-title">
          <i class="bi bi-shield-fill-check"></i>
          <h2>Versicherungsinformationen</h2>
        </div>
        <div class="insurance-badge">
          <i class="bi bi-hospital-fill"></i>
          <span>{data.profile.krankenkasse}</span>
        </div>
      </div>
    {/if}

    <!-- Action Button -->
    <div class="profile-actions">
      <button class="edit-profile-btn" disabled>
        <span class="btn-content">
          <i class="bi bi-pencil-square"></i>
          <span>Profil bearbeiten</span>
        </span>
        <span class="soon-badge">Bald verfügbar</span>
      </button>
    </div>
  </div>
</div>

<style>
  .profile-wrapper {
    min-height: 100vh;
    background: #f8fafc;
    --profile-primary: #009688;
    --profile-accent: #00bfa5;
    --profile-gradient: linear-gradient(135deg, rgba(0, 150, 136, 0.85) 0%, rgba(0, 191, 165, 0.75) 100%);
    --profile-gradient-soft: linear-gradient(135deg, rgba(0, 150, 136, 0.1) 0%, rgba(0, 191, 165, 0.05) 100%);
  }

  .profile-wrapper.purple-variant {
    --profile-primary: #8E24AA;
    --profile-accent: #AB47BC;
    --profile-gradient: linear-gradient(135deg, rgba(142, 36, 170, 0.85) 0%, rgba(171, 71, 188, 0.75) 100%);
    --profile-gradient-soft: linear-gradient(135deg, rgba(142, 36, 170, 0.1) 0%, rgba(171, 71, 188, 0.05) 100%);
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

  .info-section {
    background: white;
    border-radius: 16px;
    padding: 1.5rem;
    margin-bottom: 1rem;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
    border: 1px solid rgba(0, 0, 0, 0.05);
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 0.625rem;
    margin-bottom: 1.25rem;
    padding-bottom: 0.75rem;
    border-bottom: 2px solid #f1f5f9;
  }

  .section-title i {
    font-size: 1.25rem;
    color: var(--profile-primary);
  }

  .section-title h2 {
    font-size: 1.125rem;
    font-weight: 600;
    color: #1a202c;
    margin: 0;
  }

  /* Info Grid */
  .info-grid {
    display: grid;
    gap: 1rem;
  }

  .info-item {
    display: flex;
    gap: 1rem;
    align-items: flex-start;
    padding: 1rem;
    background: var(--profile-gradient-soft);
    border-radius: 12px;
    border-left: 3px solid var(--profile-primary);
    transition: all 0.3s ease;
  }

  .info-item:hover {
    transform: translateX(4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }

  .info-icon {
    flex-shrink: 0;
    width: 40px;
    height: 40px;
    background: white;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .info-icon i {
    font-size: 1.25rem;
    color: var(--profile-primary);
  }

  .info-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.125rem;
  }

  .info-label {
    font-size: 0.75rem;
    font-weight: 600;
    color: #64748b;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .info-value {
    font-size: 1rem;
    font-weight: 600;
    color: #1a202c;
  }

  .info-value a {
    color: var(--profile-primary);
    text-decoration: none;
    transition: all 0.2s ease;
  }

  .info-value a:hover {
    color: var(--profile-accent);
    text-decoration: underline;
  }

  /* Address Display */
  .address-display {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .address-line {
    display: flex;
    align-items: center;
    gap: 0.875rem;
    padding: 0.875rem 1rem;
    background: var(--profile-gradient-soft);
    border-radius: 10px;
    font-size: 1rem;
    font-weight: 500;
    color: #1a202c;
  }

  .address-line i {
    font-size: 1.25rem;
    color: var(--profile-primary);
  }

  /* Badges */
  .insurance-badge,
  .specialization-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.875rem;
    padding: 1rem 1.5rem;
    background: var(--profile-gradient);
    color: white;
    border-radius: 12px;
    font-size: 1rem;
    font-weight: 600;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }

  .insurance-badge i,
  .specialization-badge i {
    font-size: 1.5rem;
  }

  /* Actions */
  .profile-actions {
    margin-top: 1.5rem;
    display: flex;
    justify-content: center;
  }

  .edit-profile-btn {
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

  .edit-profile-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }

  .edit-profile-btn:hover:not(:disabled) {
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

  .soon-badge {
    position: absolute;
    top: -8px;
    right: -8px;
    background: #64748b;
    color: white;
    font-size: 0.625rem;
    padding: 0.375rem 0.625rem;
    border-radius: 20px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }

  @media (max-width: 768px) {
    .profile-hero {
      height: 220px;
    }

    .back-btn-float {
      width: 36px;
      height: 36px;
      top: 0.875rem;
      left: 0.875rem;
    }

    .back-btn-float i {
      font-size: 1rem;
    }

    .avatar-ring {
      width: 100px;
      height: 100px;
    }

    .avatar-circle i {
      font-size: 3rem;
    }

    .hero-name {
      font-size: 1.25rem;
    }

    .hero-text {
      padding: 0.625rem 1.5rem 0.875rem;
    }

    .profile-content {
      padding: 3.5rem 1rem 1.5rem;
    }

    .info-section {
      padding: 1.25rem;
      border-radius: 16px;
      margin-bottom: 0.875rem;
    }

    .info-item {
      flex-direction: column;
      gap: 0.75rem;
      padding: 0.875rem;
    }

    .info-icon {
      width: 36px;
      height: 36px;
    }

    .edit-profile-btn {
      width: 100%;
      padding: 0.875rem 2rem;
    }
  }
</style>

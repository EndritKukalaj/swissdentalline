<script>
  // Konsistenter Stil wie Login: Props via $props() und flache Zuordnung
  let { data } = $props();

  const formatDate = (instant) => {
    if (!instant) return null;
    const date = new Date(instant);
    return date.toLocaleDateString('de-CH', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  const profil = data.profil || {};
  const isPatient = data.isPatient;
  const isZahnarzt = data.isZahnarzt;

  // Variant ableiten (Patient = turquoise, Zahnarzt = purple)
  const variant = isZahnarzt ? 'purple' : 'turquoise';

  // Abgebildete Werte für Template
  const profile = {
    name: profil.name,
    email: profil.email,
    role: profil.role,
    adresse: profil.adresse,
    geburtsdatum: formatDate(profil.geburtsdatum),
    krankenkasse: profil.krankenkasse,
    praxisname: profil.praxisname,
    praxisadresse: profil.praxisadresse
  };
</script>

<div class="profil-container {variant}-variant">
  <div class="profil-header">
    <img src="/images/patientin-mit-einem-eingriff.jpg" alt="Profil Header" class="profil-header-img" />
    <div class="profil-title mt-4">
      <span class="profil-logo">Swiss Dental Line</span>
      <div class="profil-avatar">
        {#if isPatient}
          <i class="bi bi-heart-pulse"></i>
          <span>Patient</span>
        {:else if isZahnarzt}
          <i class="bi bi-clipboard2-pulse"></i>
          <span>Zahnarzt</span>
        {/if}
      </div>
    </div>
  </div>

  <div class="profil-section">
    <h3>Persönliche Daten</h3>
    <div class="profil-card">
      <i class="bi bi-person"></i>
      <div>
        <strong>{profile.name}</strong><br />
        {#if profile.adresse}<span>{profile.adresse}</span><br />{/if}
        {#if profile.geburtsdatum}<span>Geburtsdatum: {profile.geburtsdatum}</span><br />{/if}
        <span>{profile.email}</span><br />
        {#if profile.krankenkasse}<span>Krankenkasse: {profile.krankenkasse}</span>{/if}
      </div>
    </div>
  </div>


  {#if isZahnarzt}
    <div class="profil-section">
      <h3>Praxistdaten</h3>
      <div class="profil-card">
        <i class="bi bi-building"></i>
        <div>
          <span>{profile.praxisname || 'Praxisname folgt'}</span><br />
          <span>{profile.praxisadresse || 'Praxisadresse folgt'}</span>
        </div>
      </div>
    </div>
  {/if}

  <div class="profil-actions">
    <a href="/" class="profil-btn">Zur Startseite</a>
  </div>
</div>

<style>
.profil-container {
  background: var(--white);
  border-radius: var(--radius-lg);
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 2rem 1.5rem;
  max-width: 800px;
  margin: 2rem auto;
  /* Default (turquoise) profile variables */
  --profile-primary: #009688;
  --profile-accent: #30B0C7;
  --profile-soft-bg: rgba(0, 150, 136, 0.1);
  --profile-soft-bg-strong: rgba(0, 150, 136, 0.2);
  --profile-border: #009688;
  --profile-card-bg: #009688;
}

.profil-container.purple-variant {
  --profile-primary: #8E24AA;
  --profile-accent: #AB47BC;
  --profile-soft-bg: rgba(142, 36, 170, 0.08);
  --profile-soft-bg-strong: rgba(142, 36, 170, 0.18);
  --profile-border: #8E24AA;
  --profile-card-bg: #8E24AA;
}
.profil-header {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-bottom: 1rem;
}
.profil-header-img {
  width: 100%;
  height: 240px;
  object-fit: cover;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
  margin-bottom: 0.5rem;
}
.profil-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}
.profil-logo {
  font-weight: 700;
  font-size: 1.2rem;
  color: var(--profile-primary);
}
.profil-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  background: var(--profile-soft-bg-strong);
  border-radius: var(--radius-md);
  padding: 0.5rem 0.75rem;
  backdrop-filter: blur(5px);
  border: 2px solid var(--profile-border);
}
.profil-avatar i {
  font-size: 1.8rem;
  color: var(--profile-primary);
}
.profil-avatar span {
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--profile-primary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.profil-section {
  margin-bottom: 1rem;
}
.profil-card {
  color: var(--white);
  background: var(--profile-card-bg);
  border-radius: var(--radius-md);
  padding: 1rem;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 0.5rem;
}
.profil-card i {
  font-size: 1.3rem;
  color: var(--bg-light);
  margin-top: 0.2rem;
}
.profil-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}
.profil-btn {
  background: var(--profile-primary);
  color: var(--white);
  border-radius: 50px;
  padding: 0.75rem 2rem;
  font-weight: 600;
  text-align: center;
  text-decoration: none;
  font-size: 1rem;
  transition: background 0.2s;
}
.profil-btn:hover {
  background: var(--profile-accent);
}
</style>

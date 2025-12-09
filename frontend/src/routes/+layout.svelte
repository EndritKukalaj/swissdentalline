<script>
  import icon from "$lib/assets/Logo.webp";
  import "./styles.css";
  let { data, children } = $props();
  let { user, isAuthenticated, userRole, flexTermineCount = 0 } = data;
  
  // Determine variant based on role
  const variant = userRole === 'Zahnarzt' ? 'blue' : 'turquoise';
</script>

<svelte:head>
  <link rel="icon" href={icon} />
  <link
    rel="stylesheet"
    href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css"
  />
</svelte:head>

<nav class="custom-navbar {variant}-variant">
  <div class="nav-container">
    <!-- Logo & Brand -->
    <div class="nav-brand">
      <a href="/" class="brand-link">
        <img src="/images/Logo.webp" alt="Swiss Dental Line" class="nav-logo" />
        <span class="brand-text fs-4">SwissDentalLine</span>
      </a>
    </div>

    <!-- Mobile Toggle -->
    <button
      class="mobile-toggle"
      type="button"
      data-bs-toggle="collapse"
      data-bs-target="#navbarContent"
      aria-controls="navbarContent"
      aria-expanded="false"
      aria-label="Toggle navigation"
    >
      <span class="navbar-toggler-icon"></span>
    </button>

    <!-- Navigation Content -->
    <div class="collapse navbar-collapse" id="navbarContent">
      <div class="nav-content">
        <!-- Nav Links -->
        {#if isAuthenticated}
          <div class="nav-links">
            <a href="/" class="nav-link-item">
              <i class="bi bi-house-door"></i>
              Übersicht
            </a>
            <a href="/termine" class="nav-link-item">
              <i class="bi bi-calendar2-check"></i>
              Termine
            </a>
            {#if user.user_roles.includes("Patient")}
              <a href="/buchen" class="nav-link-item">
                <i class="bi bi-calendar-plus"></i>
                Termin buchen
              </a>
              <a href="/flextermine" class="nav-link-item">
                <i class="bi bi-calendar2-range"></i>
                Flex-Termine
                {#if flexTermineCount > 0}
                  <span class="nav-badge">{flexTermineCount}</span>
                {/if}
              </a>
            {/if}
            {#if user.user_roles.includes("Zahnarzt")}
              <a href="/slots" class="nav-link-item">
                <i class="bi bi-calendar-plus"></i>
                Slots erfassen
              </a>
              <a href="/" class="nav-link-item">
                <i class="bi bi-bar-chart-line"></i>
                Statistiken
              </a>
            {/if}
          </div>
        {/if}

        <!-- User Actions -->
        <div class="nav-actions">
          {#if isAuthenticated}
            <a href="/profil" class="user-info" style="text-decoration: none;">
              <div class="user-avatar">
                <i class="bi bi-person-circle"></i>
              </div>
              <span class="user-name">{user.name}</span>
            </a>
            <form method="POST" action="/logout" style="display: inline;">
              <button type="submit" class="btn btn-outline-primary btn-sm">
                Abmelden
              </button>
            </form>
          {:else}
            <a href="/login" class="btn btn-primary">Anmelden</a>
            <a href="/signup" class="btn btn-secondary">Registrieren</a>
          {/if}
        </div>
      </div>
    </div>
  </div>
</nav>

<main class="container mt-3">
  {@render children()}
</main>

<style>
  .custom-navbar {
    background: linear-gradient(135deg, var(--white) 0%, var(--bg-light) 100%);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    padding: 1rem 0;
    position: sticky;
    top: 0;
    z-index: 1000;
    --nav-primary: var(--primary-turquoise);
    --nav-accent: var(--accent-blue);
    --nav-hover-bg: rgba(0, 150, 136, 0.05);
    --nav-hover-border: rgba(0, 150, 136, 0.1);
  }
  
  .custom-navbar.blue-variant {
    --nav-primary: #30B0C7;
    --nav-accent: #268a9c;
    --nav-hover-bg: rgba(142, 36, 170, 0.05);
    --nav-hover-border: rgba(142, 36, 170, 0.1);
    background: linear-gradient(135deg, var(--white) 0%, #F9F5FC 100%);
  }

  .nav-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 1.5rem;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .nav-brand {
    display: flex;
    align-items: center;
  }

  .brand-link {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    text-decoration: none;
    color: var(--text-dark);
    font-weight: 700;
    font-size: 1.25rem;
    transition: transform 0.2s ease;
  }

  .brand-link:hover {
    transform: scale(1.02);
  }

  .nav-logo {
    width: 40px;
    height: 40px;
    filter: drop-shadow(0 2px 4px rgba(0, 150, 136, 0.2));
  }

  .brand-text {
    background: linear-gradient(
      135deg,
      var(--nav-primary),
      var(--nav-accent)
    );
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .mobile-toggle {
    display: none;
    background: none;
    border: 2px solid var(--nav-primary);
    border-radius: var(--radius-sm);
    padding: 0.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .mobile-toggle:hover {
    background-color: var(--nav-hover-bg);
  }

  .nav-content {
    display: flex;
    align-items: center;
    gap: 2rem;
    flex: 1;
    justify-content: space-between;
    margin-left: 2rem;
  }

  .nav-links {
    display: flex;
    gap: 1rem;
  }

  .nav-link-item {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.25rem;
    color: var(--text-dark);
    text-decoration: none;
    font-weight: 500;
    border-radius: var(--radius-lg);
    transition: all 0.3s ease;
    position: relative;
  }

  .nav-link-item::before {
    content: "";
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 3px;
    background: var(--nav-primary);
    border-radius: 2px;
    transition: width 0.3s ease;
  }

  .nav-link-item:hover {
    color: var(--nav-primary);
    background-color: var(--nav-hover-bg);
  }

  .nav-link-item:hover::before {
    width: 80%;
  }
  
  .nav-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 1.35rem;
    height: 1.35rem;
    padding: 0 0.4rem;
    background: linear-gradient(135deg, #FFB84D 0%, #FF9800 100%);
    color: white;
    font-size: 0.7rem;
    font-weight: 700;
    border-radius: 10px;
    margin-left: 0.25rem;
    box-shadow: 0 2px 4px rgba(255, 184, 77, 0.3);
    animation: pulse 2s ease-in-out infinite;
  }
  
  @keyframes pulse {
    0%, 100% {
      transform: scale(1);
      box-shadow: 0 2px 4px rgba(255, 184, 77, 0.3);
    }
    50% {
      transform: scale(1.05);
      box-shadow: 0 2px 8px rgba(255, 184, 77, 0.5);
    }
  }

  .nav-actions {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-left: auto;
    justify-content: flex-end;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.5rem 1rem;
    background-color: var(--nav-hover-bg);
    border-radius: var(--radius-lg);
    transition: all 0.3s ease;
  }
  
  .user-info:hover {
    background-color: var(--nav-hover-border);
  }

  .user-avatar {
    width: 36px;
    height: 36px;
    background: var(--nav-primary);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--white);
  }

  .user-name {
    font-weight: 600;
    color: var(--text-dark);
  }

  /* Mobile Styles */
  @media (max-width: 991px) {
    .mobile-toggle {
      display: block;
    }

    .navbar-collapse {
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background: var(--white);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      margin-top: 1rem;
      border-radius: var(--radius-md);
    }

    .nav-content {
      flex-direction: column;
      padding: 1.5rem;
      gap: 1.5rem;
    }

    .nav-links {
      flex-direction: column;
      width: 100%;
    }

    .nav-link-item {
      width: 100%;
      justify-content: flex-start;
    }

    .nav-actions {
      flex-direction: column;
      width: 100%;
    }

    .user-info {
      width: 100%;
      justify-content: center;
    }

    .btn {
      width: 100%;
    }
  }

  @media (min-width: 992px) {
    .navbar-collapse {
      display: flex !important;
    }
  }
</style>

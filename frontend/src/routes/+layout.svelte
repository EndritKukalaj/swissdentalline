<script>
  import favicon from "$lib/assets/favicon.svg";
  import "./styles.css";
  let { data, children } = $props();
  let { user, isAuthenticated } = data;
</script>

<svelte:head>
  <link rel="icon" href={favicon} />
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</svelte:head>

<nav class="custom-navbar">
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
              <i class="bi bi-speedometer2"></i>
              Termine
            </a>
            <a href="/flex" class="nav-link-item">
              <i class="bi bi-calendar-check"></i>
              Flex-Termine
            </a>
          </div>
        {/if}

        <!-- User Actions -->
        <div class="nav-actions">
          {#if isAuthenticated}
            <div class="user-info">
              <div class="user-avatar">
                <i class="bi bi-person-circle"></i>
              </div>
              <span class="user-name">{user.name}</span>
            </div>
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
  }

  .nav-container {
    max-width: 1200px;
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
    background: linear-gradient(135deg, var(--primary-turquoise), var(--accent-blue));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .mobile-toggle {
    display: none;
    background: none;
    border: 2px solid var(--primary-turquoise);
    border-radius: var(--radius-sm);
    padding: 0.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .mobile-toggle:hover {
    background-color: rgba(0, 150, 136, 0.1);
  }

  .nav-content {
    display: flex;
    align-items: center;
    gap: 2rem;
    flex: 1;
    justify-content: flex-end;
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
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 0;
    height: 3px;
    background: var(--primary-turquoise);
    border-radius: 2px;
    transition: width 0.3s ease;
  }

  .nav-link-item:hover {
    color: var(--primary-turquoise);
    background-color: rgba(0, 150, 136, 0.05);
  }

  .nav-link-item:hover::before {
    width: 80%;
  }

  .nav-actions {
    display: flex;
    align-items: center;
    gap: 1rem;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    padding: 0.5rem 1rem;
    background-color: rgba(0, 150, 136, 0.1);
    border-radius: var(--radius-lg);
  }

  .user-avatar {
    width: 36px;
    height: 36px;
    background: var(--primary-turquoise);
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

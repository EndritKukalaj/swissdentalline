<script>
    import { InfoBanner } from '$lib';
    
    let { form } = $props();
</script>

<div class="login-container">
    <div class="login-wrapper">
        <!-- Logo/Brand -->
        <div class="brand-section">
            <h1 class="brand-title text-center">
                SwissDentalLine
            </h1>
        </div>

        <!-- Tagline: centered and styled to sit above the submit button -->
        <div class="tagline text-center mb-12">
            <p class="text-muted mb-0">
                <span class="d-block fs-4 fw-semibold mb-8">Freie Zahnarzttermine in Echtzeit</span>
            </p>
        </div>

        <hr /><br />

        <!-- Login Form -->
        <div class="login-form">
            <h2 class="text-center mb-4">Anmelden</h2>

            <InfoBanner
                type="error"
                message={form?.error}
                show={!!form?.error}
            />

            <form method="POST">
                <div class="mb-4">
                    <input
                        type="email"
                        class="form-control"
                        id="email"
                        name="email"
                        placeholder="Email"
                        required
                    />
                </div>

                <div class="mb-4">
                    <input
                        type="password"
                        class="form-control"
                        id="password"
                        name="password"
                        placeholder="Passwort"
                        required
                    />
                </div>

                <div class="d-grid mb-4">
                    <button type="submit" class="btn btn-primary btn-lg">
                        Anmelden
                    </button>
                </div>

                <div class="text-center mt-4">
                    <small class="text-muted">
                        Noch kein Konto? 
                        <a href="/signup" class="text-primary-turquoise text-decoration-none fw-bold">
                            Erstelle eins in Sekunden!
                        </a>
                    </small>
                </div>
            </form>
        </div>
    </div>
</div>

<style>
/* Login Page Specific Styles */

.login-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    background-color: var(--white);
}

.login-wrapper {
    width: 100%;
    max-width: 480px;
    padding: 2rem 1.5rem;
}

.brand-section {
    margin-bottom: 3rem;
}

.brand-title {
    font-size: 2.5rem;
    font-weight: 700;
    line-height: 1.2;
    color: var(--text-dark);
    margin: 0;
}

.tagline {
    margin-bottom: 2rem;
}

.login-form h2 {
    font-size: 1.75rem;
    font-weight: 600;
    margin-bottom: 2rem;
}

.form-control {
    font-size: 1rem;
    padding: 0.75rem 0.5rem;
}

@media (min-width: 768px) {
    .login-wrapper {
        padding: 3rem 2rem;
    }

    .brand-title {
        font-size: 3rem;
    }
}
</style>

<script>
  import './styles.css';
  import { enhance } from '$app/forms';

  let { data, form } = $props();
  let isAuthenticated = $derived(data.isAuthenticated);

  let signupFormElement;
</script>

<div class="signup-container">
    <div class="signup-wrapper">
        <!-- Logo/Brand -->
        <div class="brand-section">
            <h1 class="brand-title text-center">
                SwissDentalLine
            </h1>
        </div>

        <!-- Tagline -->
        <div class="tagline text-center mb-12">
            <p class="text-muted mb-0">
                <span class="d-block fs-4 fw-semibold mb-8">Finden statt Suchen</span>
            </p>
        </div>

        <hr /><br />

        <!-- Signup Form -->
        <div class="signup-form">
            <h2 class="text-center mb-4">Erstelle dein Konto</h2>

            {#if form?.error}
                <div class="alert alert-danger" role="alert">
                    {form.error}
                </div>
            {/if}

            <form
                method="POST"
                action="?/signup"
                bind:this={signupFormElement}
                use:enhance
            >
                <div class="mb-3">
                    <input
                        type="text"
                        class="form-control"
                        id="firstName"
                        name="firstName"
                        placeholder="Vorname"
                        required
                    />
                </div>

                <div class="mb-3">
                    <input
                        type="text"
                        class="form-control"
                        id="lastName"
                        name="lastName"
                        placeholder="Nachname"
                        required
                    />
                </div>

                <div class="mb-3">
                    <input
                        type="email"
                        class="form-control"
                        id="email"
                        name="email"
                        placeholder="Email"
                        required
                    />
                </div>

                <div class="mb-3">
                    <input
                        type="password"
                        class="form-control"
                        id="password"
                        name="password"
                        placeholder="Passwort"
                        required
                    />
                </div>

                <div class="mb-4">
                    <input
                        type="password"
                        class="form-control"
                        id="passwordConfirm"
                        name="passwordConfirm"
                        placeholder="Passwort Bestätigen"
                        required
                    />
                </div>

                <div class="d-grid">
                    <button type="submit" class="btn btn-primary btn-lg">
                        Registrieren
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    let { data } = $props();
    let { isAuthenticated } = data;
</script>

{#if isAuthenticated}
    <div class="dashboard-container">
        <h1>Willkommen bei SwissDentalLine</h1>
        <p>Ihre Terminübersicht wird hier angezeigt.</p>
    </div>
{:else}
    <div class="home-container">
        <div class="home-wrapper">
            <!-- Logo/Brand Section -->
            <div class="brand-section">
                <div>
                    <h1 class="brand-title text-center">SwissDentalLine</h1>
                    <hr />
                </div>
            </div>

            <!-- Hero Image Section -->
            <div class="hero-section">
                <div class="hero-image-wrapper">
                    <img
                        src="/images/nahaufnahme.jpg"
                        alt="Dental Care"
                        class="hero-image"
                    />
                    <div class="hero-overlay">
                        <div class="hero-content text-center">
                            <h2 class="hero-title">
                                Finden Sie verfügbare Zahnärzte, bevor Sie
                                suchen müssen
                            </h2>
                            <p class="hero-subtitle">
                                Freie Zahnarzttermine in Echtzeit
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- CTA Section -->
            <div class="cta-section">
                <a href="/login" class="btn btn-primary btn-lg btn-block">
                    Loslegen
                </a>
            </div>
        </div>
    </div>
{/if}

<style>
    .dashboard-container {
        padding: 2rem;
    }

    .home-container {
        min-height: 100vh;
        display: flex;
        flex-direction: column;
        background-color: var(--white);
    }

    .home-wrapper {
        flex: 1;
        display: flex;
        flex-direction: column;
    }

    .brand-section {
        padding: 2rem 1.5rem 1rem;
        display: flex;
        flex-direction: column;
        gap: 1rem;
    }

    .brand-title {
        font-size: 2rem;
        font-weight: 700;
        line-height: 1.2;
        color: var(--text-dark);
        margin: 0;
    }

    .hero-section {
        position: relative;
        overflow: hidden;
    }

    .hero-image-wrapper {
        position: relative;
        width: 100%;
        height: 500px;
    }

    .hero-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        object-position: center;
        transform: scale(1.1);
    }

    .hero-overlay {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background: linear-gradient(
            to top,
            rgba(0, 150, 136, 1) 0%,
            rgba(0, 150, 136, 0.95) 30%,
            rgba(0, 150, 136, 0.85) 50%,
            rgba(0, 150, 136, 0.5) 70%,
            rgba(0, 150, 136, 0) 100%
        );
        padding: 3rem 1.5rem 2rem;
    }

    .hero-content {
        max-width: 800px;
        margin: 0 auto;
    }

    .hero-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--white);
        margin-bottom: 0.5rem;
        line-height: 1.3;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }

    .hero-subtitle {
        font-size: 1rem;
        color: var(--white);
        margin: 0;
        font-weight: 500;
        font-style: italic;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }

    .cta-section {
        padding: 2rem 1.5rem;
        text-align: center;
    }

    .btn-block {
        width: 100%;
        max-width: 400px;
    }

    @media (min-width: 768px) {
        .brand-section {
            justify-content: space-between;
            align-items: center;
        }

        .brand-title {
            font-size: 2.5rem;
        }

        .hero-image-wrapper {
            height: 500px;
        }

        .hero-title {
            font-size: 2rem;
        }

        .hero-subtitle {
            font-size: 1.25rem;
        }

        .hero-overlay {
            padding: 4rem 2rem 3rem;
        }
    }

    @media (min-width: 1024px) {
        .brand-title {
            font-size: 3rem;
        }

        .hero-image-wrapper {
            min-height: 600px;
        }

        .hero-title {
            font-size: 2.5rem;
        }
    }
</style>

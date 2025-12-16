<script>
    // Creative modern landing — static component
    let query = "";
    const features = [
        {
            title: "Termine in Echtzeit finden",
            desc: "Durchsuchen Sie verfügbare Zahnärzte und buchen Sie sofort – ohne Wartezeit am Telefon.",
            icon: "🔍",
            highlight: "Sofort buchbar",
        },
        {
            title: "Ihre Zahngesundheit im Fokus",
            desc: "Von Prophylaxe bis Behandlung – finden Sie die richtige Praxis für Ihre Bedürfnisse.",
            icon: "💙",
            highlight: "Qualität zählt",
        },
        {
            title: "Flexibel & spontan buchen",
            desc: "Nutzen Sie kurzfristige Termine und sparen Sie Zeit und Geld mit attraktiven Angeboten.",
            icon: "⚡",
            highlight: "Smart sparen",
        },
    ];

    const reasons = [
        { text: "Keine Wartezeit", icon: "✓" },
        { text: "Geprüfte Praxen", icon: "✓" },
        { text: "Einfach & sicher", icon: "✓" },
    ];

    function onSearch(e) {
        e.preventDefault();
        window.location.href = "/buchen";
    }
</script>

<main class="landing">
    <section class="hero">
        <div class="hero-bg"></div>
        <div class="hero-content">
            <div class="hero-left">
                <span class="hero-badge"
                    >✨ Ihre digitale Zahnarzt-Plattform</span
                >
                <h1 class="hero-title">
                    Zahnarzttermine finden.<br />Direkt buchen.<br />Zeit
                    sparen.
                </h1>
                <p class="hero-subtitle">
                    Entdecken Sie verfügbare Termine in Ihrer Nähe und buchen
                    Sie online – schnell, transparent und stressfrei.
                </p>

                <div class="hero-features">
                    {#each reasons as r}
                        <div class="mini-feature">
                            <span class="mini-icon">{r.icon}</span>
                            <span>{r.text}</span>
                        </div>
                    {/each}
                </div>
            </div>

            <div class="hero-right">
                <div class="info-box">
                    <div class="info-header">
                        <div class="info-icon">🦷</div>
                        <h3>Warum SwissDentalLine?</h3>
                    </div>
                    <div class="info-list">
                        <div class="info-item">
                            <span class="item-number">01</span>
                            <div class="item-content">
                                <h4>Sofortige Verfügbarkeit</h4>
                                <p>Echtzeit-Übersicht aller freien Termine.</p>
                            </div>
                        </div>
                        <div class="info-item">
                            <span class="item-number">02</span>
                            <div class="item-content">
                                <h4>Warteliste & Short-Notice</h4>
                                <p>Auf Warteliste setzen, automatisch nachrücken und spontan zugesagte Slots nutzen.</p>
                            </div>
                        </div>
                        <div class="info-item">
                            <span class="item-number">03</span>
                            <div class="item-content">
                                <h4>Transparente Preise</h4>
                                <p>Klare Angaben pro Behandlung, keine versteckten Kosten.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="cta">
        <div class="cta-inner">
            <h3>Bereit für Ihren nächsten Zahnarztbesuch?</h3>
            <p class="cta-sub">
                Starten Sie jetzt und finden Sie den perfekten Termin – in
                wenigen Klicks.
            </p>
            <a href="/buchen" class="btn-cta">Loslegen und Termine finden →</a>
        </div>
    </section>

    <section class="features">
        {#each features as f}
            <article class="feature-card">
                <div class="feature-header">
                    <div class="icon">{f.icon}</div>
                    <span class="badge">{f.highlight}</span>
                </div>
                <h3>{f.title}</h3>
                <p>{f.desc}</p>
            </article>
        {/each}
    </section>
</main>

<footer class="landing-footer">
    <small
        >© {new Date().getFullYear()} SwissDentalLine — Preise indikativ</small
    >
</footer>

<style>
    :global(:root) {
        --bg: #fbfdff;
        --teal: #00796b;
        --accent: #00bfa5;
        --muted: #6b7280;
        --card: #ffffff;
    }

    .landing {
        background: var(--bg);
        min-height: 100vh;
    }

    .hero {
        position: relative;
        padding: 1.25rem 1.25rem 1.5rem;
        min-height: 520px;
        display: flex;
        align-items: center;
        overflow: hidden;
    }
    .hero-bg {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: linear-gradient(
            135deg,
            rgba(0, 191, 165, 0.05) 0%,
            rgba(0, 121, 107, 0.08) 50%,
            rgba(0, 191, 165, 0.03) 100%
        );
        z-index: 0;
    }
    .hero-bg::after {
        content: "";
        position: absolute;
        top: -50%;
        right: -10%;
        width: 500px;
        height: 500px;
        background: radial-gradient(
            circle,
            rgba(0, 191, 165, 0.15) 0%,
            transparent 70%
        );
        border-radius: 50%;
    }

    .hero-content {
        position: relative;
        z-index: 1;
        max-width: 1180px;
        margin: 0 auto;
        display: grid;
        grid-template-columns: 1fr;
        gap: 2.2rem;
        align-items: center;
    }

    .hero-badge {
        display: inline-block;
        background: linear-gradient(135deg, var(--accent), var(--teal));
        color: #fff;
        padding: 0.5rem 1rem;
        border-radius: 30px;
        font-size: 0.85rem;
        font-weight: 600;
        margin-bottom: 1.5rem;
    }

    .hero-title {
        font-size: 2.5rem;
        line-height: 1.2;
        margin: 0 0 1rem;
        color: #042a2b;
        font-weight: 800;
    }
    .hero-subtitle {
        font-size: 1.1rem;
        color: var(--muted);
        line-height: 1.6;
        margin-bottom: 2rem;
        max-width: 600px;
    }

    .hero-features {
        display: flex;
        gap: 2rem;
        flex-wrap: wrap;
    }
    .mini-feature {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        color: var(--teal);
        font-weight: 600;
        font-size: 0.95rem;
    }
    .mini-icon {
        color: var(--accent);
        font-size: 1.1rem;
    }

    .hero-right {
        display: none;
    }
    .info-box {
        background: linear-gradient(135deg, #fff 0%, rgba(0, 191, 165, 0.04) 100%);
        border-radius: 20px;
        padding: 1.6rem;
        box-shadow: 0 10px 32px rgba(0, 121, 107, 0.12);
        border: 2px solid rgba(0, 191, 165, 0.16);
    }
    .info-header {
        display: flex;
        align-items: center;
        gap: 1rem;
        margin-bottom: 1.75rem;
        padding-bottom: 1rem;
        border-bottom: 2px solid rgba(0, 191, 165, 0.1);
    }
    .info-icon {
        font-size: 2.5rem;
        filter: drop-shadow(0 4px 12px rgba(0, 121, 107, 0.25));
    }
    .info-header h3 {
        margin: 0;
        font-size: 1.5rem;
        color: #042a2b;
        font-weight: 700;
    }
    .info-list {
        display: flex;
        flex-direction: column;
        gap: 1.1rem;
        margin-bottom: 1.25rem;
    }
    .info-item {
        display: flex;
        gap: 1rem;
        align-items: flex-start;
    }
    .item-number {
        display: flex;
        align-items: center;
        justify-content: center;
        min-width: 2.5rem;
        height: 2.5rem;
        background: linear-gradient(135deg, var(--accent), var(--teal));
        color: #fff;
        font-weight: 700;
        font-size: 0.9rem;
        border-radius: 12px;
    }
    .item-content h4 {
        margin: 0 0 0.25rem;
        font-size: 1rem;
        color: #042a2b;
        font-weight: 600;
    }
    .item-content p {
        margin: 0;
        font-size: 0.9rem;
        color: var(--muted);
        line-height: 1.5;
    }

    .features {
        display: grid;
        grid-template-columns: repeat(1, 1fr);
        gap: 1.25rem;
        padding: 2rem 1rem;
        max-width: 1100px;
        margin: 1.5rem auto;
    }
    .feature-card {
        padding: 1.5rem;
        background: var(--card);
        border-radius: 16px;
        box-shadow: 0 4px 12px rgba(0, 121, 107, 0.08);
        border: 1px solid rgba(0, 191, 165, 0.1);
        transition: all 0.3s ease;
    }
    .feature-card:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 121, 107, 0.15);
    }
    .feature-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 0.75rem;
    }
    .feature-card .icon {
        font-size: 2rem;
    }
    .badge {
        background: linear-gradient(135deg, var(--accent), var(--teal));
        color: #fff;
        padding: 0.25rem 0.75rem;
        border-radius: 20px;
        font-size: 0.75rem;
        font-weight: 700;
    }
    .feature-card h3 {
        margin: 0 0 0.5rem;
        font-size: 1.15rem;
        color: #042a2b;
    }
    .feature-card p {
        margin: 0;
        color: var(--muted);
        line-height: 1.6;
    }

    .cta {
        background: linear-gradient(
            135deg,
            rgba(0, 191, 165, 0.12),
            rgba(0, 121, 107, 0.08)
        );
        padding: 2.5rem 1.5rem;
        border-radius: 20px;
        margin: 2rem 1rem;
        border: 2px solid rgba(0, 191, 165, 0.2);
    }
    .cta-inner {
        max-width: 1100px;
        margin: 0 auto;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
        text-align: center;
    }
    .cta-inner h3 {
        margin: 0;
        color: #042a2b;
        font-size: 1.5rem;
    }
    .cta-sub {
        color: var(--muted);
        margin: 0;
        font-size: 1rem;
    }
    .btn-cta {
        background: linear-gradient(135deg, var(--accent), var(--teal));
        color: #fff;
        padding: 1rem 2rem;
        border-radius: 12px;
        text-decoration: none;
        font-weight: 700;
        font-size: 1.05rem;
        box-shadow: 0 4px 12px rgba(0, 121, 107, 0.3);
        transition: all 0.3s ease;
    }
    .btn-cta:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(0, 121, 107, 0.4);
    }

    .landing-footer {
        padding: 1rem;
        text-align: center;
        color: var(--muted);
    }

    @media (min-width: 700px) {
        .features {
            grid-template-columns: repeat(3, 1fr);
        }
    }

    @media (min-width: 1024px) {
        .hero-content {
            grid-template-columns: 2fr 1fr;
            gap: 2.4rem;
        }
        .hero-right {
            display: block;
        }
        .hero-title {
            font-size: 3rem;
        }
        .hero-subtitle {
            font-size: 1.08rem;
        }
    }
</style>

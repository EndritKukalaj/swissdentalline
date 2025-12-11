<script>
  import { goto, invalidateAll } from '$app/navigation';
  import './styles.css';
  
  let { data } = $props();
  
  // Reactive state variables
  let stats = $state(data.stats);
  let filter = $state(data.filter);
  let zeitraum = $state(data.zeitraum);
  let maxRevenue = $state(Math.max(...data.stats.einnahmenVerlauf.map(d => d.value), 1));
  let maxAppointments = $state(Math.max(...data.stats.termineVerlauf.map(d => d.value), 1));

  // Update state when data changes
  $effect(() => {
    stats = data.stats;
    filter = data.filter;
    zeitraum = data.zeitraum;
    maxRevenue = Math.max(...data.stats.einnahmenVerlauf.map(d => d.value), 1);
    maxAppointments = Math.max(...data.stats.termineVerlauf.map(d => d.value), 1);
  });

  const filterOptions = [
    { value: 'week', label: 'Woche' },
    { value: 'month', label: 'Monat' },
    { value: 'year', label: 'Jahr' },
    { value: 'all', label: 'Gesamt' }
  ];

  const handleFilterChange = async (newFilter) => {
    await goto(`/statistik?filter=${newFilter}`, { invalidateAll: true });
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('de-CH', {
      style: 'currency',
      currency: 'CHF'
    }).format(amount);
  };

</script>

<div class="statistik-wrapper blue-variant">
  <!-- Header -->
  <div class="statistik-header">
    <div class="header-content">
      <h1 class="header-title">
        <i class="bi bi-bar-chart-line"></i>
        Statistiken & Auswertungen
      </h1>
      <p class="header-subtitle">Überblick über Ihre Praxisleistung</p>
    </div>

    <!-- Filter Buttons -->
    <div class="filter-buttons">
      {#each filterOptions as option}
        <button
          class="filter-btn {filter === option.value ? 'active' : ''}"
          onclick={() => handleFilterChange(option.value)}
        >
          {option.label}
        </button>
      {/each}
    </div>

    <!-- Zeitraum Anzeige -->
    <div class="zeitraum-display">
      <i class="bi bi-calendar-range"></i>
      <span>Zeitraum: {zeitraum.von} - {zeitraum.bis}</span>
    </div>
  </div>

  <!-- Content -->
  <div class="statistik-content">
    <!-- KPI Cards Row 1 -->
    <div class="kpi-grid">
      <div class="kpi-card">
        <div class="kpi-icon auslastung">
          <i class="bi bi-speedometer2"></i>
        </div>
        <div class="kpi-content">
          <div class="kpi-label">Auslastung</div>
          <div class="kpi-value">{stats.auslastung}%</div>
        </div>
        <div class="kpi-bar">
          <div class="kpi-bar-fill" style="width: {stats.auslastung}%"></div>
        </div>
      </div>

      <div class="kpi-card">
        <div class="kpi-icon einnahmen">
          <i class="bi bi-cash-coin"></i>
        </div>
        <div class="kpi-content">
          <div class="kpi-label">Einnahmen</div>
          <div class="kpi-value">{formatCurrency(stats.gesamtEinnahmen)}</div>
        </div>
      </div>

      <div class="kpi-card">
        <div class="kpi-icon termine">
          <i class="bi bi-calendar-check"></i>
        </div>
        <div class="kpi-content">
          <div class="kpi-label">Abgeschlossene Termine</div>
          <div class="kpi-value">{stats.appointmentsByStatus.abgeschlossen}</div>
        </div>
      </div>

      <div class="kpi-card">
        <div class="kpi-icon bewertung">
          <i class="bi bi-star-fill"></i>
        </div>
        <div class="kpi-content">
          <div class="kpi-label">Durchschn. Bewertung</div>
          <div class="kpi-value">
            {stats.durchschnittsBewertung} 
            <span class="kpi-subtext">({stats.anzahlRezensionen})</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Row -->
    <div class="charts-grid">
      <!-- Revenue Chart -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>
            <i class="bi bi-graph-up"></i>
            Einnahmen-Entwicklung
          </h3>
        </div>
        <div class="chart-content">
          <div class="bar-chart">
            {#each stats.einnahmenVerlauf as dataPoint}
              <div class="bar-group">
                <div class="bar-container" style="height: {(dataPoint.value / maxRevenue) * 90}%">
                  <div 
                    class="bar revenue-bar" 
                    title="{formatCurrency(dataPoint.value)}"
                  >
                    {#if dataPoint.value > 0}
                      <span class="bar-value">{Math.round(dataPoint.value)}</span>
                    {/if}
                  </div>
                </div>
                <div class="bar-label">{dataPoint.label}</div>
              </div>
            {/each}
          </div>
        </div>
      </div>

      <!-- Appointments Chart -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>
            <i class="bi bi-calendar3"></i>
            Termine-Entwicklung
          </h3>
        </div>
        <div class="chart-content">
          <div class="bar-chart">
            {#each stats.termineVerlauf as dataPoint}
              <div class="bar-group">
                <div class="bar-container" style="height: {(dataPoint.value / maxAppointments) * 90}%">
                  <div 
                    class="bar appointments-bar" 
                    title="{dataPoint.value} Termine"
                  >
                    {#if dataPoint.value > 0}
                      <span class="bar-value">{dataPoint.value}</span>
                    {/if}
                  </div>
                </div>
                <div class="bar-label">{dataPoint.label}</div>
              </div>
            {/each}
          </div>
        </div>
      </div>
    </div>

    <!-- Bottom Row -->
    <div class="bottom-grid">
      <!-- Appointments by Status -->
      <div class="info-card">
        <div class="info-header">
          <h3>
            <i class="bi bi-pie-chart"></i>
            Termine nach Status
          </h3>
        </div>
        <div class="status-list">
          <div class="status-item">
            <div class="status-indicator gebucht"></div>
            <div class="status-label">Gebucht</div>
            <div class="status-value">{stats.appointmentsByStatus.gebucht}</div>
          </div>
          <div class="status-item">
            <div class="status-indicator abgeschlossen"></div>
            <div class="status-label">Abgeschlossen</div>
            <div class="status-value">{stats.appointmentsByStatus.abgeschlossen}</div>
          </div>
          <div class="status-item">
            <div class="status-indicator frei"></div>
            <div class="status-label">Frei</div>
            <div class="status-value">{stats.appointmentsByStatus.frei}</div>
          </div>
          <div class="status-item">
            <div class="status-indicator abgesagt"></div>
            <div class="status-label">Abgesagt</div>
            <div class="status-value">{stats.appointmentsByStatus.abgesagt}</div>
          </div>
          <div class="status-item">
            <div class="status-indicator flex"></div>
            <div class="status-label">Flex</div>
            <div class="status-value">{stats.appointmentsByStatus.flex}</div>
          </div>
          <div class="status-item total-item">
            <div class="status-indicator gesamt"></div>
            <div class="status-label">Gesamt</div>
            <div class="status-value">
              {stats.appointmentsByStatus.gebucht + 
               stats.appointmentsByStatus.flex + 
               stats.appointmentsByStatus.abgeschlossen + 
               stats.appointmentsByStatus.abgesagt + 
               stats.appointmentsByStatus.frei}
            </div>
          </div>
        </div>
      </div>

      <!-- Top Treatments -->
      <div class="info-card">
        <div class="info-header">
          <h3>
            <i class="bi bi-trophy"></i>
            Top Behandlungsarten
          </h3>
        </div>
        <div class="treatments-list">
          {#if stats.topBehandlungen.length > 0}
            {#each stats.topBehandlungen as behandlung, index}
              <div class="treatment-item">
                <div class="treatment-rank">#{index + 1}</div>
                <div class="treatment-name">{behandlung.name}</div>
                <div class="treatment-count">
                  <i class="bi bi-check2-circle"></i>
                  {behandlung.count}
                </div>
              </div>
            {/each}
          {:else}
            <div class="empty-state">
              <i class="bi bi-inbox"></i>
              <p>Keine Daten verfügbar</p>
            </div>
          {/if}
        </div>
      </div>
    </div>
  </div>
</div>

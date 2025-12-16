<script>
  import { goto, invalidateAll } from '$app/navigation';
  import { EmptyState, KpiCard } from '$lib';
  
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
      <KpiCard
        icon="speedometer2"
        label="Auslastung"
        value="{stats.auslastung}%"
        iconType="auslastung"
        progressValue={stats.auslastung}
        variant="blue"
      />

      <KpiCard
        icon="cash-coin"
        label="Einnahmen"
        value={formatCurrency(stats.gesamtEinnahmen)}
        iconType="einnahmen"
        variant="blue"
      />

      <KpiCard
        icon="calendar-check"
        label="Abgeschlossene Termine"
        value={stats.appointmentsByStatus.abgeschlossen}
        iconType="termine"
        variant="blue"
      />

      <KpiCard
        icon="star-fill"
        label="Durchschn. Bewertung"
        value={stats.durchschnittsBewertung}
        subtext="({stats.anzahlRezensionen})"
        iconType="bewertung"
        variant="blue"
      />
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
            <EmptyState
              icon="inbox"
              title="Keine Daten verfügbar"
              variant="blue"
            />
          {/if}
        </div>
      </div>
    </div>
  </div>
</div>


<style>
/* Statistik Page Styles */
.statistik-wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  padding: 2rem;
}

.statistik-wrapper.blue-variant {
  background: linear-gradient(135deg, #f0f8ff 0%, #e1f0f7 100%);
}

.statistik-header {
  max-width: 1400px;
  margin: 0 auto 2rem;
}

.header-content {
  margin-bottom: 1.5rem;
}

.header-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 0.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.header-title i {
  color: #30B0C7;
}

.header-subtitle {
  font-size: 1.1rem;
  color: #6c757d;
  margin: 0;
}

/* Filter Buttons */
.filter-buttons {
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 0.75rem 1.5rem;
  border: 2px solid #30B0C7;
  background: white;
  color: #30B0C7;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.95rem;
}

.filter-btn:hover {
  background: #e6f7fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(48, 176, 199, 0.2);
}

.filter-btn.active {
  background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(48, 176, 199, 0.3);
}

/* Zeitraum Display */
.zeitraum-display {
  margin-top: 1.5rem;
  padding: 1rem 1.5rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-weight: 500;
  color: #495057;
  border-left: 4px solid #30B0C7;
}

.zeitraum-display i {
  color: #30B0C7;
  font-size: 1.2rem;
}

.zeitraum-display span {
  font-size: 0.95rem;
}

/* Content */
.statistik-content {
  max-width: 1400px;
  margin: 0 auto;
}

/* KPI Cards */
.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

/* Charts */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.chart-header {
  padding-bottom: 1rem;
  border-bottom: 2px solid #e9ecef;
}

.chart-header h3 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.chart-header h3 i {
  color: #30B0C7;
}

.chart-content {
  height: 300px;
}

.bar-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 100%;
  gap: 0.5rem;
  position: relative;
}

/* Grid lines für bessere Lesbarkeit */
.bar-chart::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100%;
  background-image: linear-gradient(to top, #e9ecef 1px, transparent 1px);
  background-size: 100% 25%;
  pointer-events: none;
  opacity: 0.5;
}

.bar-group {
  flex: 1;
  display: flex;
  flex-direction: column-reverse;
  align-items: center;
  gap: 0.3rem;
  min-width: 0;
  position: relative;
  z-index: 1;
  height: 100%;
}

.bar-container {
  flex: none;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  position: relative;
  min-height: 4px;
}

.bar {
  width: 100%;
  height: 100%;
  max-width: 60px;
  min-height: 4px;
  border-radius: 8px 8px 0 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 0.25rem;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
}

.bar:hover {
  opacity: 0.85;
  transform: scaleX(1.1) translateY(-2px);
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.15);
}

.bar-value {
  font-size: 0.7rem;
  font-weight: 700;
  color: white;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  white-space: nowrap;
}

.revenue-bar {
  background: linear-gradient(180deg, #28a745 0%, #34ce57 50%, #20c997 100%);
  position: relative;
}

.revenue-bar::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  border-radius: 8px 8px 0 0;
}

.appointments-bar {
  background: linear-gradient(180deg, #30B0C7 0%, #268a9c 50%, #1e6b7a 100%);
  position: relative;
}

.appointments-bar::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  border-radius: 8px 8px 0 0;
}

.bar-label {
  font-size: 0.7rem;
  color: #6c757d;
  text-align: center;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

/* Bottom Grid */
.bottom-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 1.5rem;
}

.info-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.info-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e9ecef;
}

.info-header h3 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.info-header h3 i {
  color: #30B0C7;
}

/* Status List */
.status-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem;
  border-radius: 8px;
  background: #f8f9fa;
  transition: all 0.2s ease;
}

.status-item:hover {
  background: #e9ecef;
  transform: translateX(4px);
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.status-indicator.gebucht {
  background: #009688;
}

.status-indicator.flex {
  background: #FFC107;
}

.status-indicator.abgeschlossen {
  background: #30B0C7;
}

.status-indicator.abgesagt {
  background: #f44336;
}

.status-indicator.frei {
  background: #64748b;
}

.status-indicator.gesamt {
  background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
}

.status-label {
  flex: 1;
  font-weight: 500;
  color: #495057;
}

.status-value {
  font-weight: 700;
  font-size: 1.1rem;
  color: #1a1a2e;
}

.status-item.total-item .status-label {
  font-weight: 700;
  color: #1a1a2e;
}

.status-item.total-item .status-value {
  font-size: 1.3rem;
  color: #30B0C7;
}

/* Treatments List */
.treatments-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.treatment-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-radius: 8px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
  border: 1px solid #e9ecef;
  transition: all 0.2s ease;
}

.treatment-item:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #f8f9fa 100%);
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.treatment-rank {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.9rem;
  flex-shrink: 0;
}

.treatment-name {
  flex: 1;
  font-weight: 500;
  color: #495057;
}

.treatment-count {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-weight: 700;
  color: #30B0C7;
  font-size: 1.1rem;
}

.treatment-count i {
  font-size: 1rem;
}

/* Responsive */
@media (max-width: 768px) {
  .statistik-wrapper {
    padding: 0.75rem;
  }

  .header-title {
    font-size: 1.5rem;
    gap: 0.5rem;
  }

  .header-title i {
    font-size: 1.3rem;
  }

  .header-subtitle {
    font-size: 0.85rem;
  }

  .filter-buttons {
    gap: 0.5rem;
  }

  .filter-btn {
    padding: 0.5rem 0.75rem;
    font-size: 0.8rem;
  }

  .zeitraum-display {
    padding: 0.75rem 1rem;
    font-size: 0.85rem;
  }

  .zeitraum-display i {
    font-size: 1rem;
  }

  .kpi-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.75rem;
  }
  .charts-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .chart-card {
    padding: 1rem;
  }

  .chart-header {
    padding-bottom: 0.75rem;
  }

  .chart-header h3 {
    font-size: 1.05rem;
  }

  .chart-content {
    height: 200px;
  }

  .bar-chart {
    gap: 0.25rem;
    padding: 0.75rem 0.25rem;
  }

  .bar {
    max-width: 35px;
  }

  .bar-value {
    font-size: 0.6rem;
  }

  .bar-label {
    font-size: 0.6rem;
  }

  .bottom-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .info-card {
    padding: 1rem;
  }

  .info-header {
    margin-bottom: 1rem;
    padding-bottom: 0.75rem;
  }

  .info-header h3 {
    font-size: 1.05rem;
  }

  .status-item {
    padding: 0.6rem;
    gap: 0.75rem;
  }

  .status-indicator {
    width: 10px;
    height: 10px;
  }

  .status-label {
    font-size: 0.85rem;
  }

  .status-value {
    font-size: 0.95rem;
  }

  .treatment-item {
    padding: 0.75rem;
    gap: 0.75rem;
  }

  .treatment-rank {
    width: 28px;
    height: 28px;
    font-size: 0.8rem;
  }

  .treatment-name {
    font-size: 0.85rem;
  }

  .treatment-count {
    font-size: 0.95rem;
  }
}

@media (max-width: 480px) {
  .statistik-wrapper {
    padding: 0.5rem;
  }

  .header-title {
    font-size: 1.25rem;
  }

  .header-subtitle {
    font-size: 0.8rem;
  }

  .filter-buttons {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 0.4rem;
  }

  .filter-btn {
    padding: 0.6rem 0.4rem;
    text-align: center;
    font-size: 0.75rem;
  }

  .zeitraum-display {
    padding: 0.6rem 0.75rem;
  }

  .zeitraum-display span {
    font-size: 0.75rem;
  }

  .kpi-grid {
    grid-template-columns: 1fr;
    gap: 0.6rem;
  }

  .chart-card {
    padding: 0.85rem;
  }

  .chart-header h3 {
    font-size: 0.95rem;
  }

  .chart-content {
    height: 180px;
  }

  .bar-chart {
    padding: 0.5rem 0.2rem;
  }

  .bar {
    max-width: 28px;
  }

  .bar-value {
    font-size: 0.55rem;
    padding-top: 0.15rem;
  }

  .bar-label {
    font-size: 0.55rem;
  }

  .info-card {
    padding: 0.85rem;
  }

  .info-header {
    margin-bottom: 0.85rem;
  }

  .info-header h3 {
    font-size: 0.95rem;
  }

  .status-list {
    gap: 0.6rem;
  }

  .status-item {
    padding: 0.5rem;
  }

  .status-label {
    font-size: 0.8rem;
  }

  .status-value {
    font-size: 0.9rem;
  }

  .status-item.total-item .status-value {
    font-size: 1.1rem;
  }

  .treatments-list {
    gap: 0.6rem;
  }

  .treatment-item {
    padding: 0.65rem;
  }

  .treatment-rank {
    width: 26px;
    height: 26px;
    font-size: 0.75rem;
  }

  .treatment-name {
    font-size: 0.8rem;
  }

  .treatment-count {
    font-size: 0.9rem;
  }
}
</style>
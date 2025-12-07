<script>
    import './styles.css';
    import { goto } from '$app/navigation';
    import BookingProgressBar from '$lib/components/BookingProgressBar.svelte';
    
    let { data } = $props();
    let { behandlungsart, termine } = data;
    
    // Pagination state
    let currentPage = $state(1); // 1-indexed like rezensionen
    const datesPerPage = 5; // Show 5 dates per page
    
    const handleTerminSelect = (termin) => {
        goto(`/buchen/zahnarzt?terminId=${termin.id}`);
    };
    
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        return date.toLocaleDateString('de-DE', options);
    };
    
    const formatTime = (termin) => {
        // Extract time from datum ISO string (e.g., "2025-12-15T08:00:00Z")
        if (termin.startZeit) {
            return termin.startZeit.substring(0, 5);
        }
        if (termin.datum) {
            const date = new Date(termin.datum);
            return date.toLocaleTimeString('de-DE', { hour: '2-digit', minute: '2-digit' });
        }
        return '--:--';
    };
    
    const getDay = (dateString) => {
        const date = new Date(dateString);
        return date.getDate();
    };
    
    const getMonth = (dateString) => {
        const date = new Date(dateString);
        const months = ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'];
        return months[date.getMonth()];
    };
    
    const getWeekday = (dateString) => {
        const date = new Date(dateString);
        const weekdays = ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'];
        return weekdays[date.getDay()];
    };
    
    // Group termine by date
    const groupedTermine = $derived(() => {
        const groups = {};
        termine.forEach(termin => {
            // Extract only the date part (YYYY-MM-DD) without time
            const dateOnly = termin.datum.split('T')[0];
            if (!groups[dateOnly]) {
                groups[dateOnly] = [];
            }
            groups[dateOnly].push(termin);
        });
        return groups;
    });
    
    // Pagination logic (1-indexed)
    const allDates = $derived(Object.keys(groupedTermine()));
    const totalPages = $derived(Math.ceil(allDates.length / datesPerPage));
    const paginatedDates = $derived(() => {
        const start = (currentPage - 1) * datesPerPage;
        const end = start + datesPerPage;
        return allDates.slice(start, end);
    });
    
    const goToPage = (page) => {
        currentPage = page;
        // Scroll to top of termine container
        document.querySelector('.termine-container')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    };
</script>

<div class="buchen-container">
    <BookingProgressBar currentStep={2} />
    
    <!-- Header -->
    <div class="page-header">
        <button class="back-btn" onclick={() => goto('/buchen/behandlung')}>
            <i class="bi bi-arrow-left"></i>
            Zurück
        </button>
        <h1 class="page-title">Wählen Sie Ihren Wunschtermin</h1>
        <div class="selected-behandlung">
            <i class="bi bi-clipboard2-pulse"></i>
            <span>{behandlungsart.name}</span>
        </div>
    </div>
    
    <!-- Termine List -->
    {#if termine.length > 0}
        <div class="termine-container">
            {#each paginatedDates() as datum (datum)}
                {@const dateTermine = groupedTermine()[datum]}
                <div class="date-group">
                    <div class="date-header">
                        <div class="date-badge">
                            <div class="date-day">{getDay(datum)}</div>
                            <div class="date-month">{getMonth(datum)}</div>
                        </div>
                        <div class="date-info">
                            <h3>{formatDate(datum)}</h3>
                            <p>{dateTermine.length} {dateTermine.length === 1 ? 'Termin' : 'Termine'} verfügbar</p>
                        </div>
                    </div>
                    
                    <div class="time-slots">
                        {#each dateTermine as termin (termin.id)}
                            <button 
                                class="time-slot-card" 
                                onclick={() => handleTerminSelect(termin)}
                            >
                                <div class="card-content-wrapper">
                                    <div class="card-info">
                                        <div class="time-info">
                                            <i class="bi bi-clock"></i>
                                            <span class="time">{formatTime(termin)}</span>
                                            <span class="weekday">{getWeekday(termin.datum)}</span>
                                        </div>
                                        
                                        {#if termin.zahnarzt}
                                            <div class="zahnarzt-info">
                                                <i class="bi bi-person-badge"></i>
                                                <span>{termin.zahnarzt.name}</span>
                                            </div>
                                        {/if}
                                    </div>
                                    
                                    <div class="slot-action">
                                        <i class="bi bi-arrow-right-circle"></i>
                                    </div>
                                </div>
                            </button>
                        {/each}
                    </div>
                </div>
            {/each}
            
            <!-- Pagination Controls (always visible) -->
            <div class="termin-pagination">
                <button 
                    class="btn btn-secondary-pagination"
                    class:disabled={currentPage === 1}
                    disabled={currentPage === 1}
                    onclick={() => goToPage(currentPage - 1)}
                >
                    <i class="bi bi-chevron-left"></i>
                    <span>Zurück</span>
                </button>
                
                <span class="page-info">
                    Seite {currentPage} von {totalPages}
                </span>
                
                <button 
                    class="btn btn-secondary-pagination"
                    class:disabled={currentPage >= totalPages}
                    disabled={currentPage >= totalPages}
                    onclick={() => goToPage(currentPage + 1)}
                >
                    <span>Weiter</span>
                    <i class="bi bi-chevron-right"></i>
                </button>
            </div>
        </div>
    {:else}
        <div class="empty-state">
            <i class="bi bi-calendar-x"></i>
            <h3>Keine freien Termine verfügbar</h3>
            <p>Für die gewählte Behandlung sind aktuell keine freien Termine vorhanden.</p>
            <button class="btn-primary" onclick={() => goto('/buchen/warteliste')}>
                Auf Warteliste setzen
            </button>
        </div>
    {/if}
</div>
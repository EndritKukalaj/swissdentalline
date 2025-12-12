<script>
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
                    <i class="bi bi-arrow-left"></i>
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

<style>
.buchen-container {
    min-height: 100vh;
    background: #f8fafc;
    padding: 2rem;
}

/* Header */
.page-header {
    max-width: 1200px;
    margin: 0 auto 2rem;
}

.back-btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.25rem;
    font-size: 0.875rem;
    font-weight: 500;
    color: #4a5568;
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    margin-bottom: 1rem;
}

.back-btn:hover {
    border-color: #009688;
    color: #009688;
    background: #f0fffe;
}

.page-title {
    font-size: 2rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 1rem 0;
}

.selected-behandlung {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem 1.5rem;
    background: linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%);
    border-radius: 12px;
    font-weight: 600;
    color: #1a202c;
}

.selected-behandlung i {
    font-size: 1.5rem;
    color: #009688;
}

/* Termine Container */
.termine-container {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    gap: 2rem;
}

.date-group {
    background: white;
    border-radius: 16px;
    padding: 1.5rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.date-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 2px solid #f1f5f9;
}

.date-badge {
    width: 70px;
    height: 70px;
    background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: white;
    flex-shrink: 0;
}

.date-day {
    font-size: 1.75rem;
    font-weight: 700;
    line-height: 1;
}

.date-month {
    font-size: 0.875rem;
    font-weight: 600;
    text-transform: uppercase;
}

.date-info h3 {
    font-size: 1.25rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 0.25rem 0;
}

.date-info p {
    font-size: 0.9375rem;
    color: #64748b;
    margin: 0;
}

.time-slots {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
    gap: 1rem;
}

.time-slot-card {
    background: #f8fafc;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    padding: 1.25rem;
    cursor: pointer;
    transition: all 0.3s ease;
    text-align: left;
}

.time-slot-card:hover {
    border-color: #009688;
    background: #f0fffe;
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(0, 150, 136, 0.15);
}

.card-content-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
}

.card-info {
    display: flex;
    flex-direction: column;
    flex: 1;
}

.time-info {
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.time-info i {
    font-size: 1.5rem;
    color: #009688;
}

.time {
    font-size: 1.5rem;
    font-weight: 700;
    color: #1a202c;
}

.weekday {
    font-size: 0.875rem;
    font-weight: 600;
    color: #64748b;
}

.zahnarzt-info {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.9375rem;
    color: #4a5568;
}

.zahnarzt-info i {
    color: #009688;
    font-size: 1rem;
}

.slot-action {
    display: flex;
    align-items: center;
    flex-shrink: 0;
}

.slot-action i {
    font-size: 1.5rem;
    color: #009688;
}

/* Empty State */
.empty-state {
    max-width: 500px;
    margin: 4rem auto;
    text-align: center;
    padding: 3rem 2rem;
    background: white;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.empty-state i {
    font-size: 4rem;
    color: #9ca3af;
    margin-bottom: 1.5rem;
}

.empty-state h3 {
    font-size: 1.5rem;
    font-weight: 700;
    color: #1a202c;
    margin: 0 0 0.5rem 0;
}

.empty-state p {
    font-size: 1rem;
    color: #64748b;
    margin: 0 0 2rem 0;
}

/* Termin Pagination */
.termin-pagination {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1.5rem;
    margin-top: 2rem;
    padding: 2rem;
    background: white;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.btn-secondary-pagination {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.875rem 1.5rem;
    font-size: 0.9375rem;
    font-weight: 600;
    color: #4a5568;
    background: white;
    border: 2px solid #e2e8f0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    text-decoration: none;
}

.btn-secondary-pagination:hover:not(:disabled):not(.disabled) {
    border-color: #009688;
    color: #009688;
    background: #f0fffe;
}

.btn-secondary-pagination:disabled,
.btn-secondary-pagination.disabled {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: none;
}

.page-info {
    font-size: 0.875rem;
    color: #4a5568;
    font-weight: 500;
}

.btn-primary {
    padding: 0.875rem 2rem;
    font-size: 1rem;
    font-weight: 600;
    color: white;
    background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(0, 150, 136, 0.3);
}

/* Responsive */
@media (max-width: 768px) {
    .buchen-container {
        padding: 1rem;
    }

    .back-btn {
        padding: 0.5rem 0.875rem;
        font-size: 0.8125rem;
        margin-bottom: 0.75rem;
    }
    
    .page-title {
        font-size: 1.25rem;
        margin-bottom: 0.5rem;
    }
    
    .selected-behandlung {
        padding: 0.75rem 1rem;
        font-size: 0.875rem;
        border-radius: 10px;
    }
    
    .selected-behandlung i {
        font-size: 1.25rem;
    }

    .termine-container {
        gap: 1rem;
    }
    
    .date-group {
        padding: 1rem;
        border-radius: 12px;
    }

    .date-header {
        margin-bottom: 1rem;
        padding-bottom: 0.75rem;
    }

    .date-badge {
        width: 56px;
        height: 56px;
        border-radius: 10px;
    }

    .date-day {
        font-size: 1.5rem;
    }

    .date-month {
        font-size: 0.75rem;
    }

    .date-info h3 {
        font-size: 1rem;
    }

    .date-info p {
        font-size: 0.8125rem;
    }
    
    .time-slots {
        grid-template-columns: 1fr;
        gap: 0.75rem;
    }
    
    .time-slot-card {
        padding: 0.875rem;
        border-radius: 10px;
    }

    .time-info {
        font-size: 0.875rem;
    }

    .time-info i {
        font-size: 1rem;
    }

    .time {
        font-size: 1rem;
    }

    .weekday {
        font-size: 0.75rem;
    }

    .zahnarzt-info {
        font-size: 0.8125rem;
    }

    .zahnarzt-info i {
        font-size: 0.875rem;
    }

    .slot-action i {
        font-size: 1.25rem;
    }
    
    .termin-pagination {
        padding: 1rem;
        margin-top: 1rem;
        border-radius: 12px;
    }
    
    .btn-secondary-pagination {
        flex: 0 0 auto;
        width: 80px;
        padding: 0.375rem;
        font-size: 0.8125rem;
        border-width: 1px;
    }
    
    .btn-secondary-pagination i {
        font-size: 1rem;
        margin: 0;
    }
    
    .btn-secondary-pagination span {
        display: none;
    }
    
    .page-info {
        font-size: 0.75rem;
        white-space: nowrap;
        flex: 1;
        text-align: center;
    }

    .empty-state {
        padding: 2rem 1rem;
    }

    .empty-state i {
        font-size: 2.5rem;
    }

    .empty-state h3 {
        font-size: 1.125rem;
    }

    .empty-state p {
        font-size: 0.875rem;
    }
}</style>


<script>
    import './styles.css';
    import { goto } from '$app/navigation';
    
    let { data } = $props();
    let { rezensionen, pagination } = data;
    
    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString('de-CH', { 
            year: 'numeric', 
            month: 'long', 
            day: 'numeric' 
        });
    };
    
    const getStatusBadge = (approved) => {
        return approved ? {
            text: 'Veröffentlicht',
            class: 'status-published'
        } : {
            text: 'In Prüfung',
            class: 'status-pending'
        };
    };
    
    const goToPage = (page) => {
        goto(`/rezensionen?page=${page}`);
    };
</script>

<svelte:head>
    <title>Meine Rezensionen - Swissdentalline</title>
</svelte:head>

<div class="rezensionen-container">
    <!-- Header -->
    <div class="page-header">
        <div class="header-content-rezension">
            <div class="header-icon">
                <i class="bi bi-star-fill"></i>
            </div>
            <h1 class="page-title">Meine Rezensionen</h1>
            <p class="page-subtitle">Übersicht Ihrer abgegebenen Bewertungen</p>
        </div>
    </div>
    
    <!-- Info Banner -->
    {#if rezensionen.length === 0}
        <div class="empty-state">
            <div class="empty-icon">
                <i class="bi bi-star"></i>
            </div>
            <h2>Noch keine Rezensionen</h2>
            <p>Sie haben noch keine Zahnärzte bewertet. Nach abgeschlossenen Terminen können Sie Ihre Erfahrungen teilen.</p>
            <button class="btn btn-primary" onclick={() => goto('/termine')}>
                <i class="bi bi-calendar-check"></i>
                Zu meinen Terminen
            </button>
        </div>
    {:else}
        <div class="info-banner">
            <i class="bi bi-info-circle"></i>
            <div>
                <strong>Hinweis:</strong> Alle Bewertungen werden vor der Veröffentlichung geprüft. 
                Bereits veröffentlichte Bewertungen können nicht mehr bearbeitet werden.
            </div>
        </div>
        
        <!-- Reviews List -->
        <div class="reviews-list">
            {#each rezensionen as review}
                {@const status = getStatusBadge(review.approved)}
                {@const zahnarztId = review.zahnarztId || review.zahnarzt_id}
                {@const aiKommentar = review.aiKommentar || review.ai_kommentar}
                
                <div class="review-card">
                    <div class="review-header">
                        <div class="review-title-section">
                            <div class="zahnarzt-info">
                                <i class="bi bi-person-circle"></i>
                                <h3>{review.zahnarzt?.name || 'Unbekannt'}</h3>
                            </div>
                            <div class="review-meta">
                                <span class="date">
                                    <i class="bi bi-calendar"></i>
                                    {formatDate(review.datum)}
                                </span>
                                <span class="status-badge {status.class}">
                                    {#if review.approved}
                                        <i class="bi bi-check-circle-fill"></i>
                                    {:else}
                                        <i class="bi bi-clock-history"></i>
                                    {/if}
                                    {status.text}
                                </span>
                            </div>
                        </div>
                        
                        <div class="rating-display">
                            {#each [1, 2, 3, 4, 5] as star}
                                <i class="bi {review.bewertung >= star ? 'bi-star-fill' : 'bi-star'}"></i>
                            {/each}
                        </div>
                    </div>
                    
                    <div class="review-content">
                        <p class="review-text">{review.text}</p>
                        
                        {#if aiKommentar}
                            <div class="ai-comment">
                                <div class="ai-comment-header">
                                    <i class="bi bi-robot"></i>
                                    <span>Moderationshinweis</span>
                                </div>
                                <p>{aiKommentar}</p>
                            </div>
                        {/if}
                    </div>
                </div>
            {/each}
        </div>
        
        <!-- Pagination -->
        {#if pagination.totalPages > 1}
            <div class="pagination">
                <button 
                    class="btn btn-secondary"
                    disabled={pagination.currentPage === 0}
                    onclick={() => goToPage(pagination.currentPage - 1)}
                >
                    <i class="bi bi-chevron-left"></i>
                    Zurück
                </button>
                
                <span class="page-info">
                    Seite {pagination.currentPage + 1} von {pagination.totalPages}
                </span>
                
                <button 
                    class="btn btn-secondary"
                    disabled={pagination.currentPage >= pagination.totalPages - 1}
                    onclick={() => goToPage(pagination.currentPage + 1)}
                >
                    Weiter
                    <i class="bi bi-chevron-right"></i>
                </button>
            </div>
        {/if}
    {/if}
</div>

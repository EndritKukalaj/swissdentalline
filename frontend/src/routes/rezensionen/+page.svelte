<script>
    import { goto } from "$app/navigation";
    import { enhance } from "$app/forms";

    let { data } = $props();

    // reactive state
    let rezensionen = $state(data.rezensionen);
    let currentPage = $state(data.currentPage);
    let nrOfPages = $state(data.nrOfPages);
    let userRole = $state(data.userRole);
    const pageSize = 5;

    // Delete confirmation state
    let showDeleteDialog = $state(false);
    let deleteReviewId = $state(null);
    let deleteReviewData = $state(null);
    let isDeleting = $state(false);

    // Update when server data changes
    $effect(() => {
        rezensionen = data.rezensionen;
        currentPage = data.currentPage;
        nrOfPages = data.nrOfPages;
        userRole = data.userRole;
    });

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleDateString("de-CH", {
            year: "numeric",
            month: "long",
            day: "numeric",
        });
    };

    const getStatusBadge = (approved, aiKommentar) => {
        if (approved) {
            return {
                text: "Veröffentlicht",
                class: "status-published",
            };
        } else if (aiKommentar) {
            return {
                text: "Abgelehnt",
                class: "status-rejected",
            };
        } else {
            return {
                text: "In Prüfung",
                class: "status-pending",
            };
        }
    };

    const confirmDelete = (review) => {
        deleteReviewId = review.id;
        deleteReviewData = review;
        showDeleteDialog = true;
    };

    const cancelDelete = () => {
        showDeleteDialog = false;
        deleteReviewId = null;
        deleteReviewData = null;
        isDeleting = false;
    };

    // Check if user is Zahnarzt
    const isZahnarzt = $derived(userRole === "Zahnarzt");
</script>

<svelte:head>
    <title>Meine Rezensionen - Swissdentalline</title>
</svelte:head>

<div class="rezensionen-container {isZahnarzt ? 'zahnarzt-view' : ''}">
    <!-- Header -->
    <div class="page-header">
        <div class="header-content-rezension">
            <div class="header-icon {isZahnarzt ? 'blue' : ''}">
                <i class="bi bi-star-fill"></i>
            </div>
            <h1 class="page-title">
                {#if isZahnarzt}
                    Erhaltene Bewertungen
                {:else}
                    Meine Rezensionen
                {/if}
            </h1>
            <p class="page-subtitle">
                {#if isZahnarzt}
                    Übersicht aller Patientenbewertungen Ihrer Praxis
                {:else}
                    Übersicht Ihrer abgegebenen Bewertungen
                {/if}
            </p>
        </div>
    </div>

    <!-- Info Banner -->
    {#if rezensionen.length === 0}
        <div class="empty-state">
            <div class="empty-icon">
                <i class="bi bi-star"></i>
            </div>
            <h2>
                {#if isZahnarzt}
                    Noch keine Bewertungen erhalten
                {:else}
                    Noch keine Rezensionen
                {/if}
            </h2>
            <p>
                {#if isZahnarzt}
                    Sie haben noch keine Bewertungen von Patienten erhalten.
                    Bewertungen erscheinen hier nach abgeschlossenen
                    Behandlungen.
                {:else}
                    Sie haben noch keine Zahnärzte bewertet. Nach
                    abgeschlossenen Terminen können Sie Ihre Erfahrungen teilen.
                {/if}
            </p>
            {#if !isZahnarzt}
                <button
                    class="btn btn-primary"
                    onclick={() => goto("/termine")}
                >
                    <i class="bi bi-calendar-check"></i>
                    Zu meinen Terminen
                </button>
            {/if}
        </div>
    {:else}
        <div class="info-banner {isZahnarzt ? 'blue' : ''}">
            <i class="bi bi-info-circle"></i>
            <div>
                {#if isZahnarzt}
                    <strong>Hinweis:</strong> Alle Bewertungen werden vor der Veröffentlichung
                    geprüft. Nur veröffentlichte Bewertungen sind für Patienten sichtbar.
                {:else}
                    <strong>Hinweis:</strong> Alle Bewertungen werden vor der Veröffentlichung
                    geprüft. Bereits veröffentlichte Bewertungen können nicht mehr
                    bearbeitet werden.
                {/if}
            </div>
        </div>

        <!-- Reviews List -->
        <div class="reviews-list">
            {#each rezensionen as review}
                {@const aiKommentar = review.aiKommentar || review.ai_kommentar}
                {@const status = getStatusBadge(review.approved, aiKommentar)}

                <div class="review-card">
                    <div class="review-header">
                        <div class="review-title-section">
                            <div class="zahnarzt-info">
                                <i class="bi bi-person-circle"></i>
                                <h3>
                                    {#if isZahnarzt}
                                        {review.patientName || "Unbekannt"}
                                    {:else}
                                        {review.zahnarzt?.name || "Unbekannt"}
                                    {/if}
                                </h3>
                            </div>
                            <div class="review-meta">
                                <span class="date">
                                    <i class="bi bi-calendar"></i>
                                    {formatDate(review.datum)}
                                </span>
                                <span class="status-badge {status.class}">
                                    {#if review.approved}
                                        <i class="bi bi-check-circle-fill"></i>
                                    {:else if aiKommentar}
                                        <i class="bi bi-x-circle-fill"></i>
                                    {:else}
                                        <i class="bi bi-clock-history"></i>
                                    {/if}
                                    {status.text}
                                </span>
                            </div>
                        </div>

                        <div class="rating-display">
                            {#each [1, 2, 3, 4, 5] as star}
                                <i
                                    class="bi {review.bewertung >= star
                                        ? 'bi-star-fill'
                                        : 'bi-star'}"
                                ></i>
                            {/each}
                        </div>
                    </div>

                    <div class="review-content">
                        <p class="review-text">{review.text}</p>

                        {#if aiKommentar}
                            <div class="ai-comment">
                                <div class="ai-comment-header">
                                    <i class="bi bi-robot"></i>
                                    <span>AI-Moderationshinweis</span>
                                </div>
                                <p>{aiKommentar}</p>
                            </div>
                        {/if}

                        {#if !isZahnarzt}
                            <div class="review-actions">
                                <button
                                    class="btn-icon btn-edit"
                                    onclick={() =>
                                        goto(
                                            `/rezensionen/${review.id}/bearbeiten`,
                                        )}
                                    title="Rezension bearbeiten"
                                >
                                    <i class="bi bi-pencil-fill"></i>
                                </button>
                                <button
                                    type="button"
                                    class="btn-icon btn-delete"
                                    onclick={() => confirmDelete(review)}
                                    title="Rezension löschen"
                                >
                                    <i class="bi bi-trash-fill"></i>
                                </button>
                            </div>
                        {/if}
                    </div>
                </div>
            {/each}
        </div>

        <!-- Pagination -->
        {#if nrOfPages > 1}
            <div class="pagination">
                <a
                    class="btn btn-secondary {isZahnarzt ? 'blue' : ''}"
                    class:disabled={currentPage === 1}
                    href="/rezensionen?pageNumber={currentPage -
                        1}&pageSize={pageSize}"
                    aria-disabled={currentPage === 1}
                >
                    <i class="bi bi-chevron-left"></i>
                    <span>Zurück</span>
                </a>

                <span class="page-info">
                    Seite {currentPage} von {nrOfPages}
                </span>

                <a
                    class="btn btn-secondary {isZahnarzt ? 'blue' : ''}"
                    class:disabled={currentPage >= nrOfPages}
                    href="/rezensionen?pageNumber={currentPage +
                        1}&pageSize={pageSize}"
                    aria-disabled={currentPage >= nrOfPages}
                >
                    <span>Weiter</span>
                    <i class="bi bi-chevron-right"></i>
                </a>
            </div>
        {/if}
    {/if}
</div>

<!-- Delete Confirmation Dialog -->
{#if showDeleteDialog && deleteReviewData}
    <!-- svelte-ignore a11y_click_events_have_key_events -->
    <!-- svelte-ignore a11y_no_static_element_interactions -->
    <div class="dialog-overlay" onclick={cancelDelete}>
        <div class="dialog-content" onclick={(e) => e.stopPropagation()}>
            <div class="dialog-header">
                <i class="bi bi-trash" style="color: #dc2626;"></i>
                <h3>Rezension löschen</h3>
            </div>
            <div class="dialog-body">
                <p>Möchten Sie diese Rezension wirklich löschen?</p>
                <div class="dialog-info">
                    <div class="info-row">
                        <strong>Zahnarzt:</strong>
                        <span
                            >{deleteReviewData.zahnarzt?.name ||
                                "Unbekannt"}</span
                        >
                    </div>
                    <div class="info-row">
                        <strong>Bewertung:</strong>
                        <span>
                            {#each [1, 2, 3, 4, 5] as star}
                                <i
                                    class="bi {deleteReviewData.bewertung >=
                                    star
                                        ? 'bi-star-fill'
                                        : 'bi-star'}"
                                    style="color: #fbbf24; font-size: 0.875rem;"
                                ></i>
                            {/each}
                        </span>
                    </div>
                    <div class="info-row">
                        <strong>Datum:</strong>
                        <span>{formatDate(deleteReviewData.datum)}</span>
                    </div>
                </div>
                <p class="warning-text">
                    <i class="bi bi-exclamation-triangle"></i>
                    Diese Aktion kann nicht rückgängig gemacht werden.
                </p>
            </div>
            <div class="dialog-actions">
                <button
                    class="dialog-btn cancel-dialog-btn"
                    onclick={cancelDelete}
                    disabled={isDeleting}
                >
                    Abbrechen
                </button>
                <form
                    method="POST"
                    action="?/deleteReview&id={deleteReviewId}"
                    use:enhance={() => {
                        isDeleting = true;
                        return async ({ result, update }) => {
                            await update();
                            if (result.type === "success") {
                                cancelDelete();
                            }
                        };
                    }}
                >
                    <button
                        type="submit"
                        class="dialog-btn confirm-btn delete-confirm-btn"
                        disabled={isDeleting}
                    >
                        {#if isDeleting}
                            <i class="bi bi-hourglass-split"></i>
                            Wird gelöscht...
                        {:else}
                            <i class="bi bi-trash"></i>
                            Rezension löschen
                        {/if}
                    </button>
                </form>
            </div>
        </div>
    </div>
{/if}


<style>
    .rezensionen-container {
        padding: 2rem;
        max-width: 1000px;
        margin: 0 auto;
    }

    .page-header {
        margin-bottom: 2rem;
    }

    .header-content-rezension {
        text-align: center;
    }

    .header-icon {
        width: 64px;
        height: 64px;
        margin: 0 auto 1rem;
        background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
        border-radius: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 2rem;
    }
    
    .header-icon.blue {
        background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
    }

    .page-title {
        font-size: 2rem;
        font-weight: 700;
        color: #1a202c;
        margin: 0 0 0.5rem;
    }

    .page-subtitle {
        font-size: 1rem;
        color: #718096;
        margin: 0;
    }

    .empty-state {
        text-align: center;
        padding: 4rem 2rem;
        background: white;
        border-radius: 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .empty-icon {
        width: 80px;
        height: 80px;
        margin: 0 auto 1.5rem;
        background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e0 100%);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #718096;
        font-size: 2.5rem;
    }

    .empty-state h2 {
        font-size: 1.5rem;
        font-weight: 600;
        color: #1a202c;
        margin: 0 0 0.75rem;
    }

    .empty-state p {
        color: #718096;
        margin: 0 0 2rem;
        line-height: 1.6;
    }

    .info-banner {
        display: flex;
        gap: 1rem;
        padding: 1rem 1.5rem;
        background: #f0fffe;
        border-left: 4px solid #009688;
        border-radius: 8px;
        margin-bottom: 2rem;
        align-items: flex-start;
    }
    
    .info-banner.blue {
        background: #E8F8FA;
        border-left-color: #30B0C7;
    }

    .info-banner i {
        font-size: 1.25rem;
        color: #009688;
        flex-shrink: 0;
        margin-top: 0.125rem;
    }
    
    .info-banner.blue i {
        color: #30B0C7;
    }

    .info-banner strong {
        color: #1a202c;
    }

    .info-banner div {
        font-size: 0.875rem;
        color: #4a5568;
        line-height: 1.5;
    }

    .reviews-list {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .review-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        transition: all 0.2s ease;
    }

    .review-card:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }

    .review-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1rem;
        padding-bottom: 1rem;
        border-bottom: 2px solid #e2e8f0;
    }

    .review-title-section {
        flex: 1;
    }

    .zahnarzt-info {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 0.5rem;
    }

    .zahnarzt-info i {
        font-size: 1.5rem;
        color: #009688;
    }

    .zahnarzt-info h3 {
        font-size: 1.25rem;
        font-weight: 600;
        color: #1a202c;
        margin: 0;
    }

    .review-meta {
        display: flex;
        align-items: center;
        gap: 1rem;
        flex-wrap: wrap;
    }

    .review-meta .date {
        display: flex;
        align-items: center;
        gap: 0.375rem;
        font-size: 0.875rem;
        color: #718096;
    }

    .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.375rem 0.75rem;
        border-radius: 6px;
        font-size: 0.75rem;
        font-weight: 600;
    }

    .status-published {
        background: #d1fae5;
        color: #065f46;
    }

    .status-pending {
        background: #fef3c7;
        color: #92400e;
    }

    .status-rejected {
        background: #fee2e2;
        color: #991b1b;
    }

    .rating-display {
        display: flex;
        gap: 0.25rem;
        font-size: 1.25rem;
        color: #fbbf24;
    }

    .review-content {
        margin-bottom: 1rem;
    }

    .review-text {
        font-size: 1rem;
        color: #4a5568;
        line-height: 1.6;
        margin: 0 0 1rem;
    }

    .ai-comment {
        background: #fef3c7;
        border-left: 4px solid #f59e0b;
        border-radius: 8px;
        padding: 1rem;
    }

    .ai-comment-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        font-weight: 600;
        color: #92400e;
        margin-bottom: 0.5rem;
    }

    .ai-comment p {
        font-size: 0.875rem;
        color: #78350f;
        margin: 0;
        line-height: 1.5;
    }

    .review-actions {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-top: 0.75rem;
        justify-content: flex-end;
    }

    .btn-icon {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 36px;
        height: 36px;
        padding: 0;
        border: none;
        border-radius: 8px;
        font-size: 1rem;
        cursor: pointer;
        transition: all 0.2s ease;
        background: transparent;
    }

    .btn-icon:hover {
        transform: translateY(-2px);
    }

    .btn-edit {
        color: #009688;
        background: rgba(0, 150, 136, 0.1);
    }

    .btn-edit:hover {
        background: rgba(0, 150, 136, 0.2);
        box-shadow: 0 4px 12px rgba(0, 150, 136, 0.2);
    }

    .btn-delete {
        color: #ef4444;
        background: rgba(239, 68, 68, 0.1);
    }

    .btn-delete:hover {
        background: rgba(239, 68, 68, 0.2);
        box-shadow: 0 4px 12px rgba(239, 68, 68, 0.2);
    }

    .btn {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.25rem;
        font-size: 0.875rem;
        font-weight: 600;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;
    }

    .btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .btn-primary {
        background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
        color: white;
        box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
    }

    .btn-primary:hover:not(:disabled) {
        box-shadow: 0 6px 16px rgba(0, 150, 136, 0.4);
        transform: translateY(-1px);
    }

    .btn-secondary {
        background: white;
        color: #4a5568;
        border: 2px solid #cbd5e0;
    }

    .btn-secondary:hover:not(:disabled) {
        background: #f7fafc;
        border-color: #a0aec0;
    }
    
    .btn-secondary.blue {
        color: #30B0C7;
        border-color: #30B0C7;
    }
    
    .btn-secondary.blue:hover:not(:disabled) {
        background: #E8F8FA;
        border-color: #268a9c;
    }

    .btn-sm {
        padding: 0.5rem 1rem;
        font-size: 0.875rem;
    }

    .pagination {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 2rem;
        padding: 1rem;
        background: white;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    
    .pagination .btn {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1.5rem;
        font-size: 0.9375rem;
        font-weight: 600;
        color: #4a5568;
        background: white;
        border: 2px solid #e2e8f0;
        border-radius: 8px;
        text-decoration: none;
        cursor: pointer;
        transition: all 0.2s ease;
    }
    
    .pagination .btn:hover:not(.disabled) {
        border-color: #009688;
        color: #009688;
        background: #f0fffe;
    }
    
    .pagination .btn.blue:hover:not(.disabled) {
        border-color: #30B0C7;
        color: #30B0C7;
        background: #E8F8FA;
    }
    
    .pagination .btn.disabled {
        opacity: 0.5;
        cursor: not-allowed;
        pointer-events: none;
    }

    .page-info {
        font-size: 0.875rem;
        color: #4a5568;
        font-weight: 500;
    }

    /* Moderation Actions */
    .moderation-actions {
        display: flex;
        gap: 0.75rem;
        margin-top: 1rem;
        padding-top: 1rem;
        border-top: 1px solid #e2e8f0;
    }

    @media (max-width: 768px) {
        .rezensionen-container {
            padding: 0.75rem;
        }

        .page-header {
            margin-bottom: 1rem;
        }

        .header-content-rezension {
            gap: 0.75rem;
        }

        .page-title {
            font-size: 1.25rem;
        }

        .page-subtitle {
            font-size: 0.875rem;
        }

        .header-icon {
            width: 40px;
            height: 40px;
            font-size: 1.25rem;
        }

        .info-banner {
            padding: 0.75rem;
            font-size: 0.875rem;
            gap: 0.75rem;
        }

        .info-banner i {
            font-size: 1rem;
        }

        .reviews-list {
            gap: 0.75rem;
        }

        .review-card {
            padding: 1rem;
        }

        .review-header {
            flex-direction: column;
            gap: 0.75rem;
            margin-bottom: 0.75rem;
        }

        .zahnarzt-info h3 {
            font-size: 1rem;
        }

        .zahnarzt-info i {
            font-size: 1.5rem;
        }

        .review-meta {
            gap: 0.5rem;
        }

        .date {
            font-size: 0.75rem;
        }

        .status-badge {
            padding: 0.25rem 0.5rem;
            font-size: 0.7rem;
        }

        .rating-display {
            align-self: flex-start;
            font-size: 1rem;
        }

        .review-text {
            font-size: 0.9rem;
            line-height: 1.5;
        }

        .ai-comment {
            padding: 0.75rem;
            margin-top: 0.75rem;
        }

        .ai-comment-header {
            font-size: 0.8rem;
        }

        .ai-comment p {
            font-size: 0.8rem;
        }

        .review-actions {
            margin-top: 0.5rem;
        }

        .btn-icon {
            width: 32px;
            height: 32px;
            font-size: 0.875rem;
        }
        
        .pagination {
            flex-direction: row;
            gap: 0.5rem;
            padding: 0.75rem;
            margin-top: 1rem;
        }

        .pagination .btn {
            flex: 0 0 auto;
            width: 70px;
            padding: 0.5rem;
            font-size: 0.75rem;
            border-width: 1px;
        }
        
        .pagination .btn i {
            font-size: 0.875rem;
            margin: 0;
        }
        
        .pagination .btn span {
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

        .empty-icon {
            width: 60px;
            height: 60px;
            font-size: 1.5rem;
        }

        .empty-state h2 {
            font-size: 1.125rem;
        }

        .empty-state p {
            font-size: 0.875rem;
        }
    }

    /* Dialog Styles */
    .dialog-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 1000;
        padding: 1rem;
        animation: fadeIn 0.2s ease;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
        }
        to {
            opacity: 1;
        }
    }

    .dialog-content {
        background: white;
        border-radius: 16px;
        max-width: 500px;
        width: 100%;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        animation: slideUp 0.3s ease;
    }

    @keyframes slideUp {
        from {
            opacity: 0;
            transform: translateY(20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .dialog-header {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1.5rem 2rem;
        border-bottom: 1px solid #e2e8f0;
    }

    .dialog-header i {
        font-size: 1.5rem;
    }

    .dialog-header h3 {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 700;
        color: #1a202c;
    }

    .dialog-body {
        padding: 2rem;
    }

    .dialog-body > p:first-child {
        margin: 0 0 1.5rem;
        color: #4a5568;
        font-size: 1rem;
    }

    .dialog-info {
        background: #f7fafc;
        border-radius: 8px;
        padding: 1rem;
        margin-bottom: 1.5rem;
    }

    .info-row {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0.5rem 0;
    }

    .info-row:not(:last-child) {
        border-bottom: 1px solid #e2e8f0;
    }

    .info-row strong {
        color: #2d3748;
        font-weight: 600;
    }

    .info-row span {
        color: #4a5568;
    }

    .warning-text {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 1rem;
        background: #fee2e2;
        color: #991b1b;
        border-radius: 8px;
        font-size: 0.875rem;
        font-weight: 500;
        margin: 0;
    }

    .warning-text i {
        font-size: 1.25rem;
    }

    .dialog-actions {
        display: flex;
        gap: 1rem;
        padding: 1.5rem 2rem;
        border-top: 1px solid #e2e8f0;
    }

    .dialog-btn {
        flex: 1;
        padding: 0.875rem 1.5rem;
        font-size: 1rem;
        font-weight: 600;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
    }

    .dialog-btn:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }

    .cancel-dialog-btn {
        background: white;
        color: #4a5568;
        border: 2px solid #cbd5e0;
    }

    .cancel-dialog-btn:hover:not(:disabled) {
        background: #f7fafc;
        border-color: #a0aec0;
    }

    .confirm-btn {
        color: white;
    }

    .delete-confirm-btn {
        background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
        box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);
    }

    .delete-confirm-btn:hover:not(:disabled) {
        box-shadow: 0 4px 16px rgba(220, 38, 38, 0.4);
        transform: translateY(-2px);
    }

    @media (max-width: 768px) {
        .dialog-content {
            margin: 1rem;
        }

        .dialog-header,
        .dialog-body,
        .dialog-actions {
            padding: 1rem;
        }

        .dialog-actions {
            flex-direction: column-reverse;
        }
    }</style>
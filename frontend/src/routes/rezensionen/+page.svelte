<script>
    import { goto } from "$app/navigation";
    import { enhance } from "$app/forms";
    import { ConfirmDialog, EmptyState, RezensionCard, Pagination } from "$lib";

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

                <RezensionCard
                    patientName={isZahnarzt
                        ? review.patientName || "Unbekannt"
                        : review.zahnarzt?.name || "Unbekannt"}
                    bewertung={review.bewertung}
                    text={review.text}
                    datum={review.datum}
                    verified={review.approved}
                    approved={review.approved}
                    {aiKommentar}
                    showActions={!isZahnarzt}
                    onEdit={() => goto(`/rezensionen/${review.id}/bearbeiten`)}
                    onDelete={() => confirmDelete(review)}
                    variant={isZahnarzt ? "blue" : "turquoise"}
                />
            {/each}
        </div>

        <!-- Pagination -->
        {#if nrOfPages > 1}
            <Pagination
                currentPage={currentPage}
                totalPages={nrOfPages}
                baseUrl="/rezensionen?pageNumber=PAGE_NUMBER&pageSize={pageSize}"
                variant={isZahnarzt ? 'blue' : 'turquoise'}
            />
        {/if}
    {/if}
</div>

<!-- Delete Confirmation Dialog -->
<ConfirmDialog
    show={showDeleteDialog && deleteReviewData}
    title="Rezension löschen"
    message="Möchten Sie diese Rezension wirklich löschen?"
    icon="trash"
    iconColor="#dc2626"
    confirmText="Rezension löschen"
    confirmStyle="background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%); box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);"
    isSubmitting={isDeleting}
    variant={isZahnarzt ? "blue" : "turquoise"}
    infoRows={deleteReviewData
        ? [
              {
                  label: "Zahnarzt",
                  value: deleteReviewData.zahnarzt?.name || "Unbekannt",
              },
              {
                  label: "Bewertung",
                  value: [1, 2, 3, 4, 5]
                      .map(
                          (star) =>
                              `<i class="bi ${deleteReviewData.bewertung >= star ? "bi-star-fill" : "bi-star"}" style="color: #fbbf24; font-size: 0.875rem;"></i>`,
                      )
                      .join(""),
                  html: true,
              },
              { label: "Datum", value: formatDate(deleteReviewData.datum) },
          ]
        : []}
    warningText="Diese Aktion kann nicht rückgängig gemacht werden."
    onClose={cancelDelete}
    useForm={true}
    formAction="?/deleteReview&id={deleteReviewId}"
    formEnhance={() => {
        isDeleting = true;
        return async ({ result, update }) => {
            await update();
            if (result.type === "success") {
                cancelDelete();
            }
        };
    }}
/>

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
        background: linear-gradient(135deg, #30b0c7 0%, #268a9c 100%);
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
        background: #e8f8fa;
        border-left-color: #30b0c7;
    }

    .info-banner i {
        font-size: 1.25rem;
        color: #009688;
        flex-shrink: 0;
        margin-top: 0.125rem;
    }

    .info-banner.blue i {
        color: #30b0c7;
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
</style>

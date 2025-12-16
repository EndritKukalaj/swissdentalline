<script>
  /**
   * RezensionCard - Wiederverwendbare Card für Patientenbewertungen
   */
  
  let { 
    patientName = 'Patient',
    bewertung,
    text,
    datum,
    verified = true,
    approved = true,
    aiKommentar = '',
    showActions = false,
    onEdit,
    onDelete,
    variant = 'turquoise'
  } = $props();
  
  function formatReviewDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('de-CH', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  }
  
  const status = $derived(() => {
    if (approved) {
      return { text: 'Veröffentlicht', class: 'status-published', icon: 'bi-check-circle-fill' };
    } else if (aiKommentar) {
      return { text: 'Abgelehnt', class: 'status-rejected', icon: 'bi-x-circle-fill' };
    } else {
      return { text: 'In Prüfung', class: 'status-pending', icon: 'bi-clock-history' };
    }
  });
</script>

<div class="patient-review-card {variant}-variant">
  <div class="review-header">
    <div class="patient-info">
      <div class="patient-avatar">
        <i class="bi bi-person-fill"></i>
      </div>
      <div class="patient-details">
        <h3 class="patient-name">{patientName}</h3>
        <div class="patient-meta">
          {#if verified && approved}
            <span class="verified-badge">
              <i class="bi bi-patch-check-fill"></i>
              Verifiziert
            </span>
          {/if}
          {#if !approved || aiKommentar}
            <span class="status-badge {status().class}">
              <i class="bi {status().icon}"></i>
              {status().text}
            </span>
          {/if}
        </div>
      </div>
    </div>
    
    <div class="review-stars">
      {#each [1, 2, 3, 4, 5] as star}
        <i class="bi {bewertung >= star ? 'bi-star-fill' : 'bi-star'}"></i>
      {/each}
    </div>
  </div>
  
  <div class="review-content">
    <p class="review-text">{text}</p>
    
    {#if aiKommentar}
      <div class="ai-comment">
        <div class="ai-comment-header">
          <i class="bi bi-robot"></i>
          <span>AI-Moderationshinweis</span>
        </div>
        <p>{aiKommentar}</p>
      </div>
    {/if}
  </div>
  
  <div class="review-footer">
    <span class="review-date">
      <i class="bi bi-calendar3"></i>
      {formatReviewDate(datum)}
    </span>
    
    {#if showActions}
      <div class="review-actions">
        {#if onEdit}
          <button
            class="btn-icon btn-edit"
            onclick={onEdit}
            title="Rezension bearbeiten"
          >
            <i class="bi bi-pencil-fill"></i>
          </button>
        {/if}
        {#if onDelete}
          <button
            type="button"
            class="btn-icon btn-delete"
            onclick={onDelete}
            title="Rezension löschen"
          >
            <i class="bi bi-trash-fill"></i>
          </button>
        {/if}
      </div>
    {/if}
  </div>
</div>

<style>
/* Patient Review Card Styles */
.patient-review-card {
    background: white;
    border-radius: 16px;
    padding: 1.5rem;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.patient-review-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.review-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
    padding-bottom: 1rem;
    border-bottom: 2px solid #e2e8f0;
}

.patient-info {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
    flex: 1;
}

.patient-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.patient-avatar i {
    font-size: 1.75rem;
    color: white;
}

.patient-details {
    display: flex;
    flex-direction: column;
    gap: 0.375rem;
}

.patient-name {
    margin: 0;
    font-size: 1.125rem;
    font-weight: 600;
    color: #1a202c;
}

.patient-meta {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    flex-wrap: wrap;
}

.verified-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.25rem;
    font-size: 0.75rem;
    font-weight: 500;
    color: #10b981;
}

.verified-badge i {
    font-size: 0.875rem;
}

.status-badge {
    display: inline-flex;
    align-items: center;
    gap: 0.375rem;
    padding: 0.25rem 0.625rem;
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

.review-stars {
    display: flex;
    gap: 0.25rem;
    flex-shrink: 0;
}

.review-stars i {
    font-size: 1.125rem;
    color: #f59e0b;
}

.review-stars i.bi-star {
    color: #e2e8f0;
}

.review-content {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

.review-text {
    margin: 0;
    font-size: 0.9375rem;
    line-height: 1.6;
    color: #4a5568;
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

.ai-comment-header i {
    font-size: 1rem;
}

.ai-comment p {
    font-size: 0.875rem;
    color: #78350f;
    margin: 0;
    line-height: 1.5;
}

.review-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 0.75rem;
    border-top: 1px solid #e2e8f0;
}

.review-date {
    display: flex;
    align-items: center;
    gap: 0.375rem;
    font-size: 0.875rem;
    color: #64748b;
}

.review-date i {
    font-size: 0.875rem;
    color: #94a3b8;
}

.review-actions {
    display: flex;
    align-items: center;
    gap: 0.5rem;
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

/* Turquoise Variant (Patient) */
.turquoise-variant .patient-avatar {
    background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
}

.turquoise-variant:hover {
    box-shadow: 0 8px 24px rgba(0, 150, 136, 0.12);
}

/* Blue Variant (Zahnarzt) */
.blue-variant .patient-avatar {
    background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
}

.blue-variant:hover {
    box-shadow: 0 8px 24px rgba(48, 176, 199, 0.12);
}

.blue-variant .btn-edit {
    color: #30B0C7;
    background: rgba(48, 176, 199, 0.1);
}

.blue-variant .btn-edit:hover {
    background: rgba(48, 176, 199, 0.2);
    box-shadow: 0 4px 12px rgba(48, 176, 199, 0.2);
}

/* Responsive */
@media (max-width: 768px) {
    .patient-review-card {
        padding: 1.25rem;
    }

    .review-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.75rem;
    }

    .patient-avatar {
        width: 45px;
        height: 45px;
    }

    .patient-avatar i {
        font-size: 1.5rem;
    }

    .patient-name {
        font-size: 1rem;
    }

    .review-stars {
        align-self: flex-start;
    }

    .review-stars i {
        font-size: 1rem;
    }

    .review-text {
        font-size: 0.875rem;
    }

    .ai-comment {
        padding: 0.75rem;
    }

    .ai-comment-header {
        font-size: 0.8125rem;
    }

    .ai-comment p {
        font-size: 0.8125rem;
    }

    .review-footer {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.75rem;
    }

    .review-actions {
        align-self: flex-end;
    }

    .btn-icon {
        width: 32px;
        height: 32px;
        font-size: 0.875rem;
    }
}

@media (max-width: 480px) {
    .patient-review-card {
        padding: 1rem;
        gap: 0.75rem;
    }

    .patient-avatar {
        width: 40px;
        height: 40px;
    }

    .patient-avatar i {
        font-size: 1.25rem;
    }

    .patient-name {
        font-size: 0.9375rem;
    }

    .verified-badge,
    .status-badge {
        font-size: 0.6875rem;
    }

    .review-stars i {
        font-size: 0.875rem;
    }

    .review-text {
        font-size: 0.8125rem;
    }

    .review-date {
        font-size: 0.8125rem;
    }
}
</style>

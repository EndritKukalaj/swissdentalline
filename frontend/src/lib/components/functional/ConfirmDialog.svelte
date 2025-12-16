<script>
  /**
   * ConfirmDialog - Wiederverwendbarer Bestätigungs-Dialog
   */
  
  let { 
    show = false,
    title = 'Bestätigung erforderlich',
    message = 'Möchten Sie fortfahren?',
    infoRows = [],
    warningText = '',
    errorMessage = '',
    icon = 'exclamation-triangle',
    iconColor = '#f44336',
    confirmText = 'Bestätigen',
    confirmStyle = '',
    cancelText = 'Abbrechen',
    isSubmitting = false,
    variant = 'turquoise',
    onClose,
    onConfirm,
    useForm = false,
    formAction = '',
    formEnhance,
    children
  } = $props();

  const handleOverlayClick = () => {
    if (!isSubmitting && onClose) {
      onClose();
    }
  };

  const handleConfirmClick = () => {
    if (onConfirm && !useForm) {
      onConfirm();
    }
  };
</script>

{#if show}
  <!-- svelte-ignore a11y_click_events_have_key_events -->
  <!-- svelte-ignore a11y_no_static_element_interactions -->
  <div class="dialog-overlay {variant}-variant" onclick={handleOverlayClick}>
    <div class="dialog-content" onclick={(e) => e.stopPropagation()}>
      <div class="dialog-header">
        <i class="bi bi-{icon}" style="color: {iconColor};"></i>
        <h3>{title}</h3>
      </div>
      
      <div class="dialog-body">
        <p>{message}</p>
        
        {#if infoRows.length > 0}
          <div class="dialog-info">
            {#each infoRows as row}
              <div class="info-row">
                <strong>{row.label}:</strong>
                {#if row.html}
                  <!-- svelte-ignore a11y_no_static_element_interactions -->
                  <!-- svelte-ignore a11y_click_events_have_key_events -->
                  <span onclick={(e) => e.stopPropagation()}>{@html row.value}</span>
                {:else}
                  <span>{row.value}</span>
                {/if}
              </div>
            {/each}
          </div>
        {/if}
        
        {#if warningText}
          <p class="warning-text">
            <i class="bi bi-info-circle"></i>
            {warningText}
          </p>
        {/if}
        
        {#if errorMessage}
          <div class="error-message">
            <i class="bi bi-exclamation-circle"></i>
            {errorMessage}
          </div>
        {/if}

        {#if children}
          {@render children()}
        {/if}
      </div>
      
      <div class="dialog-actions">
        <button 
          class="dialog-btn cancel-dialog-btn" 
          onclick={onClose}
          disabled={isSubmitting}
          type="button"
        >
          {cancelText}
        </button>
        
        {#if useForm}
          <form method="POST" action={formAction} use:enhance={formEnhance}>
            <button 
              type="submit" 
              class="dialog-btn confirm-btn"
              style={confirmStyle}
              disabled={isSubmitting}
            >
              {#if isSubmitting}
                <i class="bi bi-hourglass-split"></i>
                Wird verarbeitet...
              {:else}
                <i class="bi bi-check-circle"></i>
                {confirmText}
              {/if}
            </button>
          </form>
        {:else}
          <button 
            type="button"
            class="dialog-btn confirm-btn"
            style={confirmStyle}
            disabled={isSubmitting}
            onclick={handleConfirmClick}
          >
            {#if isSubmitting}
              <i class="bi bi-hourglass-split"></i>
              Wird verarbeitet...
            {:else}
              <i class="bi bi-check-circle"></i>
              {confirmText}
            {/if}
          </button>
        {/if}
      </div>
    </div>
  </div>
{/if}

<style>
/* Dialog Overlay */
.dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    backdrop-filter: blur(4px);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
    padding: 1rem;
    animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }
    to {
        opacity: 1;
    }
}

/* Dialog Content */
.dialog-content {
    background: white;
    border-radius: 12px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    max-width: 500px;
    width: 100%;
    animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
    from {
        transform: translateY(20px);
        opacity: 0;
    }
    to {
        transform: translateY(0);
        opacity: 1;
    }
}

/* Dialog Header */
.dialog-header {
    padding: 1.5rem;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    align-items: center;
    gap: 0.75rem;
}

.dialog-header i {
    font-size: 1.5rem;
}

.dialog-header h3 {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: #1a202c;
}

/* Dialog Body */
.dialog-body {
    padding: 1.5rem;
}

.dialog-body > p {
    margin: 0 0 1.5rem 0;
    color: #4a5568;
    font-size: 1rem;
    line-height: 1.5;
}

.dialog-body > p:last-child {
    margin-bottom: 0;
}

/* Dialog Info Section */
.dialog-info {
    background: #f7fafc;
    border-radius: 8px;
    padding: 1rem;
    margin-bottom: 1rem;
}

.info-row {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
    gap: 1rem;
}

.info-row:not(:last-child) {
    border-bottom: 1px solid #e2e8f0;
}

.info-row strong {
    color: #2d3748;
    font-weight: 600;
    flex-shrink: 0;
}

.info-row span {
    color: #4a5568;
    text-align: right;
    flex: 1;
}

/* Warning Text */
.warning-text {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem;
    background: #fff3cd;
    border-radius: 8px;
    color: #856404;
    font-size: 0.875rem;
    margin: 1rem 0 0 0;
}

.warning-text i {
    font-size: 1rem;
    flex-shrink: 0;
}

/* Error Message */
.error-message {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem;
    background: #fee;
    border-radius: 8px;
    color: #c53030;
    font-size: 0.875rem;
    margin-top: 1rem;
}

.error-message i {
    font-size: 1rem;
    flex-shrink: 0;
}

/* Dialog Actions */
.dialog-actions {
    padding: 1.5rem;
    border-top: 1px solid #e2e8f0;
    display: flex;
    gap: 1rem;
    justify-content: flex-end;
}

.dialog-actions form {
    display: contents;
}

/* Dialog Buttons */
.dialog-btn {
    padding: 0.75rem 1.5rem;
    border-radius: 8px;
    font-size: 0.875rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    border: none;
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
    background: linear-gradient(135deg, #f44336 0%, #e53935 100%);
    color: white;
    box-shadow: 0 2px 8px rgba(244, 67, 54, 0.3);
}

.confirm-btn:hover:not(:disabled) {
    box-shadow: 0 4px 12px rgba(244, 67, 54, 0.4);
    transform: translateY(-1px);
}

.dialog-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.dialog-btn i {
    font-size: 1rem;
}

/* Responsive */
@media (max-width: 768px) {
    .dialog-overlay {
        padding: 0;
        align-items: flex-end;
    }

    .dialog-content {
        border-radius: 16px 16px 0 0;
        max-width: 100%;
        animation: slideUpMobile 0.3s ease-out;
    }

    @keyframes slideUpMobile {
        from {
            transform: translateY(100%);
        }
        to {
            transform: translateY(0);
        }
    }

    .dialog-header,
    .dialog-body,
    .dialog-actions {
        padding: 1rem;
    }

    .dialog-actions {
        flex-direction: column-reverse;
    }

    .dialog-btn {
        width: 100%;
        justify-content: center;
    }

    .info-row {
        flex-direction: column;
        gap: 0.25rem;
    }

    .info-row span {
        text-align: left;
    }
}

@media (max-width: 480px) {
    .dialog-header h3 {
        font-size: 1.125rem;
    }

    .dialog-body > p {
        font-size: 0.9375rem;
    }

    .dialog-btn {
        padding: 0.625rem 1.25rem;
        font-size: 0.875rem;
    }
}
</style>

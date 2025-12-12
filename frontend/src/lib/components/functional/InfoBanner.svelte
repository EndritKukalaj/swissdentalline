<script>
  /**
   * InfoBanner - Wiederverwendbarer Benachrichtigungs-Banner
   */
  
  let { 
    type = 'info',
    message = '',
    title = '',
    icon = '',
    closable = false,
    show = $bindable(true),
    variant = 'turquoise',
    onClose,
    autoClose = false,
    autoCloseDelay = 5000,
    children
  } = $props();

  // Auto-Icon basierend auf Type
  const defaultIcons = {
    success: 'check-circle',
    error: 'exclamation-circle',
    warning: 'exclamation-triangle',
    info: 'info-circle',
    gift: 'gift-fill'
  };

  const iconName = $derived(icon || defaultIcons[type] || 'info-circle');

  // Auto-Close Timer
  let timeoutId;
  
  $effect(() => {
    if (show && autoClose) {
      timeoutId = setTimeout(() => {
        handleClose();
      }, autoCloseDelay);
    }
    
    return () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
    };
  });

  const handleClose = () => {
    show = false;
    if (onClose) {
      onClose();
    }
  };
</script>

{#if show}
  <div class="alert alert-{type} {variant}-variant" role="alert">
    <div class="alert-icon">
      <i class="bi bi-{iconName}"></i>
    </div>
    
    <div class="alert-content">
      {#if title}
        <h4 class="alert-title">{title}</h4>
      {/if}
      
      {#if message}
        <span class="alert-message">{message}</span>
      {/if}
      
      {#if children}
        {@render children()}
      {/if}
    </div>
    
    {#if closable}
      <button class="alert-close" onclick={handleClose} aria-label="Schließen">
        <i class="bi bi-x-lg"></i>
      </button>
    {/if}
  </div>
{/if}

<style>
/* Base Alert Styles */
.alert {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
    padding: 1rem 1.25rem;
    border-radius: 12px;
    margin-bottom: 1.5rem;
    animation: slideDown 0.3s ease-out;
    position: relative;
    border: 1px solid;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

@keyframes slideDown {
    from {
        transform: translateY(-10px);
        opacity: 0;
    }
    to {
        transform: translateY(0);
        opacity: 1;
    }
}

.alert-icon {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
}

.alert-icon i {
    font-size: 1.25rem;
}

.alert-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
}

.alert-title {
    margin: 0;
    font-size: 1rem;
    font-weight: 600;
    line-height: 1.4;
}

.alert-message {
    font-size: 0.9375rem;
    line-height: 1.5;
}

.alert-close {
    flex-shrink: 0;
    background: transparent;
    border: none;
    padding: 0.25rem;
    cursor: pointer;
    color: inherit;
    opacity: 0.6;
    transition: opacity 0.2s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    border-radius: 4px;
}

.alert-close:hover {
    opacity: 1;
    background: rgba(0, 0, 0, 0.05);
}

.alert-close i {
    font-size: 0.875rem;
}

/* Success Alert */
.alert-success {
    background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
    color: #155724;
    border-color: #c3e6cb;
}

.alert-success .alert-icon i {
    color: #28a745;
}

/* Error Alert */
.alert-error {
    background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
    color: #721c24;
    border-color: #f5c6cb;
}

.alert-error .alert-icon i {
    color: #dc3545;
}

/* Warning Alert */
.alert-warning {
    background: linear-gradient(135deg, #fff3cd 0%, #ffe69c 100%);
    color: #856404;
    border-color: #ffe69c;
}

.alert-warning .alert-icon i {
    color: #ffc107;
}

/* Info Alert */
.alert-info {
    background: linear-gradient(135deg, #d1ecf1 0%, #bee5eb 100%);
    color: #0c5460;
    border-color: #bee5eb;
}

.alert-info .alert-icon i {
    color: #17a2b8;
}

/* Gift/Special Banner */
.alert-gift {
    background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
    color: #2e7d32;
    border-color: #c8e6c9;
}

.alert-gift .alert-icon i {
    color: #4caf50;
    font-size: 1.5rem;
}

/* Turquoise Variant Overrides */
.turquoise-variant.alert-success .alert-icon i {
    color: #009688;
}

.turquoise-variant.alert-info {
    background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
    color: #006064;
    border-color: #b2ebf2;
}

.turquoise-variant.alert-info .alert-icon i {
    color: #00bcd4;
}

/* Blue Variant Overrides */
.blue-variant.alert-success .alert-icon i {
    color: #30B0C7;
}

.blue-variant.alert-info {
    background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
    color: #0d47a1;
    border-color: #bbdefb;
}

.blue-variant.alert-info .alert-icon i {
    color: #2196f3;
}

/* Responsive */
@media (max-width: 768px) {
    .alert {
        padding: 0.875rem 1rem;
        gap: 0.75rem;
    }

    .alert-icon i {
        font-size: 1.125rem;
    }

    .alert-title {
        font-size: 0.9375rem;
    }

    .alert-message {
        font-size: 0.875rem;
    }
}

@media (max-width: 480px) {
    .alert {
        padding: 0.75rem 0.875rem;
    }

    .alert-title {
        font-size: 0.875rem;
    }

    .alert-message {
        font-size: 0.8125rem;
    }
}
</style>

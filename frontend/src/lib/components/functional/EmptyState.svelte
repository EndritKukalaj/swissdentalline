<script>
  /**
   * EmptyState - Wiederverwendbarer Empty State
   */
  
  let { 
    icon = 'inbox',
    title = 'Keine Daten verfügbar',
    message = '',
    buttonText = '',
    onButtonClick,
    buttonHref = '',
    variant = 'turquoise',
    showButton,
    children
  } = $props();
  
  // Derived state: zeige Button wenn buttonText oder buttonHref gesetzt ist
  let shouldShowButton = $derived(showButton ?? !!(buttonText || buttonHref));
</script>

<div class="empty-state {variant}-variant">
  <div class="empty-icon">
    <i class="bi bi-{icon}"></i>
  </div>
  
  <h3 class="empty-title">{title}</h3>
  
  {#if message}
    <p class="empty-message">{message}</p>
  {/if}
  
  {#if children}
    {@render children()}
  {/if}
  
  {#if shouldShowButton}
    {#if buttonHref}
      <a href={buttonHref} class="btn btn-primary">
        {buttonText}
      </a>
    {:else if onButtonClick}
      <button class="btn btn-primary" onclick={onButtonClick}>
        {buttonText}
      </button>
    {/if}
  {/if}
</div>

<style>
/* Empty State Styles */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 4rem 2rem;
    text-align: center;
    background: white;
    border-radius: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    min-height: 400px;
}

.empty-icon {
    width: 120px;
    height: 120px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    margin-bottom: 1.5rem;
    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.empty-icon i {
    font-size: 4rem;
    color: #94a3b8;
}

.empty-title {
    margin: 0 0 0.75rem 0;
    font-size: 1.5rem;
    font-weight: 600;
    color: #1a202c;
}

.empty-message {
    margin: 0 0 2rem 0;
    font-size: 1rem;
    color: #64748b;
    max-width: 500px;
    line-height: 1.6;
}

.empty-state .btn {
    margin-top: 1rem;
}

/* Button Styles */
.btn {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.875rem 1.75rem;
    font-size: 1rem;
    font-weight: 600;
    text-decoration: none;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.btn-primary {
    color: white;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

/* Turquoise Variant */
.turquoise-variant .empty-icon {
    background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
}

.turquoise-variant .empty-icon i {
    color: #009688;
}

.turquoise-variant .btn-primary {
    background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
}

.turquoise-variant .btn-primary:hover {
    background: linear-gradient(135deg, #00796b 0%, #00a693 100%);
}

/* Blue Variant */
.blue-variant .empty-icon {
    background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
}

.blue-variant .empty-icon i {
    color: #30B0C7;
}

.blue-variant .btn-primary {
    background: linear-gradient(135deg, #30B0C7 0%, #268a9c 100%);
}

.blue-variant .btn-primary:hover {
    background: linear-gradient(135deg, #268a9c 0%, #1e6d7a 100%);
}

/* Responsive */
@media (max-width: 768px) {
    .empty-state {
        padding: 3rem 1.5rem;
        min-height: 350px;
    }

    .empty-icon {
        width: 100px;
        height: 100px;
    }

    .empty-icon i {
        font-size: 3rem;
    }

    .empty-title {
        font-size: 1.25rem;
    }

    .empty-message {
        font-size: 0.9375rem;
    }

    .btn {
        padding: 0.75rem 1.5rem;
        font-size: 0.9375rem;
    }
}

@media (max-width: 480px) {
    .empty-state {
        padding: 2rem 1rem;
        min-height: 300px;
    }

    .empty-icon {
        width: 80px;
        height: 80px;
        margin-bottom: 1rem;
    }

    .empty-icon i {
        font-size: 2.5rem;
    }

    .empty-title {
        font-size: 1.125rem;
    }

    .empty-message {
        font-size: 0.875rem;
        margin-bottom: 1.5rem;
    }
}
</style>

<script>
  /**
   * Pagination - Wiederverwendbare Pagination-Komponente
   */
  
  let { 
    currentPage,
    totalPages,
    baseUrl = '',
    onPageChange,
    variant = 'turquoise'
  } = $props();
  
  const isFirstPage = $derived(currentPage === 1);
  const isLastPage = $derived(currentPage >= totalPages);
</script>

<div class="pagination {variant}-variant">
  {#if onPageChange}
    <button 
      class="pagination-btn"
      class:disabled={isFirstPage}
      disabled={isFirstPage}
      onclick={() => onPageChange(currentPage - 1)}
    >
      <i class="bi bi-chevron-left"></i>
      <span>Zurück</span>
    </button>
  {:else}
    <a
      class="pagination-btn"
      class:disabled={isFirstPage}
      href={isFirstPage ? undefined : baseUrl.replace('PAGE_NUMBER', String(currentPage - 1))}
      aria-disabled={isFirstPage}
    >
      <i class="bi bi-chevron-left"></i>
      <span>Zurück</span>
    </a>
  {/if}
  
  <span class="page-info">
    Seite {currentPage} von {totalPages}
  </span>
  
  {#if onPageChange}
    <button 
      class="pagination-btn"
      class:disabled={isLastPage}
      disabled={isLastPage}
      onclick={() => onPageChange(currentPage + 1)}
    >
      <span>Weiter</span>
      <i class="bi bi-chevron-right"></i>
    </button>
  {:else}
    <a
      class="pagination-btn"
      class:disabled={isLastPage}
      href={isLastPage ? undefined : baseUrl.replace('PAGE_NUMBER', String(currentPage + 1))}
      aria-disabled={isLastPage}
    >
      <span>Weiter</span>
      <i class="bi bi-chevron-right"></i>
    </a>
  {/if}
</div>

<style>
/* Pagination Styles */
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

.pagination-btn {
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

.pagination-btn:hover:not(.disabled) {
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.pagination-btn.disabled {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: none;
}

.page-info {
    font-size: 0.875rem;
    color: #4a5568;
    font-weight: 500;
}

/* Turquoise Variant (Patient) */
.turquoise-variant .pagination-btn:hover:not(.disabled) {
    border-color: #009688;
    color: #009688;
    background: #f0fffe;
}

/* Blue Variant (Zahnarzt) */
.blue-variant .pagination-btn:hover:not(.disabled) {
    border-color: #30B0C7;
    color: #30B0C7;
    background: #E8F8FA;
}

/* Responsive */
@media (max-width: 768px) {
    .pagination {
        flex-direction: row;
        gap: 0.5rem;
        padding: 0.75rem;
        margin-top: 1rem;
    }

    .pagination-btn {
        flex: 0 0 auto;
        width: 70px;
        padding: 0.5rem;
        font-size: 0.75rem;
        border-width: 1px;
    }

    .pagination-btn i {
        font-size: 0.875rem;
        margin: 0;
    }

    .pagination-btn span {
        display: none;
    }

    .page-info {
        font-size: 0.75rem;
        white-space: nowrap;
        flex: 1;
        text-align: center;
    }
}

@media (max-width: 480px) {
    .pagination {
        padding: 0.625rem;
    }

    .pagination-btn {
        width: 60px;
        padding: 0.375rem;
    }

    .pagination-btn i {
        font-size: 0.75rem;
    }

    .page-info {
        font-size: 0.6875rem;
    }
}
</style>

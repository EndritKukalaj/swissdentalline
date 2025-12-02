<script>
    import { goto } from '$app/navigation';
    import TerminListe from '$lib/components/TerminListe.svelte';
    
    let { data } = $props();
    const role = data.userRole || 'Patient';
    const termine = data.termine || [];
    const stats = data.stats || {};
</script>

<svelte:head>
    <title>Termine - SwissDentalLine</title>
</svelte:head>

{#if role === 'Patient' || role === 'Zahnarzt'}
    <TerminListe {termine} {stats} role={role} />
{:else}
    <div class="empty-state">
        <i class="bi bi-exclamation-triangle"></i>
        <p>Bitte melden Sie sich an, um Ihre Termine zu sehen.</p>
        <button class="btn btn-primary" onclick={() => goto('/login')}>
            Anmelden
        </button>
    </div>
{/if}

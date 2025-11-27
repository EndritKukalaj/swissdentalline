<script>
    import PatientOverview from '$lib/components/PatientOverview.svelte';
    import ZahnarztOverview from '$lib/components/ZahnarztOverview.svelte';
    import HomeLanding from '$lib/components/HomeLanding.svelte';
    
    let { data } = $props();
    let { isAuthenticated, termine = [], nextTermin = null, stats = {}, userRole = null } = data;
</script>

{#if isAuthenticated}
    {#if userRole === 'Zahnarzt'}
        <ZahnarztOverview {termine} {nextTermin} {stats} />
    {:else}
        <PatientOverview {termine} {nextTermin} {stats} />
    {/if}
{:else}
    <HomeLanding />
{/if}
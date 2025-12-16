<script>
    import PatientOverview from '$lib/components/overviews/PatientOverview.svelte';
    import ZahnarztOverview from '$lib/components/overviews/ZahnarztOverview.svelte';
    import HomeLanding from '$lib/components/overviews/HomeLanding.svelte';
    
    let { data } = $props();
    let { isAuthenticated, nextTermin = null, stats = {}, userRole = null } = data;
</script>

{#if isAuthenticated}
    {#if userRole === 'Zahnarzt'}
        <ZahnarztOverview {nextTermin} {stats} />
    {:else}
        <PatientOverview {nextTermin} {stats} />
    {/if}
{:else}
    <HomeLanding />
{/if}
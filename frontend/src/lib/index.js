// place files you want to import through the `$lib` alias in this folder.

// Functional Components
export { default as BookingProgressBar } from './components/functional/BookingProgressBar.svelte';
export { default as ConfirmDialog } from './components/functional/ConfirmDialog.svelte';
export { default as EmptyState } from './components/functional/EmptyState.svelte';
export { default as InfoBanner } from './components/functional/InfoBanner.svelte';
export { default as Pagination } from './components/functional/Pagination.svelte';

// Card Components
export { default as BehandlungCard } from './components/cards/BehandlungCard.svelte';
export { default as FlexTerminCard } from './components/cards/FlexTerminCard.svelte';
export { default as KpiCard } from './components/cards/KPICard.svelte';
export { default as RezensionCard } from './components/cards/RezensionCard.svelte';
export { default as TerminSlotCard } from './components/cards/TerminSlotCard.svelte';

// Overview Components (not exported, used with direct imports)
// - HomeLanding
// - PatientOverview
// - ZahnarztOverview

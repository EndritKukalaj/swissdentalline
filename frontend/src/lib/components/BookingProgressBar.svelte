<script>
    let { currentStep = 1 } = $props();
    
    const steps = [
        { number: 1, label: 'Behandlung' },
        { number: 2, label: 'Termin' },
        { number: 3, label: 'Zahnarzt' },
        { number: 4, label: 'Warteliste' },
        { number: 5, label: 'Übersicht' }
    ];
</script>

<div class="progress-bar">
    {#each steps as step, index (step.number)}
        <div 
            class="progress-step" 
            class:completed={currentStep > step.number}
            class:active={currentStep === step.number}
        >
            <div class="step-number">
                {#if currentStep > step.number}
                    <i class="bi bi-check"></i>
                {:else}
                    {step.number}
                {/if}
            </div>
            <span class="step-label">{step.label}</span>
        </div>
        
        {#if index < steps.length - 1}
            <div class="progress-line" class:completed={currentStep > step.number}></div>
        {/if}
    {/each}
</div>

<style>
    .progress-bar {
        display: flex;
        flex-direction: row;
        align-items: center;
        justify-content: center;
        margin-bottom: 1.5rem;
        padding: 1rem;
        background: white;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        gap: 0;
    }

    .progress-step {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.5rem;
        position: relative;
        z-index: 1;
    }

    .step-number {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f0f0f0;
        color: #999;
        font-weight: 600;
        font-size: 1.1rem;
        transition: all 0.3s ease;
        border: 3px solid transparent;
    }

    .progress-step.active .step-number {
        background: linear-gradient(135deg, #009688 0%, #00bfa5 100%);
        color: white;
        border-color: #00bfa5;
        box-shadow: 0 4px 12px rgba(0, 150, 136, 0.3);
    }

    .progress-step.completed .step-number {
        background: #009688;
        color: white;
        border-color: #009688;
    }

    .step-label {
        font-size: 0.85rem;
        color: #999;
        font-weight: 500;
        white-space: nowrap;
        transition: color 0.3s ease;
    }

    .progress-step.active .step-label {
        color: #009688;
        font-weight: 600;
    }

    .progress-step.completed .step-label {
        color: #009688;
    }

    .progress-line {
        flex: 1;
        height: 3px;
        background: #e0e0e0;
        min-width: 40px;
        max-width: 80px;
        transition: background 0.3s ease;
    }

    .progress-line.completed {
        background: #009688;
    }

    @media (max-width: 768px) {
        .progress-bar {
            padding: 1.5rem 0.5rem;
            margin-bottom: 1rem;
        }

        .step-number {
            width: 40px;
            height: 40px;
            font-size: 1rem;
        }

        .step-label {
            font-size: 0.75rem;
        }

        .progress-line {
            max-width: 40px;
        }
    }

    @media (max-width: 480px) {
        .step-label {
            display: none;
        }

        .step-number {
            width: 36px;
            height: 36px;
            font-size: 0.9rem;
        }

        .progress-line {
            max-width: 30px;
        }
    }
</style>

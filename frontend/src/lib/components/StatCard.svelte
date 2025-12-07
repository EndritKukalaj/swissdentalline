<script>
    let { title, value, icon = 'bi-info-circle', variant = 'turquoise', onclick = null, clickable = false } = $props();
    
    // Icon color mapping based on variant and icon type
    const getIconColor = (iconClass, colorVariant) => {
        if (colorVariant === 'blue') {
            if (iconClass.includes('calendar')) return '#30B0C7';
            if (iconClass.includes('clock')) return '#FFC107';
            if (iconClass.includes('cash')) return '#268a9c';
            return '#30B0C7';
        }
        // Turquoise variant (Patient)
        if (iconClass.includes('calendar')) return '#009688';
        if (iconClass.includes('clock')) return '#FFC107';
        if (iconClass.includes('star')) return '#FFC107';
        if (iconClass.includes('lightning')) return '#009688';
        return '#009688';
    };
    
    const getGradientColor = (iconClass, colorVariant) => {
        if (colorVariant === 'blue') {
            if (iconClass.includes('calendar')) return 'linear-gradient(135deg, #D0F0F5 0%, #b9e7fa 100%)';
            if (iconClass.includes('clock')) return 'linear-gradient(135deg, #fff4d6 0%, #ffe699 100%)';
            if (iconClass.includes('cash')) return 'linear-gradient(135deg, #D0F0F5 0%, #b6e2f8 100%)';
            return 'linear-gradient(135deg, #D0F0F5 0%, #b9e7fa 100%)';
        }
        // Turquoise variant (Patient)
        if (iconClass.includes('calendar')) return 'linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%)';
        if (iconClass.includes('clock')) return 'linear-gradient(135deg, #fff4d6 0%, #ffe699 100%)';
        if (iconClass.includes('star')) return 'linear-gradient(135deg, #fff4d6 0%, #ffe699 100%)';
        if (iconClass.includes('lightning')) return 'linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%)';
        return 'linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%)';
    };
    
    const getAccentGradient = (colorVariant) => {
        return colorVariant === 'blue' 
            ? 'linear-gradient(90deg, #30B0C7 0%, #268a9c 100%)'
            : 'linear-gradient(90deg, #009688 0%, #30B0C7 100%)';
    };

    const handleClick = () => {
        if (clickable && onclick) {
            onclick();
        }
    };
</script>

<div class="stat-card {clickable ? 'clickable' : ''}" onclick={handleClick} role={clickable ? 'button' : undefined} >
    <div class="card-accent" style="background: {getAccentGradient(variant)}"></div>
    
    <div class="card-content">
        <!-- Icon Badge -->
        <div class="icon-badge" style="background: {getGradientColor(icon, variant)}">
            <i class="bi {icon}" style="color: {getIconColor(icon, variant)}"></i>
        </div>
        
        <!-- Main Content -->
        <div class="card-main">
            <h3 class="stat-title">{title}</h3>
            <div class="stat-value">{value}</div>
        </div>
    </div>
</div>

<style>
/* StatCard styles */
.stat-card {
    position: relative;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border-radius: 20px;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(0, 0, 0, 0.05);
    height: 100%;
}

.stat-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.stat-card.clickable {
    cursor: pointer;
}

.stat-card.clickable:active {
    transform: translateY(-2px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.card-accent {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
}

.card-content {
    display: flex;
    align-items: center;
    gap: 1.25rem;
    padding: 1.5rem;
    padding-top: 1.75rem;
}

.icon-badge {
    flex-shrink: 0;
    width: 70px;
    height: 70px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.icon-badge i {
    font-size: 2rem;
    font-weight: 600;
}

.card-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    text-align: center;
}

.stat-title {
    font-size: 1rem;
    font-weight: 600;
    color: #4a5568;
    margin: 0;
    line-height: 1.3;
}

.stat-value {
    font-size: 2.5rem;
    font-weight: 800;
    color: #1a202c;
    line-height: 1;
}

@media (max-width: 640px) {
    .stat-card {
        border-radius: 12px;
    }

    .card-accent {
        height: 3px;
    }

    .card-content {
        flex-direction: column;
        padding: 1rem 0.75rem;
        padding-top: 1.25rem;
        gap: 0.75rem;
    }

    .icon-badge {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .icon-badge i {
        font-size: 1.5rem;
    }

    .card-main {
        align-items: center;
        gap: 0.25rem;
    }

    .stat-title {
        font-size: 0.8125rem;
        line-height: 1.2;
    }

    .stat-value {
        font-size: 1.75rem;
    }
}
</style>
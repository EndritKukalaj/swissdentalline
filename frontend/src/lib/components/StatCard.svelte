<script>
    let { title, value, icon = 'bi-info-circle', variant = 'turquoise' } = $props();
    
    // Icon color mapping based on variant and icon type
    const getIconColor = (iconClass, colorVariant) => {
        if (colorVariant === 'purple') {
            if (iconClass.includes('calendar')) return '#9C27B0';
            if (iconClass.includes('clock')) return '#FFC107';
            if (iconClass.includes('cash')) return '#6A1B9A';
            return '#8E24AA';
        }
        if (iconClass.includes('calendar')) return '#009688'; // Turquoise
        if (iconClass.includes('clock')) return '#FFC107'; // Yellow
        return '#30B0C7'; // Accent Blue
    };
    
    const getGradientColor = (iconClass, colorVariant) => {
        if (colorVariant === 'purple') {
            if (iconClass.includes('calendar')) return 'linear-gradient(135deg, #E1BEE7 0%, #CE93D8 100%)';
            if (iconClass.includes('clock')) return 'linear-gradient(135deg, #fff4d6 0%, #ffe699 100%)';
            if (iconClass.includes('cash')) return 'linear-gradient(135deg, #E1BEE7 0%, #CE93D8 100%)';
            return 'linear-gradient(135deg, #E1BEE7 0%, #CE93D8 100%)';
        }
        if (iconClass.includes('calendar')) return 'linear-gradient(135deg, #d1f4f0 0%, #b8eee9 100%)';
        if (iconClass.includes('clock')) return 'linear-gradient(135deg, #fff4d6 0%, #ffe699 100%)';
        return 'linear-gradient(135deg, #d1e9f0 0%, #b8dde9 100%)';
    };
    
    const getAccentGradient = (colorVariant) => {
        return colorVariant === 'purple' 
            ? 'linear-gradient(90deg, #9C27B0 0%, #7B1FA2 100%)'
            : 'linear-gradient(90deg, #009688 0%, #30B0C7 100%)';
    };
</script>

<div class="stat-card">
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
    
    .card-accent {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, #009688 0%, #30B0C7 100%);
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
        .card-content {
            flex-direction: column;
            text-align: center;
        }
        
        .icon-badge {
            align-self: center;
        }
        
        .card-main {
            align-items: center;
        }
    }
</style>

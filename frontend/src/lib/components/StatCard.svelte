<script>
    import './StatCard.css';
    let { title, value, icon = 'bi-info-circle', variant = 'turquoise', onclick = null, clickable = false } = $props();
    
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

    const handleClick = () => {
        if (clickable && onclick) {
            onclick();
        }
    };
</script>

<div class="stat-card {clickable ? 'clickable' : ''}" onclick={handleClick} role={clickable ? 'button' : undefined} tabindex={clickable ? 0 : undefined}>
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


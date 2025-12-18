import { redirect } from '@sveltejs/kit';
import axios from 'axios';

const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8080';

export async function load({ locals, url }) {
  if (!locals.isAuthenticated) {
    throw redirect(302, '/login');
  }

  const userRole = locals.user.user_roles?.[0] || 'Patient';
  
  if (userRole !== 'Zahnarzt') {
    throw redirect(302, '/');
  }

  const jwt_token = locals.jwt_token;
  const auth0UserId = locals.user.sub;
  const userId = auth0UserId.replace('auth0|', '');

  // Get filter parameter (default: 'month')
  const filter = url.searchParams.get('filter') || 'month';

  try {
    // Lade Zahnarzt-Daten
    const zahnarztResponse = await axios.get(`${API_BASE_URL}/api/zahnaerzte/${userId}`, {
      headers: {
        'Authorization': `Bearer ${jwt_token}`,
        'Content-Type': 'application/json'
      }
    });

    const zahnarzt = zahnarztResponse.data;
    const zahnarztId = zahnarzt.id;

    // Lade alle Termine des Zahnarztes
    const termineResponse = await axios.get(`${API_BASE_URL}/api/termine`, {
      headers: { 'Authorization': `Bearer ${jwt_token}` }
    });

    const alleTermine = termineResponse.data.filter(t => t.zahnarztId === zahnarztId);

    // Lade Behandlungsarten
    const behandlungsartenResponse = await axios.get(`${API_BASE_URL}/api/behandlungsarten`, {
      headers: { 'Authorization': `Bearer ${jwt_token}` }
    });

    const behandlungsarten = behandlungsartenResponse.data;

    // Lade Rezensionen
    let rezensionen = [];
    try {
      const rezensionenResponse = await axios.get(`${API_BASE_URL}/api/rezensionen`, {
        headers: { 'Authorization': `Bearer ${jwt_token}` }
      });
      rezensionen = rezensionenResponse.data.filter(r => r.zahnarztId === zahnarztId);
    } catch (error) {
      console.error('Error loading reviews:', error);
    }

    // Berechne Statistiken basierend auf Filter
    const stats = calculateStats(alleTermine, behandlungsarten, rezensionen, filter);

    // Berechne Zeitraum für Anzeige
    const zeitraum = getZeitraumDisplay(filter);

    return {
      zahnarzt,
      stats,
      filter,
      zeitraum,
      userRole
    };
  } catch (error) {
    console.error('Error loading statistics:', error);
    throw redirect(302, '/');
  }
}

function calculateStats(termine, behandlungsarten, rezensionen, filter) {
  const now = new Date();
  let startDate;
  let endDate = new Date(now);
  endDate.setHours(23, 59, 59, 999);

  // Bestimme Zeitraum basierend auf Filter
  switch (filter) {
    case 'week':
      // Montag der aktuellen Woche
      startDate = new Date(now);
      const dayOfWeek = startDate.getDay();
      const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
      startDate.setDate(startDate.getDate() + diffToMonday);
      startDate.setHours(0, 0, 0, 0);
      // Sonntag der aktuellen Woche
      endDate = new Date(startDate);
      endDate.setDate(startDate.getDate() + 6);
      endDate.setHours(23, 59, 59, 999);
      break;
    case 'month':
      // Erster Tag des aktuellen Monats
      startDate = new Date(now.getFullYear(), now.getMonth(), 1);
      startDate.setHours(0, 0, 0, 0);
      // Letzter Tag des aktuellen Monats
      endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
      endDate.setHours(23, 59, 59, 999);
      break;
    case 'year':
      // 1. Januar des aktuellen Jahres
      startDate = new Date(now.getFullYear(), 0, 1);
      startDate.setHours(0, 0, 0, 0);
      // 31. Dezember des aktuellen Jahres
      endDate = new Date(now.getFullYear(), 11, 31);
      endDate.setHours(23, 59, 59, 999);
      break;
    case 'all':
      startDate = new Date(0); // Alle Zeit - von Beginn
      endDate = new Date(2100, 11, 31); // Bis weit in die Zukunft
      endDate.setHours(23, 59, 59, 999);
      break;
    default:
      startDate = new Date(now.getFullYear(), now.getMonth(), 1);
      startDate.setHours(0, 0, 0, 0);
      endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
      endDate.setHours(23, 59, 59, 999);
  }

  // Filtere Termine nach Zeitraum
  const filteredTermine = termine.filter(t => {
    const terminDate = new Date(t.datum);
    return terminDate >= startDate && terminDate <= endDate;
  });

  // Berechne Auslastung
  const gesamtSlots = termine.filter(t => {
    const tDate = new Date(t.datum);
    return tDate >= startDate && tDate <= endDate;
  }).length;
  const belegteSlots = filteredTermine.filter(t => 
    t.status === 'GEBUCHT' || t.status === 'ABGESCHLOSSEN'
  ).length;
  const auslastungProzent = gesamtSlots > 0 ? Math.round((belegteSlots / gesamtSlots) * 100) : 0;

  // Termine nach Status
  const termineGebucht = filteredTermine.filter(t => t.status === 'GEBUCHT').length;
  const termineFlex = filteredTermine.filter(t => t.status === 'FLEX').length;
  const termineAbgeschlossen = filteredTermine.filter(t => t.status === 'ABGESCHLOSSEN').length;
  const termineAbgesagt = filteredTermine.filter(t => t.status === 'ABGESAGT').length;
  const termineFrei = filteredTermine.filter(t => t.status === 'FREI').length;

  // Berechne Gesamteinnahmen (nur abgeschlossene Termine)
  const gesamtEinnahmen = filteredTermine
    .filter(t => t.status === 'ABGESCHLOSSEN')
    .reduce((sum, t) => sum + (t.preis || 0), 0);

  // Top Behandlungsarten
  const behandlungsCounts = {};
  filteredTermine
    .filter(t => t.status === 'ABGESCHLOSSEN' && t.behandlungsartId)
    .forEach(t => {
      const id = t.behandlungsartId;
      behandlungsCounts[id] = (behandlungsCounts[id] || 0) + 1;
    });

  const topBehandlungen = Object.entries(behandlungsCounts)
    .map(([id, count]) => {
      const behandlung = behandlungsarten.find(b => b.id === id);
      return {
        name: behandlung?.name || 'Unbekannt',
        count,
        id
      };
    })
    .sort((a, b) => b.count - a.count)
    .slice(0, 5);

  // Durchschnittliche Bewertung (nur approved Rezensionen)
  const approvedRezensionen = rezensionen.filter(r => r.approved === true);
  const durchschnittsBewertung = approvedRezensionen.length > 0
    ? (approvedRezensionen.reduce((sum, r) => sum + (r.bewertung || 0), 0) / approvedRezensionen.length).toFixed(1)
    : 0;

  // Einnahmen-Verlauf (letzte 12 Monate/Wochen/Tage je nach Filter)
  const einnahmenVerlauf = generateRevenueTimeline(termine, filter, startDate, endDate);

  // Termine-Verlauf
  const termineVerlauf = generateAppointmentsTimeline(termine, filter, startDate, endDate);

  return {
    auslastung: auslastungProzent,
    appointmentsByStatus: {
      gebucht: termineGebucht,
      flex: termineFlex,
      abgeschlossen: termineAbgeschlossen,
      abgesagt: termineAbgesagt,
      frei: termineFrei
    },
    gesamtEinnahmen,
    topBehandlungen,
    durchschnittsBewertung,
    anzahlRezensionen: approvedRezensionen.length,
    einnahmenVerlauf,
    termineVerlauf
  };
}

function generateRevenueTimeline(termine, filter, startDate, endDate) {
  const timeline = [];
  let periodType = 'month';
  let periods = 12;

  if (filter === 'week') {
    periodType = 'day';
    periods = 7;
  } else if (filter === 'year') {
    periodType = 'month';
    periods = 12;
  } else if (filter === 'month') {
    periodType = 'day';
    const daysInMonth = new Date(endDate.getFullYear(), endDate.getMonth() + 1, 0).getDate();
    periods = daysInMonth;
  } else if (filter === 'all') {
    periodType = 'month';
    periods = 24;
  }

  if (filter === 'week') {
    // 7 Tage der Woche (Montag bis Sonntag)
    for (let i = 0; i < 7; i++) {
      const dayStart = new Date(startDate);
      dayStart.setDate(startDate.getDate() + i);
      dayStart.setHours(0, 0, 0, 0);
      
      const dayEnd = new Date(dayStart);
      dayEnd.setHours(23, 59, 59, 999);

      const revenue = termine
        .filter(t => {
          const tDate = new Date(t.datum);
          return t.status === 'ABGESCHLOSSEN' && tDate >= dayStart && tDate <= dayEnd;
        })
        .reduce((sum, t) => sum + (t.preis || 0), 0);

      const label = dayStart.toLocaleDateString('de-CH', { weekday: 'short' });
      timeline.push({ label, value: revenue });
    }
  } else if (filter === 'month') {
    // Wochen des Monats (KW 1-5)
    const firstDay = new Date(startDate.getFullYear(), startDate.getMonth(), 1);
    const lastDay = new Date(endDate.getFullYear(), endDate.getMonth() + 1, 0);
    
    // Finde Montag der ersten Woche
    let weekStart = new Date(firstDay);
    const dayOfWeek = weekStart.getDay();
    const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    weekStart.setDate(weekStart.getDate() + diffToMonday);
    
    let weekNum = 1;
    while (weekStart <= lastDay) {
      const weekEnd = new Date(weekStart);
      weekEnd.setDate(weekStart.getDate() + 6);
      weekEnd.setHours(23, 59, 59, 999);
      
      const revenue = termine
        .filter(t => {
          const tDate = new Date(t.datum);
          return t.status === 'ABGESCHLOSSEN' && tDate >= weekStart && tDate <= weekEnd;
        })
        .reduce((sum, t) => sum + (t.preis || 0), 0);

      // Berechne Start- und Enddatum innerhalb des Monats
      const startDay = weekStart.getDate() > 0 ? Math.max(1, weekStart.getDate()) : 1;
      const endDay = weekEnd > lastDay ? lastDay.getDate() : Math.min(weekEnd.getDate(), lastDay.getDate());
      const label = `${startDay}.-${endDay}.`;
      timeline.push({ label, value: revenue });
      
      weekNum++;
      weekStart.setDate(weekStart.getDate() + 7);
      weekStart.setHours(0, 0, 0, 0);
    }
  } else if (filter === 'year') {
    // 12 Monate des Jahres
    for (let i = 0; i < 12; i++) {
      const monthStart = new Date(startDate.getFullYear(), i, 1);
      monthStart.setHours(0, 0, 0, 0);
      
      const monthEnd = new Date(startDate.getFullYear(), i + 1, 0);
      monthEnd.setHours(23, 59, 59, 999);

      const revenue = termine
        .filter(t => {
          const tDate = new Date(t.datum);
          return t.status === 'ABGESCHLOSSEN' && tDate >= monthStart && tDate <= monthEnd;
        })
        .reduce((sum, t) => sum + (t.preis || 0), 0);

      const label = monthStart.toLocaleDateString('de-CH', { month: 'short' });
      timeline.push({ label, value: revenue });
    }
  } else {
    // Gesamt: Quartale aller Jahre mit Terminen
    const allYears = new Set();
    termine.forEach(t => {
      const year = new Date(t.datum).getFullYear();
      allYears.add(year);
    });
    
    const sortedYears = Array.from(allYears).sort((a, b) => a - b);
    
    sortedYears.forEach(year => {
      for (let q = 1; q <= 4; q++) {
        const quarterStart = new Date(year, (q - 1) * 3, 1);
        quarterStart.setHours(0, 0, 0, 0);
        
        const quarterEnd = new Date(year, q * 3, 0);
        quarterEnd.setHours(23, 59, 59, 999);
        
        const revenue = termine
          .filter(t => {
            const tDate = new Date(t.datum);
            return t.status === 'ABGESCHLOSSEN' && tDate >= quarterStart && tDate <= quarterEnd;
          })
          .reduce((sum, t) => sum + (t.preis || 0), 0);

        const label = `Q${q} '${year.toString().slice(-2)}`;
        timeline.push({ label, value: revenue });
      }
    });
  }

  return timeline;
}

function generateAppointmentsTimeline(termine, filter, startDate, endDate) {
  const timeline = [];

  if (filter === 'week') {
    // 7 Tage der Woche (Montag bis Sonntag)
    for (let i = 0; i < 7; i++) {
      const dayStart = new Date(startDate);
      dayStart.setDate(startDate.getDate() + i);
      dayStart.setHours(0, 0, 0, 0);
      
      const dayEnd = new Date(dayStart);
      dayEnd.setHours(23, 59, 59, 999);

      const count = termine
        .filter(t => {
          const tDate = new Date(t.datum);
          return (t.status === 'GEBUCHT' || t.status === 'FLEX' || t.status === 'ABGESCHLOSSEN') 
            && tDate >= dayStart && tDate <= dayEnd;
        }).length;

      const label = dayStart.toLocaleDateString('de-CH', { weekday: 'short' });
      timeline.push({ label, value: count });
    }
  } else if (filter === 'month') {
    // Wochen des Monats (KW 1-5)
    const firstDay = new Date(startDate.getFullYear(), startDate.getMonth(), 1);
    const lastDay = new Date(endDate.getFullYear(), endDate.getMonth() + 1, 0);
    
    // Finde Montag der ersten Woche
    let weekStart = new Date(firstDay);
    const dayOfWeek = weekStart.getDay();
    const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
    weekStart.setDate(weekStart.getDate() + diffToMonday);
    
    let weekNum = 1;
    while (weekStart <= lastDay) {
      const weekEnd = new Date(weekStart);
      weekEnd.setDate(weekStart.getDate() + 6);
      weekEnd.setHours(23, 59, 59, 999);
      
      const count = termine
        .filter(t => {
          const tDate = new Date(t.datum);
          return (t.status === 'GEBUCHT' || t.status === 'FLEX' || t.status === 'ABGESCHLOSSEN') 
            && tDate >= weekStart && tDate <= weekEnd;
        }).length;

      // Berechne Start- und Enddatum innerhalb des Monats
      const startDay = weekStart.getDate() > 0 ? Math.max(1, weekStart.getDate()) : 1;
      const endDay = weekEnd > lastDay ? lastDay.getDate() : Math.min(weekEnd.getDate(), lastDay.getDate());
      const label = `${startDay}.-${endDay}.`;
      timeline.push({ label, value: count });
      
      weekNum++;
      weekStart.setDate(weekStart.getDate() + 7);
      weekStart.setHours(0, 0, 0, 0);
    }
  } else if (filter === 'year') {
    // 12 Monate des Jahres
    for (let i = 0; i < 12; i++) {
      const monthStart = new Date(startDate.getFullYear(), i, 1);
      monthStart.setHours(0, 0, 0, 0);
      
      const monthEnd = new Date(startDate.getFullYear(), i + 1, 0);
      monthEnd.setHours(23, 59, 59, 999);

      const count = termine
        .filter(t => {
          const tDate = new Date(t.datum);
          return (t.status === 'GEBUCHT' || t.status === 'FLEX' || t.status === 'ABGESCHLOSSEN') 
            && tDate >= monthStart && tDate <= monthEnd;
        }).length;

      const label = monthStart.toLocaleDateString('de-CH', { month: 'short' });
      timeline.push({ label, value: count });
    }
  } else {
    // Gesamt: Quartale aller Jahre mit Terminen
    const allYears = new Set();
    termine.forEach(t => {
      const year = new Date(t.datum).getFullYear();
      allYears.add(year);
    });
    
    const sortedYears = Array.from(allYears).sort((a, b) => a - b);
    
    sortedYears.forEach(year => {
      for (let q = 1; q <= 4; q++) {
        const quarterStart = new Date(year, (q - 1) * 3, 1);
        quarterStart.setHours(0, 0, 0, 0);
        
        const quarterEnd = new Date(year, q * 3, 0);
        quarterEnd.setHours(23, 59, 59, 999);
        
        const count = termine
          .filter(t => {
            const tDate = new Date(t.datum);
            return (t.status === 'GEBUCHT' || t.status === 'FLEX' || t.status === 'ABGESCHLOSSEN') 
              && tDate >= quarterStart && tDate <= quarterEnd;
          }).length;

        const label = `Q${q} '${year.toString().slice(-2)}`;
        timeline.push({ label, value: count });
      }
    });
  }

  return timeline;
}

function getZeitraumDisplay(filter) {
  const now = new Date();
  let startDate;
  let endDate;

  switch (filter) {
    case 'week':
      // Montag der aktuellen Woche
      startDate = new Date(now);
      const dayOfWeek = startDate.getDay();
      const diffToMonday = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;
      startDate.setDate(startDate.getDate() + diffToMonday);
      // Sonntag der aktuellen Woche
      endDate = new Date(startDate);
      endDate.setDate(startDate.getDate() + 6);
      break;
    case 'month':
      // Erster Tag des aktuellen Monats
      startDate = new Date(now.getFullYear(), now.getMonth(), 1);
      // Letzter Tag des aktuellen Monats
      endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
      break;
    case 'year':
      // 1. Januar des aktuellen Jahres
      startDate = new Date(now.getFullYear(), 0, 1);
      // 31. Dezember des aktuellen Jahres
      endDate = new Date(now.getFullYear(), 11, 31);
      break;
    case 'all':
      return {
        von: 'Beginn',
        bis: 'Zukunft'
      };
    default:
      startDate = new Date(now.getFullYear(), now.getMonth(), 1);
      endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
  }

  return {
    von: startDate.toLocaleDateString('de-CH', { day: '2-digit', month: '2-digit', year: 'numeric' }),
    bis: endDate.toLocaleDateString('de-CH', { day: '2-digit', month: '2-digit', year: 'numeric' })
  };
}

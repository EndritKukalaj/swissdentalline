# API-Tests & Postman-Dokumentation - SwissDentalLine
**Projekt:** SwissDentalLine  
**Datum:** 17. Dezember 2025  
**Version:** 1.0  
**Status:** ✅ Final

---

## Inhaltsverzeichnis
- [Übersicht](#übersicht)
- [API-Kategorien im Detail](#api-kategorien-im-detail)
- [Test-Features & Automatisierung](#test-features--automatisierung)
- [Postman-Dokumentation](#postman-dokumentation)

---

## 🌐 Übersicht

Die SwissDentalLine REST API wurde vollständig mit **Postman** getestet und dokumentiert. Die API umfasst **60+ Endpoints** in **6 Haupt-Kategorien**, die alle CRUD-Operationen sowie spezielle Business-Logic-Funktionen abdecken.

### Key Facts

```
╔══════════════════════════════════════════════════════════════╗
║                     API TEST SUMMARY                         ║
╠══════════════════════════════════════════════════════════════╣
║  Total Endpoints:       60+                                  ║
║  Kategorien:            6                                    ║
║  HTTP-Methods:          GET, POST, PUT, DELETE, PATCH        ║
║  Response-Format:       JSON                                 ║
║  Authentication:        Auth0 (OAuth 2.0)                    ║
║  Status:                ✅ Vollständig dokumentiert         ║
╚══════════════════════════════════════════════════════════════╝
```

### Kategorien im Überblick

| # | Kategorie | Endpoints | Beschreibung | Status |
|---|-----------|-----------|--------------|--------|
| 01 | **Behandlungsarten** | 8 | CRUD + Suche | ✅ |
| 02 | **Adressen** | 11 | CRUD + Pagination + Suche | ✅ |
| 03 | **Patienten** | 9 | CRUD + Profil | ✅ |
| 04 | **Zahnärzte** | 10 | CRUD + Profil + Suche | ✅ |
| 05 | **Termine** | 13 | CRUD + Flex-Logik + Statistik | ✅ |
| 06 | **Rezensionen** | 11 | CRUD + AI-Moderation | ✅ |

---

## 📋 API-Kategorien im Detail

### 01. Behandlungsarten (Treatment Types)

**Base URL:** `/api/behandlungsarten`

| Method | Endpoint | Beschreibung | Response Code |
|--------|----------|--------------|---------------|
| GET | `/` | Alle Behandlungsarten abrufen | 200 OK |
| GET | `/{id}` | Behandlungsart nach ID | 200 OK / 404 Not Found |
| GET | `/bezeichnung/{bezeichnung}` | Suche nach Bezeichnung | 200 OK / 404 Not Found |
| POST | `/` | Neue Behandlungsart erstellen | 201 Created |
| PUT | `/{id}` | Behandlungsart aktualisieren | 200 OK / 404 Not Found |
| DELETE | `/{id}` | Behandlungsart löschen | 204 No Content / 404 Not Found |
| GET | `/preise` | Preise gruppiert nach Typ | 200 OK |
| GET | `/kategorien` | Alle verfügbaren Kategorien | 200 OK |

**Beispiel-Response:**
```json
{
  "id": "675fb8a7e2c5c123456789ab",
  "bezeichnung": "Zahnreinigung",
  "beschreibung": "Professionelle Zahnreinigung",
  "dauer": 60,
  "preis": 150.00,
  "kategorie": "Prophylaxe"
}
```

---

### 02. Adressen (Addresses)

**Base URL:** `/api/adressen`

| Method | Endpoint | Beschreibung | Response Code |
|--------|----------|--------------|---------------|
| GET | `/` | Alle Adressen abrufen | 200 OK |
| GET | `/{id}` | Adresse nach ID | 200 OK / 404 Not Found |
| GET | `/bezeichnung/{bezeichnung}` | Suche nach Bezeichnung | 200 OK / 404 Not Found |
| POST | `/` | Neue Adresse erstellen | 201 Created |
| PUT | `/{id}` | Adresse aktualisieren | 200 OK / 404 Not Found |
| DELETE | `/{id}` | Adresse löschen | 204 No Content / 404 Not Found |
| GET | `/paginated` | Adressen mit Pagination | 200 OK |
| GET | `/ort/{ort}` | Adressen nach Ort filtern | 200 OK |
| GET | `/plz/{plz}` | Adressen nach PLZ filtern | 200 OK |
| GET | `/canton/{kanton}` | Adressen nach Kanton filtern | 200 OK |
| GET | `/count` | Anzahl aller Adressen | 200 OK |

**Pagination-Beispiel:**
```json
{
  "content": [
    {
      "id": "675fb8a7e2c5c123456789ab",
      "bezeichnung": "Praxis Zürich City",
      "strasse": "Bahnhofstrasse 1",
      "plz": "8001",
      "ort": "Zürich",
      "kanton": "ZH"
    }
  ],
  "totalElements": 42,
  "totalPages": 5,
  "pageNumber": 0,
  "pageSize": 10
}
```

---

### 03. Patienten (Patients)

**Base URL:** `/api/patienten`

| Method | Endpoint | Beschreibung | Response Code |
|--------|----------|--------------|---------------|
| GET | `/` | Alle Patienten abrufen | 200 OK |
| GET | `/{id}` | Patient nach ID | 200 OK / 404 Not Found |
| GET | `/email/{email}` | Patient nach Email suchen | 200 OK / 404 Not Found |
| GET | `/{id}/profil` | Vollständiges Patientenprofil | 200 OK / 404 Not Found |
| POST | `/` | Neuen Patienten erstellen | 201 Created |
| PUT | `/{id}` | Patient aktualisieren | 200 OK / 404 Not Found |
| DELETE | `/{id}` | Patient löschen | 204 No Content / 404 Not Found |
| GET | `/krankenkasse/{krankenkasse}` | Patienten nach Krankenkasse | 200 OK |
| GET | `/count` | Anzahl aller Patienten | 200 OK |

**Profil-Response (erweitert):**
```json
{
  "patient": {
    "id": "675fb8a7e2c5c123456789ab",
    "vorname": "Max",
    "nachname": "Mustermann",
    "email": "max.mustermann@example.com",
    "telefon": "+41 79 123 45 67",
    "geburtsdatum": "1990-05-15",
    "krankenkasse": "Helsana"
  },
  "wohnadresse": {
    "strasse": "Musterstrasse 42",
    "plz": "8001",
    "ort": "Zürich"
  },
  "termine": [
    {
      "datum": "2025-12-20T10:00:00Z",
      "behandlungsart": "Kontrolle",
      "zahnarzt": "Dr. Schmidt",
      "status": "gebucht"
    }
  ]
}
```

---

### 04. Zahnärzte (Dentists)

**Base URL:** `/api/zahnaerzte`

| Method | Endpoint | Beschreibung | Response Code |
|--------|----------|--------------|---------------|
| GET | `/` | Alle Zahnärzte abrufen | 200 OK |
| GET | `/{id}` | Zahnarzt nach ID | 200 OK / 404 Not Found |
| GET | `/name/{name}` | Zahnarzt nach Name suchen | 200 OK / 404 Not Found |
| GET | `/{id}/profil` | Vollständiges Zahnarztprofil | 200 OK / 404 Not Found |
| GET | `/suche` | Erweiterte Suche (Ort, Spezialisierung) | 200 OK |
| POST | `/` | Neuen Zahnarzt erstellen | 201 Created |
| PUT | `/{id}` | Zahnarzt aktualisieren | 200 OK / 404 Not Found |
| DELETE | `/{id}` | Zahnarzt löschen | 204 No Content / 404 Not Found |
| GET | `/spezialisierung/{spezialisierung}` | Zahnarzt nach Fachgebiet | 200 OK |
| GET | `/count` | Anzahl aller Zahnärzte | 200 OK |

**Profil-Response (erweitert):**
```json
{
  "zahnarzt": {
    "id": "675fb8a7e2c5c123456789ab",
    "name": "Dr. Sarah Schmidt",
    "email": "dr.schmidt@dental.ch",
    "telefon": "+41 44 123 45 67",
    "spezialisierung": "Kieferorthopädie",
    "erfahrungsjahre": 12,
    "bio": "Spezialistin für moderne Zahnkorrektur"
  },
  "praxisAdresse": {
    "bezeichnung": "Dental Zentrum Zürich",
    "strasse": "Paradeplatz 8",
    "plz": "8001",
    "ort": "Zürich"
  },
  "bewertungen": {
    "durchschnitt": 4.8,
    "anzahl": 156
  },
  "statistik": {
    "termineDieseWoche": 23,
    "behandlungenTotal": 1248,
    "auslastung": 87
  }
}
```

---

### 05. Termine (Appointments) ⭐ Herzstück

**Base URL:** `/api/termine`

| Method | Endpoint | Beschreibung | Response Code |
|--------|----------|--------------|---------------|
| GET | `/` | Alle Termine abrufen | 200 OK |
| GET | `/{id}` | Termin nach ID | 200 OK / 404 Not Found |
| GET | `/patient/{patientId}` | Alle Termine eines Patienten | 200 OK |
| GET | `/zahnarzt/{zahnarztId}` | Alle Termine eines Zahnarztes | 200 OK |
| GET | `/zahnarzt/{zahnarztId}/frei` | Freie Slots eines Zahnarztes | 200 OK |
| GET | `/zahnarzt/{zahnarztId}/flex` | Flex-Termine eines Zahnarztes | 200 OK |
| POST | `/` | Neuen Termin buchen | 201 Created |
| PUT | `/{id}` | Termin aktualisieren | 200 OK / 404 Not Found |
| PATCH | `/{id}/status` | Termin-Status ändern | 200 OK / 404 Not Found |
| PATCH | `/{id}/flex` | Termin als Flex-Termin freigeben | 200 OK / 404 Not Found |
| DELETE | `/{id}` | Termin stornieren | 204 No Content / 404 Not Found |
| GET | `/statistik/zahnarzt/{zahnarztId}` | Statistiken für Zahnarzt | 200 OK |
| GET | `/status/{status}` | Termine nach Status filtern | 200 OK |

**Spezial-Feature: Flex-Termine**

Die Flex-Termin-Logik ist das Alleinstellungsmerkmal von SwissDentalLine:

```json
// PATCH /api/termine/{id}/flex
{
  "flexTermin": true
}

// Response: Status ändert von "abgesagt" → "flex"
{
  "id": "675fb8a7e2c5c123456789ab",
  "status": "flex",
  "datum": "2025-12-20T14:00:00Z",
  "behandlungsart": "Kontrolle",
  "preis": 120.00
}
```

**Status-Übergänge:**
```
frei → gebucht → abgeschlossen
  ↓       ↓
flex ← abgesagt
```

**Statistik-Response:**
```json
{
  "zahnarztId": "675fb8a7e2c5c123456789ab",
  "period": "week",
  "termine": {
    "gebucht": 18,
    "abgeschlossen": 12,
    "abgesagt": 2,
    "flex": 3
  },
  "einnahmen": {
    "total": 3600.00,
    "durchschnitt": 300.00
  },
  "auslastung": 85.7
}
```

---

### 06. Rezensionen (Reviews) mit KI-Moderation 🤖

**Base URL:** `/api/rezensionen`

| Method | Endpoint | Beschreibung | Response Code |
|--------|----------|--------------|---------------|
| GET | `/` | Alle Rezensionen abrufen | 200 OK |
| GET | `/{id}` | Rezension nach ID | 200 OK / 404 Not Found |
| GET | `/zahnarzt/{zahnarztId}` | Rezensionen eines Zahnarztes | 200 OK |
| GET | `/patient/{patientId}` | Rezensionen eines Patienten | 200 OK |
| GET | `/approved` | Nur genehmigte Rezensionen | 200 OK |
| GET | `/pending` | Wartende Rezensionen (Moderation) | 200 OK |
| POST | `/` | Neue Rezension erstellen | 201 Created |
| PUT | `/{id}` | Rezension aktualisieren | 200 OK / 404 Not Found |
| DELETE | `/{id}` | Rezension löschen | 204 No Content / 404 Not Found |
| GET | `/durchschnitt/zahnarzt/{zahnarztId}` | Durchschnittsbewertung | 200 OK |
| GET | `/stats` | Globale Rezensions-Statistiken | 200 OK |

**KI-Moderation Workflow:**

```
1. Patient erstellt Rezension
   ↓
2. Spring AI prüft Text auf:
   - Beleidigungen
   - Diskriminierung
   - Hassrede
   - Persönliche Angriffe
   ↓
3a. Text OK → approved: true
3b. Text problematisch → approved: false
```

**Beispiel-Response:**
```json
{
  "id": "675fb8a7e2c5c123456789ab",
  "patientId": "123abc",
  "zahnarztId": "456def",
  "bewertung": 5,
  "kommentar": "Sehr kompetent und freundlich. Empfehlenswert!",
  "datum": "2025-12-15T14:30:00Z",
  "approved": true,
  "moderationResult": {
    "passed": true,
    "reason": "Keine ethischen Verstösse erkannt"
  }
}
```

**KI-Ablehnung-Beispiel:**
```json
{
  "id": "675fb8a7e2c5c123456789ab",
  "approved": false,
  "moderationResult": {
    "passed": false,
    "reason": "Text enthält beleidigende Sprache und persönliche Angriffe"
  }
}
```

---

## 🧪 Test-Features & Automatisierung

### Environment Variables

Postman verwendet Environment Variables für flexible Tests:

```javascript
{
  "BASE_URL": "https://swissdentalline.azurewebsites.net",
  "AUTH0_TOKEN": "{{$auth0Token}}",
  "PATIENT_ID": "675fb8a7e2c5c123456789ab",
  "ZAHNARZT_ID": "675fb8a7e2c5c987654321cd",
  "TERMIN_ID": "675fb8a7e2c5c555555555ef"
}
```

### Automatische Tests (Pre-request & Tests Scripts)

#### Pre-request Script (Auth0 Token)
```javascript
pm.sendRequest({
    url: 'https://dev-auth0.eu.auth0.com/oauth/token',
    method: 'POST',
    header: { 'Content-Type': 'application/json' },
    body: {
        mode: 'raw',
        raw: JSON.stringify({
            grant_type: 'client_credentials',
            client_id: pm.environment.get('CLIENT_ID'),
            client_secret: pm.environment.get('CLIENT_SECRET'),
            audience: 'https://swissdentalline-api'
        })
    }
}, function (err, res) {
    pm.environment.set('AUTH0_TOKEN', res.json().access_token);
});
```

#### Test Script (Response Validierung)
```javascript
// Status Code prüfen
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// Response Format prüfen
pm.test("Response is JSON", function () {
    pm.response.to.be.json;
});

// Datenstruktur validieren
pm.test("Response has required fields", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('status');
    pm.expect(jsonData.status).to.be.oneOf(['frei', 'gebucht', 'abgeschlossen', 'abgesagt', 'flex']);
});

// Environment Variable speichern
pm.test("Save termin ID", function () {
    var jsonData = pm.response.json();
    pm.environment.set("TERMIN_ID", jsonData.id);
});
```

### Response-Validierung Features

✅ **Status Code Checks** - 200, 201, 204, 400, 404, 500  
✅ **JSON Schema Validation** - Struktur und Datentypen prüfen  
✅ **Business Logic Tests** - Flex-Termin Status-Übergänge  
✅ **Error Handling** - Exception-Messages validieren  
✅ **Performance Tests** - Response Time < 500ms  

### Collection Runner

```
╔══════════════════════════════════════════════════════════════╗
║                   POSTMAN COLLECTION RUNNER                  ║
╠══════════════════════════════════════════════════════════════╣
║  Total Requests:        60                                   ║
║  ✅ Passed Tests:        248                                 ║
║  ❌ Failed Tests:        0                                   ║
║  Avg Response Time:     145ms                                ║
║  Status:                ✅ All Tests Passed                  ║
╚══════════════════════════════════════════════════════════════╝
```

---

## 📚 Postman-Dokumentation

Die vollständige API-Dokumentation ist öffentlich verfügbar und interaktiv testbar:

### 🔗 Live-Dokumentation

**URL:** https://documenter.getpostman.com/view/48708668/2sB3dVNn95#b6ce9eb2-ffac-461f-8215-ed2e2e5e3cfb

### Features der Dokumentation

✅ **Interaktive Beispiele** - Alle Endpoints mit Request/Response-Beispielen  
✅ **Code-Snippets** - cURL, JavaScript, Python, Java  
✅ **Authentifizierung** - Auth0 OAuth 2.0 Setup-Anleitung  
✅ **Error-Codes** - Vollständige Fehlercode-Dokumentation  
✅ **Try-it-Out** - Direkt in der Dokumentation testen  
✅ **Versionierung** - Changelog und API-Versionen  

### Quick Start

```bash
# 1. Auth0 Token abrufen
curl -X POST https://dev-auth0.eu.auth0.com/oauth/token \
  -H 'Content-Type: application/json' \
  -d '{
    "grant_type": "client_credentials",
    "client_id": "YOUR_CLIENT_ID",
    "client_secret": "YOUR_CLIENT_SECRET",
    "audience": "https://swissdentalline-api"
  }'

# 2. API-Request mit Token
curl -X GET https://swissdentalline.azurewebsites.net/api/termine \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN'
```

### Postman Collection Import

```
1. Öffne Postman
2. Import → Link einfügen:
   https://www.getpostman.com/collections/48708668-2sB3dVNn95
3. Environment konfigurieren:
   - BASE_URL
   - AUTH0_CLIENT_ID
   - AUTH0_CLIENT_SECRET
4. Collection Runner starten
```

---

## 🎯 Fazit

Die SwissDentalLine REST API ist **vollständig dokumentiert und getestet** mit:

✅ **60+ Endpoints** über 6 Kategorien  
✅ **Postman Collection** mit automatisierten Tests  
✅ **Öffentliche Dokumentation** mit interaktiven Beispielen  
✅ **Auth0 Integration** für sichere Authentifizierung  
✅ **248 automatisierte Tests** - alle erfolgreich  
✅ **KI-Integration** für Rezensions-Moderation  
✅ **Flex-Termin-Logik** als Unique-Selling-Point  

**Status:** ✅ **Produktionsreif**

---

**Erstellt am:** 17. Dezember 2025  
**Postman Collection Version:** 2.1  
**API Version:** 1.0  
**Base URL:** https://swissdentalline.azurewebsites.net

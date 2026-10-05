# ThelaWala 🛒

**Apne shehar ke thele, ab live.** — A live-location platform for Tier-3 street-cart vendors (thelewale) who move through the city selling their goods on foot. Customers can see which vendors are nearby and currently out, and follow their live Google Maps location.

## Why

Cart vendors have no fixed address. Their customers never know where they are on a given day. ThelaWala lets an admin onboard a vendor with a Google Maps live-location share link, and shows online vendors on a simple website so customers can find and follow them.

## Tech stack

- **Backend:** Java 17, Spring Boot 3 (Web, Data JPA, Validation)
- **Database:** MySQL
- **Frontend:** Plain HTML/CSS/JS (served as static files by Spring Boot)
- **Testing:** Postman (collection included)

## Project structure

```
thelawala/
├── pom.xml
├── postman/ThelaWala.postman_collection.json
└── src/main/
    ├── java/com/thelawala/
    │   ├── ThelaWalaApplication.java
    │   ├── model/        # Vendor entity + VendorStatus enum
    │   ├── repository/   # Spring Data JPA repository
    │   ├── dto/          # Request/response objects (trackerLink hidden in responses)
    │   ├── service/      # Business logic (VendorService)
    │   ├── controller/   # VendorController (CRUD/admin) + TrackController (redirect)
    │   └── exception/    # Not-found + global error handling
    └── resources/
        ├── application.properties
        └── static/       # index.html, app.js, style.css
```

## Setup

### 1. MySQL

Install MySQL and ensure it's running. The app auto-creates the `thelawala` database on first run (`createDatabaseIfNotExist=true`). By default it connects as:

```
host: localhost   port: 3306   user: root   password: root
```

Override with environment variables if yours differ:

```bash
export DB_HOST=localhost DB_PORT=3306 DB_NAME=thelawala DB_USER=root DB_PASSWORD=yourpass
```

### 2. Run the app

```bash
mvn spring-boot:run
```

(Requires Maven 3.6+ and JDK 17 installed.)

Then open **http://localhost:8080** for the website.

## API reference

Base URL: `http://localhost:8080`

| Method | Endpoint | Who | Purpose |
|--------|----------|-----|---------|
| POST | `/api/vendors` | admin | Onboard a vendor (name, phone, photo, link). Goes online if link given. |
| PUT | `/api/vendors/{id}/link` | admin | Update a vendor's live tracker link (also sets online). |
| PUT | `/api/vendors/{id}/offline` | admin | Mark a vendor offline. |
| GET | `/api/vendors/online` | public | List online vendors (no raw link exposed). |
| GET | `/api/vendors` | admin | List all vendors. |
| GET | `/api/vendors/{id}` | admin | Get one vendor. |
| GET | `/track/{id}` | public | Counts a visit, then 302-redirects to the live link. |

### Onboard example

```json
POST /api/vendors
{
  "name": "Ramu Chaat Bhandar",
  "phone": "+91 98765 43210",
  "photoUrl": "https://placehold.co/160x160?text=Ramu",
  "trackerLink": "https://maps.app.goo.gl/EXAMPLE-LIVE-LINK"
}
```

`photoUrl` and `trackerLink` are optional. A dummy placeholder photo is used if none is given.

### Link privacy

The raw `trackerLink` is **never** returned by any list/read API or sent to the browser. The public UI shows a **Track** button that opens `/track/{id}` in a new tab; the server records the visit (`visitCount`) and then redirects to the live Google Maps link.

## Testing with Postman

Import `postman/ThelaWala.postman_collection.json`. Set the `baseUrl` (default `http://localhost:8080`) and `vendorId` collection variables. To inspect the `/track` redirect, disable *Automatically follow redirects* in Postman settings.

## Roadmap ideas

- Admin authentication for the management APIs
- Image upload instead of placeholder photos
- Categories / search / distance filtering
- Vendor self-service onboarding

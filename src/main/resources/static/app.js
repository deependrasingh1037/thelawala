// Fetches online vendors and renders them. The tracker link is never sent to
// the browser; the Track button opens /track/{id}, which counts the visit
// server-side and redirects to the vendor's live Google Maps location.

const grid = document.getElementById("grid");
const countEl = document.getElementById("count");
const emptyEl = document.getElementById("empty");
const refreshBtn = document.getElementById("refreshBtn");

async function loadVendors() {
    grid.innerHTML = "";
    emptyEl.hidden = true;
    try {
        const res = await fetch("/api/vendors/online");
        if (!res.ok) throw new Error("Failed to load vendors");
        const vendors = await res.json();

        countEl.textContent = `Online vendors (${vendors.length})`;

        if (vendors.length === 0) {
            emptyEl.hidden = false;
            return;
        }

        for (const v of vendors) {
            grid.appendChild(renderCard(v));
        }
    } catch (e) {
        countEl.textContent = "Could not load vendors";
        console.error(e);
    }
}

function renderCard(v) {
    const card = document.createElement("div");
    card.className = "card";

    const img = document.createElement("img");
    img.src = v.photoUrl || "https://placehold.co/160x160?text=ThelaWala";
    img.alt = v.name;

    const name = document.createElement("div");
    name.className = "name";
    name.textContent = v.name;

    const badge = document.createElement("span");
    badge.className = "badge";
    badge.innerHTML = `<span class="dot"></span> Online`;

    const btn = document.createElement("button");
    btn.className = "track-btn";
    btn.textContent = "📍 Track live location";
    btn.disabled = !v.hasLink;
    if (!v.hasLink) btn.textContent = "No live link yet";
    btn.addEventListener("click", () => {
        // Opens the visit-counting redirect in a new tab.
        window.open(`/track/${v.id}`, "_blank", "noopener");
    });

    card.append(img, name, badge, btn);
    return card;
}

refreshBtn.addEventListener("click", loadVendors);
loadVendors();

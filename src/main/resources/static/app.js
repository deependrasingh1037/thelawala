// Fetches online vendors and renders them as clickable cards. Clicking a card
// opens a popup that loads full details (/api/vendors/{id}/detail) — name, phone,
// city, likes, visits and comments — plus the Track button. The tracker link is
// never sent to the browser; Track opens /track/{id}, which counts the visit
// server-side (in vendor_stats) and redirects to the live Google Maps location.

const grid = document.getElementById("grid");
const countEl = document.getElementById("count");
const emptyEl = document.getElementById("empty");
const refreshBtn = document.getElementById("refreshBtn");

// Modal elements
const modal = document.getElementById("modal");
const modalClose = document.getElementById("modalClose");
const mPhoto = document.getElementById("mPhoto");
const mName = document.getElementById("mName");
const mStatus = document.getElementById("mStatus");
const mPhone = document.getElementById("mPhone");
const mCity = document.getElementById("mCity");
const mLikes = document.getElementById("mLikes");
const mVisits = document.getElementById("mVisits");
const mTrack = document.getElementById("mTrack");
const mComments = document.getElementById("mComments");

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
    card.tabIndex = 0;
    card.setAttribute("role", "button");

    const img = document.createElement("img");
    img.src = v.photoUrl || "https://placehold.co/160x160?text=ThelaWala";
    img.alt = v.name;

    const name = document.createElement("div");
    name.className = "name";
    name.textContent = v.name;

    const badge = document.createElement("span");
    badge.className = "badge";
    badge.innerHTML = `<span class="dot"></span> Online`;

    const hint = document.createElement("div");
    hint.className = "card-hint";
    hint.textContent = "Tap for details";

    const track = document.createElement("button");
    track.className = "track-btn card-track-btn";
    track.disabled = !v.hasLink;
    track.textContent = v.hasLink ? "📍 Track live location" : "No live link yet";
    if (v.hasLink) {
        track.addEventListener("click", (e) => {
            e.stopPropagation();
            window.open(`/track/${v.id}`, "_blank", "noopener");
        });
    }

    card.append(img, name, badge, hint, track);
    card.addEventListener("click", () => openDetail(v.id));
    card.addEventListener("keydown", (e) => {
        if (e.key === "Enter" || e.key === " ") { e.preventDefault(); openDetail(v.id); }
    });
    return card;
}

async function openDetail(id) {
    try {
        const res = await fetch(`/api/vendors/${id}/detail`);
        if (!res.ok) throw new Error("Failed to load details");
        const v = await res.json();

        mPhoto.src = v.photoUrl || "https://placehold.co/160x160?text=ThelaWala";
        mPhoto.alt = v.name;
        mName.textContent = v.name;
        mStatus.hidden = v.status !== "ONLINE";
        mPhone.textContent = v.phone || "—";
        mCity.textContent = v.city || "—";
        mLikes.textContent = v.likes;
        mVisits.textContent = v.visitCount;

        mTrack.disabled = !v.hasLink;
        mTrack.textContent = v.hasLink ? "📍 Track live location" : "No live link yet";
        mTrack.onclick = v.hasLink
            ? () => window.open(`/track/${v.id}`, "_blank", "noopener")
            : null;

        renderComments(v.comments || []);
        showModal();
    } catch (e) {
        console.error(e);
    }
}

function renderComments(comments) {
    mComments.innerHTML = "";
    if (comments.length === 0) {
        const p = document.createElement("p");
        p.className = "no-comments";
        p.textContent = "No comments yet.";
        mComments.appendChild(p);
        return;
    }
    for (const c of comments) {
        const item = document.createElement("div");
        item.className = "comment";
        const author = document.createElement("span");
        author.className = "comment-author";
        author.textContent = c.author;
        const text = document.createElement("span");
        text.className = "comment-text";
        text.textContent = c.text;
        item.append(author, text);
        mComments.appendChild(item);
    }
}

function showModal() {
    modal.hidden = false;
    document.body.style.overflow = "hidden";
}
function hideModal() {
    modal.hidden = true;
    document.body.style.overflow = "";
}

modalClose.addEventListener("click", hideModal);
modal.addEventListener("click", (e) => { if (e.target === modal) hideModal(); });
document.addEventListener("keydown", (e) => { if (e.key === "Escape") hideModal(); });

refreshBtn.addEventListener("click", loadVendors);
loadVendors();

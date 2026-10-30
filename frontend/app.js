/* BidVelocity frontend interactions */

const API = 'http://localhost:9000';

let currentUser  = null;
let authToken    = null;
let allAuctions  = [];
let currentAuction = null;
let auctionRefCounter = 0;

const sampleAuctions = [
  {
    sample: true, title: 'Mirrorless Camera, 24MP', description: 'A lightly used mirrorless camera in great condition, with a kit lens and shoulder strap.',
    category: 'Electronics', startingBid: 18500, currentBid: 21200, endTime: hoursFromNow(19),
    imageUrl: 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=80'
  },
  {
    sample: true, title: 'Classic Stainless Steel Watch', description: 'A clean, everyday stainless steel watch with a simple dial and comfortable bracelet.',
    category: 'Watches', startingBid: 4200, currentBid: 4850, endTime: hoursFromNow(8),
    imageUrl: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80'
  },
  {
    sample: true, title: 'Leather Crossbody Bag', description: 'A well-kept everyday bag with an adjustable strap and plenty of room for essentials.',
    category: 'Fashion', startingBid: 1600, currentBid: 1950, endTime: hoursFromNow(28),
    imageUrl: 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=900&q=80'
  },
  {
    sample: true, title: 'Framed Botanical Print', description: 'A framed botanical print, ready to hang in a study, living room, or reading nook.',
    category: 'Collectibles', startingBid: 900, currentBid: 1250, endTime: hoursFromNow(12),
    imageUrl: 'https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=900&q=80'
  },
  {
    sample: true, title: 'Handmade Ceramic Table Lamp', description: 'A handmade ceramic lamp with a linen shade. Works well on a bedside table or desk.',
    category: 'Home & Living', startingBid: 1200, currentBid: 1500, endTime: hoursFromNow(34),
    imageUrl: 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80'
  },
  {
    sample: true, title: 'Vintage Film Camera', description: 'A classic film camera for collectors or anyone looking to try shooting on film.',
    category: 'Collectibles', startingBid: 2800, currentBid: 3200, endTime: hoursFromNow(5),
    imageUrl: 'https://images.unsplash.com/photo-1510127034890-1e5896e0a0a4?auto=format&fit=crop&w=900&q=80'
  }
];

// ─── Init ──────────────────────────────────────
window.addEventListener('DOMContentLoaded', () => {
  restoreSession();
  setDefaultDateTimes();
  loadHomeAuctions();
  document.getElementById('menuToggle').addEventListener('click', toggleNavigation);
  document.getElementById('auctionSearch').addEventListener('input', filterAuctions);
  document.getElementById('categoryFilter').addEventListener('change', filterAuctions);
  document.getElementById('auctionSort').addEventListener('change', filterAuctions);
  document.getElementById('featuredGrid').addEventListener('click', handleAuctionCardClick);
  document.getElementById('recentGrid').addEventListener('click', handleAuctionCardClick);
  document.getElementById('auctionsGrid').addEventListener('click', handleAuctionCardClick);
});

function hoursFromNow(hours) {
  return new Date(Date.now() + hours * 60 * 60 * 1000).toISOString();
}

// ─── Session ───────────────────────────────────
function restoreSession() {
  const saved = localStorage.getItem('bv_token');
  const user  = localStorage.getItem('bv_user');
  if (saved && user) {
    authToken   = saved;
    currentUser = JSON.parse(user);
    updateNavForUser();
  }
}

function saveSession(token, user) {
  authToken   = token;
  currentUser = user;
  localStorage.setItem('bv_token', token);
  localStorage.setItem('bv_user', JSON.stringify(user));
  updateNavForUser();
}

function logout() {
  authToken   = null;
  currentUser = null;
  localStorage.removeItem('bv_token');
  localStorage.removeItem('bv_user');
  document.getElementById('navAuth').classList.remove('hidden');
  document.getElementById('navUser').classList.add('hidden');
  showToast('Logged out successfully', 'info');
  showPage('home');
}

function updateNavForUser() {
  if (!currentUser) return;
  document.getElementById('navAuth').classList.add('hidden');
  document.getElementById('navUser').classList.remove('hidden');
  document.getElementById('userBadge').textContent = currentUser.username;
}

// ─── Page Navigation ───────────────────────────
function showPage(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));

  const page = document.getElementById(`page-${name}`);
  if (!page) return;
  page.classList.add('active');
  const navEl = document.getElementById(`nav-${name}`);
  if (navEl) navEl.classList.add('active');

  if (name === 'auctions') loadAuctions();
  if (name === 'bids')     loadMyBids();
  document.getElementById('navLinks').classList.remove('open');
  document.getElementById('menuToggle').setAttribute('aria-expanded', 'false');
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function toggleNavigation() {
  const links = document.getElementById('navLinks');
  const isOpen = links.classList.toggle('open');
  document.getElementById('menuToggle').setAttribute('aria-expanded', String(isOpen));
}

// ─── Auth Handlers ─────────────────────────────
async function handleLogin(e) {
  e.preventDefault();
  const body = {
    username: document.getElementById('loginUsername').value,
    password: document.getElementById('loginPassword').value
  };
  try {
    const res  = await apiPost('/auth/login', body, false);
    saveSession(res.token, { username: res.username, role: res.role });
    closeModal('loginModal');
    showToast(`Welcome back, ${res.username}.`, 'success');
    showPage('auctions');
  } catch (err) {
    showFormError('loginError', err.message || 'Invalid credentials');
  }
}

async function handleRegister(e) {
  e.preventDefault();
  const body = {
    username: document.getElementById('regUsername').value,
    email:    document.getElementById('regEmail').value,
    password: document.getElementById('regPassword').value,
    role:     'USER'
  };
  try {
    const res = await apiPost('/auth/register', body, false);
    saveSession(res.token, { username: res.username, role: res.role });
    closeModal('registerModal');
    showToast(`Account created. Welcome, ${res.username}.`, 'success');
    showPage('auctions');
  } catch (err) {
    showFormError('registerError', err.message || 'Registration failed');
  }
}

// ─── Auctions ──────────────────────────────────
async function loadHomeAuctions() {
  try {
    const listings = await apiGet('/auctions');
    allAuctions = Array.isArray(listings) ? listings : [];
  } catch {
    allAuctions = [];
  }
  const listings = getDisplayAuctions();
  const activeListings = listings.filter(a => a.sample || a.status === 'ACTIVE');
  const featured = (activeListings.length ? activeListings : listings).slice(0, 3);
  const recentlyAdded = [...listings].sort((a, b) =>
    new Date(b.createdAt || b.startTime || 0) - new Date(a.createdAt || a.startTime || 0)
  ).slice(0, 3);
  renderAuctionCards(featured, 'featuredGrid');
  renderAuctionCards(recentlyAdded, 'recentGrid');
}

async function loadAuctions() {
  const grid = document.getElementById('auctionsGrid');
  grid.innerHTML = `<div class="loading-spinner"><div class="spinner"></div><p>Fetching auctions…</p></div>`;
  try {
    const listings = await apiGet('/auctions');
    allAuctions = Array.isArray(listings) ? listings : [];
  } catch {
    allAuctions = [];
  }
  filterAuctions();
}

function filterAuctions() {
  const query = document.getElementById('auctionSearch').value.trim().toLowerCase();
  const category = document.getElementById('categoryFilter').value;
  const sortBy = document.getElementById('auctionSort').value;
  const filtered = getDisplayAuctions().filter(auction => {
    const text = `${auction.title || ''} ${auction.description || ''}`.toLowerCase();
    return (!query || text.includes(query)) &&
      (category === 'ALL' || getAuctionCategory(auction) === category);
  });
  if (sortBy === 'LOWEST') {
    filtered.sort((a, b) => currentBid(a) - currentBid(b));
  } else if (sortBy === 'RECENT') {
    filtered.sort((a, b) => new Date(b.createdAt || b.startTime || 0) - new Date(a.createdAt || a.startTime || 0));
  } else {
    filtered.sort((a, b) => new Date(a.endTime || 0) - new Date(b.endTime || 0));
  }
  renderAuctionCards(filtered, 'auctionsGrid');
}

function getDisplayAuctions() {
  return allAuctions.length ? allAuctions : sampleAuctions;
}

function renderAuctionCards(list, targetId) {
  const grid = document.getElementById(targetId);
  if (!grid) return;
  if (!list.length) {
    grid.innerHTML = `<div class="empty-state" style="grid-column:1/-1"><p>No auctions match your search.</p></div>`;
    return;
  }
  grid.innerHTML = list.map(auctionCard).join('');
}

function auctionCard(a) {
  const ref = `listing-${++auctionRefCounter}`;
  auctionRegistry.set(ref, a);
  const category = getAuctionCategory(a);
  const badge = a.sample ? 'Sample listing' : (a.status === 'ACTIVE' ? 'Live auction' : category);
  return `
  <article class="auction-card">
    <button class="card-image-button" type="button" data-auction-ref="${ref}" aria-label="View ${esc(a.title)}">
      <img class="auction-image" src="${esc(imageForAuction(a))}" alt="${esc(a.title)}" loading="lazy" />
    </button>
    <div class="auction-body">
      <div class="card-topline"><span class="category-label">${esc(category)}</span><span class="listing-label">${esc(badge)}</span></div>
      <h3 class="auction-title">${esc(a.title || 'Untitled item')}</h3>
      <div class="auction-bids">
        <div class="bid-item"><span class="bid-lbl">Current bid</span><span class="bid-val">₹${fmt(currentBid(a))}</span></div>
        <span class="auction-deadline">${esc(remainingTime(a.endTime))}</span>
      </div>
      <button class="btn btn-outline view-auction" type="button" data-auction-ref="${ref}">View Auction</button>
    </div>
  </article>`;
}

const auctionRegistry = new Map();

function handleAuctionCardClick(event) {
  const button = event.target.closest('[data-auction-ref]');
  if (!button) return;
  const auction = auctionRegistry.get(button.dataset.auctionRef);
  if (auction) openAuctionDetails(auction);
}

function openAuctionDetails(auction) {
  currentAuction = auction;
  const image = document.getElementById('detailImage');
  image.src = imageForAuction(auction);
  image.alt = auction.title || 'Auction item';
  document.getElementById('detailTitle').textContent = auction.title || 'Auction details';
  document.getElementById('detailCategory').textContent = getAuctionCategory(auction);
  document.getElementById('detailDescription').textContent = auction.description || 'No description provided.';
  document.getElementById('detailCurrentBid').textContent = `₹${fmt(currentBid(auction))}`;
  const minimum = Math.floor(currentBid(auction)) + 1;
  document.getElementById('detailMinimumBid').textContent = `₹${fmt(minimum)}`;
  document.getElementById('detailEndTime').textContent = fmtDate(auction.endTime);
  document.getElementById('bidAuctionId').value = auction.id || '';
  document.getElementById('bidAmount').min = String(minimum);
  document.getElementById('bidAmount').value = '';
  hideFormError('bidError');
  const preview = Boolean(auction.sample);
  document.getElementById('previewNotice').classList.toggle('hidden', !preview && auction.status === 'ACTIVE');
  document.getElementById('previewNotice').textContent = preview
    ? 'This is a sample listing. Bidding is only available on live auctions.'
    : 'Bidding is not open for this auction.';
  document.getElementById('placeBidForm').classList.toggle('hidden', preview || auction.status !== 'ACTIVE');
  openModal('auctionDetailsModal');
}

async function handleCreateAuction(e) {
  e.preventDefault();
  hideFormError('auctionError');
  if (!authToken) {
    showFormError('auctionError', 'Please log in to create a listing.');
    openModal('loginModal');
    return;
  }
  const startTime = document.getElementById('auctionStart').value;
  const endTime = document.getElementById('auctionEnd').value;
  const startingBid = Number(document.getElementById('auctionBid').value);
  if (!Number.isFinite(startingBid) || startingBid <= 0) {
    showFormError('auctionError', 'Enter a starting price greater than zero.');
    return;
  }
  if (!startTime || !endTime || new Date(endTime) <= new Date(startTime)) {
    showFormError('auctionError', 'The end time must be after the start time.');
    return;
  }
  if (new Date(startTime) <= new Date()) {
    showFormError('auctionError', 'Choose a start time in the future.');
    return;
  }
  const body = {
    title:       document.getElementById('auctionTitle').value,
    description: document.getElementById('auctionDesc').value,
    startingBid,
    startTime,
    endTime
  };
  try {
    await apiPost('/auctions', body, true);
    showToast('Your listing was created.', 'success');
    e.target.reset();
    setDefaultDateTimes();
    showPage('auctions');
  } catch (err) {
    showFormError('auctionError', err.message || 'Could not create your listing.');
  }
}

async function startAuction(id) {
  try {
    await apiPut(`/auctions/${id}/start`, {});
    showToast('Auction started! ✅', 'success');
    loadAuctions();
  } catch (err) { showToast(err.message || 'Failed to start', 'error'); }
}

async function closeAuction(id) {
  try {
    const res = await apiPut(`/auctions/${id}/close`, {});
    showToast(`Auction closed! Winner: User #${res.winnerId || 'None'} 🏆`, 'success');
    loadAuctions();
  } catch (err) { showToast(err.message || 'Failed to close', 'error'); }
}

// ─── Bids ──────────────────────────────────────
function openBidModal(auction) {
  openAuctionDetails(auction);
}

async function handlePlaceBid(e) {
  e.preventDefault();
  hideFormError('bidError');
  if (!authToken || !currentUser) {
    closeModal('auctionDetailsModal');
    openModal('loginModal');
    return;
  }
  if (!currentAuction || currentAuction.sample || currentAuction.status !== 'ACTIVE') {
    showFormError('bidError', 'Bidding is only available on a live auction.');
    return;
  }
  const auctionId = Number(document.getElementById('bidAuctionId').value);
  const amount = Number(document.getElementById('bidAmount').value);
  const minimum = Math.floor(currentBid(currentAuction)) + 1;
  if (!Number.isFinite(amount) || amount < minimum) {
    showFormError('bidError', `Enter a bid of at least ₹${fmt(minimum)}.`);
    return;
  }
  const bidderId  = simpleId(currentUser.username);
  const body = { auctionId, bidderId, bidAmount: amount };
  try {
    await apiPost('/bids', body, true);
    closeModal('auctionDetailsModal');
    showToast('Your bid was placed.', 'success');
    loadAuctions();
    loadHomeAuctions();
  } catch (err) {
    showFormError('bidError', err.message || 'Could not place your bid.');
  }
}

async function loadMyBids() {
  const container = document.getElementById('bidsContainer');
  if (!authToken) {
    container.innerHTML = `<div class="empty-state"><p>Log in to see your bids.</p><button class="btn btn-primary" onclick="openModal('loginModal')">Login</button></div>`;
    return;
  }
  container.innerHTML = `<div class="loading-spinner"><div class="spinner"></div><p>Loading your bids…</p></div>`;
  try {
    const auctions = await apiGet('/auctions');
    const results = await Promise.all(auctions.map(async auction => {
      try {
        const bids = await apiGet(`/bids/auction/${auction.id}`);
        return { auction, bids: Array.isArray(bids) ? bids : [], error: null };
      } catch (error) {
        return { auction, bids: [], error };
      }
    }));
    const failedRequests = results.filter(result => result.error);
    const userBids = results.flatMap(({ auction, bids }) => bids
      .filter(bid => Number(bid.bidderId) === simpleId(currentUser.username))
      .map(bid => ({ ...bid, auction })));
    if (!userBids.length && failedRequests.length === results.length) {
      throw new Error('Your bids are temporarily unavailable. Please try again later.');
    }
    if (!userBids.length) {
      container.innerHTML = `<div class="empty-state"><p>No bids yet. Find something you like and place your first bid.</p><button class="btn btn-primary" onclick="showPage('auctions')">Browse Auctions</button></div>`;
      return;
    }
    if (failedRequests.length) showToast('Some auction bid history could not be loaded.', 'info');
    container.innerHTML = `
      <div class="table-wrap">
        <table class="bids-table">
          <thead><tr><th>Auction</th><th>Your bid</th><th>Status</th><th>Placed</th></tr></thead>
          <tbody>${userBids.map(b => `
            <tr>
              <td>${esc(b.auction.title || `Auction #${b.auctionId}`)}</td>
              <td>₹${fmt(b.bidAmount)}</td>
              <td>${b.auction.status !== 'ACTIVE' ? 'Ended' : Number(b.bidAmount) >= currentBid(b.auction) ? 'Leading' : 'Outbid'}</td>
              <td>${fmtDate(b.bidTime)}</td>
            </tr>`).join('')}
          </tbody>
        </table>
      </div>`;
  } catch (error) {
    container.innerHTML = `<div class="empty-state"><p>${esc(error.message || 'Could not load your bids right now.')}</p><button class="btn btn-outline" onclick="loadMyBids()">Try again</button></div>`;
  }
}

// ─── Payments ──────────────────────────────────
async function loadMyPayments() {
  const container = document.getElementById('paymentsContainer');
  if (!authToken) {
    container.innerHTML = `<div class="empty-state"><div class="empty-icon">💳</div><p>Login to view payments</p><button class="btn btn-primary" onclick="openModal('loginModal')">Login Now</button></div>`;
    return;
  }
  container.innerHTML = `<div class="loading-spinner"><div class="spinner"></div><p>Loading payments…</p></div>`;
  try {
    const winnerId  = simpleId(currentUser.username);
    const payments  = await apiGet(`/payments/winner/${winnerId}`);
    if (!payments.length) {
      container.innerHTML = `<div class="empty-state"><div class="empty-icon">💳</div><p>No payments yet. Win an auction to see payments here!</p></div>`;
      return;
    }
    container.innerHTML = `
      <div class="table-wrap">
        <table>
          <thead><tr><th>#</th><th>Auction ID</th><th>Amount</th><th>Status</th><th>Created</th><th>Processed</th></tr></thead>
          <tbody>${payments.map(p => `
            <tr>
              <td>${p.id}</td>
              <td>#${p.auctionId}</td>
              <td style="color:var(--accent);font-weight:700">₹${fmt(p.amount)}</td>
              <td><span class="status-badge ${payStatusClass(p.status)}">${p.status}</span></td>
              <td>${fmtDate(p.createdAt)}</td>
              <td>${p.processedAt ? fmtDate(p.processedAt) : '—'}</td>
            </tr>`).join('')}
          </tbody>
        </table>
      </div>`;
  } catch (error) {
    container.innerHTML = `<div class="empty-state"><p>${esc(error.message || 'Could not load payments right now.')}</p></div>`;
  }
}

function payStatusClass(s) {
  if (s === 'COMPLETED') return 'status-ACTIVE';
  if (s === 'FAILED')    return 'status-CLOSED';
  return 'status-CREATED';
}

// ─── API Helpers ────────────────────────────────
function extractErrorMessage(payload, fallback = 'Request failed') {
  if (!payload) return fallback;
  if (typeof payload === 'string') return payload || fallback;
  if (payload.message) return payload.message;
  if (payload.error) return payload.error;
  if (payload.errors && typeof payload.errors === 'object') {
    const firstValue = Object.values(payload.errors)[0];
    if (Array.isArray(firstValue) && firstValue.length) return firstValue[0];
    if (typeof firstValue === 'string') return firstValue;
  }
  const firstFieldError = Object.values(payload).find(value => typeof value === 'string' && value);
  if (firstFieldError) return firstFieldError;
  return fallback;
}

async function apiGet(path) {
  const headers = { 'Content-Type': 'application/json' };
  if (authToken) headers['Authorization'] = `Bearer ${authToken}`;
  const res = await fetch(`${API}${path}`, { headers });
  if (!res.ok) {
    const err = await res.json().catch(() => null);
    throw new Error(extractErrorMessage(err, res.statusText || 'Request failed'));
  }
  return res.json();
}

async function apiPost(path, body, needsAuth = true) {
  if (needsAuth && !authToken) throw new Error('Authentication required');
  const headers = { 'Content-Type': 'application/json' };
  if (needsAuth && authToken) headers['Authorization'] = `Bearer ${authToken}`;
  const res = await fetch(`${API}${path}`, {
    method: 'POST', headers, body: JSON.stringify(body)
  });
  if (!res.ok) {
    const err = await res.json().catch(() => null);
    throw new Error(extractErrorMessage(err, res.statusText || 'Request failed'));
  }
  return res.json();
}

async function apiPut(path, body) {
  if (!authToken) throw new Error('Authentication required');
  const headers = { 'Content-Type': 'application/json', 'Authorization': `Bearer ${authToken}` };
  const res = await fetch(`${API}${path}`, {
    method: 'PUT', headers, body: JSON.stringify(body)
  });
  if (!res.ok) {
    const err = await res.json().catch(() => null);
    throw new Error(extractErrorMessage(err, res.statusText || 'Request failed'));
  }
  return res.json();
}

// ─── Modal Helpers ──────────────────────────────
function openModal(id) {
  document.getElementById(id).classList.add('open');
}
function closeModal(id) {
  document.getElementById(id).classList.remove('open');
}
function closeModalOnOverlay(e, id) {
  if (e.target === document.getElementById(id)) closeModal(id);
}
function switchModal(from, to) {
  closeModal(from); openModal(to);
}

function showFormError(id, msg) {
  const el = document.getElementById(id);
  el.textContent = msg;
  el.classList.remove('hidden');
}
function hideFormError(id) {
  document.getElementById(id).classList.add('hidden');
}

// ─── Toast ──────────────────────────────────────
function showToast(msg, type = 'info') {
  const container = document.getElementById('toastContainer');
  const t = document.createElement('div');
  t.className = `toast ${type}`;
  t.textContent = msg;
  container.appendChild(t);
  setTimeout(() => {
    t.style.animation = 'toast-out 0.3s ease forwards';
    setTimeout(() => t.remove(), 300);
  }, 3500);
}

// ─── Utils ──────────────────────────────────────
function esc(str) {
  if (str === null || str === undefined) return '';
  return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;').replace(/'/g,'&#39;');
}

function fmt(num) {
  const value = Number(num);
  return Number.isFinite(value) ? value.toLocaleString('en-IN') : '0';
}

function fmtDate(dt) {
  if (!dt) return '—';
  const date = new Date(dt);
  return Number.isNaN(date.getTime()) ? '—' : date.toLocaleString('en-IN', { dateStyle:'medium', timeStyle:'short' });
}

function currentBid(auction) {
  const bid = Number(auction.currentBid);
  return Number.isFinite(bid) && bid > 0 ? bid : Number(auction.startingBid) || 0;
}

function remainingTime(endTime) {
  const end = new Date(endTime).getTime();
  if (!Number.isFinite(end)) return 'End date not set';
  const remaining = end - Date.now();
  if (remaining <= 0) return 'Auction ended';
  const hours = Math.floor(remaining / 3600000);
  const days = Math.floor(hours / 24);
  if (days) return `Ends in ${days}d ${hours % 24}h`;
  if (hours) return `Ends in ${hours}h`;
  return `Ends in ${Math.max(1, Math.floor(remaining / 60000))}m`;
}

function getAuctionCategory(auction) {
  const categories = ['Electronics', 'Watches', 'Fashion', 'Collectibles', 'Home & Living'];
  if (categories.includes(auction.category)) return auction.category;
  const text = `${auction.title || ''} ${auction.description || ''}`.toLowerCase();
  if (/watch|clock|timepiece/.test(text)) return 'Watches';
  if (/bag|fashion|jacket|clothing|shoes|dress/.test(text)) return 'Fashion';
  if (/print|art|collectible|vintage|camera|antique/.test(text)) return 'Collectibles';
  if (/lamp|chair|home|sofa|table|decor/.test(text)) return 'Home & Living';
  return 'Electronics';
}

function imageForAuction(auction) {
  const fallbackImages = {
    'Electronics': 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80',
    'Watches': 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80',
    'Fashion': 'https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=900&q=80',
    'Collectibles': 'https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?auto=format&fit=crop&w=900&q=80',
    'Home & Living': 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80'
  };
  const candidate = auction.imageUrl || auction.image;
  if (typeof candidate === 'string') {
    try {
      const url = new URL(candidate, window.location.href);
      if (url.protocol === 'https:' || url.protocol === 'http:') return url.href;
    } catch {
      return fallbackImages[getAuctionCategory(auction)];
    }
  }
  return fallbackImages[getAuctionCategory(auction)];
}

function simpleId(username) {
  // Deterministic numeric ID from username for demo purposes
  let hash = 0;
  for (let i = 0; i < username.length; i++) {
    hash = ((hash << 5) - hash) + username.charCodeAt(i);
    hash |= 0;
  }
  return Math.abs(hash) % 10000 + 1;
}

function setDefaultDateTimes() {
  const now = new Date();
  const pad = n => String(n).padStart(2,'0');
  const fmt = d => `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;

  const start = new Date(now.getTime() + 5 * 60000);
  const end   = new Date(now.getTime() + 60 * 60000);

  const sEl = document.getElementById('auctionStart');
  const eEl = document.getElementById('auctionEnd');
  if (sEl) sEl.value = fmt(start);
  if (eEl) eEl.value = fmt(end);
}

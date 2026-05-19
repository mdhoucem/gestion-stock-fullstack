import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.response.use(
  res => res,
  err => {
    const message = err.response?.data?.message || err.message || 'Erreur serveur'
    return Promise.reject(new Error(message))
  }
)

// ── Catégories ──────────────────────────────────────────
export const categorieService = {
  getAll: () => api.get('/categories').then(r => r.data),
  getById: (id) => api.get(`/categories/${id}`).then(r => r.data),
  create: (data) => api.post('/categories', data).then(r => r.data),
  update: (id, data) => api.put(`/categories/${id}`, data).then(r => r.data),
  delete: (id) => api.delete(`/categories/${id}`),
}

// ── Articles ─────────────────────────────────────────────
export const articleService = {
  getAll: () => api.get('/articles').then(r => r.data),
  getById: (id) => api.get(`/articles/${id}`).then(r => r.data),
  getByCategorie: (catId) => api.get(`/articles/categorie/${catId}`).then(r => r.data),
  create: (data) => api.post('/articles', data).then(r => r.data),
  update: (id, data) => api.put(`/articles/${id}`, data).then(r => r.data),
  delete: (id) => api.delete(`/articles/${id}`),
}

// ── Commandes Client ─────────────────────────────────────
export const commandeClientService = {
  getAll: () => api.get('/commandes-clients').then(r => r.data),
  getById: (id) => api.get(`/commandes-clients/${id}`).then(r => r.data),
  create: (data) => api.post('/commandes-clients', data).then(r => r.data),
  updateEtat: (id, etat) => api.patch(`/commandes-clients/${id}/etat?etat=${etat}`).then(r => r.data),
  updateClient: (id, nomClient, emailClient) =>
    api.patch(`/commandes-clients/${id}/client?nomClient=${nomClient}&emailClient=${emailClient || ''}`).then(r => r.data),
  updateQuantiteLigne: (commandeId, ligneId, quantite) =>
    api.patch(`/commandes-clients/${commandeId}/lignes/${ligneId}/quantite?quantite=${quantite}`).then(r => r.data),
  supprimerLigne: (commandeId, ligneId) =>
    api.delete(`/commandes-clients/${commandeId}/lignes/${ligneId}`).then(r => r.data),
  getLignes: (commandeId) => api.get(`/commandes-clients/${commandeId}/lignes`).then(r => r.data),
  delete: (id) => api.delete(`/commandes-clients/${id}`),
}

// ── Commandes Fournisseur ────────────────────────────────
export const commandeFournisseurService = {
  getAll: () => api.get('/commandes-fournisseurs').then(r => r.data),
  getById: (id) => api.get(`/commandes-fournisseurs/${id}`).then(r => r.data),
  create: (data) => api.post('/commandes-fournisseurs', data).then(r => r.data),
  updateEtat: (id, etat) => api.patch(`/commandes-fournisseurs/${id}/etat?etat=${etat}`).then(r => r.data),
  updateFournisseur: (id, nomFournisseur, emailFournisseur) =>
    api.patch(`/commandes-fournisseurs/${id}/fournisseur?nomFournisseur=${nomFournisseur}&emailFournisseur=${emailFournisseur || ''}`).then(r => r.data),
  updateQuantiteLigne: (commandeId, ligneId, quantite) =>
    api.patch(`/commandes-fournisseurs/${commandeId}/lignes/${ligneId}/quantite?quantite=${quantite}`).then(r => r.data),
  supprimerLigne: (commandeId, ligneId) =>
    api.delete(`/commandes-fournisseurs/${commandeId}/lignes/${ligneId}`).then(r => r.data),
  getLignes: (commandeId) => api.get(`/commandes-fournisseurs/${commandeId}/lignes`).then(r => r.data),
  delete: (id) => api.delete(`/commandes-fournisseurs/${id}`),
}

// ── Mouvements de Stock ──────────────────────────────────
export const stockService = {
  getStockReel: (articleId) => api.get(`/mouvements-stock/stock-reel/${articleId}`).then(r => r.data),
  getMouvements: (articleId) => api.get(`/mouvements-stock/article/${articleId}`).then(r => r.data),
  entree: (articleId, quantite, motif) =>
    api.post(`/mouvements-stock/entree/${articleId}?quantite=${quantite}&motif=${motif || ''}`).then(r => r.data),
  sortie: (articleId, quantite, motif) =>
    api.post(`/mouvements-stock/sortie/${articleId}?quantite=${quantite}&motif=${motif || ''}`).then(r => r.data),
  correctionPositive: (articleId, quantite, motif) =>
    api.post(`/mouvements-stock/correction-positive/${articleId}?quantite=${quantite}&motif=${motif || ''}`).then(r => r.data),
  correctionNegative: (articleId, quantite, motif) =>
    api.post(`/mouvements-stock/correction-negative/${articleId}?quantite=${quantite}&motif=${motif || ''}`).then(r => r.data),
}

export default api

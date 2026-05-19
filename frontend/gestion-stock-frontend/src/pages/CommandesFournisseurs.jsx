import { useState, useEffect } from 'react'
import { commandeFournisseurService, articleService } from '../services/api'
import { Modal, ConfirmModal, EtatBadge, Loading, Empty, Alert } from '../components/UI'

const emptyForm = { nomFournisseur: '', emailFournisseur: '', lignesCommande: [] }

export default function CommandesFournisseurs() {
  const [commandes, setCommandes] = useState([])
  const [articles, setArticles] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showCreate, setShowCreate] = useState(false)
  const [showDetail, setShowDetail] = useState(null)
  const [confirmId, setConfirmId] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [newLigne, setNewLigne] = useState({ articleId: '', quantite: 1 })

  const load = () => {
    setLoading(true)
    Promise.all([commandeFournisseurService.getAll(), articleService.getAll()])
      .then(([cmds, arts]) => { setCommandes(cmds); setArticles(arts); setLoading(false) })
      .catch(e => { setError(e.message); setLoading(false) })
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    load()
  }, [])

  const addLigne = () => {
    if (!newLigne.articleId) return
    const art = articles.find(a => String(a.id) === String(newLigne.articleId))
    if (!art) return
    setForm(f => ({
      ...f,
      lignesCommande: [...f.lignesCommande, {
        articleId: art.id,
        articleNom: art.nom,
        quantite: parseInt(newLigne.quantite),
        prixUnitaire: art.prixUnitaire
      }]
    }))
    setNewLigne({ articleId: '', quantite: 1 })
  }

  const removeLigne = (idx) =>
    setForm(f => ({ ...f, lignesCommande: f.lignesCommande.filter((_, i) => i !== idx) }))

  const handleCreate = async () => {
    if (!form.nomFournisseur.trim()) return setError('Le nom du fournisseur est obligatoire')
    if (form.lignesCommande.length === 0) return setError('Ajoutez au moins un article')
    try {
      await commandeFournisseurService.create(form)
      setShowCreate(false); setForm(emptyForm); load()
    } catch (e) { setError(e.message) }
  }

  const handleEtat = async (id, etat) => {
    try { await commandeFournisseurService.updateEtat(id, etat); load() }
    catch (e) { setError(e.message) }
  }

  const handleDelete = async () => {
    try { await commandeFournisseurService.delete(confirmId); setConfirmId(null); load() }
    catch (e) { setError(e.message); setConfirmId(null) }
  }

  const total = (lignes) => lignes?.reduce((s, l) => s + l.prixUnitaire * l.quantite, 0) || 0

  return (
    <div className="main-content">
      <div className="page-header">
        <h2>Commandes Fournisseurs</h2>
        <p>Gérer les commandes passées aux fournisseurs</p>
      </div>
      <div className="page-body">
        <Alert type="error" message={error} />

        <div className="toolbar">
          <div className="toolbar-left">
            <span style={{ color: 'var(--text-muted)', fontSize: 13 }}>
              {commandes.length} commande{commandes.length !== 1 ? 's' : ''}
            </span>
          </div>
          <div className="toolbar-right">
            <button className="btn btn-primary" onClick={() => { setForm(emptyForm); setShowCreate(true); setError('') }}>
              + Nouvelle commande
            </button>
          </div>
        </div>

        <div className="card">
          {loading ? <Loading /> : commandes.length === 0 ? (
            <Empty icon="↙" message="Aucune commande fournisseur" />
          ) : (
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Fournisseur</th>
                    <th>Date</th>
                    <th>Articles</th>
                    <th>Total</th>
                    <th>État</th>
                    <th style={{ width: 160 }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {commandes.map(c => (
                    <tr key={c.id}>
                      <td style={{ color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>{c.id}</td>
                      <td>
                        <div style={{ fontWeight: 500 }}>{c.nomFournisseur}</div>
                        {c.emailFournisseur && (
                          <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{c.emailFournisseur}</div>
                        )}
                      </td>
                      <td style={{ color: 'var(--text-muted)', fontSize: 12 }}>{c.dateCommande}</td>
                      <td style={{ color: 'var(--text-secondary)', fontSize: 13 }}>
                        {c.lignesCommande?.length || 0} article(s)
                      </td>
                      <td style={{ fontFamily: 'var(--font-mono)', fontSize: 13 }}>
                        {total(c.lignesCommande).toFixed(2)} DT
                      </td>
                      <td><EtatBadge etat={c.etatCommande} /></td>
                      <td>
                        <div style={{ display: 'flex', gap: 4, flexWrap: 'wrap' }}>
                          <button className="btn btn-icon btn-sm" onClick={() => setShowDetail(c)} title="Détails">⊙</button>
                          {c.etatCommande === 'EN_COURS_DE_PREPARATION' && (
                            <button className="btn btn-sm btn-secondary" onClick={() => handleEtat(c.id, 'VALIDEE')}>
                              Valider
                            </button>
                          )}
                          {c.etatCommande === 'VALIDEE' && (
                            <button className="btn btn-sm btn-secondary" onClick={() => handleEtat(c.id, 'LIVREE')}>
                              Livrer
                            </button>
                          )}
                          {c.etatCommande !== 'LIVREE' && (
                            <button className="btn btn-danger btn-sm" onClick={() => setConfirmId(c.id)}>✕</button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Modal création */}
      {showCreate && (
        <Modal
          title="Nouvelle commande fournisseur"
          onClose={() => setShowCreate(false)}
          onSubmit={handleCreate}
        >
          <Alert type="error" message={error} />
          <div className="form-grid" style={{ marginBottom: 16 }}>
            <div className="form-group">
              <label>Nom fournisseur *</label>
              <input
                value={form.nomFournisseur}
                onChange={e => setForm({ ...form, nomFournisseur: e.target.value })}
                placeholder="Nom du fournisseur"
              />
            </div>
            <div className="form-group">
              <label>Email fournisseur</label>
              <input
                type="email"
                value={form.emailFournisseur}
                onChange={e => setForm({ ...form, emailFournisseur: e.target.value })}
                placeholder="email@fournisseur.com"
              />
            </div>
          </div>
          <div className="divider" />
          <div style={{ marginBottom: 12, fontWeight: 500, fontSize: 13 }}>Articles</div>
          <div style={{ display: 'flex', gap: 8, marginBottom: 12 }}>
            <select
              value={newLigne.articleId}
              onChange={e => setNewLigne({ ...newLigne, articleId: e.target.value })}
              style={{ flex: 1 }}
            >
              <option value="">Choisir un article</option>
              {articles.map(a => (
                <option key={a.id} value={a.id}>{a.nom} — {Number(a.prixUnitaire).toFixed(2)} DT</option>
              ))}
            </select>
            <input
              type="number" min="1"
              value={newLigne.quantite}
              onChange={e => setNewLigne({ ...newLigne, quantite: e.target.value })}
              style={{ width: 70 }}
            />
            <button className="btn btn-secondary" onClick={addLigne}>Ajouter</button>
          </div>
          {form.lignesCommande.length > 0 && (
            <table style={{ fontSize: 13 }}>
              <thead><tr><th>Article</th><th>Qté</th><th>Prix</th><th></th></tr></thead>
              <tbody>
                {form.lignesCommande.map((l, i) => (
                  <tr key={i}>
                    <td>{l.articleNom}</td>
                    <td>{l.quantite}</td>
                    <td style={{ fontFamily: 'var(--font-mono)' }}>{(l.prixUnitaire * l.quantite).toFixed(2)} DT</td>
                    <td><button className="btn btn-danger btn-sm" onClick={() => removeLigne(i)}>✕</button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </Modal>
      )}

      {/* Modal détail */}
      {showDetail && (
        <Modal
          title={`Commande #${showDetail.id} — ${showDetail.nomFournisseur}`}
          onClose={() => setShowDetail(null)}
        >
          <div style={{ marginBottom: 12 }}>
            <EtatBadge etat={showDetail.etatCommande} />
            <span style={{ marginLeft: 10, fontSize: 13, color: 'var(--text-muted)' }}>{showDetail.dateCommande}</span>
          </div>
          {showDetail.emailFournisseur && (
            <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 12 }}>
              📧 {showDetail.emailFournisseur}
            </p>
          )}
          <div className="divider" />
          <table>
            <thead>
              <tr><th>Article</th><th>Qté</th><th>Prix unit.</th><th>Sous-total</th></tr>
            </thead>
            <tbody>
              {showDetail.lignesCommande?.map(l => (
                <tr key={l.id}>
                  <td>{l.articleNom}</td>
                  <td>{l.quantite}</td>
                  <td style={{ fontFamily: 'var(--font-mono)' }}>{Number(l.prixUnitaire).toFixed(2)} DT</td>
                  <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 500 }}>
                    {(l.prixUnitaire * l.quantite).toFixed(2)} DT
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <div style={{ textAlign: 'right', marginTop: 14, fontWeight: 600, fontFamily: 'var(--font-mono)' }}>
            Total : {total(showDetail.lignesCommande).toFixed(2)} DT
          </div>
        </Modal>
      )}

      {confirmId && (
        <ConfirmModal
          message="Supprimer cette commande fournisseur ?"
          onConfirm={handleDelete}
          onClose={() => setConfirmId(null)}
        />
      )}
    </div>
  )
}

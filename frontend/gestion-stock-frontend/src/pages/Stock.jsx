import { useState, useEffect } from 'react'
import { stockService, articleService } from '../services/api'
import { Modal, TypeMvtBadge, Loading, Empty, Alert } from '../components/UI'

export default function Stock() {
  const [articles, setArticles] = useState([])
  const [selectedArticle, setSelectedArticle] = useState(null)
  const [mouvements, setMouvements] = useState([])
  const [stockReel, setStockReel] = useState(null)
  const [loading, setLoading] = useState(true)
  const [loadingMvt, setLoadingMvt] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [form, setForm] = useState({ type: 'ENTREE', quantite: '', motif: '' })

  useEffect(() => {
    articleService.getAll()
      .then(data => { setArticles(data); setLoading(false) })
      .catch(e => { setError(e.message); setLoading(false) })
  }, [])

  const selectArticle = async (article) => {
    setSelectedArticle(article)
    setLoadingMvt(true)
    setError('')
    try {
      const [mvts, stock] = await Promise.all([
        stockService.getMouvements(article.id),
        stockService.getStockReel(article.id),
      ])
      setMouvements(mvts)
      setStockReel(stock)
    } catch (e) {
      setError(e.message)
    }
    setLoadingMvt(false)
  }

  const handleMouvement = async () => {
    if (!form.quantite || parseInt(form.quantite) <= 0) return setError('La quantité doit être > 0')
    setError('')
    try {
      const q = parseInt(form.quantite)
      const m = form.motif
      const id = selectedArticle.id
      if (form.type === 'ENTREE') await stockService.entree(id, q, m)
      else if (form.type === 'SORTIE') await stockService.sortie(id, q, m)
      else if (form.type === 'CORRECTION_POSITIVE') await stockService.correctionPositive(id, q, m)
      else if (form.type === 'CORRECTION_NEGATIVE') await stockService.correctionNegative(id, q, m)

      setSuccess('Mouvement enregistré avec succès')
      setShowModal(false)
      setForm({ type: 'ENTREE', quantite: '', motif: '' })
      setTimeout(() => setSuccess(''), 3000)
      await selectArticle(selectedArticle)
    } catch (e) {
      setError(e.message)
    }
  }

  const formatDate = (d) => {
    if (!d) return '—'
    return new Date(d).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' })
  }

  return (
    <div className="main-content">
      <div className="page-header">
        <h2>Mouvements de Stock</h2>
        <p>Consulter et gérer les entrées / sorties de stock</p>
      </div>
      <div className="page-body">
        <Alert type="error" message={error} />
        <Alert type="success" message={success} />

        <div style={{ display: 'grid', gridTemplateColumns: '260px 1fr', gap: 16, alignItems: 'start' }}>

          {/* Liste articles */}
          <div className="card">
            <div className="card-header">
              <span className="card-title">Articles</span>
            </div>
            {loading ? <Loading /> : articles.length === 0 ? (
              <Empty icon="◻" message="Aucun article" />
            ) : (
              <div style={{ maxHeight: 520, overflowY: 'auto' }}>
                {articles.map(a => (
                  <div
                    key={a.id}
                    onClick={() => selectArticle(a)}
                    style={{
                      padding: '11px 16px',
                      cursor: 'pointer',
                      borderBottom: '1px solid var(--border)',
                      background: selectedArticle?.id === a.id ? 'var(--accent-light)' : 'transparent',
                      transition: 'background 0.1s',
                    }}
                  >
                    <div style={{
                      fontWeight: 500,
                      fontSize: 13.5,
                      color: selectedArticle?.id === a.id ? 'var(--accent)' : 'var(--text-primary)'
                    }}>
                      {a.nom}
                    </div>
                    <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 2 }}>
                      Stock initial : {a.quantiteEnStock}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Détail article sélectionné */}
          <div>
            {!selectedArticle ? (
              <div className="card">
                <div style={{ padding: '48px 24px', textAlign: 'center', color: 'var(--text-muted)' }}>
                  <div style={{ fontSize: 28, marginBottom: 10 }}>▤</div>
                  <p style={{ fontSize: 13 }}>Sélectionnez un article pour voir ses mouvements</p>
                </div>
              </div>
            ) : (
              <>
                {/* Info stock */}
                <div className="stats-grid" style={{ marginBottom: 16 }}>
                  <div className="stat-card">
                    <div className="stat-label">Article</div>
                    <div style={{ fontWeight: 600, fontSize: 15, marginTop: 4 }}>{selectedArticle.nom}</div>
                    {selectedArticle.categorieNom && (
                      <div className="stat-sub">{selectedArticle.categorieNom}</div>
                    )}
                  </div>
                  <div className="stat-card">
                    <div className="stat-label">Stock réel</div>
                    <div className={`stat-value ${stockReel < 5 ? 'badge-red' : ''}`}
                      style={{ color: stockReel < 5 ? 'var(--danger)' : 'var(--text-primary)' }}>
                      {stockReel ?? '…'}
                    </div>
                    <div className="stat-sub">unités disponibles</div>
                  </div>
                  <div className="stat-card">
                    <div className="stat-label">Prix unitaire</div>
                    <div className="stat-value" style={{ fontSize: 20 }}>
                      {Number(selectedArticle.prixUnitaire).toFixed(2)}
                    </div>
                    <div className="stat-sub">DT</div>
                  </div>
                </div>

                {/* Tableau mouvements */}
                <div className="card">
                  <div className="card-header">
                    <span className="card-title">Historique des mouvements</span>
                    <button className="btn btn-primary btn-sm" onClick={() => { setShowModal(true); setError('') }}>
                      + Nouveau mouvement
                    </button>
                  </div>
                  {loadingMvt ? <Loading /> : mouvements.length === 0 ? (
                    <Empty icon="▤" message="Aucun mouvement pour cet article" />
                  ) : (
                    <div className="table-container">
                      <table>
                        <thead>
                          <tr>
                            <th>Date</th>
                            <th>Type</th>
                            <th>Quantité</th>
                            <th>Motif</th>
                          </tr>
                        </thead>
                        <tbody>
                          {mouvements.map(m => (
                            <tr key={m.id}>
                              <td style={{ fontSize: 12, color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                                {formatDate(m.dateMouvement)}
                              </td>
                              <td><TypeMvtBadge type={m.typeMouvement} /></td>
                              <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 500 }}>
                                {['ENTREE','CORRECTION_POSITIVE'].includes(m.typeMouvement) ? '+' : '-'}{m.quantite}
                              </td>
                              <td style={{ color: 'var(--text-secondary)', fontSize: 13 }}>
                                {m.motif || '—'}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Modal nouveau mouvement */}
      {showModal && (
        <Modal
          title={`Mouvement de stock — ${selectedArticle?.nom}`}
          onClose={() => setShowModal(false)}
          onSubmit={handleMouvement}
          submitLabel="Enregistrer"
        >
          <Alert type="error" message={error} />
          <div className="form-grid cols-1">
            <div className="form-group">
              <label>Type de mouvement *</label>
              <select value={form.type} onChange={e => setForm({ ...form, type: e.target.value })}>
                <option value="ENTREE">↑ Entrée de stock</option>
                <option value="SORTIE">↓ Sortie de stock</option>
                <option value="CORRECTION_POSITIVE">+ Correction positive</option>
                <option value="CORRECTION_NEGATIVE">− Correction négative</option>
              </select>
            </div>
            <div className="form-group">
              <label>Quantité *</label>
              <input
                type="number" min="1"
                value={form.quantite}
                onChange={e => setForm({ ...form, quantite: e.target.value })}
                placeholder="Ex : 10"
              />
            </div>
            <div className="form-group">
              <label>Motif</label>
              <textarea
                value={form.motif}
                onChange={e => setForm({ ...form, motif: e.target.value })}
                placeholder="Ex : Réception commande fournisseur #12…"
              />
            </div>
          </div>
          <div style={{
            marginTop: 12, padding: '10px 14px',
            background: 'var(--bg)', borderRadius: 'var(--radius-sm)',
            fontSize: 13, color: 'var(--text-secondary)'
          }}>
            Stock actuel : <strong>{stockReel}</strong> unité{stockReel !== 1 ? 's' : ''}
          </div>
        </Modal>
      )}
    </div>
  )
}

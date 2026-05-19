import { useState, useEffect } from 'react'
import { articleService, categorieService } from '../services/api'
import { Modal, ConfirmModal, Loading, Empty, Alert } from '../components/UI'

const emptyForm = { id: null, nom: '', description: '', prixUnitaire: '', quantiteEnStock: '', categorieId: '' }

export default function Articles() {
  const [articles, setArticles] = useState([])
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [confirmId, setConfirmId] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [filterCat, setFilterCat] = useState('')

  const load = () => {
    setLoading(true)
    Promise.all([articleService.getAll(), categorieService.getAll()])
      .then(([arts, cats]) => { setArticles(arts); setCategories(cats); setLoading(false) })
      .catch(e => { setError(e.message); setLoading(false) })
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    load()
  }, [])

  const openCreate = () => { setForm(emptyForm); setShowModal(true); setError('') }
  const openEdit = (a) => {
    setForm({ id: a.id, nom: a.nom, description: a.description || '', prixUnitaire: a.prixUnitaire, quantiteEnStock: a.quantiteEnStock, categorieId: a.categorieId || '' })
    setShowModal(true); setError('')
  }

  const handleSubmit = async () => {
    if (!form.nom.trim()) return setError('Le nom est obligatoire')
    if (!form.prixUnitaire) return setError('Le prix est obligatoire')
    if (form.quantiteEnStock === '') return setError('La quantité est obligatoire')
    try {
      const payload = { ...form, prixUnitaire: parseFloat(form.prixUnitaire), quantiteEnStock: parseInt(form.quantiteEnStock), categorieId: form.categorieId || null }
      if (form.id) await articleService.update(form.id, payload)
      else await articleService.create(payload)
      setShowModal(false); load()
    } catch (e) { setError(e.message) }
  }

  const handleDelete = async () => {
    try { await articleService.delete(confirmId); setConfirmId(null); load() }
    catch (e) { setError(e.message); setConfirmId(null) }
  }

  const displayed = filterCat ? articles.filter(a => String(a.categorieId) === filterCat) : articles

  return (
    <div className="main-content">
      <div className="page-header">
        <h2>Articles</h2>
        <p>Catalogue de tous les articles</p>
      </div>
      <div className="page-body">
        <Alert type="error" message={error} />

        <div className="toolbar">
          <div className="toolbar-left">
            <select value={filterCat} onChange={e => setFilterCat(e.target.value)} style={{ width: 180 }}>
              <option value="">Toutes les catégories</option>
              {categories.map(c => <option key={c.id} value={c.id}>{c.nom}</option>)}
            </select>
            <span style={{ color: 'var(--text-muted)', fontSize: 13 }}>{displayed.length} article{displayed.length !== 1 ? 's' : ''}</span>
          </div>
          <div className="toolbar-right">
            <button className="btn btn-primary" onClick={openCreate}>+ Nouvel article</button>
          </div>
        </div>

        <div className="card">
          {loading ? <Loading /> : displayed.length === 0 ? (
            <Empty icon="◻" message="Aucun article trouvé" />
          ) : (
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Nom</th>
                    <th>Catégorie</th>
                    <th>Prix unitaire</th>
                    <th>Qté en stock</th>
                    <th style={{ width: 100 }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {displayed.map(a => (
                    <tr key={a.id}>
                      <td style={{ color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>{a.id}</td>
                      <td>
                        <div style={{ fontWeight: 500 }}>{a.nom}</div>
                        {a.description && <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{a.description}</div>}
                      </td>
                      <td>
                        {a.categorieNom ? <span className="badge badge-gray">{a.categorieNom}</span> : <span style={{ color: 'var(--text-muted)' }}>—</span>}
                      </td>
                      <td style={{ fontFamily: 'var(--font-mono)', fontSize: 13 }}>{Number(a.prixUnitaire).toFixed(2)} DT</td>
                      <td>
                        <span className={`badge ${a.quantiteEnStock < 5 ? 'badge-red' : a.quantiteEnStock < 20 ? 'badge-yellow' : 'badge-green'}`}>
                          {a.quantiteEnStock}
                        </span>
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: 4 }}>
                          <button className="btn btn-icon btn-sm" onClick={() => openEdit(a)}>✎</button>
                          <button className="btn btn-danger btn-sm" onClick={() => setConfirmId(a.id)}>✕</button>
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

      {showModal && (
        <Modal title={form.id ? 'Modifier l\'article' : 'Nouvel article'} onClose={() => setShowModal(false)} onSubmit={handleSubmit}>
          <Alert type="error" message={error} />
          <div className="form-grid">
            <div className="form-group span-2">
              <label>Nom *</label>
              <input value={form.nom} onChange={e => setForm({ ...form, nom: e.target.value })} placeholder="Nom de l'article" />
            </div>
            <div className="form-group">
              <label>Prix unitaire (DT) *</label>
              <input type="number" min="0" step="0.01" value={form.prixUnitaire} onChange={e => setForm({ ...form, prixUnitaire: e.target.value })} placeholder="0.00" />
            </div>
            <div className="form-group">
              <label>Quantité en stock *</label>
              <input type="number" min="0" value={form.quantiteEnStock} onChange={e => setForm({ ...form, quantiteEnStock: e.target.value })} placeholder="0" />
            </div>
            <div className="form-group span-2">
              <label>Catégorie</label>
              <select value={form.categorieId} onChange={e => setForm({ ...form, categorieId: e.target.value })}>
                <option value="">Sans catégorie</option>
                {categories.map(c => <option key={c.id} value={c.id}>{c.nom}</option>)}
              </select>
            </div>
            <div className="form-group span-2">
              <label>Description</label>
              <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} placeholder="Description optionnelle…" />
            </div>
          </div>
        </Modal>
      )}

      {confirmId && (
        <ConfirmModal
          message="Voulez-vous vraiment supprimer cet article ?"
          onConfirm={handleDelete}
          onClose={() => setConfirmId(null)}
        />
      )}
    </div>
  )
}

import { useState, useEffect } from 'react'
import { categorieService } from '../services/api'
import { Modal, ConfirmModal, Loading, Empty, Alert } from '../components/UI'

export default function Categories() {
  const [categories, setCategories] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [confirmId, setConfirmId] = useState(null)
  const [form, setForm] = useState({ id: null, nom: '', description: '' })

  const load = () => {
    setLoading(true)
    categorieService.getAll()
      .then(data => { setCategories(data); setLoading(false) })
      .catch(e => { setError(e.message); setLoading(false) })
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    load()
  }, [])

  const openCreate = () => { setForm({ id: null, nom: '', description: '' }); setShowModal(true); setError('') }
  const openEdit = (c) => { setForm({ id: c.id, nom: c.nom, description: c.description || '' }); setShowModal(true); setError('') }

  const handleSubmit = async () => {
    if (!form.nom.trim()) return setError('Le nom est obligatoire')
    try {
      if (form.id) await categorieService.update(form.id, form)
      else await categorieService.create(form)
      setShowModal(false)
      load()
    } catch (e) { setError(e.message) }
  }

  const handleDelete = async () => {
    try {
      await categorieService.delete(confirmId)
      setConfirmId(null)
      load()
    } catch (e) { setError(e.message); setConfirmId(null) }
  }

  return (
    <div className="main-content">
      <div className="page-header">
        <h2>Catégories</h2>
        <p>Gérer les familles d'articles</p>
      </div>
      <div className="page-body">
        <Alert type="error" message={error} />

        <div className="toolbar">
          <div className="toolbar-left">
            <span style={{ color: 'var(--text-muted)', fontSize: 13 }}>{categories.length} catégorie{categories.length !== 1 ? 's' : ''}</span>
          </div>
          <div className="toolbar-right">
            <button className="btn btn-primary" onClick={openCreate}>+ Nouvelle catégorie</button>
          </div>
        </div>

        <div className="card">
          {loading ? <Loading /> : categories.length === 0 ? (
            <Empty icon="⊞" message="Aucune catégorie — créez-en une !" />
          ) : (
            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Nom</th>
                    <th>Description</th>
                    <th style={{ width: 100 }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {categories.map(c => (
                    <tr key={c.id}>
                      <td style={{ color: 'var(--text-muted)', fontFamily: 'var(--font-mono)', fontSize: 12 }}>{c.id}</td>
                      <td style={{ fontWeight: 500 }}>{c.nom}</td>
                      <td style={{ color: 'var(--text-secondary)' }}>{c.description || '—'}</td>
                      <td>
                        <div style={{ display: 'flex', gap: 4 }}>
                          <button className="btn btn-icon btn-sm" onClick={() => openEdit(c)} title="Modifier">✎</button>
                          <button className="btn btn-danger btn-sm" onClick={() => setConfirmId(c.id)} title="Supprimer">✕</button>
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
        <Modal title={form.id ? 'Modifier la catégorie' : 'Nouvelle catégorie'} onClose={() => setShowModal(false)} onSubmit={handleSubmit}>
          <Alert type="error" message={error} />
          <div className="form-grid cols-1">
            <div className="form-group">
              <label>Nom *</label>
              <input value={form.nom} onChange={e => setForm({ ...form, nom: e.target.value })} placeholder="Ex : Informatique" />
            </div>
            <div className="form-group">
              <label>Description</label>
              <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} placeholder="Description optionnelle…" />
            </div>
          </div>
        </Modal>
      )}

      {confirmId && (
        <ConfirmModal
          message="Voulez-vous vraiment supprimer cette catégorie ? Cette action est irréversible."
          onConfirm={handleDelete}
          onClose={() => setConfirmId(null)}
        />
      )}
    </div>
  )
}

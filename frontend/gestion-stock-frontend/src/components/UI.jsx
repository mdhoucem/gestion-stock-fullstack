// ── Modal ────────────────────────────────────────────────
export function Modal({ title, onClose, onSubmit, children, submitLabel = 'Enregistrer' }) {
  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal">
        <div className="modal-header">
          <h3>{title}</h3>
          <button className="btn btn-icon" onClick={onClose}>✕</button>
        </div>
        <div className="modal-body">{children}</div>
        {onSubmit && (
          <div className="modal-footer">
            <button className="btn btn-secondary" onClick={onClose}>Annuler</button>
            <button className="btn btn-primary" onClick={onSubmit}>{submitLabel}</button>
          </div>
        )}
      </div>
    </div>
  )
}

// ── Badge état commande ───────────────────────────────────
export function EtatBadge({ etat }) {
  const map = {
    EN_COURS_DE_PREPARATION: { label: 'En préparation', cls: 'badge-yellow' },
    VALIDEE: { label: 'Validée', cls: 'badge-blue' },
    LIVREE: { label: 'Livrée', cls: 'badge-green' },
  }
  const { label, cls } = map[etat] || { label: etat, cls: 'badge-gray' }
  return <span className={`badge ${cls}`}>{label}</span>
}

// ── Type mouvement badge ──────────────────────────────────
export function TypeMvtBadge({ type }) {
  const map = {
    ENTREE: { label: '↑ Entrée', cls: 'badge-green' },
    SORTIE: { label: '↓ Sortie', cls: 'badge-red' },
    CORRECTION_POSITIVE: { label: '+ Correction', cls: 'badge-blue' },
    CORRECTION_NEGATIVE: { label: '− Correction', cls: 'badge-yellow' },
  }
  const { label, cls } = map[type] || { label: type, cls: 'badge-gray' }
  return <span className={`badge ${cls}`}>{label}</span>
}

// ── Loading / Empty / Error ───────────────────────────────
export function Loading() {
  return (
    <div className="loading">
      <div className="spinner" /> Chargement…
    </div>
  )
}

export function Empty({ icon = '📭', message = 'Aucune donnée' }) {
  return (
    <div className="empty">
      <div className="empty-icon">{icon}</div>
      <p>{message}</p>
    </div>
  )
}

export function Alert({ type = 'error', message }) {
  if (!message) return null
  return <div className={`alert alert-${type}`}>{message}</div>
}

// ── Confirm delete dialog ─────────────────────────────────
export function ConfirmModal({ message, onConfirm, onClose }) {
  return (
    <div className="modal-overlay" onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="modal" style={{ maxWidth: 380 }}>
        <div className="modal-header">
          <h3>Confirmer la suppression</h3>
          <button className="btn btn-icon" onClick={onClose}>✕</button>
        </div>
        <div className="modal-body">
          <p style={{ color: 'var(--text-secondary)', fontSize: 13.5 }}>{message}</p>
        </div>
        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>Annuler</button>
          <button className="btn btn-primary" style={{ background: 'var(--danger)', borderColor: 'var(--danger)' }} onClick={onConfirm}>
            Supprimer
          </button>
        </div>
      </div>
    </div>
  )
}

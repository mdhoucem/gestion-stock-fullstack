import { useState, useEffect } from 'react'
import { articleService, categorieService, commandeClientService, commandeFournisseurService } from '../services/api'
import { Loading } from '../components/UI'

export default function Dashboard() {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([
      articleService.getAll(),
      categorieService.getAll(),
      commandeClientService.getAll(),
      commandeFournisseurService.getAll(),
    ]).then(([articles, categories, commandesClient, commandesFourn]) => {
      setStats({ articles, categories, commandesClient, commandesFourn })
      setLoading(false)
    }).catch(() => setLoading(false))
  }, [])

  if (loading) return (
    <div className="main-content">
      <div className="page-header"><h2>Tableau de bord</h2></div>
      <div className="page-body"><Loading /></div>
    </div>
  )

  const articlesStockFaible = stats?.articles.filter(a => a.quantiteEnStock < 5) || []
  const commandesEnCours = stats?.commandesClient.filter(c => c.etatCommande === 'EN_COURS_DE_PREPARATION') || []

  return (
    <div className="main-content">
      <div className="page-header">
        <h2>Tableau de bord</h2>
        <p>Vue d'ensemble de votre stock</p>
      </div>
      <div className="page-body">

        {/* Stats */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-label">Articles</div>
            <div className="stat-value">{stats?.articles.length ?? 0}</div>
            <div className="stat-sub">dans le catalogue</div>
          </div>
          <div className="stat-card">
            <div className="stat-label">Catégories</div>
            <div className="stat-value">{stats?.categories.length ?? 0}</div>
            <div className="stat-sub">familles d'articles</div>
          </div>
          <div className="stat-card">
            <div className="stat-label">Cmd. clients</div>
            <div className="stat-value">{stats?.commandesClient.length ?? 0}</div>
            <div className="stat-sub">{commandesEnCours.length} en préparation</div>
          </div>
          <div className="stat-card">
            <div className="stat-label">Cmd. fournisseurs</div>
            <div className="stat-value">{stats?.commandesFourn.length ?? 0}</div>
            <div className="stat-sub">commandes passées</div>
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>

          {/* Stock faible */}
          <div className="card">
            <div className="card-header">
              <span className="card-title">⚠ Stock faible</span>
              <span className="badge badge-yellow">{articlesStockFaible.length}</span>
            </div>
            {articlesStockFaible.length === 0 ? (
              <div style={{ padding: '24px', color: 'var(--text-muted)', fontSize: 13, textAlign: 'center' }}>
                Tout le stock est suffisant ✓
              </div>
            ) : (
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Article</th>
                      <th>Qté en stock</th>
                    </tr>
                  </thead>
                  <tbody>
                    {articlesStockFaible.map(a => (
                      <tr key={a.id}>
                        <td>{a.nom}</td>
                        <td>
                          <span className="badge badge-red">{a.quantiteEnStock}</span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>

          {/* Commandes récentes */}
          <div className="card">
            <div className="card-header">
              <span className="card-title">Commandes récentes</span>
            </div>
            {(stats?.commandesClient.length === 0) ? (
              <div style={{ padding: '24px', color: 'var(--text-muted)', fontSize: 13, textAlign: 'center' }}>
                Aucune commande client
              </div>
            ) : (
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Client</th>
                      <th>Date</th>
                      <th>État</th>
                    </tr>
                  </thead>
                  <tbody>
                    {stats?.commandesClient.slice(-5).reverse().map(c => (
                      <tr key={c.id}>
                        <td>{c.nomClient}</td>
                        <td style={{ color: 'var(--text-muted)', fontSize: 12 }}>{c.dateCommande}</td>
                        <td>
                          <span className={`badge ${
                            c.etatCommande === 'LIVREE' ? 'badge-green' :
                            c.etatCommande === 'VALIDEE' ? 'badge-blue' : 'badge-yellow'
                          }`}>
                            {c.etatCommande === 'EN_COURS_DE_PREPARATION' ? 'En cours' :
                             c.etatCommande === 'VALIDEE' ? 'Validée' : 'Livrée'}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

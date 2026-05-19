import { useLocation, useNavigate } from 'react-router-dom'

const navItems = [
  {
    section: 'Général',
    links: [
      { path: '/', icon: '◈', label: 'Tableau de bord' },
    ]
  },
  {
    section: 'Catalogue',
    links: [
      { path: '/categories', icon: '⊞', label: 'Catégories' },
      { path: '/articles', icon: '◻', label: 'Articles' },
    ]
  },
  {
    section: 'Commandes',
    links: [
      { path: '/commandes-clients', icon: '↗', label: 'Clients' },
      { path: '/commandes-fournisseurs', icon: '↙', label: 'Fournisseurs' },
    ]
  },
  {
    section: 'Stock',
    links: [
      { path: '/stock', icon: '▤', label: 'Mouvements' },
    ]
  },
]

export default function Sidebar() {
  const location = useLocation()
  const navigate = useNavigate()

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h1>StockApp</h1>
        <span>Gestion de stock</span>
      </div>
      <nav className="sidebar-nav">
        {navItems.map(section => (
          <div key={section.section}>
            <div className="nav-section-label">{section.section}</div>
            {section.links.map(link => (
              <button
                key={link.path}
                className={`nav-link ${location.pathname === link.path ? 'active' : ''}`}
                onClick={() => navigate(link.path)}
              >
                <span className="nav-icon">{link.icon}</span>
                {link.label}
              </button>
            ))}
          </div>
        ))}
      </nav>
    </aside>
  )
}

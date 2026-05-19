import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Sidebar from './components/Sidebar'
import Dashboard from './pages/Dashboard'
import Categories from './pages/Categories'
import Articles from './pages/Articles'
import CommandesClients from './pages/CommandesClients'
import CommandesFournisseurs from './pages/CommandesFournisseurs'
import Stock from './pages/Stock'

export default function App() {
  return (
    <BrowserRouter>
      <div className="app-layout">
        <Sidebar />
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/categories" element={<Categories />} />
          <Route path="/articles" element={<Articles />} />
          <Route path="/commandes-clients" element={<CommandesClients />} />
          <Route path="/commandes-fournisseurs" element={<CommandesFournisseurs />} />
          <Route path="/stock" element={<Stock />} />
        </Routes>
      </div>
    </BrowserRouter>
  )
}

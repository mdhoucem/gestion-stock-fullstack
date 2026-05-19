# 📦 Gestion de Stock

Application de gestion de stock — **Spring Boot 3** + **React 19** + **MySQL**

---

## Stack

| Backend | Frontend | Base de données |
|---------|----------|-----------------|
| Spring Boot 3 | React 19 + Vite | MySQL 8 |
| Spring Data JPA | Axios + React Router | - |
| Lombok | - | - |

---

## Prérequis

- Java 17+
- Node.js 18+
- MySQL 8+

---

## Installation

### Backend
```bash
# Ouvrir gestion-stock/ dans IntelliJ
# Maven télécharge les dépendances automatiquement
# Modifier le mot de passe dans application.properties si nécessaire
spring.datasource.password=2004
```

### Frontend
```bash
cd frontend
npm install
```

---

## Lancer

```bash
# 1. Démarrer MySQL

# 2. Spring Boot (IntelliJ ▶ ou terminal)
./mvnw spring-boot:run

# 3. React
cd frontend
npm run dev
```

- API : http://localhost:8080
- App : http://localhost:3000

---

## Fonctionnalités

- ✅ Catégories & Articles
- ✅ Commandes clients & fournisseurs
- ✅ États commande : `EN_COURS` → `VALIDEE` → `LIVREE`
- ✅ Mouvements de stock (entrée / sortie / correction)
- ✅ Calcul du stock réel par article

---

## API

| Module | Base URL |
|--------|----------|
| Catégories | `/api/categories` |
| Articles | `/api/articles` |
| Commandes clients | `/api/commandes-clients` |
| Commandes fournisseurs | `/api/commandes-fournisseurs` |
| Stock | `/api/mouvements-stock` |

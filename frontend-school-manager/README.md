## INSTALLATION DE DEPENDANCES:
📦 2. Installer des dépendances utiles (recommandé)
npm install bootstrap
npm install primeicons
npm install primeng

🔹 Gestion des requêtes HTTP (optionnel mais utile)
npm install axios

🔹 Gestion d’état (optionnel avancé)
npm install @ngrx/store



src/
├── app/
│   ├── core/                          # Services et composants singletons
│   │   ├── guards/                    # Guards d'authentification
│   │   │   ├── auth.guard.ts
│   │   │   └── role.guard.ts
│   │   ├── interceptors/              # Intercepteurs HTTP
│   │   │   ├── auth.interceptor.ts
│   │   │   └── error.interceptor.ts
│   │   ├── services/                  # Services globaux
│   │   │   ├── auth.service.ts
│   │   │   ├── api.service.ts
│   │   │   └── notification.service.ts
│   │   ├── models/                    # Modèles globaux
│   │   │   ├── user.model.ts
│   │   │   └── api-response.model.ts
│   │   ├── constants/                 # Constantes globales
│   │   │   ├── app.constants.ts
│   │   │   └── api.constants.ts
│   │   └── core.module.ts             # Module Core
│   │
│   ├── shared/                        # Composants réutilisables
│   │   ├── components/                # Composants partagés
│   │   │   ├── header/
│   │   │   │   ├── header.component.ts
│   │   │   │   ├── header.component.html
│   │   │   │   ├── header.component.css
│   │   │   │   └── header.component.spec.ts
│   │   │   ├── footer/
│   │   │   ├── sidebar/
│   │   │   ├── modal/
│   │   │   └── loading-spinner/
│   │   ├── directives/                # Directives personnalisées
│   │   │   ├── highlight.directive.ts
│   │   │   └── permission.directive.ts
│   │   ├── pipes/                     # Pipes personnalisés
│   │   │   ├── truncate.pipe.ts
│   │   │   └── date-format.pipe.ts
│   │   ├── validators/                # Validateurs personnalisés
│   │   │   └── custom.validators.ts
│   │   └── shared.module.ts           # Module Shared
│   │
│   ├── features/                      # Modules fonctionnels (lazy loading)
│   │   ├── auth/
│   │   │   ├── pages/
│   │   │   │   ├── login/
│   │   │   │   │   ├── login.component.ts
│   │   │   │   │   ├── login.component.html
│   │   │   │   │   ├── login.component.css
│   │   │   │   │   └── login.component.spec.ts
│   │   │   │   └── register/
│   │   │   ├── services/
│   │   │   │   └── auth-feature.service.ts
│   │   │   ├── models/
│   │   │   │   └── auth.model.ts
│   │   │   ├── auth-routing.module.ts
│   │   │   └── auth.module.ts
│   │   │
│   │   ├── dashboard/
│   │   │   ├── pages/
│   │   │   │   ├── dashboard.component.ts
│   │   │   │   └── dashboard.component.html
│   │   │   ├── components/
│   │   │   │   ├── stats-card/
│   │   │   │   └── chart-widget/
│   │   │   ├── services/
│   │   │   ├── models/
│   │   │   ├── dashboard-routing.module.ts
│   │   │   └── dashboard.module.ts
│   │   │
│   │   └── users/
│   │       ├── pages/
│   │       │   ├── user-list/
│   │       │   ├── user-detail/
│   │       │   └── user-form/
│   │       ├── components/
│   │       │   └── user-card/
│   │       ├── services/
│   │       │   └── user.service.ts
│   │       ├── models/
│   │       │   └── user.model.ts
│   │       ├── resolvers/
│   │       │   └── user.resolver.ts
│   │       ├── users-routing.module.ts
│   │       └── users.module.ts
│   │
│   ├── layouts/                       # Layouts de l'application
│   │   ├── default-layout/
│   │   │   ├── default-layout.component.ts
│   │   │   ├── default-layout.component.html
│   │   │   └── default-layout.component.css
│   │   └── auth-layout/
│   │       ├── auth-layout.component.ts
│   │       └── auth-layout.component.html
│   │
│   ├── app-routing.module.ts          # Routing principal
│   ├── app.component.ts               # Composant racine
│   ├── app.component.html
│   ├── app.component.css
│   ├── app.module.ts                  # Module principal
│   │
│   ├── environments/                  # Configuration environnement
│   │   ├── environment.ts
│   │   ├── environment.prod.ts
│   │   └── environment.staging.ts
│   │
│   ├── assets/                        # Ressources statiques
│   │   ├── images/
│   │   ├── icons/
│   │   └── fonts/
│   │
│   └── styles/                        # Styles globaux
│       ├── variables.scss
│       ├── mixins.scss
│       ├── global.scss
│       └── themes/
│           └── dark-theme.scss
│
├── .env                               # Variables d'environnement
├── .eslintrc.json                     # Configuration ESLint
├── .prettierrc                        # Configuration Prettier
├── angular.json                       # Configuration Angular
├── package.json
├── tsconfig.json
└── README.md

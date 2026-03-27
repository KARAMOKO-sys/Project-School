import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home';
import { PricingComponent } from './pages/pricing/pricing';
import { WebDevelopmentComponent } from './pages/web-development/web-development';
import { UserResearchComponent } from './pages/user-research/user-research';
import { SignupComponent } from './pages/auth/signup/signup';
import { SearchComponent } from './pages/search/search';


export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { 
    path: 'home', 
    component: HomeComponent  // Utilisation directe du composant
  },
  { 
    path: 'pricing', 
    component: PricingComponent
  },
  { 
    path: 'web-development', 
    component: WebDevelopmentComponent
  },
  { 
    path: 'user-research', 
    component: UserResearchComponent
  },
  { 
    path: 'auth/signup', 
    component: SignupComponent
  },
  { 
    path: 'search', 
    component: SearchComponent
  },
  { path: '**', redirectTo: '/home' }
];
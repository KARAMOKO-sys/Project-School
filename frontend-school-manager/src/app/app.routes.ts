import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home';
import { PricingComponent } from './pages/pricing/pricing';
import { WebDevelopmentComponent } from './pages/web-development/web-development';
import { UserResearchComponent } from './pages/user-research/user-research';
import { SignupComponent } from './pages/auth/signup/signup';
import { SearchComponent } from './pages/search/search';
import { StudentDashboardComponent } from './pages/dashboard/pages/student-dashboard.component';
import { RoleGuard } from './core/guards/role.guard';
import { AuthGuard } from './core/guards/auth.guard';


export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  // Dashboard Student
  {
    path: 'dashboard/student',
    component: StudentDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['STUDENT_SIMPLE', 'STUDENT'] }
  },
  
  // Dashboard Teacher
  /*
  {
    path: 'dashboard/teacher',
    component: TeacherDashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['TEACHER_SIMPLE', 'TEACHER'] }
  },
  */
  { 
    path: 'home', 
    component: HomeComponent  
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
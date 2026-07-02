// core/guards/role.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const expectedRoles = route.data['roles'] as string[];
    const user = this.authService.getCurrentUser();
    
    if (!user) {
      this.router.navigate(['/login']);
      return false;
    }

    if (expectedRoles && expectedRoles.includes(user.role)) {
      return true;
    }

    // Rediriger vers le bon dashboard selon le rôle
    if (this.authService.isStudent()) {
      this.router.navigate(['/dashboard/student']);
    } else if (this.authService.isTeacher()) {
      this.router.navigate(['/dashboard/teacher']);
    } else {
      this.router.navigate(['/home']);
    }
    return false;
  }
}
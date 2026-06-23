// shared/components/header/header.component.ts
import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { Router, NavigationEnd, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { SignupModalComponent } from '../../../pages/auth/signup/signup-modal.component';
import { AuthService, User } from '../../../core/services/auth.service';

interface MenuItem {
  label: string;
  route: string;
  active?: boolean;
  icon?: string;
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, FormsModule, SignupModalComponent],
  templateUrl: './header.html',
  styleUrls: ['./header.scss'],
})
export class HeaderComponent implements OnInit, OnDestroy {
  logoUrl = 'assets/logo-n.png';
  searchQuery = '';
  isScrolled = false;
  currentRoute = '';
  showSignupModal = false; // ✅ Contrôle l'affichage de la modal

  // États d'authentification
  isAuthenticated = false;
  currentUser: User | null = null;
  userRole: string = '';
  userFullName: string = '';
  userInitials: string = '';

  private authSubscription: Subscription | null = null;

  public router: Router;

  menuItems: MenuItem[] = [
    { label: 'Accueil', route: '/home' },
    { label: 'Cours', route: '/courses' },
    { label: 'Tarifs', route: '/pricing' },
    { label: 'Développement Web', route: '/web-development' },
    { label: 'User Research', route: '/user-research' },
  ];

  constructor(
    router: Router,
    private authService: AuthService,
  ) {
    this.router = router;
  }

  ngOnInit(): void {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.urlAfterRedirects;
      });

    this.authSubscription = this.authService.currentUser$.subscribe((user: User | null) => {
      this.isAuthenticated = !!user;
      this.currentUser = user;

      if (user) {
        this.userRole = user.role || 'student';
        this.userFullName = user.fullName || `${user.firstName} ${user.lastName}`;
        this.userInitials = this.getInitials(user.firstName, user.lastName);
      } else {
        this.userRole = '';
        this.userFullName = '';
        this.userInitials = '';
      }
    });
  }

  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 50;
  }

  isActive(route: string): boolean {
    if (route === '/home' || route === '/') {
      return this.currentRoute === '/' || this.currentRoute === '/home' || this.currentRoute === '';
    }
    return this.currentRoute.startsWith(route);
  }

  onSearch(query: string): void {
    if (query && query.trim()) {
      console.log('Recherche:', query);
      this.router.navigate(['/search'], { queryParams: { q: query } });
      this.searchQuery = '';

      const dropdownButton = document.getElementById('dropdownMenuButton1');
      if (dropdownButton) {
        const bootstrap = (window as any).bootstrap;
        if (bootstrap && bootstrap.Dropdown) {
          const dropdown = bootstrap.Dropdown.getInstance(dropdownButton);
          if (dropdown) {
            dropdown.hide();
          }
        }
      }
    }
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }

  // ✅ Méthode pour ouvrir la modal (appelée depuis le bouton Sign Up)
  openSignupModal(): void {
    console.log("🔓 Ouverture de la modal d'inscription");
    this.showSignupModal = true;
    document.body.style.overflow = 'hidden';
  }

  // ✅ Méthode pour fermer la modal
  closeSignupModal(): void {
    console.log("🔒 Fermeture de la modal d'inscription");
    this.showSignupModal = false;
    document.body.style.overflow = '';
  }

  // ✅ Gestion du succès d'inscription
  onSignupSuccess(userData: any): void {
    console.log('✅ Inscription réussie:', userData);
    this.closeSignupModal();

    if (userData?.role === 'teacher' || userData?.role === 'TEACHER_SIMPLE') {
      this.router.navigate(['/teacher/dashboard']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  // ✅ Méthode pour aller au tableau de bord
  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  // ✅ Méthode pour aller au profil
  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/home']);
  }

  getInitials(firstName: string, lastName: string): string {
    if (!firstName && !lastName) return '?';
    const first = firstName ? firstName.charAt(0).toUpperCase() : '';
    const last = lastName ? lastName.charAt(0).toUpperCase() : '';
    return first + last;
  }

  getRoleLabel(role: string): string {
    const roleMap: { [key: string]: string } = {
      student: 'Étudiant',
      teacher: 'Enseignant',
      admin: 'Administrateur',
      support: 'Support',
    };
    return roleMap[role] || role;
  }

  getAvatarUrl(): string {
    if (this.currentUser?.profilePictureUrl) {
      return this.currentUser.profilePictureUrl;
    }
    return `https://ui-avatars.com/api/?name=${this.userFullName}&background=ffb400&color=1a1a1a&size=100&font-size=0.5`;
  }

  // ✅ Gestion de l'erreur d'image
  onImageError(): void {
    if (this.currentUser) {
      this.currentUser.profilePictureUrl = undefined;
    }
  }

  // ✅ Alias pour openSignupModal (pour la compatibilité)
  onSignUp(): void {
    this.openSignupModal();
  }
}
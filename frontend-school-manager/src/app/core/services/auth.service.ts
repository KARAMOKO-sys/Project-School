// core/services/auth.service.ts
import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { StorageService } from './storage.service';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { Router } from '@angular/router';

// ==================== EXPORTS ====================
export interface User {
  uuid: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  fullName?: string;
  profilePictureUrl?: string;
}

export type StudentLevel = 'ELEVE' | 'COLLEGIEN' | 'LYCEEN' | 'ETUDIANT' | 'PROFESSIONEL';
export type TeacherLevel =
  | 'OTHER'
  | 'MIDDLE'
  | 'ASSISTANT'
  | 'JUNIOR'
  | 'SENIOR'
  | 'EXPERT'
  | 'PROFESSOR';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  data: {
    token: string;
    tokenType: string;
    expiresIn: number;
    uuid: string;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
  };
}

export interface StudentSimpleRegisterRequest {
  firstName: string;
  lastName: string;
  statutUserSimple: 'ELEVE';
  levelStudent: StudentLevel;
}

export interface TeacherSimpleRegisterRequest {
  firstName: string;
  lastName: string;
  statutUserSimple: 'ENSEIGNANT';
  levelTeacher: TeacherLevel;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private apiService: ApiService,
    private storageService: StorageService,
    private router: Router,
  ) {
    this.loadStoredUser();
  }

  // ==================== AUTHENTIFICATION ====================

  /**
   * Connexion de l'utilisateur
   */
  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.apiService.post<LoginResponse>('api/auth/login', credentials).pipe(
      tap((response) => {
        if (response.success && response.data) {
          this.handleAuthSuccess(response.data);
        }
      }),
    );
  }

  /**
   * Inscription simplifiée étudiant
   */
  registerStudentSimple(data: StudentSimpleRegisterRequest): Observable<any> {
    return this.apiService.post('api/students/register-simple', data);
  }

  /**
   * Inscription simplifiée enseignant
   */
  registerTeacherSimple(data: TeacherSimpleRegisterRequest): Observable<any> {
    return this.apiService.post('api/teachers-simple/register-simple', data);
  }

  /**
   * ✅ Méthode PUBLIQUE pour connecter l'utilisateur après inscription
   */
  public loginAfterRegistration(userData: any): void {
    this.handleAuthSuccess(userData);
  }

  /**
   * Déconnexion
   */
  logout(): void {
    this.storageService.removeItem('access_token');
    this.storageService.removeItem('user');
    this.currentUserSubject.next(null);
    this.router.navigate(['/']);
  }

  // ==================== GESTION DU TOKEN ====================

  /**
   * Récupère le token d'accès
   */
  getToken(): string | null {
    return this.storageService.getItem('access_token');
  }

  /**
   * Vérifie si l'utilisateur est authentifié
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /**
   * Ajoute le token aux headers
   */
  getAuthHeaders(): { Authorization: string } | {} {
    const token = this.getToken();
    if (token) {
      return { Authorization: `Bearer ${token}` };
    }
    return {};
  }

  // ==================== GESTION DE L'UTILISATEUR ====================

  /**
   * Récupère l'utilisateur courant
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Récupère le nom complet de l'utilisateur
   */
  getUserFullName(): string {
    const user = this.getCurrentUser();
    return (
      user?.fullName || `${user?.firstName || ''} ${user?.lastName || ''}`.trim() || 'Utilisateur'
    );
  }

  /**
   * Récupère le rôle de l'utilisateur
   */
  getUserRole(): string {
    const user = this.getCurrentUser();
    return user?.role || '';
  }

  /**
   * Vérifie si l'utilisateur a un rôle spécifique
   */
  hasRole(role: string): boolean {
    return this.getUserRole() === role;
  }

  /**
   * Vérifie si l'utilisateur est un étudiant
   */
  isStudent(): boolean {
    return this.hasRole('STUDENT_SIMPLE') || this.hasRole('student');
  }

  /**
   * Vérifie si l'utilisateur est un enseignant
   */
  isTeacher(): boolean {
    return this.hasRole('TEACHER_SIMPLE') || this.hasRole('teacher');
  }

  /**
   * Vérifie si l'utilisateur est un administrateur
   */
  isAdmin(): boolean {
    return this.hasRole('ADMINISTRATOR') || this.hasRole('admin');
  }

  /**
   * Vérifie si l'utilisateur est un support agent
   */
  isSupportAgent(): boolean {
    return this.hasRole('SUPPORT_AGENT') || this.hasRole('support');
  }

  // ==================== MÉTHODES PRIVÉES ====================

  /**
   * Gère le succès de l'authentification
   */
  public handleAuthSuccess(userData: any): void {
    // Stocker le token
    if (userData.token) {
      this.storageService.setItem('access_token', userData.token);
    }

    // Créer l'objet utilisateur
    const user: User = {
      uuid: userData.uuid,
      email: userData.email,
      firstName: userData.firstName,
      lastName: userData.lastName,
      role: userData.role,
      fullName: `${userData.firstName} ${userData.lastName}`,
      profilePictureUrl: userData.profilePictureUrl || undefined,
    };

    // Stocker l'utilisateur
    this.storageService.setItem('user', user);
    this.currentUserSubject.next(user);
  }

  /**
   * Charge l'utilisateur stocké
   */
  private loadStoredUser(): void {
    const user = this.storageService.getItem<User>('user');
    if (user && this.getToken()) {
      this.currentUserSubject.next(user);
    } else {
      // Si pas d'utilisateur ou de token, on déconnecte
      this.currentUserSubject.next(null);
    }
  }

  // ==================== UTILITAIRES ====================

  /**
   * Met à jour les informations utilisateur (après modification du profil)
   */
  updateUser(userData: Partial<User>): void {
    const currentUser = this.getCurrentUser();
    if (currentUser) {
      const updatedUser = { ...currentUser, ...userData };
      this.storageService.setItem('user', updatedUser);
      this.currentUserSubject.next(updatedUser);
    }
  }

  /**
   * Redirige vers la page de login
   */
  redirectToLogin(returnUrl?: string): void {
    this.router.navigate(['/login'], { queryParams: { returnUrl } });
  }
}

// core/services/user.service.ts
import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { AuthService, User } from './auth.service';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(
    private apiService: ApiService,
    private authService: AuthService,
  ) {}

  /**
   * Récupère les informations de l'utilisateur connecté
   */
  getCurrentUserProfile(): Observable<any> {
    const user = this.authService.getCurrentUser();
    if (!user?.uuid) {
      throw new Error('Utilisateur non connecté');
    }
    return this.apiService.get(`api/users/${user.uuid}`);
  }

  /**
   * Récupère un utilisateur par son UUID
   */
  getUserByUuid(uuid: string): Observable<any> {
    return this.apiService.get(`api/users/${uuid}`);
  }

  /**
   * Met à jour le profil de l'utilisateur
   */
  updateProfile(data: Partial<User>): Observable<any> {
    const user = this.authService.getCurrentUser();
    if (!user?.uuid) {
      throw new Error('Utilisateur non connecté');
    }
    return this.apiService.put(`api/users/${user.uuid}/profile`, data).pipe(
      tap((response: any) => {
        if (response.success) {
          this.authService.updateUser(data);
        }
      }),
    );
  }

  /**
   * Change le mot de passe
   */
  changePassword(currentPassword: string, newPassword: string): Observable<any> {
    const user = this.authService.getCurrentUser();
    if (!user?.uuid) {
      throw new Error('Utilisateur non connecté');
    }
    return this.apiService.post(`api/users/${user.uuid}/change-password`, {
      currentPassword,
      newPassword,
    });
  }

  /**
   * Upload de la photo de profil
   */
  uploadProfilePicture(file: File): Observable<any> {
    const user = this.authService.getCurrentUser();
    if (!user?.uuid) {
      throw new Error('Utilisateur non connecté');
    }
    return this.apiService.upload(`api/users/${user.uuid}/profile-picture`, file).pipe(
      tap((response: any) => {
        if (response.success && response.data?.profilePictureUrl) {
          this.authService.updateUser({ profilePictureUrl: response.data.profilePictureUrl });
        }
      }),
    );
  }

  /**
   * Supprime la photo de profil
   */
  deleteProfilePicture(): Observable<any> {
    const user = this.authService.getCurrentUser();
    if (!user?.uuid) {
      throw new Error('Utilisateur non connecté');
    }
    return this.apiService.delete(`api/users/${user.uuid}/profile-picture`).pipe(
      tap((response: any) => {
        if (response.success) {
          this.authService.updateUser({ profilePictureUrl: '' });
        }
      }),
    );
  }
}

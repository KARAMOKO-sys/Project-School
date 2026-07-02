// features/auth/components/signup-modal/signup-modal.component.ts
import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

// Définir les types
type StudentLevel = 'ELEVE' | 'COLLEGIEN' | 'LYCEEN' | 'ETUDIANT' | 'PROFESSIONEL';
type TeacherLevel = 'OTHER' | 'MIDDLE' | 'ASSISTANT' | 'JUNIOR' | 'SENIOR' | 'EXPERT' | 'PROFESSOR';

@Component({
  selector: 'app-signup-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-overlay" (click)="closeModal()">
      <div class="modal-container" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h3>Inscription rapide</h3>
          <button class="close-btn" (click)="closeModal()">&times;</button>
        </div>
        <div class="modal-body">
          <!-- Sélection du rôle -->
          <div class="form-group">
            <label>Je suis :</label>
            <div class="role-selector">
              <button
                class="role-btn"
                [class.active]="userRole === 'student'"
                (click)="userRole = 'student'; resetForm()"
              >
                <i class="fas fa-user-graduate me-2"></i>Étudiant
              </button>
              <button
                class="role-btn"
                [class.active]="userRole === 'teacher'"
                (click)="userRole = 'teacher'; resetForm()"
              >
                <i class="fas fa-chalkboard-teacher me-2"></i>Enseignant
              </button>
            </div>
          </div>

          <!-- Message d'erreur -->
          <div *ngIf="errorMessage" class="alert alert-danger">
            <i class="fas fa-exclamation-circle me-2"></i>
            {{ errorMessage }}
          </div>

          <!-- Message d'information -->
          <div class="info-box">
            <i class="fas fa-info-circle me-2"></i>
            <span>
              Inscription simplifiée. Un email temporaire sera généré automatiquement. Vous serez
              connecté immédiatement après l'inscription.
            </span>
          </div>

          <!-- Formulaire -->
          <form (ngSubmit)="onSubmit()" #signupForm="ngForm">
            <!-- Prénom -->
            <div class="form-group">
              <label for="firstName">Prénom <span class="text-danger">*</span></label>
              <input
                type="text"
                id="firstName"
                class="form-control"
                [(ngModel)]="user.firstName"
                name="firstName"
                required
                placeholder="Votre prénom"
                autofocus
              />
            </div>

            <!-- Nom -->
            <div class="form-group">
              <label for="lastName">Nom <span class="text-danger">*</span></label>
              <input
                type="text"
                id="lastName"
                class="form-control"
                [(ngModel)]="user.lastName"
                name="lastName"
                required
                placeholder="Votre nom"
              />
            </div>

            <!-- ==================== CHAMPS SPÉCIFIQUES ÉTUDIANT ==================== -->
            <ng-container *ngIf="userRole === 'student'">
              <div class="form-group">
                <label for="levelStudent">Niveau <span class="text-danger">*</span></label>
                <select
                  id="levelStudent"
                  class="form-control"
                  [(ngModel)]="user.levelStudent"
                  name="levelStudent"
                  required
                >
                  <option value="">-- Sélectionnez votre niveau --</option>
                  <option value="DEBUTANT">Débutant</option>
                  <option value="INTERMEDIAIRE">Intermédiaire</option>
                  <option value="AVANCE">Avancé</option>
                  <option value="EXPERT">Expert</option>
                </select>
              </div>

              <!-- Info niveau -->
              <div class="info-box" *ngIf="user.levelStudent">
                <i class="fas fa-graduation-cap me-2"></i>
                <span>
                  Niveau sélectionné :
                  <strong>{{ getStudentLevelLabel(user.levelStudent) }}</strong>
                </span>
              </div>
            </ng-container>

            <!-- ==================== CHAMPS SPÉCIFIQUES ENSEIGNANT ==================== -->
            <ng-container *ngIf="userRole === 'teacher'">
              <div class="form-group">
                <label for="levelTeacher"
                  >Niveau d'enseignement <span class="text-danger">*</span></label
                >
                <select
                  id="levelTeacher"
                  class="form-control"
                  [(ngModel)]="user.levelTeacher"
                  name="levelTeacher"
                  required
                >
                  <option value="">-- Sélectionnez votre niveau --</option>
                  <option value="DEBUTANT">Débutant</option>
                  <option value="INTERMEDIAIRE">Intermédiaire</option>
                  <option value="CONFIRME">Confirmé</option>
                  <option value="EXPERIMENTE">Expérimenté</option>
                  <option value="SENIOR">Senior</option>
                  <option value="EXPERT">Expert</option>
                </select>
              </div>

              <!-- Info enseignant -->
              <div class="info-box" *ngIf="user.levelTeacher">
                <i class="fas fa-chalkboard-teacher me-2"></i>
                <span
                  >Niveau sélectionné :
                  <strong>{{ getTeacherLevelLabel(user.levelTeacher) }}</strong></span
                >
              </div>
            </ng-container>

            <!-- Bouton de soumission -->
            <button type="submit" class="btn-submit" [disabled]="isLoading || !signupForm.valid">
              <span *ngIf="!isLoading">
                <i class="fas fa-user-plus me-2"></i>
                {{
                  userRole === 'student'
                    ? 'Créer mon compte étudiant'
                    : 'Créer mon compte enseignant'
                }}
              </span>
              <span *ngIf="isLoading">
                <i class="fas fa-spinner fa-spin me-2"></i>
                Inscription en cours...
              </span>
            </button>

            <!-- Lien vers la connexion -->
            <div class="login-link">
              <span>Vous avez déjà un compte ?</span>
              <a (click)="goToLogin()">Se connecter</a>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .modal-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.6);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
        animation: fadeIn 0.3s ease-in;
      }

      @keyframes fadeIn {
        from {
          opacity: 0;
        }
        to {
          opacity: 1;
        }
      }

      .modal-container {
        background: white;
        border-radius: 12px;
        max-width: 480px;
        width: 95%;
        max-height: 90vh;
        overflow-y: auto;
        animation: slideUp 0.3s ease-out;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      }

      @keyframes slideUp {
        from {
          transform: translateY(50px);
          opacity: 0;
        }
        to {
          transform: translateY(0);
          opacity: 1;
        }
      }

      .modal-header {
        padding: 20px 25px;
        border-bottom: 1px solid #e9ecef;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: #f8f9fa;
        border-radius: 12px 12px 0 0;
      }

      .modal-header h3 {
        margin: 0;
        color: #2c3e50;
        font-weight: 600;
      }

      .close-btn {
        background: none;
        border: none;
        font-size: 28px;
        color: #6c757d;
        cursor: pointer;
        transition: color 0.2s;
        padding: 0 5px;
      }

      .close-btn:hover {
        color: #dc3545;
      }

      .modal-body {
        padding: 25px;
      }

      .form-group {
        margin-bottom: 18px;
      }

      .form-group label {
        display: block;
        margin-bottom: 6px;
        color: #495057;
        font-weight: 500;
        font-size: 14px;
      }

      .text-danger {
        color: #dc3545;
      }

      .form-control {
        width: 100%;
        padding: 10px 14px;
        border: 2px solid #dee2e6;
        border-radius: 8px;
        font-size: 14px;
        transition:
          border-color 0.2s,
          box-shadow 0.2s;
        box-sizing: border-box;
        background: white;
      }

      .form-control:focus {
        outline: none;
        border-color: #ffb400;
        box-shadow: 0 0 0 3px rgba(255, 180, 0, 0.1);
      }

      .form-control:disabled {
        background: #f8f9fa;
        cursor: not-allowed;
      }

      .role-selector {
        display: flex;
        gap: 10px;
      }

      .role-btn {
        flex: 1;
        padding: 12px;
        border: 2px solid #dee2e6;
        border-radius: 8px;
        background: white;
        cursor: pointer;
        transition: all 0.3s;
        font-weight: 500;
        font-size: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
      }

      .role-btn.active {
        border-color: #ffb400;
        background: #fff8e1;
        color: #1a1a1a;
      }

      .role-btn:hover {
        border-color: #ffb400;
        transform: translateY(-2px);
      }

      .role-btn i {
        font-size: 16px;
      }

      .btn-submit {
        width: 100%;
        padding: 12px;
        background: #ffb400;
        border: none;
        border-radius: 8px;
        color: #1a1a1a;
        font-weight: 600;
        font-size: 16px;
        cursor: pointer;
        transition: all 0.3s;
        margin-top: 10px;
      }

      .btn-submit:hover:not(:disabled) {
        background: #e6a200;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(255, 180, 0, 0.3);
      }

      .btn-submit:disabled {
        opacity: 0.6;
        cursor: not-allowed;
        transform: none;
      }

      .alert {
        padding: 12px 16px;
        border-radius: 8px;
        margin-bottom: 16px;
      }

      .alert-danger {
        background: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
      }

      .info-box {
        background: #fff8e1;
        border: 1px solid #ffb400;
        border-radius: 8px;
        padding: 10px 14px;
        font-size: 13px;
        color: #1a1a1a;
        margin-bottom: 16px;
      }

      .info-box i {
        color: #ffb400;
      }

      .info-box .d-block {
        display: block;
      }

      .info-box .mt-1 {
        margin-top: 4px;
      }

      .login-link {
        text-align: center;
        margin-top: 16px;
        font-size: 14px;
        color: #6c757d;
      }

      .login-link a {
        color: #ffb400;
        cursor: pointer;
        font-weight: 600;
        margin-left: 5px;
        text-decoration: none;
      }

      .login-link a:hover {
        text-decoration: underline;
      }

      /* Scrollbar personnalisée */
      .modal-container::-webkit-scrollbar {
        width: 6px;
      }

      .modal-container::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 10px;
      }

      .modal-container::-webkit-scrollbar-thumb {
        background: #ffb400;
        border-radius: 10px;
      }

      .modal-container::-webkit-scrollbar-thumb:hover {
        background: #e6a200;
      }

      /* Responsive */
      @media (max-width: 576px) {
        .modal-container {
          max-width: 98%;
          border-radius: 8px;
        }

        .modal-header {
          padding: 15px 20px;
        }

        .modal-body {
          padding: 20px;
        }

        .role-btn {
          font-size: 13px;
          padding: 10px;
        }

        .role-btn i {
          font-size: 14px;
        }
      }
    `,
  ],
})
export class SignupModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() signupSuccess = new EventEmitter<any>();

  userRole: 'student' | 'teacher' = 'student';
  isLoading = false;
  errorMessage = '';

  // Interface utilisateur du formulaire (simplifiée)
  user = {
    firstName: '',
    lastName: '',
    // Champs spécifiques étudiant
    levelStudent: '' as StudentLevel | '',
    // Champs spécifiques enseignant
    levelTeacher: '' as TeacherLevel | '',
  };

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  // ==================== MÉTHODES UTILITAIRES ====================

  getStudentLevelLabel(level: string): string {
    const levelMap: Record<string, string> = {
      DEBUTANT: 'Débutant',
      INTERMEDIAIRE: 'Intermédiaire',
      AVANCE: 'Avancé',
      EXPERT: 'Expert',
    };
    return levelMap[level] || level;
  }

  getTeacherLevelLabel(level: string): string {
    const levelMap: Record<string, string> = {
      DEBUTANT: 'Débutant',
      INTERMEDIAIRE: 'Intermédiaire',
      CONFIRME: 'Confirmé',
      EXPERIMENTE: 'Expérimenté',
      SENIOR: 'Senior',
      EXPERT: 'Expert',
    };
    return levelMap[level] || level;
  }

  resetForm(): void {
    // Réinitialiser les champs spécifiques lors du changement de rôle
    if (this.userRole === 'student') {
      this.user.levelTeacher = '';
    } else {
      this.user.levelStudent = '';
    }
    this.errorMessage = '';
  }

  goToLogin(): void {
    this.closeModal();
    this.router.navigate(['/login']);
  }

  // ==================== SOUMISSION DU FORMULAIRE ====================

  onSubmit() {
    // Validation du prénom
    if (!this.user.firstName || this.user.firstName.trim().length < 2) {
      this.errorMessage = 'Le prénom doit contenir au moins 2 caractères';
      return;
    }

    // Validation du nom
    if (!this.user.lastName || this.user.lastName.trim().length < 2) {
      this.errorMessage = 'Le nom doit contenir au moins 2 caractères';
      return;
    }

    // Validation du niveau pour l'étudiant
    if (this.userRole === 'student' && !this.user.levelStudent) {
      this.errorMessage = "Veuillez sélectionner votre niveau d'études";
      return;
    }

    // Validation du niveau pour l'enseignant
    if (this.userRole === 'teacher' && !this.user.levelTeacher) {
      this.errorMessage = "Veuillez sélectionner votre niveau d'enseignement";
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    // Construction des données d'inscription selon le rôle
    let registrationData: any;

    if (this.userRole === 'student') {
      // 🔥 Données pour l'inscription étudiant simplifiée
      registrationData = {
        firstName: this.user.firstName.trim(),
        lastName: this.user.lastName.trim(),
        statutUserSimple: 'EN_ATTENTE',  // ← Utiliser EN_ATTENTE au lieu de ELEVE
        levelStudent: this.user.levelStudent,
      };
    } else {
      // 🔥 Données pour l'inscription enseignant simplifiée
      registrationData = {
        firstName: this.user.firstName.trim(),
        lastName: this.user.lastName.trim(),
        statutUserSimple: 'EN_ATTENTE',  // ← Utiliser EN_ATTENTE au lieu de ENSEIGNANT
        levelTeacher: this.user.levelTeacher,
      };
    }

    console.log("📝 Données d'inscription:", registrationData);

    // Appel du service approprié
    const registerObservable =
      this.userRole === 'student'
        ? this.authService.registerStudentSimple(registrationData)
        : this.authService.registerTeacherSimple(registrationData);

    registerObservable.subscribe({
      next: (response) => {
        this.isLoading = false;
        console.log('✅ Réponse inscription:', response);

        if (response.success) {
          // Stocker le token et les infos utilisateur
          if (response.data?.token) {
            this.authService.handleAuthSuccess({
              token: response.data.token,
              tokenType: response.data.tokenType || 'Bearer',
              uuid: response.data.uuid,
              email: response.data.email,
              firstName: response.data.firstName,
              lastName: response.data.lastName,
              role: this.userRole === 'student' ? 'STUDENT_SIMPLE' : 'TEACHER_SIMPLE',
            });
          }

          this.signupSuccess.emit(response.data);
          this.closeModal();

          // Message de succès
          alert(
            `✅ Bienvenue ${this.user.firstName} !\n\n` +
              `Votre compte ${this.userRole === 'student' ? 'étudiant' : 'enseignant'} a été créé avec succès.\n` +
              `Email généré : ${response.data?.email || 'non disponible'}\n` +
              `Vous êtes maintenant connecté(e).`,
          );
        } else {
          this.errorMessage = response.message || "Erreur lors de l'inscription";
        }
      },
      error: (error) => {
        this.isLoading = false;
        console.error("❌ Erreur d'inscription:", error);

        // Gestion des erreurs spécifiques
        if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else if (error.status === 409) {
          this.errorMessage = 'Un compte avec ces informations existe déjà.';
        } else if (error.status === 400) {
          this.errorMessage = 'Données invalides. Veuillez vérifier vos informations.';
        } else {
          this.errorMessage = "Une erreur est survenue lors de l'inscription.";
        }
      },
    });
  }

  closeModal() {
    this.close.emit();
  }
}
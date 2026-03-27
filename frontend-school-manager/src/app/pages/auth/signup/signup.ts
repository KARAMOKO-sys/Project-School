import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header bg-primary text-white">
              <h3 class="mb-0">Inscription</h3>
            </div>
            <div class="card-body">
              <form (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label for="name" class="form-label">Nom</label>
                  <input type="text" class="form-control" id="name" [(ngModel)]="user.name" name="name" required>
                </div>
                <div class="mb-3">
                  <label for="email" class="form-label">Email</label>
                  <input type="email" class="form-control" id="email" [(ngModel)]="user.email" name="email" required>
                </div>
                <div class="mb-3">
                  <label for="password" class="form-label">Mot de passe</label>
                  <input type="password" class="form-control" id="password" [(ngModel)]="user.password" name="password" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">S'inscrire</button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class SignupComponent {
  user = {
    name: '',
    email: '',
    password: ''
  };

  onSubmit() {
    console.log('Inscription:', this.user);
    alert(`Merci ${this.user.name} ! Votre inscription a été enregistrée.`);
  }
}
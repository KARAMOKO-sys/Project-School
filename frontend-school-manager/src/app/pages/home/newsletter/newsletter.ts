import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-newsletter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './newsletter.html',
  styleUrls: ['./newsletter.scss'],
})
export class NewsletterComponent {
  @Input() title: string = 'Get every single update you will get';
  @Input() subtitle: string = 'Subscribe now';
  @Input() buttonText: string = 'Subscribe now';
  @Input() placeholder: string = 'Enter your email address';
  @Input() imageUrl: string = 'assets/img/gallery/cta.png';
  @Input() imageAlt: string = 'Newsletter subscription';
  @Input() successMessage: string = 'Thank you for subscribing!';
  @Input() errorMessage: string = 'Please enter a valid email address.';
  
  @Output() subscribed = new EventEmitter<string>();
  
  email: string = '';
  isSubmitting: boolean = false;
  showSuccess: boolean = false;
  showError: boolean = false;
  errorText: string = '';

  onSubmit(event: Event): void {
    event.preventDefault();
    
    if (!this.validateEmail(this.email)) {
      this.showErrorMessage(this.errorMessage);
      return;
    }
    
    this.isSubmitting = true;
    this.showSuccess = false;
    this.showError = false;
    
    // Simuler un appel API
    setTimeout(() => {
      this.isSubmitting = false;
      this.showSuccessMessage(this.successMessage);
      this.subscribed.emit(this.email);
      this.email = '';
      
      // Masquer le message après 3 secondes
      setTimeout(() => {
        this.showSuccess = false;
      }, 3000);
    }, 1000);
  }

  private validateEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@([^\s@]+\.)+[^\s@]+$/;
    return emailRegex.test(email);
  }

  private showSuccessMessage(message: string): void {
    this.showSuccess = true;
    this.errorText = message;
  }

  private showErrorMessage(message: string): void {
    this.showError = true;
    this.errorText = message;
    
    setTimeout(() => {
      this.showError = false;
    }, 3000);
  }
}
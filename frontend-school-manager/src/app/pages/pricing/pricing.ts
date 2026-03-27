import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChildPricing } from './child-pricing/child-pricing';
import { FooterComponent } from '../../shared/components/footer/footer';
import { NewsletterComponent } from '../home/newsletter/newsletter';

@Component({
  selector: 'app-pricing',
  standalone: true,
  imports: [CommonModule, ChildPricing, NewsletterComponent,
   FooterComponent],
  templateUrl: './pricing.html',
  styleUrls: ['./pricing.scss'],
})
export class PricingComponent {
  // Ces méthodes seront appelées depuis l'enfant
  onBillingToggle(isYearly: boolean) {
    console.log('Billing toggled:', isYearly);
    // Analytics tracking
  }

  onPlanSelected(plan: any) {
    console.log('Plan selected:', plan);
    // Naviguer vers checkout ou afficher modal
    alert(`You selected the ${plan.name} plan`);
  }


  onNewsletterSubscribe(email: string) {
    console.log('Newsletter subscription:', email);
    // Ici vous pouvez appeler votre API pour enregistrer l'email
    // this.apiService.subscribeToNewsletter(email).subscribe(...);
  }
}
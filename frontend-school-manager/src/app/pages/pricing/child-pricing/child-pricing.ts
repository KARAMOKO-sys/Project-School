import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // ⚠️ IMPORTANT: Ajouter FormsModule
import { RouterLink } from '@angular/router';

export interface PricingFeature {
  name: string;
  included: boolean;
}

export interface PricingPlan {
  id: number;
  name: string;
  badge: string;
  badgeClass: string;
  monthlyPrice: number;
  yearlyPrice: number;
  description: string;
  features: PricingFeature[];
  buttonText: string;
  buttonClass: string;
  popular?: boolean;
}

@Component({
  selector: 'app-child-pricing',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink], // ✅ Ajouter FormsModule et RouterLink
  templateUrl: './child-pricing.html',
  styleUrls: ['./child-pricing.scss'],
})
export class ChildPricing {
  @Input() title: string = 'Get in Reasonable Price';
  @Input() subtitle: string = 'Our Plans';
  @Input() monthlyLabel: string = 'Monthly';
  @Input() yearlyLabel: string = 'Yearly';
  
  @Input() plans: PricingPlan[] = [
    {
      id: 1,
      name: 'Starter',
      badge: 'Starter',
      badgeClass: 'badge-starter',
      monthlyPrice: 19,
      yearlyPrice: 190,
      description: 'Perfect for beginners starting their learning journey',
      buttonText: 'Purchase now',
      buttonClass: 'btn-primary',
      features: [
        { name: 'Access to 25 courses', included: true },
        { name: 'Course Discussions', included: true },
        { name: 'Offline Viewing', included: true },
        { name: 'Certificate after completion', included: false },
        { name: 'Private sessions', included: false }
      ]
    },
    {
      id: 2,
      name: 'Premium',
      badge: 'Premium',
      badgeClass: 'badge-premium',
      monthlyPrice: 29,
      yearlyPrice: 290,
      description: 'Most popular choice for serious learners',
      buttonText: 'Purchase now',
      buttonClass: 'btn-premium',
      popular: true,
      features: [
        { name: 'Access to 25 courses', included: true },
        { name: 'Course Discussions', included: true },
        { name: 'Offline Viewing', included: true },
        { name: 'Certificate after completion', included: true },
        { name: 'Private sessions', included: false }
      ]
    },
    {
      id: 3,
      name: 'Enterprise',
      badge: 'Enterprise',
      badgeClass: 'badge-enterprise',
      monthlyPrice: 49,
      yearlyPrice: 490,
      description: 'Complete solution for teams and organizations',
      buttonText: 'Contact sales',
      buttonClass: 'btn-primary',
      features: [
        { name: 'Access to 25 courses', included: true },
        { name: 'Course Discussions', included: true },
        { name: 'Offline Viewing', included: true },
        { name: 'Certificate after completion', included: true },
        { name: 'Private sessions', included: true }
      ]
    }
  ];
  
  @Output() billingToggle = new EventEmitter<boolean>();
  @Output() planSelected = new EventEmitter<PricingPlan>();
  
  isYearly: boolean = false;
  hoveredPlan: number | null = null;

  onBillingToggle(): void {
    this.isYearly = !this.isYearly;
    this.billingToggle.emit(this.isYearly);
  }

  onPlanSelect(plan: PricingPlan): void {
    this.planSelected.emit(plan);
  }

  getCurrentPrice(plan: PricingPlan): number {
    return this.isYearly ? plan.yearlyPrice : plan.monthlyPrice;
  }

  getPriceSuffix(): string {
    return this.isYearly ? '/year' : '/month';
  }

  getAnnualSavings(plan: PricingPlan): number {
    const monthlyTotal = plan.monthlyPrice * 12;
    const yearlyTotal = plan.yearlyPrice;
    return monthlyTotal - yearlyTotal;
  }

  hasSavings(plan: PricingPlan): boolean {
    return this.getAnnualSavings(plan) > 0;
  }
}
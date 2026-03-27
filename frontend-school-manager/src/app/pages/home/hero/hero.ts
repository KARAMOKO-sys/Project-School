import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './hero.html',
  styleUrls: ['./hero.scss'],
})
export class HeroComponent {
  @Input() title: string = 'Learn New Skills Online Find Best Courses';
  @Input() subtitle: string = 'Become Master';
  @Input() buttonText: string = 'Get A Quote';
  @Input() secondaryButtonText: string = 'Read more';
  @Input() imageUrl: string = 'assets/hero-header.png';
  @Input() imageAlt: string = 'hero-header';
  @Input() primaryButtonLink: string = '/contact';
  @Input() secondaryButtonLink: string = '/about';
  
  @Output() primaryClick = new EventEmitter<void>();
  @Output() secondaryClick = new EventEmitter<void>();

  onPrimaryClick(): void {
    this.primaryClick.emit();
  }

  onSecondaryClick(): void {
    this.secondaryClick.emit();
  }
}
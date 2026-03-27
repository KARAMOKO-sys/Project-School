import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

export interface FooterLink {
  label: string;
  url: string;
  external?: boolean;
}

export interface FooterSection {
  title: string;
  links: FooterLink[];
}

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './footer.html',
  styleUrls: ['./footer.scss'],
})
export class FooterComponent {
  @Input() logoUrl: string = 'assets/footer-logo.png';
  @Input() logoAlt: string = 'Logo';
  @Input() companyName: string = 'Your Company';
  @Input() currentYear: number = new Date().getFullYear();
  
  @Input() address: string = '1500 Treat Ave, Suite 200';
  @Input() city: string = 'San Francisco, CA 94110';
  @Input() phone: string = '+604-680-9785';
  @Input() supportPhone: string = '1-800-800-2299';
  @Input() email: string = 'vctung@outlook.com';
  
  @Input() socialLinks: FooterLink[] = [
    { label: 'Facebook', url: '#!', external: true },
    { label: 'Twitter', url: '#!', external: true },
    { label: 'Instagram', url: '#!', external: true },
    { label: 'LinkedIn', url: '#!', external: true }
  ];
  
  @Input() sections: FooterSection[] = [
    {
      title: 'Community',
      links: [
        { label: 'Learners', url: '/learners' },
        { label: 'Partners', url: '/partners' },
        { label: 'Developers', url: '/developers' },
        { label: 'Beta Testers', url: '/beta-testers' },
        { label: 'Translators', url: '/translators' },
        { label: 'Blog', url: '/blog' },
        { label: 'Tech Blog', url: '/tech-blog' },
        { label: 'Teaching Center', url: '/teaching-center' }
      ]
    },
    {
      title: 'Information',
      links: [
        { label: 'About', url: '/about' },
        { label: 'Pricing', url: '/pricing' },
        { label: 'Blog', url: '/blog' },
        { label: 'Careers', url: '/careers' },
        { label: 'Contact', url: '/contact' }
      ]
    },
    {
      title: 'More',
      links: [
        { label: 'Press', url: '/press' },
        { label: 'Investors', url: '/investors' },
        { label: 'Terms', url: '/terms' },
        { label: 'Privacy', url: '/privacy' },
        { label: 'Help', url: '/help' },
        { label: 'Accessibility', url: '/accessibility' },
        { label: 'Contact', url: '/contact' },
        { label: 'Articles', url: '/articles' },
        { label: 'Directory', url: '/directory' },
        { label: 'Affiliates', url: '/affiliates' }
      ]
    }
  ];
  
  @Output() linkClick = new EventEmitter<FooterLink>();

  onLinkClick(link: FooterLink): void {
    this.linkClick.emit(link);
  }

  getSocialIcon(platform: string): string {
    const icons: { [key: string]: string } = {
      'Facebook': 'fab fa-facebook-f',
      'Twitter': 'fab fa-twitter',
      'Instagram': 'fab fa-instagram',
      'LinkedIn': 'fab fa-linkedin-in',
      'YouTube': 'fab fa-youtube',
      'Pinterest': 'fab fa-pinterest'
    };
    return icons[platform] || 'fas fa-link';
  }
}
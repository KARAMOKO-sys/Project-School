import { Component, OnInit, HostListener } from '@angular/core';
import { Router, NavigationEnd, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { filter } from 'rxjs/operators';

interface MenuItem {
  label: string;
  route: string;
  active?: boolean;
}

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './header.html',
  styleUrls: ['./header.scss'],
})
export class HeaderComponent implements OnInit {
  logoUrl = 'assets/logo-n.png';
  signUpRoute = '/auth/signup';
  searchQuery = '';
  isScrolled = false;
  currentRoute = '';

  menuItems: MenuItem[] = [
    { label: 'Home', route: '/' },
    { label: 'Pricing', route: '/pricing' },
    { label: 'Web Development', route: '/web-development' },
    { label: 'User Research', route: '/user-research' },
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.urlAfterRedirects;
      });
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScrolled = window.scrollY > 50;
  }

  isActive(route: string): boolean {
    if (route === '/') {
      return this.currentRoute === '/' || this.currentRoute === '';
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

  onSignUp(): void {
    console.log('Navigation vers inscription');
    this.router.navigate([this.signUpRoute]);
  }
}
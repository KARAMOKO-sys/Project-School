import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="container mt-5">
      <h1>Résultats de recherche</h1>
      <p>Recherche pour : <strong>{{ searchQuery }}</strong></p>
      <div class="alert alert-info">
        Aucun résultat trouvé pour "{{ searchQuery }}"
      </div>
      <a routerLink="/" class="btn btn-primary">Retour à l'accueil</a>
    </div>
  `,
  styles: []
})
export class SearchComponent implements OnInit {
  searchQuery = '';

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.searchQuery = params['q'] || '';
    });
  }
}
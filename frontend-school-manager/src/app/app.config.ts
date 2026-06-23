// app/app.config.ts
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, withFetch } from '@angular/common/http';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    // ✅ Pour le développement, utiliser Zone.js (plus stable)
    provideZoneChangeDetection({ eventCoalescing: true }),
    
    // ✅ OU pour zoneless (si vous voulez vraiment l'utiliser)
    // provideZonelessChangeDetection(),
    
    provideRouter(routes),
    
    // ✅ TRÈS IMPORTANT : Fournir HttpClient
    provideHttpClient(
      withFetch(), // Utiliser fetch API (recommandé)
      // withInterceptors([authInterceptor]) // Si vous avez des intercepteurs
    ),
  ],
};
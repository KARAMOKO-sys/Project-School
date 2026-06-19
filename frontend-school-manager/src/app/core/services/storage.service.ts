// core/services/storage.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private readonly prefix = 'edulearn_';

  constructor() {}

  /**
   * Définit une valeur dans le localStorage
   */
  setItem(key: string, value: any): void {
    try {
      const jsonValue = JSON.stringify(value);
      localStorage.setItem(this.prefix + key, jsonValue);
    } catch (error) {
      console.error('Erreur lors du stockage:', error);
    }
  }

  /**
   * Récupère une valeur du localStorage
   */
  getItem<T>(key: string): T | null {
    try {
      const item = localStorage.getItem(this.prefix + key);
      if (item) {
        return JSON.parse(item);
      }
      return null;
    } catch (error) {
      console.error('Erreur lors de la récupération:', error);
      return null;
    }
  }

  /**
   * Supprime une valeur du localStorage
   */
  removeItem(key: string): void {
    localStorage.removeItem(this.prefix + key);
  }

  /**
   * Supprime toutes les valeurs du localStorage
   */
  clear(): void {
    const keys = Object.keys(localStorage);
    keys.forEach((key) => {
      if (key.startsWith(this.prefix)) {
        localStorage.removeItem(key);
      }
    });
  }

  /**
   * Vérifie si une clé existe
   */
  hasItem(key: string): boolean {
    return localStorage.getItem(this.prefix + key) !== null;
  }
}

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseFilterComponent, FilterOptions } from './course-filter/course-filter';
import { Course, CoursesGridComponent } from './courses-grid/courses-grid';
import { NewsletterComponent } from '../home/newsletter/newsletter';
import { FooterComponent } from '../../shared/components/footer/footer';

@Component({
  selector: 'app-web-development',
  standalone: true,
  imports: [CommonModule, CourseFilterComponent, CoursesGridComponent, NewsletterComponent, FooterComponent],
  templateUrl: './web-development.html',
  styleUrls: ['./web-development.scss'],
})
export class WebDevelopmentComponent implements OnInit {
  // Données complètes des cours
  allCourses: Course[] = [
    {
      id: 1,
      title: 'User Research for User Experience Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/design.png',
      category: 'design',
      level: 'Intermediate',
      rating: 4.8,
      students: 12500
    },
    {
      id: 2,
      title: 'Buddhism and Modern Psychology',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/psychology.png',
      category: 'psychology',
      level: 'Beginner',
      rating: 4.7,
      students: 8900
    },
    {
      id: 3,
      title: 'Introduction to Philosophy',
      institution: 'Duke University',
      imageUrl: 'assets/philosophy.png',
      category: 'philosophy',
      level: 'Beginner',
      rating: 4.9,
      students: 15600
    },
    {
      id: 4,
      title: 'Advance on Seeing Through Photographs',
      institution: 'Duke University',
      imageUrl: 'assets/photographs.png',
      category: 'photography',
      level: 'Advanced',
      rating: 4.6,
      students: 7400
    },
    {
      id: 5,
      title: 'Think Again I: How to Understand Arguments',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/arguments.png',
      category: 'critical-thinking',
      level: 'Intermediate',
      rating: 4.8,
      students: 11200
    },
    {
      id: 6,
      title: 'User Experience Design Fundamentals',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/experience-design.png',
      category: 'design',
      level: 'Intermediate',
      rating: 4.7,
      students: 9800
    },
    {
      id: 7,
      title: 'Advanced User Interface Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/user-design.png',
      category: 'design',
      level: 'Advanced',
      rating: 4.9,
      students: 13400
    },
    {
      id: 8,
      title: 'Introduction to Logic and Critical Thinking',
      institution: 'Duke University',
      imageUrl: 'assets/critical-thinking.png',
      category: 'logic',
      level: 'Beginner',
      rating: 4.8,
      students: 16700
    },
    {
      id: 9,
      title: 'Modern and Contemporary Art and Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/art-design.png',
      category: 'art',
      level: 'Intermediate',
      rating: 4.7,
      students: 10500
    },
    {
      id: 10,
      title: 'User Research Methods and Techniques',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/user-research.png',
      category: 'research',
      level: 'Advanced',
      rating: 4.9,
      students: 8900
    },
    {
      id: 11,
      title: 'Advanced Photography Techniques',
      institution: 'Duke University',
      imageUrl: 'assets/photographs.png',
      category: 'photography',
      level: 'Advanced',
      rating: 4.8,
      students: 6300
    },
    {
      id: 12,
      title: 'UX Design Masterclass',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/ux.png',
      category: 'ux-design',
      level: 'Expert',
      rating: 5.0,
      students: 4500
    },
    {
      id: 13,
      title: 'Web Development Bootcamp',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/design.png',
      category: 'web-development',
      level: 'Beginner',
      rating: 4.8,
      students: 21500
    },
    {
      id: 14,
      title: 'Advanced Critical Thinking',
      institution: 'Duke University',
      imageUrl: 'assets/critical-thinking.png',
      category: 'logic',
      level: 'Advanced',
      rating: 4.7,
      students: 7200
    },
    {
      id: 15,
      title: 'Contemporary Art History',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/art-design-1.png',
      category: 'art',
      level: 'Intermediate',
      rating: 4.6,
      students: 5800
    }
  ];

  // Cours filtrés (initialisés avec tous les cours)
  filteredCourses: Course[] = [...this.allCourses];
  
  // État de chargement
  isLoading: boolean = false;
  
  // Statistiques des filtres
  activeFiltersCount: number = 0;

  ngOnInit(): void {
    console.log('WebDevelopmentComponent initialized');
  }

  // Méthode appelée quand les filtres changent en temps réel
  onFilterChanged(filters: FilterOptions): void {
    console.log('Filters changed:', filters);
    this.applyFilters(filters);
  }

  // Méthode appelée lors de la soumission du formulaire
  onFilterSubmit(filters: FilterOptions): void {
    console.log('Filters submitted:', filters);
    this.applyFilters(filters);
  }

  // Logique d'application des filtres
  applyFilters(filters: FilterOptions): void {
    this.isLoading = true;
    
    // Simuler un délai de chargement pour une meilleure UX
    setTimeout(() => {
      this.filteredCourses = this.allCourses.filter(course => {
        let match = true;
        
        // Filtre par catégorie
        if (filters.category && filters.category !== 'web-development') {
          match = match && course.category?.toLowerCase() === filters.category;
        }
        
        // Filtre par niveau
        if (filters.level && filters.level !== 'all') {
          match = match && course.level?.toLowerCase() === filters.level;
        }
        
        // Filtre par langue (si disponible dans les cours)
        if (filters.language && filters.language !== 'english') {
          // Vous pouvez ajouter un champ language à vos cours
          // match = match && (course as any).language?.toLowerCase() === filters.language;
        }
        
        // Filtre par instructeur (si disponible dans les cours)
        if (filters.instructor && filters.instructor !== 'all') {
          // Vous pouvez ajouter un champ instructor à vos cours
          // match = match && (course as any).instructor?.toLowerCase() === filters.instructor;
        }
        
        return match;
      });
      
      // Calculer le nombre de filtres actifs
      this.activeFiltersCount = this.getActiveFiltersCount(filters);
      
      this.isLoading = false;
      console.log(`Filtered ${this.filteredCourses.length} courses from ${this.allCourses.length} total`);
    }, 300);
  }

  // Compter le nombre de filtres actifs
  private getActiveFiltersCount(filters: FilterOptions): number {
    let count = 0;
    if (filters.category && filters.category !== 'web-development') count++;
    if (filters.level && filters.level !== 'all') count++;
    if (filters.language && filters.language !== 'english') count++;
    if (filters.instructor && filters.instructor !== 'all') count++;
    return count;
  }

  // Méthode appelée quand un cours est cliqué
  onCourseClick(course: Course): void {
    console.log('Course clicked:', course);
    // Navigation vers la page du cours
    // this.router.navigate(['/courses', course.id]);
    alert(`Opening course: ${course.title}`);
  }

  // Réinitialiser tous les filtres
  resetFilters(): void {
    const defaultFilters: FilterOptions = {
      category: 'web-development',
      level: 'all',
      language: 'english',
      instructor: 'all'
    };
    this.applyFilters(defaultFilters);
  }

   onNewsletterSubscribe(email: string) {
    console.log('Newsletter subscription:', email);
    // Ici vous pouvez appeler votre API pour enregistrer l'email
    // this.apiService.subscribeToNewsletter(email).subscribe(...);
  }
}
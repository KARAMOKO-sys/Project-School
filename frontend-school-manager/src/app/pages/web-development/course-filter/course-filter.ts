import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface FilterOptions {
  category: string;
  level: string;
  language: string;
  instructor: string;
}

export interface FilterCategory {
  id: string;
  name: string;
  value: string;
}

@Component({
  selector: 'app-course-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './course-filter.html',
  styleUrls: ['./course-filter.scss'],
})
export class CourseFilterComponent {
  @Input() title: string = 'Web Development';
  @Input() subtitle: string = 'Course category';
  @Input() buttonText: string = 'Submit';
  
  @Input() categories: FilterCategory[] = [
    { id: 'web-dev', name: 'Web development', value: 'web-development' },
    { id: 'ux-design', name: 'UX Designer', value: 'ux-designer' },
    { id: 'mobile-dev', name: 'Mobile Development', value: 'mobile-development' },
    { id: 'data-science', name: 'Data Science', value: 'data-science' },
    { id: 'ai-ml', name: 'AI & Machine Learning', value: 'ai-ml' }
  ];
  
  @Input() levels: FilterCategory[] = [
    { id: 'all', name: 'All Level', value: 'all' },
    { id: 'beginner', name: 'Beginner', value: 'beginner' },
    { id: 'intermediate', name: 'Intermediate', value: 'intermediate' },
    { id: 'advanced', name: 'Advanced', value: 'advanced' },
    { id: 'expert', name: 'Expert', value: 'expert' }
  ];
  
  @Input() languages: FilterCategory[] = [
    { id: 'en', name: 'English', value: 'english' },
    { id: 'fr', name: 'Français', value: 'french' },
    { id: 'es', name: 'Español', value: 'spanish' },
    { id: 'de', name: 'Deutsch', value: 'german' },
    { id: 'zh', name: '中文', value: 'chinese' }
  ];
  
  @Input() instructors: FilterCategory[] = [
    { id: 'all', name: 'All Instructor', value: 'all' },
    { id: 'john-doe', name: 'John Doe', value: 'john-doe' },
    { id: 'jane-smith', name: 'Jane Smith', value: 'jane-smith' },
    { id: 'mike-johnson', name: 'Mike Johnson', value: 'mike-johnson' },
    { id: 'sarah-williams', name: 'Sarah Williams', value: 'sarah-williams' }
  ];
  
  @Output() filterChanged = new EventEmitter<FilterOptions>();
  @Output() filterSubmit = new EventEmitter<FilterOptions>();
  
  selectedCategory: string = 'web-development';
  selectedLevel: string = 'all';
  selectedLanguage: string = 'english';
  selectedInstructor: string = 'all';

  onFilterChange(): void {
    const filters: FilterOptions = {
      category: this.selectedCategory,
      level: this.selectedLevel,
      language: this.selectedLanguage,
      instructor: this.selectedInstructor
    };
    this.filterChanged.emit(filters);
  }

  onSubmit(): void {
    const filters: FilterOptions = {
      category: this.selectedCategory,
      level: this.selectedLevel,
      language: this.selectedLanguage,
      instructor: this.selectedInstructor
    };
    this.filterSubmit.emit(filters);
  }

  resetFilters(): void {
    this.selectedCategory = 'web-development';
    this.selectedLevel = 'all';
    this.selectedLanguage = 'english';
    this.selectedInstructor = 'all';
    this.onFilterChange();
  }

  // ✅ Ajout de la méthode hasActiveFilters
  hasActiveFilters(): boolean {
    return this.selectedCategory !== 'web-development' ||
           this.selectedLevel !== 'all' ||
           this.selectedLanguage !== 'english' ||
           this.selectedInstructor !== 'all';
  }

  // ✅ Ajout de la méthode resetCategory
  resetCategory(): void {
    this.selectedCategory = 'web-development';
    this.onFilterChange();
  }

  // ✅ Ajout de la méthode resetLevel
  resetLevel(): void {
    this.selectedLevel = 'all';
    this.onFilterChange();
  }

  // ✅ Ajout de la méthode resetLanguage
  resetLanguage(): void {
    this.selectedLanguage = 'english';
    this.onFilterChange();
  }

  // ✅ Ajout de la méthode resetInstructor
  resetInstructor(): void {
    this.selectedInstructor = 'all';
    this.onFilterChange();
  }

  getCategoryName(value: string): string {
    const category = this.categories.find(c => c.value === value);
    return category ? category.name : value;
  }

  getLevelName(value: string): string {
    const level = this.levels.find(l => l.value === value);
    return level ? level.name : value;
  }

  getLanguageName(value: string): string {
    const language = this.languages.find(l => l.value === value);
    return language ? language.name : value;
  }

  getInstructorName(value: string): string {
    const instructor = this.instructors.find(i => i.value === value);
    return instructor ? instructor.name : value;
  }
}
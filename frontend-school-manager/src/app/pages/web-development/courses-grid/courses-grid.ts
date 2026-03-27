import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

export interface Course {
  id: number;
  title: string;
  institution: string;
  imageUrl: string;
  category?: string;
  level?: string;
  rating?: number;
  students?: number;
}

@Component({
  selector: 'app-courses-grid',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './courses-grid.html',
  styleUrls: ['./courses-grid.scss'],
})
export class CoursesGridComponent implements OnInit {
  @Input() courses: Course[] = [
    {
      id: 1,
      title: 'User Research for User Experience Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/design.png',
      category: 'Design',
      level: 'Intermediate'
    },
    {
      id: 2,
      title: 'Buddhism and Modern Psychology',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/psychology.png',
      category: 'Psychology',
      level: 'Beginner'
    },
    {
      id: 3,
      title: 'Introduction to Philosophy',
      institution: 'Duke University',
      imageUrl: 'assets/img/gallery/philosophy.png',
      category: 'Philosophy',
      level: 'Beginner'
    },
    {
      id: 4,
      title: 'Advance on Seeing Through Photographs',
      institution: 'Duke University',
      imageUrl: 'assets/img/gallery/photographs.png',
      category: 'Photography',
      level: 'Advanced'
    },
    {
      id: 5,
      title: 'Think Again I: How to Understand Arguments',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/arguments.png',
      category: 'Critical Thinking',
      level: 'Intermediate'
    },
    {
      id: 6,
      title: 'User Research for User Experience Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/experience-design.png',
      category: 'Design',
      level: 'Intermediate'
    },
    {
      id: 7,
      title: 'User Research for User Experience Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/user-design.png',
      category: 'Design',
      level: 'Advanced'
    },
    {
      id: 8,
      title: 'Introduction to Logic and Critical Thinking',
      institution: 'Duke University',
      imageUrl: 'assets/img/gallery/critical-thinking.png',
      category: 'Logic',
      level: 'Beginner'
    },
    {
      id: 9,
      title: 'Modern and Contemporary Art and Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/art-design.png',
      category: 'Art',
      level: 'Intermediate'
    },
    {
      id: 10,
      title: 'Modern and Contemporary Art and Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/user-research.png',
      category: 'Art',
      level: 'Advanced'
    },
    {
      id: 11,
      title: 'Advance on Seeing Through Photographs',
      institution: 'Duke University',
      imageUrl: 'assets/img/gallery/photographs.png',
      category: 'Photography',
      level: 'Intermediate'
    },
    {
      id: 12,
      title: 'Modern and Contemporary Art and Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/ux.png',
      category: 'UX Design',
      level: 'Expert'
    },
    {
      id: 13,
      title: 'User Research for User Experience Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/design.png',
      category: 'Design',
      level: 'Beginner'
    },
    {
      id: 14,
      title: 'Introduction to Logic and Critical Thinking',
      institution: 'Duke University',
      imageUrl: 'assets/img/gallery/critical-thinking.png',
      category: 'Logic',
      level: 'Intermediate'
    },
    {
      id: 15,
      title: 'Modern and Contemporary Art and Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/img/gallery/art-design-1.png',
      category: 'Art',
      level: 'Advanced'
    }
  ];
  
  @Input() itemsPerPage: number = 9;
  @Input() showLoadMore: boolean = true;
  @Output() courseClick = new EventEmitter<Course>();
  
  visibleCourses: Course[] = [];
  currentPage: number = 1;
  isLoading: boolean = false;
  hoveredCourse: number | null = null;

  ngOnInit(): void {
    this.loadMoreCourses();
  }

  loadMoreCourses(): void {
    if (this.isLoading) return;
    
    this.isLoading = true;
    
    // Simuler un chargement asynchrone
    setTimeout(() => {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      const newCourses = this.courses.slice(start, end);
      this.visibleCourses = [...this.visibleCourses, ...newCourses];
      this.currentPage++;
      this.isLoading = false;
    }, 500);
  }

  onCourseClick(course: Course): void {
    this.courseClick.emit(course);
  }

  hasMoreCourses(): boolean {
    return this.visibleCourses.length < this.courses.length;
  }

  getLevelClass(level: string): string {
    const classes: { [key: string]: string } = {
      'Beginner': 'level-beginner',
      'Intermediate': 'level-intermediate',
      'Advanced': 'level-advanced',
      'Expert': 'level-expert'
    };
    return classes[level] || 'level-default';
  }

  getRatingStars(rating: number): number[] {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(i);
    }
    return stars;
  }

  
}
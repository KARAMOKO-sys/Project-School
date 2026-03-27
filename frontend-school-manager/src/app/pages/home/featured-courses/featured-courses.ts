import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

export interface Course {
  id: number;
  title: string;
  institution: string;
  imageUrl: string;
  category?: string;
  rating?: number;
  students?: number;
  price?: number;
}

@Component({
  selector: 'app-featured-courses',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './featured-courses.html',
  styleUrls: ['./featured-courses.scss'],
})
export class FeaturedCoursesComponent {
  @Input() sectionTitle: string = 'Top Featured Courses';
  @Input() courses: Course[] = [
    {
      id: 1,
      title: 'User Research for User Experience Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/design.png',
      category: 'Design',
      rating: 4.8,
      students: 12450,
      price: 49
    },
    {
      id: 2,
      title: 'Buddhism and Modern Psychology',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/psychology.png',
      category: 'Psychology',
      rating: 4.7,
      students: 8920,
      price: 39
    },
    {
      id: 3,
      title: 'Introduction to Philosophy',
      institution: 'Duke University',
      imageUrl: 'assets/philosophy.png',
      category: 'Philosophy',
      rating: 4.9,
      students: 15600,
      price: 59
    },
    {
      id: 4,
      title: 'Advance on Seeing Through Photographs',
      institution: 'Duke University',
      imageUrl: 'assets/photographs.png',
      category: 'Photography',
      rating: 4.6,
      students: 7430,
      price: 45
    },
    {
      id: 5,
      title: 'Think Again I: How to Understand Arguments',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/arguments.png',
      category: 'Critical Thinking',
      rating: 4.8,
      students: 11200,
      price: 49
    },
    {
      id: 6,
      title: 'User Research for User Experience Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/experience-design.png',
      category: 'Design',
      rating: 4.7,
      students: 9800,
      price: 55
    },
    {
      id: 7,
      title: 'User Research for User Experience Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/user-research.png',
      category: 'Research',
      rating: 4.9,
      students: 13400,
      price: 65
    },
    {
      id: 8,
      title: 'Introduction to Logic and Critical Thinking',
      institution: 'Duke University',
      imageUrl: 'assets/critical-thinking.png',
      category: 'Logic',
      rating: 4.8,
      students: 16700,
      price: 49
    },
    {
      id: 9,
      title: 'Modern and Contemporary Art and Design',
      institution: 'The Museum of Modern Art',
      imageUrl: 'assets/art-design.png',
      category: 'Art',
      rating: 4.7,
      students: 10500,
      price: 59
    }
  ];

  @Output() courseClick = new EventEmitter<Course>();

  onCourseClick(course: Course): void {
    this.courseClick.emit(course);
  }

  getStars(rating: number): number[] {
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    const stars = [];
    
    for (let i = 0; i < fullStars; i++) {
      stars.push(1); // full star
    }
    
    if (hasHalfStar) {
      stars.push(0.5); // half star
    }
    
    while (stars.length < 5) {
      stars.push(0); // empty star
    }
    
    return stars;
  }
}
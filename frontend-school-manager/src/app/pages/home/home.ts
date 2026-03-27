import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeroComponent } from './hero/hero';
import { CourseBannerComponent } from './course-banner/course-banner';
import { FeaturedCoursesComponent } from './featured-courses/featured-courses';
import { StudentFeedbackComponent } from './student-feedback/student-feedback';
import { StatisticsComponent } from './statistics/statistics';
import { NewsletterComponent } from './newsletter/newsletter';
import { FooterComponent } from '../../shared/components/footer/footer';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule, 
    HeroComponent, 
    CourseBannerComponent,
    FeaturedCoursesComponent,
    StudentFeedbackComponent,
    StatisticsComponent,
    NewsletterComponent,
    FooterComponent
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.scss'],
})
export class HomeComponent {
  // Déclarez la propriété courseStartDate
  courseStartDate = new Date(2026, 3, 15, 10, 0, 0);
  
  // Déclarez la propriété courses avec les données
  courses = [
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

  onGetQuote() {
    console.log('Get Quote clicked');
    alert('Demande de devis envoyée !');
  }

  onReadMore() {
    console.log('Read more clicked');
  }

  onCourseClick(course: any) {
    console.log('Course clicked:', course);
    alert(`Vous avez cliqué sur: ${course.title}`);
    // Navigation vers la page du cours
    // this.router.navigate(['/courses', course.id]);
  }

  // ✅ Ajoutez cette méthode pour gérer le clic sur l'étudiant
  onStudentClick(student: any) {
    console.log('Student clicked:', student);
    alert(`Profil de ${student.name} - ${student.role}`);
    // Navigation vers le profil de l'étudiant
    // this.router.navigate(['/student', student.id]);
  }

  // ✅ Ajoutez cette méthode pour gérer le clic sur un cours de l'étudiant
  onStudentCourseClick(course: any) {
    console.log('Student course clicked:', course);
    alert(`Cours: ${course.title} - ${course.institution}`);
    // Navigation vers la page du cours
    // this.router.navigate(['/courses', course.id]);
  }

  onNewsletterSubscribe(email: string) {
    console.log('Newsletter subscription:', email);
    // Ici vous pouvez appeler votre API pour enregistrer l'email
    // this.apiService.subscribeToNewsletter(email).subscribe(...);
  }
}
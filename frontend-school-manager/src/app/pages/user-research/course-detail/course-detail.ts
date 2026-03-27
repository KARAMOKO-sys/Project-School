import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';

export interface CourseDetail {
  id: number;
  title: string;
  subtitle?: string;
  description: string;
  longDescription?: string;
  imageUrl: string;
  enrolled: number;
  duration: string;
  lectures: number;
  categories: string[];
  level: string;
  whatYouWillLearn: string[];
  contactEmail: string;
  supportImage?: string;
}

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './course-detail.html',
  styleUrls: ['./course-detail.scss'],
})
export class CourseDetailComponent implements OnInit {
  @Input() course: CourseDetail = {
    id: 1,
    title: 'User Research for User Experience Design',
    subtitle: 'Course detail',
    description: 'The online Master of Computer and Information Technology degree (MCIT Online) is an online masters degree in Computer Science tailored for non-Computer Science majors. Offered by the University of Pennsylvania, this new program brings the long-running, established on-campus MCIT degree online. The MCIT Online program empowers students without computer science backgrounds to succeed in computing and technology fields. MCIT Online students come from diverse academic backgrounds ranging from business and history to chemistry and medicine.',
    longDescription: 'Computer science might not be in your past, but it will be in your future. Technology has an immense impact on our lives, and is creating fields and positions that didn\'t exist five years ago. Equipped with a competitive computer science degree, MCIT Online graduates will be uniquely positioned to fill roles in finance, healthcare, education, and government, as well as in the core software development industry. Exposure to real-world projects throughout the program will prepare students to utilize skills that positively impact society.',
    imageUrl: 'assets/img/gallery/ux-designer.png',
    enrolled: 1200,
    duration: '2 hours',
    lectures: 8,
    categories: ['Technology', 'Design', 'UX Research'],
    level: 'Intermediate Level',
    whatYouWillLearn: [
      'Ivy League Quality - A first-of-its-kind program that offers an Ivy League master\'s degree in Computer Science designed for non-CS majors.',
      'Built Around Your Schedule - The coursework is 100 percent online. You\'ll benefit from the same high-quality instruction as on-campus students and graduate with the same degree.',
      'Accessible Pricing - The cost of the MCIT Online degree is significantly less than on-campus alternatives and most online master\'s degrees in Computer Science.',
      'Try before you apply - Penn Engineering offers an online Specialization, Introduction to Programming with Python and Java, on Coursera to help you decide.'
    ],
    contactEmail: 'vctung@outlook.com',
    supportImage: 'assets/img/gallery/searching.png'
  };

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    // Récupérer l'ID du cours depuis l'URL si nécessaire
    this.route.params.subscribe(params => {
      const courseId = params['id'];
      if (courseId) {
        this.loadCourseDetails(courseId);
      }
    });
  }

  loadCourseDetails(courseId: number): void {
    // Logique pour charger les détails du cours depuis une API
    console.log('Loading course details for ID:', courseId);
  }

  // ✅ Ajout de la méthode enrollCourse
  enrollCourse(): void {
    console.log('Enrolling in course:', this.course.title);
    alert(`You have successfully enrolled in "${this.course.title}"!`);
    // Navigation vers la page de paiement ou de confirmation
    // this.router.navigate(['/checkout', this.course.id]);
  }

  getCategoryBadgeClass(category: string): string {
    const classes: { [key: string]: string } = {
      'Technology': 'badge-tech',
      'Design': 'badge-design',
      'UX Research': 'badge-ux',
      'Business': 'badge-business',
      'Science': 'badge-science'
    };
    return classes[category] || 'badge-default';
  }
}
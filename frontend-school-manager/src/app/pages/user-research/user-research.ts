import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseDetailComponent, CourseDetail } from './course-detail/course-detail';

@Component({
  selector: 'app-user-research',
  standalone: true,
  imports: [CommonModule, CourseDetailComponent],
  templateUrl: './user-research.html',
  styleUrls: ['./user-research.scss'],
})
export class UserResearchComponent {
  // ✅ Déclarez la propriété courseData avec les données du cours
  courseData: CourseDetail = {
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

  // Méthode optionnelle pour gérer l'inscription depuis le parent
  onEnroll(course: CourseDetail): void {
    console.log('Enrolling from parent component:', course);
    alert(`You have enrolled in "${course.title}"!`);
  }
}
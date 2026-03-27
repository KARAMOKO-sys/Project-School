import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

export interface Student {
  id: number;
  name: string;
  role: string;
  avatar?: string;
  feedback: string;
  courses: StudentCourse[];
}

export interface StudentCourse {
  id: number;
  title: string;
  institution: string;
  imageUrl: string;
}

@Component({
  selector: 'app-student-feedback',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './student-feedback.html',
  styleUrls: ['./student-feedback.scss'],
})
export class StudentFeedbackComponent {
  @Input() title: string = 'Successful Student';
  @Input() highlightText: string = 'Feedback';
  @Input() description: string = `Take courses from the world's best instructors and universities. Courses include recorded auto-graded and peer-reviewed assignments, video lectures, and community discussion forums. When you complete a course, you'll be eligible to receive a shareable electronic Course Certificate for a small fee.`;
  
  @Input() student: Student = {
    id: 1,
    name: 'Kimmie Vo',
    role: 'Junior Designer',
    feedback: 'The courses are amazing!',
    courses: [
      {
        id: 1,
        title: 'Modern and Contemporary Art and Design',
        institution: 'The Museum of Modern Art',
        imageUrl: 'assets/art-design.png'
      }
    ]
  };

  @Input() coursesTitle: string = 'The courses that Kimmie has taken';
  @Input() studentImageUrl: string = 'assets/student-feedback.png';
  
  @Output() studentClick = new EventEmitter<Student>();
  @Output() courseClick = new EventEmitter<StudentCourse>();

  onStudentClick(): void {
    this.studentClick.emit(this.student);
  }

  onCourseClick(course: StudentCourse, event: Event): void {
    event.stopPropagation();
    this.courseClick.emit(course);
  }
}
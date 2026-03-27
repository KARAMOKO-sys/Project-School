import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-course-banner',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './course-banner.html',
  styleUrls: ['./course-banner.scss'],
})
export class CourseBannerComponent implements OnInit, OnDestroy {
  @Input() courseTitle: string = 'Basic in Python';
  @Input() imageUrl: string = 'assets/img/gallery/funfacts.png';
  @Input() imageAlt: string = 'Course promotion';
  @Input() targetDate: Date = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000); // 7 jours par défaut
  
  days: number = 0;
  hours: number = 0;
  minutes: number = 0;
  seconds: number = 0;
  
  private timerInterval: any;

  ngOnInit(): void {
    this.startCountdown();
  }

  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  startCountdown(): void {
    this.updateCountdown();
    this.timerInterval = setInterval(() => {
      this.updateCountdown();
    }, 1000);
  }

  updateCountdown(): void {
    const now = new Date().getTime();
    const target = this.targetDate.getTime();
    const distance = target - now;

    if (distance < 0) {
      this.days = 0;
      this.hours = 0;
      this.minutes = 0;
      this.seconds = 0;
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
      }
      return;
    }

    this.days = Math.floor(distance / (1000 * 60 * 60 * 24));
    this.hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    this.minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
    this.seconds = Math.floor((distance % (1000 * 60)) / 1000);
  }

  formatNumber(num: number): string {
    return num < 10 ? `0${num}` : `${num}`;
  }
}
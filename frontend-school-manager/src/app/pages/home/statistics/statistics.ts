import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface StatisticItem {
  id: number;
  icon: string;
  value: number;
  label: string;
  suffix?: string;
  prefix?: string;
}

@Component({
  selector: 'app-statistics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics.html',
  styleUrls: ['./statistics.scss'],
})
export class StatisticsComponent implements OnInit, OnDestroy {
  @Input() statistics: StatisticItem[] = [
    {
      id: 1,
      icon: 'assets/published.png',
      value: 768,
      label: 'COURSES PUBLISHED'
    },
    {
      id: 2,
      icon: 'assets/instructors.png',
      value: 120,
      label: 'EXPERT INSTRUCTORS'
    },
    {
      id: 3,
      icon: 'assets/learners.png',
      value: 8300,
      label: 'HAPPY LEARNERS'
    },
    {
      id: 4,
      icon: 'assets/awards.png',
      value: 32,
      label: 'AWARDS ACHIEVED'
    }
  ];

  @Input() backgroundImage: string = 'assets/funfacts-2-bg.png';
  @Input() animationDuration: number = 2000; // 2 seconds
  @Input() startAnimation: boolean = true;

  displayedValues: number[] = [];
  private animationFrameId: any;
  private isVisible: boolean = false;
  private observer: IntersectionObserver | null = null;

  ngOnInit(): void {
    this.displayedValues = this.statistics.map(() => 0);
    
    // Setup intersection observer to start animation when component is visible
    if (this.startAnimation) {
      this.setupIntersectionObserver();
    }
  }

  ngOnDestroy(): void {
    if (this.animationFrameId) {
      cancelAnimationFrame(this.animationFrameId);
    }
    if (this.observer) {
      this.observer.disconnect();
    }
  }

  private setupIntersectionObserver(): void {
    this.observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && !this.isVisible) {
          this.isVisible = true;
          this.startCounters();
        }
      });
    }, { threshold: 0.3 });

    const element = document.querySelector('.statistics-section');
    if (element) {
      this.observer.observe(element);
    }
  }

  private startCounters(): void {
    const startTime = performance.now();
    const endValues = this.statistics.map(s => s.value);

    const animate = (currentTime: number) => {
      const elapsed = currentTime - startTime;
      const progress = Math.min(1, elapsed / this.animationDuration);
      
      this.displayedValues = endValues.map(endValue => 
        Math.floor(endValue * progress)
      );
      
      if (progress < 1) {
        this.animationFrameId = requestAnimationFrame(animate);
      } else {
        this.displayedValues = [...endValues];
        this.animationFrameId = null;
      }
    };
    
    this.animationFrameId = requestAnimationFrame(animate);
  }

  formatNumber(value: number): string {
    if (value >= 1000) {
      return value.toLocaleString();
    }
    return value.toString();
  }
}
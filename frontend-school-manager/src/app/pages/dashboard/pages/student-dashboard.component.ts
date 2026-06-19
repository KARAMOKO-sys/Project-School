// features/dashboard/components/student-dashboard/student-dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

interface Course {
  id: string;
  title: string;
  teacherName: string;
  progress: number;
  imageUrl?: string;
  category: string;
  rating?: number;
  totalReviews?: number;
  price?: string;
}

interface Activity {
  id: string;
  type: 'quiz' | 'message' | 'course' | 'payment';
  title: string;
  description: string;
  time: string;
  icon: string;
  color: string;
}

@Component({
  selector: 'app-student-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="dashboard-container">
      <!-- Sidebar -->
      <aside class="sidebar">
        <div class="logo">
          <h2>📚 EduLearn</h2>
        </div>

        <nav class="sidebar-nav">
          <ul>
            <li>
              <a routerLink="/dashboard" class="active">
                <i class="fas fa-home"></i>
                <span>Tableau de bord</span>
              </a>
            </li>
            <li>
              <a routerLink="/my-courses">
                <i class="fas fa-book"></i>
                <span>Mes cours</span>
              </a>
            </li>
            <li>
              <a routerLink="/favorites">
                <i class="fas fa-heart"></i>
                <span>Favoris</span>
              </a>
            </li>
            <li>
              <a routerLink="/certificates">
                <i class="fas fa-certificate"></i>
                <span>Certificats</span>
              </a>
            </li>
            <li>
              <a routerLink="/ai-quiz">
                <i class="fas fa-question-circle"></i>
                <span>Quiz IA</span>
              </a>
            </li>
            <li>
              <a routerLink="/messages">
                <i class="fas fa-envelope"></i>
                <span>Messagerie</span>
                <span class="badge" *ngIf="unreadMessages > 0">{{ unreadMessages }}</span>
              </a>
            </li>
            <li>
              <a routerLink="/private-services">
                <i class="fas fa-briefcase"></i>
                <span>Services privés</span>
              </a>
            </li>
            <li>
              <a routerLink="/payments">
                <i class="fas fa-credit-card"></i>
                <span>Paiements</span>
              </a>
            </li>
            <li>
              <a routerLink="/history">
                <i class="fas fa-history"></i>
                <span>Historique</span>
              </a>
            </li>
            <li>
              <a routerLink="/settings">
                <i class="fas fa-cog"></i>
                <span>Paramètres</span>
              </a>
            </li>
          </ul>
        </nav>

        <div class="sidebar-footer">
          <a (click)="logout()" class="logout-link">
            <i class="fas fa-sign-out-alt"></i>
            <span>Déconnexion</span>
          </a>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="main-content">
        <!-- Header -->
        <header class="dashboard-header">
          <div class="header-left">
            <h1>Bonjour, {{ userFirstName }} ! 👋</h1>
            <p class="subtitle">Continuez votre apprentissage aujourd'hui.</p>
          </div>
          <div class="header-right">
            <div class="search-bar">
              <i class="fas fa-search"></i>
              <input type="text" placeholder="Rechercher un cours, une notion..." />
            </div>
            <button class="notification-btn">
              <i class="fas fa-bell"></i>
              <span class="notification-badge" *ngIf="notifications > 0">{{ notifications }}</span>
            </button>
            <div class="user-profile">
              <div class="avatar">
                <i class="fas fa-user"></i>
              </div>
              <span class="user-name">{{ userFirstName }}</span>
              <i class="fas fa-chevron-down"></i>
            </div>
          </div>
        </header>

        <!-- Stats Cards -->
        <section class="stats-grid">
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
            >
              <i class="fas fa-book"></i>
            </div>
            <div class="stat-info">
              <h3>{{ stats.courses }}</h3>
              <p>Cours suivis</p>
            </div>
          </div>
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)"
            >
              <i class="fas fa-clock"></i>
            </div>
            <div class="stat-info">
              <h3>{{ stats.hours }}<span class="unit">h</span></h3>
              <p>Heures d'apprentissage</p>
            </div>
          </div>
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)"
            >
              <i class="fas fa-certificate"></i>
            </div>
            <div class="stat-info">
              <h3>{{ stats.certificates }}</h3>
              <p>Certificats obtenus</p>
            </div>
          </div>
          <div class="stat-card">
            <div
              class="stat-icon"
              style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)"
            >
              <i class="fas fa-trophy"></i>
            </div>
            <div class="stat-info">
              <h3>{{ stats.points }}</h3>
              <p>Points</p>
            </div>
          </div>
        </section>

        <!-- Main Grid -->
        <div class="dashboard-grid">
          <!-- Resume Courses -->
          <div class="card resume-courses">
            <div class="card-header">
              <h2>Reprendre un cours</h2>
              <a routerLink="/my-courses" class="view-all">Voir tout</a>
            </div>
            <div class="courses-list">
              <div *ngFor="let course of resumeCourses" class="course-item">
                <div class="course-image">
                  <img *ngIf="course.imageUrl" [src]="course.imageUrl" [alt]="course.title" />
                  <div *ngIf="!course.imageUrl" class="course-placeholder">
                    <i class="fas fa-book-open"></i>
                  </div>
                  <div class="progress-overlay">
                    <div class="progress-bar" [style.width.%]="course.progress"></div>
                  </div>
                </div>
                <div class="course-info">
                  <h4>{{ course.title }}</h4>
                  <p class="instructor">{{ course.teacherName }}</p>
                  <div class="course-progress">
                    <span>{{ course.progress }}% complété</span>
                    <button class="btn-continue">Continuer</button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Recent Activity -->
          <div class="card recent-activity">
            <div class="card-header">
              <h2>Activité récente</h2>
            </div>
            <div class="activity-list">
              <div *ngFor="let activity of recentActivities" class="activity-item">
                <div class="activity-icon" [ngStyle]="{ background: activity.color }">
                  <i [class]="'fas fa-' + activity.icon"></i>
                </div>
                <div class="activity-content">
                  <h4>{{ activity.title }}</h4>
                  <p>{{ activity.description }}</p>
                  <span class="time">{{ activity.time }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Recommended Courses -->
          <div class="card recommended-courses">
            <div class="card-header">
              <h2>Cours recommandés pour vous</h2>
              <a routerLink="/courses" class="view-all">Voir tout</a>
            </div>
            <div class="courses-grid">
              <div *ngFor="let course of recommendedCourses" class="course-card">
                <div class="course-card-image">
                  <div class="course-placeholder">
                    <i class="fas fa-book-open"></i>
                  </div>
                  <span class="course-badge" *ngIf="course.price === 'Gratuit'">{{
                    course.price
                  }}</span>
                </div>
                <div class="course-card-content">
                  <h4>{{ course.title }}</h4>
                  <p class="instructor">{{ course.teacherName }}</p>
                  <div class="course-meta">
                    <span class="rating" *ngIf="course.rating">
                      <i class="fas fa-star"></i> {{ course.rating }}
                    </span>
                    <span class="category">{{ course.category }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- AI Assistant Promo -->
          <div class="card ai-assistant-promo">
            <div class="ai-content">
              <h2>Générez des Quiz IA</h2>
              <p>
                Générez des quiz personnalisés à partir d'une notion ou d'un cours. L'IA analyse
                votre niveau et adapte les questions.
              </p>
              <button routerLink="/ai-quiz" class="btn-ai">
                <i class="fas fa-robot"></i>
                Créer un quiz
              </button>
            </div>
            <div class="ai-image">
              <i class="fas fa-robot"></i>
            </div>
          </div>

          <!-- Learning Statistics -->
          <div class="card learning-stats">
            <div class="card-header">
              <h2>Statistiques d'apprentissage</h2>
            </div>
            <div class="stats-content">
              <div class="chart-section">
                <h3>Heures d'apprentissage (6 derniers mois)</h3>
                <div class="chart-bars">
                  <div *ngFor="let month of learningMonths" class="bar">
                    <div class="bar-fill" [style.height.%]="month.value"></div>
                    <span class="bar-label">{{ month.label }}</span>
                  </div>
                </div>
              </div>
              <div class="subject-distribution">
                <h3>Répartition par matière</h3>
                <div *ngFor="let subject of subjectDistribution" class="distribution-item">
                  <div class="subject-info">
                    <div class="subject-color" [style.background]="subject.color"></div>
                    <span>{{ subject.name }}</span>
                  </div>
                  <div class="subject-stats">
                    <span class="percentage">{{ subject.percentage }}%</span>
                    <div class="mini-progress">
                      <div
                        class="progress-fill"
                        [style.width.%]="subject.percentage"
                        [style.background]="subject.color"
                      ></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  `,
  styles: [
    `
      .dashboard-container {
        display: flex;
        min-height: 100vh;
        background: #f5f7fa;
      }

      /* ===== SIDEBAR ===== */
      .sidebar {
        width: 260px;
        background: linear-gradient(180deg, #1e3c72 0%, #2a5298 100%);
        color: white;
        padding: 20px 0;
        display: flex;
        flex-direction: column;
        position: fixed;
        height: 100vh;
        overflow-y: auto;
        z-index: 1000;
      }

      .logo {
        padding: 0 20px 20px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
      }

      .logo h2 {
        font-size: 24px;
        font-weight: 700;
        margin: 0;
        color: #ffb400;
      }

      .sidebar-nav {
        flex: 1;
        padding: 20px 0;
      }

      .sidebar-nav ul {
        list-style: none;
        padding: 0;
        margin: 0;
      }

      .sidebar-nav li {
        margin-bottom: 4px;
      }

      .sidebar-nav a {
        display: flex;
        align-items: center;
        padding: 12px 20px;
        color: rgba(255, 255, 255, 0.8);
        text-decoration: none;
        transition: all 0.3s;
        gap: 15px;
        font-size: 14px;
        position: relative;
      }

      .sidebar-nav a:hover,
      .sidebar-nav a.active {
        background: rgba(255, 255, 255, 0.1);
        color: white;
        border-left: 4px solid #ffb400;
      }

      .sidebar-nav a i {
        width: 20px;
        text-align: center;
        font-size: 16px;
      }

      .sidebar-nav .badge {
        background: #ffb400;
        color: #1a1a2e;
        font-size: 10px;
        padding: 2px 8px;
        border-radius: 50px;
        margin-left: auto;
        font-weight: 600;
      }

      .sidebar-footer {
        padding: 20px;
        border-top: 1px solid rgba(255, 255, 255, 0.1);
      }

      .logout-link {
        display: flex;
        align-items: center;
        gap: 14px;
        color: rgba(255, 255, 255, 0.8);
        cursor: pointer;
        transition: all 0.3s;
        padding: 12px;
        border-radius: 8px;
      }

      .logout-link:hover {
        background: rgba(220, 53, 69, 0.2);
        color: white;
      }

      /* ===== MAIN CONTENT ===== */
      .main-content {
        margin-left: 260px;
        flex: 1;
        padding: 30px;
      }

      /* ===== HEADER ===== */
      .dashboard-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 30px;
        flex-wrap: wrap;
        gap: 20px;
      }

      .header-left h1 {
        font-size: 28px;
        font-weight: 700;
        color: #1e3c72;
        margin: 0 0 5px 0;
      }

      .header-left .subtitle {
        font-size: 14px;
        color: #666;
        margin: 0;
      }

      .header-right {
        display: flex;
        align-items: center;
        gap: 20px;
      }

      .search-bar {
        position: relative;
        max-width: 600px;
      }

      .search-bar i {
        position: absolute;
        left: 15px;
        top: 50%;
        transform: translateY(-50%);
        color: #999;
      }

      .search-bar input {
        width: 100%;
        padding: 10px 15px 10px 45px;
        border: 2px solid #e0e0e0;
        border-radius: 25px;
        font-size: 14px;
        transition: border-color 0.3s;
      }

      .search-bar input:focus {
        outline: none;
        border-color: #ffb400;
      }

      .notification-btn {
        position: relative;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        border: none;
        background: white;
        color: #666;
        cursor: pointer;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        transition: all 0.3s;
        font-size: 18px;
      }

      .notification-btn:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
        transform: translateY(-2px);
      }

      .notification-badge {
        position: absolute;
        top: -5px;
        right: -5px;
        background: #dc3545;
        color: white;
        font-size: 10px;
        padding: 2px 6px;
        border-radius: 10px;
        font-weight: 600;
      }

      .user-profile {
        display: flex;
        align-items: center;
        gap: 10px;
        cursor: pointer;
        padding: 5px 15px 5px 5px;
        border-radius: 25px;
        background: white;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        transition: all 0.3s;
      }

      .user-profile:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
      }

      .avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: linear-gradient(135deg, #ffb400, #ffa502);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 16px;
        border: 2px solid #ffb400;
      }

      .user-name {
        font-size: 14px;
        font-weight: 600;
        color: #333;
      }

      /* ===== STATS ===== */
      .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
        gap: 20px;
        margin-bottom: 30px;
      }

      .stat-card {
        background: white;
        padding: 20px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        gap: 15px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      }

      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: white;
      }

      .stat-info h3 {
        font-size: 24px;
        font-weight: 700;
        margin: 0;
        color: #1e3c72;
      }

      .stat-info .unit {
        font-size: 14px;
        color: #666;
      }

      .stat-info p {
        font-size: 14px;
        color: #666;
        margin: 0;
      }

      /* ===== DASHBOARD GRID ===== */
      .dashboard-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
        gap: 20px;
      }

      .card {
        background: white;
        border-radius: 12px;
        padding: 20px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
      }

      .card-header h2 {
        font-size: 18px;
        font-weight: 600;
        color: #1e3c72;
        margin: 0;
      }

      .view-all {
        color: #ffb400;
        text-decoration: none;
        font-size: 14px;
        font-weight: 600;
      }

      /* ===== RESUME COURSES ===== */
      .courses-list {
        display: flex;
        flex-direction: column;
        gap: 15px;
      }

      .course-item {
        display: flex;
        gap: 15px;
        padding: 15px;
        background: #f8f9fa;
        border-radius: 8px;
        transition: transform 0.3s;
      }

      .course-item:hover {
        transform: translateX(5px);
      }

      .course-image {
        position: relative;
        width: 100px;
        height: 70px;
        border-radius: 8px;
        overflow: hidden;
      }

      .course-image img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .course-placeholder {
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 24px;
      }

      .progress-overlay {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 4px;
        background: rgba(0, 0, 0, 0.3);
      }

      .progress-bar {
        height: 100%;
        background: #ffb400;
      }

      .course-info h4 {
        margin: 0 0 5px 0;
        color: #1e3c72;
        font-size: 14px;
      }

      .instructor {
        color: #666;
        font-size: 12px;
        margin: 0 0 10px 0;
      }

      .course-progress {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .course-progress span {
        font-size: 12px;
        color: #666;
      }

      .btn-continue {
        background: #ffb400;
        border: none;
        padding: 5px 15px;
        border-radius: 5px;
        color: #1a1a1a;
        font-size: 12px;
        font-weight: 600;
        cursor: pointer;
      }

      /* ===== RECENT ACTIVITY ===== */
      .activity-list {
        display: flex;
        flex-direction: column;
        gap: 15px;
      }

      .activity-item {
        display: flex;
        gap: 15px;
        padding: 10px;
        border-radius: 8px;
        transition: background 0.3s;
      }

      .activity-item:hover {
        background: #f8f9fa;
      }

      .activity-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        flex-shrink: 0;
      }

      .activity-content h4 {
        margin: 0 0 5px 0;
        color: #1e3c72;
        font-size: 14px;
      }

      .activity-content p {
        margin: 0 0 5px 0;
        color: #666;
        font-size: 12px;
      }

      .time {
        color: #999;
        font-size: 11px;
      }

      /* ===== RECOMMENDED COURSES ===== */
      .courses-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        gap: 15px;
      }

      .course-card {
        background: #f8f9fa;
        border-radius: 8px;
        overflow: hidden;
        transition:
          transform 0.3s,
          box-shadow 0.3s;
      }

      .course-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
      }

      .course-card-image {
        position: relative;
        height: 100px;
        overflow: hidden;
      }

      .course-badge {
        position: absolute;
        top: 10px;
        left: 10px;
        background: #28a745;
        color: white;
        padding: 3px 8px;
        border-radius: 5px;
        font-size: 11px;
        font-weight: 600;
      }

      .course-card-content {
        padding: 12px;
      }

      .course-card-content h4 {
        margin: 0 0 5px 0;
        color: #1e3c72;
        font-size: 13px;
      }

      .course-card-content .instructor {
        font-size: 11px;
        margin-bottom: 8px;
      }

      .course-meta {
        display: flex;
        justify-content: space-between;
        font-size: 11px;
        color: #666;
      }

      .rating {
        color: #ffc107;
      }

      /* ===== AI ASSISTANT PROMO ===== */
      .ai-assistant-promo {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 30px;
        grid-column: 1 / -1;
      }

      .ai-content {
        flex: 1;
      }

      .ai-content h2 {
        margin: 0 0 10px 0;
        font-size: 24px;
      }

      .ai-content p {
        margin: 0 0 20px 0;
        opacity: 0.9;
        max-width: 500px;
      }

      .btn-ai {
        background: white;
        color: #764ba2;
        border: none;
        padding: 12px 25px;
        border-radius: 25px;
        font-weight: 600;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
        gap: 10px;
        transition: transform 0.3s;
      }

      .btn-ai:hover {
        transform: scale(1.05);
      }

      .ai-image {
        font-size: 120px;
        opacity: 0.3;
      }

      /* ===== LEARNING STATISTICS ===== */
      .learning-stats {
        grid-column: 1 / -1;
      }

      .stats-content {
        display: grid;
        grid-template-columns: 2fr 1fr;
        gap: 30px;
      }

      .chart-section h3 {
        margin: 0 0 20px 0;
        color: #1e3c72;
        font-size: 14px;
      }

      .chart-bars {
        display: flex;
        align-items: flex-end;
        justify-content: space-around;
        height: 150px;
        gap: 10px;
        background: #f8f9fa;
        padding: 20px;
        border-radius: 8px;
      }

      .bar {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
      }

      .bar-fill {
        width: 100%;
        background: linear-gradient(180deg, #ffb400 0%, #ffa502 100%);
        border-radius: 5px 5px 0 0;
        min-height: 20px;
        transition: height 0.3s;
      }

      .bar-label {
        font-size: 11px;
        color: #666;
      }

      .subject-distribution h3 {
        margin: 0 0 15px 0;
        color: #1e3c72;
        font-size: 14px;
      }

      .distribution-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
      }

      .subject-info {
        display: flex;
        align-items: center;
        gap: 10px;
      }

      .subject-color {
        width: 12px;
        height: 12px;
        border-radius: 50%;
      }

      .subject-stats {
        display: flex;
        align-items: center;
        gap: 10px;
      }

      .percentage {
        font-weight: 600;
        color: #1e3c72;
        font-size: 14px;
        min-width: 40px;
        text-align: right;
      }

      .mini-progress {
        width: 80px;
        height: 6px;
        background: #e0e0e0;
        border-radius: 3px;
        overflow: hidden;
      }

      .progress-fill {
        height: 100%;
        border-radius: 3px;
      }

      /* ===== RESPONSIVE ===== */
      @media (max-width: 1024px) {
        .sidebar {
          transform: translateX(-100%);
        }

        .sidebar.open {
          transform: translateX(0);
        }

        .main-content {
          margin-left: 0;
        }

        .dashboard-grid {
          grid-template-columns: 1fr;
        }

        .stats-content {
          grid-template-columns: 1fr;
        }
      }

      @media (max-width: 768px) {
        .header-right {
          flex-wrap: wrap;
        }

        .search-bar {
          order: 3;
          max-width: 100%;
          width: 100%;
        }

        .stats-grid {
          grid-template-columns: repeat(2, 1fr);
        }
      }
    `,
  ],
})
export class StudentDashboardComponent implements OnInit {
  userFirstName: string = 'Marie';
  unreadMessages = 3;
  notifications = 5;

  stats = {
    courses: 12,
    hours: 48,
    certificates: 5,
    points: 1250,
  };

  resumeCourses: Course[] = [
    {
      id: '1',
      title: 'Algorithmes avancés',
      teacherName: 'Dr. Jean Dupont',
      progress: 75,
      category: 'Informatique',
    },
    {
      id: '2',
      title: 'Physique-Chimie Terminale',
      teacherName: 'Pr. Marie Martin',
      progress: 40,
      category: 'Sciences',
    },
    {
      id: '3',
      title: 'Les limites et continuité',
      teacherName: 'Dr. Paul Bernard',
      progress: 100,
      category: 'Mathématiques',
    },
  ];

  recentActivities: Activity[] = [
    {
      id: '1',
      type: 'quiz',
      title: 'Quiz terminé',
      description: 'Vous avez terminé le quiz Les intégrales',
      time: 'Il y a 2h',
      icon: 'check-circle',
      color: '#6f42c1',
    },
    {
      id: '2',
      type: 'message',
      title: 'Nouveau message',
      description: 'Dr. Martin vous a envoyé un message',
      time: 'Il y a 5h',
      icon: 'envelope',
      color: '#28a745',
    },
    {
      id: '3',
      type: 'course',
      title: 'Cours ajouté aux favoris',
      description: 'Les suites numériques',
      time: 'Il y a 1j',
      icon: 'heart',
      color: '#17a2b8',
    },
    {
      id: '4',
      type: 'payment',
      title: 'Paiement réussi',
      description: 'Physique-Chimie',
      time: 'Il y a 2j',
      icon: 'check',
      color: '#ffc107',
    },
  ];

  recommendedCourses: Course[] = [
    {
      id: '4',
      title: 'Mathématiques Terminale',
      teacherName: 'Dr. Sophie Laurent',
      progress: 0,
      category: 'Gratuit',
      rating: 4.8,
      price: 'Gratuit',
    },
    {
      id: '5',
      title: 'Chimie organique',
      teacherName: 'Pr. Ahmed Benali',
      progress: 0,
      category: 'Gratuit',
      rating: 4.7,
      price: 'Gratuit',
    },
    {
      id: '6',
      title: 'Algorithmique',
      teacherName: 'Dr. Claire Petit',
      progress: 0,
      category: 'Payant',
      rating: 4.9,
      price: 'Payant',
    },
    {
      id: '7',
      title: 'Physique Terminale',
      teacherName: 'Pr. Lucas Moreau',
      progress: 0,
      category: 'Gratuit',
      rating: 4.6,
      price: 'Gratuit',
    },
  ];

  learningMonths = [
    { label: 'Janv', value: 35 },
    { label: 'Fév', value: 45 },
    { label: 'Mars', value: 60 },
    { label: 'Avr', value: 40 },
    { label: 'Mai', value: 55 },
    { label: 'Juin', value: 70 },
  ];

  subjectDistribution = [
    { name: 'Mathématiques', percentage: 40, color: '#667eea' },
    { name: 'Physique-Chimie', percentage: 30, color: '#f093fb' },
    { name: 'Algorithmique', percentage: 20, color: '#4facfe' },
    { name: 'Autres', percentage: 10, color: '#43e97b' },
  ];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user?.firstName) {
      this.userFirstName = user.firstName;
    }
  }

  logout(): void {
    this.authService.logout();
  }
}

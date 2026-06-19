// features/courses/services/course.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';

export interface Course {
  uuid: string;
  title: string;
  description: string;
  typeCourses: 'PAYANT' | 'GRATUIT';
  thumbnailUrl?: string;
  status: 'DRAFT' | 'PUBLISHED' | 'IN_PROGRESS' | 'COMPLETED' | 'ARCHIVED';
  levelStudent: 'ELEVE' | 'COLLEGIEN' | 'LYCEEN' | 'ETUDIANT' | 'PROFESSIONEL';
  teacherUuid: string;
  startDate?: string;
  endDate?: string;
  durationHours?: number;
  credits?: number;
  contents?: Content[];
}

export interface Content {
  uuid?: string;
  title: string;
  description?: string;
  contentType: 'VIDEO' | 'DOCUMENT' | 'QUIZ' | 'ASSIGNMENT' | 'LINK' | 'DISCUSSION';
  contentUrl?: string;
  orderIndex: number;
  durationMinutes?: number;
  isRequired?: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class CourseService {
  constructor(private apiService: ApiService) {}

  // ==================== GESTION DES COURS ====================

  // Créer un nouveau cours
  createCourse(course: Course): Observable<any> {
    return this.apiService.post('api/courses', course);
  }

  // Récupérer un cours par UUID
  getCourseByUuid(uuid: string): Observable<any> {
    return this.apiService.get(`api/courses/${uuid}`);
  }

  // Mettre à jour un cours
  updateCourse(uuid: string, course: Course): Observable<any> {
    return this.apiService.put(`api/courses/${uuid}`, course);
  }

  // Publier un cours
  publishCourse(uuid: string): Observable<any> {
    return this.apiService.post(`api/courses/${uuid}/publish`);
  }

  // Supprimer un cours
  deleteCourse(uuid: string): Observable<any> {
    return this.apiService.delete(`api/courses/${uuid}`);
  }

  // Récupérer les cours d'un enseignant
  getTeacherCourses(teacherUuid: string): Observable<any> {
    return this.apiService.get(`api/courses/teacher/${teacherUuid}`);
  }

  // Récupérer les cours par niveau d'étudiant
  getCoursesByStudentLevel(studentUuid: string): Observable<any> {
    return this.apiService.get(`api/courses/${studentUuid}/courses`);
  }

  // ==================== GESTION DES CONTENUS ====================

  // Ajouter un contenu à un cours
  addContentToCourse(courseUuid: string, content: Content): Observable<any> {
    return this.apiService.post(`api/courses/${courseUuid}/contents`, content);
  }

  // Ajouter plusieurs contenus en lot
  bulkAddContents(courseUuid: string, contents: Content[]): Observable<any> {
    return this.apiService.post(`api/courses/${courseUuid}/contents/bulk`, contents);
  }

  // Récupérer tous les contenus d'un cours
  getCourseContents(courseUuid: string): Observable<any> {
    return this.apiService.get(`api/courses/${courseUuid}/contents`);
  }

  // Mettre à jour un contenu
  updateContent(courseUuid: string, contentUuid: string, content: Content): Observable<any> {
    return this.apiService.put(`api/courses/${courseUuid}/contents/${contentUuid}`, content);
  }

  // Réorganiser les contenus
  reorderContents(courseUuid: string, contentUuids: string[]): Observable<any> {
    return this.apiService.post(`api/courses/${courseUuid}/contents/reorder`, contentUuids);
  }

  // Supprimer un contenu
  deleteContent(courseUuid: string, contentUuid: string): Observable<any> {
    return this.apiService.delete(`api/courses/${courseUuid}/contents/${contentUuid}`);
  }
}

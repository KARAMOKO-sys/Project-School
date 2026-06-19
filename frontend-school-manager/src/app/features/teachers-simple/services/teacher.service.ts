// features/teachers/services/teacher.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';

export interface Teacher {
  uuid: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  teacherNumber?: string;
  birthDate?: string;
  phoneNumber?: string;
  status: string;
  profilePictureUrl?: string;
  specialties?: string[];
  levelTeacher?: string;
  createdAt: string;
}

export interface TeacherRegisterRequest {
  email: string;
  firstName: string;
  lastName: string;
  password: string;
  birthDate?: string;
  phoneNumber?: string;
  specialties?: string[];
  levelTeacher?: string;
}

@Injectable({
  providedIn: 'root',
})
export class TeacherService {
  constructor(private apiService: ApiService) {}

  // Inscription d'un enseignant
  registerTeacher(data: TeacherRegisterRequest): Observable<any> {
    return this.apiService.post('api/teachers/register', data);
  }

  // Inscription simplifiée
  registerTeacherSimple(data: any): Observable<any> {
    return this.apiService.post('api/teachers-simple/register-simple', data);
  }

  // Récupérer un enseignant par UUID
  getTeacherByUuid(uuid: string): Observable<any> {
    return this.apiService.get(`api/teachers-simple/${uuid}`);
  }

  // Récupérer tous les enseignants avec pagination
  getTeachers(page: number = 0, size: number = 10): Observable<any> {
    return this.apiService.get('api/teachers-simple', { page, size });
  }

  // Mettre à jour un enseignant
  updateTeacher(uuid: string, data: any): Observable<any> {
    return this.apiService.put(`api/teachers-simple/${uuid}`, data);
  }

  // Mettre à jour le profil d'un enseignant
  updateTeacherProfile(uuid: string, data: any): Observable<any> {
    return this.apiService.put(`api/teachers-simple/profil/${uuid}`, data);
  }

  // Changer le statut d'un enseignant
  updateTeacherStatus(uuid: string, status: string, reason?: string): Observable<any> {
    return this.apiService.patch(`api/teachers-simple/${uuid}/status`, { status, reason });
  }

  // Supprimer un enseignant
  deleteTeacher(uuid: string): Observable<any> {
    return this.apiService.delete(`api/teachers-simple/${uuid}`);
  }

  // Rechercher des enseignants
  searchTeachers(keyword: string, page: number = 0, size: number = 10): Observable<any> {
    return this.apiService.get('api/teachers-simple/search', { keyword, page, size });
  }

  // Récupérer les cours d'un enseignant
  getTeacherCourses(teacherUuid: string): Observable<any> {
    return this.apiService.get(`api/courses/teacher/${teacherUuid}`);
  }
}

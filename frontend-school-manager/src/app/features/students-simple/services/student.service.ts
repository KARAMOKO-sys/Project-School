// features/students/services/student.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from '../../../core/services/api.service';

export interface Student {
  uuid: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  birthDate?: string;
  phoneNumber?: string;
  levelStudent?: string;
  profilePictureUrl?: string;
  status: string;
  createdAt: string;
  token?: string;
}

export interface StudentUpdateRequest {
  firstName?: string;
  lastName?: string;
  email?: string;
  phoneNumber?: string;
  birthDate?: string;
  levelStudent?: string;
}

@Injectable({
  providedIn: 'root',
})
export class StudentService {
  constructor(private apiService: ApiService) {}

  // Récupérer un étudiant par UUID
  getStudentByUuid(uuid: string): Observable<any> {
    return this.apiService.get(`api/students/${uuid}`);
  }

  // Mettre à jour un étudiant
  updateStudent(uuid: string, data: StudentUpdateRequest): Observable<any> {
    return this.apiService.put(`api/students/${uuid}`, data);
  }

  // Mettre à jour le profil d'un étudiant
  updateStudentProfile(uuid: string, data: StudentUpdateRequest): Observable<any> {
    return this.apiService.put(`api/students/profil/${uuid}`, data);
  }

  // Changer le mot de passe
  changePassword(uuid: string, currentPassword: string, newPassword: string): Observable<any> {
    return this.apiService.post(`api/students/${uuid}/change-password`, {
      currentPassword,
      newPassword,
    });
  }

  // Supprimer un étudiant (soft delete)
  deleteStudent(uuid: string): Observable<any> {
    return this.apiService.delete(`api/students/${uuid}`);
  }

  // Récupérer les cours d'un étudiant
  getStudentCourses(studentUuid: string): Observable<any> {
    return this.apiService.get(`api/students/${studentUuid}/courses`);
  }

  // Récupérer les cours par niveau
  getCoursesByLevel(studentUuid: string): Observable<any> {
    return this.apiService.get(`api/courses/${studentUuid}/courses`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Patient } from '../models/patient';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private readonly baseUrl = 'http://localhost:8080/hitic/api/patient';

  constructor(private http: HttpClient) {}

  getPresentPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/list/present`);
  }

  getAbsentPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/list/absent`);
  }
}
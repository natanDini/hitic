// operator.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Operator } from '../models/operator.model';
import { Conversation } from '../models/conversation.model';

@Injectable({
  providedIn: 'root'
})
export class OperatorService {

  constructor(private http: HttpClient) {}

  getOperators(): Observable<Operator[]> {
    const url = 'http://localhost:8080/hitic/api/operator/list';
    return this.http.get<Operator[]>(url);
  }

  getConversations(operatorId: string): Observable<Conversation[]> {
    const url = `http://localhost:8080/hitic/api/operator/${operatorId}/conversations`;
    return this.http.get<Conversation[]>(url);
  }  
}
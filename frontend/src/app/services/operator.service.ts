// operator.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Operator } from '../models/operator.model';
import { Conversation } from '../models/conversation.model';
import { Message } from '../models/message.model';
import { SendMessageRequest } from '../models/send-message-request.model';


@Injectable({
  providedIn: 'root'
})
export class OperatorService {

  constructor(private http: HttpClient) { }

  getOperators(): Observable<Operator[]> {
    const url = 'http://localhost:8080/hitic/api/operator/list';
    return this.http.get<Operator[]>(url);
  }

  getConversations(operatorId: string): Observable<Conversation[]> {
    const url = `http://localhost:8080/hitic/api/operator/${operatorId}/conversations`;
    return this.http.get<Conversation[]>(url);
  }

  getMessages(conversationId: number): Observable<Message[]> {
    const url = `http://localhost:8080/hitic/api/conversation/${conversationId}/message`;
    return this.http.get<Message[]>(url);
  }

  /**
   * Faz um POST simples usando fetch e lê o corpo como uma
   * stream SSE. Para cada linha do formato "data: { ... }",
   * extrai o `chunk` e emite via Observer.
   */
  // Exemplo de método no seu OperatorService (ou serviço equivalente)
  sendMessageSse(request: SendMessageRequest): Observable<string> {
    const url = 'http://localhost:8080/hitic/api/conversation/send-message';
  
    return new Observable<string>((observer) => {
      const controller = new AbortController();
  
      fetch(url, {
        method: 'POST',
        body: JSON.stringify(request),
        credentials: 'include',
        headers: {
          'Accept': 'text/event-stream',
          'Cache-Control': 'no-cache',
          'Connection': 'keep-alive',
          'Content-Type': 'application/json',
        },
        signal: controller.signal
      })
        .then((response) => {
          if (!response.body) {
            observer.error(new Error('Este navegador não suporta leitura de stream na resposta.'));
            return;
          }
  
          const reader = response.body.getReader();
          const decoder = new TextDecoder('utf-8');
          let buffer = '';
  
          const readChunk = () => {
            reader.read().then(({ done, value }) => {
              if (done) {
                observer.complete();
                return;
              }
  
              buffer += decoder.decode(value, { stream: true });
  
              const events = buffer.split('\n\n');
              buffer = events.pop() || ''; // mantém o resto para próxima leitura
  
              for (const event of events) {
                const dataLine = event.split('\n').find(line => line.startsWith('data:'));
                if (dataLine) {
                  const jsonStr = dataLine.replace('data:', '').trim();
                  try {
                    const parsed = JSON.parse(jsonStr);
                    if (parsed.chunk !== undefined) {
                      observer.next(parsed.chunk);
                    }
                  } catch (e) {
                    console.warn('Erro parseando chunk:', jsonStr, e);
                  }
                }
              }
  
              readChunk();
            }).catch((err) => observer.error(err));
          };
  
          readChunk();
        })
        .catch((err) => observer.error(err));
  
      return () => controller.abort();
    });
  }  
}
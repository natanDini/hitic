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
  sendMessageSse(request: SendMessageRequest): Observable<string> {
    const url = 'http://localhost:8080/hitic/api/conversation/send-message';
  
    return new Observable<string>((observer) => {
      fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(request)
      })
        .then((response) => {
          if (!response.ok || !response.body) {
            observer.complete();
            return;
          }
  
          const reader = response.body.getReader();
          const decoder = new TextDecoder();
          let buffer = '';
  
          function readChunk() {
            reader
              .read()
              .then(({ value, done }) => {
                if (done) {
                  // Se o servidor fechou a conexão, encerramos
                  observer.complete();
                  return;
                }
  
                // Decodifica o pedaço recém-lido
                buffer += decoder.decode(value, { stream: true });
  
                /**
                 * Quebramos em linhas por '\n' e tratamos cada "data: ...".
                 * Se o backend mandar SSE no formato tradicional (uma linha "data: {...}" e depois '\n\n'),
                 * pode ser que você precise usar .split('\n\n') ou manipular de outro jeito.
                 * Mas se o back tá mandando só `\n`, fique com .split('\n') normal.
                 */
                const lines = buffer.split('\n');
                // O que sobrou (última linha) pode estar incompleta e fica em buffer
                buffer = lines.pop() || '';
  
                // Agora processamos cada linha completa
                for (const line of lines) {
                  // Checa se a linha começa com "data:"
                  if (line.startsWith('data:')) {
                    // Pega só a parte JSON (sem 'data:')
                    const jsonStr = line.substring('data:'.length).trim();
  
                    try {
                      // Faz o parse do JSON
                      const parsed = JSON.parse(jsonStr);
                      // Se o backend tá mandando {"chunk": "...texto aqui..."}
                      if (parsed && parsed.chunk) {
                        // Emite esse pedaço pro .subscribe() lá em quem chamou
                        observer.next(parsed.chunk);
                      }
                    } catch (err) {
                      // Se der erro no parse, ignora ou loga
                      console.error('JSON inválido:', err);
                    }
                  }
                }
  
                // Continua lendo até acabar
                readChunk();
              })
              .catch((err) => observer.error(err));
          }
  
          // Inicia a leitura
          readChunk();
        })
        .catch((err) => observer.error(err));
    });
  } 
}
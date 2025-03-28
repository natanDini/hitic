import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OperatorService } from '../../services/operator.service';
import { Operator } from '../../models/operator.model';
import { Conversation } from '../../models/conversation.model';
import { Message } from '../../models/message.model';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SendMessageRequest } from '../../models/send-message-request.model';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { MarkdownModule } from 'ngx-markdown';

@Component({
  standalone: true,
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, DatePipe, MarkdownModule]
})
export class ChatComponent implements OnInit {
  operators: Operator[] = [];
  conversations: Conversation[] = [];
  messages: Message[] = [];

  prompt: string = '';
  isSending: boolean = false;
  chatActive: boolean = false;

  createOperatorForm!: FormGroup;
  showCreateOperatorModal: boolean = false;
  isCreatingOperator: boolean = false;

  selectedOperatorId: string | null = null;
  selectedConversationId: number | null = null;

  constructor(private fb: FormBuilder, private operatorService: OperatorService, private http: HttpClient) { }

  ngOnInit(): void {
    this.createOperatorForm = this.fb.group({
      name: [''],
      description: [''],
      promptTemplate: [''],
      archives: [[]]
    });

    this.operatorService.getOperators().subscribe((data) => {
      this.operators = data;

      // Se houver operadores, seleciona o primeiro automaticamente
      if (this.operators.length > 0) {
        this.selectOperator(this.operators[0]);
      }
    });
  }

  selectOperator(operator: Operator): void {
    this.selectedOperatorId = operator.id;
    this.selectedConversationId = null;
    this.messages = [];

    this.startNewConversation()

    this.operatorService.getConversations(operator.id).subscribe((data) => {
      this.conversations = data;
    });
  }

  selectConversation(conv: Conversation): void {
    this.selectedConversationId = conv.id;
    this.chatActive = true;

    this.operatorService.getMessages(conv.id).subscribe((data) => {
      this.messages = data;
    });
  }

  isOperatorSelected(id: string): boolean {
    return this.selectedOperatorId === id;
  }

  isConversationSelected(id: number): boolean {
    return this.selectedConversationId === id;
  }

  startNewConversation(): void {
    this.selectedConversationId = null;
    this.messages = [];
    this.chatActive = false; // voltamos à tela inicial
  }


  sendMessage(): void {
    // 1. Valida prompt e operador
    if (!this.prompt.trim() || !this.selectedOperatorId) return;

    // 2. Monta o request
    const req: SendMessageRequest = {
      conversationId: this.selectedConversationId || 0,
      operatorId: this.selectedOperatorId,
      prompt: this.prompt.trim()
    };

    this.isSending = true;
    let fullAnswer = '';

    // 3. Insere mensagem do usuário na tela
    this.messages.push({
      id: Date.now(),
      questionMessage: req.prompt,
      answerMessage: '' // será populado ao receber chunks
    });

    this.chatActive = true;

    // limpa o campo
    this.prompt = '';

    // 4. Chama o serviço que faz POST + SSE
    this.operatorService.sendMessageSse(req).subscribe({
      next: (chunk) => {
        // Concatena os chunks como vêm do servidor, sem manipulação
        fullAnswer += chunk;

        const last = this.messages[this.messages.length - 1];
        if (last) {
          last.answerMessage = fullAnswer;
        }
      },
      error: (err) => {
        console.error('Erro SSE:', err);
        this.isSending = false;
      },
      complete: () => {
        this.isSending = false;
        console.log('SSE encerrado.');

        // Sempre recarrega a lista de conversas
        this.operatorService.getConversations(req.operatorId).subscribe((data) => {
          this.conversations = data;

          // Se for conversa nova, pega a mais recente (data[0]), 
          // senão pega a mesma que o usuário está usando (req.conversationId).
          let conversationToUse;

          if (req.conversationId === 0) {
            // Nova conversa --> assumimos que a mais recente é a primeira
            conversationToUse = data[0];
          } else {
            // Conversa existente --> acha a conversa pelo ID
            conversationToUse = data.find(c => c.id === req.conversationId);
          }

          if (conversationToUse) {
            this.selectedConversationId = conversationToUse.id;
            // Carrega a lista de mensagens novamente
            this.operatorService.getMessages(conversationToUse.id).subscribe((msgs) => {
              this.messages = msgs;
            });
          }
        });
      }
    });
  }

  openCreateOperatorModal(): void {
    this.showCreateOperatorModal = true;
  }

  selectedFiles: File[] = [];

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
  
    if (input.files && input.files.length > 0) {
      const files: File[] = Array.from(input.files);
      this.selectedFiles.push(...files);
      this.createOperatorForm.patchValue({ archives: this.selectedFiles });
    }
  }  

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
    this.createOperatorForm.patchValue({ archives: this.selectedFiles });
  }

  closeCreateOperatorModal(): void {
    if (!this.isCreatingOperator) {
      this.showCreateOperatorModal = false;
      this.createOperatorForm.reset();
      this.selectedFiles = [];
    }
  }

  submitCreateOperator(): void {
    if (this.createOperatorForm.invalid) return;
    this.isCreatingOperator = true;

    const formData = new FormData();
    formData.append('name', this.createOperatorForm.value.name);
    formData.append('description', this.createOperatorForm.value.description);
    formData.append('promptTemplate', this.createOperatorForm.value.promptTemplate);

    for (const file of this.createOperatorForm.value.archives) {
      formData.append('archives', file);
    }

    this.http.post('http://localhost:8080/hitic/api/operator/create', formData).subscribe({
      next: () => {
        this.isCreatingOperator = false;
        this.showCreateOperatorModal = false;
        this.createOperatorForm.reset();
        this.selectedFiles = [];
        this.operatorService.getOperators().subscribe((data) => {
          this.operators = data;
        });
      },
      error: () => {
        this.isCreatingOperator = false;
      }
    });
  }
}
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OperatorService } from '../../services/operator.service';
import { Operator } from '../../models/operator.model';
import { Conversation } from '../../models/conversation.model';
import { Message } from '../../models/message.model';
import { DatePipe } from '@angular/common';

import { MarkdownModule } from 'ngx-markdown';

@Component({
  standalone: true,
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
  imports: [CommonModule, DatePipe, MarkdownModule]
})
export class ChatComponent implements OnInit {
  operators: Operator[] = [];
  conversations: Conversation[] = [];
  messages: Message[] = [];

  selectedOperatorId: string | null = null;
  selectedConversationId: number | null = null;

  constructor(private operatorService: OperatorService) {}

  ngOnInit(): void {
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

    this.operatorService.getConversations(operator.id).subscribe((data) => {
      this.conversations = data;
    });
  }

  selectConversation(conv: Conversation): void {
    this.selectedConversationId = conv.id;

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
}
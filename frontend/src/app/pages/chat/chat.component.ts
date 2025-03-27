import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OperatorService } from '../../services/operator.service';
import { Operator } from '../../models/operator.model';
import { Conversation } from '../../models/conversation.model';
import { DatePipe } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
  imports: [CommonModule, DatePipe]
})
export class ChatComponent implements OnInit {
  operators: Operator[] = [];
  conversations: Conversation[] = [];
  selectedOperatorId: string | null = null;

  constructor(private operatorService: OperatorService) {}

  ngOnInit(): void {
    this.operatorService.getOperators().subscribe((data) => {
      this.operators = data;
    });
  }

  selectOperator(operator: Operator): void {
    this.selectedOperatorId = operator.id;
    this.operatorService.getConversations(operator.id).subscribe((data) => {
      this.conversations = data;
    });
  }

  isOperatorSelected(id: string): boolean {
    return this.selectedOperatorId === id;
  }
}
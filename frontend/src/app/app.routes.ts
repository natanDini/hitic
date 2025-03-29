import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ChatComponent } from './pages/chat/chat.component';
import { AttendanceComponent } from './pages/attendance/attendance.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'chat', component: ChatComponent },
  { path: 'attendance', component: AttendanceComponent }
];

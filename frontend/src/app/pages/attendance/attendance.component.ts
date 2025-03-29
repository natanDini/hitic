import { Component } from '@angular/core';
import { VideoCaptureComponent } from '../../components/video-capture/video-capture.component';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [VideoCaptureComponent],
  templateUrl: './attendance.component.html',
  styleUrl: './attendance.component.scss'
})
export class AttendanceComponent {

}

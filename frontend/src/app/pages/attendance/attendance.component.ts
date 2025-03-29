import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { NgxMaskDirective } from 'ngx-mask';
import { Patient } from '../../models/patient';
import { AttendanceService } from '../../services/attendance.service';
import { NgZone } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, NgxMaskDirective, FormsModule],
  templateUrl: './attendance.component.html',
  styleUrl: './attendance.component.scss'
})
export class AttendanceComponent implements OnInit {
  presentes: Patient[] = [];
  ausentes: any[] = [];
  recognizedPatient: Patient | null = null;
  recognitionError: string | null = null;
  isLoading = false;

  newPatient = {
    name: '',
    cpf: '',
    birthDate: ''
  };

  isRegistering = false;
  registerMessage: string | null = null;


  @ViewChild('video', { static: true }) video!: ElementRef<HTMLVideoElement>;

  constructor(private http: HttpClient, private zone: NgZone, private attendanceService: AttendanceService) { }

  ngOnInit() {
    this.startCamera();
    this.loadPresentes();
    this.loadAusentes();
  }

  loadPresentes(): void {
    this.attendanceService.getPresentPatients().subscribe({
      next: (data) => this.presentes = data,
      error: (err) => console.error('Erro ao buscar pacientes presentes:', err)
    });
  }

  loadAusentes(): void {
    this.attendanceService.getAbsentPatients().subscribe({
      next: (data) => this.ausentes = data,
      error: (err) => console.error('Erro ao buscar pacientes ausentes:', err)
    });
  }

  startCamera() {
    navigator.mediaDevices.getUserMedia({ video: true })
      .then(stream => {
        this.video.nativeElement.srcObject = stream;
      })
      .catch(err => {
        console.error("Erro ao acessar a câmera:", err);
      });
  }

  registerPatient() {
    if (this.isRegistering) return;

    this.isRegistering = true;
    this.registerMessage = null;

    const video = this.video.nativeElement;
    const canvas = document.createElement('canvas');
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    const context = canvas.getContext('2d');
    if (!context) {
      this.isRegistering = false;
      this.registerMessage = 'Erro ao capturar imagem da câmera.';
      return;
    }

    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    canvas.toBlob((blob) => {
      if (!blob) {
        this.isRegistering = false;
        this.registerMessage = 'Erro ao converter imagem.';
        return;
      }

      const formData = new FormData();
      formData.append('name', this.newPatient.name);
      formData.append('cpf', this.newPatient.cpf);
      formData.append('birthDate', this.newPatient.birthDate);
      formData.append('patientPicture', blob, 'foto.jpg');

      this.http.post('http://localhost:8080/hitic/api/patient/register', formData)
        .subscribe({
          next: () => {
            this.zone.run(() => {
              this.registerMessage = 'Paciente cadastrado com sucesso!';
              this.isRegistering = false;
              this.newPatient = { name: '', cpf: '', birthDate: '' };
              this.loadPresentes();
              this.loadAusentes();
            });
          },
          error: (err) => {
            console.error('Erro ao cadastrar paciente:', err);
            this.zone.run(() => {
              this.registerMessage = 'Erro ao cadastrar paciente.';
              this.isRegistering = false;
            });
          }
        });
    }, 'image/jpeg');
  }

  captureFrame() {
    if (this.isLoading) return; // Evita chamadas múltiplas

    this.isLoading = true;
    this.recognizedPatient = null;
    this.recognitionError = null;

    const video = this.video.nativeElement;
    const canvas = document.createElement('canvas');
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    const context = canvas.getContext('2d');
    if (!context) {
      console.error("Não foi possível obter contexto 2D do canvas.");
      this.isLoading = false;
      return;
    }

    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    canvas.toBlob((blob) => {
      if (!blob) {
        console.error("Não foi possível converter canvas em blob.");
        this.zone.run(() => this.isLoading = false);
        return;
      }

      const formData = new FormData();
      formData.append('file', blob, 'frame.jpg');

      this.http.post<Patient>('http://localhost:8080/hitic/api/patient/recognize', formData, { observe: 'response' })
        .subscribe({
          next: (response) => {
            this.zone.run(() => {
              if (response.status === 200 && response.body) {
                this.recognizedPatient = response.body;
                this.recognitionError = null;
                this.loadAusentes();
                this.loadPresentes();
              } else {
                this.recognitionError = "Paciente não Identificado ou Face não Localizada.";
              }
              this.isLoading = false;
            });
          },
          error: (error) => {
            console.error("Erro no envio da imagem:", error);
            this.zone.run(() => {
              this.recognitionError = "Paciente não Identificado ou Face não Localizada.";
              this.isLoading = false;
            });
          }
        });
    }, 'image/jpeg');
  }

  switchAbsent() {
    this.http.post('http://localhost:8080/hitic/api/patient/switch-absent', {})
      .subscribe({
        next: (res) => {
          if (res) {
            this.loadPresentes();
            this.loadAusentes();
          }
        },
        error: (err) => {
          console.error('Erro ao alternar ausentes:', err);
        }
      });
  }
}

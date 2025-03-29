import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [],
  templateUrl: './attendance.component.html',
  styleUrl: './attendance.component.scss'
})
export class AttendanceComponent implements OnInit {
  @ViewChild('video', { static: true }) video!: ElementRef<HTMLVideoElement>;

  constructor(private http: HttpClient) { }

  ngOnInit() {
    this.startCamera();
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

  captureFrame() {
    const video = this.video.nativeElement;

    // Cria um canvas para desenhar o frame
    const canvas = document.createElement('canvas');
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    const context = canvas.getContext('2d');
    if (!context) {
      console.error("Não foi possível obter contexto 2D do canvas.");
      return;
    }

    // "Fotografa" o frame do vídeo
    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    // Converte o canvas em blob (imagem)
    canvas.toBlob((blob) => {
      if (!blob) {
        console.error("Não foi possível converter canvas em blob.");
        return;
      }

      // Monta um FormData para enviar via multipart/form-data
      const formData = new FormData();
      formData.append('file', blob, 'frame.jpg');

      // Faz POST para o endpoint Python (por exemplo, http://localhost:8000/recognize)
      this.http.post<any>('http://localhost:5002/recognize', formData)
        .subscribe(response => {
          console.log("Resposta do servidor:", response);
          // Exiba mensagem pro usuário, etc.
        }, error => {
          console.error("Erro no envio da imagem:", error);
        });
    }, 'image/jpeg'); // MIME type "image/jpeg" ou "image/png"
  }
}

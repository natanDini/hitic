import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import { provideClientHydration } from '@angular/platform-browser';
import { provideMarkdown } from 'ngx-markdown';
import { provideNgxMask  } from 'ngx-mask';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(), // precisa disso!
    provideClientHydration(),
    provideMarkdown(),
    provideNgxMask ()
  ]
};
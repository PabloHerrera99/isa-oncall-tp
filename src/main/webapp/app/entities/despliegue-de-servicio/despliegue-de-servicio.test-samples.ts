import dayjs from 'dayjs/esm';

import { IDespliegueDeServicio, NewDespliegueDeServicio } from './despliegue-de-servicio.model';

export const sampleWithRequiredData: IDespliegueDeServicio = {
  id: 25352,
  version: 'inasmuch',
  entorno: 'DESARROLLO',
  iniciadoEn: dayjs('2026-11-12T14:28'),
  resultado: 'REVERTIDO',
};

export const sampleWithPartialData: IDespliegueDeServicio = {
  id: 17049,
  version: 'boo maroon at',
  entorno: 'DESARROLLO',
  iniciadoEn: dayjs('2026-11-11T22:27'),
  finalizadoEn: dayjs('2026-11-12T15:16'),
  resultado: 'EXITOSO',
};

export const sampleWithFullData: IDespliegueDeServicio = {
  id: 5871,
  version: 'frantically whose ick',
  entorno: 'STAGING',
  iniciadoEn: dayjs('2026-11-11T23:05'),
  finalizadoEn: dayjs('2026-11-12T04:27'),
  resultado: 'EXITOSO',
};

export const sampleWithNewData: NewDespliegueDeServicio = {
  version: 'phew',
  entorno: 'PRODUCCION',
  iniciadoEn: dayjs('2026-11-11T21:50'),
  resultado: 'REVERTIDO',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

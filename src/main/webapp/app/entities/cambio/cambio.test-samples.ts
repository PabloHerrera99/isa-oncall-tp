import dayjs from 'dayjs/esm';

import { ICambio, NewCambio } from './cambio.model';

export const sampleWithRequiredData: ICambio = {
  id: 28701,
  titulo: 'bleakly',
  tipo: 'CODIGO',
  riesgo: 'ALTO',
  creadoEn: dayjs('2026-11-12T14:03'),
};

export const sampleWithPartialData: ICambio = {
  id: 18787,
  titulo: 'someplace ripe shakily',
  tipo: 'INFRAESTRUCTURA',
  riesgo: 'ALTO',
  ticketUrl: 'oxidize bidet wee',
  creadoEn: dayjs('2026-11-11T17:13'),
};

export const sampleWithFullData: ICambio = {
  id: 498,
  titulo: 'phooey imaginative shocked',
  descripcion: 'but hoarse',
  tipo: 'CONFIGURACION',
  riesgo: 'ALTO',
  ticketUrl: 'gee',
  creadoEn: dayjs('2026-11-12T05:08'),
};

export const sampleWithNewData: NewCambio = {
  titulo: 'yuck scale',
  tipo: 'BASE_DE_DATOS',
  riesgo: 'ALTO',
  creadoEn: dayjs('2026-11-12T08:02'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

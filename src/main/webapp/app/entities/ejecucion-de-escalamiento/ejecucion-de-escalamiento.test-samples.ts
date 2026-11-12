import dayjs from 'dayjs/esm';

import { IEjecucionDeEscalamiento } from './ejecucion-de-escalamiento.model';

export const sampleWithRequiredData: IEjecucionDeEscalamiento = {
  id: 9489,
  vuelta: 3,
  nivel: 10,
  motivo: 'AUTOR_DEL_CAMBIO',
  resultado: 'SIN_DESTINATARIO',
  ejecutadoEn: dayjs('2026-11-11T21:39'),
};

export const sampleWithPartialData: IEjecucionDeEscalamiento = {
  id: 24741,
  vuelta: 3,
  nivel: 4,
  motivo: 'DESTINATARIO_DIRECTO',
  resultado: 'SIN_POLITICA',
  ejecutadoEn: dayjs('2026-11-12T02:38'),
  proximoEn: dayjs('2026-11-12T11:42'),
  detalle: 'overconfidently lazily mid',
};

export const sampleWithFullData: IEjecucionDeEscalamiento = {
  id: 22601,
  vuelta: 0,
  nivel: 2,
  motivo: 'AUTOR_DEL_CAMBIO',
  resultado: 'AGOTADO',
  ejecutadoEn: dayjs('2026-11-12T05:21'),
  proximoEn: dayjs('2026-11-12T04:00'),
  detalle: 'procurement or',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);

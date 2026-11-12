import dayjs from 'dayjs/esm';

import { ICorrelacionCambioIncidente, NewCorrelacionCambioIncidente } from './correlacion-cambio-incidente.model';

export const sampleWithRequiredData: ICorrelacionCambioIncidente = {
  id: 7579,
  origen: 'AUTOMATICA',
  estado: 'SUGERIDA',
  detectadaEn: dayjs('2026-11-11T17:06'),
};

export const sampleWithPartialData: ICorrelacionCambioIncidente = {
  id: 22333,
  origen: 'MANUAL',
  estado: 'SUGERIDA',
  detectadaEn: dayjs('2026-11-11T17:27'),
  minutosEntreDespliegueEIncidente: 24549,
  nota: 'solemnly',
};

export const sampleWithFullData: ICorrelacionCambioIncidente = {
  id: 4541,
  origen: 'MANUAL',
  estado: 'DESCARTADA',
  detectadaEn: dayjs('2026-11-11T16:10'),
  minutosEntreDespliegueEIncidente: 15509,
  nota: 'misjudge ew',
};

export const sampleWithNewData: NewCorrelacionCambioIncidente = {
  origen: 'AUTOMATICA',
  estado: 'CONFIRMADA',
  detectadaEn: dayjs('2026-11-12T11:05'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
